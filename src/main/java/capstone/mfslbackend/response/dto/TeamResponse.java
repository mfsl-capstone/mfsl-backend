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
    long id;
    String name;
    String logo;
    String update;
    Boolean winner;
    String code;
    String country;
    Integer founded;
    Boolean national;
}