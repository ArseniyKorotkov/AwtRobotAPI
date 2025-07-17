package by.arsy;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class AwtRobotApi {

    private final Robot robot;
    private final int pressPause = 100;
    private final int releasePause = 240;
    private final int millisInSecond = 1000;

    public AwtRobotApi() {
        try {
            robot = new Robot(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice());
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }


    public void clickButton(int keyCode) {
        robot.keyPress(keyCode);
        sleep(pressPause);
        robot.keyRelease(keyCode);
        sleep(releasePause);
    }


    public void clickButtonPair(int mainKeyCode, int secondKeyCode) {
        robot.keyPress(mainKeyCode);
        clickButton(secondKeyCode);
        robot.keyRelease(mainKeyCode);
    }


    public void clickButtonPairPaste() {
        clickButtonPair(KeyEvent.VK_CONTROL, KeyEvent.VK_V);
    }


    public void clickButtonPairPaste(String text) {
        Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        systemClipboard.setContents(new StringSelection(text), null);
        clickButtonPairPaste();
    }


    public void selectString() {
        clickButton(KeyEvent.VK_HOME);
        robot.keyPress(KeyEvent.VK_SHIFT);
        sleep(pressPause);
        clickButton(KeyEvent.VK_END);
        robot.keyRelease(KeyEvent.VK_SHIFT);
        sleep(releasePause);
    }


    public void spinMouseWheelDown(int steps) {
        robot.mouseWheel(steps);
    }


    public void spinMouseWheelUp(int steps) {
        spinMouseWheelDown(steps * (-1));
    }


    public void spinMouseWheelDown(int steps, double timesInSecond) {
        for (int i = 0; i < steps; i++) {
            spinMouseWheelDown(1);
            sleep((int)(millisInSecond / timesInSecond));
        }
    }


    public void spinMouseWheelUp(int steps, double timesInSecond) {
        for (int i = 0; i < steps; i++) {
            spinMouseWheelUp(1);
            sleep((int)(millisInSecond / timesInSecond));
        }
    }


    public void clickMouse(int x, int y) {
        robot.mouseMove(x, y);
        clickMouseLeftButton();
    }


    public void clickMouseLeftButton() {
        clickMouse(InputEvent.BUTTON1_DOWN_MASK);
    }


    public void clickMouse(int keyCode) {
        robot.mousePress(keyCode);
        sleep(pressPause);
        robot.mouseRelease(keyCode);
        sleep(releasePause);
    }


    private void sleep(long value) {
        try {
            Thread.sleep(value);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
