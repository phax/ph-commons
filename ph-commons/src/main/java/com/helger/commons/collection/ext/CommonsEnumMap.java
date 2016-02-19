package com.helger.commons.collection.ext;

import java.util.EnumMap;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.ReturnsMutableCopy;

public class CommonsEnumMap <KEYTYPE extends Enum <KEYTYPE>, VALUETYPE> extends EnumMap <KEYTYPE, VALUETYPE>
                            implements ICommonsMap <KEYTYPE, VALUETYPE>
{
  public CommonsEnumMap (@Nonnull final Class <KEYTYPE> aKeyClass)
  {
    super (aKeyClass);
  }

  public CommonsEnumMap (@Nonnull final EnumMap <KEYTYPE, ? extends VALUETYPE> aMap)
  {
    super (aMap);
  }

  @Nonnull
  @ReturnsMutableCopy
  public CommonsEnumMap <KEYTYPE, VALUETYPE> getClone ()
  {
    return new CommonsEnumMap <> (this);
  }
}
