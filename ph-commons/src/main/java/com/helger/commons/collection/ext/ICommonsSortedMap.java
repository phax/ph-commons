package com.helger.commons.collection.ext;

import java.util.Collections;
import java.util.SortedMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ICommonsSortedMap <KEYTYPE, VALUETYPE>
                                   extends SortedMap <KEYTYPE, VALUETYPE>, ICommonsMap <KEYTYPE, VALUETYPE>
{
  @Nullable
  default KEYTYPE getFirstKey ()
  {
    return isEmpty () ? null : firstKey ();
  }

  @Nullable
  default VALUETYPE getFirstValue ()
  {
    final KEYTYPE aKey = getFirstKey ();
    return aKey == null ? null : get (aKey);
  }

  @Nullable
  default KEYTYPE getLastKey ()
  {
    return isEmpty () ? null : lastKey ();
  }

  @Nullable
  default VALUETYPE getLastValue ()
  {
    final KEYTYPE aKey = getLastKey ();
    return aKey == null ? null : get (aKey);
  }

  @Nonnull
  default SortedMap <KEYTYPE, VALUETYPE> getAsUnmodifiable ()
  {
    return Collections.unmodifiableSortedMap (this);
  }
}
