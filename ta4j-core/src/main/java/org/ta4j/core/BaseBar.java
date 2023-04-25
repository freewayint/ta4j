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

import java.math.BigDecimal;
import java.time.Duration;
import java.time.ZoneId;
import org.ta4j.core.BarDateTime;
import java.util.Objects;
import java.util.function.Function;

import org.ta4j.core.num.Num;

/**
 * Base implementation of a {@link Bar}.
 */
public class BaseBar implements Bar {

    private static final long serialVersionUID = 8038383777467488147L;
    /** End time of the bar */
    private final BarDateTime endTime;
    /** Begin time of the bar */
    private final BarDateTime beginTime;
    /** Open price of the period */
    private Num openPrice;
    /** Close price of the period */
    private Num closePrice;
    /** High price of the period */
    private Num highPrice;
    /** Low price of the period */
    private Num lowPrice;
	/** Volume of the period */
    private final Num volume;
    /** Traded buyVolume during the period */
    private final Num buyVolume;

	public BaseBar(BarDateTime beginTime, BarDateTime endTime, Num openPrice, Num highPrice, Num lowPrice, Num closePrice, Num volume, Num buyVolume) {
        this.beginTime = beginTime;
		this.endTime = endTime;
        this.openPrice = openPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.closePrice = closePrice;
        this.volume = volume;
        this.buyVolume = buyVolume;
    }

	public BaseBar(long beginTime, long endTime, double openPrice, double highPrice, double lowPrice, double closePrice, double volume, double buyVolume) {
		this.beginTime = new BarDateTime(beginTime);
		this.endTime = new BarDateTime(endTime);
		this.openPrice = Num.valueOf(openPrice);
		this.highPrice = Num.valueOf(highPrice);
		this.lowPrice = Num.valueOf(lowPrice);
		this.closePrice = Num.valueOf(closePrice);
		this.volume = Num.valueOf(volume);
		this.buyVolume = Num.valueOf(buyVolume);
	}

    /**
     * @return the open price of the period
     */
	@Override
    public Num getOpenPrice() {
        return openPrice;
    }

    /**
     * @return the low price of the period
     */
	@Override
    public Num getLowPrice() {
        return lowPrice;
    }

    /**
     * @return the high price of the period
     */
	@Override
    public Num getHighPrice() {
        return highPrice;
    }

    /**
     * @return the close price of the period
     */
	@Override
    public Num getClosePrice() {
        return closePrice;
    }

    /**
     * @return the whole traded volume in the period
     */
	@Override
    public Num getVolume() {
        return volume;
    }

    /**
     * @return the whole traded buyVolume (tradePrice x tradeVolume) of the period
     */
	@Override
    public Num getBuyVolume() {
        return buyVolume;
    }

    /**
     * @return the begin timestamp of the bar period
     */
	@Override
    public BarDateTime getBeginTime() {
        return beginTime;
    }

    /**
     * @return the end timestamp of the bar period
     */
	@Override
    public BarDateTime getEndTime() {
        return endTime;
    }

    @Override
    public void addPrice(Num price) {
        if (openPrice == null) {
            openPrice = price;
        }
        closePrice = price;
        if (highPrice == null || highPrice.isLessThan(price)) {
            highPrice = price;
        }
        if (lowPrice == null || lowPrice.isGreaterThan(price)) {
            lowPrice = price;
        }
    }

    @Override
    public String toString() {
        return String.format(
                "{end time: %1s, close price: %2$f, open price: %3$f, low price: %4$f, high price: %5$f, volume: %6$f}",
                endTime.toString(), closePrice.doubleValue(), openPrice.doubleValue(),
                lowPrice.doubleValue(), highPrice.doubleValue(), volume.doubleValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(beginTime, endTime, openPrice, highPrice, lowPrice, closePrice, volume, buyVolume);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof BaseBar))
            return false;
        final BaseBar other = (BaseBar) obj;
        return Objects.equals(beginTime, other.beginTime) && Objects.equals(endTime, other.endTime)
                && Objects.equals(openPrice, other.openPrice)
                && Objects.equals(highPrice, other.highPrice) && Objects.equals(lowPrice, other.lowPrice)
                && Objects.equals(closePrice, other.closePrice) && Objects.equals(volume, other.volume)
                && Objects.equals(buyVolume, other.buyVolume);
    }
}
