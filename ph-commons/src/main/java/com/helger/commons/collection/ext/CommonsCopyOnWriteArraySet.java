package com.helger.commons.collection.ext;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.ReturnsMutableCopy;

public class CommonsCopyOnWriteArraySet <ELEMENTTYPE> extends CopyOnWriteArraySet <ELEMENTTYPE>
                                        implements ICommonsSet <ELEMENTTYPE>
{
  public CommonsCopyOnWriteArraySet ()
  {}

  public CommonsCopyOnWriteArraySet (@Nonnull final Collection <? extends ELEMENTTYPE> aCont)
  {
    super (aCont);
  }

  @Nonnull
  @ReturnsMutableCopy
  public <T> CommonsCopyOnWriteArraySet <T> createInstance ()
  {
    return new CommonsCopyOnWriteArraySet <> ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public CommonsCopyOnWriteArraySet <ELEMENTTYPE> getClone ()
  {
    return new CommonsCopyOnWriteArraySet <> (this);
  }
}
