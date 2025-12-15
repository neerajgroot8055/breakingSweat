package com.breakingSweat.userservice;

import com.breakingSweat.userservice.models.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,String> {

    Boolean existsByEmail(String email);

    @Nullable Boolean existsByKeyCloakId(String userId);

    User findByEmail(@NotBlank(message = "Email is required") @Email(message = "Invalid format") String email);
}
