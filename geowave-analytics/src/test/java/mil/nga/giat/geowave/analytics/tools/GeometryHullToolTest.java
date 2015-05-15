package mil.nga.giat.geowave.analytics.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import mil.nga.giat.geowave.analytics.distance.CoordinateCircleDistanceFn;
import mil.nga.giat.geowave.analytics.tools.GeometryHullTool;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.algorithm.ConvexHull;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;

public class GeometryHullToolTest
{

	protected static final Logger LOGGER = LoggerFactory.getLogger(GeometryHullToolTest.class);

	GeometryFactory factory = new GeometryFactory();

	@Test
	public void testDistance() {
		double distance1 = GeometryHullTool.calcDistance(
				new Coordinate(
						3,
						3),
				new Coordinate(
						6,
						6),
				new Coordinate(
						5,
						5.5));

		double distance2 = GeometryHullTool.calcDistance(
				new Coordinate(
						3,
						3),
				new Coordinate(
						6,
						6),
				new Coordinate(
						5,
						4.5));

		assertEquals(
				distance1,
				distance2,
				0.0001);

		double distance3 = GeometryHullTool.calcDistance(
				new Coordinate(
						4,
						6),
				new Coordinate(
						6,
						12),
				new Coordinate(
						5,
						8));

		assertTrue(distance3 > 0);

		double distance4 = GeometryHullTool.calcDistance(
				new Coordinate(
						4,
						6),
				new Coordinate(
						6,
						12),
				new Coordinate(
						5,
						9));

		assertEquals(
				0.0,
				distance4,
				0.001);

		double distance5 = GeometryHullTool.calcDistance(
				new Coordinate(
						5,
						7),
				new Coordinate(
						11,
						3),
				new Coordinate(
						6,
						10));

		// assertTrue(distance5 < 0);

		double distance6 = GeometryHullTool.calcDistance(
				new Coordinate(
						5,
						7),
				new Coordinate(
						11,
						3),
				new Coordinate(
						7,
						6.5));

		double distance7 = GeometryHullTool.calcDistance(
				new Coordinate(
						5,
						7),
				new Coordinate(
						11,
						3),
				new Coordinate(
						7,
						5.0));

		assertTrue(distance7 < distance6);

	}

	@Test
	public void testAngles() {
		assertTrue(GeometryHullTool.calcAngle(
				new Coordinate(
						39,
						41.5),
				new Coordinate(
						41,
						41),
				new Coordinate(
						38,
						41.2)) > 0);

		assertTrue(GeometryHullTool.calcAngle(
				new Coordinate(
						39,
						41.5),
				new Coordinate(
						41,
						41),
				new Coordinate(
						38,
						43)) < 0);

		assertTrue(GeometryHullTool.calcAngle(
				new Coordinate(
						39,
						41.5),
				new Coordinate(
						41,
						41),
				new Coordinate(
						38,
						41.2)) < GeometryHullTool.calcAngle(
				new Coordinate(
						39,
						41.5),
				new Coordinate(
						41,
						41),
				new Coordinate(
						38,
						41.1)));

		assertTrue(GeometryHullTool.calcAngle(
				new Coordinate(
						39,
						41.5),
				new Coordinate(
						41,
						41),
				new Coordinate(
						38,
						43)) > GeometryHullTool.calcAngle(
				new Coordinate(
						39,
						41.5),
				new Coordinate(
						41,
						41),
				new Coordinate(
						38,
						44)));

		assertTrue(GeometryHullTool.calcAngle(
				new Coordinate(
						42,
						42),
				new Coordinate(
						41,
						41),
				new Coordinate(
						42.5,
						44)) > 0);

		assertTrue(GeometryHullTool.calcAngle(
				new Coordinate(
						42,
						42),
				new Coordinate(
						41,
						41),
				new Coordinate(
						42.5,
						40.5)) < 0);

		assertEquals(
				-90.0,
				GeometryHullTool.calcAngle(
						new Coordinate(
								41,
								42),
						new Coordinate(
								41,
								41),
						new Coordinate(
								42,
								41)),
				0.001);

		assertEquals(
				90.0,
				GeometryHullTool.calcAngle(
						new Coordinate(
								42,
								41),
						new Coordinate(
								41,
								41),
						new Coordinate(
								41,
								42)),
				0.001);

		assertEquals(
				-180,
				GeometryHullTool.calcAngle(
						new Coordinate(
								42,
								42),
						new Coordinate(
								41,
								41),
						new Coordinate(
								40,
								40)),
				0.001);

		assertEquals(
				0,
				GeometryHullTool.calcAngle(
						new Coordinate(
								42,
								42),
						new Coordinate(
								41,
								41),
						new Coordinate(
								42,
								42)),
				0.001);

		assertEquals(
				-315,
				GeometryHullTool.calcAngle(
						new Coordinate(
								41,
								41),
						new Coordinate(
								42,
								41),
						new Coordinate(
								41,
								40)),
				0.001);

		assertEquals(
				-45,
				GeometryHullTool.calcAngle(
						new Coordinate(
								42,
								41),
						new Coordinate(
								41,
								41),
						new Coordinate(
								42,
								40)),
				0.001);

		assertEquals(
				-45,
				GeometryHullTool.calcAngle(
						new Coordinate(
								41,
								42),
						new Coordinate(
								41,
								41),
						new Coordinate(
								42,
								42)),
				0.001);

	}

	@Test
	public void testConcaveHullBulkTest() {
		for (int i = 0; i < 1000; i++) {
			assertTrue(getHull(
					factory.createLineString(new Coordinate[] {
						new Coordinate(
								41.2,
								40.8),
						new Coordinate(
								40.8,
								40.6)
					}),
					"1",
					true).isSimple());
		}

	}

	final Coordinate[] poly1 = new Coordinate[] {
		new Coordinate(
				40,
				40),
		new Coordinate(
				40.1,
				40.1),
		new Coordinate(
				39.2,
				41.2), // selected top (2)
		new Coordinate(
				39,
				40.7),
		new Coordinate(
				38.7,
				40.1),
		new Coordinate(
				38.4,
				39.5),
		new Coordinate(
				// selected bottom (6)
				39.3,
				39.2),
		new Coordinate(
				40,
				40)
	};

	final Coordinate[] poly2 = new Coordinate[] {
		new Coordinate(
				40.2,
				40),
		new Coordinate(
				40.5,
				41), // selected top (1)
		new Coordinate(
				41.2,
				40.8),
		new Coordinate(
				40.8,
				40.6),
		new Coordinate(
				40.6,
				39.6),
		new Coordinate(
				40.3,
				39.8), // selected bottom(5)
		new Coordinate(
				40.2,
				40)
	};

	@Test
	public void testLRPolygons() {
		Geometry leftShape = factory.createPolygon(poly1);
		Geometry rightShape = factory.createPolygon(poly2);
		assertTrue(GeometryHullTool.clockwise(leftShape.getCoordinates()));
		assertFalse(GeometryHullTool.clockwise(rightShape.getCoordinates()));
		GeometryHullTool cg = new GeometryHullTool();
		cg.setDistanceFnForCoordinate(new CoordinateCircleDistanceFn());
		Geometry geo = cg.connect(
				leftShape,
				rightShape);
		assertEquals(
				"POLYGON ((39.2 41.2, 39 40.7, 38.7 40.1, 38.4 39.5, 39.3 39.2, 40.6 39.6, 40.8 40.6, 41.2 40.8, 40.5 41, 39.2 41.2))",
				geo.toString());

	}

	@Test
	public void testRLPolygons() {
		Geometry leftShape = factory.createPolygon(poly2);

		Geometry rightShape = factory.createPolygon(poly1);

		assertFalse(GeometryHullTool.clockwise(leftShape.getCoordinates()));
		assertTrue(GeometryHullTool.clockwise(rightShape.getCoordinates()));
		GeometryHullTool cg = new GeometryHullTool();
		cg.setDistanceFnForCoordinate(new CoordinateCircleDistanceFn());
		Geometry geo = cg.connect(
				leftShape,
				rightShape);
		assertEquals(
				"POLYGON ((39.2 41.2, 39 40.7, 38.7 40.1, 38.4 39.5, 39.3 39.2, 40.6 39.6, 40.8 40.6, 41.2 40.8, 40.5 41, 39.2 41.2))",
				geo.toString());
	}

	private Coordinate[] reversed(
			Coordinate[] poly ) {
		Coordinate polyReversed[] = new Coordinate[poly.length];
		for (int i = 0; i < poly.length; i++) {
			polyReversed[i] = poly[poly.length - i - 1];
		}
		return polyReversed;
	}

	@Test
	public void interesectEdges() {
		GeometryHullTool.Edge e1 = new GeometryHullTool.Edge(
				new Coordinate(
						20.0,
						20.0),
				new Coordinate(
						21.5,
						21),
				0);
		GeometryHullTool.Edge e2 = new GeometryHullTool.Edge(
				new Coordinate(
						20.4,
						19.0),
				new Coordinate(
						21.0,
						22),
				0);
		assertTrue(GeometryHullTool.edgesIntersect(
				e1,
				e2));
		GeometryHullTool.Edge e3 = new GeometryHullTool.Edge(
				new Coordinate(
						20.4,
						19.0),
				new Coordinate(
						21.0,
						19.5),
				0);
		assertTrue(!GeometryHullTool.edgesIntersect(
				e1,
				e3));
	}

	@Test
	public void testRLSamePolygons() {

		Geometry leftShape = factory.createPolygon(reversed(poly1));
		Geometry rightShape = factory.createPolygon(reversed(poly2));

		assertFalse(GeometryHullTool.clockwise(leftShape.getCoordinates()));
		assertTrue(GeometryHullTool.clockwise(rightShape.getCoordinates()));
		GeometryHullTool cg = new GeometryHullTool();
		cg.setDistanceFnForCoordinate(new CoordinateCircleDistanceFn());
		Geometry geo = cg.connect(
				leftShape,
				rightShape);
		assertEquals(
				"POLYGON ((39.2 41.2, 39 40.7, 38.7 40.1, 38.4 39.5, 39.3 39.2, 40.6 39.6, 40.8 40.6, 41.2 40.8, 40.5 41, 39.2 41.2))",
				geo.toString());
	}

	@Test
	public void testPolygonConnection() {

		boolean save = true;
		final Geometry concave1 = getHull(
				factory.createLineString(new Coordinate[] {
					new Coordinate(
							41.2,
							40.8),
					new Coordinate(
							40.8,
							40.6)
				}),
				"1",
				save);
		final Geometry concave2 = getHull(
				factory.createLineString(new Coordinate[] {
					new Coordinate(
							39.9,
							40.6),
					new Coordinate(
							40.8,
							40.6)
				}),
				"2",
				save);
		final Geometry concave3 = getHull(
				factory.createLineString(new Coordinate[] {
					new Coordinate(
							42.0,
							42.0),
					new Coordinate(
							41.2,
							40.8)
				}),
				"3",
				save);

		final Geometry hull = concave1.union(
				concave2).union(
				concave3);

		assertTrue(hull.isSimple());

		writeToShapeFile(
				"finalhull",
				hull);

		coversPoints(
				hull,
				concave1);
		coversPoints(
				hull,
				concave2);
		coversPoints(
				hull,
				concave3);
	}

	private Geometry getHull(
			LineString str,
			String name,
			boolean save ) {

		List<Point> points = CurvedDensityDataGeneratorTool.generatePoints(
				str,
				0.4,
				1000);

		GeometryHullTool cg = new GeometryHullTool();
		cg.setDistanceFnForCoordinate(new CoordinateCircleDistanceFn());

		final Coordinate[] coordinates = new Coordinate[points.size()];
		int i = 0;
		for (Point point : points) {
			coordinates[i++] = point.getCoordinate();
		}

		final ConvexHull convexHull = new ConvexHull(
				coordinates,
				factory);

		final Geometry concaveHull = cg.concaveHull(
				convexHull.getConvexHull(),
				Arrays.asList(coordinates));
		if (save || !concaveHull.isSimple()) {
			writeToShapeFile(
					"set_" + name,
					points.toArray(new Geometry[points.size()]));
			writeToShapeFile(
					"chull_" + name,
					concaveHull);
			writeToShapeFile(
					"hull_" + name,
					convexHull.getConvexHull());
		}

		return concaveHull;
	}

	private static void writeToShapeFile(
			String name,
			Geometry... geos ) {
		if (true) { // LOGGER.isDebugEnabled()) {
			try {
				ShapefileTool.writeShape(
						name,
						new File(
								"./target/test_" + name),
						geos);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private static boolean coversPoints(
			Geometry coverer,
			Geometry pointsToCover ) {
		for (Coordinate coordinate : pointsToCover.getCoordinates()) {
			if (!coverer.covers(coverer.getFactory().createPoint(
					coordinate))) return false;
		}
		return true;
	}

}
