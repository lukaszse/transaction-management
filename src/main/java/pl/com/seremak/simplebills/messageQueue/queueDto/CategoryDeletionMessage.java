package pl.com.seremak.simplebills.messageQueue.queueDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class CategoryDeletionMessage {

    private String username;
    private String deletedCategory;
    private String replacementCategory;
}
