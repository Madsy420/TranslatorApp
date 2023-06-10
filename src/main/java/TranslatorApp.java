import javax.swing.*;

public class TranslatorApp {
    private static String googleVisionCredPath = "GOOGLE\\VISION\\CRED\\PATH";
    private static String googleTranslateCredPath = "GOOGLE\\TRANSLATE\\CRED\\PATH";
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new TranslatorAppUI(googleVisionCredPath, googleTranslateCredPath);

            // Pack and set frame properties
            frame.pack();
            frame.setLocationRelativeTo(null); // Center the frame on the screen
            frame.setVisible(true);
        });
    }
}
