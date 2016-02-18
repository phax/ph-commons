package com.helger.commons.collection.ext;

import java.util.Collection;
import java.util.HashSet;

import javax.annotation.Nonnull;

public class CommonsHashSet <T> extends HashSet <T> implements ICommonsSet <T>
{
  public CommonsHashSet ()
  {}

  public CommonsHashSet (final int nInitialCapacity)
  {
    super (nInitialCapacity);
  }

  public CommonsHashSet (final int nInitialCapacity, final float fLoadFactor)
  {
    super (nInitialCapacity, fLoadFactor);
  }

  public CommonsHashSet (@Nonnull final Collection <? extends T> aCont)
  {
    super (aCont);
  }
}
