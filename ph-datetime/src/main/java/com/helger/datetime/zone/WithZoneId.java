/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.datetime.zone;

import java.time.ZoneId;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.tostring.ToStringGenerator;

/**
 * A combination of a date/time string and an optional {@link ZoneId}.
 *
 * @author Philip Helger
 * @since 9.0.0
 */
public class WithZoneId
{
  private final String m_sStr;
  private final ZoneId m_aZoneID;

  /**
   * Constructor.
   *
   * @param sStr
   *        The date/time string. May not be <code>null</code>.
   * @param aZoneID
   *        The optional zone ID. May be <code>null</code>.
   */
  public WithZoneId (@NonNull final String sStr, @Nullable final ZoneId aZoneID)
  {
    m_sStr = ValueEnforcer.notNull (sStr, "Str");
    m_aZoneID = aZoneID;
  }

  /**
   * @return The date/time string. Never <code>null</code>.
   */
  @NonNull
  public String getString ()
  {
    return m_sStr;
  }

  /**
   * @return The zone ID. May be <code>null</code>.
   */
  @Nullable
  public ZoneId getZoneID ()
  {
    return m_aZoneID;
  }

  /**
   * @return <code>true</code> if a zone ID is present, <code>false</code> if
   *         not.
   */
  public boolean hasZoneID ()
  {
    return m_aZoneID != null;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("String", m_sStr).append ("ZoneID", m_aZoneID).getToString ();
  }
}
