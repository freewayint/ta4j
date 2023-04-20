package org.ta4j.core.indicators.helpers;

import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.num.Num;

public class ExtremaValueIndicator extends CachedIndicator<Num> { // andrewp
	private final Indicator<Num> indicator;

	private final boolean high;
    protected final int barCount;

	private int cached_zone_end_index;
	private Num cached_extrema_value;
	private int cached_extrema_index;

    public ExtremaValueIndicator(Indicator<Num> indicator, int barCount, boolean high) {
        super(indicator);
		assert (barCount > 0);
        this.indicator = indicator;
        this.barCount = barCount;
		invalidateCachedExtrema();
		this.high = high;
    }

	private void invalidateCachedExtrema() {
		cached_extrema_value = null;
		cached_extrema_index = Integer.MAX_VALUE;
		cached_zone_end_index = Integer.MIN_VALUE;
	}

    @Override
    protected Num calculate(int index) {
		final int series_begin_index = getBarSeries().getBeginIndex();
		final int series_end_index = getBarSeries().getEndIndex();

		if (index == series_end_index) {
			// NOTE: andrewp: use cached extrema value for the most popular case:
			// |            search_begin_index  -  index
			// |                    |                |
			// | 5 [ cached zone  3 1 5 9 3 0 ] 5 10 1
			// |                        |
			// |               cached_zone_end_index

			int search_begin_index = Math.max(series_begin_index, series_end_index - barCount + 1);

			if (cached_extrema_index < search_begin_index)
				invalidateCachedExtrema();

			int uncached_begin_index = Math.max(cached_zone_end_index + 1, search_begin_index);

			Num uncached_extrema = indicator.getValue(series_end_index);
			int uncached_extrema_index = series_end_index;
			for (int i = series_end_index - 1; i >= uncached_begin_index; --i) {
				Num value = indicator.getValue(i);
				if (high ? uncached_extrema.isLessThan(value) : uncached_extrema.isGreaterThan(value)) {
					uncached_extrema = value;
					uncached_extrema_index = i;
				}
			}

			cached_zone_end_index = series_end_index;

			if (cached_extrema_value == null || (high ? cached_extrema_value.isLessThan(uncached_extrema) : cached_extrema_value.isGreaterThan(uncached_extrema))) {
				cached_extrema_value = uncached_extrema;
				cached_extrema_index = uncached_extrema_index;
			}

			return cached_extrema_value;

		} else {
			int begin = Math.max(series_begin_index, index - barCount + 1);

			if (high) {
				Num highest = indicator.getValue(index);
				for (int i = index - 1; i >= begin; i--) {
					Num value = indicator.getValue(i);
					if (highest.isLessThan(value))
						highest = value;
				}
				return highest;

			} else {
				Num lowest = indicator.getValue(index);
				for (int i = index - 1; i >= begin; i--) {
					Num value = indicator.getValue(i);
					if (lowest.isGreaterThan(value))
						lowest = value;
				}
				return lowest;
			}
		}
    }
}
