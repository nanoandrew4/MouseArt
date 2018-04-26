package mouseart;

import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;

public class MouseHook implements NativeMouseInputListener {
	@Override
	public void nativeMouseClicked(NativeMouseEvent nativeMouseEvent) {
		System.out.println(nativeMouseEvent.getButton() + " clicked");
	}

	@Override
	public void nativeMousePressed(NativeMouseEvent nativeMouseEvent) {
		ImageRecorder.mousePressed = true;
	}

	@Override
	public void nativeMouseReleased(NativeMouseEvent nativeMouseEvent) {
		ImageRecorder.mouseReleased = true;
	}

	@Override
	public void nativeMouseMoved(NativeMouseEvent nativeMouseEvent) {
	}

	@Override
	public void nativeMouseDragged(NativeMouseEvent nativeMouseEvent) {
	}
}
