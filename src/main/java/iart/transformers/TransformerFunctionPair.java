package iart.transformers;

import java.awt.*;
import java.util.function.Function;

public class TransformerFunctionPair {

	private Function<Integer, Integer> transformX, transformY;

	private TransformerFunctionPair() {

	}

	public TransformerFunctionPair(Function<Integer, Integer> transformX, Function<Integer, Integer> transformY) {
		this.transformX = transformX;
		this.transformY = transformY;
	}

	public void transform(Point p) {
		p.x = transformX.apply(p.x);
		p.y = transformY.apply(p.y);
	}
}
