package com.helger.config.source;

import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.MustImplementEqualsAndHashcode;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * Abstract base class for an abstract configuration source.
 *
 * @author Philip Helger
 */
@MustImplementEqualsAndHashcode
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
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final AbstractConfigurationSource rhs = (AbstractConfigurationSource) o;
    return m_eType.equals (rhs.m_eType) && m_nPriority == rhs.m_nPriority;
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_eType).append (m_nPriority).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Type", m_eType).append ("Priority", m_nPriority).getToString ();
  }
}
