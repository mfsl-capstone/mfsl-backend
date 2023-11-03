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
    Paging paging;
    String message;
    Integer results;
    List<PlayersResponse> response;
}
