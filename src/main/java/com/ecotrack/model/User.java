package com.ecotrack.model;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonProperty;
@Entity @Table(name = "users")
public class User {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) public Long id;
  public String name;
  @Column(unique = true) public String email;
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) public String password;
  public String role;
  public User() {}
  public User(String n, String e, String p, String r) { name = n; email = e; password = p; role = r; }
}
