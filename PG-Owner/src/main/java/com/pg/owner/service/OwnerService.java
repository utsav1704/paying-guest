package com.pg.owner.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pg.owner.constants.FileConstant;
import com.pg.owner.custom_exception.ResourceExistException;
import com.pg.owner.custom_exception.ResourceNotFoundException;
import com.pg.owner.entity.Owner;
import com.pg.owner.repo.OwnerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OwnerService {
    private final OwnerRepository ownerRepository;
    private final PasswordEncoder passwordEncoder;
    private final KafkaTemplate<String, Owner> kafkaTemplate;

    public Owner addOwner(String ownerString, MultipartFile identity) throws Exception {
        Owner owner = getOwnerFromString(ownerString);
        verifyOwnersInfo(owner);
        owner.setIsVerified(false);
        owner.setIsDeleted(false);
        log.info("Password is : {}", owner.getPassword());
        owner.setPassword(passwordEncoder.encode(owner.getPassword()));
        owner.setIdNumber(CryptoUtils.encrypt(owner.getIdNumber()));
        owner.setIdType(CryptoUtils.encrypt(owner.getIdType()));
        owner.setProperties(new ArrayList<>());

        if (identity != null && !identity.isEmpty()) {
            owner.setIdentityImage(uploadFile(owner, identity));
        }
        Owner savedOwner = ownerRepository.save(owner);
        sendOwner(owner);
        savedOwner.setIdType(CryptoUtils.decrypt(owner.getIdType()));
        savedOwner.setIdNumber(CryptoUtils.decrypt(owner.getIdNumber()));
        return savedOwner;
    }

    public Owner updateOwner(String ownerString, MultipartFile identity) throws Exception {
        Owner owner = getOwnerFromString(ownerString);
        Owner oldOwner = getOwner(owner.getOwnerId());
        verifyOwnersInfo(owner);

        if (!owner.getPassword().isEmpty()) {
            owner.setPassword(passwordEncoder.encode(owner.getPassword()));
        } else {
            owner.setPassword(oldOwner.getPassword());
        }

        owner.setIsDeleted(oldOwner.getIsDeleted());
        owner.setIsVerified(oldOwner.getIsVerified());
        owner.setIdNumber(CryptoUtils.encrypt(owner.getIdNumber()));
        owner.setIdType(CryptoUtils.encrypt(owner.getIdType()));
        owner.setProperties(oldOwner.getProperties());

        if (identity != null && !identity.isEmpty()) {
            owner.setIdentityImage(uploadFile(owner, identity));
        }

        Owner updatedOwner = ownerRepository.save(owner);
        updatedOwner.setIdType(CryptoUtils.decrypt(owner.getIdType()));
        updatedOwner.setIdNumber(CryptoUtils.decrypt(owner.getIdNumber()));
        return updatedOwner;
    }

    public Owner getOwner(String ownerId) throws ResourceNotFoundException {
        Optional<Owner> optionalOwner = ownerRepository.findById(ownerId);
        if (optionalOwner.isEmpty()) {
            throw new ResourceNotFoundException("No owner is available");
        }
        return optionalOwner.get();
    }

    public void deleteOwner(String ownerId) throws ResourceNotFoundException {
        Owner owner = getOwner(ownerId);
        owner.setIsDeleted(true);
        ownerRepository.save(owner);
    }

    public void verifyOwner(String ownerId) throws ResourceNotFoundException {
        Owner owner = getOwner(ownerId);
        owner.setIsVerified(true);
        ownerRepository.save(owner);
        sendOwner(owner);
    }

    private void verifyOwnersInfo(Owner owner) throws ResourceExistException {
        Optional<Owner> optionalOwner = ownerRepository.findByUsernameAndIsDeletedIsFalse(owner.getUsername());

        if (optionalOwner.isPresent()) {
            if (owner.getOwnerId() == null || owner.getOwnerId().isEmpty() || !owner.getOwnerId().equals(optionalOwner.get().getOwnerId())) {
                throw new ResourceExistException("Username already Exist!!");
            }
        }
    }

    private Owner getOwnerFromString(String ownerString) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(ownerString, Owner.class);
        } catch (JsonMappingException e) {
            throw new RuntimeException("Unable to map RoomType");
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to process RoomType");
        }
    }

    private String uploadFile(Owner owner, MultipartFile identity) throws Exception {
        Path imageDir = Paths.get(FileConstant.MAIN_DIRECTORY + "Owner/" + owner.getUsername()).toAbsolutePath().normalize();

        if (!Files.exists(imageDir)) {
            Files.createDirectories(imageDir);
        }

        String fileName = identity.getOriginalFilename() == null ? owner.getUsername() + "_" + owner.getFirstName() : identity.getOriginalFilename();

        Files.copy(identity.getInputStream(), imageDir.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }

    private void sendOwner(Owner owner) {
        Message<Owner> message = MessageBuilder
                .withPayload(owner)
                .setHeader(KafkaHeaders.TOPIC, "pg_owner")
                .build();
        kafkaTemplate.send(message);
    }

}
