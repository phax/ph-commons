package com.helger.cli2;

import java.io.Serializable;

import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;

public final class OptionValue implements Serializable
{
  private final Option m_aOption;
  private final ICommonsList <String> m_aValues = new CommonsArrayList <> ();

  public OptionValue (@Nonnull final Option aOption)
  {
    m_aOption = ValueEnforcer.notNull (aOption, "Option");
  }

  @Nonnull
  @ReturnsMutableObject
  public Option option ()
  {
    return m_aOption;
  }

  @Nonnull
  @ReturnsMutableObject
  public ICommonsList <String> values ()
  {
    return m_aValues;
  }
}
