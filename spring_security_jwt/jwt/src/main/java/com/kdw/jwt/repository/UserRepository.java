package com.kdw.jwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kdw.jwt.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {

	Boolean existsByUsername(String username); 
	
	UserEntity findByUsername(String username);
}
