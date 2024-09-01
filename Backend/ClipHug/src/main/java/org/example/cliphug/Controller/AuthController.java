package org.example.cliphug.Controller;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.cliphug.Dto.Auth.*;
import org.example.cliphug.Dto.User.UserCreateDTO;
import org.example.cliphug.Model.ApiResponse;
import org.example.cliphug.Service.AuthenticationService;
import org.example.cliphug.Service.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authenticationService;
    private final JwtService jwtService;


    @Value("${register.pin}")
    private String pin;

    @GetMapping("/checkId/{id}")
    public ApiResponse<IdCheck> checkId(@PathVariable String id, Authentication authentication) {
        return new ApiResponse<>(new IdCheck(this.authenticationService.isIdOfSelf(UUID.fromString(id), authentication)), HttpStatus.OK);
    }

    @PostMapping(value = "/login")
    public ApiResponse<?> login(@Valid @RequestBody AuthRequestDTO loginDTO, HttpServletResponse response) {
        authenticationService.login(loginDTO.getUsername(), loginDTO.getPassword(), response);
        return new ApiResponse<>("Login succes", HttpStatus.OK);
    }

    @PostMapping(value = "/register")
    public ApiResponse<AuthResponseDTO> register(@RequestBody UserCreateDTO userCreateDTO) {
        // I don't want anybody being able to register for an account, so I have implemented a pin
        // You can leave the pin empty in the application.properties file if you don't want a pin for register

        if (!pin.isEmpty()) {
            if (!userCreateDTO.getPin().equals(pin)) {
                return new ApiResponse<>("Invalid pin", HttpStatus.BAD_REQUEST);
            }
        }


        Optional<String> tokenResponse = authenticationService.register(userCreateDTO);

        if (tokenResponse.isEmpty()) {
            return new ApiResponse<>("User already exists", HttpStatus.BAD_REQUEST);
        }

        String token = tokenResponse.get();
        return new ApiResponse<>(new AuthResponseDTO(token));
    }

    @GetMapping(value = "/authenticated")
    public ApiResponse<AuthCheckResponseDTO> checkAuthenticated(HttpServletRequest request, HttpServletResponse response) {
        AuthCheckResponseDTO authCheckResponseDTO = this.authenticationService.checkAuthenticated(request);

        if (!authCheckResponseDTO.isAuthenticated()) {
            // Clears the cookies in case the browser still has them stored whilst they're invalid
            Cookie cookie = this.authenticationService.getEmptyCookie("token");
            response.addCookie(cookie);
        }

        return new ApiResponse<>(authCheckResponseDTO, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public void logout(HttpServletResponse response) {
        response.addCookie(this.authenticationService.logout());
    }

    @GetMapping("/isAdmin")
    public ApiResponse<AdminCheckResponseDTO> isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"));
        return new ApiResponse<>(new AdminCheckResponseDTO(isAdmin), HttpStatus.OK);
    }


}
