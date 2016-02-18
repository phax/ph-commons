package com.helger.commons.collection.ext;

import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CommonsTreeSet <T> extends TreeSet <T> implements ICommonsNavigableSet <T>
{
  public CommonsTreeSet ()
  {}

  public CommonsTreeSet (@Nullable final Comparator <? super T> aComparator)
  {
    super (aComparator);
  }

  public CommonsTreeSet (@Nonnull final Collection <? extends T> aCont)
  {
    super (aCont);
  }
}
