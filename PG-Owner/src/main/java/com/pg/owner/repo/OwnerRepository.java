package com.pg.owner.repo;

import com.pg.owner.entity.Owner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OwnerRepository extends JpaRepository<Owner, String> {
    Optional<Owner> findByUsername(String username);

    Optional<Owner> findByUsernameAndIsDeletedIsTrue(String username);
    Optional<Owner> findByUsernameAndIsDeletedIsFalse(String username);
}
