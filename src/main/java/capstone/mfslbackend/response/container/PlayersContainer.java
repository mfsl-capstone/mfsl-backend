package capstone.mfslbackend.response.container;

import capstone.mfslbackend.response.dto.PlayersResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PlayersContainer {
    private Paging paging;
    private String message;
    private Integer results;
    private List<PlayersResponse> response;
}
