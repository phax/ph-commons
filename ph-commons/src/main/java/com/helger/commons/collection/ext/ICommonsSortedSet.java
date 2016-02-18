package com.helger.commons.collection.ext;

import java.util.Collections;
import java.util.SortedSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ICommonsSortedSet <ELEMENTTYPE> extends SortedSet <ELEMENTTYPE>, ICommonsSet <ELEMENTTYPE>
{
  @Nullable
  default ELEMENTTYPE getFirst ()
  {
    return isEmpty () ? null : first ();
  }

  @Nullable
  default ELEMENTTYPE getLast ()
  {
    return isEmpty () ? null : last ();
  }

  @Nonnull
  default SortedSet <ELEMENTTYPE> getAsUnmodifiable ()
  {
    return Collections.unmodifiableSortedSet (this);
  }
}
