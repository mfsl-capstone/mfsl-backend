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

public class TeamResponse implements Serializable {
    private long id;
    private String name;
    private String logo;
    private String update;
    private Boolean winner;
    private String code;
    private String country;
    private Integer founded;
    private Boolean national;


}

