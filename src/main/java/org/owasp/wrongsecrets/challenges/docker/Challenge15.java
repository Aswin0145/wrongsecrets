package org.owasp.wrongsecrets.challenges.docker;

import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@Slf4j
@Component
@Order(15)
public class Challenge15 extends Challenge {

    private String ciphterText = "qemGhPXJjmipa9O7cYBJnuO79BQg/MgvSFbV9rhiBFuEmVqEfDsuz6xfBDMV2lH8TAhwKX39OrW+WIYxgaEWl8c1/n93Yxz5G/ZKbuTBbEaJ58YvC88IoB4NtnQciU6p+uJ+P+uHMMzRGQ0oGNvQeb5+bKK9V62Rp4aOhDupHnjeTUPKmWUV9/lzC5IUM7maNGuBLllzJnoM6QHMnGe5YpBBEA==";
    private String encryptionKey;


    public Challenge15(ScoreCard scoreCard) {
        super(scoreCard);
        encryptionKey = Base64.getEncoder().encodeToString("this is it for now".getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Spoiler spoiler() {
        return new Spoiler(quickDecrypt(ciphterText));
    }

    @Override
    protected boolean answerCorrect(String answer) {
        return answer.equals(quickDecrypt(ciphterText));
    }

    @Override
    public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
        return List.of(RuntimeEnvironment.Environment.DOCKER);
    }

    private String quickDecrypt(String cipherText) {
        try {
            final byte[] keyData = Base64.getDecoder().decode(encryptionKey);
            int aes256KeyLengthInBytes = 16;
            byte[] key = new byte[aes256KeyLengthInBytes];
            System.arraycopy(keyData, 0, key, 0, 16);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            int gcmTagLengthInBytes = 16;
            int gcmIVLengthInBytes = 12;
            byte[] initializationVector = new byte[gcmIVLengthInBytes];
            Arrays.fill(initializationVector, (byte) 0); //done for "poor-man's convergent encryption", please check actual convergent cryptosystems for better implementation ;-)
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(gcmTagLengthInBytes * 8, initializationVector);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmParameterSpec);
            byte[] plainTextBytes = cipher.doFinal(Base64.getDecoder().decode(cipherText.getBytes(StandardCharsets.UTF_8)));
            return new String(plainTextBytes);
        } catch (Exception e) {
            log.warn("Exception with Challenge 15", e);
            return "";
        }
    }


    //arcane:
    //qemGhPXJjmipa9O7cYBJnuO79BQg/MgvSFbV9rhiBFuEmVqEfDsuz6xfBDMV2lH8TAhwKX39OrW+WIYxgaEWl8c1/n93Yxz5G/ZKbuTBbEaJ58YvC88IoB4NtnQciU6p+uJ+P+uHMMzRGQ0oGNvQeb5+bKK9V62Rp4aOhDupHnjeTUPKmWUV9/lzC5IUM7maNGuBLllzJnoM6QHMnGe5YpBBEA==
    //wrongsecrets:
    //qcyRgfXSh0HUKsW/Xb5LnuWt9DgU8tQJfluR66UDDlmMgVWCGEwk1qxKAzUcpzb0KWQxP3nRFqO4SZEgqp8Ul8Ej/lNDbQCgBuszE/3WTn+g09Q7HcVUphA8g0Atg1GG4MpoepL8QOnhC0wxKMuqbe9TCu2nVqmUptKTmXGwAnmQH1TIl2MUueRuXpRKe72IMzKen1ArbMZqhu0I2HivROZgCUo=
    //wrongsecrets-2:
    //qcyRgfXSh0HUKsW/Xb5LnuWt9DgU8tQJfluR66UDDlmMgVWCGEwk1qxKCi4ZvzDwM38xP3nRFqO4SZEgqp8Ul8Ej/lNDbQCgBuszSILVSV6D9eojOMl6zTcNgzUmjW2K3dJKN9LqXOLYezEpEN2gUaYqPu2nVqmUptKTmXGwAnmQH1TIl2MUueRuXpRKe72IMzKenxZHKRsNFp+ebQebS3qzP+Q=

}
