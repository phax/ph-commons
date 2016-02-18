package com.helger.commons.collection.ext;

import java.util.Collection;
import java.util.LinkedHashSet;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.ReturnsMutableCopy;

public class CommonsLinkedHashSet <ELEMENTTYPE> extends LinkedHashSet <ELEMENTTYPE> implements ICommonsSet <ELEMENTTYPE>
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

  public CommonsLinkedHashSet (@Nonnull final Collection <? extends ELEMENTTYPE> aCont)
  {
    super (aCont);
  }

  @Nonnull
  @ReturnsMutableCopy
  public <T> CommonsLinkedHashSet <T> createInstance ()
  {
    return new CommonsLinkedHashSet <> ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public CommonsLinkedHashSet <ELEMENTTYPE> getClone ()
  {
    return new CommonsLinkedHashSet <> (this);
  }
}
