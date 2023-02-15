package uk.co.samgraham.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.co.samgraham.model.ERole;
import uk.co.samgraham.model.Role;

import java.util.Optional;

public interface RoleRepository extends MongoRepository<Role, String> {
  Optional<Role> findByName(ERole name);
}
