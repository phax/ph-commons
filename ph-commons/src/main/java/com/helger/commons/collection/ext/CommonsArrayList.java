package com.helger.commons.collection.ext;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Nonnull;

public class CommonsArrayList <T> extends ArrayList <T> implements ICommonsList <T>
{
  public CommonsArrayList ()
  {}

  public CommonsArrayList (final int nInitialCapacity)
  {
    super (nInitialCapacity);
  }

  public CommonsArrayList (@Nonnull final Collection <? extends T> aCont)
  {
    super (aCont);
  }
}
