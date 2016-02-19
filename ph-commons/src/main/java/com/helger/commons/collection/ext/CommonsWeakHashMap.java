package com.helger.commons.collection.ext;

import java.util.Map;
import java.util.WeakHashMap;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.ReturnsMutableCopy;

public class CommonsWeakHashMap <KEYTYPE, VALUETYPE> extends WeakHashMap <KEYTYPE, VALUETYPE>
                                implements ICommonsMap <KEYTYPE, VALUETYPE>
{
  public CommonsWeakHashMap ()
  {}

  public CommonsWeakHashMap (final int nInitialCapacity)
  {
    super (nInitialCapacity);
  }

  public CommonsWeakHashMap (final int nInitialCapacity, final float fLoadFactor)
  {
    super (nInitialCapacity, fLoadFactor);
  }

  public CommonsWeakHashMap (@Nonnull final Map <? extends KEYTYPE, ? extends VALUETYPE> aMap)
  {
    super (aMap);
  }

  @Nonnull
  @ReturnsMutableCopy
  public CommonsWeakHashMap <KEYTYPE, VALUETYPE> getClone ()
  {
    return new CommonsWeakHashMap <> (this);
  }
}
