package com.pg.owner.resource;

import com.pg.owner.custom_exception.ResourceNotFoundException;
import com.pg.owner.entity.Owner;
import com.pg.owner.entity.Property;
import com.pg.owner.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/property")
@RequiredArgsConstructor
public class PropertyResource {

    private final PropertyService propertyService;

    @GetMapping(path = {"", "/"})
    public String isOwnerWorking() {
        return "Property is working";
    }

    @GetMapping(path = "/get")
    public ResponseEntity<Property> getProperty(@RequestParam("propertyId") String propertyId) throws ResourceNotFoundException {
        Property property = propertyService.getPropertyFromId(propertyId);
        return ResponseEntity.ok(property);
    }

    @PostMapping(path = "/add", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Property> addOwner(@RequestPart("property") String property,
                                             @RequestPart(name = "images", required = false) List<MultipartFile> images,
                                             @RequestPart("ownerId") String ownerId) throws Exception {
        Property newProperty = propertyService.addProperty(property, images, ownerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(newProperty);
    }

    @PutMapping(path = "/update", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Property> updateOwner(@RequestPart("property") String property,
                                                @RequestPart(name = "images", required = false) List<MultipartFile> images,
                                                @RequestPart("ownerId") String ownerId) throws Exception {
        Property updatedProperty = propertyService.updateProperty(property, images, ownerId);
        return ResponseEntity.status(HttpStatus.OK).body(updatedProperty);
    }

    @DeleteMapping(path = "/delete")
    public ResponseEntity<String> deleteProperty(@RequestParam("propertyId") String propertyId) throws ResourceNotFoundException {
        propertyService.deleteProperty(propertyId);
        return ResponseEntity.ok("Property deleted Successfully.");
    }

    @GetMapping(path = "/my-verified-properties")
    public ResponseEntity<List<Property>> getVerifiedPropertiesOfOwner(@RequestParam("ownerId") String ownerId){
        List<Property> properties = propertyService.getVerifiedPropertiesOfOwner(ownerId);
        return ResponseEntity.ok(properties);
    }

    @GetMapping(path = "/my-properties")
    public ResponseEntity<List<Property>> getAllPropertiesOfOwner(@RequestParam("ownerId") String ownerId){
        List<Property> properties = propertyService.getAllPropertiesOfOwner(ownerId);
        return ResponseEntity.ok(properties);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @GetMapping(path = "/verify")
    public ResponseEntity<String> verifyProperty(@RequestParam("propertyId") String propertyId) throws ResourceNotFoundException {
        propertyService.verifyProperty(propertyId);
        return ResponseEntity.ok("Property verified Successfully.");
    }

}
