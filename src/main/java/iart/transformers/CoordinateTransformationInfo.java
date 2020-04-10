package iart.transformers;

public class CoordinateTransformationInfo {
	private double intersectingVirtualScreensLength;

	private TransformationAxis axis;

	public CoordinateTransformationInfo(TransformationAxis axis) {
		this.axis = axis;
	}

	public void setAxis(TransformationAxis axis) {
		this.axis = axis;
	}

	public TransformationAxis getAxis() {
		return axis;
	}

	public void setIntersectingVirtualScreensLength(double intersectingVirtualScreensLength) {
		this.intersectingVirtualScreensLength = intersectingVirtualScreensLength;
	}

	public double getIntersectingVirtualScreensLength() {
		return intersectingVirtualScreensLength;
	}
}