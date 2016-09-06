package com.helger.commons.error.text;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;
import com.helger.commons.text.IMultilingualText;

/**
 * A implementation of {@link IHasErrorText} based on {@link IMultilingualText}
 * because this class is Serializable and implements equals/hashCode. Cannot be
 * a lambda expression because equals/hashCode is required!
 *
 * @author Philip Helger
 */
@Immutable
public class DynamicHasErrorText implements IHasErrorText
{
  private final IMultilingualText m_aText;

  public DynamicHasErrorText (@Nullable final IMultilingualText aText)
  {
    m_aText = ValueEnforcer.notNull (aText, "Text");
  }

  @Nullable
  public String getDisplayText (@Nonnull final Locale aContentLocale)
  {
    return m_aText.getText (aContentLocale);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final DynamicHasErrorText rhs = (DynamicHasErrorText) o;
    return EqualsHelper.equals (m_aText, rhs.m_aText);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aText).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("MLT", m_aText).toString ();
  }
}
