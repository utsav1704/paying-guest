package com.pg.owner.resource;

import com.pg.owner.custom_exception.ResourceNotFoundException;
import com.pg.owner.entity.Owner;
import com.pg.owner.service.OwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/owner")
@RequiredArgsConstructor
public class OwnerResource {

    private final OwnerService ownerService;

    @GetMapping(path = {"", "/"})
    public String isOwnerWorking() {
        return "Owner is working";
    }

    @GetMapping(path = "/get")
    public ResponseEntity<Owner> getOwner(@RequestParam("ownerId") String ownerId) throws ResourceNotFoundException {
        Owner owner = ownerService.getOwner(ownerId);
        return ResponseEntity.ok(owner);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @PostMapping(path = "/add", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Owner> addOwner(@RequestPart("owner") String owner,
                                          @RequestPart(name = "image", required = false) MultipartFile identity) throws Exception {
        Owner newOwner = ownerService.addOwner(owner, identity);
        return ResponseEntity.status(HttpStatus.CREATED).body(newOwner);
    }

    @PutMapping(path = "/update", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Owner> updateOwner(@RequestPart("owner") String owner,
                                          @RequestPart(name = "image", required = false) MultipartFile identity) throws Exception {
        Owner updatedOwner = ownerService.updateOwner(owner, identity);
        return ResponseEntity.status(HttpStatus.OK).body(updatedOwner);
    }

    @DeleteMapping(path = "/delete")
    public ResponseEntity<String> deleteOwner(@RequestParam("ownerId") String ownerId) throws ResourceNotFoundException {
        ownerService.deleteOwner(ownerId);
        return ResponseEntity.ok("Owner deleted Successfully.");
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @GetMapping(path = "/verify")
    public ResponseEntity<String> verifyProperty(@RequestParam("ownerId") String ownerId) throws ResourceNotFoundException {
        ownerService.verifyOwner(ownerId);
        return ResponseEntity.ok("Owner verified Successfully.");
    }

}
