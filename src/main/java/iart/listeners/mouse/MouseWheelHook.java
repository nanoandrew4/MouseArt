package iart.listeners.mouse;

import org.jnativehook.GlobalScreen;
import org.jnativehook.mouse.NativeMouseWheelEvent;
import org.jnativehook.mouse.NativeMouseWheelListener;

public class MouseWheelHook implements NativeMouseWheelListener {

	public MouseWheelHook() {
		GlobalScreen.addNativeMouseWheelListener(this);
	}

	@Override
	public void nativeMouseWheelMoved(NativeMouseWheelEvent nativeMouseWheelEvent) {
	}
}
