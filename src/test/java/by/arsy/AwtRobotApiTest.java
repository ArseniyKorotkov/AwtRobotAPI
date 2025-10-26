package by.arsy;

import junit.framework.TestCase;

import java.awt.event.KeyEvent;

/**
 * Unit test for simple App.
 */
public class AwtRobotApiTest extends TestCase {

	private final AwtRobotApi awtRobotApi = new AwtRobotApi(0, true);

	public void testClickButtonGroup() {
		awtRobotApi.clickButtonGroup(
				KeyEvent.VK_SHIFT,
				KeyEvent.VK_CONTROL,
				KeyEvent.VK_RIGHT
		);
	}
}
