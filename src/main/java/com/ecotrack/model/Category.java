package com.ecotrack.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
@Entity
public class Category {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) public Long id;
  @NotBlank public String name;
  public Category() {}
  public Category(String n) { name = n; }
}
