package com.helger.commons.collection.ext;

import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CommonsTreeSet <ELEMENTTYPE> extends TreeSet <ELEMENTTYPE> implements ICommonsNavigableSet <ELEMENTTYPE>
{
  public CommonsTreeSet ()
  {}

  public CommonsTreeSet (@Nullable final Comparator <? super ELEMENTTYPE> aComparator)
  {
    super (aComparator);
  }

  public CommonsTreeSet (@Nonnull final Collection <? extends ELEMENTTYPE> aCont)
  {
    super (aCont);
  }
}
