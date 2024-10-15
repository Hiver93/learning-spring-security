package com.kdw.jwt.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.kdw.jwt.dto.JoinDto;
import com.kdw.jwt.entity.UserEntity;
import com.kdw.jwt.repository.UserRepository;

@Service
public class JoinService {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	
	public JoinService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.userRepository = userRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}
	
	public void joinProcess(JoinDto joinDto) {
		String username = joinDto.getUsername();
		String password = joinDto.getPassword();
		
		Boolean isExist = userRepository.existsByUsername(username);
		
		if(isExist) {
			return;
		}
		UserEntity data = new UserEntity();
		data.setUsername(username);
		data.setPassword(bCryptPasswordEncoder.encode(password));
		data.setRole("ROLE_ADMIN");
		
		userRepository.save(data);
	}
}
