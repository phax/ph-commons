package com.helger.collection;

import static com.helger.collection.CollectionHelper.getSize;
import static com.helger.collection.CollectionHelper.isEmpty;
import static com.helger.collection.CollectionHelper.isNotEmpty;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import com.helger.annotation.concurrent.Immutable;
import com.helger.base.equals.ValueEnforcer;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Contains helper functions that read-only access collections to find stuff
 *
 * @author Philip Helger
 */
@Immutable
public final class CollectionFind
{
  private CollectionFind ()
  {}

  public static <ELEMENTTYPE> boolean contains (@Nullable final Collection <? extends ELEMENTTYPE> aCollection,
                                                @Nullable final ELEMENTTYPE aSearch)
  {
    if (isEmpty (aCollection))
      return false;

    return aCollection.contains (aSearch);
  }

  public static <ELEMENTTYPE> boolean containsAny (@Nullable final Iterable <? extends ELEMENTTYPE> aCollection,
                                                   @Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    if (aFilter == null)
      return isNotEmpty (aCollection);

    if (isNotEmpty (aCollection))
      for (final ELEMENTTYPE aElement : aCollection)
        if (aFilter.test (aElement))
          return true;
    return false;
  }

  public static <ELEMENTTYPE> boolean containsNone (@Nullable final Iterable <? extends ELEMENTTYPE> aCollection,
                                                    @Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    if (aFilter == null)
      return isEmpty (aCollection);

    for (final ELEMENTTYPE aElement : aCollection)
      if (aFilter.test (aElement))
        return false;
    return true;
  }

  /**
   * Check if the passed collection contains only elements matching the predicate. An empty
   * collection does not fulfill this requirement! If no filter is provided the return value is
   * identical to {@link #isNotEmpty(Iterable)}
   *
   * @param aCollection
   *        The collection to check. May be <code>null</code>.
   * @param aFilter
   *        Predicate to check against all elements. May not be <code>null</code>.
   * @return <code>true</code> only if the passed collection is neither <code>null</code> nor empty
   *         and if only matching elements are contained, or if no filter is provided and the
   *         collection is not empty.
   * @param <ELEMENTTYPE>
   *        Collection data type
   */
  public static <ELEMENTTYPE> boolean containsOnly (@Nullable final Iterable <? extends ELEMENTTYPE> aCollection,
                                                    @Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    if (isEmpty (aCollection))
      return false;

    if (aFilter == null)
      return isNotEmpty (aCollection);

    for (final ELEMENTTYPE aElement : aCollection)
      if (!aFilter.test (aElement))
        return false;
    return true;
  }

  /**
   * Check if the passed collection contains at least one <code>null</code> element.
   *
   * @param aCont
   *        The collection to check. May be <code>null</code>.
   * @return <code>true</code> only if the passed collection is neither <code>null</code> nor empty
   *         and if at least one <code>null</code> element is contained.
   */
  public static boolean containsAnyNullElement (@Nullable final Iterable <?> aCont)
  {
    return containsAny (aCont, Objects::isNull);
  }

  /**
   * Check if the passed collection contains only <code>null</code> element.
   *
   * @param aCont
   *        The collection to check. May be <code>null</code>.
   * @return <code>true</code> only if the passed collection is neither <code>null</code> nor empty
   *         and if at least one <code>null</code> element is contained.
   */
  public static boolean containsOnlyNullElements (@Nullable final Iterable <?> aCont)
  {
    return containsOnly (aCont, Objects::isNull);
  }

  /**
   * Get the first element of the passed list.
   *
   * @param <ELEMENTTYPE>
   *        List element type
   * @param aList
   *        The list. May be <code>null</code>.
   * @return <code>null</code> if the list is <code>null</code> or empty, the first element
   *         otherwise.
   */
  @Nullable
  public static <ELEMENTTYPE> ELEMENTTYPE getFirstElement (@Nullable final List <ELEMENTTYPE> aList)
  {
    return isEmpty (aList) ? null : aList.get (0);
  }

  /**
   * Get the first element of the passed collection.
   *
   * @param <ELEMENTTYPE>
   *        Collection element type
   * @param aCollection
   *        The collection. May be <code>null</code>.
   * @return <code>null</code> if the collection is <code>null</code> or empty, the first element
   *         otherwise.
   */
  @Nullable
  public static <ELEMENTTYPE> ELEMENTTYPE getFirstElement (@Nullable final Collection <ELEMENTTYPE> aCollection)
  {
    return isEmpty (aCollection) ? null : aCollection.iterator ().next ();
  }

  /**
   * Get the first element of the passed iterable.
   *
   * @param <ELEMENTTYPE>
   *        Iterable element type
   * @param aIterable
   *        The iterable. May be <code>null</code>.
   * @return <code>null</code> if the iterable is <code>null</code> or empty, the first element
   *         otherwise.
   */
  @Nullable
  public static <ELEMENTTYPE> ELEMENTTYPE getFirstElement (@Nullable final Iterable <ELEMENTTYPE> aIterable)
  {
    return getFirstElement (aIterable, (ELEMENTTYPE) null);
  }

  /**
   * Get the first element of the passed iterable.
   *
   * @param <ELEMENTTYPE>
   *        Iterable element type
   * @param aIterable
   *        The iterable. May be <code>null</code>.
   * @param aDefault
   *        The default value to be returned, if no such value is present. May be <code>null</code>.
   * @return <code>null</code> if the iterable is <code>null</code> or empty, the first element
   *         otherwise.
   * @since 10.1.3
   */
  @Nullable
  public static <ELEMENTTYPE> ELEMENTTYPE getFirstElement (@Nullable final Iterable <? extends ELEMENTTYPE> aIterable,
                                                           @Nullable final ELEMENTTYPE aDefault)
  {
    if (aIterable != null)
    {
      final Iterator <? extends ELEMENTTYPE> it = aIterable.iterator ();
      if (it.hasNext ())
        return it.next ();
    }
    return aDefault;
  }

  @Nullable
  public static <ELEMENTTYPE> ELEMENTTYPE getLastElement (@Nullable final List <ELEMENTTYPE> aList)
  {
    final int nSize = getSize (aList);
    return nSize == 0 ? null : aList.get (nSize - 1);
  }

  @Nullable
  public static <ELEMENTTYPE> ELEMENTTYPE getLastElement (@Nullable final Collection <ELEMENTTYPE> aCollection)
  {
    if (isEmpty (aCollection))
      return null;

    // Slow but shouldn't matter
    ELEMENTTYPE aLast = null;
    for (final ELEMENTTYPE aElement : aCollection)
      aLast = aElement;
    return aLast;
  }

  @Nullable
  public static <ELEMENTTYPE> ELEMENTTYPE getLastElement (@Nullable final Iterable <ELEMENTTYPE> aIterable)
  {
    if (aIterable == null)
      return null;

    // Slow but shouldn't matter
    ELEMENTTYPE aLast = null;
    for (final ELEMENTTYPE aElement : aIterable)
      aLast = aElement;
    return aLast;
  }

  @Nullable
  public static <ELEMENTTYPE> ELEMENTTYPE findFirst (@Nullable final Iterable <? extends ELEMENTTYPE> aCollection,
                                                     @Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    // use null as default
    return findFirst (aCollection, aFilter, (ELEMENTTYPE) null);
  }

  @Nullable
  public static <ELEMENTTYPE> ELEMENTTYPE findFirst (@Nullable final Iterable <? extends ELEMENTTYPE> aCollection,
                                                     @Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                                                     @Nullable final ELEMENTTYPE aDefault)
  {
    if (aFilter == null)
      return getFirstElement (aCollection, aDefault);

    if (isNotEmpty (aCollection))
      for (final ELEMENTTYPE aElement : aCollection)
        if (aFilter.test (aElement))
          return aElement;

    return aDefault;
  }

  @Nullable
  public static <ELEMENTTYPE, DSTTYPE> DSTTYPE findFirstMapped (@Nullable final Iterable <? extends ELEMENTTYPE> aCollection,
                                                                @Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                                                                @Nonnull final Function <? super ELEMENTTYPE, DSTTYPE> aMapper)
  {
    return findFirstMapped (aCollection, aFilter, aMapper, (DSTTYPE) null);
  }

  @Nullable
  public static <ELEMENTTYPE, DSTTYPE> DSTTYPE findFirstMapped (@Nullable final Iterable <? extends ELEMENTTYPE> aCollection,
                                                                @Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                                                                @Nonnull final Function <? super ELEMENTTYPE, DSTTYPE> aMapper,
                                                                @Nullable final DSTTYPE aDefault)
  {
    ValueEnforcer.notNull (aMapper, "Mapper");

    if (isNotEmpty (aCollection))
    {
      if (aFilter == null)
        return aMapper.apply (aCollection.iterator ().next ());

      for (final ELEMENTTYPE aElement : aCollection)
        if (aFilter.test (aElement))
          return aMapper.apply (aElement);
    }

    return aDefault;
  }

  public static <ELEMENTTYPE> void findAll (@Nullable final Iterable <? extends ELEMENTTYPE> aSrc,
                                            @Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                                            @Nonnull final Consumer <? super ELEMENTTYPE> aConsumer)
  {
    ValueEnforcer.notNull (aConsumer, "Consumer");

    if (isNotEmpty (aSrc))
    {
      if (aFilter == null)
        aSrc.forEach (aConsumer);
      else
      {
        for (final ELEMENTTYPE aElement : aSrc)
          if (aFilter.test (aElement))
            aConsumer.accept (aElement);
      }
    }
  }

  public static <SRCTYPE, DSTTYPE> void findAllMapped (@Nullable final Iterable <? extends SRCTYPE> aSrc,
                                                       @Nonnull final Function <? super SRCTYPE, ? extends DSTTYPE> aMapper,
                                                       @Nonnull final Consumer <? super DSTTYPE> aConsumer)
  {
    ValueEnforcer.notNull (aMapper, "Mapper");
    ValueEnforcer.notNull (aConsumer, "Consumer");

    if (isNotEmpty (aSrc))
      for (final SRCTYPE aElement : aSrc)
        aConsumer.accept (aMapper.apply (aElement));
  }

  /**
   * Iterate, filter, map, add.
   *
   * @param aSrc
   *        Source collection. May be <code>null</code>.
   * @param aFilter
   *        Filter on source object to use. May be <code>null</code>.
   * @param aMapper
   *        Mapping function. May not be <code>null</code>.
   * @param aConsumer
   *        Destination consumer. May not be <code>null</code>.
   * @param <SRCTYPE>
   *        Source object type
   * @param <DSTTYPE>
   *        Destination object type
   */
  public static <SRCTYPE, DSTTYPE> void findAllMapped (@Nullable final Iterable <? extends SRCTYPE> aSrc,
                                                       @Nullable final Predicate <? super SRCTYPE> aFilter,
                                                       @Nonnull final Function <? super SRCTYPE, ? extends DSTTYPE> aMapper,
                                                       @Nonnull final Consumer <? super DSTTYPE> aConsumer)
  {
    ValueEnforcer.notNull (aMapper, "Mapper");
    ValueEnforcer.notNull (aConsumer, "Consumer");

    if (aFilter == null)
      findAllMapped (aSrc, aMapper, aConsumer);
    else
      if (isNotEmpty (aSrc))
        for (final SRCTYPE aElement : aSrc)
          if (aFilter.test (aElement))
            aConsumer.accept (aMapper.apply (aElement));
  }

  /**
   * Iterate, map, filter, add.
   *
   * @param aSrc
   *        Source collection. May be <code>null</code>.
   * @param aMapper
   *        Mapping function. May not be <code>null</code>.
   * @param aFilter
   *        Filter on mapped object to use. May be <code>null</code>.
   * @param aConsumer
   *        Destination consumer. May not be <code>null</code>.
   * @param <SRCTYPE>
   *        Source object type
   * @param <DSTTYPE>
   *        Destination object type
   * @since 8.5.2
   */
  public static <SRCTYPE, DSTTYPE> void findAllMapped (@Nullable final Iterable <? extends SRCTYPE> aSrc,
                                                       @Nonnull final Function <? super SRCTYPE, ? extends DSTTYPE> aMapper,
                                                       @Nullable final Predicate <? super DSTTYPE> aFilter,
                                                       @Nonnull final Consumer <? super DSTTYPE> aConsumer)
  {
    ValueEnforcer.notNull (aMapper, "Mapper");
    ValueEnforcer.notNull (aConsumer, "Consumer");

    if (aFilter == null)
      findAllMapped (aSrc, aMapper, aConsumer);
    else
      if (isNotEmpty (aSrc))
        for (final SRCTYPE aElement : aSrc)
        {
          final DSTTYPE aMapped = aMapper.apply (aElement);
          if (aFilter.test (aMapped))
            aConsumer.accept (aMapped);
        }
  }
}
