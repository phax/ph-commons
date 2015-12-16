package com.helger.commons.text.display;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * An implementation of the {@link IHasDisplayText} interface that always
 * returns a constant string. Use this only for texts that never need to be
 * translated!
 *
 * @author Philip Helger
 */
@Immutable
public class ConstantHasDisplayText implements IHasDisplayText
{
  private final String m_sFixedText;

  public ConstantHasDisplayText (@Nonnull final String sFixedText)
  {
    m_sFixedText = ValueEnforcer.notNull (sFixedText, "FixedText");
  }

  @Override
  @Nullable
  public String getDisplayText (@Nonnull final Locale aContentLocale)
  {
    return m_sFixedText;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final ConstantHasDisplayText rhs = (ConstantHasDisplayText) o;
    return EqualsHelper.equals (m_sFixedText, rhs.m_sFixedText);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_sFixedText).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("FixedText", m_sFixedText).toString ();
  }
}
