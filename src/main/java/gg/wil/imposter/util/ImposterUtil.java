package gg.wil.imposter.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ServerWebExchange;

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

    public static String getClientIP(ServerWebExchange exchange) {
        String header = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (header != null && !header.isEmpty() && !"unknown".equalsIgnoreCase(header)) {
            return header.split(",")[0].trim();
        }

        // fallback to X-Real-IP if X-Forwarded-For is not present
        header = exchange.getRequest().getHeaders().getFirst("X-Real-IP");
        if (header != null && !header.isEmpty() && !"unknown".equalsIgnoreCase(header)) {
            return header;
        }

        // fallback to the direct remote address if no proxy headers are found
        if (exchange.getRequest().getRemoteAddress() != null) {
            return exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        }
        return "unknown";
    }
}
