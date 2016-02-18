package com.helger.commons.collection.ext;

import java.util.Collection;
import java.util.LinkedList;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.ReturnsMutableCopy;

public class CommonsLinkedList <ELEMENTTYPE> extends LinkedList <ELEMENTTYPE> implements ICommonsList <ELEMENTTYPE>
{
  public CommonsLinkedList ()
  {}

  public CommonsLinkedList (@Nonnull final Collection <? extends ELEMENTTYPE> aCont)
  {
    super (aCont);
  }

  @Nonnull
  @ReturnsMutableCopy
  public <T> CommonsLinkedList <T> createInstance ()
  {
    return new CommonsLinkedList <> ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public CommonsLinkedList <ELEMENTTYPE> getClone ()
  {
    return new CommonsLinkedList <> (this);
  }
}
