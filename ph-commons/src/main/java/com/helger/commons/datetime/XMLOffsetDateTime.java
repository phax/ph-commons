/**
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
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
import static java.time.temporal.ChronoField.INSTANT_SECONDS;
import static java.time.temporal.ChronoField.NANO_OF_DAY;
import static java.time.temporal.ChronoField.OFFSET_SECONDS;
import static java.time.temporal.ChronoUnit.FOREVER;
import static java.time.temporal.ChronoUnit.NANOS;

import java.io.Serializable;
import java.time.Clock;
import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalQuery;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;

/**
 * A version of {@link OffsetDateTime} that has an optional {@link ZoneOffset}
 *
 * @since 10.1
 */
@Immutable
public class XMLOffsetDateTime implements Temporal, TemporalAdjuster, Comparable <XMLOffsetDateTime>, Serializable
{
  /**
   * The minimum supported {@code XMLOffsetDateTime},
   * '-999999999-01-01T00:00:00+18:00'. This is the local date-time of midnight
   * at the start of the minimum date in the maximum offset (larger offsets are
   * earlier on the time-line). This combines {@link LocalDateTime#MIN} and
   * {@link ZoneOffset#MAX}. This could be used by an application as a "far
   * past" date-time.
   */
  public static final XMLOffsetDateTime MIN = XMLOffsetDateTime.of (LocalDateTime.MIN, ZoneOffset.MAX);
  /**
   * The maximum supported {@code XMLOffsetDateTime},
   * '+999999999-12-31T23:59:59.999999999-18:00'. This is the local date-time
   * just before midnight at the end of the maximum date in the minimum offset
   * (larger negative offsets are later on the time-line). This combines
   * {@link LocalDateTime#MAX} and {@link ZoneOffset#MIN}. This could be used by
   * an application as a "far future" date-time.
   */
  public static final XMLOffsetDateTime MAX = XMLOffsetDateTime.of (LocalDateTime.MAX, ZoneOffset.MIN);

  /**
   * Gets a comparator that compares two {@code XMLOffsetDateTime} instances
   * based solely on the instant.
   * <p>
   * This method differs from the comparison in {@link #compareTo} in that it
   * only compares the underlying instant.
   *
   * @return a comparator that compares in time-line order
   * @see #isAfter
   * @see #isBefore
   * @see #isEqual
   */
  public static Comparator <XMLOffsetDateTime> timeLineOrder ()
  {
    return XMLOffsetDateTime::compareInstant;
  }

  /**
   * Compares this {@code XMLOffsetDateTime} to another date-time. The
   * comparison is based on the instant.
   *
   * @param datetime1
   *        the first date-time to compare, not null
   * @param datetime2
   *        the other date-time to compare to, not null
   * @return the comparator value, negative if less, positive if greater
   */
  protected static int compareInstant (@Nonnull final XMLOffsetDateTime datetime1, @Nonnull final XMLOffsetDateTime datetime2)
  {
    if (EqualsHelper.equals (datetime1.m_aOffset, datetime2.m_aOffset))
      return datetime1.toLocalDateTime ().compareTo (datetime2.toLocalDateTime ());

    int ret = Long.compare (datetime1.toEpochSecond (), datetime2.toEpochSecond ());
    if (ret == 0)
      ret = datetime1.toLocalTime ().getNano () - datetime2.toLocalTime ().getNano ();
    return ret;
  }

  /**
   * The local date-time.
   */
  private final LocalDateTime m_aDateTime;
  /**
   * The offset from UTC/Greenwich.
   */
  private final ZoneOffset m_aOffset;

  /**
   * Obtains the current date-time from the system clock in the default
   * time-zone.
   * <p>
   * This will query the {@link Clock#systemDefaultZone() system clock} in the
   * default time-zone to obtain the current date-time. The offset will be
   * calculated from the time-zone in the clock.
   * <p>
   * Using this method will prevent the ability to use an alternate clock for
   * testing because the clock is hard-coded.
   *
   * @return the current date-time using the system clock, not null
   */
  @Nonnull
  public static XMLOffsetDateTime now ()
  {
    return now (Clock.systemDefaultZone ());
  }

  /**
   * Obtains the current date-time from the system clock in the specified
   * time-zone.
   * <p>
   * This will query the {@link Clock#system(ZoneId) system clock} to obtain the
   * current date-time. Specifying the time-zone avoids dependence on the
   * default time-zone. The offset will be calculated from the specified
   * time-zone.
   * <p>
   * Using this method will prevent the ability to use an alternate clock for
   * testing because the clock is hard-coded.
   *
   * @param zone
   *        the zone ID to use, not null
   * @return the current date-time using the system clock, not null
   */
  @Nonnull
  public static XMLOffsetDateTime now (@Nonnull final ZoneId zone)
  {
    return now (Clock.system (zone));
  }

  /**
   * Obtains the current date-time from the specified clock.
   * <p>
   * This will query the specified clock to obtain the current date-time. The
   * offset will be calculated from the time-zone in the clock.
   * <p>
   * Using this method allows the use of an alternate clock for testing. The
   * alternate clock may be introduced using {@link Clock dependency injection}.
   *
   * @param clock
   *        the clock to use, not null
   * @return the current date-time, not null
   */
  @Nonnull
  public static XMLOffsetDateTime now (@Nonnull final Clock clock)
  {
    Objects.requireNonNull (clock, "clock");
    final Instant now = clock.instant ();
    return ofInstant (now, clock.getZone ().getRules ().getOffset (now));
  }

  /**
   * Obtains an instance of {@code XMLOffsetDateTime} from a date, time and
   * offset.
   * <p>
   * This creates an offset date-time with the specified local date, time and
   * offset.
   *
   * @param date
   *        the local date, not null
   * @param time
   *        the local time, not null
   * @param offset
   *        the zone offset, not null
   * @return the offset date-time, not null
   */
  @Nonnull
  public static XMLOffsetDateTime of (@Nonnull final LocalDate date, @Nonnull final LocalTime time, @Nullable final ZoneOffset offset)
  {
    final LocalDateTime dt = LocalDateTime.of (date, time);
    return new XMLOffsetDateTime (dt, offset);
  }

  /**
   * Obtains an instance of {@code XMLOffsetDateTime} from a date-time and
   * offset.
   * <p>
   * This creates an offset date-time with the specified local date-time and
   * offset.
   *
   * @param dateTime
   *        the local date-time, not null
   * @param offset
   *        the zone offset, not null
   * @return the offset date-time, may be null
   */
  @Nonnull
  public static XMLOffsetDateTime of (@Nonnull final LocalDateTime dateTime, @Nullable final ZoneOffset offset)
  {
    return new XMLOffsetDateTime (dateTime, offset);
  }

  @Nonnull
  public static XMLOffsetDateTime of (@Nonnull final OffsetDateTime ofsDateTime)
  {
    return new XMLOffsetDateTime (ofsDateTime.toLocalDateTime (), ofsDateTime.getOffset ());
  }

  /**
   * Obtains an instance of {@code XMLOffsetDateTime} from a year, month, day,
   * hour, minute, second, nanosecond and offset.
   * <p>
   * This creates an offset date-time with the seven specified fields.
   * <p>
   * This method exists primarily for writing test cases. Non test-code will
   * typically use other methods to create an offset time. {@code LocalDateTime}
   * has five additional convenience variants of the equivalent factory method
   * taking fewer arguments. They are not provided here to reduce the footprint
   * of the API.
   *
   * @param year
   *        the year to represent, from MIN_YEAR to MAX_YEAR
   * @param month
   *        the month-of-year to represent, from 1 (January) to 12 (December)
   * @param dayOfMonth
   *        the day-of-month to represent, from 1 to 31
   * @param hour
   *        the hour-of-day to represent, from 0 to 23
   * @param minute
   *        the minute-of-hour to represent, from 0 to 59
   * @param second
   *        the second-of-minute to represent, from 0 to 59
   * @param nanoOfSecond
   *        the nano-of-second to represent, from 0 to 999,999,999
   * @param offset
   *        the zone offset, not null
   * @return the offset date-time, not null
   * @throws DateTimeException
   *         if the value of any field is out of range, or if the day-of-month
   *         is invalid for the month-year
   */
  @Nonnull
  public static XMLOffsetDateTime of (final int year,
                                      final int month,
                                      final int dayOfMonth,
                                      final int hour,
                                      final int minute,
                                      final int second,
                                      final int nanoOfSecond,
                                      @Nullable final ZoneOffset offset)
  {
    final LocalDateTime dt = LocalDateTime.of (year, month, dayOfMonth, hour, minute, second, nanoOfSecond);
    return new XMLOffsetDateTime (dt, offset);
  }

  /**
   * Obtains an instance of {@code XMLOffsetDateTime} from an {@code Instant}
   * and zone ID.
   * <p>
   * This creates an offset date-time with the same instant as that specified.
   * Finding the offset from UTC/Greenwich is simple as there is only one valid
   * offset for each instant.
   *
   * @param instant
   *        the instant to create the date-time from, not null
   * @param zone
   *        the time-zone, which may be an offset, not null
   * @return the offset date-time, not null
   * @throws DateTimeException
   *         if the result exceeds the supported range
   */
  @Nonnull
  public static XMLOffsetDateTime ofInstant (@Nonnull final Instant instant, @Nonnull final ZoneId zone)
  {
    Objects.requireNonNull (instant, "instant");
    Objects.requireNonNull (zone, "zone");
    final ZoneOffset offset = zone.getRules ().getOffset (instant);
    final LocalDateTime ldt = LocalDateTime.ofEpochSecond (instant.getEpochSecond (), instant.getNano (), offset);
    return new XMLOffsetDateTime (ldt, offset);
  }

  /**
   * Obtains an instance of {@code XMLOffsetDateTime} from a temporal object.
   * <p>
   * This obtains an offset date-time based on the specified temporal. A
   * {@code TemporalAccessor} represents an arbitrary set of date and time
   * information, which this factory converts to an instance of
   * {@code XMLOffsetDateTime}.
   * <p>
   * The conversion will first obtain a {@code ZoneOffset} from the temporal
   * object. It will then try to obtain a {@code LocalDateTime}, falling back to
   * an {@code Instant} if necessary. The result will be the combination of
   * {@code ZoneOffset} with either with {@code LocalDateTime} or
   * {@code Instant}. Implementations are permitted to perform optimizations
   * such as accessing those fields that are equivalent to the relevant objects.
   * <p>
   * This method matches the signature of the functional interface
   * {@link TemporalQuery} allowing it to be used as a query via method
   * reference, {@code XMLOffsetDateTime::from}.
   *
   * @param temporal
   *        the temporal object to convert, not null
   * @return the offset date-time, not null
   * @throws DateTimeException
   *         if unable to convert to an {@code XMLOffsetDateTime}
   */
  @Nonnull
  public static XMLOffsetDateTime from (@Nonnull final TemporalAccessor temporal)
  {
    if (temporal instanceof XMLOffsetDateTime)
      return (XMLOffsetDateTime) temporal;

    try
    {
      final ZoneOffset offset = ZoneOffset.from (temporal);
      final LocalDate date = temporal.query (TemporalQueries.localDate ());
      final LocalTime time = temporal.query (TemporalQueries.localTime ());
      if (date != null && time != null)
        return XMLOffsetDateTime.of (date, time, offset);

      final Instant instant = Instant.from (temporal);
      return XMLOffsetDateTime.ofInstant (instant, offset);
    }
    catch (final DateTimeException ex)
    {
      throw new DateTimeException ("Unable to obtain XMLOffsetDateTime from TemporalAccessor: " +
                                   temporal +
                                   " of type " +
                                   temporal.getClass ().getName (),
                                   ex);
    }
  }

  /**
   * Obtains an instance of {@code XMLOffsetDateTime} from a text string such as
   * {@code 2007-12-03T10:15:30+01:00}.
   * <p>
   * The string must represent a valid date-time and is parsed using
   * {@link java.time.format.DateTimeFormatter#ISO_DATE_TIME}.
   *
   * @param text
   *        the text to parse such as "2007-12-03T10:15:30+01:00", not null
   * @return the parsed offset date-time, not null
   * @throws DateTimeParseException
   *         if the text cannot be parsed
   */
  @Nonnull
  public static XMLOffsetDateTime parse (@Nonnull final CharSequence text)
  {
    // Optional ZoneID
    return parse (text, DateTimeFormatter.ISO_DATE_TIME);
  }

  /**
   * Obtains an instance of {@code XMLOffsetDateTime} from a text string using a
   * specific formatter.
   * <p>
   * The text is parsed using the formatter, returning a date-time.
   *
   * @param text
   *        the text to parse, not null
   * @param formatter
   *        the formatter to use, not null
   * @return the parsed offset date-time, not null
   * @throws DateTimeParseException
   *         if the text cannot be parsed
   */
  @Nonnull
  public static XMLOffsetDateTime parse (@Nonnull final CharSequence text, @Nonnull final DateTimeFormatter formatter)
  {
    Objects.requireNonNull (formatter, "formatter");
    return formatter.parse (text, XMLOffsetDateTime::from);
  }

  /**
   * Constructor.
   *
   * @param dateTime
   *        the local date-time, not null
   * @param offset
   *        the zone offset, not null
   */
  protected XMLOffsetDateTime (@Nonnull final LocalDateTime dateTime, @Nullable final ZoneOffset offset)
  {
    m_aDateTime = Objects.requireNonNull (dateTime, "dateTime");
    m_aOffset = offset;
  }

  /**
   * Returns a new date-time based on this one, returning {@code this} where
   * possible.
   *
   * @param dateTime
   *        the date-time to create with, not null
   * @param offset
   *        the zone offset to create with, not null
   */
  @Nonnull
  protected XMLOffsetDateTime with (@Nonnull final LocalDateTime dateTime, @Nullable final ZoneOffset offset)
  {
    if (m_aDateTime == dateTime && EqualsHelper.equals (m_aOffset, offset))
      return this;

    return new XMLOffsetDateTime (dateTime, offset);
  }

  /**
   * Checks if the specified field is supported.
   * <p>
   * This checks if this date-time can be queried for the specified field. If
   * false, then calling the {@link #range(TemporalField) range},
   * {@link #get(TemporalField) get} and {@link #with(TemporalField, long)}
   * methods will throw an exception.
   * <p>
   * If the field is a {@link ChronoField} then the query is implemented here.
   * The supported fields are:
   * <ul>
   * <li>{@code NANO_OF_SECOND}
   * <li>{@code NANO_OF_DAY}
   * <li>{@code MICRO_OF_SECOND}
   * <li>{@code MICRO_OF_DAY}
   * <li>{@code MILLI_OF_SECOND}
   * <li>{@code MILLI_OF_DAY}
   * <li>{@code SECOND_OF_MINUTE}
   * <li>{@code SECOND_OF_DAY}
   * <li>{@code MINUTE_OF_HOUR}
   * <li>{@code MINUTE_OF_DAY}
   * <li>{@code HOUR_OF_AMPM}
   * <li>{@code CLOCK_HOUR_OF_AMPM}
   * <li>{@code HOUR_OF_DAY}
   * <li>{@code CLOCK_HOUR_OF_DAY}
   * <li>{@code AMPM_OF_DAY}
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
   * <li>{@code INSTANT_SECONDS}
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
   * @return true if the field is supported on this date-time, false if not
   */
  @Override
  public boolean isSupported (@Nullable final TemporalField field)
  {
    return field instanceof ChronoField || (field != null && field.isSupportedBy (this));
  }

  /**
   * Checks if the specified unit is supported.
   * <p>
   * This checks if the specified unit can be added to, or subtracted from, this
   * date-time. If false, then calling the {@link #plus(long, TemporalUnit)} and
   * {@link #minus(long, TemporalUnit) minus} methods will throw an exception.
   * <p>
   * If the unit is a {@link ChronoUnit} then the query is implemented here. The
   * supported units are:
   * <ul>
   * <li>{@code NANOS}
   * <li>{@code MICROS}
   * <li>{@code MILLIS}
   * <li>{@code SECONDS}
   * <li>{@code MINUTES}
   * <li>{@code HOURS}
   * <li>{@code HALF_DAYS}
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
  @Override // override for Javadoc
  public boolean isSupported (@Nullable final TemporalUnit unit)
  {
    if (unit instanceof ChronoUnit)
      return unit != FOREVER;

    return unit != null && unit.isSupportedBy (this);
  }

  /**
   * Gets the range of valid values for the specified field.
   * <p>
   * The range object expresses the minimum and maximum valid values for a
   * field. This date-time is used to enhance the accuracy of the returned
   * range. If it is not possible to return the range, because the field is not
   * supported or for some other reason, an exception is thrown.
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
  @Nonnull
  public ValueRange range (@Nonnull final TemporalField field)
  {
    if (field instanceof ChronoField)
    {
      if (field == INSTANT_SECONDS || field == OFFSET_SECONDS)
        return field.range ();
      return m_aDateTime.range (field);
    }
    return field.rangeRefinedBy (this);
  }

  /**
   * Gets the value of the specified field from this date-time as an
   * {@code int}.
   * <p>
   * This queries this date-time for the value of the specified field. The
   * returned value will always be within the valid range of values for the
   * field. If it is not possible to return the value, because the field is not
   * supported or for some other reason, an exception is thrown.
   * <p>
   * If the field is a {@link ChronoField} then the query is implemented here.
   * The {@link #isSupported(TemporalField) supported fields} will return valid
   * values based on this date-time, except {@code NANO_OF_DAY},
   * {@code MICRO_OF_DAY}, {@code EPOCH_DAY}, {@code PROLEPTIC_MONTH} and
   * {@code INSTANT_SECONDS} which are too large to fit in an {@code int} and
   * throw a {@code DateTimeException}. All other {@code ChronoField} instances
   * will throw an {@code UnsupportedTemporalTypeException}.
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
  @Override
  public int get (@Nonnull final TemporalField field)
  {
    if (field instanceof ChronoField)
    {
      switch ((ChronoField) field)
      {
        case INSTANT_SECONDS:
          throw new UnsupportedTemporalTypeException ("Invalid field 'InstantSeconds' for get() method, use getLong() instead");
        case OFFSET_SECONDS:
          return m_aOffset == null ? 0 : m_aOffset.getTotalSeconds ();
      }
      return m_aDateTime.get (field);
    }
    return Temporal.super.get (field);
  }

  /**
   * Gets the value of the specified field from this date-time as a
   * {@code long}.
   * <p>
   * This queries this date-time for the value of the specified field. If it is
   * not possible to return the value, because the field is not supported or for
   * some other reason, an exception is thrown.
   * <p>
   * If the field is a {@link ChronoField} then the query is implemented here.
   * The {@link #isSupported(TemporalField) supported fields} will return valid
   * values based on this date-time. All other {@code ChronoField} instances
   * will throw an {@code UnsupportedTemporalTypeException}.
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
      switch ((ChronoField) field)
      {
        case INSTANT_SECONDS:
          return toEpochSecond ();
        case OFFSET_SECONDS:
          return m_aOffset != null ? m_aOffset.getTotalSeconds () : 0;
      }
      return m_aDateTime.getLong (field);
    }
    return field.getFrom (this);
  }

  /**
   * Gets the zone offset, such as '+01:00'.
   * <p>
   * This is the offset of the local date-time from UTC/Greenwich.
   *
   * @return the zone offset, may be null
   */
  @Nullable
  public ZoneOffset getOffset ()
  {
    return m_aOffset;
  }

  /**
   * @return <code>true</code> if this date has a zone offset,
   *         <code>false</code> if not.
   */
  public boolean hasOffset ()
  {
    return m_aOffset != null;
  }

  /**
   * Returns a copy of this {@code XMLOffsetDateTime} with the specified offset
   * ensuring that the result has the same local date-time.
   * <p>
   * This method returns an object with the same {@code LocalDateTime} and the
   * specified {@code ZoneOffset}. No calculation is needed or performed. For
   * example, if this time represents {@code 2007-12-03T10:30+02:00} and the
   * offset specified is {@code +03:00}, then this method will return
   * {@code 2007-12-03T10:30+03:00}.
   * <p>
   * To take into account the difference between the offsets, and adjust the
   * time fields, use {@link #withOffsetSameInstant}.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param offset
   *        the zone offset to change to, not null
   * @return an {@code XMLOffsetDateTime} based on this date-time with the
   *         requested offset, not null
   */
  @Nonnull
  public XMLOffsetDateTime withOffsetSameLocal (@Nullable final ZoneOffset offset)
  {
    return with (m_aDateTime, offset);
  }

  /**
   * Returns a copy of this {@code XMLOffsetDateTime} with the specified offset
   * ensuring that the result is at the same instant.
   * <p>
   * This method returns an object with the specified {@code ZoneOffset} and a
   * {@code LocalDateTime} adjusted by the difference between the two offsets.
   * This will result in the old and new objects representing the same instant.
   * This is useful for finding the local time in a different offset. For
   * example, if this time represents {@code 2007-12-03T10:30+02:00} and the
   * offset specified is {@code +03:00}, then this method will return
   * {@code 2007-12-03T11:30+03:00}.
   * <p>
   * To change the offset without adjusting the local time use
   * {@link #withOffsetSameLocal}.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param offset
   *        the zone offset to change to, not null
   * @return an {@code XMLOffsetDateTime} based on this date-time with the
   *         requested offset, not null
   * @throws DateTimeException
   *         if the result exceeds the supported date range
   */
  @Nonnull
  public XMLOffsetDateTime withOffsetSameInstant (@Nullable final ZoneOffset offset)
  {
    if (EqualsHelper.equals (offset, m_aOffset))
      return this;

    final int difference = (offset != null ? offset.getTotalSeconds () : 0) - (m_aOffset != null ? m_aOffset.getTotalSeconds () : 0);
    final LocalDateTime adjusted = m_aDateTime.plusSeconds (difference);
    return new XMLOffsetDateTime (adjusted, offset);
  }

  /**
   * Gets the {@code LocalDateTime} part of this date-time.
   * <p>
   * This returns a {@code LocalDateTime} with the same year, month, day and
   * time as this date-time.
   *
   * @return the local date-time part of this date-time, not null
   */
  @Nonnull
  public LocalDateTime toLocalDateTime ()
  {
    return m_aDateTime;
  }

  /**
   * Gets the {@code LocalDate} part of this date-time.
   * <p>
   * This returns a {@code LocalDate} with the same year, month and day as this
   * date-time.
   *
   * @return the date part of this date-time, not null
   */
  @Nonnull
  public LocalDate toLocalDate ()
  {
    return m_aDateTime.toLocalDate ();
  }

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
    return m_aDateTime.getYear ();
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
    return m_aDateTime.getMonthValue ();
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
    return m_aDateTime.getMonth ();
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
    return m_aDateTime.getDayOfMonth ();
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
    return m_aDateTime.getDayOfYear ();
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
    return m_aDateTime.getDayOfWeek ();
  }

  /**
   * Gets the {@code LocalTime} part of this date-time.
   * <p>
   * This returns a {@code LocalTime} with the same hour, minute, second and
   * nanosecond as this date-time.
   *
   * @return the time part of this date-time, not null
   */
  @Nonnull
  public LocalTime toLocalTime ()
  {
    return m_aDateTime.toLocalTime ();
  }

  /**
   * Gets the hour-of-day field.
   *
   * @return the hour-of-day, from 0 to 23
   */
  public int getHour ()
  {
    return m_aDateTime.getHour ();
  }

  /**
   * Gets the minute-of-hour field.
   *
   * @return the minute-of-hour, from 0 to 59
   */
  public int getMinute ()
  {
    return m_aDateTime.getMinute ();
  }

  /**
   * Gets the second-of-minute field.
   *
   * @return the second-of-minute, from 0 to 59
   */
  public int getSecond ()
  {
    return m_aDateTime.getSecond ();
  }

  /**
   * Gets the nano-of-second field.
   *
   * @return the nano-of-second, from 0 to 999,999,999
   */
  public int getNano ()
  {
    return m_aDateTime.getNano ();
  }

  /**
   * Returns an adjusted copy of this date-time.
   * <p>
   * This returns an {@code XMLOffsetDateTime}, based on this one, with the
   * date-time adjusted. The adjustment takes place using the specified adjuster
   * strategy object. Read the documentation of the adjuster to understand what
   * adjustment will be made.
   * <p>
   * A simple adjuster might simply set the one of the fields, such as the year
   * field. A more complex adjuster might set the date to the last day of the
   * month. A selection of common adjustments is provided in
   * {@link java.time.temporal.TemporalAdjusters TemporalAdjusters}. These
   * include finding the "last day of the month" and "next Wednesday". Key
   * date-time classes also implement the {@code TemporalAdjuster} interface,
   * such as {@link Month} and {@link java.time.MonthDay MonthDay}. The adjuster
   * is responsible for handling special cases, such as the varying lengths of
   * month and leap years.
   * <p>
   * For example this code returns a date on the last day of July:
   *
   * <pre>
   *  import static java.time.Month.*;
   *  import static java.time.temporal.TemporalAdjusters.*;
   *
   *  result = offsetDateTime.with(JULY).with(lastDayOfMonth());
   * </pre>
   * <p>
   * The classes {@link LocalDate}, {@link LocalTime} and {@link ZoneOffset}
   * implement {@code TemporalAdjuster}, thus this method can be used to change
   * the date, time or offset:
   *
   * <pre>
   * result = offsetDateTime.with (date);
   * result = offsetDateTime.with (time);
   * result = offsetDateTime.with (offset);
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
   * @return an {@code XMLOffsetDateTime} based on {@code this} with the
   *         adjustment made, not null
   * @throws DateTimeException
   *         if the adjustment cannot be made
   * @throws ArithmeticException
   *         if numeric overflow occurs
   */
  @Override
  @Nonnull
  public XMLOffsetDateTime with (@Nonnull final TemporalAdjuster adjuster)
  {
    // optimizations
    if (adjuster instanceof LocalDate || adjuster instanceof LocalTime || adjuster instanceof LocalDateTime)
      return with (m_aDateTime.with (adjuster), m_aOffset);

    if (adjuster instanceof Instant)
      return ofInstant ((Instant) adjuster, m_aOffset);

    if (adjuster instanceof ZoneOffset)
      return with (m_aDateTime, (ZoneOffset) adjuster);

    if (adjuster instanceof XMLOffsetDateTime)
      return (XMLOffsetDateTime) adjuster;

    return (XMLOffsetDateTime) adjuster.adjustInto (this);
  }

  /**
   * Returns a copy of this date-time with the specified field set to a new
   * value.
   * <p>
   * This returns an {@code XMLOffsetDateTime}, based on this one, with the
   * value for the specified field changed. This can be used to change any
   * supported field, such as the year, month or day-of-month. If it is not
   * possible to set the value, because the field is not supported or for some
   * other reason, an exception is thrown.
   * <p>
   * In some cases, changing the specified field can cause the resulting
   * date-time to become invalid, such as changing the month from 31st January
   * to February would make the day-of-month invalid. In cases like this, the
   * field is responsible for resolving the date. Typically it will choose the
   * previous valid date, which would be the last valid day of February in this
   * example.
   * <p>
   * If the field is a {@link ChronoField} then the adjustment is implemented
   * here.
   * <p>
   * The {@code INSTANT_SECONDS} field will return a date-time with the
   * specified instant. The offset and nano-of-second are unchanged. If the new
   * instant value is outside the valid range then a {@code DateTimeException}
   * will be thrown.
   * <p>
   * The {@code OFFSET_SECONDS} field will return a date-time with the specified
   * offset. The local date-time is unaltered. If the new offset value is
   * outside the valid range then a {@code DateTimeException} will be thrown.
   * <p>
   * The other {@link #isSupported(TemporalField) supported fields} will behave
   * as per the matching method on
   * {@link LocalDateTime#with(TemporalField, long) LocalDateTime}. In this
   * case, the offset is not part of the calculation and will be unchanged.
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
   * @return an {@code XMLOffsetDateTime} based on {@code this} with the
   *         specified field set, not null
   * @throws DateTimeException
   *         if the field cannot be set
   * @throws UnsupportedTemporalTypeException
   *         if the field is not supported
   * @throws ArithmeticException
   *         if numeric overflow occurs
   */
  @Override
  @Nonnull
  public XMLOffsetDateTime with (@Nonnull final TemporalField field, final long newValue)
  {
    if (field instanceof ChronoField)
    {
      final ChronoField f = (ChronoField) field;
      switch (f)
      {
        case INSTANT_SECONDS:
          return ofInstant (Instant.ofEpochSecond (newValue, getNano ()), m_aOffset);
        case OFFSET_SECONDS:
          return with (m_aDateTime, ZoneOffset.ofTotalSeconds (f.checkValidIntValue (newValue)));
      }
      return with (m_aDateTime.with (field, newValue), m_aOffset);
    }
    return field.adjustInto (this, newValue);
  }

  /**
   * Returns a copy of this {@code XMLOffsetDateTime} with the year altered.
   * <p>
   * The time and offset do not affect the calculation and will be the same in
   * the result. If the day-of-month is invalid for the year, it will be changed
   * to the last valid day of the month.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param year
   *        the year to set in the result, from MIN_YEAR to MAX_YEAR
   * @return an {@code XMLOffsetDateTime} based on this date-time with the
   *         requested year, not null
   * @throws DateTimeException
   *         if the year value is invalid
   */
  @Nonnull
  public XMLOffsetDateTime withYear (final int year)
  {
    return with (m_aDateTime.withYear (year), m_aOffset);
  }

  /**
   * Returns a copy of this {@code XMLOffsetDateTime} with the month-of-year
   * altered.
   * <p>
   * The time and offset do not affect the calculation and will be the same in
   * the result. If the day-of-month is invalid for the year, it will be changed
   * to the last valid day of the month.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param month
   *        the month-of-year to set in the result, from 1 (January) to 12
   *        (December)
   * @return an {@code XMLOffsetDateTime} based on this date-time with the
   *         requested month, not null
   * @throws DateTimeException
   *         if the month-of-year value is invalid
   */
  @Nonnull
  public XMLOffsetDateTime withMonth (final int month)
  {
    return with (m_aDateTime.withMonth (month), m_aOffset);
  }

  /**
   * Returns a copy of this {@code XMLOffsetDateTime} with the day-of-month
   * altered.
   * <p>
   * If the resulting {@code XMLOffsetDateTime} is invalid, an exception is
   * thrown. The time and offset do not affect the calculation and will be the
   * same in the result.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param dayOfMonth
   *        the day-of-month to set in the result, from 1 to 28-31
   * @return an {@code XMLOffsetDateTime} based on this date-time with the
   *         requested day, not null
   * @throws DateTimeException
   *         if the day-of-month value is invalid, or if the day-of-month is
   *         invalid for the month-year
   */
  @Nonnull
  public XMLOffsetDateTime withDayOfMonth (final int dayOfMonth)
  {
    return with (m_aDateTime.withDayOfMonth (dayOfMonth), m_aOffset);
  }

  /**
   * Returns a copy of this {@code XMLOffsetDateTime} with the day-of-year
   * altered.
   * <p>
   * The time and offset do not affect the calculation and will be the same in
   * the result. If the resulting {@code XMLOffsetDateTime} is invalid, an
   * exception is thrown.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param dayOfYear
   *        the day-of-year to set in the result, from 1 to 365-366
   * @return an {@code XMLOffsetDateTime} based on this date with the requested
   *         day, not null
   * @throws DateTimeException
   *         if the day-of-year value is invalid, or if the day-of-year is
   *         invalid for the year
   */
  @Nonnull
  public XMLOffsetDateTime withDayOfYear (final int dayOfYear)
  {
    return with (m_aDateTime.withDayOfYear (dayOfYear), m_aOffset);
  }

  /**
   * Returns a copy of this {@code XMLOffsetDateTime} with the hour-of-day
   * altered.
   * <p>
   * The date and offset do not affect the calculation and will be the same in
   * the result.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param hour
   *        the hour-of-day to set in the result, from 0 to 23
   * @return an {@code XMLOffsetDateTime} based on this date-time with the
   *         requested hour, not null
   * @throws DateTimeException
   *         if the hour value is invalid
   */
  @Nonnull
  public XMLOffsetDateTime withHour (final int hour)
  {
    return with (m_aDateTime.withHour (hour), m_aOffset);
  }

  /**
   * Returns a copy of this {@code XMLOffsetDateTime} with the minute-of-hour
   * altered.
   * <p>
   * The date and offset do not affect the calculation and will be the same in
   * the result.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param minute
   *        the minute-of-hour to set in the result, from 0 to 59
   * @return an {@code XMLOffsetDateTime} based on this date-time with the
   *         requested minute, not null
   * @throws DateTimeException
   *         if the minute value is invalid
   */
  @Nonnull
  public XMLOffsetDateTime withMinute (final int minute)
  {
    return with (m_aDateTime.withMinute (minute), m_aOffset);
  }

  /**
   * Returns a copy of this {@code XMLOffsetDateTime} with the second-of-minute
   * altered.
   * <p>
   * The date and offset do not affect the calculation and will be the same in
   * the result.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param second
   *        the second-of-minute to set in the result, from 0 to 59
   * @return an {@code XMLOffsetDateTime} based on this date-time with the
   *         requested second, not null
   * @throws DateTimeException
   *         if the second value is invalid
   */
  @Nonnull
  public XMLOffsetDateTime withSecond (final int second)
  {
    return with (m_aDateTime.withSecond (second), m_aOffset);
  }

  /**
   * Returns a copy of this {@code XMLOffsetDateTime} with the nano-of-second
   * altered.
   * <p>
   * The date and offset do not affect the calculation and will be the same in
   * the result.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param nanoOfSecond
   *        the nano-of-second to set in the result, from 0 to 999,999,999
   * @return an {@code XMLOffsetDateTime} based on this date-time with the
   *         requested nanosecond, not null
   * @throws DateTimeException
   *         if the nano value is invalid
   */
  @Nonnull
  public XMLOffsetDateTime withNano (final int nanoOfSecond)
  {
    return with (m_aDateTime.withNano (nanoOfSecond), m_aOffset);
  }

  /**
   * Returns a copy of this {@code XMLOffsetDateTime} with the time truncated.
   * <p>
   * Truncation returns a copy of the original date-time with fields smaller
   * than the specified unit set to zero. For example, truncating with the
   * {@link ChronoUnit#MINUTES minutes} unit will set the second-of-minute and
   * nano-of-second field to zero.
   * <p>
   * The unit must have a {@linkplain TemporalUnit#getDuration() duration} that
   * divides into the length of a standard day without remainder. This includes
   * all supplied time units on {@link ChronoUnit} and {@link ChronoUnit#DAYS
   * DAYS}. Other units throw an exception.
   * <p>
   * The offset does not affect the calculation and will be the same in the
   * result.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param unit
   *        the unit to truncate to, not null
   * @return an {@code XMLOffsetDateTime} based on this date-time with the time
   *         truncated, not null
   * @throws DateTimeException
   *         if unable to truncate
   * @throws UnsupportedTemporalTypeException
   *         if the unit is not supported
   */
  @Nonnull
  public XMLOffsetDateTime truncatedTo (@Nonnull final TemporalUnit unit)
  {
    return with (m_aDateTime.truncatedTo (unit), m_aOffset);
  }

  /**
   * Returns a copy of this date-time with the specified amount added.
   * <p>
   * This returns an {@code XMLOffsetDateTime}, based on this one, with the
   * specified amount added. The amount is typically {@link Period} or
   * {@link Duration} but may be any other type implementing the
   * {@link TemporalAmount} interface.
   * <p>
   * The calculation is delegated to the amount object by calling
   * {@link TemporalAmount#addTo(Temporal)}. The amount implementation is free
   * to implement the addition in any way it wishes, however it typically calls
   * back to {@link #plus(long, TemporalUnit)}. Consult the documentation of the
   * amount implementation to determine if it can be successfully added.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param amountToAdd
   *        the amount to add, not null
   * @return an {@code XMLOffsetDateTime} based on this date-time with the
   *         addition made, not null
   * @throws DateTimeException
   *         if the addition cannot be made
   * @throws ArithmeticException
   *         if numeric overflow occurs
   */
  @Override
  public XMLOffsetDateTime plus (@Nonnull final TemporalAmount amountToAdd)
  {
    return (XMLOffsetDateTime) amountToAdd.addTo (this);
  }

  /**
   * Returns a copy of this date-time with the specified amount added.
   * <p>
   * This returns an {@code XMLOffsetDateTime}, based on this one, with the
   * amount in terms of the unit added. If it is not possible to add the amount,
   * because the unit is not supported or for some other reason, an exception is
   * thrown.
   * <p>
   * If the field is a {@link ChronoUnit} then the addition is implemented by
   * {@link LocalDateTime#plus(long, TemporalUnit)}. The offset is not part of
   * the calculation and will be unchanged in the result.
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
   * @return an {@code XMLOffsetDateTime} based on this date-time with the
   *         specified amount added, not null
   * @throws DateTimeException
   *         if the addition cannot be made
   * @throws UnsupportedTemporalTypeException
   *         if the unit is not supported
   * @throws ArithmeticException
   *         if numeric overflow occurs
   */
  @Override
  public XMLOffsetDateTime plus (final long amountToAdd, @Nonnull final TemporalUnit unit)
  {
    if (unit instanceof ChronoUnit)
      return with (m_aDateTime.plus (amountToAdd, unit), m_aOffset);

    return unit.addTo (this, amountToAdd);
  }

  /**
   * Returns a copy of this {@code XMLOffsetDateTime} with the specified number
   * of years added.
   * <p>
   * This method adds the specified amount to the years field in three steps:
   * <ol>
   * <li>Add the input years to the year field</li>
   * <li>Check if the resulting date would be invalid</li>
   * <li>Adjust the day-of-month to the last valid day if necessary</li>
   * </ol>
   * <p>
   * For example, 2008-02-29 (leap year) plus one year would result in the
   * invalid date 2009-02-29 (standard year). Instead of returning an invalid
   * result, the last valid day of the month, 2009-02-28, is selected instead.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param years
   *        the years to add, may be negative
   * @return an {@code XMLOffsetDateTime} based on this date-time with the years
   *         added, not null
   * @throws DateTimeException
   *         if the result exceeds the supported date range
   */
  @Nonnull
  public XMLOffsetDateTime plusYears (final long years)
  {
    return with (m_aDateTime.plusYears (years), m_aOffset);
  }

  /**
   * Returns a copy of this {@code XMLOffsetDateTime} with the specified number
   * of months added.
   * <p>
   * This method adds the specified amount to the months field in three steps:
   * <ol>
   * <li>Add the input months to the month-of-year field</li>
   * <li>Check if the resulting date would be invalid</li>
   * <li>Adjust the day-of-month to the last valid day if necessary</li>
   * </ol>
   * <p>
   * For example, 2007-03-31 plus one month would result in the invalid date
   * 2007-04-31. Instead of returning an invalid result, the last valid day of
   * the month, 2007-04-30, is selected instead.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param months
   *        the months to add, may be negative
   * @return an {@code XMLOffsetDateTime} based on this date-time with the
   *         months added, not null
   * @throws DateTimeException
   *         if the result exceeds the supported date range
   */
  @Nonnull
  public XMLOffsetDateTime plusMonths (final long months)
  {
    return with (m_aDateTime.plusMonths (months), m_aOffset);
  }

  /**
   * Returns a copy of this XMLOffsetDateTime with the specified number of weeks
   * added.
   * <p>
   * This method adds the specified amount in weeks to the days field
   * incrementing the month and year fields as necessary to ensure the result
   * remains valid. The result is only invalid if the maximum/minimum year is
   * exceeded.
   * <p>
   * For example, 2008-12-31 plus one week would result in 2009-01-07.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param weeks
   *        the weeks to add, may be negative
   * @return an {@code XMLOffsetDateTime} based on this date-time with the weeks
   *         added, not null
   * @throws DateTimeException
   *         if the result exceeds the supported date range
   */
  @Nonnull
  public XMLOffsetDateTime plusWeeks (final long weeks)
  {
    return with (m_aDateTime.plusWeeks (weeks), m_aOffset);
  }

  /**
   * Returns a copy of this XMLOffsetDateTime with the specified number of days
   * added.
   * <p>
   * This method adds the specified amount to the days field incrementing the
   * month and year fields as necessary to ensure the result remains valid. The
   * result is only invalid if the maximum/minimum year is exceeded.
   * <p>
   * For example, 2008-12-31 plus one day would result in 2009-01-01.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param days
   *        the days to add, may be negative
   * @return an {@code XMLOffsetDateTime} based on this date-time with the days
   *         added, not null
   * @throws DateTimeException
   *         if the result exceeds the supported date range
   */
  @Nonnull
  public XMLOffsetDateTime plusDays (final long days)
  {
    return with (m_aDateTime.plusDays (days), m_aOffset);
  }

  /**
   * Returns a copy of this {@code XMLOffsetDateTime} with the specified number
   * of hours added.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param hours
   *        the hours to add, may be negative
   * @return an {@code XMLOffsetDateTime} based on this date-time with the hours
   *         added, not null
   * @throws DateTimeException
   *         if the result exceeds the supported date range
   */
  @Nonnull
  public XMLOffsetDateTime plusHours (final long hours)
  {
    return with (m_aDateTime.plusHours (hours), m_aOffset);
  }

  /**
   * Returns a copy of this {@code XMLOffsetDateTime} with the specified number
   * of minutes added.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param minutes
   *        the minutes to add, may be negative
   * @return an {@code XMLOffsetDateTime} based on this date-time with the
   *         minutes added, not null
   * @throws DateTimeException
   *         if the result exceeds the supported date range
   */
  @Nonnull
  public XMLOffsetDateTime plusMinutes (final long minutes)
  {
    return with (m_aDateTime.plusMinutes (minutes), m_aOffset);
  }

  /**
   * Returns a copy of this {@code XMLOffsetDateTime} with the specified number
   * of seconds added.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param seconds
   *        the seconds to add, may be negative
   * @return an {@code XMLOffsetDateTime} based on this date-time with the
   *         seconds added, not null
   * @throws DateTimeException
   *         if the result exceeds the supported date range
   */
  @Nonnull
  public XMLOffsetDateTime plusSeconds (final long seconds)
  {
    return with (m_aDateTime.plusSeconds (seconds), m_aOffset);
  }

  /**
   * Returns a copy of this {@code XMLOffsetDateTime} with the specified number
   * of nanoseconds added.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param nanos
   *        the nanos to add, may be negative
   * @return an {@code XMLOffsetDateTime} based on this date-time with the
   *         nanoseconds added, not null
   * @throws DateTimeException
   *         if the unit cannot be added to this type
   */
  @Nonnull
  public XMLOffsetDateTime plusNanos (final long nanos)
  {
    return with (m_aDateTime.plusNanos (nanos), m_aOffset);
  }

  /**
   * Returns a copy of this date-time with the specified amount subtracted.
   * <p>
   * This returns an {@code XMLOffsetDateTime}, based on this one, with the
   * specified amount subtracted. The amount is typically {@link Period} or
   * {@link Duration} but may be any other type implementing the
   * {@link TemporalAmount} interface.
   * <p>
   * The calculation is delegated to the amount object by calling
   * {@link TemporalAmount#subtractFrom(Temporal)}. The amount implementation is
   * free to implement the subtraction in any way it wishes, however it
   * typically calls back to {@link #minus(long, TemporalUnit)}. Consult the
   * documentation of the amount implementation to determine if it can be
   * successfully subtracted.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param amountToSubtract
   *        the amount to subtract, not null
   * @return an {@code XMLOffsetDateTime} based on this date-time with the
   *         subtraction made, not null
   * @throws DateTimeException
   *         if the subtraction cannot be made
   * @throws ArithmeticException
   *         if numeric overflow occurs
   */
  @Override
  @Nonnull
  public XMLOffsetDateTime minus (@Nonnull final TemporalAmount amountToSubtract)
  {
    return (XMLOffsetDateTime) amountToSubtract.subtractFrom (this);
  }

  /**
   * Returns a copy of this date-time with the specified amount subtracted.
   * <p>
   * This returns an {@code XMLOffsetDateTime}, based on this one, with the
   * amount in terms of the unit subtracted. If it is not possible to subtract
   * the amount, because the unit is not supported or for some other reason, an
   * exception is thrown.
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
   * @return an {@code XMLOffsetDateTime} based on this date-time with the
   *         specified amount subtracted, not null
   * @throws DateTimeException
   *         if the subtraction cannot be made
   * @throws UnsupportedTemporalTypeException
   *         if the unit is not supported
   * @throws ArithmeticException
   *         if numeric overflow occurs
   */
  @Override
  @Nonnull
  public XMLOffsetDateTime minus (final long amountToSubtract, final TemporalUnit unit)
  {
    return amountToSubtract == Long.MIN_VALUE ? plus (Long.MAX_VALUE, unit).plus (1, unit) : plus (-amountToSubtract, unit);
  }

  /**
   * Returns a copy of this {@code XMLOffsetDateTime} with the specified number
   * of years subtracted.
   * <p>
   * This method subtracts the specified amount from the years field in three
   * steps:
   * <ol>
   * <li>Subtract the input years from the year field</li>
   * <li>Check if the resulting date would be invalid</li>
   * <li>Adjust the day-of-month to the last valid day if necessary</li>
   * </ol>
   * <p>
   * For example, 2008-02-29 (leap year) minus one year would result in the
   * invalid date 2009-02-29 (standard year). Instead of returning an invalid
   * result, the last valid day of the month, 2009-02-28, is selected instead.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param years
   *        the years to subtract, may be negative
   * @return an {@code XMLOffsetDateTime} based on this date-time with the years
   *         subtracted, not null
   * @throws DateTimeException
   *         if the result exceeds the supported date range
   */
  @Nonnull
  public XMLOffsetDateTime minusYears (final long years)
  {
    return years == Long.MIN_VALUE ? plusYears (Long.MAX_VALUE).plusYears (1) : plusYears (-years);
  }

  /**
   * Returns a copy of this {@code XMLOffsetDateTime} with the specified number
   * of months subtracted.
   * <p>
   * This method subtracts the specified amount from the months field in three
   * steps:
   * <ol>
   * <li>Subtract the input months from the month-of-year field</li>
   * <li>Check if the resulting date would be invalid</li>
   * <li>Adjust the day-of-month to the last valid day if necessary</li>
   * </ol>
   * <p>
   * For example, 2007-03-31 minus one month would result in the invalid date
   * 2007-04-31. Instead of returning an invalid result, the last valid day of
   * the month, 2007-04-30, is selected instead.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param months
   *        the months to subtract, may be negative
   * @return an {@code XMLOffsetDateTime} based on this date-time with the
   *         months subtracted, not null
   * @throws DateTimeException
   *         if the result exceeds the supported date range
   */
  @Nonnull
  public XMLOffsetDateTime minusMonths (final long months)
  {
    return months == Long.MIN_VALUE ? plusMonths (Long.MAX_VALUE).plusMonths (1) : plusMonths (-months);
  }

  /**
   * Returns a copy of this {@code XMLOffsetDateTime} with the specified number
   * of weeks subtracted.
   * <p>
   * This method subtracts the specified amount in weeks from the days field
   * decrementing the month and year fields as necessary to ensure the result
   * remains valid. The result is only invalid if the maximum/minimum year is
   * exceeded.
   * <p>
   * For example, 2008-12-31 minus one week would result in 2009-01-07.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param weeks
   *        the weeks to subtract, may be negative
   * @return an {@code XMLOffsetDateTime} based on this date-time with the weeks
   *         subtracted, not null
   * @throws DateTimeException
   *         if the result exceeds the supported date range
   */
  @Nonnull
  public XMLOffsetDateTime minusWeeks (final long weeks)
  {
    return weeks == Long.MIN_VALUE ? plusWeeks (Long.MAX_VALUE).plusWeeks (1) : plusWeeks (-weeks);
  }

  /**
   * Returns a copy of this {@code XMLOffsetDateTime} with the specified number
   * of days subtracted.
   * <p>
   * This method subtracts the specified amount from the days field decrementing
   * the month and year fields as necessary to ensure the result remains valid.
   * The result is only invalid if the maximum/minimum year is exceeded.
   * <p>
   * For example, 2008-12-31 minus one day would result in 2009-01-01.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param days
   *        the days to subtract, may be negative
   * @return an {@code XMLOffsetDateTime} based on this date-time with the days
   *         subtracted, not null
   * @throws DateTimeException
   *         if the result exceeds the supported date range
   */
  @Nonnull
  public XMLOffsetDateTime minusDays (final long days)
  {
    return days == Long.MIN_VALUE ? plusDays (Long.MAX_VALUE).plusDays (1) : plusDays (-days);
  }

  /**
   * Returns a copy of this {@code XMLOffsetDateTime} with the specified number
   * of hours subtracted.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param hours
   *        the hours to subtract, may be negative
   * @return an {@code XMLOffsetDateTime} based on this date-time with the hours
   *         subtracted, not null
   * @throws DateTimeException
   *         if the result exceeds the supported date range
   */
  @Nonnull
  public XMLOffsetDateTime minusHours (final long hours)
  {
    return hours == Long.MIN_VALUE ? plusHours (Long.MAX_VALUE).plusHours (1) : plusHours (-hours);
  }

  /**
   * Returns a copy of this {@code XMLOffsetDateTime} with the specified number
   * of minutes subtracted.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param minutes
   *        the minutes to subtract, may be negative
   * @return an {@code XMLOffsetDateTime} based on this date-time with the
   *         minutes subtracted, not null
   * @throws DateTimeException
   *         if the result exceeds the supported date range
   */
  @Nonnull
  public XMLOffsetDateTime minusMinutes (final long minutes)
  {
    return minutes == Long.MIN_VALUE ? plusMinutes (Long.MAX_VALUE).plusMinutes (1) : plusMinutes (-minutes);
  }

  /**
   * Returns a copy of this {@code XMLOffsetDateTime} with the specified number
   * of seconds subtracted.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param seconds
   *        the seconds to subtract, may be negative
   * @return an {@code XMLOffsetDateTime} based on this date-time with the
   *         seconds subtracted, not null
   * @throws DateTimeException
   *         if the result exceeds the supported date range
   */
  @Nonnull
  public XMLOffsetDateTime minusSeconds (final long seconds)
  {
    return seconds == Long.MIN_VALUE ? plusSeconds (Long.MAX_VALUE).plusSeconds (1) : plusSeconds (-seconds);
  }

  /**
   * Returns a copy of this {@code XMLOffsetDateTime} with the specified number
   * of nanoseconds subtracted.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param nanos
   *        the nanos to subtract, may be negative
   * @return an {@code XMLOffsetDateTime} based on this date-time with the
   *         nanoseconds subtracted, not null
   * @throws DateTimeException
   *         if the result exceeds the supported date range
   */
  @Nonnull
  public XMLOffsetDateTime minusNanos (final long nanos)
  {
    return nanos == Long.MIN_VALUE ? plusNanos (Long.MAX_VALUE).plusNanos (1) : plusNanos (-nanos);
  }

  /**
   * Queries this date-time using the specified query.
   * <p>
   * This queries this date-time using the specified query strategy object. The
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
    if (query == TemporalQueries.offset () || query == TemporalQueries.zone ())
      return (R) getOffsetOrDefault ();

    if (query == TemporalQueries.zoneId ())
      return null;

    if (query == TemporalQueries.localDate ())
      return (R) toLocalDate ();

    if (query == TemporalQueries.localTime ())
      return (R) toLocalTime ();

    if (query == TemporalQueries.chronology ())
      return (R) IsoChronology.INSTANCE;

    if (query == TemporalQueries.precision ())
      return (R) NANOS;

    return Temporal.super.query (query);
  }

  /**
   * Adjusts the specified temporal object to have the same offset, date and
   * time as this object.
   * <p>
   * This returns a temporal object of the same observable type as the input
   * with the offset, date and time changed to be the same as this.
   * <p>
   * The adjustment is equivalent to using
   * {@link Temporal#with(TemporalField, long)} three times, passing
   * {@link ChronoField#EPOCH_DAY}, {@link ChronoField#NANO_OF_DAY} and
   * {@link ChronoField#OFFSET_SECONDS} as the fields.
   * <p>
   * In most cases, it is clearer to reverse the calling pattern by using
   * {@link Temporal#with(TemporalAdjuster)}:
   *
   * <pre>
   * // these two lines are equivalent, but the second approach is recommended
   * temporal = thisOffsetDateTime.adjustInto (temporal);
   * temporal = temporal.with (thisOffsetDateTime);
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
    // XMLOffsetDateTime is treated as three separate fields, not an instant
    // this produces the most consistent set of results overall
    // the offset is set after the date and time, as it is typically a small
    // tweak to the result, with ZonedDateTime frequently ignoring the offset
    return temporal.with (EPOCH_DAY, toLocalDate ().toEpochDay ())
                   .with (NANO_OF_DAY, toLocalTime ().toNanoOfDay ())
                   .with (OFFSET_SECONDS, m_aOffset != null ? m_aOffset.getTotalSeconds () : 0);
  }

  /**
   * Calculates the amount of time until another date-time in terms of the
   * specified unit.
   * <p>
   * This calculates the amount of time between two {@code XMLOffsetDateTime}
   * objects in terms of a single {@code TemporalUnit}. The start and end points
   * are {@code this} and the specified date-time. The result will be negative
   * if the end is before the start. For example, the amount in days between two
   * date-times can be calculated using
   * {@code startDateTime.until(endDateTime, DAYS)}.
   * <p>
   * The {@code Temporal} passed to this method is converted to a
   * {@code XMLOffsetDateTime} using {@link #from(TemporalAccessor)}. If the
   * offset differs between the two date-times, the specified end date-time is
   * normalized to have the same offset as this date-time.
   * <p>
   * The calculation returns a whole number, representing the number of complete
   * units between the two date-times. For example, the amount in months between
   * 2012-06-15T00:00Z and 2012-08-14T23:59Z will only be one month as it is one
   * minute short of two months.
   * <p>
   * There are two equivalent ways of using this method. The first is to invoke
   * this method. The second is to use
   * {@link TemporalUnit#between(Temporal, Temporal)}:
   *
   * <pre>
   * // these two lines are equivalent
   * amount = start.until (end, MONTHS);
   * amount = MONTHS.between (start, end);
   * </pre>
   *
   * The choice should be made based on which makes the code more readable.
   * <p>
   * The calculation is implemented in this method for {@link ChronoUnit}. The
   * units {@code NANOS}, {@code MICROS}, {@code MILLIS}, {@code SECONDS},
   * {@code MINUTES}, {@code HOURS} and {@code HALF_DAYS}, {@code DAYS},
   * {@code WEEKS}, {@code MONTHS}, {@code YEARS}, {@code DECADES},
   * {@code CENTURIES}, {@code MILLENNIA} and {@code ERAS} are supported. Other
   * {@code ChronoUnit} values will throw an exception.
   * <p>
   * If the unit is not a {@code ChronoUnit}, then the result of this method is
   * obtained by invoking {@code TemporalUnit.between(Temporal, Temporal)}
   * passing {@code this} as the first argument and the converted input temporal
   * as the second argument.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param endExclusive
   *        the end date, exclusive, which is converted to an
   *        {@code XMLOffsetDateTime}, not null
   * @param unit
   *        the unit to measure the amount in, not null
   * @return the amount of time between this date-time and the end date-time
   * @throws DateTimeException
   *         if the amount cannot be calculated, or the end temporal cannot be
   *         converted to an {@code XMLOffsetDateTime}
   * @throws UnsupportedTemporalTypeException
   *         if the unit is not supported
   * @throws ArithmeticException
   *         if numeric overflow occurs
   */
  @Override
  public long until (@Nonnull final Temporal endExclusive, @Nonnull final TemporalUnit unit)
  {
    XMLOffsetDateTime end = XMLOffsetDateTime.from (endExclusive);
    if (unit instanceof ChronoUnit)
    {
      end = end.withOffsetSameInstant (m_aOffset);
      return m_aDateTime.until (end.m_aDateTime, unit);
    }
    return unit.between (this, end);
  }

  /**
   * Formats this date-time using the specified formatter.
   * <p>
   * This date-time will be passed to the formatter to produce a string.
   *
   * @param formatter
   *        the formatter to use, not null
   * @return the formatted date-time string, not null
   * @throws DateTimeException
   *         if an error occurs during printing
   */
  @Nonnull
  public String format (@Nonnull final DateTimeFormatter formatter)
  {
    Objects.requireNonNull (formatter, "formatter");
    return formatter.format (this);
  }

  @Nonnull
  protected ZoneOffset getOffsetOr (@Nonnull final Supplier <ZoneOffset> aSupplier)
  {
    ZoneOffset ret = m_aOffset;
    if (ret == null)
      ret = aSupplier.get ();
    return ret;
  }

  @Nonnull
  protected ZoneOffset getOffsetOrDefault ()
  {
    return getOffsetOr ( () -> PDTConfig.getDefaultZoneId ().getRules ().getOffset (m_aDateTime));
  }

  @Nonnull
  protected ZoneOffset getOffsetOrUTC ()
  {
    return getOffsetOr ( () -> ZoneOffset.UTC);
  }

  /**
   * Combines this date-time with a time-zone to create a {@code ZonedDateTime}
   * ensuring that the result has the same instant.
   * <p>
   * This returns a {@code ZonedDateTime} formed from this date-time and the
   * specified time-zone. This conversion will ignore the visible local
   * date-time and use the underlying instant instead. This avoids any problems
   * with local time-line gaps or overlaps. The result might have different
   * values for fields such as hour, minute an even day.
   * <p>
   * To attempt to retain the values of the fields, use
   * {@link #atZoneSimilarLocal(ZoneId)}. To use the offset as the zone ID, use
   * {@link #toZonedDateTime()}.
   *
   * @param zone
   *        the time-zone to use, not null
   * @return the zoned date-time formed from this date-time, not null
   */
  @Nonnull
  public ZonedDateTime atZoneSameInstant (@Nonnull final ZoneId zone)
  {
    return ZonedDateTime.ofInstant (m_aDateTime, getOffsetOrDefault (), zone);
  }

  /**
   * Combines this date-time with a time-zone to create a {@code ZonedDateTime}
   * trying to keep the same local date and time.
   * <p>
   * This returns a {@code ZonedDateTime} formed from this date-time and the
   * specified time-zone. Where possible, the result will have the same local
   * date-time as this object.
   * <p>
   * Time-zone rules, such as daylight savings, mean that not every time on the
   * local time-line exists. If the local date-time is in a gap or overlap
   * according to the rules then a resolver is used to determine the resultant
   * local time and offset. This method uses
   * {@link ZonedDateTime#ofLocal(LocalDateTime, ZoneId, ZoneOffset)} to retain
   * the offset from this instance if possible.
   * <p>
   * Finer control over gaps and overlaps is available in two ways. If you
   * simply want to use the later offset at overlaps then call
   * {@link ZonedDateTime#withLaterOffsetAtOverlap()} immediately after this
   * method.
   * <p>
   * To create a zoned date-time at the same instant irrespective of the local
   * time-line, use {@link #atZoneSameInstant(ZoneId)}. To use the offset as the
   * zone ID, use {@link #toZonedDateTime()}.
   *
   * @param zone
   *        the time-zone to use, not null
   * @return the zoned date-time formed from this date and the earliest valid
   *         time for the zone, not null
   */
  @Nonnull
  public ZonedDateTime atZoneSimilarLocal (@Nonnull final ZoneId zone)
  {
    return ZonedDateTime.ofLocal (m_aDateTime, zone, getOffsetOrDefault ());
  }

  /**
   * Converts this date-time to an {@code OffsetTime}.
   * <p>
   * This returns an offset time with the same local time and offset.
   *
   * @return an OffsetTime representing the time and offset, not null
   */
  @Nonnull
  public OffsetTime toOffsetTime ()
  {
    return OffsetTime.of (m_aDateTime.toLocalTime (), getOffsetOrDefault ());
  }

  /**
   * Converts this date-time to an {@code XMLOffsetTime}.
   * <p>
   * This returns an offset time with the same local time and offset.
   *
   * @return an XMLOffsetTime representing the time and offset, not null
   */
  @Nonnull
  public XMLOffsetTime toXMLOffsetTime ()
  {
    return XMLOffsetTime.of (m_aDateTime.toLocalTime (), m_aOffset);
  }

  @Nullable
  public OffsetDate toOffsetDate ()
  {
    return OffsetDate.of (m_aDateTime.toLocalDate (), getOffsetOrDefault ());
  }

  @Nullable
  public XMLOffsetDate toXMLOffsetDate ()
  {
    return XMLOffsetDate.of (m_aDateTime.toLocalDate (), m_aOffset);
  }

  @Nullable
  public OffsetDateTime toOffsetDateTime ()
  {
    return OffsetDateTime.of (m_aDateTime, getOffsetOrDefault ());
  }

  /**
   * Converts this date-time to a {@code ZonedDateTime} using the offset as the
   * zone ID.
   * <p>
   * This creates the simplest possible {@code ZonedDateTime} using the offset
   * as the zone ID.
   * <p>
   * To control the time-zone used, see {@link #atZoneSameInstant(ZoneId)} and
   * {@link #atZoneSimilarLocal(ZoneId)}.
   *
   * @return a zoned date-time representing the same local date-time and offset,
   *         not null
   */
  @Nonnull
  public ZonedDateTime toZonedDateTime ()
  {
    return ZonedDateTime.of (m_aDateTime, getOffsetOrDefault ());
  }

  /**
   * Converts this date-time to an {@code Instant}.
   * <p>
   * This returns an {@code Instant} representing the same point on the
   * time-line as this date-time.
   *
   * @return an {@code Instant} representing the same instant, not null
   */
  @Nonnull
  public Instant toInstant ()
  {
    return m_aDateTime.toInstant (getOffsetOrDefault ());
  }

  /**
   * Converts this date-time to the number of seconds from the epoch of
   * 1970-01-01T00:00:00Z.
   * <p>
   * This allows this date-time to be converted to a value of the
   * {@link ChronoField#INSTANT_SECONDS epoch-seconds} field. This is primarily
   * intended for low-level conversions rather than general application usage.
   *
   * @return the number of seconds from the epoch of 1970-01-01T00:00:00Z
   */
  public long toEpochSecond ()
  {
    return m_aDateTime.toEpochSecond (getOffsetOrUTC ());
  }

  /**
   * Compares this date-time to another date-time.
   * <p>
   * The comparison is based on the instant then on the local date-time. It is
   * "consistent with equals", as defined by {@link Comparable}.
   * <p>
   * For example, the following is the comparator order:
   * <ol>
   * <li>{@code 2008-12-03T10:30+01:00}</li>
   * <li>{@code 2008-12-03T11:00+01:00}</li>
   * <li>{@code 2008-12-03T12:00+02:00}</li>
   * <li>{@code 2008-12-03T11:30+01:00}</li>
   * <li>{@code 2008-12-03T12:00+01:00}</li>
   * <li>{@code 2008-12-03T12:30+01:00}</li>
   * </ol>
   * Values #2 and #3 represent the same instant on the time-line. When two
   * values represent the same instant, the local date-time is compared to
   * distinguish them. This step is needed to make the ordering consistent with
   * {@code equals()}.
   *
   * @param other
   *        the other date-time to compare to, not null
   * @return the comparator value, negative if less, positive if greater
   */
  @Override
  public int compareTo (@Nonnull final XMLOffsetDateTime other)
  {
    int ret = compareInstant (this, other);
    if (ret == 0)
      ret = toLocalDateTime ().compareTo (other.toLocalDateTime ());
    return ret;
  }

  /**
   * Checks if the instant of this date-time is after that of the specified
   * date-time.
   * <p>
   * This method differs from the comparison in {@link #compareTo} and
   * {@link #equals} in that it only compares the instant of the date-time. This
   * is equivalent to using
   * {@code dateTime1.toInstant().isAfter(dateTime2.toInstant());}.
   *
   * @param other
   *        the other date-time to compare to, not null
   * @return true if this is after the instant of the specified date-time
   */
  public boolean isAfter (@Nonnull final XMLOffsetDateTime other)
  {
    final long thisEpochSec = toEpochSecond ();
    final long otherEpochSec = other.toEpochSecond ();
    return thisEpochSec > otherEpochSec || (thisEpochSec == otherEpochSec && toLocalTime ().getNano () > other.toLocalTime ().getNano ());
  }

  /**
   * Checks if the instant of this date-time is before that of the specified
   * date-time.
   * <p>
   * This method differs from the comparison in {@link #compareTo} in that it
   * only compares the instant of the date-time. This is equivalent to using
   * {@code dateTime1.toInstant().isBefore(dateTime2.toInstant());}.
   *
   * @param other
   *        the other date-time to compare to, not null
   * @return true if this is before the instant of the specified date-time
   */
  public boolean isBefore (@Nonnull final XMLOffsetDateTime other)
  {
    final long thisEpochSec = toEpochSecond ();
    final long otherEpochSec = other.toEpochSecond ();
    return thisEpochSec < otherEpochSec || (thisEpochSec == otherEpochSec && toLocalTime ().getNano () < other.toLocalTime ().getNano ());
  }

  /**
   * Checks if the instant of this date-time is equal to that of the specified
   * date-time.
   * <p>
   * This method differs from the comparison in {@link #compareTo} and
   * {@link #equals} in that it only compares the instant of the date-time. This
   * is equivalent to using
   * {@code dateTime1.toInstant().equals(dateTime2.toInstant());}.
   *
   * @param other
   *        the other date-time to compare to, not null
   * @return true if the instant equals the instant of the specified date-time
   */
  public boolean isEqual (@Nonnull final XMLOffsetDateTime other)
  {
    return toEpochSecond () == other.toEpochSecond () && toLocalTime ().getNano () == other.toLocalTime ().getNano ();
  }

  /**
   * Checks if this date-time is equal to another date-time.
   * <p>
   * The comparison is based on the local date-time and the offset. To compare
   * for the same instant on the time-line, use {@link #isEqual}. Only objects
   * of type {@code XMLOffsetDateTime} are compared, other types return false.
   *
   * @param o
   *        the object to check, null returns false
   * @return true if this is equal to the other date-time
   */
  @Override
  public boolean equals (final Object o)
  {
    if (this == o)
      return true;

    if (o == null || !getClass ().equals (o.getClass ()))
      return false;

    final XMLOffsetDateTime other = (XMLOffsetDateTime) o;
    return m_aDateTime.equals (other.m_aDateTime) && EqualsHelper.equals (m_aOffset, other.m_aOffset);
  }

  /**
   * A hash code for this date-time.
   *
   * @return a suitable hash code
   */
  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aDateTime).append (m_aOffset).getHashCode ();
  }

  @Nonnull
  @Nonempty
  @Deprecated
  public String getAsString ()
  {
    return toString ();
  }

  /**
   * Outputs this date-time as a {@code String}, such as
   * {@code 2007-12-03T10:15:30+01:00}.
   * <p>
   * The output will be one of the following ISO-8601 formats:
   * <ul>
   * <li>{@code uuuu-MM-dd'T'HH:mm}</li>
   * <li>{@code uuuu-MM-dd'T'HH:mm:ss}</li>
   * <li>{@code uuuu-MM-dd'T'HH:mm:ss.SSS}</li>
   * <li>{@code uuuu-MM-dd'T'HH:mm:ss.SSSSSS}</li>
   * <li>{@code uuuu-MM-dd'T'HH:mm:ss.SSSSSSSSS}</li>
   * <li>{@code uuuu-MM-dd'T'HH:mmXXXXX}</li>
   * <li>{@code uuuu-MM-dd'T'HH:mm:ssXXXXX}</li>
   * <li>{@code uuuu-MM-dd'T'HH:mm:ss.SSSXXXXX}</li>
   * <li>{@code uuuu-MM-dd'T'HH:mm:ss.SSSSSSXXXXX}</li>
   * <li>{@code uuuu-MM-dd'T'HH:mm:ss.SSSSSSSSSXXXXX}</li>
   * </ul>
   * The format used will be the shortest that outputs the full value of the
   * time where the omitted parts are implied to be zero.
   *
   * @return a string representation of this date-time, not null
   */
  @Override
  public String toString ()
  {
    return m_aDateTime.toString () + (m_aOffset != null ? m_aOffset.toString () : "");
  }
}
