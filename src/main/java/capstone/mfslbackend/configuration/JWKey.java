package capstone.mfslbackend.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Getter
@Setter
@AllArgsConstructor
public class JWKey {

    private RSAPrivateKey rsaPrivateKey;

    private RSAPublicKey rsaPublicKey;

}
