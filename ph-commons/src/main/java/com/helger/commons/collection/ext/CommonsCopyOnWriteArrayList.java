package com.helger.commons.collection.ext;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.ReturnsMutableCopy;

public class CommonsCopyOnWriteArrayList <ELEMENTTYPE> extends CopyOnWriteArrayList <ELEMENTTYPE>
                                         implements ICommonsList <ELEMENTTYPE>
{
  public CommonsCopyOnWriteArrayList ()
  {}

  public CommonsCopyOnWriteArrayList (@Nonnull final Collection <? extends ELEMENTTYPE> aCont)
  {
    super (aCont);
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <ELEMENTTYPE> getCopy ()
  {
    return new CommonsCopyOnWriteArrayList <> (this);
  }
}
