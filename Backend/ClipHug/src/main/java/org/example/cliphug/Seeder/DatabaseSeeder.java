package org.example.cliphug.Seeder;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder {
    private final AdminSeeder userSeeder;

    @EventListener
    public void seed(ContextRefreshedEvent event) {
        userSeeder.seed();
    }
}
