package capstone.mfslbackend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

@Service
public class ApiService {
    private final String footballApiKey;
    private final ObjectMapper mapper;
    public ApiService(@Value("${football.api.key}") String footballApiKey) {
        this.footballApiKey = footballApiKey;
        this.mapper = new ObjectMapper();
    }

    public <T> T getRequest(URL url, Class<T> type) throws IOException {
        URLConnection yc = url.openConnection();
        yc.setRequestProperty("x-rapidapi-key", footballApiKey);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        yc.getInputStream()));
        String inputLine;
        StringBuilder output = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            output.append(inputLine);
        }
        in.close();
        return mapper.readValue(output.toString(), type);
    }
}
