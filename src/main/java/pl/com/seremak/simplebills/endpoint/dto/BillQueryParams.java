package pl.com.seremak.simplebills.endpoint.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.time.Instant;
import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BillQueryParams {

    @Nullable
    private String category;
    @Nullable
    private Instant dateFrom;
    @Nullable
    private Instant dateTo;
    @Nullable
    private Integer pageSize;
    @Nullable
    private Integer pageNumber;
}
