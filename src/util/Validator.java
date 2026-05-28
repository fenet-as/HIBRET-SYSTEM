package util;

public class Validator {

    // ================= EMPTY =================

    public static boolean isEmpty(String text) {

        return text == null
                || text.trim().isEmpty();
    }

    // ================= NUMERIC =================

    public static boolean isNumeric(String text) {

        try {

            Double.parseDouble(text);

            return true;

        } catch (Exception e) {

            return false;
        }
    }

    // ================= POSITIVE =================

    public static boolean isPositive(String text) {

        try {

            return Double.parseDouble(text) > 0;

        } catch (Exception e) {

            return false;
        }
    }

    // ================= POSITIVE NUMBER =================

    public static boolean isPositiveNumber(String text) {

        return isPositive(text);
    }

    // ================= REALISTIC =================

    public static boolean isRealisticAmount(String text) {

        try {

            double value =
                    Double.parseDouble(text);

            return value <= 1000000;

        } catch (Exception e) {

            return false;
        }
    }
}