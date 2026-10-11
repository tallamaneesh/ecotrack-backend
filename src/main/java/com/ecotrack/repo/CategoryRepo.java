package com.ecotrack.repo;
import com.ecotrack.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CategoryRepo extends JpaRepository<Category, Long> {}
