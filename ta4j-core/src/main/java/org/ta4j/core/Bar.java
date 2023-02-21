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
import java.util.function.Function;

import org.ta4j.core.num.Num;

/**
 * End bar of a time period.
 *
 * Bar object is aggregated open/high/low/close/volume/etc. data over a time
 * period.
 */
public interface Bar extends Serializable {
    /**
     * @return the open price of the period
     */
    Num getOpenPrice();

    /**
     * @return the low price of the period
     */
    Num getLowPrice();

    /**
     * @return the high price of the period
     */
    Num getHighPrice();

    /**
     * @return the close price of the period
     */
    Num getClosePrice();

    /**
     * @return the whole tradeNum volume in the period
     */
    Num getVolume();

    /**
     * @return the whole traded amount of the period
     */
    Num getBuyVolume();

    /**
     * @return the begin timestamp of the bar period
     */
    BarDateTime getBeginTime();

    /**
     * @return the end timestamp of the bar period
     */
    BarDateTime getEndTime();

    /**
     * @param timestamp a timestamp
     * @return true if the provided timestamp is between the begin time and the end
     *         time of the current period, false otherwise
     */
    default boolean inPeriod(BarDateTime timestamp) {
        return timestamp != null && !timestamp.isBefore(getBeginTime()) && timestamp.isBefore(getEndTime());
    }

    /**
     * @return true if this is a bearish bar, false otherwise
     */
    default boolean isBearish() {
        Num openPrice = getOpenPrice();
        Num closePrice = getClosePrice();
        return (openPrice != null) && (closePrice != null) && closePrice.isLessThan(openPrice);
    }

    /**
     * @return true if this is a bullish bar, false otherwise
     */
    default boolean isBullish() {
        Num openPrice = getOpenPrice();
        Num closePrice = getClosePrice();
        return (openPrice != null) && (closePrice != null) && openPrice.isLessThan(closePrice);
    }

    default void addPrice(String price, Function<Number, Num> numFunction) {
        addPrice(numFunction.apply(new BigDecimal(price)));
    }

    default void addPrice(Number price, Function<Number, Num> numFunction) {
        addPrice(numFunction.apply(price));
    }

    void addPrice(Num price);
}
