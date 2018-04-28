package mouseart.color_scheme;

import mouseart.MouseArt;
import mouseart.geometry.Circle;
import mouseart.geometry.Geometry;

import java.awt.*;

public class GrayScale implements ColorScheme{
	public Color getColor(Geometry g, int alpha) {
		if (g instanceof Circle)
			return getRandGrayScale(alpha);
		else
			return new Color(0, 0, 0, alpha);
	}

	private Color getRandGrayScale(int alpha) {
		int shadeOfGray = MouseArt.rand.nextInt(150) + 50;
		return new Color(shadeOfGray, shadeOfGray, shadeOfGray, alpha);
	}
}
