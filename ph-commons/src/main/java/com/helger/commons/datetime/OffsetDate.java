/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.commons.datetime;

import static java.time.temporal.ChronoField.EPOCH_DAY;
import static java.time.temporal.ChronoField.OFFSET_SECONDS;
import static java.time.temporal.ChronoUnit.DAYS;

import java.io.Serializable;
import java.time.Clock;
import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalQuery;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;
import java.time.zone.ZoneRules;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.hashcode.HashCodeGenerator;

/**
 * Copyright (c) 2007-present, Stephen Colebourne &amp; Michael Nascimento
 * Santos.<br>
 * This class is originally from the ThreeTen-Extra project. It is included here
 * to reduce dependencies.
 * <p>
 * A date with an offset from UTC/Greenwich in the ISO-8601 calendar system,
 * such as {@code 2007-12-03+01:00}.
 * <p>
 * {@code OffsetDate} is an immutable date-time object that represents a date,
 * often viewed as year-month-day-offset. This object can also access other date
 * fields such as day-of-year, day-of-week and week-of-year.
 * <p>
 * This class does not store or represent a time. For example, the value "2nd
 * October 2007 +02:00" can be stored in an {@code OffsetDate}.
 * <b>Implementation Requirements:</b> This class is immutable and thread-safe.
 * <p>
 * This class must be treated as a value type. Do not synchronize, rely on the
 * identity hash code or use the distinction between equals() and ==.
 *
 * @since v10.0.0
 * @see XMLOffsetDate
 */
public class OffsetDate implements Temporal, TemporalAdjuster, Comparable <OffsetDate>, Serializable
{
  /**
   * The minimum supported {@code OffsetDate}, '-999999999-01-01+18:00'. This is
   * the minimum local date in the maximum offset (larger offsets are earlier on
   * the time-line). This combines {@link LocalDate#MIN} and
   * {@link ZoneOffset#MAX}. This could be used by an application as a "far
   * past" date.
   */
  public static final OffsetDate MIN = OffsetDate.of (LocalDate.MIN, ZoneOffset.MAX);
  /**
   * The maximum supported {@code OffsetDate}, '+999999999-12-31-18:00'. This is
   * the maximum local date in the minimum offset (larger negative offsets are
   * later on the time-line). This combines {@link LocalDate#MAX} and
   * {@link ZoneOffset#MIN}. This could be used by an application as a "far
   * future" date.
   */
  public static final OffsetDate MAX = OffsetDate.of (LocalDate.MAX, ZoneOffset.MIN);

  /**
   * The number of seconds per day.
   */
  private static final long SECONDS_PER_DAY = 86400;

  /**
   * The local date.
   */
  private final LocalDate m_aDate;
  /**
   * The offset from UTC/Greenwich.
   */
  private final ZoneOffset m_aOffset;

  /**
   * Obtains the current date from the system clock in the default time-zone.
   * <p>
   * This will query the {@link Clock#systemDefaultZone() system clock} in the
   * default time-zone to obtain the current date. The offset will be calculated
   * from the time-zone in the clock.
   * <p>
   * Using this method will prevent the ability to use an alternate clock for
   * testing because the clock is hard-coded.
   *
   * @return the current date using the system clock, not null
   */
  @Nonnull
  public static OffsetDate now ()
  {
    return now (Clock.systemDefaultZone ());
  }

  /**
   * Obtains the current date from the system clock in the specified time-zone.
   * <p>
   * This will query the {@link Clock#system(ZoneId) system clock} to obtain the
   * current date. Specifying the time-zone avoids dependence on the default
   * time-zone. The offset will be calculated from the specified time-zone.
   * <p>
   * Using this method will prevent the ability to use an alternate clock for
   * testing because the clock is hard-coded.
   *
   * @param zone
   *        the zone ID to use, not null
   * @return the current date using the system clock, not null
   */
  @Nonnull
  public static OffsetDate now (@Nonnull final ZoneId zone)
  {
    return now (Clock.system (zone));
  }

  /**
   * Obtains the current date from the specified clock.
   * <p>
   * This will query the specified clock to obtain the current date - today. The
   * offset will be calculated from the time-zone in the clock.
   * <p>
   * Using this method allows the use of an alternate clock for testing. The
   * alternate clock may be introduced using {@link Clock dependency injection}.
   *
   * @param clock
   *        the clock to use, not null
   * @return the current date, not null
   */
  @Nonnull
  public static OffsetDate now (@Nonnull final Clock clock)
  {
    ValueEnforcer.notNull (clock, "clock");
    final Instant now = clock.instant (); // called once
    return ofInstant (now, clock.getZone ().getRules ().getOffset (now));
  }

  /**
   * Obtains an instance of {@code OffsetDate} from a local date and an offset.
   *
   * @param date
   *        the local date, not null
   * @param offset
   *        the zone offset, not null
   * @return the offset date, not null
   */
  @Nonnull
  public static OffsetDate of (@Nonnull final LocalDate date, @Nonnull final ZoneOffset offset)
  {
    return new OffsetDate (date, offset);
  }

  /**
   * Obtains an instance of {@code OffsetDate} from a year, month, day and
   * offset.
   * <p>
   * This creates an offset date with the four specified fields.
   * <p>
   * This method exists primarily for writing test cases. Non test-code will
   * typically use other methods to create an offset time.
   *
   * @param year
   *        the year to represent, from MIN_YEAR to MAX_YEAR
   * @param month
   *        the month-of-year to represent, from 1 (January) to 12 (December)
   * @param dayOfMonth
   *        the day-of-month to represent, from 1 to 31
   * @param offset
   *        the zone offset, not null
   * @return the offset date, not null
   * @throws DateTimeException
   *         if the value of any field is out of range, or if the day-of-month
   *         is invalid for the month-year
   */
  @Nonnull
  public static OffsetDate of (final int year, final int month, final int dayOfMonth, @Nonnull final ZoneOffset offset)
  {
    final LocalDate d = LocalDate.of (year, month, dayOfMonth);
    return new OffsetDate (d, offset);
  }

  /**
   * Obtains an instance of {@code OffsetDate} from a year, month, day and
   * offset.
   * <p>
   * This creates an offset date with the four specified fields.
   * <p>
   * This method exists primarily for writing test cases. Non test-code will
   * typically use other methods to create an offset time.
   *
   * @param year
   *        the year to represent, from MIN_YEAR to MAX_YEAR
   * @param month
   *        the month-of-year to represent, from 1 (January) to 12 (December)
   * @param dayOfMonth
   *        the day-of-month to represent, from 1 to 31
   * @param offset
   *        the zone offset, not null
   * @return the offset date, not null
   * @throws DateTimeException
   *         if the value of any field is out of range, or if the day-of-month
   *         is invalid for the month-year
   */
  @Nonnull
  public static OffsetDate of (final int year,
                               final Month month,
                               final int dayOfMonth,
                               @Nonnull final ZoneOffset offset)
  {
    final LocalDate d = LocalDate.of (year, month, dayOfMonth);
    return new OffsetDate (d, offset);
  }

  /**
   * Obtains an instance of {@code OffsetDate} from an {@code Instant} and zone
   * ID.
   * <p>
   * This creates an offset date with the same instant as midnight at the start
   * of day of the instant specified. Finding the offset from UTC/Greenwich is
   * simple as there is only one valid offset for each instant.
   *
   * @param instant
   *        the instant to create the time from, not null
   * @param zone
   *        the time-zone, which may be an offset, not null
   * @return the offset time, not null
   */
  @Nonnull
  public static OffsetDate ofInstant (@Nonnull final Instant instant, @Nonnull final ZoneId zone)
  {
    ValueEnforcer.notNull (instant, "instant");
    ValueEnforcer.notNull (zone, "zone");
    final ZoneRules rules = zone.getRules ();
    final ZoneOffset offset = rules.getOffset (instant);
    // overflow caught later
    final long epochSec = instant.getEpochSecond () + offset.getTotalSeconds ();
    final long epochDay = Math.floorDiv (epochSec, SECONDS_PER_DAY);
    final LocalDate date = LocalDate.ofEpochDay (epochDay);
    return new OffsetDate (date, offset);
  }

  // -----------------------------------------------------------------------
  /**
   * Obtains an instance of {@code OffsetDate} from a temporal object.
   * <p>
   * A {@code TemporalAccessor} represents some form of date and time
   * information. This factory converts the arbitrary temporal object to an
   * instance of {@code OffsetDate}.
   * <p>
   * The conversion extracts and combines {@code LocalDate} and
   * {@code ZoneOffset}.
   * <p>
   * This method matches the signature of the functional interface
   * {@link TemporalQuery} allowing it to be used in queries via method
   * reference, {@code OffsetDate::from}.
   *
   * @param temporal
   *        the temporal object to convert, not null
   * @return the offset date, not null
   * @throws DateTimeException
   *         if unable to convert to an {@code OffsetDate}
   */
  @Nonnull
  public static OffsetDate from (@Nonnull final TemporalAccessor temporal)
  {
    if (temporal instanceof OffsetDate)
      return (OffsetDate) temporal;

    try
    {
      final LocalDate date = LocalDate.from (temporal);
      final ZoneOffset offset = ZoneOffset.from (temporal);
      return new OffsetDate (date, offset);
    }
    catch (final DateTimeException ex)
    {
      throw new DateTimeException ("Unable to obtain OffsetDate from TemporalAccessor: " + temporal.getClass (), ex);
    }
  }

  // -----------------------------------------------------------------------
  /**
   * Obtains an instance of {@code OffsetDate} from a text string such as
   * {@code 2007-12-03+01:00}.
   * <p>
   * The string must represent a valid date and is parsed using
   * {@link DateTimeFormatter#ISO_OFFSET_DATE}.
   *
   * @param text
   *        the text to parse such as "2007-12-03+01:00", not null
   * @return the parsed offset date, not null
   * @throws DateTimeParseException
   *         if the text cannot be parsed
   */
  @Nonnull
  public static OffsetDate parse (@Nonnull final CharSequence text)
  {
    return parse (text, DateTimeFormatter.ISO_OFFSET_DATE);
  }

  /**
   * Obtains an instance of {@code OffsetDate} from a text string using a
   * specific formatter.
   * <p>
   * The text is parsed using the formatter, returning a date.
   *
   * @param text
   *        the text to parse, not null
   * @param formatter
   *        the formatter to use, not null
   * @return the parsed offset date, not null
   * @throws DateTimeParseException
   *         if the text cannot be parsed
   */
  @Nonnull
  public static OffsetDate parse (@Nonnull final CharSequence text, @Nonnull final DateTimeFormatter formatter)
  {
    ValueEnforcer.notNull (formatter, "formatter");
    return formatter.parse (text, OffsetDate::from);
  }

  // -----------------------------------------------------------------------
  /**
   * Constructor.
   *
   * @param date
   *        the local date, not null
   * @param offset
   *        the zone offset, not null
   */
  protected OffsetDate (@Nonnull final LocalDate date, @Nonnull final ZoneOffset offset)
  {
    m_aDate = ValueEnforcer.notNull (date, "date");
    m_aOffset = ValueEnforcer.notNull (offset, "offset");
  }

  /**
   * Validates the input.
   *
   * @return the valid object, not null
   */
  protected Object readResolve ()
  {
    return of (m_aDate, m_aOffset);
  }

  /**
   * Returns a new date based on this one, returning {@code this} where
   * possible.
   *
   * @param date
   *        the date to create with, not null
   * @param offset
   *        the zone offset to create with, not null
   */
  @Nonnull
  private OffsetDate with (@Nonnull final LocalDate date, @Nonnull final ZoneOffset offset)
  {
    if (this.m_aDate == date && this.m_aOffset.equals (offset))
      return this;

    return new OffsetDate (date, offset);
  }

  // -----------------------------------------------------------------------
  /**
   * Checks if the specified field is supported.
   * <p>
   * This checks if this date can be queried for the specified field. If false,
   * then calling the {@link #range(TemporalField) range},
   * {@link #get(TemporalField) get} and {@link #with(TemporalField, long)}
   * methods will throw an exception.
   * <p>
   * If the field is a {@link ChronoField} then the query is implemented here.
   * The supported fields are:
   * <ul>
   * <li>{@code DAY_OF_WEEK}
   * <li>{@code ALIGNED_DAY_OF_WEEK_IN_MONTH}
   * <li>{@code ALIGNED_DAY_OF_WEEK_IN_YEAR}
   * <li>{@code DAY_OF_MONTH}
   * <li>{@code DAY_OF_YEAR}
   * <li>{@code EPOCH_DAY}
   * <li>{@code ALIGNED_WEEK_OF_MONTH}
   * <li>{@code ALIGNED_WEEK_OF_YEAR}
   * <li>{@code MONTH_OF_YEAR}
   * <li>{@code PROLEPTIC_MONTH}
   * <li>{@code YEAR_OF_ERA}
   * <li>{@code YEAR}
   * <li>{@code ERA}
   * <li>{@code OFFSET_SECONDS}
   * </ul>
   * All other {@code ChronoField} instances will return false.
   * <p>
   * If the field is not a {@code ChronoField}, then the result of this method
   * is obtained by invoking
   * {@code TemporalField.isSupportedBy(TemporalAccessor)} passing {@code this}
   * as the argument. Whether the field is supported is determined by the field.
   *
   * @param field
   *        the field to check, null returns false
   * @return true if the field is supported on this date, false if not
   */
  @Override
  public boolean isSupported (@Nullable final TemporalField field)
  {
    if (field instanceof ChronoField)
      return field.isDateBased () || field == OFFSET_SECONDS;

    return field != null && field.isSupportedBy (this);
  }

  /**
   * Checks if the specified unit is supported.
   * <p>
   * This checks if the specified unit can be added to, or subtracted from, this
   * date. If false, then calling the {@link #plus(long, TemporalUnit)} and
   * {@link #minus(long, TemporalUnit) minus} methods will throw an exception.
   * <p>
   * If the unit is a {@link ChronoUnit} then the query is implemented here. The
   * supported units are:
   * <ul>
   * <li>{@code DAYS}
   * <li>{@code WEEKS}
   * <li>{@code MONTHS}
   * <li>{@code YEARS}
   * <li>{@code DECADES}
   * <li>{@code CENTURIES}
   * <li>{@code MILLENNIA}
   * <li>{@code ERAS}
   * </ul>
   * All other {@code ChronoUnit} instances will return false.
   * <p>
   * If the unit is not a {@code ChronoUnit}, then the result of this method is
   * obtained by invoking {@code TemporalUnit.isSupportedBy(Temporal)} passing
   * {@code this} as the argument. Whether the unit is supported is determined
   * by the unit.
   *
   * @param unit
   *        the unit to check, null returns false
   * @return true if the unit can be added/subtracted, false if not
   */
  @Override
  public boolean isSupported (@Nullable final TemporalUnit unit)
  {
    if (unit instanceof ChronoUnit)
    {
      return unit.isDateBased ();
    }
    return unit != null && unit.isSupportedBy (this);
  }

  // -----------------------------------------------------------------------
  /**
   * Gets the range of valid values for the specified field.
   * <p>
   * The range object expresses the minimum and maximum valid values for a
   * field. This date is used to enhance the accuracy of the returned range. If
   * it is not possible to return the range, because the field is not supported
   * or for some other reason, an exception is thrown.
   * <p>
   * If the field is a {@link ChronoField} then the query is implemented here.
   * The {@link #isSupported(TemporalField) supported fields} will return
   * appropriate range instances. All other {@code ChronoField} instances will
   * throw an {@code UnsupportedTemporalTypeException}.
   * <p>
   * If the field is not a {@code ChronoField}, then the result of this method
   * is obtained by invoking
   * {@code TemporalField.rangeRefinedBy(TemporalAccessor)} passing {@code this}
   * as the argument. Whether the range can be obtained is determined by the
   * field.
   *
   * @param field
   *        the field to query the range for, not null
   * @return the range of valid values for the field, not null
   * @throws DateTimeException
   *         if the range for the field cannot be obtained
   * @throws UnsupportedTemporalTypeException
   *         if the field is not supported
   */
  @Override
  public ValueRange range (@Nonnull final TemporalField field)
  {
    if (field instanceof ChronoField)
    {
      if (field == OFFSET_SECONDS)
      {
        return field.range ();
      }
      return m_aDate.range (field);
    }
    return field.rangeRefinedBy (this);
  }

  /**
   * Gets the value of the specified field from this date as an {@code int}.
   * <p>
   * This queries this date for the value for the specified field. The returned
   * value will always be within the valid range of values for the field. If it
   * is not possible to return the value, because the field is not supported or
   * for some other reason, an exception is thrown.
   * <p>
   * If the field is a {@link ChronoField} then the query is implemented here.
   * The {@link #isSupported(TemporalField) supported fields} will return valid
   * values based on this date, except {@code EPOCH_DAY} and
   * {@code PROLEPTIC_MONTH} which are too large to fit in an {@code int} and
   * throw a {@code DateTimeException}. All other {@code ChronoField} instances
   * will throw a {@code DateTimeException}.
   * <p>
   * If the field is not a {@code ChronoField}, then the result of this method
   * is obtained by invoking {@code TemporalField.getFrom(TemporalAccessor)}
   * passing {@code this} as the argument. Whether the value can be obtained,
   * and what the value represents, is determined by the field.
   *
   * @param field
   *        the field to get, not null
   * @return the value for the field
   * @throws DateTimeException
   *         if a value for the field cannot be obtained or the value is outside
   *         the range of valid values for the field
   * @throws UnsupportedTemporalTypeException
   *         if the field is not supported or the range of values exceeds an
   *         {@code int}
   * @throws ArithmeticException
   *         if numeric overflow occurs
   */
  @Override // override for Javadoc
  public int get (@Nonnull final TemporalField field)
  {
    return Temporal.super.get (field);
  }

  /**
   * Gets the value of the specified field from this date as a {@code long}.
   * <p>
   * This queries this date for the value for the specified field. If it is not
   * possible to return the value, because the field is not supported or for
   * some other reason, an exception is thrown.
   * <p>
   * If the field is a {@link ChronoField} then the query is implemented here.
   * The {@link #isSupported(TemporalField) supported fields} will return valid
   * values based on this date. All other {@code ChronoField} instances will
   * throw an {@code UnsupportedTemporalTypeException}.
   * <p>
   * If the field is not a {@code ChronoField}, then the result of this method
   * is obtained by invoking {@code TemporalField.getFrom(TemporalAccessor)}
   * passing {@code this} as the argument. Whether the value can be obtained,
   * and what the value represents, is determined by the field.
   *
   * @param field
   *        the field to get, not null
   * @return the value for the field
   * @throws DateTimeException
   *         if a value for the field cannot be obtained
   * @throws UnsupportedTemporalTypeException
   *         if the field is not supported
   * @throws ArithmeticException
   *         if numeric overflow occurs
   */
  @Override
  public long getLong (@Nonnull final TemporalField field)
  {
    if (field instanceof ChronoField)
    {
      if (field == OFFSET_SECONDS)
      {
        return getOffset ().getTotalSeconds ();
      }
      return m_aDate.getLong (field);
    }
    return field.getFrom (this);
  }

  // -----------------------------------------------------------------------
  /**
   * Gets the zone offset, such as '+01:00'.
   * <p>
   * This is the offset of the local date from UTC/Greenwich.
   *
   * @return the zone offset, not null
   */
  @Nonnull
  public ZoneOffset getOffset ()
  {
    return m_aOffset;
  }

  /**
   * Returns a copy of this {@code OffsetDate} with the specified offset
   * ensuring that the result has the same local date.
   * <p>
   * This method returns an object with the same {@code LocalDate} and the
   * specified {@code ZoneOffset}. No calculation is needed or performed. For
   * example, if this time represents {@code 2007-12-03+02:00} and the offset
   * specified is {@code +03:00}, then this method will return
   * {@code 2007-12-03+03:00}.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param offset
   *        the zone offset to change to, not null
   * @return an {@code OffsetDate} based on this date with the requested offset,
   *         not null
   */
  @Nonnull
  public OffsetDate withOffsetSameLocal (@Nonnull final ZoneOffset offset)
  {
    ValueEnforcer.notNull (offset, "offset");
    return with (m_aDate, offset);
  }

  // -----------------------------------------------------------------------
  /**
   * Gets the {@code LocalDate} part of this date.
   * <p>
   * This returns a {@code LocalDate} with the same year, month and day as this
   * date.
   *
   * @return the date part of this date, not null
   */
  @Nonnull
  public LocalDate toLocalDate ()
  {
    return m_aDate;
  }

  // -----------------------------------------------------------------------
  /**
   * Gets the year field.
   * <p>
   * This method returns the primitive {@code int} value for the year.
   * <p>
   * The year returned by this method is proleptic as per {@code get(YEAR)}. To
   * obtain the year-of-era, use {@code get(YEAR_OF_ERA)}.
   *
   * @return the year, from MIN_YEAR to MAX_YEAR
   */
  public int getYear ()
  {
    return m_aDate.getYear ();
  }

  /**
   * Gets the month-of-year field from 1 to 12.
   * <p>
   * This method returns the month as an {@code int} from 1 to 12. Application
   * code is frequently clearer if the enum {@link Month} is used by calling
   * {@link #getMonth()}.
   *
   * @return the month-of-year, from 1 to 12
   * @see #getMonth()
   */
  public int getMonthValue ()
  {
    return m_aDate.getMonthValue ();
  }

  /**
   * Gets the month-of-year field using the {@code Month} enum.
   * <p>
   * This method returns the enum {@link Month} for the month. This avoids
   * confusion as to what {@code int} values mean. If you need access to the
   * primitive {@code int} value then the enum provides the
   * {@link Month#getValue() int value}.
   *
   * @return the month-of-year, not null
   * @see #getMonthValue()
   */
  @Nonnull
  public Month getMonth ()
  {
    return m_aDate.getMonth ();
  }

  /**
   * Gets the day-of-month field.
   * <p>
   * This method returns the primitive {@code int} value for the day-of-month.
   *
   * @return the day-of-month, from 1 to 31
   */
  public int getDayOfMonth ()
  {
    return m_aDate.getDayOfMonth ();
  }

  /**
   * Gets the day-of-year field.
   * <p>
   * This method returns the primitive {@code int} value for the day-of-year.
   *
   * @return the day-of-year, from 1 to 365, or 366 in a leap year
   */
  public int getDayOfYear ()
  {
    return m_aDate.getDayOfYear ();
  }

  /**
   * Gets the day-of-week field, which is an enum {@code DayOfWeek}.
   * <p>
   * This method returns the enum {@link DayOfWeek} for the day-of-week. This
   * avoids confusion as to what {@code int} values mean. If you need access to
   * the primitive {@code int} value then the enum provides the
   * {@link DayOfWeek#getValue() int value}.
   * <p>
   * Additional information can be obtained from the {@code DayOfWeek}. This
   * includes textual names of the values.
   *
   * @return the day-of-week, not null
   */
  @Nonnull
  public DayOfWeek getDayOfWeek ()
  {
    return m_aDate.getDayOfWeek ();
  }

  // -----------------------------------------------------------------------
  /**
   * Returns an adjusted copy of this date.
   * <p>
   * This returns an {@code OffsetDate} based on this one, with the date
   * adjusted. The adjustment takes place using the specified adjuster strategy
   * object. Read the documentation of the adjuster to understand what
   * adjustment will be made.
   * <p>
   * A simple adjuster might simply set the one of the fields, such as the year
   * field. A more complex adjuster might set the date to the last day of the
   * month. A selection of common adjustments is provided in
   * {@link TemporalAdjusters}. These include finding the "last day of the
   * month" and "next Wednesday". Key date-time classes also implement the
   * {@code TemporalAdjuster} interface, such as {@link Month} and
   * {@link MonthDay MonthDay}. The adjuster is responsible for handling special
   * cases, such as the varying lengths of month and leap years.
   * <p>
   * For example this code returns a date on the last day of July:
   *
   * <pre>
   *  import static java.time.Month.*;
   *  import static java.time.temporal.TemporalAdjusters.*;
   *
   *  result = offsetDate.with(JULY).with(lastDayOfMonth());
   * </pre>
   * <p>
   * The classes {@link LocalDate} and {@link ZoneOffset} implement
   * {@code TemporalAdjuster}, thus this method can be used to change the date
   * or offset:
   *
   * <pre>
   * result = offsetDate.with (date);
   * result = offsetDate.with (offset);
   * </pre>
   * <p>
   * The result of this method is obtained by invoking the
   * {@link TemporalAdjuster#adjustInto(Temporal)} method on the specified
   * adjuster passing {@code this} as the argument.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param adjuster
   *        the adjuster to use, not null
   * @return an {@code OffsetDate} based on {@code this} with the adjustment
   *         made, not null
   * @throws DateTimeException
   *         if the adjustment cannot be made
   * @throws ArithmeticException
   *         if numeric overflow occurs
   */
  @Override
  @Nonnull
  public OffsetDate with (@Nonnull final TemporalAdjuster adjuster)
  {
    // optimizations
    if (adjuster instanceof LocalDate)
      return with ((LocalDate) adjuster, m_aOffset);
    if (adjuster instanceof ZoneOffset)
      return with (m_aDate, (ZoneOffset) adjuster);
    if (adjuster instanceof OffsetDate)
      return (OffsetDate) adjuster;
    return (OffsetDate) adjuster.adjustInto (this);
  }

  /**
   * Returns a copy of this date with the specified field set to a new value.
   * <p>
   * This returns an {@code OffsetDate} based on this one, with the value for
   * the specified field changed. This can be used to change any supported
   * field, such as the year, month or day-of-month. If it is not possible to
   * set the value, because the field is not supported or for some other reason,
   * an exception is thrown.
   * <p>
   * In some cases, changing the specified field can cause the resulting date to
   * become invalid, such as changing the month from 31st January to February
   * would make the day-of-month invalid. In cases like this, the field is
   * responsible for resolving the date. Typically it will choose the previous
   * valid date, which would be the last valid day of February in this example.
   * <p>
   * If the field is a {@link ChronoField} then the adjustment is implemented
   * here.
   * <p>
   * The {@code OFFSET_SECONDS} field will return a date with the specified
   * offset. The local date is unaltered. If the new offset value is outside the
   * valid range then a {@code DateTimeException} will be thrown.
   * <p>
   * The other {@link #isSupported(TemporalField) supported fields} will behave
   * as per the matching method on {@link LocalDate#with(TemporalField, long)}
   * LocalDate}. In this case, the offset is not part of the calculation and
   * will be unchanged.
   * <p>
   * All other {@code ChronoField} instances will throw an
   * {@code UnsupportedTemporalTypeException}.
   * <p>
   * If the field is not a {@code ChronoField}, then the result of this method
   * is obtained by invoking {@code TemporalField.adjustInto(Temporal, long)}
   * passing {@code this} as the argument. In this case, the field determines
   * whether and how to adjust the instant.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param field
   *        the field to set in the result, not null
   * @param newValue
   *        the new value of the field in the result
   * @return an {@code OffsetDate} based on {@code this} with the specified
   *         field set, not null
   * @throws DateTimeException
   *         if the field cannot be set
   * @throws UnsupportedTemporalTypeException
   *         if the field is not supported
   * @throws ArithmeticException
   *         if numeric overflow occurs
   */
  @Override
  @Nonnull
  public OffsetDate with (@Nonnull final TemporalField field, final long newValue)
  {
    if (field instanceof ChronoField)
    {
      if (field == OFFSET_SECONDS)
      {
        final ChronoField f = (ChronoField) field;
        return with (m_aDate, ZoneOffset.ofTotalSeconds (f.checkValidIntValue (newValue)));
      }
      return with (m_aDate.with (field, newValue), m_aOffset);
    }
    return field.adjustInto (this, newValue);
  }

  // -----------------------------------------------------------------------
  /**
   * Returns a copy of this {@code OffsetDate} with the year altered.
   * <p>
   * The offset does not affect the calculation and will be the same in the
   * result. If the day-of-month is invalid for the year, it will be changed to
   * the last valid day of the month.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param year
   *        the year to set in the result, from MIN_YEAR to MAX_YEAR
   * @return an {@code OffsetDate} based on this date with the requested year,
   *         not null
   * @throws DateTimeException
   *         if the year value is invalid
   */
  @Nonnull
  public OffsetDate withYear (final int year)
  {
    return with (m_aDate.withYear (year), m_aOffset);
  }

  /**
   * Returns a copy of this {@code OffsetDate} with the month-of-year altered.
   * <p>
   * The offset does not affect the calculation and will be the same in the
   * result. If the day-of-month is invalid for the year, it will be changed to
   * the last valid day of the month.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param month
   *        the month-of-year to set in the result, from 1 (January) to 12
   *        (December)
   * @return an {@code OffsetDate} based on this date with the requested month,
   *         not null
   * @throws DateTimeException
   *         if the month-of-year value is invalid
   */
  @Nonnull
  public OffsetDate withMonth (final int month)
  {
    return with (m_aDate.withMonth (month), m_aOffset);
  }

  /**
   * Returns a copy of this {@code OffsetDate} with the day-of-month altered.
   * <p>
   * If the resulting date is invalid, an exception is thrown. The offset does
   * not affect the calculation and will be the same in the result.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param dayOfMonth
   *        the day-of-month to set in the result, from 1 to 28-31
   * @return an {@code OffsetDate} based on this date with the requested day,
   *         not null
   * @throws DateTimeException
   *         if the day-of-month value is invalid, or if the day-of-month is
   *         invalid for the month-year
   */
  @Nonnull
  public OffsetDate withDayOfMonth (final int dayOfMonth)
  {
    return with (m_aDate.withDayOfMonth (dayOfMonth), m_aOffset);
  }

  /**
   * Returns a copy of this {@code OffsetDate} with the day-of-year altered.
   * <p>
   * If the resulting date is invalid, an exception is thrown.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param dayOfYear
   *        the day-of-year to set in the result, from 1 to 365-366
   * @return an {@code OffsetDate} based on this date with the requested day,
   *         not null
   * @throws DateTimeException
   *         if the day-of-year value is invalid, or if the day-of-year is
   *         invalid for the year
   */
  @Nonnull
  public OffsetDate withDayOfYear (final int dayOfYear)
  {
    return with (m_aDate.withDayOfYear (dayOfYear), m_aOffset);
  }

  // -----------------------------------------------------------------------
  /**
   * Returns a copy of this date with the specified period added.
   * <p>
   * This returns an {@code OffsetDate} based on this one, with the specified
   * amount added. The amount is typically {@link Period} but may be any other
   * type implementing the {@link TemporalAmount} interface.
   * <p>
   * This uses {@link TemporalAmount#addTo(Temporal)} to perform the
   * calculation.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param amountToAdd
   *        the amount to add, not null
   * @return an {@code OffsetDate} based on this date with the addition made,
   *         not null
   * @throws DateTimeException
   *         if the addition cannot be made
   * @throws ArithmeticException
   *         if numeric overflow occurs
   */
  @Override
  @Nonnull
  public OffsetDate plus (@Nonnull final TemporalAmount amountToAdd)
  {
    return (OffsetDate) amountToAdd.addTo (this);
  }

  /**
   * Returns a copy of this date with the specified amount added.
   * <p>
   * This returns an {@code OffsetDate} based on this one, with the amount in
   * terms of the unit added. If it is not possible to add the amount, because
   * the unit is not supported or for some other reason, an exception is thrown.
   * <p>
   * If the field is a {@link ChronoUnit} then the addition is implemented by
   * {@link LocalDate#plus(long, TemporalUnit)}. The offset is not part of the
   * calculation and will be unchanged in the result.
   * <p>
   * If the field is not a {@code ChronoUnit}, then the result of this method is
   * obtained by invoking {@code TemporalUnit.addTo(Temporal, long)} passing
   * {@code this} as the argument. In this case, the unit determines whether and
   * how to perform the addition.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param amountToAdd
   *        the amount of the unit to add to the result, may be negative
   * @param unit
   *        the unit of the amount to add, not null
   * @return an {@code OffsetDate} based on this date with the specified amount
   *         added, not null
   * @throws DateTimeException
   *         if the addition cannot be made
   * @throws UnsupportedTemporalTypeException
   *         if the unit is not supported
   * @throws ArithmeticException
   *         if numeric overflow occurs
   */
  @Override
  @Nonnull
  public OffsetDate plus (final long amountToAdd, @Nonnull final TemporalUnit unit)
  {
    if (unit instanceof ChronoUnit)
      return with (m_aDate.plus (amountToAdd, unit), m_aOffset);
    return unit.addTo (this, amountToAdd);
  }

  // -----------------------------------------------------------------------
  /**
   * Returns a copy of this {@code OffsetDate} with the specified number of
   * years added.
   * <p>
   * This uses {@link LocalDate#plusYears(long)} to add the years. The offset
   * does not affect the calculation and will be the same in the result.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param years
   *        the years to add, may be negative
   * @return an {@code OffsetDate} based on this date with the years added, not
   *         null
   * @throws DateTimeException
   *         if the result exceeds the supported date range
   */
  @Nonnull
  public OffsetDate plusYears (final long years)
  {
    return with (m_aDate.plusYears (years), m_aOffset);
  }

  /**
   * Returns a copy of this {@code OffsetDate} with the specified number of
   * months added.
   * <p>
   * This uses {@link LocalDate#plusMonths(long)} to add the months. The offset
   * does not affect the calculation and will be the same in the result.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param months
   *        the months to add, may be negative
   * @return an {@code OffsetDate} based on this date with the months added, not
   *         null
   * @throws DateTimeException
   *         if the result exceeds the supported date range
   */
  @Nonnull
  public OffsetDate plusMonths (final long months)
  {
    return with (m_aDate.plusMonths (months), m_aOffset);
  }

  /**
   * Returns a copy of this {@code OffsetDate} with the specified number of
   * weeks added.
   * <p>
   * This uses {@link LocalDate#plusWeeks(long)} to add the weeks. The offset
   * does not affect the calculation and will be the same in the result.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param weeks
   *        the weeks to add, may be negative
   * @return an {@code OffsetDate} based on this date with the weeks added, not
   *         null
   * @throws DateTimeException
   *         if the result exceeds the supported date range
   */
  @Nonnull
  public OffsetDate plusWeeks (final long weeks)
  {
    return with (m_aDate.plusWeeks (weeks), m_aOffset);
  }

  /**
   * Returns a copy of this {@code OffsetDate} with the specified number of days
   * added.
   * <p>
   * This uses {@link LocalDate#plusDays(long)} to add the days. The offset does
   * not affect the calculation and will be the same in the result.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param days
   *        the days to add, may be negative
   * @return an {@code OffsetDate} based on this date with the days added, not
   *         null
   * @throws DateTimeException
   *         if the result exceeds the supported date range
   */
  @Nonnull
  public OffsetDate plusDays (final long days)
  {
    return with (m_aDate.plusDays (days), m_aOffset);
  }

  // -----------------------------------------------------------------------
  /**
   * Returns a copy of this date with the specified amount subtracted.
   * <p>
   * This returns am {@code OffsetDate} based on this one, with the specified
   * amount subtracted. The amount is typically {@link Period} but may be any
   * other type implementing the {@link TemporalAmount} interface.
   * <p>
   * This uses {@link TemporalAmount#subtractFrom(Temporal)} to perform the
   * calculation.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param amountToSubtract
   *        the amount to subtract, not null
   * @return an {@code OffsetDate} based on this date with the subtraction made,
   *         not null
   * @throws DateTimeException
   *         if the subtraction cannot be made
   * @throws ArithmeticException
   *         if numeric overflow occurs
   */
  @Override
  @Nonnull
  public OffsetDate minus (@Nonnull final TemporalAmount amountToSubtract)
  {
    return (OffsetDate) amountToSubtract.subtractFrom (this);
  }

  /**
   * Returns a copy of this date with the specified amount subtracted.
   * <p>
   * This returns an {@code OffsetDate} based on this one, with the amount in
   * terms of the unit subtracted. If it is not possible to subtract the amount,
   * because the unit is not supported or for some other reason, an exception is
   * thrown.
   * <p>
   * This method is equivalent to {@link #plus(long, TemporalUnit)} with the
   * amount negated. See that method for a full description of how addition, and
   * thus subtraction, works.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param amountToSubtract
   *        the amount of the unit to subtract from the result, may be negative
   * @param unit
   *        the unit of the amount to subtract, not null
   * @return an {@code OffsetDate} based on this date with the specified amount
   *         subtracted, not null
   * @throws DateTimeException
   *         if the subtraction cannot be made
   * @throws UnsupportedTemporalTypeException
   *         if the unit is not supported
   * @throws ArithmeticException
   *         if numeric overflow occurs
   */
  @Override
  @Nonnull
  public OffsetDate minus (final long amountToSubtract, @Nonnull final TemporalUnit unit)
  {
    return amountToSubtract == Long.MIN_VALUE ? plus (Long.MAX_VALUE, unit).plus (1, unit) : plus (-amountToSubtract,
                                                                                                   unit);
  }

  // -----------------------------------------------------------------------
  /**
   * Returns a copy of this {@code OffsetDate} with the specified number of
   * years subtracted.
   * <p>
   * This uses {@link LocalDate#minusYears(long)} to subtract the years. The
   * offset does not affect the calculation and will be the same in the result.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param years
   *        the years to subtract, may be negative
   * @return an {@code OffsetDate} based on this date with the years subtracted,
   *         not null
   * @throws DateTimeException
   *         if the result exceeds the supported date range
   */
  @Nonnull
  public OffsetDate minusYears (final long years)
  {
    return with (m_aDate.minusYears (years), m_aOffset);
  }

  /**
   * Returns a copy of this {@code OffsetDate} with the specified number of
   * months subtracted.
   * <p>
   * This uses {@link LocalDate#minusMonths(long)} to subtract the months. The
   * offset does not affect the calculation and will be the same in the result.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param months
   *        the months to subtract, may be negative
   * @return an {@code OffsetDate} based on this date with the months
   *         subtracted, not null
   * @throws DateTimeException
   *         if the result exceeds the supported date range
   */
  @Nonnull
  public OffsetDate minusMonths (final long months)
  {
    return with (m_aDate.minusMonths (months), m_aOffset);
  }

  /**
   * Returns a copy of this {@code OffsetDate} with the specified number of
   * weeks subtracted.
   * <p>
   * This uses {@link LocalDate#minusWeeks(long)} to subtract the weeks. The
   * offset does not affect the calculation and will be the same in the result.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param weeks
   *        the weeks to subtract, may be negative
   * @return an {@code OffsetDate} based on this date with the weeks subtracted,
   *         not null
   * @throws DateTimeException
   *         if the result exceeds the supported date range
   */
  @Nonnull
  public OffsetDate minusWeeks (final long weeks)
  {
    return with (m_aDate.minusWeeks (weeks), m_aOffset);
  }

  /**
   * Returns a copy of this {@code OffsetDate} with the specified number of days
   * subtracted.
   * <p>
   * This uses {@link LocalDate#minusDays(long)} to subtract the days. The
   * offset does not affect the calculation and will be the same in the result.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param days
   *        the days to subtract, may be negative
   * @return an {@code OffsetDate} based on this date with the days subtracted,
   *         not null
   * @throws DateTimeException
   *         if the result exceeds the supported date range
   */
  @Nonnull
  public OffsetDate minusDays (final long days)
  {
    return with (m_aDate.minusDays (days), m_aOffset);
  }

  // -----------------------------------------------------------------------
  /**
   * Queries this date using the specified query.
   * <p>
   * This queries this date using the specified query strategy object. The
   * {@code TemporalQuery} object defines the logic to be used to obtain the
   * result. Read the documentation of the query to understand what the result
   * of this method will be.
   * <p>
   * The result of this method is obtained by invoking the
   * {@link TemporalQuery#queryFrom(TemporalAccessor)} method on the specified
   * query passing {@code this} as the argument.
   *
   * @param <R>
   *        the type of the result
   * @param query
   *        the query to invoke, not null
   * @return the query result, null may be returned (defined by the query)
   * @throws DateTimeException
   *         if unable to query (defined by the query)
   * @throws ArithmeticException
   *         if numeric overflow occurs (defined by the query)
   */
  @SuppressWarnings ("unchecked")
  @Override
  public <R> R query (@Nonnull final TemporalQuery <R> query)
  {
    if (query == TemporalQueries.chronology ())
      return (R) IsoChronology.INSTANCE;
    if (query == TemporalQueries.precision ())
      return (R) DAYS;
    if (query == TemporalQueries.offset () || query == TemporalQueries.zone ())
      return (R) getOffset ();
    return Temporal.super.query (query);
  }

  /**
   * Adjusts the specified temporal object to have the same offset and date as
   * this object.
   * <p>
   * This returns a temporal object of the same observable type as the input
   * with the offset and date changed to be the same as this.
   * <p>
   * The adjustment is equivalent to using
   * {@link Temporal#with(TemporalField, long)} twice, passing
   * {@link ChronoField#OFFSET_SECONDS} and {@link ChronoField#EPOCH_DAY} as the
   * fields.
   * <p>
   * In most cases, it is clearer to reverse the calling pattern by using
   * {@link Temporal#with(TemporalAdjuster)}:
   *
   * <pre>
   * // these two lines are equivalent, but the second approach is recommended
   * temporal = thisOffsetDate.adjustInto (temporal);
   * temporal = temporal.with (thisOffsetDate);
   * </pre>
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param temporal
   *        the target object to be adjusted, not null
   * @return the adjusted object, not null
   * @throws DateTimeException
   *         if unable to make the adjustment
   * @throws ArithmeticException
   *         if numeric overflow occurs
   */
  @Override
  public Temporal adjustInto (@Nonnull final Temporal temporal)
  {
    return temporal.with (OFFSET_SECONDS, getOffset ().getTotalSeconds ())
                   .with (EPOCH_DAY, toLocalDate ().toEpochDay ());
  }

  /**
   * Calculates the period between this date and another date in terms of the
   * specified unit.
   * <p>
   * This calculates the period between two dates in terms of a single unit. The
   * start and end points are {@code this} and the specified date. The result
   * will be negative if the end is before the start. For example, the period in
   * days between two dates can be calculated using
   * {@code startDate.until(endDate, DAYS)}.
   * <p>
   * The {@code Temporal} passed to this method is converted to a
   * {@code OffsetDate} using {@link #from(TemporalAccessor)}. If the offset
   * differs between the two times, then the specified end time is normalized to
   * have the same offset as this time.
   * <p>
   * The calculation returns a whole number, representing the number of complete
   * units between the two dates. For example, the period in months between
   * 2012-06-15Z and 2012-08-14Z will only be one month as it is one day short
   * of two months.
   * <p>
   * There are two equivalent ways of using this method. The first is to invoke
   * this method. The second is to use
   * {@link TemporalUnit#between(Temporal, Temporal)}:
   *
   * <pre>
   * // these two lines are equivalent
   * amount = start.until (end, DAYS);
   * amount = DAYS.between (start, end);
   * </pre>
   *
   * The choice should be made based on which makes the code more readable.
   * <p>
   * The calculation is implemented in this method for {@link ChronoUnit}. The
   * units {@code DAYS}, {@code WEEKS}, {@code MONTHS}, {@code YEARS},
   * {@code DECADES}, {@code CENTURIES}, {@code MILLENNIA} and {@code ERAS} are
   * supported. Other {@code ChronoUnit} values will throw an exception.
   * <p>
   * If the unit is not a {@code ChronoUnit}, then the result of this method is
   * obtained by invoking {@code TemporalUnit.between(Temporal, Temporal)}
   * passing {@code this} as the first argument and the converted input temporal
   * as the second argument.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param endExclusive
   *        the end time, exclusive, which is converted to an
   *        {@code OffsetDate}, not null
   * @param unit
   *        the unit to measure the amount in, not null
   * @return the amount of time between this date and the end date
   * @throws DateTimeException
   *         if the amount cannot be calculated, or the end temporal cannot be
   *         converted to an {@code OffsetDate}
   * @throws UnsupportedTemporalTypeException
   *         if the unit is not supported
   * @throws ArithmeticException
   *         if numeric overflow occurs
   */
  @Override
  public long until (@Nonnull final Temporal endExclusive, @Nonnull final TemporalUnit unit)
  {
    final OffsetDate end = OffsetDate.from (endExclusive);
    if (unit instanceof ChronoUnit)
    {
      final long offsetDiff = (long) end.m_aOffset.getTotalSeconds () - m_aOffset.getTotalSeconds ();
      final LocalDate endLocal = end.m_aDate.plusDays (Math.floorDiv (-offsetDiff, SECONDS_PER_DAY));
      return m_aDate.until (endLocal, unit);
    }
    return unit.between (this, end);
  }

  /**
   * Formats this date using the specified formatter.
   * <p>
   * This date will be passed to the formatter to produce a string.
   *
   * @param formatter
   *        the formatter to use, not null
   * @return the formatted date string, not null
   * @throws DateTimeException
   *         if an error occurs during printing
   */
  @Nonnull
  public String format (@Nonnull final DateTimeFormatter formatter)
  {
    ValueEnforcer.notNull (formatter, "formatter");
    return formatter.format (this);
  }

  /**
   * Returns an offset date-time formed from this date at the specified time.
   * <p>
   * This combines this date with the specified time to form an
   * {@code OffsetDateTime}. All possible combinations of date and time are
   * valid.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param aTime
   *        the time to combine with, not null
   * @return the offset date-time formed from this date and the specified time,
   *         not null
   */
  @Nonnull
  public OffsetDateTime atTime (@Nonnull final LocalTime aTime)
  {
    return OffsetDateTime.of (m_aDate, aTime, m_aOffset);
  }

  @Nonnull
  public XMLOffsetDateTime atTimeXML (@Nonnull final LocalTime aTime)
  {
    return XMLOffsetDateTime.of (m_aDate, aTime, m_aOffset);
  }

  /**
   * Converts this date to midnight at the start of day in epoch seconds.
   *
   * @return the epoch seconds value
   */
  private long toEpochSecond ()
  {
    final long epochDay = m_aDate.toEpochDay ();
    final long secs = epochDay * SECONDS_PER_DAY;
    return secs - m_aOffset.getTotalSeconds ();
  }

  /**
   * Converts this {@code OffsetDate} to the number of seconds since the epoch
   * of 1970-01-01T00:00:00Z.
   * <p>
   * This combines this offset date with the specified time to calculate the
   * epoch-second value, which is the number of elapsed seconds from
   * 1970-01-01T00:00:00Z. Instants on the time-line after the epoch are
   * positive, earlier are negative.
   *
   * @param time
   *        the local time, not null
   * @return the number of seconds since the epoch of 1970-01-01T00:00:00Z, may
   *         be negative
   */
  public long toEpochSecond (@Nonnull final LocalTime time)
  {
    ValueEnforcer.notNull (time, "time");
    return toEpochSecond () + time.toSecondOfDay ();
  }

  // -----------------------------------------------------------------------
  /**
   * Compares this {@code OffsetDate} to another date.
   * <p>
   * The comparison is based first on the UTC equivalent instant, then on the
   * local date. It is "consistent with equals", as defined by
   * {@link Comparable}.
   * <p>
   * For example, the following is the comparator order:
   * <ol>
   * <li>2008-06-29-11:00</li>
   * <li>2008-06-29-12:00</li>
   * <li>2008-06-30+12:00</li>
   * <li>2008-06-29-13:00</li>
   * </ol>
   * Values #2 and #3 represent the same instant on the time-line. When two
   * values represent the same instant, the local date is compared to
   * distinguish them. This step is needed to make the ordering consistent with
   * {@code equals()}.
   * <p>
   * To compare the underlying local date of two {@code TemporalAccessor}
   * instances, use {@link ChronoField#EPOCH_DAY} as a comparator.
   *
   * @param o
   *        the other date to compare to, not null
   * @return the comparator value, negative if less, positive if greater
   */
  @Override
  public int compareTo (@Nonnull final OffsetDate o)
  {
    if (m_aOffset.equals (o.m_aOffset))
      return m_aDate.compareTo (o.m_aDate);

    int ret = Long.compare (toEpochSecond (), o.toEpochSecond ());
    if (ret == 0)
      ret = m_aDate.compareTo (o.m_aDate);
    return ret;
  }

  // -----------------------------------------------------------------------
  /**
   * Checks if the instant of midnight at the start of this {@code OffsetDate}
   * is after midnight at the start of the specified date.
   * <p>
   * This method differs from the comparison in {@link #compareTo} in that it
   * only compares the instant of the date. This is equivalent to using
   * {@code date1.toEpochSecond().isAfter(date2.toEpochSecond())}.
   *
   * @param other
   *        the other date to compare to, not null
   * @return true if this is after the instant of the specified date
   */
  public boolean isAfter (@Nonnull final OffsetDate other)
  {
    return toEpochSecond () > other.toEpochSecond ();
  }

  /**
   * Checks if the instant of midnight at the start of this {@code OffsetDate}
   * is before midnight at the start of the specified date.
   * <p>
   * This method differs from the comparison in {@link #compareTo} in that it
   * only compares the instant of the date. This is equivalent to using
   * {@code date1.toEpochSecond().isBefore(date2.toEpochSecond())}.
   *
   * @param other
   *        the other date to compare to, not null
   * @return true if this is before the instant of the specified date
   */
  public boolean isBefore (@Nonnull final OffsetDate other)
  {
    return toEpochSecond () < other.toEpochSecond ();
  }

  /**
   * Checks if the instant of midnight at the start of this {@code OffsetDate}
   * equals midnight at the start of the specified date.
   * <p>
   * This method differs from the comparison in {@link #compareTo} and
   * {@link #equals} in that it only compares the instant of the date. This is
   * equivalent to using
   * {@code date1.toEpochSecond().equals(date2.toEpochSecond())}.
   *
   * @param other
   *        the other date to compare to, not null
   * @return true if the instant equals the instant of the specified date
   */
  public boolean isEqual (@Nonnull final OffsetDate other)
  {
    return toEpochSecond () == other.toEpochSecond ();
  }

  @Nonnull
  public XMLOffsetDate toXMLOffsetDate ()
  {
    return XMLOffsetDate.of (m_aDate, m_aOffset);
  }

  /**
   * Checks if this date is equal to another date.
   * <p>
   * The comparison is based on the local-date and the offset. To compare for
   * the same instant on the time-line, use {@link #isEqual(OffsetDate)}.
   * <p>
   * Only objects of type {@code OffsetDate} are compared, other types return
   * false. To compare the underlying local date of two {@code TemporalAccessor}
   * instances, use {@link ChronoField#EPOCH_DAY} as a comparator.
   *
   * @param o
   *        the object to check, null returns false
   * @return true if this is equal to the other date
   */
  @Override
  public boolean equals (final Object o)
  {
    if (this == o)
      return true;

    if (o == null || !getClass ().equals (o.getClass ()))
      return false;

    final OffsetDate other = (OffsetDate) o;
    return m_aDate.equals (other.m_aDate) && m_aOffset.equals (other.m_aOffset);
  }

  /**
   * A hash code for this date.
   *
   * @return a suitable hash code
   */
  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aDate).append (m_aOffset).getHashCode ();
  }

  // Don't use "getAsString" for compatibility with the rest of the Java DT API
  @Nonnull
  @Nonempty
  @Deprecated (forRemoval = false)
  public String getAsString ()
  {
    return toString ();
  }

  @Override
  public String toString ()
  {
    return m_aDate.toString () + m_aOffset.toString ();
  }
}
