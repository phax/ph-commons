package com.helger.commons.collection.ext;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.Nonnull;

public class CommonsCopyOnWriteArrayList <T> extends CopyOnWriteArrayList <T> implements ICommonsList <T>
{
  public CommonsCopyOnWriteArrayList ()
  {}

  public CommonsCopyOnWriteArrayList (@Nonnull final Collection <? extends T> aCont)
  {
    super (aCont);
  }
}
