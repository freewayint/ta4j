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
package org.ta4j.core;

import static org.ta4j.core.num.NaN.NaN;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;

/**
 * Base implementation of a {@link BarSeries}.
 * </p>
 */
public class BaseBarSeries implements BarSeries {
    private static final long serialVersionUID = -1878027009398790126L;

	private class BarCircularBuffer { // andrewp
		private final Bar[] data;
		private final int len;
		private final int address_mask;
		private final int capacity;
		private int written;
		private int write_index;

		BarCircularBuffer(int len) {
			this.len = len;

			int capacity_bits = 32 - Integer.numberOfLeadingZeros(len - 1);
			capacity = 1 << capacity_bits;
			assert (len <= capacity);

			data = new Bar[capacity];

			address_mask = capacity - 1;
		}

		public int size() {
			return written;
		}

		public boolean isEmpty() {
			return written == 0;
		}

		public void add(Bar object) {
			data[write_index & address_mask] = object;
			write_index++;
			if (written < len)
				written++;
		}

		public void set(int index, Bar object) {
			int base_index = write_index - written;
			assert (index >= base_index);
			assert (index < base_index + written);
			data[index & address_mask] = object;
		}

		public Bar get(int index) {
			int base_index = write_index - written;
			assert (index >= base_index);
			assert (index < base_index + written);
			return data[index & address_mask];
		}
	}

    /**
     * The logger
     */
    private final transient Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Name for unnamed series
     */
    private static final String UNNAMED_SERIES_NAME = "unnamed_series";

    /**
     * Any instance of Num to determine its Num type.
     */
    protected final transient Num num;

    /**
     * Name of the series
     */
    private final String name;
    /**
     * List of bars
     */
    private final BarCircularBuffer bars;
    /**
     * Begin index of the bar series
     */
    private int seriesBeginIndex;
    /**
     * End index of the bar series
     */
    private int seriesEndIndex;
    /**
     * Maximum number of bars for the bar series
     */
    private final int maximumBarCount;

    /**
     * Constructor.
     *
     * @param name             the name of the series
     * @param maximumBarCount  capacity
     * @param seriesBeginIndex the begin index (inclusive) of the bar series
     * @param seriesEndIndex   the end index (inclusive) of the bar series
     * @param num              any instance of Num to determine its Num function;
     *                         with this, we can convert a {@link Number} to a
     *                         {@link Num Num implementation}
     */
    public BaseBarSeries(String name, int maximumBarCount, int seriesBeginIndex, int seriesEndIndex, Num num) {
        this.name = name;
		this.maximumBarCount = maximumBarCount;
		this.seriesBeginIndex = -1;
		this.seriesEndIndex = -1;
		this.num = num;

		bars = new BarCircularBuffer(maximumBarCount);
    }

    @Override
    public Num num() {
        return num;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Bar getBar(int i) {
		assert (i >= seriesBeginIndex); // andrewp: never mitigate access to removed bars
		assert (i <= seriesEndIndex);

        return bars.get(i);
    }

	@Override
	public List<Bar> getBarData() { // andrewp
		List<Bar> list = new ArrayList<>(bars.size());
		for (int i = seriesBeginIndex; i <= seriesEndIndex; ++i)
			list.add(bars.get(i));
		return list;
	}

    @Override
    public int getBarCount() {
        if (seriesEndIndex < 0) {
            return 0;
        }
        return bars.size();
    }

    @Override
    public int getBeginIndex() {
        return seriesBeginIndex;
    }

    @Override
    public int getEndIndex() {
        return seriesEndIndex;
    }

    @Override
    public int getMaximumBarCount() {
        return maximumBarCount;
    }

    /**
     * @param bar the <code>Bar</code> to be added
     * @apiNote to add bar data directly use #addBar(Duration, ZonedDateTime, Num,
     *          Num, Num, Num, Num)
     * @throws NullPointerException if bar is null
     */
    @Override
    public void addBar(Bar bar, boolean replace) {
        Objects.requireNonNull(bar, "bar must not be null");
        if (!bars.isEmpty()) {
            if (replace) {
				if (bars.size() >= 2) { // andrewp
					ZonedDateTime seriesEndTime = bars.get(seriesEndIndex - 1).getEndTime();
					if (!bar.getEndTime().isAfter(seriesEndTime)) {
						throw new IllegalArgumentException(
								String.format("Cannot replace a bar with end time:%s that is <= to series end time: %s",
										bar.getEndTime(), seriesEndTime));
					}
				}
                bars.set(seriesEndIndex, bar);
                return;
            }
            ZonedDateTime seriesEndTime = bars.get(seriesEndIndex).getEndTime();
            if (!bar.getEndTime().isAfter(seriesEndTime)) {
                throw new IllegalArgumentException(
                        String.format("Cannot add a bar with end time:%s that is <= to series end time: %s",
                                bar.getEndTime(), seriesEndTime));
            }
        }

        bars.add(bar);

        seriesEndIndex++;
		seriesBeginIndex = seriesEndIndex + 1 - bars.size();
    }

    @Override
    public void addBar(Duration timePeriod, ZonedDateTime endTime) {
        this.addBar(new BaseBar(timePeriod, endTime, function()));
    }

    @Override
    public void addBar(ZonedDateTime endTime, Num openPrice, Num highPrice, Num lowPrice, Num closePrice, Num volume) {
        this.addBar(
                new BaseBar(Duration.ofDays(1), endTime, openPrice, highPrice, lowPrice, closePrice, volume, zero()));
    }

    @Override
    public void addBar(ZonedDateTime endTime, Num openPrice, Num highPrice, Num lowPrice, Num closePrice, Num volume,
            Num amount) {
        this.addBar(
                new BaseBar(Duration.ofDays(1), endTime, openPrice, highPrice, lowPrice, closePrice, volume, amount));
    }

    @Override
    public void addBar(Duration timePeriod, ZonedDateTime endTime, Num openPrice, Num highPrice, Num lowPrice,
            Num closePrice, Num volume) {
        this.addBar(new BaseBar(timePeriod, endTime, openPrice, highPrice, lowPrice, closePrice, volume, zero()));
    }

    @Override
    public void addBar(Duration timePeriod, ZonedDateTime endTime, Num openPrice, Num highPrice, Num lowPrice,
            Num closePrice, Num volume, Num amount) {
        this.addBar(new BaseBar(timePeriod, endTime, openPrice, highPrice, lowPrice, closePrice, volume, amount));
    }

    @Override
    public void addTrade(Number price, Number amount) {
        addTrade(numOf(price), numOf(amount));
    }

    @Override
    public void addTrade(String price, String amount) {
        addTrade(numOf(new BigDecimal(price)), numOf(new BigDecimal(amount)));
    }

    @Override
    public void addTrade(Num tradeVolume, Num tradePrice) {
        getLastBar().addTrade(tradeVolume, tradePrice);
    }

    @Override
    public void addPrice(Num price) {
        getLastBar().addPrice(price);
    }
}
