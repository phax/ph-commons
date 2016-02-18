package com.helger.commons.collection.ext;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.ReturnsMutableCopy;

public interface ICommonsList <ELEMENTTYPE> extends List <ELEMENTTYPE>, ICommonsCollection <ELEMENTTYPE>
{
  @Nonnull
  @ReturnsMutableCopy
  default ICommonsList <ELEMENTTYPE> getCopy ()
  {
    return new CommonsArrayList <> (this);
  }

  @Nonnull
  @ReturnsMutableCopy
  default ICommonsList <ELEMENTTYPE> getAll (@Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    if (aFilter == null)
      return getCopy ();

    final ICommonsList <ELEMENTTYPE> ret = new CommonsArrayList <> ();
    findAll (aFilter, ret::add);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  default <DSTTYPE> ICommonsList <DSTTYPE> getAllMapped (@Nonnull final Function <? super ELEMENTTYPE, DSTTYPE> aMapper)
  {
    final ICommonsList <DSTTYPE> ret = new CommonsArrayList <> (size ());
    findAllMapped (aMapper, ret::add);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  default <DSTTYPE> ICommonsList <DSTTYPE> getAllMapped (@Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                                                         @Nonnull final Function <? super ELEMENTTYPE, DSTTYPE> aMapper)
  {
    final ICommonsList <DSTTYPE> ret = new CommonsArrayList <> ();
    findAllMapped (aFilter, aMapper, ret::add);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  default <DSTTYPE extends ELEMENTTYPE> ICommonsList <DSTTYPE> getAllInstanceOf (@Nonnull final Class <DSTTYPE> aDstClass)
  {
    final ICommonsList <DSTTYPE> ret = new CommonsArrayList <> ();
    findAllInstanceOf (aDstClass, ret::add);
    return ret;
  }

  @Nullable
  default ELEMENTTYPE getFirst ()
  {
    return isEmpty () ? null : get (0);
  }

  @Nullable
  default ELEMENTTYPE getLast ()
  {
    final int nSize = size ();
    return nSize == 0 ? null : get (nSize - 1);
  }

  /**
   * Safe list element accessor method.
   *
   * @param nIndex
   *        The index to access. Should be &ge; 0.
   * @param aDefault
   *        The value to be returned, if the index is out of bounds.
   * @return The default parameter if the element cannot be accessed.
   */
  @Nullable
  default ELEMENTTYPE getAtIndex (@Nonnegative final int nIndex, @Nullable final ELEMENTTYPE aDefault)
  {
    return nIndex >= 0 && nIndex < size () ? get (nIndex) : aDefault;
  }

  @Nullable
  default ELEMENTTYPE setFirst (@Nullable final ELEMENTTYPE aNewElement)
  {
    return set (0, aNewElement);
  }

  @Nullable
  default ELEMENTTYPE setLast (@Nullable final ELEMENTTYPE aNewElement)
  {
    return set (size () - 1, aNewElement);
  }

  @Nullable
  default ELEMENTTYPE removeFirst ()
  {
    return isEmpty () ? null : remove (0);
  }

  @Nullable
  default ELEMENTTYPE removeLast ()
  {
    final int nSize = size ();
    return nSize == 0 ? null : remove (nSize - 1);
  }

  @Nonnull
  default List <ELEMENTTYPE> getAsUnmodifiable ()
  {
    return Collections.unmodifiableList (this);
  }
}
