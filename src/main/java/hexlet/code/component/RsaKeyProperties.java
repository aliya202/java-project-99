package hexlet.code.component;


import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
@ConfigurationProperties(prefix = "rsa")
@Setter
@Getter
public class RsaKeyProperties {

    private RSAPublicKey publicKey;
    private RSAPrivateKey privateKey;

    @PostConstruct
    public void initKeys() {
        try {
            ClassPathResource publicResource = new ClassPathResource("certs/public.pem");
            String publicKeyContent = StreamUtils.copyToString(publicResource.getInputStream(), StandardCharsets.UTF_8);
            publicKeyContent = publicKeyContent
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] decodedPublic = Base64.getDecoder().decode(publicKeyContent);

            ClassPathResource privateResource = new ClassPathResource("certs/private.pem");
            String privateKeyContent = StreamUtils.copyToString(privateResource.getInputStream(),
                    StandardCharsets.UTF_8);
            privateKeyContent = privateKeyContent
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] decodedPrivate = Base64.getDecoder().decode(privateKeyContent);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(decodedPublic);
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(decodedPrivate);

            this.publicKey = (RSAPublicKey) keyFactory.generatePublic(publicKeySpec);
            this.privateKey = (RSAPrivateKey) keyFactory.generatePrivate(privateKeySpec);
        } catch (Exception e) {
            throw new IllegalStateException("Ошибка загрузки RSA ключей", e);
        }
    }
}
