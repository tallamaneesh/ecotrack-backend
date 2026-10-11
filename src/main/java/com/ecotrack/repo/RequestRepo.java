package com.ecotrack.repo;
import com.ecotrack.model.PickupRequest;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface RequestRepo extends JpaRepository<PickupRequest, Long> {
  List<PickupRequest> findByResidentEmailOrderByIdDesc(String e);
  List<PickupRequest> findByStaffEmailOrderByIdDesc(String e);
  List<PickupRequest> findByStatusInOrderByIdDesc(List<String> s);
  List<PickupRequest> findAllByOrderByIdDesc();
}
