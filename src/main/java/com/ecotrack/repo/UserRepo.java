package com.ecotrack.repo;
import com.ecotrack.model.User;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
public interface UserRepo extends JpaRepository<User, Long> { Optional<User> findByEmail(String e); List<User> findByRole(String r); }
