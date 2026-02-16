package org.example.service;

import org.example.model.AppUser;

import java.util.Optional;

public interface UserService {
    AppUser saveUser(AppUser user);
    AppUser findByUsername(String user);
    Optional<AppUser> findById(Long id);
}
