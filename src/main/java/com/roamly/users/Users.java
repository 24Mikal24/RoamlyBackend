package com.roamly.users;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface Users extends JpaRepository<User, UUID> {
}