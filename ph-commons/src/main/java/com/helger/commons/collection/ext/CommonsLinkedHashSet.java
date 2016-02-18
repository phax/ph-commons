package com.helger.commons.collection.ext;

import java.util.Collection;
import java.util.LinkedHashSet;

import javax.annotation.Nonnull;

public class CommonsLinkedHashSet <T> extends LinkedHashSet <T> implements ICommonsSet <T>
{
  public CommonsLinkedHashSet ()
  {}

  public CommonsLinkedHashSet (final int nInitialCapacity)
  {
    super (nInitialCapacity);
  }

  public CommonsLinkedHashSet (final int nInitialCapacity, final float fLoadFactor)
  {
    super (nInitialCapacity, fLoadFactor);
  }

  public CommonsLinkedHashSet (@Nonnull final Collection <? extends T> aCont)
  {
    super (aCont);
  }
}
