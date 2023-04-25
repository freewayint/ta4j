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
import org.ta4j.core.utils.NumCache;

/**
 * Cached {@link Indicator indicator}.
 *
 * <p>
 * Caches the calculated results of the indicator to avoid calculating the same
 * index of the indicator twice. The caching drastically speeds up access to
 * indicator values. Caching is especially recommended when indicators calculate
 * their values based on the values of other indicators. Such nested indicators
 * can call {@link #getValue(int)} multiple times without the need to
 * {@link #calculate(int)} again.
 */
public abstract class CachedIndicator extends AbstractIndicator {
	// andrewp:
	private final int minicache_size_bits = 1;
	private final int minicache_size = 1 << minicache_size_bits;
	private final int minicache_address_mask = minicache_size - 1;

	private final Num[] minicache_objects;
	private final int[] minicache_indices;
	private int minicache_write_index;

	private final NumCache full_cache;

	protected CachedIndicator(BarSeries series) {
		this(series, false);
	}

    /**
     * Constructor.
     *
     * @param series the related bar series
     */
    protected CachedIndicator(BarSeries series, boolean full_cache) {
        super(series);

		assert (series != null);

		if (full_cache) {
			minicache_objects = null;
			minicache_indices = null;

			this.full_cache = new NumCache(series.getMaximumBarCount());

		} else {
			minicache_objects = new Num[minicache_size];
			minicache_indices = new int[minicache_size];

			minicache_write_index = 1;

			for (int i = 0; i < minicache_size; ++i)
				minicache_indices[i] = Integer.MIN_VALUE;

			this.full_cache = null;
		}
    }

	protected CachedIndicator(Indicator indicator, boolean full_cache) {
		this(indicator.getBarSeries(), full_cache);
	}

    /**
     * Constructor.
     *
     * @param indicator a related indicator (with a bar series)
     */
    protected CachedIndicator(Indicator indicator) {
        this(indicator.getBarSeries(), false);
    }

    /**
     * @param index the bar index
     * @return the value of the indicator
     */
    protected abstract Num calculate(int index);

	private boolean recursionProtection;
	private boolean cache_end_index = true;

	public void setCacheMode(boolean cache_end_index) {
		this.cache_end_index = cache_end_index;
	}

    @Override
    public Num getValue(int index) {
		Num result = null;

		assert (recursionProtection == false); // andrewp: inherit your class from RecursiveCachedIndicator instead!
		recursionProtection = true;

		BarSeries series = getBarSeries();
		if (series == null || (!cache_end_index && index == series.getEndIndex())) {
			result = calculate(index);

		} else {
			if (full_cache != null) {
				result = full_cache.get(index);

			} if (minicache_size == 2) { // NOTE: unrolling the loop
				if (index == minicache_indices[0])
					result = minicache_objects[0];
				else
				if (index == minicache_indices[1])
					result = minicache_objects[1];

			} else {
				int last_write_index = (minicache_write_index - 1) & minicache_address_mask;

				if (index == minicache_indices[last_write_index]) {
					result = minicache_objects[last_write_index];

				} else {
					for (int i = 0; i < minicache_size; ++i)
						if (index == minicache_indices[i]) {
							result = minicache_objects[i];
							break;
						}
				}
			}

			if (result == null) {
				int seriesEndIndex = series.getEndIndex();
				assert (index <= seriesEndIndex);

				int seriesBeginIndex = series.getBeginIndex();
				assert (index >= seriesBeginIndex);

				result = calculate(index);

				if (full_cache != null) {
					full_cache.add(result);
					assert (full_cache.endIndex() == seriesEndIndex);

				} else {
					int write_index = minicache_write_index++ & minicache_address_mask;
					minicache_indices[write_index] = index;
					minicache_objects[write_index] = result;
				}
			}
		}

		if (log.isTraceEnabled()) {
			log.trace("{}({}): {}", this, index, result);
		}

		recursionProtection = false;

		return result;
    }
}
