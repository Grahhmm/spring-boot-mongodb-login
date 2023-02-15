package uk.co.samgraham.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import uk.co.samgraham.model.validation.Password;
import uk.co.samgraham.model.validation.Username;

import java.util.Set;

@Data
@Builder
public class SignupRequest {
  @Username
  private String username;

  @NotBlank
  @Email
  private String email;

  private Set<String> roles;

  @Password
  private String password;
}
