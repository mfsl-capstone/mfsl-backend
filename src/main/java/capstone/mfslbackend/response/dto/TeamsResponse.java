package capstone.mfslbackend.response.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TeamsResponse {

    private TeamResponse team;
    private TeamResponse home;
    private TeamResponse away;

    //create constructor
    public TeamsResponse(TeamResponse team) {
        this.team = team;
        this.home= home;
        this.away= away;
    }
    public void setTeam(TeamResponse team) {
        this.team = team;
    }

    public void setHome(TeamResponse home) {
        this.home = home;
    }

    public void setAway(TeamResponse away) {
        this.away = away;
    }

}
