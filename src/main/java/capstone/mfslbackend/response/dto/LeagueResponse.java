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
public class LeagueResponse implements Serializable {

    private Integer id;
    private String name;
    private String country;
    private String logo;
    private String flag;
    private Integer season;
    private String round;
}
