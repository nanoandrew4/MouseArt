package iart.transformers;

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
		Screen s;

		// Testing rectangles that are considered direct neighbours
		s = new Screen(new Rectangle2D(0, 0, 100, 100));
		Assert.assertTrue(s.hasNeighbourDirectlyOnRightSide(new Rectangle2D(100, 0, 100, 100)));

		s = new Screen(new Rectangle2D(0, 0, 100, 100));
		Assert.assertTrue(s.hasNeighbourDirectlyOnRightSide(new Rectangle2D(100, 50, 100, 100)));

		s = new Screen(new Rectangle2D(0, 50, 100, 100));
		Assert.assertTrue(s.hasNeighbourDirectlyOnRightSide(new Rectangle2D(100, 0, 100, 100)));

		// Testing rectangles that are not considered direct neighbours
		s = new Screen(new Rectangle2D(0, 0, 100, 100));
		Assert.assertFalse(s.hasNeighbourDirectlyOnRightSide(new Rectangle2D(100, 100, 100, 100)));

		s = new Screen(new Rectangle2D(0, 100, 100, 100));
		Assert.assertFalse(s.hasNeighbourDirectlyOnRightSide(new Rectangle2D(100, 0, 100, 100)));
	}

	@Test
	public void testHasNeighbourOnRightSide() {
		Screen s;

		// Testing rectangles that are considered neighbours
		s = new Screen(new Rectangle2D(0, 0, 100, 100));
		Assert.assertTrue(s.hasNeighbourOnRightSide(new Rectangle2D(200, 0, 100, 100)));

		s = new Screen(new Rectangle2D(0, 0, 100, 100));
		Assert.assertTrue(s.hasNeighbourOnRightSide(new Rectangle2D(200, 50, 100, 100)));

		s = new Screen(new Rectangle2D(0, 50, 100, 100));
		Assert.assertTrue(s.hasNeighbourOnRightSide(new Rectangle2D(200, 0, 100, 100)));

		// Testing rectangles that are not considered neighbours
		s = new Screen(new Rectangle2D(0, 100, 100, 100));
		Assert.assertFalse(s.hasNeighbourOnRightSide(new Rectangle2D(200, 0, 100, 100)));

		s = new Screen(new Rectangle2D(0, 0, 100, 100));
		Assert.assertFalse(s.hasNeighbourOnRightSide(new Rectangle2D(200, 100, 100, 100)));
	}

	@Test
	public void testHasNeighbourDirectlyOnLeftSide() {
		Screen s;

		// Testing rectangles that are considered direct neighbours
		s = new Screen(new Rectangle2D(100, 0, 100, 100));
		Assert.assertTrue(s.hasNeighbourDirectlyOnLeftSide(new Rectangle2D(0, 0, 100, 100)));

		s = new Screen(new Rectangle2D(100, 50, 100, 100));
		Assert.assertTrue(s.hasNeighbourDirectlyOnLeftSide(new Rectangle2D(0, 0, 100, 100)));

		s = new Screen(new Rectangle2D(100, 0, 100, 100));
		Assert.assertTrue(s.hasNeighbourDirectlyOnLeftSide(new Rectangle2D(0, 50, 100, 100)));

		// Testing rectangles that are not considered direct neighbours
		s = new Screen(new Rectangle2D(100, 100, 100, 100));
		Assert.assertFalse(s.hasNeighbourDirectlyOnLeftSide(new Rectangle2D(0, 0, 100, 100)));

		s = new Screen(new Rectangle2D(0, 0, 100, 100));
		Assert.assertFalse(s.hasNeighbourDirectlyOnLeftSide(new Rectangle2D(100, 100, 100, 100)));
	}

	@Test
	public void testHasNeighbourOnLeftSide() {
		Screen s;

		// Testing rectangles that are considered direct neighbours
		s = new Screen(new Rectangle2D(150, 0, 100, 100));
		Assert.assertTrue(s.hasNeighbourOnLeftSide(new Rectangle2D(0, 0, 100, 100)));

		s = new Screen(new Rectangle2D(150, 50, 100, 100));
		Assert.assertTrue(s.hasNeighbourOnLeftSide(new Rectangle2D(0, 0, 100, 100)));

		s = new Screen(new Rectangle2D(150, 0, 100, 100));
		Assert.assertTrue(s.hasNeighbourOnLeftSide(new Rectangle2D(0, 50, 100, 100)));

		// Testing rectangles that are not considered direct neighbours
		s = new Screen(new Rectangle2D(150, 100, 100, 100));
		Assert.assertFalse(s.hasNeighbourOnLeftSide(new Rectangle2D(0, 0, 100, 100)));

		s = new Screen(new Rectangle2D(0, 0, 100, 100));
		Assert.assertFalse(s.hasNeighbourOnLeftSide(new Rectangle2D(150, 100, 100, 100)));
	}

	@Test
	public void testHasNeighbourDirectlyOnTop() {
		Screen s;

		// Testing rectangles that are considered direct neighbours
		s = new Screen(new Rectangle2D(0, 100, 100, 100));
		Assert.assertTrue(s.hasNeighbourDirectlyOnTopSide(new Rectangle2D(0, 0, 100, 100)));

		s = new Screen(new Rectangle2D(50, 100, 100, 100));
		Assert.assertTrue(s.hasNeighbourDirectlyOnTopSide(new Rectangle2D(0, 0, 100, 100)));

		s = new Screen(new Rectangle2D(50, 100, 100, 100));
		Assert.assertTrue(s.hasNeighbourDirectlyOnTopSide(new Rectangle2D(100, 0, 100, 100)));

		// Testing rectangles that are not considered direct neighbours
		s = new Screen(new Rectangle2D(0, 100, 100, 100));
		Assert.assertFalse(s.hasNeighbourDirectlyOnTopSide(new Rectangle2D(100, 0, 100, 100)));

		s = new Screen(new Rectangle2D(100, 100, 100, 100));
		Assert.assertFalse(s.hasNeighbourDirectlyOnTopSide(new Rectangle2D(0, 0, 100, 100)));
	}

	@Test
	public void testHasNeighbourOnTopSide() {
		Screen s;

		// Testing rectangles that are considered direct neighbours
		s = new Screen(new Rectangle2D(0, 150, 100, 100));
		Assert.assertTrue(s.hasNeighbourOnTopSide(new Rectangle2D(0, 0, 100, 100)));

		s = new Screen(new Rectangle2D(50, 150, 100, 100));
		Assert.assertTrue(s.hasNeighbourOnTopSide(new Rectangle2D(0, 0, 100, 100)));

		s = new Screen(new Rectangle2D(50, 150, 100, 100));
		Assert.assertTrue(s.hasNeighbourOnTopSide(new Rectangle2D(100, 0, 100, 100)));

		// Testing rectangles that are not considered direct neighbours
		s = new Screen(new Rectangle2D(0, 150, 100, 100));
		Assert.assertFalse(s.hasNeighbourOnTopSide(new Rectangle2D(100, 0, 100, 100)));

		s = new Screen(new Rectangle2D(100, 150, 100, 100));
		Assert.assertFalse(s.hasNeighbourOnTopSide(new Rectangle2D(0, 0, 100, 100)));
	}

	@Test
	public void testHasNeighbourDirectlyOnBottomSide() {
		Screen s;

		// Testing rectangles that are considered direct neighbours
		s = new Screen(new Rectangle2D(0, 0, 100, 100));
		Assert.assertTrue(s.hasNeighbourDirectlyOnBottomSide(new Rectangle2D(0, 100, 100, 100)));

		s = new Screen(new Rectangle2D(50, 0, 100, 100));
		Assert.assertTrue(s.hasNeighbourDirectlyOnBottomSide(new Rectangle2D(0, 100, 100, 100)));

		s = new Screen(new Rectangle2D(50, 0, 100, 100));
		Assert.assertTrue(s.hasNeighbourDirectlyOnBottomSide(new Rectangle2D(100, 100, 100, 100)));

		// Testing rectangles that are not considered direct neighbours
		s = new Screen(new Rectangle2D(0, 0, 100, 100));
		Assert.assertFalse(s.hasNeighbourDirectlyOnBottomSide(new Rectangle2D(100, 100, 100, 100)));

		s = new Screen(new Rectangle2D(100, 0, 100, 100));
		Assert.assertFalse(s.hasNeighbourDirectlyOnBottomSide(new Rectangle2D(0, 100, 100, 100)));
	}

	@Test
	public void testHasNeighbourOnBottomSide() {
		Screen s;

		// Testing rectangles that are considered direct neighbours
		s = new Screen(new Rectangle2D(0, 0, 100, 100));
		Assert.assertTrue(s.hasNeighbourOnBottomSide(new Rectangle2D(0, 150, 100, 100)));

		s = new Screen(new Rectangle2D(50, 0, 100, 100));
		Assert.assertTrue(s.hasNeighbourOnBottomSide(new Rectangle2D(0, 150, 100, 100)));

		s = new Screen(new Rectangle2D(50, 0, 100, 100));
		Assert.assertTrue(s.hasNeighbourOnBottomSide(new Rectangle2D(100, 150, 100, 100)));

		// Testing rectangles that are not considered direct neighbours
		s = new Screen(new Rectangle2D(0, 0, 100, 100));
		Assert.assertFalse(s.hasNeighbourOnBottomSide(new Rectangle2D(100, 150, 100, 100)));

		s = new Screen(new Rectangle2D(100, 0, 100, 100));
		Assert.assertFalse(s.hasNeighbourOnBottomSide(new Rectangle2D(0, 150, 100, 100)));
	}
}
