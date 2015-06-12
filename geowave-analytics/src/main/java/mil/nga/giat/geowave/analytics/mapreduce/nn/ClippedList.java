package mil.nga.giat.geowave.analytics.mapreduce.nn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import mil.nga.giat.geowave.index.ByteArrayId;

/**
 * 
 * Maintains a list of entries, clipping entries after reaching a maximum size;
 * Ideally, middle entries are clipped at Random. However, due to performance
 * considerations, the last entry is clipped.
 * 
 * The list maintains the total number of additions (minus removals) since the
 * list size is reflective of the clipped components.
 * 
 * 
 * @param <VALUEIN>
 */
public class ClippedList<VALUEIN> implements
		List<Map.Entry<ByteArrayId, VALUEIN>>
{
	private final List<Map.Entry<ByteArrayId, VALUEIN>> list = new ArrayList<Map.Entry<ByteArrayId, VALUEIN>>();
	private int addCount;
	private final int maxSize;

	protected ClippedList(
			final int maxSize ) {
		super();
		this.maxSize = maxSize;
	}

	@Override
	public boolean add(
			final Entry<ByteArrayId, VALUEIN> entry ) {
		if (list.size() == maxSize) {
			list.remove(maxSize - 1);
		}
		addCount++;
		return list.add(entry);
	}

	@Override
	public void add(
			final int position,
			final Entry<ByteArrayId, VALUEIN> entry ) {
		if (list.size() == maxSize) {
			list.remove(maxSize - 1);
		}
		addCount++;
		list.add(
				position,
				entry);
	}

	public int getMaxSize() {
		return maxSize;
	}

	@Override
	public boolean addAll(
			final Collection<? extends Entry<ByteArrayId, VALUEIN>> collection ) {
		int listSize = list.size();
		final int amountToRemove = (listSize + collection.size()) - maxSize;
		if (amountToRemove > 0) {
			for (int i = 0; i < amountToRemove; i++) {
				list.remove(--listSize);
			}
		}
		final boolean success = list.addAll(collection);
		if (success) {
			if (collection instanceof ClippedList) {
				this.addCount += ((ClippedList) collection).addCount();
			}
			else {
				this.addCount += collection.size();
			}
		}
		return success;
	}

	@Override
	public boolean addAll(
			final int position,
			final Collection<? extends Entry<ByteArrayId, VALUEIN>> collection ) {
		throw new java.lang.UnsupportedOperationException(
				"ClippedList.addAll(int,Collection<? extends Entry<ByteArrayId, VALUEIN>>)");
	}

	@Override
	public void clear() {
		addCount = 0;
		list.clear();
	}

	@Override
	public boolean contains(
			final Object obj ) {
		return this.list.contains(obj);
	}

	@Override
	public boolean containsAll(
			final Collection<?> arg0 ) {
		throw new java.lang.UnsupportedOperationException(
				"ClippedList.containsAll(Collection<?> arg0)");
	}

	@Override
	public Entry<ByteArrayId, VALUEIN> get(
			final int position ) {
		return list.get(position);
	}

	@Override
	public int indexOf(
			final Object position ) {
		return list.indexOf(position);
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public Iterator<Entry<ByteArrayId, VALUEIN>> iterator() {
		return list.iterator();
	}

	@Override
	public int lastIndexOf(
			final Object obj ) {
		return list.lastIndexOf(obj);
	}

	@Override
	public ListIterator<Entry<ByteArrayId, VALUEIN>> listIterator() {
		return list.listIterator();
	}

	@Override
	public ListIterator<Entry<ByteArrayId, VALUEIN>> listIterator(
			final int position ) {
		return list.listIterator(position);
	}

	@Override
	public boolean remove(
			final Object obj ) {
		final boolean success = list.remove(obj);
		if (success) {
			addCount--;
		}
		return success;
	}

	@Override
	public Entry<ByteArrayId, VALUEIN> remove(
			final int position ) {
		final Entry<ByteArrayId, VALUEIN> obj = list.remove(position);
		if (obj != null) {
			addCount--;
		}
		return obj;
	}

	@Override
	public boolean removeAll(
			final Collection<?> arg0 ) {
		throw new java.lang.UnsupportedOperationException(
				"ClippedList.removeAll(Collection<?> arg0)");
	}

	@Override
	public boolean retainAll(
			final Collection<?> arg0 ) {
		throw new java.lang.UnsupportedOperationException(
				"ClippedList.retainAll(Collection<?> arg0)");
	}

	@Override
	public Entry<ByteArrayId, VALUEIN> set(
			final int arg0,
			final Entry<ByteArrayId, VALUEIN> obj ) {
		return list.set(
				0,
				obj);
	}

	protected int addCount() {
		return addCount;
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public List<Entry<ByteArrayId, VALUEIN>> subList(
			final int positionOne,
			final int length ) {
		return list.subList(
				positionOne,
				length);
	}

	@Override
	public Object[] toArray() {
		return list.toArray();
	}

	@Override
	public <T> T[] toArray(
			final T[] array ) {
		return list.toArray(array);
	}
}
