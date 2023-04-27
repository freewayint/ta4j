package org.ta4j.core.utils;

public class RollingWindowObjectCache {
	private final Object[] data;
	private final int size;
	private final int address_mask;
	private final int capacity;
	private int begin_index;
	private int end_index;

	public RollingWindowObjectCache(int size) {
		this.size = size;

		int capacity_bits = 32 - Integer.numberOfLeadingZeros(size - 1);
		capacity = 1 << capacity_bits;
		assert (size <= capacity);

		data = new Object[capacity];

		address_mask = capacity - 1;
		end_index = -1;
		begin_index = -1;
	}

	public int beginIndex() {
		return begin_index;
	}

	public int endIndex() {
		return end_index;
	}

	public void set(int index, Object object) {
		if (index >= begin_index && index <= end_index) {
			data[index & address_mask] = object;

		} else if (index > end_index) {
			end_index = index;
			begin_index = end_index + 1 - size;
			data[index & address_mask] = object;

		} else {
			assert (index >= begin_index);
		}
	}

	public boolean hasIndex(int index) {
		return index >= begin_index && index <= end_index;
	}

	public Object get(int index) {
		assert (index >= begin_index);
		assert (index <= end_index);
		return data[index & address_mask];
	}
}
