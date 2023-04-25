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

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Duration;
import org.ta4j.core.BarDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;

import org.ta4j.core.num.Num;

/**
 * Sequence of {@link Bar bars} separated by a predefined period (e.g. 15
 * minutes, 1 day, etc.)
 *
 * Notably, a {@link BarSeries bar series} can be:
 * <ul>
 * <li>the base of {@link Indicator indicator} calculations
 * <li>constrained between begin and end indexes (e.g. for some backtesting
 * cases)
 * <li>limited to a fixed number of bars (e.g. for actual trading)
 * </ul>
 */
public interface BarSeries extends Serializable {

    /**
     * @return the name of the series
     */
    String getName();

    /**
     * @return the Num of 0
     */
    default Num zero() {
        return Num.ZERO;
    }

    /**
     * @return the Num of 1
     */
    default Num one() {
        return Num.ONE;
    }

    /**
     * Transforms a {@link Number} into the {@link Num implementation} used by this
     * bar series
     *
     * @param number a {@link Number} implementing object.
     * @return the corresponding value as a Num implementing object
     */
    default Num numOf(Number number) {
        return Num.valueOf(number);
    }

    /**
     * @param i an index
     * @return the bar at the i-th position
     */
    Bar getBar(int i);

    /**
     * @return the first bar of the series
     */
    default Bar getFirstBar() {
        return getBar(getBeginIndex());
    }

    /**
     * @return the last bar of the series
     */
    default Bar getLastBar() {
        return getBar(getEndIndex());
    }

    /**
     * @return the number of bars in the series
     */
    int getBarCount();

    /**
     * @return true if the series is empty, false otherwise
     */
    default boolean isEmpty() {
        return getBarCount() == 0;
    }

    /**
     * Warning: should be used carefully!
     *
     * Returns the raw bar data. It means that it returns the current List object
     * used internally to store the {@link Bar bars}. It may be: - a shortened bar
     * list if a maximum bar count has been set - an extended bar list if it is a
     * constrained bar series
     *
     * @return the raw bar data
     */
    List<Bar> getBarData();

    /**
     * @return the begin index of the series
     */
    int getBeginIndex();

    /**
     * @return the end index of the series
     */
    int getEndIndex();

    /**
     * @return the description of the series period (e.g. "from 12:00 21/01/2014 to
     *         12:15 21/01/2014")
     */
    default String getSeriesPeriodDescription() {
        StringBuilder sb = new StringBuilder();
        if (!getBarData().isEmpty()) {
            Bar firstBar = getFirstBar();
            Bar lastBar = getLastBar();
            sb.append(firstBar.getEndTime().toString())
                    .append(" - ")
                    .append(lastBar.getEndTime().toString());
        }
        return sb.toString();
    }

    /**
     * @return the maximum number of bars
     */
    int getMaximumBarCount();

    /**
     * Adds a bar at the end of the series.
     *
     * Begin index set to 0 if it wasn't initialized.<br>
     * End index set to 0 if it wasn't initialized, or incremented if it matches the
     * end of the series.<br>
     * Exceeding bars are removed.
     *
     * @param bar the bar to be added
     * @apiNote use #addBar(Duration, BarDateTime, Num, Num, Num, Num, Num) to add
     *          bar data directly
     * @see BarSeries#setMaximumBarCount(int)
     */
    default void addBar(Bar bar) {
        addBar(bar, false);
    }

    /**
     * Adds a bar at the end of the series.
     *
     * Begin index set to 0 if it wasn't initialized.<br>
     * End index set to 0 if it wasn't initialized, or incremented if it matches the
     * end of the series.<br>
     * Exceeding bars are removed.
     *
     * @param bar     the bar to be added
     * @param replace true to replace the latest bar. Some exchange provide
     *                continuous new bar data in the time period. (eg. 1s in 1m
     *                Duration)<br>
     * @apiNote use #addBar(Duration, BarDateTime, Num, Num, Num, Num, Num) to add
     *          bar data directly
     * @see BarSeries#setMaximumBarCount(int)
     */
    void addBar(Bar bar, boolean replace);
}
