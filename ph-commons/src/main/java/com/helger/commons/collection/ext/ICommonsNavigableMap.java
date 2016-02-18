package com.helger.commons.collection.ext;

import java.util.Collections;
import java.util.NavigableMap;

import javax.annotation.Nonnull;

public interface ICommonsNavigableMap <KEYTYPE, VALUETYPE>
                                      extends NavigableMap <KEYTYPE, VALUETYPE>, ICommonsSortedMap <KEYTYPE, VALUETYPE>
{
  @Nonnull
  default NavigableMap <KEYTYPE, VALUETYPE> getAsUnmodifiable ()
  {
    return Collections.unmodifiableNavigableMap (this);
  }
}
