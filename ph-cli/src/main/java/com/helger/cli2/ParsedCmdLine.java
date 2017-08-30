package com.helger.cli2;

import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsLinkedHashMap;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsOrderedMap;

public class ParsedCmdLine
{
  private final ICommonsOrderedMap <Option, ICommonsList <String>> m_aParams = new CommonsLinkedHashMap <> ();

  public ParsedCmdLine ()
  {}

  void addValue (@Nonnull final Option aOption, @Nonnull @Nonempty final String sValue)
  {
    ValueEnforcer.notNull (aOption, "Option");
    ValueEnforcer.notEmpty (sValue, "Value");

    m_aParams.computeIfAbsent (aOption, k -> new CommonsArrayList <> ()).add (sValue);
  }
}
