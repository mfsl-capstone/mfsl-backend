package capstone.mfslbackend.response.dto.stats;

import capstone.mfslbackend.response.dto.TeamResponse;
import capstone.mfslbackend.response.dto.GameResponse;
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
public class StatisticResponse implements Serializable {
    TeamResponse team;
    GameResponse games;
    Integer offsides;
    ShotResponse shots;
    GoalResponse goals;
    PassResponse passes;
    TackleResponse tackles;
    DuelResponse duels;
    DribbleResponse dribbles;
    FoulResponse fouls;
    CardResponse cards;
    PenaltyResponse penalty;
}