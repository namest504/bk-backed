package k_paas.balloon.keeper.global.util;

import java.util.UUID;

public class CodeGenerateUtil {
    /**
     * Util 클래스 인스턴스화 방지
     */
    private CodeGenerateUtil() {
    }
    public static String generateCode() {
        return UUID.randomUUID().toString().toUpperCase();
    }
}
