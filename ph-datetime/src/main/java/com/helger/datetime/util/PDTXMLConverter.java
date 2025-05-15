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
package com.helger.datetime.util;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import com.helger.annotation.CheckForSigned;
import com.helger.annotation.Nonnull;
import com.helger.annotation.Nullable;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.CGlobal;
import com.helger.commons.datetime.PDTConfig;
import com.helger.commons.datetime.PDTFactory;
import com.helger.commons.exception.InitializationException;

/**
 * Utility class for XML date/time data type handling.
 *
 * @author Philip Helger
 */
@Immutable
public final class PDTXMLConverter
{
  private static final Logger LOGGER = LoggerFactory.getLogger (PDTXMLConverter.class);
  private static final DatatypeFactory DT_FACTORY;

  static
  {
    try
    {
      // required for the Gregorian calendar
      DT_FACTORY = DatatypeFactory.newInstance ();
    }
    catch (final DatatypeConfigurationException ex)
    {
      throw new InitializationException ("Failed to init DataTypeFactory", ex);
    }
  }

  @PresentForCodeCoverage
  private static final PDTXMLConverter INSTANCE = new PDTXMLConverter ();

  private PDTXMLConverter ()
  {}

  /**
   * @return The global {@link DatatypeFactory} used internally in this class.
   */
  @Nonnull
  public static DatatypeFactory getDatatypeFactory ()
  {
    return DT_FACTORY;
  }

  /**
   * Convert milliseconds to minutes.
   *
   * @param nOffsetInMillis
   *        The offset in milliseconds to use. May not be <code>null</code>.
   * @return 0 for no offset to UTC, the minutes otherwise. Usually in 60minutes
   *         steps :)
   */
  public static int getTimezoneOffsetInMinutes (final int nOffsetInMillis)
  {
    return nOffsetInMillis / (int) CGlobal.MILLISECONDS_PER_MINUTE;
  }

  /**
   * Get the time zone offset to UTC of the passed calendar in minutes to be
   * used in {@link XMLGregorianCalendar}.
   *
   * @param aCalendar
   *        The calendar to use. May not be <code>null</code>.
   * @return 0 for no offset to UTC, the minutes otherwise. Usually in 60minutes
   *         steps :)
   */
  public static int getTimezoneOffsetInMinutes (@Nonnull final Calendar aCalendar)
  {
    final int nOffsetInMillis = aCalendar.getTimeZone ().getOffset (aCalendar.getTimeInMillis ());
    return getTimezoneOffsetInMinutes (nOffsetInMillis);
  }

  /**
   * Get the passed date as {@link GregorianCalendar} using the default time
   * zone.
   *
   * @param aDate
   *        The source date. May be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @Nonnull
  public static GregorianCalendar getCalendar (@Nonnull final Date aDate)
  {
    final GregorianCalendar aCalendar = new GregorianCalendar (PDTFactory.getTimeZone (aDate), Locale.getDefault (Locale.Category.FORMAT));
    aCalendar.setTime (aDate);
    return aCalendar;
  }

  /**
   * Get the passed milliseconds as {@link GregorianCalendar}.
   *
   * @param nMillis
   *        Milliseconds since 1.1.1970
   * @return Never <code>null</code>.
   * @since 9.1.8
   */
  @Nonnull
  public static GregorianCalendar getCalendarDefaultTimeZone (final long nMillis)
  {
    final GregorianCalendar aCalendar = new GregorianCalendar (PDTConfig.getDefaultTimeZone (), Locale.getDefault (Locale.Category.FORMAT));
    aCalendar.setTimeInMillis (nMillis);
    return aCalendar;
  }

  /**
   * Get the passed milliseconds as {@link GregorianCalendar}.
   *
   * @param nMillis
   *        Milliseconds since 1.1.1970
   * @return Never <code>null</code>.
   * @since 9.1.8
   */
  @Nonnull
  public static GregorianCalendar getCalendarUTC (final long nMillis)
  {
    final GregorianCalendar aCalendar = new GregorianCalendar (PDTConfig.getUTCTimeZone (), Locale.getDefault (Locale.Category.FORMAT));
    aCalendar.setTimeInMillis (nMillis);
    return aCalendar;
  }

  /**
   * @return A new XML calendar instance, with all fields uninitialized. Never
   *         <code>null</code>.
   */
  @Nonnull
  public static XMLGregorianCalendar createNewCalendar ()
  {
    return DT_FACTORY.newXMLGregorianCalendar ();
  }

  /**
   * Get the current date as {@link XMLGregorianCalendar}.
   *
   * @return Never <code>null</code>.
   */
  @Nonnull
  public static XMLGregorianCalendar getXMLCalendarDateNow ()
  {
    return getXMLCalendarDate (PDTFactory.getCurrentLocalDate ());
  }

  /**
   * Get the passed object as {@link XMLGregorianCalendar} date (without a
   * time).
   *
   * @param aBase
   *        The source object. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static XMLGregorianCalendar getXMLCalendarDate (@Nullable final LocalDate aBase)
  {
    return getXMLCalendarDate (aBase, DatatypeConstants.FIELD_UNDEFINED);
  }

  /**
   * Get the passed object as {@link XMLGregorianCalendar} date (without a
   * time).
   *
   * @param aBase
   *        The source object. May be <code>null</code>.
   * @param nTimezoneOffsetInMinutes
   *        Timezone offset in minutes. Use
   *        {@link DatatypeConstants#FIELD_UNDEFINED} if none is to be used.
   * @return <code>null</code> if the parameter is <code>null</code>.
   * @since 9.3.5
   */
  @Nullable
  public static XMLGregorianCalendar getXMLCalendarDate (@Nullable final LocalDate aBase, final int nTimezoneOffsetInMinutes)
  {
    if (aBase == null)
      return null;
    return DT_FACTORY.newXMLGregorianCalendarDate (aBase.getYear (),
                                                   aBase.getMonth ().getValue (),
                                                   aBase.getDayOfMonth (),
                                                   nTimezoneOffsetInMinutes);
  }

  /**
   * Get the passed object as {@link XMLGregorianCalendar} date (without a
   * time).
   *
   * @param aBase
   *        The source object. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static XMLGregorianCalendar getXMLCalendarDate (@Nullable final Date aBase)
  {
    if (aBase == null)
      return null;
    return getXMLCalendarDate (getCalendar (aBase));
  }

  /**
   * Get the passed object as {@link XMLGregorianCalendar} date (without a
   * time).
   *
   * @param aBase
   *        The source object. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static XMLGregorianCalendar getXMLCalendarDate (@Nullable final GregorianCalendar aBase)
  {
    if (aBase == null)
      return null;
    // Month in XMLGregorianCalendar is 1-based! Fix in 9.1.8
    return DT_FACTORY.newXMLGregorianCalendarDate (aBase.get (Calendar.YEAR),
                                                   aBase.get (Calendar.MONTH) + 1,
                                                   aBase.get (Calendar.DAY_OF_MONTH),
                                                   getTimezoneOffsetInMinutes (aBase));
  }

  /**
   * Get the passed object as {@link XMLGregorianCalendar} date (without a
   * time).
   *
   * @param aBase
   *        The source object. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static XMLGregorianCalendar getXMLCalendarDate (@Nullable final XMLGregorianCalendar aBase)
  {
    if (aBase == null)
      return null;
    return DT_FACTORY.newXMLGregorianCalendarDate (aBase.getYear (),
                                                   aBase.getMonth (),
                                                   aBase.getDay (),
                                                   aBase.getTimezone () == 0 ? DatatypeConstants.FIELD_UNDEFINED : aBase.getTimezone ());
  }

  /**
   * <p>
   * Create a Java representation of XML Schema builtin datatype
   * <code>date</code> or <code>g*</code>.
   * </p>
   * <p>
   * For example, an instance of <code>gYear</code> can be created invoking this
   * factory with <code>month</code> and <code>day</code> parameters set to
   * {@link DatatypeConstants#FIELD_UNDEFINED}.
   * </p>
   * <p>
   * A {@link DatatypeConstants#FIELD_UNDEFINED} value indicates that field is
   * not set.
   * </p>
   *
   * @param nYear
   *        Year to be created.
   * @param nMonth
   *        Month to be created.
   * @param nDay
   *        Day to be created.
   * @return <code>XMLGregorianCalendar</code> created from parameter values.
   * @see DatatypeConstants#FIELD_UNDEFINED
   * @throws IllegalArgumentException
   *         If any individual parameter's value is outside the maximum value
   *         constraint for the field as determined by the Date/Time Data
   *         Mapping table in {@link XMLGregorianCalendar} or if the composite
   *         values constitute an invalid <code>XMLGregorianCalendar</code>
   *         instance as determined by {@link XMLGregorianCalendar#isValid()}.
   */
  @Nonnull
  public static XMLGregorianCalendar getXMLCalendarDate (final int nYear, final int nMonth, final int nDay)
  {
    return getXMLCalendarDate (nYear, nMonth, nDay, DatatypeConstants.FIELD_UNDEFINED);
  }

  /**
   * <p>
   * Create a Java representation of XML Schema builtin datatype
   * <code>date</code> or <code>g*</code>.
   * </p>
   * <p>
   * For example, an instance of <code>gYear</code> can be created invoking this
   * factory with <code>month</code> and <code>day</code> parameters set to
   * {@link DatatypeConstants#FIELD_UNDEFINED}.
   * </p>
   * <p>
   * A {@link DatatypeConstants#FIELD_UNDEFINED} value indicates that field is
   * not set.
   * </p>
   *
   * @param nYear
   *        Year to be created.
   * @param nMonth
   *        Month to be created.
   * @param nDay
   *        Day to be created.
   * @param nTimezone
   *        Offset in minutes. {@link DatatypeConstants#FIELD_UNDEFINED}
   *        indicates optional field is not set.
   * @return <code>XMLGregorianCalendar</code> created from parameter values.
   * @see DatatypeConstants#FIELD_UNDEFINED
   * @throws IllegalArgumentException
   *         If any individual parameter's value is outside the maximum value
   *         constraint for the field as determined by the Date/Time Data
   *         Mapping table in {@link XMLGregorianCalendar} or if the composite
   *         values constitute an invalid <code>XMLGregorianCalendar</code>
   *         instance as determined by {@link XMLGregorianCalendar#isValid()}.
   */
  @Nonnull
  public static XMLGregorianCalendar getXMLCalendarDate (final int nYear, final int nMonth, final int nDay, final int nTimezone)
  {
    return DT_FACTORY.newXMLGregorianCalendarDate (nYear, nMonth, nDay, nTimezone);
  }

  /**
   * Get the current time as {@link XMLGregorianCalendar}.
   *
   * @return Never <code>null</code>.
   */
  @Nonnull
  public static XMLGregorianCalendar getXMLCalendarTimeNow ()
  {
    return getXMLCalendarTime (PDTFactory.getCurrentLocalTime ());
  }

  /**
   * Get the passed object as {@link XMLGregorianCalendar} time (without a
   * date).
   *
   * @param aBase
   *        The source object. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static XMLGregorianCalendar getXMLCalendarTime (@Nullable final LocalTime aBase)
  {
    if (aBase == null)
      return null;
    return DT_FACTORY.newXMLGregorianCalendarTime (aBase.getHour (),
                                                   aBase.getMinute (),
                                                   aBase.getSecond (),
                                                   aBase.get (ChronoField.MILLI_OF_SECOND),
                                                   DatatypeConstants.FIELD_UNDEFINED);
  }

  /**
   * Get the passed object as {@link XMLGregorianCalendar} time (without a
   * date).
   *
   * @param aBase
   *        The source object. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static XMLGregorianCalendar getXMLCalendarTime (@Nullable final Date aBase)
  {
    if (aBase == null)
      return null;
    return getXMLCalendarTime (getCalendar (aBase));
  }

  /**
   * Get the passed object as {@link XMLGregorianCalendar} time (without a
   * date).
   *
   * @param aBase
   *        The source object. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static XMLGregorianCalendar getXMLCalendarTime (@Nullable final GregorianCalendar aBase)
  {
    if (aBase == null)
      return null;
    return DT_FACTORY.newXMLGregorianCalendarTime (aBase.get (Calendar.HOUR_OF_DAY),
                                                   aBase.get (Calendar.MINUTE),
                                                   aBase.get (Calendar.SECOND),
                                                   aBase.get (Calendar.MILLISECOND),
                                                   getTimezoneOffsetInMinutes (aBase));
  }

  /**
   * Get the passed object as {@link XMLGregorianCalendar} time (without a
   * date).
   *
   * @param aBase
   *        The source object. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static XMLGregorianCalendar getXMLCalendarTime (@Nullable final XMLGregorianCalendar aBase)
  {
    if (aBase == null)
      return null;
    return DT_FACTORY.newXMLGregorianCalendarTime (aBase.getHour (),
                                                   aBase.getMinute (),
                                                   aBase.getSecond (),
                                                   aBase.getMillisecond (),
                                                   aBase.getTimezone ());
  }

  /**
   * <p>
   * Create a Java representation of XML Schema builtin datatype
   * <code>date</code> or <code>g*</code>.
   * </p>
   * <p>
   * For example, an instance of <code>gYear</code> can be created invoking this
   * factory with <code>month</code> and <code>day</code> parameters set to
   * {@link DatatypeConstants#FIELD_UNDEFINED}.
   * </p>
   * <p>
   * A {@link DatatypeConstants#FIELD_UNDEFINED} value indicates that field is
   * not set.
   * </p>
   *
   * @param nHour
   *        Hour to be created.
   * @param nMinute
   *        Minute to be created.
   * @param nSecond
   *        Second to be created.
   * @param nMilliSecond
   *        Milli second to be created.
   * @return <code>XMLGregorianCalendar</code> created from parameter values.
   * @see DatatypeConstants#FIELD_UNDEFINED
   * @throws IllegalArgumentException
   *         If any individual parameter's value is outside the maximum value
   *         constraint for the field as determined by the Date/Time Data
   *         Mapping table in {@link XMLGregorianCalendar} or if the composite
   *         values constitute an invalid <code>XMLGregorianCalendar</code>
   *         instance as determined by {@link XMLGregorianCalendar#isValid()}.
   */
  @Nonnull
  public static XMLGregorianCalendar getXMLCalendarTime (final int nHour, final int nMinute, final int nSecond, final int nMilliSecond)
  {
    return getXMLCalendarTime (nHour, nMinute, nSecond, nMilliSecond, DatatypeConstants.FIELD_UNDEFINED);
  }

  /**
   * <p>
   * Create a Java representation of XML Schema builtin datatype
   * <code>date</code> or <code>g*</code>.
   * </p>
   * <p>
   * For example, an instance of <code>gYear</code> can be created invoking this
   * factory with <code>month</code> and <code>day</code> parameters set to
   * {@link DatatypeConstants#FIELD_UNDEFINED}.
   * </p>
   * <p>
   * A {@link DatatypeConstants#FIELD_UNDEFINED} value indicates that field is
   * not set.
   * </p>
   *
   * @param nHour
   *        Hour to be created.
   * @param nMinute
   *        Minute to be created.
   * @param nSecond
   *        Second to be created.
   * @param nMilliSecond
   *        Milli second to be created.
   * @param nTimezone
   *        Offset in minutes. {@link DatatypeConstants#FIELD_UNDEFINED}
   *        indicates optional field is not set.
   * @return <code>XMLGregorianCalendar</code> created from parameter values.
   * @see DatatypeConstants#FIELD_UNDEFINED
   * @throws IllegalArgumentException
   *         If any individual parameter's value is outside the maximum value
   *         constraint for the field as determined by the Date/Time Data
   *         Mapping table in {@link XMLGregorianCalendar} or if the composite
   *         values constitute an invalid <code>XMLGregorianCalendar</code>
   *         instance as determined by {@link XMLGregorianCalendar#isValid()}.
   */
  @Nonnull
  public static XMLGregorianCalendar getXMLCalendarTime (final int nHour,
                                                         final int nMinute,
                                                         final int nSecond,
                                                         final int nMilliSecond,
                                                         final int nTimezone)
  {
    return DT_FACTORY.newXMLGregorianCalendarTime (nHour, nMinute, nSecond, nMilliSecond, nTimezone);
  }

  /**
   * Get the current date and time as {@link XMLGregorianCalendar} in the
   * default time zone.
   *
   * @return Never <code>null</code>.
   */
  @Nonnull
  public static XMLGregorianCalendar getXMLCalendarNow ()
  {
    return getXMLCalendar (PDTFactory.getCurrentZonedDateTime ());
  }

  /**
   * Get the current date and time as {@link XMLGregorianCalendar} in UTC.
   *
   * @return Never <code>null</code>.
   */
  @Nonnull
  public static XMLGregorianCalendar getXMLCalendarNowUTC ()
  {
    return getXMLCalendar (LocalDateTime.now (Clock.systemUTC ()));
  }

  /**
   * Get the passed object as {@link XMLGregorianCalendar} with date and time.
   *
   * @param aBase
   *        The source object. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static XMLGregorianCalendar getXMLCalendar (@Nullable final LocalDateTime aBase)
  {
    if (aBase == null)
      return null;
    return DT_FACTORY.newXMLGregorianCalendar (aBase.getYear (),
                                               aBase.getMonth ().getValue (),
                                               aBase.getDayOfMonth (),
                                               aBase.getHour (),
                                               aBase.getMinute (),
                                               aBase.getSecond (),
                                               aBase.get (ChronoField.MILLI_OF_SECOND),
                                               DatatypeConstants.FIELD_UNDEFINED);
  }

  /**
   * Get the passed object as {@link XMLGregorianCalendar} with date and time.
   *
   * @param aBase
   *        The source object. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static XMLGregorianCalendar getXMLCalendar (@Nullable final ZonedDateTime aBase)
  {
    if (aBase == null)
      return null;
    return DT_FACTORY.newXMLGregorianCalendar (GregorianCalendar.from (aBase));
  }

  /**
   * Get the passed {@link GregorianCalendar} as {@link XMLGregorianCalendar}.
   *
   * @param aCal
   *        Source calendar. May be <code>null</code>.
   * @return <code>null</code> if the passed calendar is <code>null</code>.
   */
  @Nullable
  public static XMLGregorianCalendar getXMLCalendar (@Nullable final GregorianCalendar aCal)
  {
    if (aCal == null)
      return null;
    return DT_FACTORY.newXMLGregorianCalendar (aCal);
  }

  /**
   * Create a new {@link XMLGregorianCalendar} using separate objects for date
   * and time.
   *
   * @param aDate
   *        Source date. May be <code>null</code>.
   * @param aTime
   *        Source time. May be <code>null</code>.
   * @return <code>null</code> if the passed date and time are <code>null</code>
   *         .
   */
  @Nullable
  public static XMLGregorianCalendar getXMLCalendar (@Nullable final XMLGregorianCalendar aDate, @Nullable final XMLGregorianCalendar aTime)
  {
    if (aDate == null && aTime == null)
      return null;
    if (aTime == null)
    {
      // Date only
      return DT_FACTORY.newXMLGregorianCalendar (aDate.getYear (), aDate.getMonth (), aDate.getDay (), 0, 0, 0, 0, aDate.getTimezone ());
    }
    if (aDate == null)
    {
      // Time only
      return DT_FACTORY.newXMLGregorianCalendar (0,
                                                 0,
                                                 0,
                                                 aTime.getHour (),
                                                 aTime.getMinute (),
                                                 aTime.getSecond (),
                                                 aTime.getMillisecond (),
                                                 aTime.getTimezone ());
    }

    if (aDate.getTimezone () != aTime.getTimezone ())
      LOGGER.warn ("Date and time have different timezones: " + aDate.getTimezone () + " vs. " + aTime.getTimezone ());
    return DT_FACTORY.newXMLGregorianCalendar (aDate.getYear (),
                                               aDate.getMonth (),
                                               aDate.getDay (),
                                               aTime.getHour (),
                                               aTime.getMinute (),
                                               aTime.getSecond (),
                                               aTime.getMillisecond (),
                                               aDate.getTimezone ());
  }

  /**
   * Get the passed milliseconds as {@link XMLGregorianCalendar}.
   *
   * @param nMillis
   *        Milliseconds since 1.1.1970
   * @return Never <code>null</code>.
   */
  @Nonnull
  public static XMLGregorianCalendar getXMLCalendar (final long nMillis)
  {
    return DT_FACTORY.newXMLGregorianCalendar (getCalendarDefaultTimeZone (nMillis));
  }

  /**
   * Get the passed milliseconds as {@link XMLGregorianCalendar}.
   *
   * @param nMillis
   *        Milliseconds since 1.1.1970
   * @return Never <code>null</code>.
   * @since 9.1.8
   */
  @Nonnull
  public static XMLGregorianCalendar getXMLCalendarUTC (final long nMillis)
  {
    return DT_FACTORY.newXMLGregorianCalendar (getCalendarUTC (nMillis));
  }

  /**
   * Get the passed {@link Date} as {@link XMLGregorianCalendar}.
   *
   * @param aDate
   *        Source date. May be <code>null</code>.
   * @return <code>null</code> if the passed date is <code>null</code>.
   */
  @Nullable
  public static XMLGregorianCalendar getXMLCalendar (@Nullable final Date aDate)
  {
    if (aDate == null)
      return null;
    return DT_FACTORY.newXMLGregorianCalendar (getCalendar (aDate));
  }

  /**
   * Convert the passed {@link XMLGregorianCalendar} to a
   * {@link GregorianCalendar}.
   *
   * @param aCal
   *        Source calendar. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static GregorianCalendar getGregorianCalendar (@Nullable final XMLGregorianCalendar aCal)
  {
    if (aCal == null)
      return null;
    return aCal.toGregorianCalendar (aCal.getTimeZone (aCal.getTimezone ()), Locale.getDefault (Locale.Category.FORMAT), null);
  }

  /**
   * Get the passed {@link XMLGregorianCalendar} as {@link LocalDate}.
   *
   * @param aCal
   *        The source {@link XMLGregorianCalendar}. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static LocalDate getLocalDate (@Nullable final XMLGregorianCalendar aCal)
  {
    if (aCal == null)
      return null;
    return getGregorianCalendar (aCal).toZonedDateTime ().toLocalDate ();
  }

  /**
   * Get the passed {@link XMLGregorianCalendar} as {@link LocalTime}.
   *
   * @param aCal
   *        The source {@link XMLGregorianCalendar}. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static LocalTime getLocalTime (@Nullable final XMLGregorianCalendar aCal)
  {
    if (aCal == null)
      return null;
    return getGregorianCalendar (aCal).toZonedDateTime ().toLocalTime ();
  }

  /**
   * Get the passed {@link XMLGregorianCalendar} as {@link LocalDateTime}.
   *
   * @param aCal
   *        The source {@link XMLGregorianCalendar}. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static LocalDateTime getLocalDateTime (@Nullable final XMLGregorianCalendar aCal)
  {
    if (aCal == null)
      return null;
    return getGregorianCalendar (aCal).toZonedDateTime ().toLocalDateTime ();
  }

  /**
   * Get the passed {@link XMLGregorianCalendar} as {@link ZonedDateTime}.
   *
   * @param aCal
   *        The source {@link XMLGregorianCalendar}. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static ZonedDateTime getZonedDateTime (@Nullable final XMLGregorianCalendar aCal)
  {
    if (aCal == null)
      return null;
    return getGregorianCalendar (aCal).toZonedDateTime ();
  }

  /**
   * Get the passed {@link XMLGregorianCalendar} as {@link Date}.
   *
   * @param aCal
   *        The source {@link XMLGregorianCalendar}. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>.
   */
  @Nullable
  public static Date getDate (@Nullable final XMLGregorianCalendar aCal)
  {
    final GregorianCalendar aGregorianCalendar = getGregorianCalendar (aCal);
    return aGregorianCalendar == null ? null : aGregorianCalendar.getTime ();
  }

  /**
   * Get the passed {@link XMLGregorianCalendar} as milliseconds.
   *
   * @param aCal
   *        The source {@link XMLGregorianCalendar}. May be <code>null</code>.
   * @return <code>{@link CGlobal#ILLEGAL_ULONG}</code> if the parameter is
   *         <code>null</code>.
   */
  @CheckForSigned
  public static long getMillis (@Nullable final XMLGregorianCalendar aCal)
  {
    final GregorianCalendar aGregorianCalendar = getGregorianCalendar (aCal);
    return aGregorianCalendar == null ? CGlobal.ILLEGAL_ULONG : aGregorianCalendar.getTimeInMillis ();
  }
}
