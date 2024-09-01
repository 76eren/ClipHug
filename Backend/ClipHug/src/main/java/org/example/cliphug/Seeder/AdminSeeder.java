package org.example.cliphug.Seeder;

import lombok.RequiredArgsConstructor;
import org.example.cliphug.Dao.UserDao;
import org.example.cliphug.Model.Role;
import org.example.cliphug.Model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@RequiredArgsConstructor
@Component
public class AdminSeeder {
    private final PasswordEncoder passwordEncoder;
    private final UserDao userDao;

    @Value("${admin.username}")
    private String username;

    @Value("${admin.password}")
    private String password;

    @Value("${admin.firstName}")
    private String firstName;

    @Value("${admin.lastName}")
    private String lastName;

    @Value("${admin.email}")
    private String email;


    public void seed() {
        // We check if an admin already exists
        if (userDao.findByUsername(username).isPresent()) {
            System.out.println("SKIPPING");
            return;
        }

        System.out.println("NOT SKIPPING");
        for (User user : getUsers()) {
            this.userDao.save(user);
        }
    }


    public ArrayList<User> getUsers() {
        ArrayList<User> users = new ArrayList<>();
        users.add(createAdmin(username, password, firstName, lastName, email));
        return users;
    }


    public User createAdmin(String username, String password, String firstName, String lastName, String email) {
        return User
                .builder()
                .username(username)
                .role(Role.ADMIN)
                .password(passwordEncoder.encode(password))
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .build();
    }
}
