package uk.co.samgraham.payload.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserInfoResponse {
  private String id;
  private String username;
  private String email;
  private final List<String> roles;
}
