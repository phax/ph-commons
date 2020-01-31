package com.helger.config.source;

import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;

/**
 * Abstract base class for an abstract configuration source.
 *
 * @author Philip Helger
 */
public abstract class AbstractConfigurationSource implements IConfigurationSource
{
  private final EConfigSourceType m_eType;
  private final int m_nPriority;

  protected AbstractConfigurationSource (@Nonnull final EConfigSourceType eType, final int nPriority)
  {
    ValueEnforcer.notNull (eType, "Type");
    m_eType = eType;
    m_nPriority = nPriority;
  }

  @Nonnull
  public final EConfigSourceType getType ()
  {
    return m_eType;
  }

  public final int getPriority ()
  {
    return m_nPriority;
  }
}
