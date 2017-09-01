package com.helger.cli;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsLinkedHashMap;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsOrderedMap;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;

@NotThreadSafe
public class ParsedCmdLine
{
  private final ICommonsOrderedMap <IOptionBase, ICommonsList <String>> m_aParams = new CommonsLinkedHashMap <> ();
  private final ICommonsList <String> m_aUnknownTokens = new CommonsArrayList <> ();

  public ParsedCmdLine ()
  {}

  public void internalAddValue (@Nonnull final IOptionBase aOption, @Nonnull final ICommonsList <String> aValues)
  {
    ValueEnforcer.notNull (aOption, "Option");
    ValueEnforcer.notNull (aValues, "Values");

    m_aParams.computeIfAbsent (aOption, k -> new CommonsArrayList <> ()).addAll (aValues);
  }

  void internalAddUnhandledToken (@Nonnull @Nonempty final String sUnknownToken)
  {
    ValueEnforcer.notEmpty (sUnknownToken, "UnknownToken");
    m_aUnknownTokens.add (sUnknownToken);
  }

  @Nullable
  private ICommonsList <String> _find (@Nullable final IOptionBase aOption)
  {
    return aOption == null ? null : m_aParams.get (aOption);
  }

  private static boolean _matches (@Nonnull final Option aOption, @Nonnull @Nonempty final String sOption)
  {
    return aOption.hasShortOpt (sOption) || aOption.hasLongOpt (sOption);
  }

  @Nullable
  private ICommonsList <String> _find (@Nullable final String sOption)
  {
    if (StringHelper.hasNoText (sOption))
      return null;

    for (final Map.Entry <IOptionBase, ICommonsList <String>> aEntry : m_aParams.entrySet ())
      if (aEntry.getKey () instanceof Option)
      {
        if (_matches ((Option) aEntry.getKey (), sOption))
          return aEntry.getValue ();
      }
      else
        // Do not resolve option groups, as the resolution happens on insertion!
        if (false)
        {
          for (final Option aOption : (OptionGroup) aEntry.getKey ())
            if (_matches (aOption, sOption))
              return aEntry.getValue ();
        }
    return null;
  }

  public boolean hasOption (@Nullable final IOptionBase aOption)
  {
    return _find (aOption) != null;
  }

  public boolean hasOption (@Nullable final String sOption)
  {
    return _find (sOption) != null;
  }

  @Nullable
  public String getValue (@Nonnull final IOptionBase aOption)
  {
    final ICommonsList <String> aValues = _find (aOption);
    return aValues == null ? null : aValues.getFirst ();
  }

  @Nullable
  public String getValue (@Nonnull final String sOption)
  {
    final ICommonsList <String> aValues = _find (sOption);
    return aValues == null ? null : aValues.getFirst ();
  }

  @Nullable
  public ICommonsList <String> getAllValues (@Nonnull final IOptionBase aOption)
  {
    final ICommonsList <String> aValues = _find (aOption);
    return aValues == null ? null : aValues.getClone ();
  }

  @Nullable
  public ICommonsList <String> getAllValues (@Nonnull final String sOption)
  {
    final ICommonsList <String> aValues = _find (sOption);
    return aValues == null ? null : aValues.getClone ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("Params", m_aParams)
                                       .append ("UnknownTokens", m_aUnknownTokens)
                                       .getToString ();
  }
}
