package pl.com.seremak.simplebills.endpoint;

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

//    public Instant getInstantDateFrom() {
//        return Optional.ofNullable(dateFrom)
//                .map(Instant::parse)
//                .orElse(null);
//    }
//
//    public Instant getInstantDateTo() {
//        return Optional.ofNullable(dateTo)
//                .map(Instant::parse)
//                .orElse(null);
//    }
}
