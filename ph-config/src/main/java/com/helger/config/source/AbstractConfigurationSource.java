package com.helger.config.source;

import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.string.ToStringGenerator;

/**
 * Abstract base class for an abstract configuration source.
 *
 * @author Philip Helger
 */
public abstract class AbstractConfigurationSource implements IConfigurationSource
{
  private final EConfigSourceType m_eType;
  private final int m_nPriority;

  /**
   * Constructor
   * 
   * @param eType
   *        Configuration source type. May not be <code>null</code>.
   * @param nPriority
   *        The priority to use. The higher the more important.
   */
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

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Type", m_eType).append ("Priority", m_nPriority).getToString ();
  }
}
