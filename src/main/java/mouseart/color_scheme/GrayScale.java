package mouseart.color_scheme;

import mouseart.MouseArt;

import java.awt.*;

public class GrayScale implements ColorScheme{
	public Color getColor(int alpha) {
			return new Color(0, 0, 0, alpha);
	}

	private Color getRandGrayScale(int alpha) {
		int shadeOfGray = MouseArt.rand.nextInt(150) + 50;
		return new Color(shadeOfGray, shadeOfGray, shadeOfGray, alpha);
	}
}
