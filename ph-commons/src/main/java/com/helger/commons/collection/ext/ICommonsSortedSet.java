package com.helger.commons.collection.ext;

import java.util.Collections;
import java.util.SortedSet;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.ReturnsMutableCopy;

public interface ICommonsSortedSet <ELEMENTTYPE> extends SortedSet <ELEMENTTYPE>, ICommonsSet <ELEMENTTYPE>
{
  @Nonnull
  @ReturnsMutableCopy
  default ICommonsSortedSet <ELEMENTTYPE> getCopy ()
  {
    return new CommonsTreeSet <> (this);
  }

  @Nonnull
  @ReturnsMutableCopy
  default ICommonsSortedSet <ELEMENTTYPE> getAll (@Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    if (aFilter == null)
      return getCopy ();

    final ICommonsSortedSet <ELEMENTTYPE> ret = new CommonsTreeSet <> ();
    findAll (aFilter, ret::add);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  default <DSTTYPE> ICommonsSortedSet <DSTTYPE> getAllMapped (@Nonnull final Function <? super ELEMENTTYPE, DSTTYPE> aMapper)
  {
    final ICommonsSortedSet <DSTTYPE> ret = new CommonsTreeSet <> ();
    findAllMapped (aMapper, ret::add);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  default <DSTTYPE> ICommonsSortedSet <DSTTYPE> getAllMapped (@Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                                                              @Nonnull final Function <? super ELEMENTTYPE, DSTTYPE> aMapper)
  {
    final ICommonsSortedSet <DSTTYPE> ret = new CommonsTreeSet <> ();
    findAllMapped (aFilter, aMapper, ret::add);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  default <DSTTYPE extends ELEMENTTYPE> ICommonsSortedSet <DSTTYPE> getAllInstanceOf (@Nonnull final Class <DSTTYPE> aDstClass)
  {
    final ICommonsSortedSet <DSTTYPE> ret = new CommonsTreeSet <> ();
    findAllInstanceOf (aDstClass, ret::add);
    return ret;
  }

  @Nullable
  default ELEMENTTYPE getFirst ()
  {
    return isEmpty () ? null : first ();
  }

  @Nullable
  default ELEMENTTYPE getLast ()
  {
    return isEmpty () ? null : last ();
  }

  @Nonnull
  default SortedSet <ELEMENTTYPE> getAsUnmodifiable ()
  {
    return Collections.unmodifiableSortedSet (this);
  }
}
