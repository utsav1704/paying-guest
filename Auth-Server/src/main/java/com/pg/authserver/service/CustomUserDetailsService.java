package com.pg.authserver.service;

import com.pg.authserver.domain.Owner;
import com.pg.authserver.repo.OwnerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final OwnerRepository ownerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        if (username.equals("pg_admin")) {
            return new User("pg_admin", "$2a$11$4Ncc.AaG2ocwVF9fKAffk.dj1wNO3mNSypbYwNO3LMtJaqTzxORH.", true, true, true, true, getAuthorities(List.of("ROLE_ADMIN")));
        }
        Optional<Owner> optionalOwner = ownerRepository.findByUsernameAndIsDeletedIsFalse(username);

        if (optionalOwner.isEmpty()) {
            throw new UsernameNotFoundException("No Username found");
        }

        Owner owner = optionalOwner.get();
        return new User(owner.getUsername(), owner.getPassword(), owner.getIsVerified(), true, true, true, getAuthorities(List.of("ROLE_OWNER")));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(List<String> roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }
        return authorities;
    }
}
