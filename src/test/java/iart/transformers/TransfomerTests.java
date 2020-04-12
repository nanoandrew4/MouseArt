package iart.transformers;

import iart.GlobalVariables;
import javafx.geometry.Rectangle2D;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TransfomerTests {

	@BeforeClass
	public static void setup() {
		GlobalVariables.screenWidth = 1000;
		GlobalVariables.screenHeight = 1000;
	}

	@Test
	public void testHasNeighboursDirectlyToRight() {
		Screen s;

		// Testing rectangles that are considered direct neighbours
		s = new Screen(new Rectangle2D(0, 0, 100, 100));
		Assert.assertTrue(s.hasNeighbourDirectlyToRight(new Rectangle2D(100, 0, 100, 100)));

		s = new Screen(new Rectangle2D(0, 0, 100, 100));
		Assert.assertTrue(s.hasNeighbourDirectlyToRight(new Rectangle2D(100, 50, 100, 100)));

		s = new Screen(new Rectangle2D(0, 50, 100, 100));
		Assert.assertTrue(s.hasNeighbourDirectlyToRight(new Rectangle2D(100, 0, 100, 100)));

		// Testing rectangles that are not considered direct neighbours
		s = new Screen(new Rectangle2D(0, 0, 100, 100));
		Assert.assertFalse(s.hasNeighbourDirectlyToRight(new Rectangle2D(100, 100, 100, 100)));

		s = new Screen(new Rectangle2D(0, 100, 100, 100));
		Assert.assertFalse(s.hasNeighbourDirectlyToRight(new Rectangle2D(100, 0, 100, 100)));
	}

	@Test
	public void testHasNeighbourToRight() {
		Screen s;

		// Testing rectangles that are considered neighbours
		s = new Screen(new Rectangle2D(0, 0, 100, 100));
		Assert.assertTrue(s.hasNeighbourToRight(new Rectangle2D(200, 0, 100, 100)));

		s = new Screen(new Rectangle2D(0, 0, 100, 100));
		Assert.assertTrue(s.hasNeighbourToRight(new Rectangle2D(200, 50, 100, 100)));

		s = new Screen(new Rectangle2D(0, 50, 100, 100));
		Assert.assertTrue(s.hasNeighbourToRight(new Rectangle2D(200, 0, 100, 100)));

		// Testing rectangles that are not considered neighbours
		s = new Screen(new Rectangle2D(0, 100, 100, 100));
		Assert.assertFalse(s.hasNeighbourToRight(new Rectangle2D(200, 0, 100, 100)));

		s = new Screen(new Rectangle2D(0, 0, 100, 100));
		Assert.assertFalse(s.hasNeighbourToRight(new Rectangle2D(200, 100, 100, 100)));
	}

	@Test
	public void testHasNeighboursDirectlyToLeft() {
		Screen s;

		// Testing rectangles that are considered direct neighbours
		s = new Screen(new Rectangle2D(100, 0, 100, 100));
		Assert.assertTrue(s.hasNeighbourDirectlyToLeft(new Rectangle2D(0, 0, 100, 100)));

		s = new Screen(new Rectangle2D(100, 50, 100, 100));
		Assert.assertTrue(s.hasNeighbourDirectlyToLeft(new Rectangle2D(0, 0, 100, 100)));

		s = new Screen(new Rectangle2D(100, 0, 100, 100));
		Assert.assertTrue(s.hasNeighbourDirectlyToLeft(new Rectangle2D(0, 50, 100, 100)));

		// Testing rectangles that are not considered direct neighbours
		s = new Screen(new Rectangle2D(100, 100, 100, 100));
		Assert.assertFalse(s.hasNeighbourDirectlyToLeft(new Rectangle2D(0, 0, 100, 100)));

		s = new Screen(new Rectangle2D(0, 0, 100, 100));
		Assert.assertFalse(s.hasNeighbourDirectlyToLeft(new Rectangle2D(100, 100, 100, 100)));
	}
}
