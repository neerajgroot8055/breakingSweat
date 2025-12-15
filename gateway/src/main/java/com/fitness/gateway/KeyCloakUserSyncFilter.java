package com.fitness.gateway;


import com.fitness.gateway.user.RegisterRequest;
import com.fitness.gateway.user.UserService;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.text.ParseException;
import java.util.function.Consumer;

@Component
@Slf4j
@RequiredArgsConstructor
public class KeyCloakUserSyncFilter implements WebFilter {

    private final UserService userService ;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        String userId = exchange.getRequest().getHeaders()
                .getFirst("X-User-Id") ;

        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        RegisterRequest registerRequest = getUserDetails(token) ;
        if(userId == null){
            userId = registerRequest.getKeyCloakId();
        }
        if(userId != null && token != null){
            String finalUserId = userId;
            return userService.validateUser(userId)
                    .flatMap(exist -> {
                        if(!exist){
                            if(registerRequest != null){
                                return userService.registerUser(registerRequest)
                                        .then(Mono.empty());
                            } else {
                                return Mono.empty() ;
                            }
                        } else {
                            log.info("User exists");
                            return Mono.empty();
                        }
                    })
                    .then(Mono.defer(()->{
                        ServerHttpRequest mutatedRequest = (ServerHttpRequest) exchange.getRequest().mutate()
                                .header("X-User-id", finalUserId)
                                .build();
                        return chain.filter(exchange.mutate().request(mutatedRequest).build());
                    })) ;
        }

        return chain.filter(exchange);
    }

    private RegisterRequest getUserDetails(String token) {
        try{
            String tokenWithoutBearer = token.replace("Bearer","").trim();
            SignedJWT signedJWT = SignedJWT.parse(tokenWithoutBearer);
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

            RegisterRequest request = new RegisterRequest();
            request.setEmail(claims.getStringClaim("email"));
            request.setKeyCloakId(claims.getStringClaim("sub"));
            request.setFirstname(claims.getStringClaim("given_name"));
            request.setLastname(claims.getStringClaim("family_name"));

            request.setPassword("dummy@123123");

            return request ;


        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
