package com.ecotrack.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
@Entity
public class PickupRequest {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) public Long id;
  @NotBlank public String description;
  @NotBlank public String address;
  @ManyToOne public Category category;
  @ManyToOne public User resident;
  @ManyToOne public User staff;
  public String materialsReceived;
  public String status = "SUBMITTED";
  public LocalDateTime createdAt = LocalDateTime.now();
}
