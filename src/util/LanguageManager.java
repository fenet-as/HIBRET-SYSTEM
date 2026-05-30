package util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class LanguageManager {
    private static final String PREF_LANG_KEY = "selected_language";
    private static ResourceBundle bundle;
    private static Locale currentLocale;
    private static final Preferences prefs = Preferences.userNodeForPackage(LanguageManager.class);

    static {
        // Retrieve last saved language preference, default to English ("en")
        String savedLang = prefs.get(PREF_LANG_KEY, "en");
        setLanguage(savedLang);
    }



    public static void setLanguage(String langCode) {
        currentLocale = new Locale(langCode);

        // Look directly inside your local development directory
        String fallbackFilePath = "src/messages_" + langCode + ".properties";

        try (java.io.FileInputStream fis = new java.io.FileInputStream(fallbackFilePath);
             java.io.InputStreamReader isr = new java.io.InputStreamReader(fis, java.nio.charset.StandardCharsets.UTF_8)) {

            bundle = new java.util.PropertyResourceBundle(isr);
            System.out.println("Successfully loaded language bundle from disk: " + fallbackFilePath);

        } catch (Exception diskEx) {
            System.err.println("Disk path failed, trying fallback stream...");
            // Fallback to the classpath search if the direct file path fails
            String resourceName = "/messages_" + langCode + ".properties";
            try (java.io.InputStream is = LanguageManager.class.getResourceAsStream(resourceName)) {
                if (is != null) {
                    try (java.io.InputStreamReader isr = new java.io.InputStreamReader(is, java.nio.charset.StandardCharsets.UTF_8)) {
                        bundle = new java.util.PropertyResourceBundle(isr);
                    }
                } else {
                    bundle = ResourceBundle.getBundle("messages", currentLocale);
                }
            } catch (Exception e) {
                System.err.println("Complete failure loading resource bundles:");
                e.printStackTrace();
            }
        }

        prefs.put(PREF_LANG_KEY, langCode);
    }



    public static String getString(String key) {
        try {
            return bundle.getString(key);
        } catch (Exception e) {
            return key; // Fallback placeholder if key is missing
        }
    }

    // Handles formatted strings containing variable injections like {0}
    public static String getFormattedString(String key, Object... args) {
        try {
            String pattern = bundle.getString(key);
            return MessageFormat.format(pattern, args);
        } catch (Exception e) {
            return key;
        }
    }

    public static Locale getCurrentLocale() {
        return currentLocale;
    }
}