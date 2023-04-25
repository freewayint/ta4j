package org.ta4j.core.utils;

import org.ta4j.core.num.Num;

public class NumCache { // andrewp
	private final Num[] data;
	private final int size;
	private final int address_mask;
	private final int capacity;
	private int written;
	private int write_index;
	private int begin_index;
	private int end_index;

	public NumCache(int size) {
		this.size = size;

		int capacity_bits = 32 - Integer.numberOfLeadingZeros(size - 1);
		capacity = 1 << capacity_bits;
		assert (size <= capacity);

		data = new Num[capacity];

		address_mask = capacity - 1;
		end_index = -1;
		begin_index = -1;
	}

	public int size() {
		return written;
	}

	public int beginIndex() {
		return begin_index;
	}

	public int endIndex() {
		return end_index;
	}

	public boolean isEmpty() {
		return written == 0;
	}

	public void add(Num object) {
		data[write_index & address_mask] = object;
		write_index++;
		if (written < size)
			written++;
		end_index++;
		begin_index = end_index + 1 - written;
	}

	public Num get(int index) {
		assert (index >= begin_index);
		assert (index <= end_index);
		return data[index & address_mask];
	}
}
