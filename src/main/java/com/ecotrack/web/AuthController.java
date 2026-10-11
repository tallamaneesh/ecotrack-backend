package com.ecotrack.web;
import com.ecotrack.model.User;
import com.ecotrack.repo.UserRepo;
import java.security.Principal;
import java.util.Map;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/auth")
public class AuthController {
  private final UserRepo users; private final PasswordEncoder enc;
  public AuthController(UserRepo u, PasswordEncoder e) { users = u; enc = e; }
  @GetMapping("/") String root() { return "EcoTrack API running"; }
  @PostMapping("/register")
  User register(@RequestBody Map<String, String> b) {
    String name = b.getOrDefault("name", "").trim(), email = b.getOrDefault("email", "").trim(), pw = b.getOrDefault("password", "");
    String role = "RECYCLER".equals(b.get("role")) ? "RECYCLER" : "RESIDENT";
    if (name.isEmpty() || !email.contains("@") || pw.length() < 6) throw new IllegalArgumentException("Enter a name, a valid email and a password of 6+ characters");
    if (users.findByEmail(email).isPresent()) throw new IllegalArgumentException("That email is already registered");
    return users.save(new User(name, email, enc.encode(pw), role));
  }
  @GetMapping("/me") User me(Principal p) { return users.findByEmail(p.getName()).orElseThrow(); }
}
