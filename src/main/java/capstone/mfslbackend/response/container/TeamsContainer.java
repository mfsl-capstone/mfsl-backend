package capstone.mfslbackend.response.container;

import capstone.mfslbackend.response.dto.TeamResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TeamsContainer {
    Paging paging;
    String message;
    Integer results;
    List<TeamsResponseDTO> response;

    public List<TeamResponse> getTeams() {
        return response.stream()
                .map(TeamsResponseDTO::getTeam)
                .collect(Collectors.toList());
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    private static class TeamsResponseDTO {
        TeamResponse team;
    }
}
