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
