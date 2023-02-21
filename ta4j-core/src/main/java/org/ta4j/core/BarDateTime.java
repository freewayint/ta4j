package org.ta4j.core;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

public class BarDateTime { // andrewp
	private final long timestamp_ms;

	public BarDateTime(long timestamp_ms) {
		this.timestamp_ms = timestamp_ms;
	}

	public long toEpochMilli() {
		return timestamp_ms;
	}

	public int toEpochSecond() {
		return (int) (timestamp_ms / 1000L);
	}

	public BarDateTime minus(Duration duration) {
		return new BarDateTime(timestamp_ms - duration.toMillis());
	}

	public BarDateTime plus(Duration duration) {
		return new BarDateTime(timestamp_ms + duration.toMillis());
	}

	public boolean isBefore(BarDateTime other) {
		return timestamp_ms < other.timestamp_ms;
	}

	public boolean isAfter(BarDateTime other) {
		return timestamp_ms > other.timestamp_ms;
	}

	public boolean isEqual(BarDateTime other) {
		return timestamp_ms == other.timestamp_ms;
	}

	public LocalTime toLocalTime() {
		return LocalTime.ofInstant(Instant.ofEpochMilli(timestamp_ms), ZoneId.systemDefault());
	}

	@Override
	public String toString() {
		return new Date(timestamp_ms).toString();
	}
}
