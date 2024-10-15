package com.kdw.jwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kdw.jwt.entity.RefreshEntity;

import jakarta.transaction.Transactional;

@Repository
public interface RefreshRepository extends JpaRepository<RefreshEntity, Long>{
	
	Boolean existsByRefresh(String refresh);
	
	@Transactional
	void deleteByRefresh(String refresh);
}
