package com.helger.commons.collection.ext;

import java.util.Collections;
import java.util.NavigableSet;
import java.util.SortedSet;

import javax.annotation.Nonnull;

public interface ICommonsNavigableSet <ELEMENTTYPE> extends NavigableSet <ELEMENTTYPE>, ICommonsSortedSet <ELEMENTTYPE>
{
  @Nonnull
  default SortedSet <ELEMENTTYPE> getAsUnmodifiable ()
  {
    return Collections.unmodifiableSortedSet (this);
  }
}
