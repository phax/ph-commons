package com.helger.collection.commons;

import java.util.Iterator;
import java.util.function.Function;

import com.helger.collection.base.IIterableIterator;
import com.helger.collection.base.MapperIterator;

import jakarta.annotation.Nonnull;

/**
 * A specific {@link MapperIterator} that implements {@link ICommonsIterableIterator}
 *
 * @author Philip Helger
 * @param <SRCTYPE>
 *        The type of the source iterator
 * @param <ELEMENTTYPE>
 *        The type of this iterator
 */
public class CommonsMapperIterator <SRCTYPE, ELEMENTTYPE> extends MapperIterator <SRCTYPE, ELEMENTTYPE> implements
                                   ICommonsIterableIterator <ELEMENTTYPE>
{
  public CommonsMapperIterator (@Nonnull final IIterableIterator <? extends SRCTYPE> aBaseIter,
                                @Nonnull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aConverter)
  {
    super (aBaseIter, aConverter);
  }

  public CommonsMapperIterator (@Nonnull final Iterable <? extends SRCTYPE> aBaseCont,
                                @Nonnull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aConverter)
  {
    super (aBaseCont, aConverter);
  }

  public CommonsMapperIterator (@Nonnull final Iterator <? extends SRCTYPE> aBaseIter,
                                @Nonnull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aConverter)
  {
    super (aBaseIter, aConverter);
  }
}
