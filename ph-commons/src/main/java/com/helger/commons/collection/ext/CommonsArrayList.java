package com.helger.commons.collection.ext;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.ReturnsMutableCopy;

public class CommonsArrayList <ELEMENTTYPE> extends ArrayList <ELEMENTTYPE> implements ICommonsList <ELEMENTTYPE>
{
  public CommonsArrayList ()
  {}

  public CommonsArrayList (final int nInitialCapacity)
  {
    super (nInitialCapacity);
  }

  public CommonsArrayList (@Nonnull final Collection <? extends ELEMENTTYPE> aCont)
  {
    super (aCont);
  }

  @Nonnull
  @ReturnsMutableCopy
  public CommonsArrayList <ELEMENTTYPE> getClone ()
  {
    return new CommonsArrayList <> (this);
  }
}
