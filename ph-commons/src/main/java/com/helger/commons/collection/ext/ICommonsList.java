package com.helger.commons.collection.ext;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.state.EChange;

public interface ICommonsList <ELEMENTTYPE> extends List <ELEMENTTYPE>
{
  /**
   * @return <code>true</code> if the map is not empty, <code>false</code>
   *         otherwise.
   */
  default boolean isNotEmpty ()
  {
    return !isEmpty ();
  }

  @Nonnull
  @ReturnsMutableCopy
  default ICommonsList <ELEMENTTYPE> getCopy ()
  {
    return new CommonsList <> (this);
  }

  @Nonnull
  @ReturnsMutableCopy
  default ICommonsList <ELEMENTTYPE> getAll (@Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    if (aFilter == null)
      return getCopy ();

    final CommonsList <ELEMENTTYPE> ret = new CommonsList <> (size ());
    findAll (aFilter, ret::add);
    return ret;
  }

  default void findAll (@Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                        @Nonnull final Collection <? super ELEMENTTYPE> aDst)
  {
    findAll (aFilter, aDst::add);
  }

  default void findAll (@Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                        @Nonnull final Consumer <? super ELEMENTTYPE> aConsumer)
  {
    CollectionHelper.findAll (this, aFilter, aConsumer);
  }

  @Nonnull
  @ReturnsMutableCopy
  default <DSTTYPE> ICommonsList <DSTTYPE> getAllMapped (@Nonnull final Function <? super ELEMENTTYPE, DSTTYPE> aMapper)
  {
    final CommonsList <DSTTYPE> ret = new CommonsList <> (size ());
    findAllMapped (aMapper, ret::add);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  default <DSTTYPE> void findAllMapped (@Nonnull final Function <? super ELEMENTTYPE, DSTTYPE> aMapper,
                                        @Nonnull final Collection <? super DSTTYPE> aDst)
  {
    findAllMapped (aMapper, aDst::add);
  }

  @Nonnull
  @ReturnsMutableCopy
  default <DSTTYPE> void findAllMapped (@Nonnull final Function <? super ELEMENTTYPE, DSTTYPE> aMapper,
                                        @Nonnull final Consumer <? super DSTTYPE> aConsumer)
  {
    CollectionHelper.findAllMapped (this, aMapper, aConsumer);
  }

  @Nonnull
  @ReturnsMutableCopy
  default <DSTTYPE> ICommonsList <DSTTYPE> getAllMapped (@Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                                                         @Nonnull final Function <? super ELEMENTTYPE, DSTTYPE> aMapper)
  {
    final CommonsList <DSTTYPE> ret = new CommonsList <> ();
    findAllMapped (aFilter, aMapper, ret::add);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  default <DSTTYPE> void findAllMapped (@Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                                        @Nonnull final Function <? super ELEMENTTYPE, DSTTYPE> aMapper,
                                        @Nonnull final Collection <? super DSTTYPE> aDst)
  {
    findAllMapped (aFilter, aMapper, aDst::add);
  }

  @Nonnull
  @ReturnsMutableCopy
  default <DSTTYPE> void findAllMapped (@Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                                        @Nonnull final Function <? super ELEMENTTYPE, DSTTYPE> aMapper,
                                        @Nonnull final Consumer <? super DSTTYPE> aConsumer)
  {
    CollectionHelper.findAllMapped (this, aFilter, aMapper, aConsumer);
  }

  @Nonnull
  @ReturnsMutableCopy
  default <DSTTYPE extends ELEMENTTYPE> ICommonsList <DSTTYPE> getAllInstanceOf (@Nonnull final Class <DSTTYPE> aDstClass)
  {
    final CommonsList <DSTTYPE> ret = new CommonsList <> ();
    findAllInstanceOf (aDstClass, ret::add);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  default <DSTTYPE extends ELEMENTTYPE> void findAllInstanceOf (@Nonnull final Class <DSTTYPE> aDstClass,
                                                                @Nonnull final Consumer <? super DSTTYPE> aConsumer)
  {
    findAllMapped (e -> aDstClass.isInstance (e), e -> aDstClass.cast (e), aConsumer);
  }

  @Nullable
  default ELEMENTTYPE findFirst (@Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    return findFirst (aFilter, null);
  }

  @Nullable
  default ELEMENTTYPE findFirst (@Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                                 @Nullable final ELEMENTTYPE aDefault)
  {
    return CollectionHelper.findFirst (this, aFilter, aDefault);
  }

  @Nullable
  default <DSTTYPE> DSTTYPE findFirstMapped (@Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                                             @Nonnull final Function <? super ELEMENTTYPE, DSTTYPE> aMapper)
  {
    return findFirstMapped (aFilter, aMapper, null);
  }

  @Nullable
  default <DSTTYPE> DSTTYPE findFirstMapped (@Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                                             @Nonnull final Function <? super ELEMENTTYPE, DSTTYPE> aMapper,
                                             @Nullable final DSTTYPE aDefault)
  {
    return CollectionHelper.findFirstMapped (this, aFilter, aMapper, aDefault);
  }

  @Nonnegative
  default int getCount (@Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    return CollectionHelper.getCount (this, aFilter);
  }

  default boolean containsAny (@Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    return CollectionHelper.containsAny (this, aFilter);
  }

  default boolean containsNone (@Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    return CollectionHelper.containsNone (this, aFilter);
  }

  default boolean containsOnly (@Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    return CollectionHelper.containsOnly (this, aFilter);
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
   * @return <code>null</code> if the element cannot be accessed.
   */
  @Nullable
  default ELEMENTTYPE getAtIndex (final int nIndex)
  {
    return getAtIndex (nIndex, null);
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
  default ELEMENTTYPE getAtIndex (final int nIndex, @Nullable final ELEMENTTYPE aDefault)
  {
    return nIndex >= 0 && nIndex < size () ? get (nIndex) : aDefault;
  }

  @Nullable
  default ELEMENTTYPE getAtIndex (@Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                                  @Nonnegative final int nIndex)
  {
    return getAtIndex (aFilter, nIndex, null);
  }

  @Nullable
  default ELEMENTTYPE getAtIndex (@Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                                  @Nonnegative final int nIndex,
                                  @Nullable final ELEMENTTYPE aDefault)
  {
    return CollectionHelper.getAtIndex (this, aFilter, nIndex, aDefault);
  }

  @Nullable
  default <DSTTYPE> DSTTYPE getAtIndexMapped (@Nonnull final Predicate <? super ELEMENTTYPE> aFilter,
                                              @Nonnegative final int nIndex,
                                              @Nonnull final Function <? super ELEMENTTYPE, ? extends DSTTYPE> aMapper)
  {
    return getAtIndexMapped (aFilter, nIndex, aMapper, null);
  }

  @Nullable
  default <DSTTYPE> DSTTYPE getAtIndexMapped (@Nonnull final Predicate <? super ELEMENTTYPE> aFilter,
                                              @Nonnegative final int nIndex,
                                              @Nonnull final Function <? super ELEMENTTYPE, ? extends DSTTYPE> aMapper,
                                              @Nullable final DSTTYPE aDefault)
  {
    return CollectionHelper.getAtIndexMapped (this, aFilter, nIndex, aMapper, aDefault);
  }

  @Nonnull
  default ICommonsList <ELEMENTTYPE> getSorted (@Nonnull final Comparator <? super ELEMENTTYPE> aComparator)
  {
    sort (aComparator);
    return this;
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
  default EChange removeAll ()
  {
    if (isEmpty ())
      return EChange.UNCHANGED;
    clear ();
    return EChange.CHANGED;
  }
}
