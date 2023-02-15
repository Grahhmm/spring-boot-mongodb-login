package uk.co.samgraham.payload.request;

import lombok.Builder;
import lombok.Data;
import uk.co.samgraham.model.validation.Password;
import uk.co.samgraham.model.validation.Username;

@Data
@Builder
public class LoginRequest {
  @Username
  private String username;
  @Password
  private String password;
}
