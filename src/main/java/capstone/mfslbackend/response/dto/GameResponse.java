package capstone.mfslbackend.response.dto;

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
public class GameResponse implements Serializable {
    Integer minutes;
    Integer number;
    String position;
    Float rating;
    boolean captain;
    boolean substitute;
}