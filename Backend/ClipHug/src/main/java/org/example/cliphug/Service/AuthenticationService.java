package org.example.cliphug.Service;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.cliphug.Dao.UserDao;
import org.example.cliphug.Dto.Auth.AuthCheckResponseDTO;
import org.example.cliphug.Dto.User.UserCreateDTO;
import org.example.cliphug.Model.Role;
import org.example.cliphug.Model.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserDao userDAO;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public Optional<String> register(UserCreateDTO userCreateDTO) {
        Optional<User> foundUser = userDAO.findByUsername(userCreateDTO.getUsername());
        if (foundUser.isPresent()) {
            return Optional.empty();
        }

        User user = User.builder()
                .username(userCreateDTO.getUsername())
                .password(passwordEncoder.encode(userCreateDTO.getPassword()))
                .firstName(userCreateDTO.getFirstName())
                .lastName(userCreateDTO.getLastName())
                .role(Role.USER)
                .build();

        userDAO.save(user);
        String token = jwtService.generateToken(Map.of("id", user.getId()), user.getId() );
        return Optional.of(token);
    }

    public void login(String username, String password, HttpServletResponse response) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        Optional<User> user = userDAO.findByUsername(username);
        if (user.isPresent()) {
            String token = jwtService.generateToken(Map.of("id", user.get().getId()), user.get().getId());

            Cookie cookie = new Cookie("token", token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60 * 24 * 7);
            response.addCookie(cookie);
        }

    }


    public Cookie logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String token = this.jwtService.generateToken(Map.of("id", authentication.getPrincipal()), (UUID) authentication.getPrincipal());
        jwtService.invalidateToken(token);

        Cookie cookie = new Cookie("token", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setSecure(false);
        cookie.setMaxAge(0);
        return cookie;
    }

    public AuthCheckResponseDTO checkAuthenticated(HttpServletRequest request) {
        String jwt = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    jwt = cookie.getValue();
                    break;
                }
            }
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated()
                && !authentication.getName().equals("anonymousUser") && jwtService.isTokenValid(jwt, (UUID) authentication.getPrincipal());

        return AuthCheckResponseDTO
                .builder()
                .isAuthenticated(isAuthenticated)
                .build();
    }

    public Cookie getEmptyCookie(String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setSecure(false);
        cookie.setMaxAge(0);
        return cookie;
    }

    public boolean isIdOfSelf(UUID id, Authentication authentication) {
        User authUser = userDAO.findById((UUID) authentication.getPrincipal()).orElse(null);
        return authUser != null && authUser.getId().equals(id);
    }

    public boolean checkIfUserIsRequestingTheirOwnData(UUID id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString(authentication.getPrincipal().toString());
        return userId.equals(id);
    }
}
