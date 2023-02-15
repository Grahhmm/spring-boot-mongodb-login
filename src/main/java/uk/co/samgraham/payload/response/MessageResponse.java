package uk.co.samgraham.payload.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageResponse {
  private String message;
}
