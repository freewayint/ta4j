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
package org.ta4j.core.utils;

import java.time.Duration;
import org.ta4j.core.BarDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.aggregator.BarAggregator;
import org.ta4j.core.aggregator.BarSeriesAggregator;
import org.ta4j.core.aggregator.BaseBarSeriesAggregator;
import org.ta4j.core.num.Num;

/**
 * Common utilities and helper methods for BarSeries.
 */
public final class BarSeriesUtils {

    /**
     * Sorts the Bars by {@link Bar#getEndTime()} in ascending sequence (lower
     * values before higher values).
     */
    public static final Comparator<Bar> sortBarsByTime = (b1, b2) -> b1.getEndTime().isAfter(b2.getEndTime()) ? 1 : -1;

    private BarSeriesUtils() {
    }

    /**
     * Adds <code>newBars</code> to <code>barSeries</code>.
     *
     * @param barSeries the BarSeries
     * @param newBars   the new bars to be added
     */
    public static void addBars(BarSeries barSeries, List<Bar> newBars) {
        if (newBars != null && !newBars.isEmpty()) {
            sortBars(newBars);
            for (Bar bar : newBars) {
                if (barSeries.isEmpty() || bar.getEndTime().isAfter(barSeries.getLastBar().getEndTime())) {
                    barSeries.addBar(bar);
                }
            }
        }
    }

    /**
     * Sorts the Bars by {@link Bar#getEndTime()} in ascending sequence (lower times
     * before higher times).
     *
     * @param bars the bars
     * @return the sorted bars
     */
    public static List<Bar> sortBars(List<Bar> bars) {
        if (!bars.isEmpty()) {
            Collections.sort(bars, BarSeriesUtils.sortBarsByTime);
        }
        return bars;
    }

}
