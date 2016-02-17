package com.helger.commons.collection.ext;

import java.util.SortedMap;

import javax.annotation.Nullable;

public interface ICommonsSortedMap <KEYTYPE, VALUETYPE>
                                   extends SortedMap <KEYTYPE, VALUETYPE>, ICommonsMap <KEYTYPE, VALUETYPE>
{
  @Nullable
  default KEYTYPE getFirstKey ()
  {
    return isEmpty () ? null : firstKey ();
  }
}
