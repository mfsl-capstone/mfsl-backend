package capstone.mfslbackend.response.dto.stats;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GoalResponse implements Serializable {
    private Integer total;
    private Integer conceded;
    private Integer assists;
    private Integer saves;
}
