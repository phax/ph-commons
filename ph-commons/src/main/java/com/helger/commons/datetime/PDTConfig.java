/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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

import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.TimeZone;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.state.ESuccess;

/**
 * This class provides the most basic settings for date time operating: the
 * date-time-zone and the chronology to use.
 *
 * @author Philip Helger
 */
@ThreadSafe
public final class PDTConfig
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (PDTConfig.class);

  private static ZoneId s_aDefaultZoneId = TimeZone.getDefault ().toZoneId ();

  @PresentForCodeCoverage
  private static final PDTConfig s_aInstance = new PDTConfig ();

  private PDTConfig ()
  {}

  /**
   * Set the default date time zone to use. This effects all objects created via
   * {@link PDTFactory} as well as the default JDK TimeZone.
   *
   * @param sDateTimeZoneID
   *        Must be a valid, non-<code>null</code> time zone.
   * @return {@link ESuccess}
   */
  @Nonnull
  public static ESuccess setDefaultDateTimeZoneID (@Nonnull @Nonempty final String sDateTimeZoneID)
  {
    ValueEnforcer.notEmpty (sDateTimeZoneID, "DateTimeZoneID");

    try
    {
      // Try to resolve ID -> throws exception if unknown
      final ZoneId aDefaultZoneId = ZoneId.of (sDateTimeZoneID);

      // getTimeZone falls back to GMT if unknown
      final TimeZone aDefaultTimeZone = TimeZone.getTimeZone (aDefaultZoneId);

      s_aDefaultZoneId = aDefaultZoneId;
      TimeZone.setDefault (aDefaultTimeZone);
      return ESuccess.SUCCESS;
    }
    catch (final DateTimeException ex)
    {
      // time zone ID is unknown
      s_aLogger.warn ("Unsupported ZoneId '" + sDateTimeZoneID + "'", ex);
      return ESuccess.FAILURE;
    }
  }

  /**
   * @return The default date time zone to use. Never <code>null</code>. The
   *         default is defined by the system.
   */
  @Nonnull
  public static ZoneId getDefaultZoneId ()
  {
    return s_aDefaultZoneId;
  }

  @Nonnull
  public static ZoneId getUTCZoneId ()
  {
    return ZoneOffset.UTC;
  }

  /**
   * @return The default time zone to use. Never <code>null</code>. The default
   *         is is defined by the system.
   */
  @Nonnull
  public static TimeZone getDefaultTimeZone ()
  {
    return TimeZone.getDefault ();
  }
}
