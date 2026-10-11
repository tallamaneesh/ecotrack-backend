package com.ecotrack;
import com.ecotrack.model.*;
import com.ecotrack.repo.*;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
@SpringBootApplication
public class EcoTrackApplication {
  public static void main(String[] a) { SpringApplication.run(EcoTrackApplication.class, a); }
  @Bean CommandLineRunner seed(UserRepo u, CategoryRepo c, PasswordEncoder e) {
    return x -> {
      if (u.count() == 0) {
        u.save(new User("Admin", "admin@ecotrack.com", e.encode("admin123"), "ADMIN"));
        u.save(new User("Staff One", "staff@ecotrack.com", e.encode("staff123"), "STAFF"));
        u.save(new User("Green Recyclers", "recycler@ecotrack.com", e.encode("recycle123"), "RECYCLER"));
      }
      if (c.count() == 0) for (String n : new String[]{"Plastic","Paper","Glass","E-waste","Organic","Hazardous"}) c.save(new Category(n));
    };
  }
}
