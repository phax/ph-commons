package com.helger.commons.collection.ext;

import java.util.Collection;
import java.util.HashSet;

import javax.annotation.Nonnull;

public class CommonsSet <T> extends HashSet <T> implements ICommonsSet <T>
{
  public CommonsSet ()
  {}

  public CommonsSet (final int nInitialCapacity)
  {
    super (nInitialCapacity);
  }

  public CommonsSet (final int nInitialCapacity, final float fLoadFactor)
  {
    super (nInitialCapacity, fLoadFactor);
  }

  public CommonsSet (@Nonnull final Collection <? extends T> aCont)
  {
    super (aCont);
  }
}
