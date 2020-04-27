package iart;

public class GlobalVariables {
	public static double screenWidth, screenHeight;
	public static boolean transformMousePosition = false;
	private static double resMultiplier = 1;

	private static double virtualScreenWidth, virtualScreenHeight;

	public static double getResMultiplier() {
		return resMultiplier;
	}

	public static void setResMultiplier(double resMultiplier) {
		GlobalVariables.resMultiplier = Math.sqrt(resMultiplier);
		virtualScreenWidth = screenWidth * GlobalVariables.resMultiplier;
		virtualScreenHeight = screenHeight * GlobalVariables.resMultiplier;
	}

	public static double getVirtualScreenWidth() {
		return virtualScreenWidth;
	}

	public static double getVirtualScreenHeight() {
		return virtualScreenHeight;
	}
}
