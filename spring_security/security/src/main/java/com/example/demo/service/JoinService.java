package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.JoinDto;
import com.example.demo.entity.UserEntity;
import com.example.demo.repository.UserRepository;

@Service
public class JoinService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	public boolean joinProcess(JoinDto joinDto) {
		
		// db에 이미 동일한 username을 가진 데이터가 있는지 확인
		boolean isUser = userRepository.existsByUsername(joinDto.getUsername());
		if(isUser) {
			return false;
		}
				
		UserEntity userEntity = new UserEntity();
		userEntity.setUsername(joinDto.getUsername());
		userEntity.setPassword(bCryptPasswordEncoder.encode(joinDto.getPassword()));
		userEntity.setRole("ROLE_ADMIN");
		
		userRepository.save(userEntity);
		return true;
	}
}
