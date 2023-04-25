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
package org.ta4j.core.indicators.helpers;

import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.num.Num;

/**
 * True range indicator.
 */
public class TRIndicator extends CachedIndicator {

    public TRIndicator(BarSeries series) {
        super(series);
    }

    @Override
    protected Num calculate(int index) {
		Bar bar = getBarSeries().getBar(index);

		// NOTE: https://www.tradingview.com/pine-script-reference/v5/#fun_ta%7Bdot%7Dtr
		Num high = bar.getHighPrice();
		Num low = bar.getLowPrice();

		if (index == getBarSeries().getBeginIndex()) {
			return high.minus(low); // NOTE: ta.tr(handle_na==true)

		} else {
			Num prevClose = getBarSeries().getBar(index - 1).getClosePrice();
			return high.minus(low).max(
				high.minus(prevClose).abs().max(
					low.minus(prevClose).abs()
				)
			);
		}

		/*
        Num ts = getBarSeries().getBar(index).getHighPrice().minus(getBarSeries().getBar(index).getLowPrice());
        Num ys = index == getBarSeries().getBeginIndex() ? zero() // andrewp
                : getBarSeries().getBar(index).getHighPrice().minus(getBarSeries().getBar(index - 1).getClosePrice());
        Num yst = index == getBarSeries().getBeginIndex() ? zero() // andrewp
                : getBarSeries().getBar(index - 1).getClosePrice().minus(getBarSeries().getBar(index).getLowPrice());
        return ts.abs().max(ys.abs()).max(yst.abs());
		*/
    }
}
