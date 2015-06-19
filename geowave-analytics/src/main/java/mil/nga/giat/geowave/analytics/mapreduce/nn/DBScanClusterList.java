package mil.nga.giat.geowave.analytics.mapreduce.nn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import mil.nga.giat.geowave.index.ByteArrayId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Geometry;

/**
 * 
 * Represents a cluster. Maintains links to other clusters through shared
 * components Maintains counts contributed by components of this cluster.
 * Supports merging with other clusters, incrementing the count by only those
 * components different from the other cluster.
 * 
 * Intended to run in a single thread. Not Thread Safe.
 * 
 */
public abstract class DBScanClusterList implements
		CompressingCluster<ClusterItem, Geometry>
{

	protected static final Logger LOGGER = LoggerFactory.getLogger(DBScanClusterList.class);

	// internal state
	private int addCount;
	private final Set<Cluster<ClusterItem>> linkedClusters = new HashSet<Cluster<ClusterItem>>();
	// maintains the count of a geometry representing a cluster of points
	private HashMap<ByteArrayId, Long> clusteredGeometryCounts = null;

	private final ByteArrayId id;

	// global state
	private final Map<ByteArrayId, Cluster<ClusterItem>> index;

	public DBScanClusterList(
			final ByteArrayId centerId,
			final Map<ByteArrayId, Cluster<ClusterItem>> index ) {
		super();

		linkedClusters.add(this);
		this.index = index;

		id = centerId;
	}

	protected static final Long ONE = 1L;
	protected static final Long ZERO = 0L;

	@Override
	public boolean add(
			final Entry<ByteArrayId, ClusterItem> entry ) {
		return this.add(
				entry.getKey(),
				entry.getValue());
	}

	private boolean checkAssignment(
			final ByteArrayId id ) {
		final Cluster<ClusterItem> cluster = index.get(id);
		if (cluster != null) {
			linkedClusters.add(cluster);
			return true;
		}
		return false;
	}

	protected abstract Long addAndFetchCount(
			final ByteArrayId id,
			final ClusterItem newInstance );

	protected boolean add(
			final ByteArrayId id,
			final ClusterItem newInstance ) {
		if (getCount(id) != 0) {
			return false;
		}

		final boolean assignedElseWhere = checkAssignment(id);

		final Long count = addAndFetchCount(
				id,
				newInstance);

		putCount(
				id,
				count,
				!assignedElseWhere);

		return true;
	}

	@Override
	public void clear() {
		addCount = 0;
		clusteredGeometryCounts = null;
	}

	@Override
	public boolean contains(
			final ByteArrayId obj ) {
		return (clusteredGeometryCounts != null) && clusteredGeometryCounts.containsKey(obj);
	}

	@Override
	public Iterator<Entry<ByteArrayId, ClusterItem>> iterator() {
		return Collections.<Entry<ByteArrayId, ClusterItem>> emptyList().iterator();

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(
			final Object obj ) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final DBScanClusterList other = (DBScanClusterList) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		}
		else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	@Override
	public int size() {
		return addCount;
	}

	@Override
	public boolean isEmpty() {
		return (clusteredGeometryCounts == null) || clusteredGeometryCounts.isEmpty();
	}

	@Override
	public Geometry get() {
		return compress();
	}

	@Override
	public Iterator<ByteArrayId> clusteredIds() {
		return clusteredGeometryCounts == null ? Collections.<ByteArrayId> emptyList().iterator() : clusteredGeometryCounts.keySet().iterator();
	}

	@Override
	public void init() {
		if (clusteredGeometryCounts != null && !clusteredGeometryCounts.isEmpty()) {
			final Iterator<Map.Entry<ByteArrayId, Long>> it = clusteredGeometryCounts.entrySet().iterator();
			while (it.hasNext()) {
				final Map.Entry<ByteArrayId, Long> count = it.next();
				if (index.containsKey(count.getKey())) {
					Cluster<ClusterItem> cluster = index.get(count.getKey());
					this.linkedClusters.add(cluster);
					it.remove();
				}
			}
		}
	}

	@Override
	public void merge(
			final Cluster<ClusterItem> cluster ) {
		if (cluster != this) {
			if (((DBScanClusterList) cluster).clusteredGeometryCounts != null) {
				for (final Map.Entry<ByteArrayId, Long> count : ((DBScanClusterList) cluster).clusteredGeometryCounts.entrySet()) {
					if (!clusteredGeometryCounts.containsKey(count.getKey())) {
						putCount(
								count.getKey(),
								count.getValue(),
								true);
					}
				}
			}
			else {
				interpolateAddCount((DBScanClusterList) cluster);
			}
		}
	}

	protected void interpolateAddCount(
			final DBScanClusterList clusterToAdd ) {
		if (clusterToAdd != this) {
			double interpolationFactor = 1.0;
			final Geometry geo1 = compress();
			final Geometry geo2 = clusterToAdd.compress();
			try {
				interpolationFactor = geo2.difference(
						geo1).getArea() / geo2.getArea();
			}
			catch (final Exception ex) {
				LOGGER.warn(
						"Cannot calculate difference of geometries to interpolate size ",
						ex);
			}
			addCount += (int) (clusterToAdd.addCount * interpolationFactor);
		}
	}

	@Override
	public ByteArrayId getId() {
		return id;
	}

	protected abstract Geometry compress();

	@Override
	public Iterator<Cluster<ClusterItem>> getLinkedClusters() {
		final List<Cluster<ClusterItem>> sortList = new ArrayList<Cluster<ClusterItem>>(
				linkedClusters);
		Collections.sort(
				sortList,
				new Comparator<Cluster<ClusterItem>>() {
					@Override
					public int compare(
							final Cluster<ClusterItem> arg0,
							final Cluster<ClusterItem> arg1 ) {
						return ((((DBScanClusterList) arg1).clusteredGeometryCounts == null) || (((DBScanClusterList) arg0).clusteredGeometryCounts == null)) ? ((DBScanClusterList) arg1).addCount - ((DBScanClusterList) arg0).addCount : ((DBScanClusterList) arg1).clusteredGeometryCounts.size() - ((DBScanClusterList) arg0).clusteredGeometryCounts.size();
					}

				});
		return sortList.iterator();
	}

	protected Long getCount(
			final ByteArrayId keyId ) {
		if ((clusteredGeometryCounts == null) || !clusteredGeometryCounts.containsKey(keyId)) {
			return ZERO;
		}
		return clusteredGeometryCounts.get(keyId);
	}

	protected void putCount(
			final ByteArrayId keyId,
			final Long value,
			final boolean updateCounts ) {
		if (updateCounts) {
			if (clusteredGeometryCounts == null) {
				clusteredGeometryCounts = new HashMap<ByteArrayId, Long>();
			}
			clusteredGeometryCounts.put(
					keyId,
					value);
		}

		addCount += value;
	}
}
