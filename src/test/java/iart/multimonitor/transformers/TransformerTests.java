package iart.multimonitor.transformers;

import iart.GlobalVariables;
import javafx.geometry.Rectangle2D;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TransformerTests {

	@BeforeClass
	public static void setup() {
		GlobalVariables.screenWidth = 1000;
		GlobalVariables.screenHeight = 1000;
	}

	@Test
	public void testHasNeighbourDirectlyOnRightSide() {
		TransformableScreen s;

		// Testing rectangles that are considered direct neighbours
		s = new TransformableScreen(new Rectangle2D(0, 0, 100, 100));
		Assert.assertTrue(s.isNeighbouringDirectlyOnRightSide(new Rectangle2D(100, 0, 100, 100)));

		s = new TransformableScreen(new Rectangle2D(0, 0, 100, 100));
		Assert.assertTrue(s.isNeighbouringDirectlyOnRightSide(new Rectangle2D(100, 50, 100, 100)));

		s = new TransformableScreen(new Rectangle2D(0, 50, 100, 100));
		Assert.assertTrue(s.isNeighbouringDirectlyOnRightSide(new Rectangle2D(100, 0, 100, 100)));

		// Testing rectangles that are not considered direct neighbours
		s = new TransformableScreen(new Rectangle2D(0, 0, 100, 100));
		Assert.assertFalse(s.isNeighbouringDirectlyOnRightSide(new Rectangle2D(100, 100, 100, 100)));

		s = new TransformableScreen(new Rectangle2D(0, 100, 100, 100));
		Assert.assertFalse(s.isNeighbouringDirectlyOnRightSide(new Rectangle2D(100, 0, 100, 100)));
	}

	@Test
	public void testHasNeighbourOnRightSide() {
		TransformableScreen s;

		// Testing rectangles that are considered neighbours
		s = new TransformableScreen(new Rectangle2D(0, 0, 100, 100));
		Assert.assertTrue(s.isNeighbouringOnRightSide(new Rectangle2D(200, 0, 100, 100)));

		s = new TransformableScreen(new Rectangle2D(0, 0, 100, 100));
		Assert.assertTrue(s.isNeighbouringOnRightSide(new Rectangle2D(200, 50, 100, 100)));

		s = new TransformableScreen(new Rectangle2D(0, 50, 100, 100));
		Assert.assertTrue(s.isNeighbouringOnRightSide(new Rectangle2D(200, 0, 100, 100)));

		// Testing rectangles that are not considered neighbours
		s = new TransformableScreen(new Rectangle2D(0, 100, 100, 100));
		Assert.assertFalse(s.isNeighbouringOnRightSide(new Rectangle2D(200, 0, 100, 100)));

		s = new TransformableScreen(new Rectangle2D(0, 0, 100, 100));
		Assert.assertFalse(s.isNeighbouringOnRightSide(new Rectangle2D(200, 100, 100, 100)));
	}

	@Test
	public void testHasNeighbourDirectlyOnLeftSide() {
		TransformableScreen s;

		// Testing rectangles that are considered direct neighbours
		s = new TransformableScreen(new Rectangle2D(100, 0, 100, 100));
		Assert.assertTrue(s.isNeighbouringDirectlyOnLeftSide(new Rectangle2D(0, 0, 100, 100)));

		s = new TransformableScreen(new Rectangle2D(100, 50, 100, 100));
		Assert.assertTrue(s.isNeighbouringDirectlyOnLeftSide(new Rectangle2D(0, 0, 100, 100)));

		s = new TransformableScreen(new Rectangle2D(100, 0, 100, 100));
		Assert.assertTrue(s.isNeighbouringDirectlyOnLeftSide(new Rectangle2D(0, 50, 100, 100)));

		// Testing rectangles that are not considered direct neighbours
		s = new TransformableScreen(new Rectangle2D(100, 100, 100, 100));
		Assert.assertFalse(s.isNeighbouringDirectlyOnLeftSide(new Rectangle2D(0, 0, 100, 100)));

		s = new TransformableScreen(new Rectangle2D(0, 0, 100, 100));
		Assert.assertFalse(s.isNeighbouringDirectlyOnLeftSide(new Rectangle2D(100, 100, 100, 100)));
	}

	@Test
	public void testHasNeighbourOnLeftSide() {
		TransformableScreen s;

		// Testing rectangles that are considered direct neighbours
		s = new TransformableScreen(new Rectangle2D(150, 0, 100, 100));
		Assert.assertTrue(s.isNeighbouringOnLeftSide(new Rectangle2D(0, 0, 100, 100)));

		s = new TransformableScreen(new Rectangle2D(150, 50, 100, 100));
		Assert.assertTrue(s.isNeighbouringOnLeftSide(new Rectangle2D(0, 0, 100, 100)));

		s = new TransformableScreen(new Rectangle2D(150, 0, 100, 100));
		Assert.assertTrue(s.isNeighbouringOnLeftSide(new Rectangle2D(0, 50, 100, 100)));

		// Testing rectangles that are not considered direct neighbours
		s = new TransformableScreen(new Rectangle2D(150, 100, 100, 100));
		Assert.assertFalse(s.isNeighbouringOnLeftSide(new Rectangle2D(0, 0, 100, 100)));

		s = new TransformableScreen(new Rectangle2D(0, 0, 100, 100));
		Assert.assertFalse(s.isNeighbouringOnLeftSide(new Rectangle2D(150, 100, 100, 100)));
	}

	@Test
	public void testHasNeighbourDirectlyOnTop() {
		TransformableScreen s;

		// Testing rectangles that are considered direct neighbours
		s = new TransformableScreen(new Rectangle2D(0, 100, 100, 100));
		Assert.assertTrue(s.isNeighbouringDirectlyOnTopSide(new Rectangle2D(0, 0, 100, 100)));

		s = new TransformableScreen(new Rectangle2D(50, 100, 100, 100));
		Assert.assertTrue(s.isNeighbouringDirectlyOnTopSide(new Rectangle2D(0, 0, 100, 100)));

		s = new TransformableScreen(new Rectangle2D(50, 100, 100, 100));
		Assert.assertTrue(s.isNeighbouringDirectlyOnTopSide(new Rectangle2D(100, 0, 100, 100)));

		// Testing rectangles that are not considered direct neighbours
		s = new TransformableScreen(new Rectangle2D(0, 100, 100, 100));
		Assert.assertFalse(s.isNeighbouringDirectlyOnTopSide(new Rectangle2D(100, 0, 100, 100)));

		s = new TransformableScreen(new Rectangle2D(100, 100, 100, 100));
		Assert.assertFalse(s.isNeighbouringDirectlyOnTopSide(new Rectangle2D(0, 0, 100, 100)));
	}

	@Test
	public void testHasNeighbourOnTopSide() {
		TransformableScreen s;

		// Testing rectangles that are considered direct neighbours
		s = new TransformableScreen(new Rectangle2D(0, 150, 100, 100));
		Assert.assertTrue(s.isNeighbouringOnTopSide(new Rectangle2D(0, 0, 100, 100)));

		s = new TransformableScreen(new Rectangle2D(50, 150, 100, 100));
		Assert.assertTrue(s.isNeighbouringOnTopSide(new Rectangle2D(0, 0, 100, 100)));

		s = new TransformableScreen(new Rectangle2D(50, 150, 100, 100));
		Assert.assertTrue(s.isNeighbouringOnTopSide(new Rectangle2D(100, 0, 100, 100)));

		// Testing rectangles that are not considered direct neighbours
		s = new TransformableScreen(new Rectangle2D(0, 150, 100, 100));
		Assert.assertFalse(s.isNeighbouringOnTopSide(new Rectangle2D(100, 0, 100, 100)));

		s = new TransformableScreen(new Rectangle2D(100, 150, 100, 100));
		Assert.assertFalse(s.isNeighbouringOnTopSide(new Rectangle2D(0, 0, 100, 100)));
	}

	@Test
	public void testHasNeighbourDirectlyOnBottomSide() {
		TransformableScreen s;

		// Testing rectangles that are considered direct neighbours
		s = new TransformableScreen(new Rectangle2D(0, 0, 100, 100));
		Assert.assertTrue(s.isNeighbouringDirectlyOnBottomSide(new Rectangle2D(0, 100, 100, 100)));

		s = new TransformableScreen(new Rectangle2D(50, 0, 100, 100));
		Assert.assertTrue(s.isNeighbouringDirectlyOnBottomSide(new Rectangle2D(0, 100, 100, 100)));

		s = new TransformableScreen(new Rectangle2D(50, 0, 100, 100));
		Assert.assertTrue(s.isNeighbouringDirectlyOnBottomSide(new Rectangle2D(100, 100, 100, 100)));

		// Testing rectangles that are not considered direct neighbours
		s = new TransformableScreen(new Rectangle2D(0, 0, 100, 100));
		Assert.assertFalse(s.isNeighbouringDirectlyOnBottomSide(new Rectangle2D(100, 100, 100, 100)));

		s = new TransformableScreen(new Rectangle2D(100, 0, 100, 100));
		Assert.assertFalse(s.isNeighbouringDirectlyOnBottomSide(new Rectangle2D(0, 100, 100, 100)));
	}

	@Test
	public void testHasNeighbourOnBottomSide() {
		TransformableScreen s;

		// Testing rectangles that are considered direct neighbours
		s = new TransformableScreen(new Rectangle2D(0, 0, 100, 100));
		Assert.assertTrue(s.isNeighbouringOnBottomSide(new Rectangle2D(0, 150, 100, 100)));

		s = new TransformableScreen(new Rectangle2D(50, 0, 100, 100));
		Assert.assertTrue(s.isNeighbouringOnBottomSide(new Rectangle2D(0, 150, 100, 100)));

		s = new TransformableScreen(new Rectangle2D(50, 0, 100, 100));
		Assert.assertTrue(s.isNeighbouringOnBottomSide(new Rectangle2D(100, 150, 100, 100)));

		// Testing rectangles that are not considered direct neighbours
		s = new TransformableScreen(new Rectangle2D(0, 0, 100, 100));
		Assert.assertFalse(s.isNeighbouringOnBottomSide(new Rectangle2D(100, 150, 100, 100)));

		s = new TransformableScreen(new Rectangle2D(100, 0, 100, 100));
		Assert.assertFalse(s.isNeighbouringOnBottomSide(new Rectangle2D(0, 150, 100, 100)));
	}
}
