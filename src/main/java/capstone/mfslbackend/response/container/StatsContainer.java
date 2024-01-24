package capstone.mfslbackend.response.container;

import capstone.mfslbackend.response.dto.PlayersStatsResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class StatsContainer implements Serializable {
    private List<String> errors;
    private List<PlayersStatsResponse> response;
}
