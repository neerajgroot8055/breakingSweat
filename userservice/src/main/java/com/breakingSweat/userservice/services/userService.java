package com.breakingSweat.userservice.services;


import com.breakingSweat.userservice.UserRepository;
import com.breakingSweat.userservice.dto.RegisterRequest;
import com.breakingSweat.userservice.dto.UserResponse;
import com.breakingSweat.userservice.models.User;
import lombok.AllArgsConstructor;

import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class userService {

    private final UserRepository repository ;

    public UserResponse register(RegisterRequest request) {

        if(repository.existsByEmail(request.getEmail())){
            User existingUser = repository.findByEmail(request.getEmail()) ;
            UserResponse userResponse = new UserResponse();
            userResponse.setId(existingUser.getId());
            userResponse.setEmail(existingUser.getEmail());
            userResponse.setFirstName(existingUser.getFirstName());
            userResponse.setLastName(existingUser.getLastName());
            userResponse.setPassword(existingUser.getPassword());
            userResponse.setCreatedAt(existingUser.getCreatedAt());
            userResponse.setUpdatedAT(existingUser.getUpdatedAT());


            return userResponse ;
        }


        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setKeyCloakId(request.getKeyCloakId());

        user.setFirstName(request.getFirstname());
        user.setLastName(request.getLastname());

        User savedUser = repository.save(user) ;
        UserResponse userResponse = new UserResponse();
        userResponse.setId(savedUser.getId());
        userResponse.setKeyCloakId(savedUser.getKeyCloakId());

        userResponse.setEmail(savedUser.getEmail());
        userResponse.setFirstName(savedUser.getFirstName());
        userResponse.setLastName(savedUser.getLastName());
        userResponse.setPassword(savedUser.getPassword());
        userResponse.setCreatedAt(savedUser.getCreatedAt());
        userResponse.setUpdatedAT(savedUser.getUpdatedAT());


        return userResponse ;

    }

    public @Nullable UserResponse getUserProfile(String userId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        User savedUser = repository.save(user) ;
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setEmail(user.getEmail());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setPassword(user.getPassword());
        userResponse.setCreatedAt(user.getCreatedAt());
        userResponse.setUpdatedAT(user.getUpdatedAT());

        return userResponse ;



    }

    public @Nullable Boolean existByUserId(String userId) {
        return repository.existsByKeyCloakId(userId);
    }
}
