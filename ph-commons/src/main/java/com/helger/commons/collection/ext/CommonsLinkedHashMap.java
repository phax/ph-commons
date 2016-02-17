package com.helger.commons.collection.ext;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nonnull;

public class CommonsLinkedHashMap <KEYTYPE, VALUETYPE> extends LinkedHashMap <KEYTYPE, VALUETYPE>
                                  implements ICommonsMap <KEYTYPE, VALUETYPE>
{
  public CommonsLinkedHashMap ()
  {}

  public CommonsLinkedHashMap (final int nInitialCapacity)
  {
    super (nInitialCapacity);
  }

  public CommonsLinkedHashMap (final int nInitialCapacity, final float fLoadFactor)
  {
    super (nInitialCapacity, fLoadFactor);
  }

  public CommonsLinkedHashMap (@Nonnull final Map <? extends KEYTYPE, ? extends VALUETYPE> aMap)
  {
    super (aMap);
  }
}
