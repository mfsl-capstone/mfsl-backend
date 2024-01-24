package capstone.mfslbackend.response.dto;

import capstone.mfslbackend.response.dto.stats.StatisticResponse;
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
public class PlayerStatsResponse implements Serializable {
    private PlayerResponse player;
    private List<StatisticResponse> statistics;
}
