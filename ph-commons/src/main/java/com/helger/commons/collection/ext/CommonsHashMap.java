package com.helger.commons.collection.ext;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.ReturnsMutableCopy;

public class CommonsHashMap <KEYTYPE, VALUETYPE> extends HashMap <KEYTYPE, VALUETYPE>
                            implements ICommonsMap <KEYTYPE, VALUETYPE>
{
  public CommonsHashMap ()
  {}

  public CommonsHashMap (final int nInitialCapacity)
  {
    super (nInitialCapacity);
  }

  public CommonsHashMap (final int nInitialCapacity, final float fLoadFactor)
  {
    super (nInitialCapacity, fLoadFactor);
  }

  public CommonsHashMap (@Nonnull final Map <? extends KEYTYPE, ? extends VALUETYPE> aMap)
  {
    super (aMap);
  }

  @Nonnull
  @ReturnsMutableCopy
  public CommonsHashMap <KEYTYPE, VALUETYPE> getClone ()
  {
    return new CommonsHashMap <> (this);
  }
}
