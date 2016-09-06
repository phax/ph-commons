package com.helger.commons.error.text;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * A constant implementation of {@link IHasErrorText}. Cannot be a lambda
 * expression because equals/hashCode is required!
 *
 * @author Philip Helger
 */
@Immutable
public class ConstantHasErrorText implements IHasErrorText
{
  private final String m_sText;

  public ConstantHasErrorText (@Nullable final String sText)
  {
    m_sText = sText;
  }

  @Nullable
  public String getDisplayText (@Nonnull final Locale aContentLocale)
  {
    return m_sText;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final ConstantHasErrorText rhs = (ConstantHasErrorText) o;
    return EqualsHelper.equals (m_sText, rhs.m_sText);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_sText).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("Text", m_sText).toString ();
  }
}
