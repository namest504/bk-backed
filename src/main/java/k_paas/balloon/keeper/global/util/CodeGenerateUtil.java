package k_paas.balloon.keeper.global.util;

import java.util.UUID;

public class CodeGenerateUtil {
    public static String generateCode() {
        return UUID.randomUUID().toString().toUpperCase();
    }
}
