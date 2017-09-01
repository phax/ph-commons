package com.helger.cli;

import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;

public class OptionGroup extends CommonsArrayList <Option> implements IOptionBase
{
  private boolean m_bRequired = false;

  public OptionGroup ()
  {}

  @Nonnull
  public OptionGroup addOption (@Nonnull final Option.Builder aBuilder)
  {
    return addOption (aBuilder.build ());
  }

  @Nonnull
  public OptionGroup addOption (@Nonnull final Option aOption)
  {
    ValueEnforcer.notNull (aOption, "Option");
    add (aOption);
    return this;
  }

  public boolean isRequired ()
  {
    return m_bRequired;
  }

  @Nonnull
  public OptionGroup setRequired (final boolean bRequired)
  {
    m_bRequired = bRequired;
    return this;
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <Option> getAllOptions ()
  {
    return getClone ();
  }

  @Override
  public boolean equals (final Object o)
  {
    if (this == o)
      return true;
    if (o == null || getClass () != o.getClass ())
      return false;

    return super.equals (o);
  }

  @Override
  public int hashCode ()
  {
    return super.hashCode ();
  }
}
