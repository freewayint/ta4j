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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.ta4j.core.num.Num;

public class BaseBarSeriesBuilder implements BarSeriesBuilder {

    /**
     * Default instance of Num to determine its Num type and function.
     **/
    private String name;
    private int maxBarCount;

    public BaseBarSeriesBuilder(int maxBarCount) {
        initValues();
		this.maxBarCount = maxBarCount;
    }

    private void initValues() {
        this.name = "unnamed_series";
        this.maxBarCount = Integer.MAX_VALUE;
    }

    @Override
    public BaseBarSeries build() {
        int beginIndex = -1;
        int endIndex = -1;
        BaseBarSeries series = new BaseBarSeries(name, maxBarCount, beginIndex, endIndex);
        initValues(); // reinitialize values for next series
        return series;
    }

    /**
     * @param name to set {@link BaseBarSeries#getName()}
     * @return {@code this}
     */
    public BaseBarSeriesBuilder withName(String name) {
        this.name = name;
        return this;
    }

    /**
     * @param maxBarCount to set {@link BaseBarSeries#getMaximumBarCount()}
     * @return {@code this}
     */
    public BaseBarSeriesBuilder withMaxBarCount(int maxBarCount) {
        this.maxBarCount = maxBarCount;
        return this;
    }
}
