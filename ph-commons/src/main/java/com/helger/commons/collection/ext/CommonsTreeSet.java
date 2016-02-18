package com.helger.commons.collection.ext;

import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.ReturnsMutableCopy;

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

  @Nonnull
  @ReturnsMutableCopy
  public <T> CommonsTreeSet <T> createInstance ()
  {
    return new CommonsTreeSet <> ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public CommonsTreeSet <ELEMENTTYPE> getClone ()
  {
    final CommonsTreeSet <ELEMENTTYPE> ret = new CommonsTreeSet <> (comparator ());
    ret.addAll (this);
    return ret;
  }
}
