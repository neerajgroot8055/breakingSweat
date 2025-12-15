package com.fitness.gateway.user;


import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final WebClient userServiceWebCLient;

    public Mono<Boolean> validateUser(String userId) {
        try {
            return userServiceWebCLient.get()
                    .uri("/api/users/{userId}/validate", userId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .onErrorResume(WebClientResponseException.class , e-> {
                        if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                            return Mono.error(new RuntimeException("User not found"));
                            
                        } else if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                            return Mono.error(new RuntimeException("Invalid request"));
                        }
                        return Mono.error(new RuntimeException("Unexpected error"));
                    });
        } catch (WebClientResponseException e) {
            throw new RuntimeException(e);
        }

    }

    public Mono<UserResponse> registerUser(RegisterRequest registerRequest) {
        log.info("Calling for user registration");
        try {
            return userServiceWebCLient.post()
                    .uri("/api/users/register")
                    .bodyValue(registerRequest)
                    .retrieve()
                    .bodyToMono(UserResponse.class)
                    .onErrorResume(WebClientResponseException.class , e-> {
                        if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                            return Mono.error(new RuntimeException("Invalid request"));
                        }
                        return Mono.error(new RuntimeException("Unexpected error"));
                    });
        } catch (WebClientResponseException e) {
            throw new RuntimeException(e);
        }
    }
}


