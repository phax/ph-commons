package com.helger.cli2;

import java.util.Iterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsIterable;
import com.helger.commons.collection.impl.ICommonsList;

/**
 * Manager for {@link IOptionBase} objects which may be {@link Option} or
 * {@link OptionGroup}.
 *
 * @author Philip Helger
 */
public class Options implements ICommonsIterable <IOptionBase>
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

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <Option> getAllOptions ()
  {
    return m_aOptions.getAllInstanceOf (Option.class);
  }

  @Nonnull
  public Iterator <IOptionBase> iterator ()
  {
    return m_aOptions.iterator ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <Option> getAllResolvedOptions ()
  {
    final ICommonsList <Option> ret = new CommonsArrayList <> ();
    for (final IOptionBase aOptionBase : m_aOptions)
      if (aOptionBase instanceof Option)
        ret.add ((Option) aOptionBase);
      else
        ret.addAll (((OptionGroup) aOptionBase).getAllOptions ());
    return ret;
  }

  @Nullable
  public OptionGroup getOptionGroup (@Nullable final Option aOption)
  {
    if (aOption != null)
      for (final IOptionBase aOptionBase : m_aOptions)
        if (aOptionBase instanceof OptionGroup)
        {
          final OptionGroup aOptionGroup = (OptionGroup) aOptionBase;
          if (aOptionGroup.contains (aOption))
            return aOptionGroup;
        }
    return null;
  }
}
