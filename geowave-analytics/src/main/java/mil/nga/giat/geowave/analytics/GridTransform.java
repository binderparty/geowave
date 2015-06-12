/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008-2011 TOPP - www.openplans.org.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package mil.nga.giat.geowave.analytics;

import com.vividsolutions.jts.geom.Envelope;

/**
 * An affine transformation between two parallel coordinate systems, one defined
 * by an {@link Envelope} and one defined by a discrete zero-based grid
 * representing the same area as the envelope. The transformation incorporates
 * an isotropic scaling and a translation.
 * 
 * @author Martin Davis - OpenGeo
 * 
 */
class GridTransform
{

	private final Envelope env;

	private final int xSize;

	private final int ySize;

	private final double dx;

	private final double dy;

	/**
	 * Creates a new transform.
	 * 
	 * @param env
	 *            the envelope defining one coordinate system
	 * @param xSize
	 *            the number of cells along the X axis of the grid
	 * @param ySize
	 *            the number of cells along the Y axis of the grid
	 */
	public GridTransform(
			final Envelope env,
			final int xSize,
			final int ySize ) {
		this.env = env;
		this.xSize = xSize;
		this.ySize = ySize;
		dx = env.getWidth() / (xSize - 1);
		dy = env.getHeight() / (ySize - 1);
	}

	/**
	 * Computes the X ordinate of the i'th grid column.
	 * 
	 * @param i
	 *            the index of a grid column
	 * @return the X ordinate of the column
	 */
	public double x(
			final int i ) {
		if (i >= (xSize - 1)) {
			return env.getMaxX();
		}
		return env.getMinX() + (i * dx);
	}

	/**
	 * Computes the Y ordinate of the i'th grid row.
	 * 
	 * @param j
	 *            the index of a grid row
	 * @return the Y ordinate of the row
	 */
	public double y(
			final int j ) {
		if (j >= (ySize - 1)) {
			return env.getMaxY();
		}
		return env.getMinY() + (j * dy);
	}

	/**
	 * Computes the column index of an X ordinate.
	 * 
	 * @param x
	 *            the X ordinate
	 * @return the column index
	 */
	public int i(
			final double x ) {
		if (x > env.getMaxX()) {
			return xSize;
		}
		if (x < env.getMinX()) {
			return -1;
		}
		int i = (int) ((x - env.getMinX()) / dx);
		// have already check x is in bounds, so ensure returning a valid value
		if (i >= xSize) {
			i = xSize - 1;
		}
		return i;
	}

	/**
	 * Computes the column index of an Y ordinate.
	 * 
	 * @param y
	 *            the Y ordinate
	 * @return the column index
	 */
	public int j(
			final double y ) {
		if (y > env.getMaxY()) {
			return ySize;
		}
		if (y < env.getMinY()) {
			return -1;
		}
		int j = (int) ((y - env.getMinY()) / dy);
		// have already check x is in bounds, so ensure returning a valid value
		if (j >= ySize) {
			j = ySize - 1;
		}
		return j;
	}

}