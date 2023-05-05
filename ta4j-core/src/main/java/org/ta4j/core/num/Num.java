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
package org.ta4j.core.num;

import java.io.Serializable;

// andrewp: DoubleNum -> Num collapsed
public final class Num implements Comparable<Num>, Serializable {
	public static final Num NaN = Num.valueOf(Double.NaN);
	public static final Num ZERO = Num.valueOf(0);
    public static final Num ONE = Num.valueOf(1);

	private final double delegate;

	private Num(double val) {
        delegate = val;
    }

    public Num numOf(Number value) {
        return new Num((double) value);
    }

    public static Num valueOf(int i) {
        return new Num((double) i);
    }

	public static Num valueOf(boolean i) {
        return new Num(i ? 1.0 : 0.0);
    }

    public static Num valueOf(long i) {
        return new Num((double) i);
    }

    public static Num valueOf(short i) {
        return new Num((double) i);
    }

    public static Num valueOf(float i) {
        return new Num((double) i);
    }

    public static Num valueOf(String i) {
        return new Num(Double.parseDouble(i));
    }

    public static Num valueOf(Number i) {
        return new Num(i.doubleValue());
    }

    public Num zero() {
        return ZERO;
    }

    public Num one() {
        return ONE;
    }

    public Num plus(Num augend) {
        return new Num(delegate + augend.delegate);
    }

    public Num minus(Num subtrahend) {
        return new Num(delegate - subtrahend.delegate);
    }

    public Num multipliedBy(Num multiplicand) {
        return new Num(delegate * multiplicand.delegate);
    }

    public Num dividedBy(Num divisor) {
        if (divisor.isZero()) {
            return NaN;
        }
        return new Num(delegate / divisor.delegate);
    }

    public Num remainder(Num divisor) {
        return new Num(delegate % divisor.delegate);
    }

    public Num floor() {
        return new Num(Math.floor(delegate));
    }

	public Num round() {
        return new Num(Math.round(delegate));
    }

    public Num ceil() {
        return new Num(Math.ceil(delegate));
    }

    public Num pow(int n) {
        return new Num(Math.pow(delegate, n));
    }

    public Num pow(Num n) {
        return new Num(Math.pow(delegate, n.doubleValue()));
    }

    public Num sqrt() {
        if (delegate < 0) {
            return NaN;
        }
        return new Num(Math.sqrt(delegate));
    }

    public Num sqrt(int precision) {
        return sqrt();
    }

    public Num abs() {
        return new Num(Math.abs(delegate));
    }

    public Num negate() {
        return new Num(-delegate);
    }

    public boolean isZero() {
        return delegate == 0;
    }

    public boolean isPositive() {
        return delegate > 0;
    }

    public boolean isPositiveOrZero() {
        return delegate >= 0;
    }

    public boolean isNegative() {
        return delegate < 0;
    }

    public boolean isNegativeOrZero() {
        return delegate <= 0;
    }

    public boolean isEqual(Num other) {
        return delegate == other.delegate;
    }

    public Num log() {
        if (delegate <= 0) {
            return NaN;
        }
        return new Num(Math.log(delegate));
    }

    /**
     * Checks if this value is greater than another.
     *
     * @param other the other value, not null
     * @return true is this is greater than the specified value, false otherwise
     */
    public boolean isGreaterThan(Num other) {
        return delegate > other.delegate;
    }

    /**
     * Checks if this value is greater than or equal to another.
     *
     * @param other the other value, not null
     * @return true is this is greater than or equal to the specified value, false
     *         otherwise
     */
    public boolean isGreaterThanOrEqual(Num other) {
        return delegate >= other.delegate;
    }

    /**
     * Checks if this value is less than another.
     *
     * @param other the other value, not null
     * @return true is this is less than the specified value, false otherwise
     */
    public boolean isLessThan(Num other) {
        return delegate < other.delegate;
    }

    public boolean isLessThanOrEqual(Num other) {
        return delegate <= other.delegate;
    }

    public Num min(Num other) {
		if (delegate < other.delegate)
			return this;
		else
			return other;
    }

    public Num max(Num other) {
        if (delegate > other.delegate)
			return this;
		else
			return other;
    }

    @Override
    public int hashCode() {
        return ((Double) (delegate)).hashCode();
    }

    @Override
    public String toString() {
        return Double.toString(delegate);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Num)) {
            return false;
        }

		Num otherNum = (Num) obj;
		return delegate == otherNum.delegate;
    }

    @Override
    public int compareTo(Num o) {
        if (this == NaN || o == NaN) {
            return 0;
        }
        return Double.compare(delegate, o.delegate);
    }

    /**
     * Only for NaN this should be true
     *
     * @return false if this implementation is not NaN
     */
    public boolean isNaN() {
        return this == NaN || Double.isNaN(delegate);
    }

    /**
     * Converts this {@code num} to a {@code double}.
     *
     * @return this {@code num} converted to a {@code double}
     */
    public double doubleValue() {
        return delegate;
    }

    /**
     * Converts this {@code num} to an {@code integer}.
     *
     * @return this {@code num} converted to an {@code integer}
     */
    public int intValue() {
        return (int) delegate;
    }

    /**
     * Converts this {@code num} to a {@code long}.
     *
     * @return this {@code num} converted to a {@code loong}
     */
    public long longValue() {
        return (long) delegate;
    }

    /**
     * Converts this {@code num} to a {@code float}.
     *
     * @return this {@code num} converted to a {@code float}
     */
    public float floatValue() {
        return (float) delegate;
    }
}
