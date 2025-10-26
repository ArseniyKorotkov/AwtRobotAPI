package by.arsy;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

public class AwtRobotApi {

	private final Robot robot;
	private final int pressPause = 100;
	private final int releasePause = 240;
	private final int millisInSecond = 1000;

	/**
	 * @param stopKeyCode           {@link #initStopListener}
	 * @param showClickedButtonCode {@link #initStopListener}
	 */
	public AwtRobotApi(int stopKeyCode, boolean showClickedButtonCode) {
		try {
			robot = new Robot(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice());
		} catch (AWTException e) {
			throw new RuntimeException(e);
		}
		initStopListener(stopKeyCode, showClickedButtonCode);
	}


	/**
	 * Make click button on the keyboard according to the button code
	 *
	 * @param keyCode button code
	 */
	public void clickButton(int keyCode) {
		robot.keyPress(keyCode);
		sleep(pressPause);
		robot.keyRelease(keyCode);
		sleep(releasePause);
	}


	/**
	 * Press two buttons in a specific order to use their combination
	 *
	 * @param mainKeyCode   is pressed first and released last
	 * @param secondKeyCode is pressed second and released first
	 *
	 * @deprecated use {@link #clickButtonGroup)}
	 */
	@Deprecated()
	public void clickButtonPair(int mainKeyCode, int secondKeyCode) {
		clickButtonGroup(mainKeyCode, secondKeyCode);
	}


	/**
	 * Press few buttons in a specific order to use their combination
	 *
	 * @param keyCodeGroup are pressed in order and released in reverse order
	 */
	public void clickButtonGroup(int... keyCodeGroup) {
		ArrayList<Integer> keyCodeList = Arrays.stream(keyCodeGroup)
				.boxed()
				.collect(Collectors.toCollection(ArrayList::new));

		keyCodeList.forEach(it -> {
			robot.keyPress(it);
			sleep(pressPause);
		});

		Collections.reverse(keyCodeList);

		keyCodeList.forEach(it -> {
			robot.keyRelease(it);
			sleep(releasePause);
		});
	}


	/**
	 * Click Ctrl + V
	 */
	public void clickButtonPairPaste() {
		clickButtonGroup(KeyEvent.VK_CONTROL, KeyEvent.VK_V);
	}


	/**
	 * Takes text to the clipboard and pastes it
	 *
	 * @param text to paste
	 */
	public void clickButtonPairPaste(String text) {
		Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		systemClipboard.setContents(new StringSelection(text), null);
		clickButtonPairPaste();
	}


	/**
	 * When working with a text document, selects the current line entirely
	 */
	public void selectString() {
		clickButton(KeyEvent.VK_HOME);
		robot.keyPress(KeyEvent.VK_SHIFT);
		sleep(pressPause);
		clickButton(KeyEvent.VK_END);
		robot.keyRelease(KeyEvent.VK_SHIFT);
		sleep(releasePause);
	}


	/**
	 * Make rotate the mouse wheel down at the one moment
	 *
	 * @param steps set the number of clicks
	 */
	public void spinMouseWheelDown(int steps) {
		robot.mouseWheel(steps);
	}


	/**
	 * Make rotate the mouse wheel up at the one moment
	 *
	 * @param steps set the number of clicks
	 */
	public void spinMouseWheelUp(int steps) {
		spinMouseWheelDown(steps * (-1));
	}


	/**
	 * Make rotate the mouse wheel down at the specified speed
	 *
	 * @param steps         set the number of clicks
	 * @param timesInSecond set the speed for the wheel per second
	 */
	public void spinMouseWheelDown(int steps, double timesInSecond) {
		for (int i = 0; i < steps; i++) {
			spinMouseWheelDown(1);
			sleep((int) (millisInSecond / timesInSecond));
		}
	}


	/**
	 * Make rotate the mouse wheel up at the specified speed
	 *
	 * @param steps         set the number of clicks
	 * @param timesInSecond set the speed for the wheel per second
	 */
	public void spinMouseWheelUp(int steps, double timesInSecond) {
		for (int i = 0; i < steps; i++) {
			spinMouseWheelUp(1);
			sleep((int) (millisInSecond / timesInSecond));
		}
	}


	/**
	 * Make a left mouse click on the specified coordinates
	 *
	 * @param x coordinate
	 * @param y coordinate
	 */
	public void clickMouse(int x, int y) {
		moveMouse(x, y);
		clickMouseLeftButton();
	}


	/**
	 * Change cursor coordinates
	 *
	 * @param x coordinate
	 * @param y coordinate
	 */
	public void moveMouse(int x, int y) {
		robot.mouseMove(x, y);
	}


	/**
	 * Make a left mouse click
	 */
	public void clickMouseLeftButton() {
		clickMouse(InputEvent.BUTTON1_DOWN_MASK);
	}


	/**
	 * Make a mouse click according to the button code
	 *
	 * @param keyCode button code
	 * @see InputEvent static values
	 */
	public void clickMouse(int keyCode) {
		robot.mousePress(keyCode);
		sleep(pressPause);
		robot.mouseRelease(keyCode);
		sleep(releasePause);
	}


	/**
	 * The program termination method. It should be used at the end of the program to stop NativeKeyListener from running.
	 *
	 * @see AwtRobotApi#initStopListener(int, boolean)
	 */
	public void close() {
		System.exit(0);
	}


	private void sleep(long value) {
		try {
			Thread.sleep(value);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}


	/**
	 * Initializing Native KeyListener to quickly stop a program by pressing a button.
	 * It is necessary to use the program termination method.
	 *
	 * @param stopKeyCode           button`s code for fast stop program.
	 *                              You can use the showClickedButtonCode parameter to view the code of the button you are interested in,
	 *                              or use this list from jnativehook documentation:
	 *                              NativeKeyEvent.NATIVE_KEY_FIRST = 2400;
	 *                              NativeKeyEvent.NATIVE_KEY_LAST = 2402;
	 *                              NativeKeyEvent.NATIVE_KEY_TYPED = 2400;
	 *                              NativeKeyEvent.NATIVE_KEY_PRESSED = 2401;
	 *                              NativeKeyEvent.NATIVE_KEY_RELEASED = 2402;
	 *                              NativeKeyEvent.KEY_LOCATION_UNKNOWN = 0;
	 *                              NativeKeyEvent.KEY_LOCATION_STANDARD = 1;
	 *                              NativeKeyEvent.KEY_LOCATION_LEFT = 2;
	 *                              NativeKeyEvent.KEY_LOCATION_RIGHT = 3;
	 *                              NativeKeyEvent.KEY_LOCATION_NUMPAD = 4;
	 *                              NativeKeyEvent.VC_ESCAPE = 1;
	 *                              NativeKeyEvent.VC_F1 = 59;
	 *                              NativeKeyEvent.VC_F2 = 60;
	 *                              NativeKeyEvent.VC_F3 = 61;
	 *                              NativeKeyEvent.VC_F4 = 62;
	 *                              NativeKeyEvent.VC_F5 = 63;
	 *                              NativeKeyEvent.VC_F6 = 64;
	 *                              NativeKeyEvent.VC_F7 = 65;
	 *                              NativeKeyEvent.VC_F8 = 66;
	 *                              NativeKeyEvent.VC_F9 = 67;
	 *                              NativeKeyEvent.VC_F10 = 68;
	 *                              NativeKeyEvent.VC_F11 = 87;
	 *                              NativeKeyEvent.VC_F12 = 88;
	 *                              NativeKeyEvent.VC_F13 = 91;
	 *                              NativeKeyEvent.VC_F14 = 92;
	 *                              NativeKeyEvent.VC_F15 = 93;
	 *                              NativeKeyEvent.VC_F16 = 99;
	 *                              NativeKeyEvent.VC_F17 = 100;
	 *                              NativeKeyEvent.VC_F18 = 101;
	 *                              NativeKeyEvent.VC_F19 = 102;
	 *                              NativeKeyEvent.VC_F20 = 103;
	 *                              NativeKeyEvent.VC_F21 = 104;
	 *                              NativeKeyEvent.VC_F22 = 105;
	 *                              NativeKeyEvent.VC_F23 = 106;
	 *                              NativeKeyEvent.VC_F24 = 107;
	 *                              NativeKeyEvent.VC_BACKQUOTE = 41;
	 *                              NativeKeyEvent.VC_1 = 2;
	 *                              NativeKeyEvent.VC_2 = 3;
	 *                              NativeKeyEvent.VC_3 = 4;
	 *                              NativeKeyEvent.VC_4 = 5;
	 *                              NativeKeyEvent.VC_5 = 6;
	 *                              NativeKeyEvent.VC_6 = 7;
	 *                              NativeKeyEvent.VC_7 = 8;
	 *                              NativeKeyEvent.VC_8 = 9;
	 *                              NativeKeyEvent.VC_9 = 10;
	 *                              NativeKeyEvent.VC_0 = 11;
	 *                              NativeKeyEvent.VC_MINUS = 12;
	 *                              NativeKeyEvent.VC_EQUALS = 13;
	 *                              NativeKeyEvent.VC_BACKSPACE = 14;
	 *                              NativeKeyEvent.VC_TAB = 15;
	 *                              NativeKeyEvent.VC_CAPS_LOCK = 58;
	 *                              NativeKeyEvent.VC_A = 30;
	 *                              NativeKeyEvent.VC_B = 48;
	 *                              NativeKeyEvent.VC_C = 46;
	 *                              NativeKeyEvent.VC_D = 32;
	 *                              NativeKeyEvent.VC_E = 18;
	 *                              NativeKeyEvent.VC_F = 33;
	 *                              NativeKeyEvent.VC_G = 34;
	 *                              NativeKeyEvent.VC_H = 35;
	 *                              NativeKeyEvent.VC_I = 23;
	 *                              NativeKeyEvent.VC_J = 36;
	 *                              NativeKeyEvent.VC_K = 37;
	 *                              NativeKeyEvent.VC_L = 38;
	 *                              NativeKeyEvent.VC_M = 50;
	 *                              NativeKeyEvent.VC_N = 49;
	 *                              NativeKeyEvent.VC_O = 24;
	 *                              NativeKeyEvent.VC_P = 25;
	 *                              NativeKeyEvent.VC_Q = 16;
	 *                              NativeKeyEvent.VC_R = 19;
	 *                              NativeKeyEvent.VC_S = 31;
	 *                              NativeKeyEvent.VC_T = 20;
	 *                              NativeKeyEvent.VC_U = 22;
	 *                              NativeKeyEvent.VC_V = 47;
	 *                              NativeKeyEvent.VC_W = 17;
	 *                              NativeKeyEvent.VC_X = 45;
	 *                              NativeKeyEvent.VC_Y = 21;
	 *                              NativeKeyEvent.VC_Z = 44;
	 *                              NativeKeyEvent.VC_OPEN_BRACKET = 26;
	 *                              NativeKeyEvent.VC_CLOSE_BRACKET = 27;
	 *                              NativeKeyEvent.VC_BACK_SLASH = 43;
	 *                              NativeKeyEvent.VC_SEMICOLON = 39;
	 *                              NativeKeyEvent.VC_QUOTE = 40;
	 *                              NativeKeyEvent.VC_ENTER = 28;
	 *                              NativeKeyEvent.VC_COMMA = 51;
	 *                              NativeKeyEvent.VC_PERIOD = 52;
	 *                              NativeKeyEvent.VC_SLASH = 53;
	 *                              NativeKeyEvent.VC_SPACE = 57;
	 *                              NativeKeyEvent.VC_PRINTSCREEN = 3639;
	 *                              NativeKeyEvent.VC_SCROLL_LOCK = 70;
	 *                              NativeKeyEvent.VC_PAUSE = 3653;
	 *                              NativeKeyEvent.VC_INSERT = 3666;
	 *                              NativeKeyEvent.VC_DELETE = 3667;
	 *                              NativeKeyEvent.VC_HOME = 3655;
	 *                              NativeKeyEvent.VC_END = 3663;
	 *                              NativeKeyEvent.VC_PAGE_UP = 3657;
	 *                              NativeKeyEvent.VC_PAGE_DOWN = 3665;
	 *                              NativeKeyEvent.VC_UP = 57416;
	 *                              NativeKeyEvent.VC_LEFT = 57419;
	 *                              NativeKeyEvent.VC_CLEAR = 57420;
	 *                              NativeKeyEvent.VC_RIGHT = 57421;
	 *                              NativeKeyEvent.VC_DOWN = 57424;
	 *                              NativeKeyEvent.VC_NUM_LOCK = 69;
	 *                              NativeKeyEvent.VC_SEPARATOR = 83;
	 *                              NativeKeyEvent.VC_SHIFT = 42;
	 *                              NativeKeyEvent.VC_CONTROL = 29;
	 *                              NativeKeyEvent.VC_ALT = 56;
	 *                              NativeKeyEvent.VC_META = 3675;
	 *                              NativeKeyEvent.VC_CONTEXT_MENU = 3677;
	 *                              NativeKeyEvent.VC_POWER = 57438;
	 *                              NativeKeyEvent.VC_SLEEP = 57439;
	 *                              NativeKeyEvent.VC_WAKE = 57443;
	 *                              NativeKeyEvent.VC_MEDIA_PLAY = 57378;
	 *                              NativeKeyEvent.VC_MEDIA_STOP = 57380;
	 *                              NativeKeyEvent.VC_MEDIA_PREVIOUS = 57360;
	 *                              NativeKeyEvent.VC_MEDIA_NEXT = 57369;
	 *                              NativeKeyEvent.VC_MEDIA_SELECT = 57453;
	 *                              NativeKeyEvent.VC_MEDIA_EJECT = 57388;
	 *                              NativeKeyEvent.VC_VOLUME_MUTE = 57376;
	 *                              NativeKeyEvent.VC_VOLUME_UP = 57392;
	 *                              NativeKeyEvent.VC_VOLUME_DOWN = 57390;
	 *                              NativeKeyEvent.VC_APP_MAIL = 57452;
	 *                              NativeKeyEvent.VC_APP_CALCULATOR = 57377;
	 *                              NativeKeyEvent.VC_APP_MUSIC = 57404;
	 *                              NativeKeyEvent.VC_APP_PICTURES = 57444;
	 *                              NativeKeyEvent.VC_BROWSER_SEARCH = 57445;
	 *                              NativeKeyEvent.VC_BROWSER_HOME = 57394;
	 *                              NativeKeyEvent.VC_BROWSER_BACK = 57450;
	 *                              NativeKeyEvent.VC_BROWSER_FORWARD = 57449;
	 *                              NativeKeyEvent.VC_BROWSER_STOP = 57448;
	 *                              NativeKeyEvent.VC_BROWSER_REFRESH = 57447;
	 *                              NativeKeyEvent.VC_BROWSER_FAVORITES = 57446;
	 *                              NativeKeyEvent.VC_KATAKANA = 112;
	 *                              NativeKeyEvent.VC_UNDERSCORE = 115;
	 *                              NativeKeyEvent.VC_FURIGANA = 119;
	 *                              NativeKeyEvent.VC_KANJI = 121;
	 *                              NativeKeyEvent.VC_HIRAGANA = 123;
	 *                              NativeKeyEvent.VC_YEN = 125;
	 *                              NativeKeyEvent.VC_SUN_HELP = 65397;
	 *                              NativeKeyEvent.VC_SUN_STOP = 65400;
	 *                              NativeKeyEvent.VC_SUN_PROPS = 65398;
	 *                              NativeKeyEvent.VC_SUN_FRONT = 65399;
	 *                              NativeKeyEvent.VC_SUN_OPEN = 65396;
	 *                              NativeKeyEvent.VC_SUN_FIND = 65406;
	 *                              NativeKeyEvent.VC_SUN_AGAIN = 65401;
	 *                              NativeKeyEvent.VC_SUN_UNDO = 65402;
	 *                              NativeKeyEvent.VC_SUN_COPY = 65404;
	 *                              NativeKeyEvent.VC_SUN_INSERT = 65405;
	 *                              NativeKeyEvent.VC_SUN_CUT = 65403;
	 *                              NativeKeyEvent.VC_UNDEFINED = 0;
	 * @param showClickedButtonCode use standard output for show clicked button
	 * @see AwtRobotApi#close()
	 */
	private void initStopListener(final int stopKeyCode, final boolean showClickedButtonCode) {
		try {
			GlobalScreen.registerNativeHook();
			GlobalScreen.addNativeKeyListener(new NativeKeyListener() {
				@Override
				public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
					NativeKeyListener.super.nativeKeyPressed(nativeEvent);
					if (nativeEvent.getKeyCode() == stopKeyCode) {
						System.out.println("program is stopped from stop key with code: " + stopKeyCode);
						System.exit(0);
					}
					if (showClickedButtonCode) {
						System.out.println("Clicked button code is: " + nativeEvent.getKeyCode());
					}
				}
			});
		} catch (NativeHookException e) {
			throw new RuntimeException(e);
		}
	}

}
