package gg.wil.imposter.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class ImposterUtil {

    private static final Logger logger = LoggerFactory.getLogger(ImposterUtil.class);

    public static SecureRandom generateSecureRandom() {
        try {
            return SecureRandom.getInstance("NativePRNGNonBlocking");
        } catch(NoSuchAlgorithmException ex) {
            logger.warn("Failed to get 'NativePRNGNonBlocking' algorithm, falling back to default.", ex);
            return new SecureRandom();
        }
    }

    public static boolean checkValidHexColorCode(String colorCode) {
        return colorCode.matches("#[0-9a-fA-F]{6}");
    }
}
