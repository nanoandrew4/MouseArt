package iart;

public class GlobalVariables {
	public static double screenWidth, screenHeight;
	private static double resMultiplier = 1;

	private static double virtualScreenWidth, virtualScreenHeight;

	public static double getResMultiplier() {
		return resMultiplier;
	}

	public static void setResMultiplier(double resMultiplier) {
		GlobalVariables.resMultiplier = Math.sqrt(resMultiplier);
		virtualScreenWidth = screenWidth * resMultiplier;
		virtualScreenHeight = screenHeight * resMultiplier;
	}

	public static double getVirtualScreenWidth() {
		return virtualScreenWidth;
	}

	public static double getVirtualScreenHeight() {
		return virtualScreenHeight;
	}
}
