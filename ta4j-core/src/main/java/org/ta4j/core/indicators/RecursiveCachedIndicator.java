/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2022 Ta4j Organization & respective
 * authors (see AUTHORS)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.ta4j.core.indicators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.num.Num;

/**
 * Recursive cached {@link Indicator indicator}.
 *
 * Recursive indicators should extend this class.<br>
 * This class is only here to avoid (OK, to postpone) the StackOverflowError
 * that may be thrown on the first getValue(int) call of a recursive indicator.
 * Concretely when an index value is asked, if the last cached value is too
 * old/far, the computation of all the values between the last cached and the
 * asked one is executed iteratively.
 */
public abstract class RecursiveCachedIndicator<T extends Num> extends AbstractIndicator<T> {
	// andrewp:
	private class Cache {
		private final Num[] data;
		private final int len;
		private final int address_mask;
		private final int capacity;
		private int written;
		private int write_index;
		private int begin_index;
		private int end_index;

		Cache(int len) {
			this.len = len;

			int capacity_bits = 32 - Integer.numberOfLeadingZeros(len - 1);
			capacity = 1 << capacity_bits;
			assert (len <= capacity);

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
			if (written < len)
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

	/**
     * List of cached cache.
     */
    private final Cache cache;

    /**
     * Constructor.
     *
     * @param series the related bar series
     */
    protected RecursiveCachedIndicator(BarSeries series) {
        super(series);
		assert (series != null);

		cache = new Cache(series.getMaximumBarCount());
    }

    /**
     * Constructor.
     *
     * @param indicator a related indicator (with a bar series)
     */
    protected RecursiveCachedIndicator(Indicator<?> indicator) {
        this(indicator.getBarSeries());
    }

	/**
     * @param index the bar index
     * @return the value of the indicator
     */
    protected abstract T calculate(int index);

    @Override
    public T getValue(int index) {
		BarSeries series = getBarSeries();

		assert (index >= series.getBeginIndex());
		assert (index <= series.getEndIndex());

		Num result;

		if (index >= cache.beginIndex() && index <= cache.endIndex()) {
			result = cache.get(index);

		} else {
			int startIndex = Math.max(series.getBeginIndex(), cache.endIndex() + 1);
			for (int i = startIndex; i < index; ++i) {
				Num value = calculate(i);
				assert (value != null);

				cache.add(value);
			}

			result = calculate(index);
			assert (result != null);

			if (index != series.getEndIndex()) // andrewp: do not cache the last candle result
				cache.add(result);
		}

		if (log.isTraceEnabled()) {
			log.trace("{}({}): {}", this, index, result);
		}

		return (T) result;
    }
}
