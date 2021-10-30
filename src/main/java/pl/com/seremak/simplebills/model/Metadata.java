package pl.com.seremak.simplebills.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
public class Metadata {

    private Long version;
    private Instant createdAt;
    private Instant modifiedAt;
}
