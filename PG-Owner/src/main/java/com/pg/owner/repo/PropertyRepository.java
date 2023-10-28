package com.pg.owner.repo;

import com.pg.owner.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropertyRepository extends JpaRepository<Property, String> {

    List<Property> findByOwnerOwnerIdAndIsVerifiedIsTrueAndIsRemovedIsFalse(String ownerId);

    List<Property> findByOwnerOwnerIdAndIsRemovedIsFalse(String ownerId);
}
