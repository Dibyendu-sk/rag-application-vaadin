package com.dibyendu.service;

import com.dibyendu.UserRepo;
import com.dibyendu.entity.UserEntity;
import com.dibyendu.exception.ResourceNotFoundException;
import com.dibyendu.models.UpdateUserDto;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@BrowserCallable
@AnonymousAllowed
public class UserService {
    private final UserRepo userRepo;

    public UserService(UserRepo userRepo){
        this.userRepo = userRepo;
    }

    public List<UserEntity> findAll(){
        return userRepo.findAll();
    }

    public UserEntity findById(String id){
        return userRepo.findById(id).orElse(null);
    }

    public UserEntity save(UserEntity userEntity){
        return userRepo.save(userEntity);
    }
    public boolean delete(String userId){
        try{
            userRepo.deleteById(userId);
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean update(UpdateUserDto updateUserDto){
        UserEntity userEntity = userRepo.findById(updateUserDto.getId()).orElseThrow(
                () -> new ResourceNotFoundException("User does not exists")
        );

        userEntity.setEmail(updateUserDto.getEmail());
        userEntity.setPhoneNumber(updateUserDto.getPhone());
        userEntity.setName(updateUserDto.getName());
        userEntity.setRole(updateUserDto.getRole());

        userRepo.save(userEntity);

        return true;
    }
}
