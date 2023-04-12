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
public abstract class CachedIndicator<T> extends AbstractIndicator<T> {
	private T last_cached_value;
	private int last_cached_index = Integer.MIN_VALUE;

    /**
     * Constructor.
     *
     * @param series the related bar series
     */
    protected CachedIndicator(BarSeries series) {
        super(series);
    }

    /**
     * Constructor.
     *
     * @param indicator a related indicator (with a bar series)
     */
    protected CachedIndicator(Indicator<?> indicator) {
        this(indicator.getBarSeries());
    }

    /**
     * @param index the bar index
     * @return the value of the indicator
     */
    protected abstract T calculate(int index);

	private boolean recursionProtection;

    @Override
    public T getValue(int index) {
		T result;

		assert (recursionProtection == false); // andrewp: inherit your class from RecursiveCachedIndicator instead!
		recursionProtection = true;

		if (last_cached_index == index) {
			result = last_cached_value;

		} else {
			BarSeries series = getBarSeries();
			if (series == null) {
				result = calculate(index);

			} else {
				int seriesEndIndex = series.getEndIndex();
				assert (index <= seriesEndIndex);

				int seriesBeginIndex = series.getBeginIndex();
				assert (index >= seriesBeginIndex);

				result = calculate(index);
			}

			last_cached_value = result;
			last_cached_index = index;
		}

		if (log.isTraceEnabled()) {
			log.trace("{}({}): {}", this, index, result);
		}

		recursionProtection = false;

		return result;
    }
}
