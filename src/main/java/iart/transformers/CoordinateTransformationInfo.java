package iart.transformers;

public class CoordinateTransformationInfo {
	private double combinedIntersectingVirtualScreensWidthOrHeight;

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

	public void setCombinedIntersectingVirtualScreensWidthOrHeight(double combinedIntersectingVirtualScreensWidthOrHeight) {
		this.combinedIntersectingVirtualScreensWidthOrHeight = combinedIntersectingVirtualScreensWidthOrHeight;
	}

	public double getCombinedIntersectingVirtualScreensWidthOrHeight() {
		return combinedIntersectingVirtualScreensWidthOrHeight;
	}
}