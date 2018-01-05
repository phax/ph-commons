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

import java.io.Serializable;
import java.time.ZoneId;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.string.ToStringGenerator;

/**
 * A combination of a date/time string and an optional {@link ZoneId}.
 *
 * @author Philip Helger
 * @since 9.0.0
 */
public class WithZoneId implements Serializable
{
  private final String m_sStr;
  private final ZoneId m_aZoneId;

  public WithZoneId (@Nonnull final String sStr, @Nullable final ZoneId aZoneId)
  {
    m_sStr = ValueEnforcer.notNull (sStr, "Str");
    m_aZoneId = aZoneId;
  }

  @Nonnull
  public String getString ()
  {
    return m_sStr;
  }

  @Nullable
  public ZoneId getZoneId ()
  {
    return m_aZoneId;
  }

  public boolean hasZoneId ()
  {
    return m_aZoneId != null;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("String", m_sStr).append ("ZoneId", m_aZoneId).getToString ();
  }
}
