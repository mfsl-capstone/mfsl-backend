package capstone.mfslbackend.response.dto.stats;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DribbleResponse implements Serializable {
    private Integer attempts;
    private Integer success;
    private Integer past;
}
