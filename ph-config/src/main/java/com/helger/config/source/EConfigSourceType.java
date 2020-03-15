package com.helger.config.source;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.id.IHasID;
import com.helger.commons.lang.EnumHelper;

/**
 * Defines the type of configuration sources and the respective default
 * priority.
 *
 * @author Philip Helger
 */
public enum EConfigSourceType implements IHasID <String>
{
  SYSTEM_PROPERTY ("sysprop", 400),
  ENVIRONMENT_VARIABLE ("envvar", 300),
  FILE ("file", 200),
  APPLICATION ("appl", 100);

  private final String m_sID;
  private final int m_nDefaultPriority;

  private EConfigSourceType (@Nonnull @Nonempty final String sID, @Nonnegative final int nDefaultPriority)
  {
    m_sID = sID;
    m_nDefaultPriority = nDefaultPriority;
  }

  @Nonnull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  /**
   * @return The default priority for a configuration source of this type.
   *         Always &gt; 0.
   */
  @Nonnegative
  public int getDefaultPriority ()
  {
    return m_nDefaultPriority;
  }

  @Nullable
  public static EConfigSourceType getFromIDOrNull (@Nullable final String sID)
  {
    return EnumHelper.getFromIDOrNull (EConfigSourceType.class, sID);
  }
}
