package com.helger.cli2;

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;

/**
 * Manager for {@link IOptionBase} objects which may be {@link Option} or
 * {@link OptionGroup}.
 * 
 * @author Philip Helger
 */
public class Options implements Serializable
{
  private final ICommonsList <IOptionBase> m_aOptions = new CommonsArrayList <> ();

  public Options ()
  {}

  @Nonnull
  public Options addOption (@Nonnull final Option.Builder aBuilder)
  {
    return addOption (aBuilder.build ());
  }

  @Nonnull
  public Options addOption (@Nonnull final Option aOption)
  {
    ValueEnforcer.notNull (aOption, "Option");
    m_aOptions.add (aOption);
    return this;
  }

  @Nonnull
  public Options addOptionGroup (@Nonnull final OptionGroup aOptionGroup)
  {
    ValueEnforcer.notNull (aOptionGroup, "OptionGroup");
    m_aOptions.add (aOptionGroup);
    return this;
  }

  @Nullable
  public OptionGroup getOptionGroup (final Option aOption)
  {
    // TODO
    return null;
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <Option> getAllOptions ()
  {
    return m_aOptions.getAllInstanceOf (Option.class);
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <OptionGroup> getAllOptionGroups ()
  {
    return m_aOptions.getAllInstanceOf (OptionGroup.class);
  }
}
