package com.pg.owner.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pg.owner.constants.FileConstant;
import com.pg.owner.custom_exception.ResourceNotFoundException;
import com.pg.owner.entity.Owner;
import com.pg.owner.entity.Property;
import com.pg.owner.repo.OwnerRepository;
import com.pg.owner.repo.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PropertyService {
    private final PropertyRepository propertyRepository;
    private final OwnerRepository ownerRepository;
    private final KafkaTemplate<String, Property> kafkaTemplate;

    public Property getPropertyFromId(String propertyId) throws ResourceNotFoundException {
        Optional<Property> optionalProperty = propertyRepository.findById(propertyId);

        if (optionalProperty.isEmpty()) {
            throw new ResourceNotFoundException("No Property Exist!!");
        }

        return optionalProperty.get();
    }

    public Property addProperty(String propertyString, List<MultipartFile> images, String ownerId) throws Exception {
        Property property = getPropertyFromString(propertyString);
        property.setIsVerified(false);
        property.setIsRemoved(false);
        property.setOwner(getOwner(ownerId));

        List<String> imageList = new ArrayList<>();

        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                imageList.add(uploadFile(property, image));
            }
        }

        property.setImageList(imageList);
        Property savedProperty = propertyRepository.save(property);
        sendProperty(savedProperty);
        return savedProperty;
    }

    public Property updateProperty(String propertyString, List<MultipartFile> images, String ownerId) throws Exception {
        Property property = getPropertyFromString(propertyString);
        Property oldProp = getPropertyFromId(property.getPropertyId());

        if (!oldProp.getOwner().getOwnerId().equals(ownerId)) {
            throw new ResourceNotFoundException("Owner mismatched!!");
        }

        List<String> imageList = property.getImageList();
        property.setIsVerified(oldProp.getIsVerified());
        property.setIsRemoved(oldProp.getIsRemoved());

        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                imageList.add(uploadFile(property, image));
            }
        }

        return propertyRepository.save(property);
    }

    public void deleteProperty(String propertyId) throws ResourceNotFoundException {
        Property property = getPropertyFromId(propertyId);
        property.setIsRemoved(true);
        propertyRepository.save(property);
    }

    public List<Property> getVerifiedPropertiesOfOwner(String ownerId){
        return propertyRepository.findByOwnerOwnerIdAndIsVerifiedIsTrueAndIsRemovedIsFalse(ownerId);
    }

    public List<Property> getAllPropertiesOfOwner(String ownerId){
        return propertyRepository.findByOwnerOwnerIdAndIsRemovedIsFalse(ownerId);
    }

    public void verifyProperty(String propertyId) throws ResourceNotFoundException {
        Property property = getPropertyFromId(propertyId);
        property.setIsVerified(true);
        propertyRepository.save(property);
        sendProperty(property);
    }

    private Owner getOwner(String ownerId) throws ResourceNotFoundException {
        Optional<Owner> optionalOwner = ownerRepository.findById(ownerId);

        if (optionalOwner.isEmpty()) {
            throw new ResourceNotFoundException("No Owner found!!");
        }
        return optionalOwner.get();
    }

    private Property getPropertyFromString(String propertyString) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(propertyString, Property.class);
        } catch (JsonMappingException e) {
            throw new RuntimeException("Unable to map RoomType");
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to process RoomType");
        }
    }

    private String uploadFile(Property property, MultipartFile identity) throws Exception {
        Path imageDir = Paths.get(FileConstant.MAIN_DIRECTORY + "Owner/" + property.getPropertyName()).toAbsolutePath().normalize();

        if (!Files.exists(imageDir)) {
            Files.createDirectories(imageDir);
        }

        String fileName = identity.getOriginalFilename() == null ? UUID.randomUUID().toString() : identity.getOriginalFilename();

        Files.copy(identity.getInputStream(), imageDir.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }

    private void sendProperty(Property property) {
        Message<Property> message = MessageBuilder
                .withPayload(property)
                .setHeader(KafkaHeaders.TOPIC, "pg_property")
                .build();
        kafkaTemplate.send(message);
    }

}
