package com.helger.commons.collection.ext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.ReturnsMutableCopy;

public class CommonsConcurrentHashMap <KEYTYPE, VALUETYPE> extends ConcurrentHashMap <KEYTYPE, VALUETYPE>
                                      implements ICommonsMap <KEYTYPE, VALUETYPE>
{
  public CommonsConcurrentHashMap ()
  {}

  public CommonsConcurrentHashMap (final int nInitialCapacity)
  {
    super (nInitialCapacity);
  }

  public CommonsConcurrentHashMap (final int nInitialCapacity, final float fLoadFactor)
  {
    super (nInitialCapacity, fLoadFactor);
  }

  public CommonsConcurrentHashMap (@Nonnull final Map <? extends KEYTYPE, ? extends VALUETYPE> aMap)
  {
    super (aMap);
  }

  @Nonnull
  @ReturnsMutableCopy
  public CommonsConcurrentHashMap <KEYTYPE, VALUETYPE> getClone ()
  {
    return new CommonsConcurrentHashMap <> (this);
  }
}
