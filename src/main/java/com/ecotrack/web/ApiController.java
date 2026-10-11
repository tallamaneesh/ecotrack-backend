package com.ecotrack.web;
import com.ecotrack.model.*;
import com.ecotrack.repo.*;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.*;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api")
public class ApiController {
  private final UserRepo users; private final CategoryRepo cats; private final RequestRepo reqs;
  static final List<String> FLOW = List.of("SUBMITTED", "ASSIGNED", "COLLECTED", "COMPLETED");
  public ApiController(UserRepo u, CategoryRepo c, RequestRepo r) { users = u; cats = c; reqs = r; }

  // ----- categories (admin CRUD, everyone reads)
  @GetMapping("/categories") List<Category> categories() { return cats.findAll(); }
  @PostMapping("/categories") Category addCat(@Valid @RequestBody Category c) { c.id = null; return cats.save(c); }
  @PutMapping("/categories/{id}") Category editCat(@PathVariable Long id, @Valid @RequestBody Category c) { c.id = id; return cats.save(c); }
  @DeleteMapping("/categories/{id}") void delCat(@PathVariable Long id) { cats.deleteById(id); }

  // ----- users (admin)
  @GetMapping("/users") List<User> allUsers(@RequestParam(required = false) String role) { return role == null ? users.findAll() : users.findByRole(role); }
  @DeleteMapping("/users/{id}") void delUser(@PathVariable Long id) { users.deleteById(id); }

  // ----- requests
  @PostMapping("/requests")
  PickupRequest create(@Valid @RequestBody PickupRequest r, Principal p) {
    if (r.category == null || r.category.id == null) throw new IllegalArgumentException("Select a waste category");
    r.id = null; r.status = "SUBMITTED"; r.staff = null; r.materialsReceived = null;
    r.category = cats.findById(r.category.id).orElseThrow(() -> new IllegalArgumentException("Unknown category"));
    r.resident = users.findByEmail(p.getName()).orElseThrow();
    return reqs.save(r);
  }
  @GetMapping("/requests")
  List<PickupRequest> list(Principal p, @RequestParam(required = false) String q, @RequestParam(required = false) String status) {
    User me = users.findByEmail(p.getName()).orElseThrow();
    List<PickupRequest> out = switch (me.role) {
      case "RESIDENT" -> reqs.findByResidentEmailOrderByIdDesc(me.email);
      case "STAFF" -> reqs.findByStaffEmailOrderByIdDesc(me.email);
      case "RECYCLER" -> reqs.findByStatusInOrderByIdDesc(List.of("COLLECTED", "COMPLETED"));
      default -> reqs.findAllByOrderByIdDesc();
    };
    return out.stream()
      .filter(r -> status == null || status.isEmpty() || r.status.equals(status))
      .filter(r -> q == null || q.isBlank() || (r.description + r.address + r.category.name).toLowerCase().contains(q.toLowerCase()))
      .toList();
  }
  @PutMapping("/requests/{id}/assign/{staffId}")
  PickupRequest assign(@PathVariable Long id, @PathVariable Long staffId, Principal p) {
    requireRole(p, "ADMIN");
    PickupRequest r = reqs.findById(id).orElseThrow(); r.staff = users.findById(staffId).orElseThrow(); r.status = "ASSIGNED";
    return reqs.save(r);
  }
  @PutMapping("/requests/{id}/status")
  PickupRequest status(@PathVariable Long id, @RequestBody Map<String, String> b, Principal p) {
    String s = b.get("status");
    if (!FLOW.contains(s)) throw new IllegalArgumentException("Invalid status");
    User me = users.findByEmail(p.getName()).orElseThrow();
    if (!List.of("ADMIN", "STAFF", "RECYCLER").contains(me.role)) throw new IllegalArgumentException("Not allowed");
    PickupRequest r = reqs.findById(id).orElseThrow(); r.status = s;
    if (b.get("materialsReceived") != null) r.materialsReceived = b.get("materialsReceived");
    return reqs.save(r);
  }
  @DeleteMapping("/requests/{id}") void del(@PathVariable Long id, Principal p) {
    User me = users.findByEmail(p.getName()).orElseThrow();
    PickupRequest r = reqs.findById(id).orElseThrow();
    if (me.role.equals("ADMIN") || (r.resident.email.equals(me.email) && r.status.equals("SUBMITTED"))) reqs.delete(r);
    else throw new IllegalArgumentException("You can only cancel your own requests before they are assigned");
  }
  private void requireRole(Principal p, String role) {
    if (!users.findByEmail(p.getName()).orElseThrow().role.equals(role)) throw new IllegalArgumentException("Not allowed");
  }
}
