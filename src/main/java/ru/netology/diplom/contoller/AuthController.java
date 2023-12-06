package ru.netology.diplom.contoller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.netology.diplom.Token.AuthRequest;
import ru.netology.diplom.Token.AuthResponse;
import ru.netology.diplom.Token.JWTUtil;
import ru.netology.diplom.repository.UserTokenRepository;

@RestController
@AllArgsConstructor
@Slf4j
public class AuthController {

    private UserTokenRepository tokenRepository;
    private JWTUtil jwtUtil;
    private AuthenticationManager authenticationManager;
    private UserTokenRepository userTokenRepository;

    @PostMapping("/login")
    public AuthResponse authenticateUser(@RequestBody AuthRequest authRequest) {
        final String userName = authRequest.login();
        final String password = authRequest.password();
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userName,
                        password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtil.generateToken(authentication);
        userTokenRepository.saveUserToken(userName, jwt);
        log.info("User {} authentication. Token {}", userName, jwt);
        return new AuthResponse(jwt);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("auth-token") String authToken) {
        tokenRepository.deleteUserAndToken(authToken);
        return ResponseEntity.ok(HttpStatus.OK);
    }

}
