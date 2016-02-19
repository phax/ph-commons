package com.helger.commons.collection.ext;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.ReturnsMutableCopy;

public class CommonsTreeMap <KEYTYPE, VALUETYPE> extends TreeMap <KEYTYPE, VALUETYPE>
                            implements ICommonsMap <KEYTYPE, VALUETYPE>
{
  public CommonsTreeMap ()
  {}

  public CommonsTreeMap (@Nullable final Comparator <? super KEYTYPE> aComparator)
  {
    super (aComparator);
  }

  public CommonsTreeMap (@Nonnull final Map <? extends KEYTYPE, ? extends VALUETYPE> aMap)
  {
    super (aMap);
  }

  @Nonnull
  @ReturnsMutableCopy
  public CommonsTreeMap <KEYTYPE, VALUETYPE> getClone ()
  {
    final CommonsTreeMap <KEYTYPE, VALUETYPE> ret = new CommonsTreeMap <> (comparator ());
    ret.putAll (this);
    return ret;
  }
}
