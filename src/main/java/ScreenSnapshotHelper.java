import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;

public class ScreenSnapshotHelper {
    public static BufferedImage getScreenSnap(Rectangle captureRect) {

        try {
            Robot robot = new Robot();
            BufferedImage screenshot = robot.createScreenCapture(captureRect);

            return screenshot;
        } catch (AWTException e) {
            return null;
        }

    }
}
