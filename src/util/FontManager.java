package util;

import java.awt.Font;

public class FontManager {

    // 1. Detect runtime operating system to determine the safest local Amharic font
    public static String getAmharicFontName() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) return "Nyala";
        if (os.contains("mac")) return "Kefa";
        return "Abyssinica SIL"; // Fallback for Linux environments
    }

    // 2. Returns a standard plain font with custom text size
    // Inside your FontManager class
    public static Font getPlainFont(int size) {
        // Using a system font fallback sequence allows fallback glyph layout mechanics to load icons
        return new Font("SansSerif", Font.PLAIN, size);
    }

    // 3. Returns a heavy bold font with custom text size
    public static Font getBoldFont(int size) {
        return new Font(getAmharicFontName(), Font.BOLD, size);
    }
}