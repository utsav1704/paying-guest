package com.pg.authserver.repo;

import com.pg.authserver.domain.Owner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OwnerRepository extends JpaRepository<Owner, String> {
    Optional<Owner> findByUsername(String username);

    Optional<Owner> findByUsernameAndIsDeletedIsTrue(String username);
    Optional<Owner> findByUsernameAndIsDeletedIsFalse(String username);
}
