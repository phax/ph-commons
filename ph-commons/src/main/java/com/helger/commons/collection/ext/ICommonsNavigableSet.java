package com.helger.commons.collection.ext;

import java.util.Collections;
import java.util.NavigableSet;
import java.util.SortedSet;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.ReturnsMutableCopy;

public interface ICommonsNavigableSet <ELEMENTTYPE> extends NavigableSet <ELEMENTTYPE>, ICommonsSortedSet <ELEMENTTYPE>
{
  @Nonnull
  @ReturnsMutableCopy
  default ICommonsNavigableSet <ELEMENTTYPE> getCopy ()
  {
    return new CommonsTreeSet <> (this);
  }

  @Nonnull
  @ReturnsMutableCopy
  default ICommonsNavigableSet <ELEMENTTYPE> getAll (@Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    if (aFilter == null)
      return getCopy ();

    final ICommonsNavigableSet <ELEMENTTYPE> ret = new CommonsTreeSet <> ();
    findAll (aFilter, ret::add);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  default <DSTTYPE> ICommonsNavigableSet <DSTTYPE> getAllMapped (@Nonnull final Function <? super ELEMENTTYPE, DSTTYPE> aMapper)
  {
    final ICommonsNavigableSet <DSTTYPE> ret = new CommonsTreeSet <> ();
    findAllMapped (aMapper, ret::add);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  default <DSTTYPE> ICommonsNavigableSet <DSTTYPE> getAllMapped (@Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                                                                 @Nonnull final Function <? super ELEMENTTYPE, DSTTYPE> aMapper)
  {
    final ICommonsNavigableSet <DSTTYPE> ret = new CommonsTreeSet <> ();
    findAllMapped (aFilter, aMapper, ret::add);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  default <DSTTYPE extends ELEMENTTYPE> ICommonsNavigableSet <DSTTYPE> getAllInstanceOf (@Nonnull final Class <DSTTYPE> aDstClass)
  {
    final ICommonsNavigableSet <DSTTYPE> ret = new CommonsTreeSet <> ();
    findAllInstanceOf (aDstClass, ret::add);
    return ret;
  }

  @Nonnull
  default SortedSet <ELEMENTTYPE> getAsUnmodifiable ()
  {
    return Collections.unmodifiableSortedSet (this);
  }
}
