package com.helger.collection.commons;

import java.util.Iterator;
import java.util.function.Predicate;

import com.helger.collection.base.FilterIterator;
import com.helger.collection.base.IIterableIterator;

import jakarta.annotation.Nonnull;

/**
 * A specific {@link FilterIterator} that implements {@link ICommonsIterableIterator}
 *
 * @author Philip Helger
 * @param <ELEMENTTYPE>
 *        The type to iterate
 */
public class CommonsFilterIterator <ELEMENTTYPE> extends FilterIterator <ELEMENTTYPE> implements
                                   ICommonsIterableIterator <ELEMENTTYPE>
{
  public CommonsFilterIterator (final IIterableIterator <? extends ELEMENTTYPE> aBaseIter,
                                final Predicate <? super ELEMENTTYPE> aFilter)
  {
    super (aBaseIter, aFilter);
  }

  public CommonsFilterIterator (@Nonnull final Iterable <? extends ELEMENTTYPE> aBaseCont,
                                @Nonnull final Predicate <? super ELEMENTTYPE> aFilter)
  {
    super (aBaseCont, aFilter);
  }

  public CommonsFilterIterator (@Nonnull final Iterator <? extends ELEMENTTYPE> aBaseIter,
                                @Nonnull final Predicate <? super ELEMENTTYPE> aFilter)
  {
    super (aBaseIter, aFilter);
  }
}
