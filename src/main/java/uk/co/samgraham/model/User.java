package uk.co.samgraham.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import uk.co.samgraham.model.validation.Password;
import uk.co.samgraham.model.validation.Username;

import java.util.Set;

@Document(collection = "users")
@Data
@Builder
public class User {
  @Id
  private String id;

  @Username
  private String username;

  @NotBlank
  @Email
  private String email;

  @Password
  private String password;

  @DBRef
  private Set<Role> roles;
}
