package mil.nga.giat.geowave.examples.ingest.bulk;

import org.geotools.feature.AttributeTypeBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Provides a static method to obtain an instance of a SimpleFeatureType for
 * Geonames data
 */
public class GeonamesSimpleFeatureType
{

	private static final String FEATURE_NAME = "GeonamesPoint";
	private static SimpleFeatureType simpleFeatureType;

	private GeonamesSimpleFeatureType() {
		// prevent instantiation
	}

	public static SimpleFeatureType getInstance() {
		if (simpleFeatureType == null) {
			simpleFeatureType = createGeonamesPointType();
		}
		return simpleFeatureType;
	}

	private static SimpleFeatureType createGeonamesPointType() {

		final SimpleFeatureTypeBuilder sftBuilder = new SimpleFeatureTypeBuilder();
		final AttributeTypeBuilder atBuilder = new AttributeTypeBuilder();

		sftBuilder.setName(FEATURE_NAME);

		sftBuilder.add(atBuilder.binding(
				Geometry.class).nillable(
				false).buildDescriptor(
				"geometry"));
		sftBuilder.add(atBuilder.binding(
				Double.class).nillable(
				false).buildDescriptor(
				"Latitude"));
		sftBuilder.add(atBuilder.binding(
				Double.class).nillable(
				false).buildDescriptor(
				"Longitude"));
		sftBuilder.add(atBuilder.binding(
				String.class).nillable(
				false).buildDescriptor(
				"Location"));

		return sftBuilder.buildFeatureType();
	}

}
