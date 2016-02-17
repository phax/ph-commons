package com.helger.commons.collection.ext;

import java.util.Collection;
import java.util.Vector;

import javax.annotation.Nonnull;

public class CommonsVector <T> extends Vector <T> implements ICommonsList <T>
{
  public CommonsVector ()
  {}

  public CommonsVector (final int nInitialCapacity)
  {
    super (nInitialCapacity);
  }

  public CommonsVector (@Nonnull final Collection <? extends T> aCont)
  {
    super (aCont);
  }
}
