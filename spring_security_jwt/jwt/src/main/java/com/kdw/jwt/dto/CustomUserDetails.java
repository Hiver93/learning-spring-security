package com.kdw.jwt.dto;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.kdw.jwt.entity.UserEntity;

public class CustomUserDetails implements UserDetails {

	private final UserEntity userEntity;
	
	public CustomUserDetails(UserEntity userEntity) {
		this.userEntity = userEntity;
	}
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> collection = new ArrayList<>();
		collection.add(new GrantedAuthority() {
			
			@Override
			public String getAuthority() {
				return userEntity.getRole();
			}
		});
		return collection;
	}

	@Override
	public String getPassword() {
		return userEntity.getPassword();
	}

	@Override
	public String getUsername() {
		return userEntity.getUsername();
	}
	
	// 이하 만료 잠김 등은 트루값이 디폴트이므로 놔두었다.

}
