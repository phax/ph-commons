package com.helger.config.source;

import javax.annotation.Nonnegative;

/**
 * Defines the type of configuration sources
 *
 * @author Philip Helger
 */
public enum EConfigSourceType
{
  SYSTEM_PROPERTY (400),
  ENVIRONMENT_VARIABLE (300),
  FILE (200),
  APPLICATION (100);

  private int m_nDefaultPriority;

  private EConfigSourceType (@Nonnegative final int nDefaultPriority)
  {
    m_nDefaultPriority = nDefaultPriority;
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
}
