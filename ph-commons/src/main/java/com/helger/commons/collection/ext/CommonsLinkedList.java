package com.helger.commons.collection.ext;

import java.util.Collection;
import java.util.LinkedList;

import javax.annotation.Nonnull;

public class CommonsLinkedList <T> extends LinkedList <T> implements ICommonsList <T>
{
  public CommonsLinkedList ()
  {}

  public CommonsLinkedList (@Nonnull final Collection <? extends T> aCont)
  {
    super (aCont);
  }
}
