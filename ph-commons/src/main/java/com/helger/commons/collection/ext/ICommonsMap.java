package com.helger.commons.collection.ext;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.state.EChange;

public interface ICommonsMap <KEYTYPE, VALUETYPE> extends Map <KEYTYPE, VALUETYPE>
{
  @Nonnull
  ICommonsSet <KEYTYPE> keySet ();

  @Nonnull
  ICommonsCollection <VALUETYPE> values ();

  @Nonnull
  ICommonsSet <Map.Entry <KEYTYPE, VALUETYPE>> entrySet ();

  /**
   * @return <code>true</code> if the map is not empty, <code>false</code>
   *         otherwise.
   */
  default boolean isNotEmpty ()
  {
    return !isEmpty ();
  }

  /**
   * Get the first element of this map.
   *
   * @return <code>null</code> if the map is <code>null</code> or empty, the
   *         first element otherwise.
   */
  @Nullable
  default Map.Entry <KEYTYPE, VALUETYPE> getFirstElement ()
  {
    return isEmpty () ? null : entrySet ().iterator ().next ();
  }

  /**
   * Get the first key of this map.
   *
   * @return <code>null</code> if the map is <code>null</code> or empty, the
   *         first key otherwise.
   */
  @Nullable
  default KEYTYPE getFirstKey ()
  {
    return isEmpty () ? null : keySet ().iterator ().next ();
  }

  /**
   * Get the first value of this map.
   *
   * @return <code>null</code> if the map is empty, the first value otherwise.
   */
  @Nullable
  default VALUETYPE getFirstValue ()
  {
    return isEmpty () ? null : values ().iterator ().next ();
  }

  /**
   * Get the map sorted by its keys. The comparison order is defined by the
   * passed comparator object.
   *
   * @param aKeyComparator
   *        The comparator to be used. May not be <code>null</code>.
   * @return the sorted map and never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  default ICommonsMap <KEYTYPE, VALUETYPE> getSortedByKey (@Nonnull final Comparator <? super KEYTYPE> aKeyComparator)
  {
    return CollectionHelper.getSortedByKey (this, aKeyComparator);
  }

  /**
   * Get the map sorted by its values. The comparison order is defined by the
   * passed comparator object.
   *
   * @param aValueComparator
   *        The comparator to be used. May not be <code>null</code>.
   * @return the sorted map and never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  default ICommonsMap <KEYTYPE, VALUETYPE> getSortedByValue (@Nonnull final Comparator <? super VALUETYPE> aValueComparator)
  {
    return CollectionHelper.getSortedByValue (this, aValueComparator);
  }

  /**
   * Get a map where keys and values are exchanged.
   *
   * @return The swapped hash map (unsorted!)
   */
  @Nullable
  @ReturnsMutableCopy
  default ICommonsMap <VALUETYPE, KEYTYPE> getSwappedKeyValues ()
  {
    return CollectionHelper.getSwappedKeyValues (this);
  }

  @Nonnull
  default void putIf (@Nonnull final KEYTYPE aKey,
                      @Nullable final VALUETYPE aValue,
                      @Nonnull final Predicate <VALUETYPE> aFilter)
  {
    if (aFilter.test (aValue))
      put (aKey, aValue);
  }

  @Nonnull
  default void putIfNotNull (@Nonnull final KEYTYPE aKey, @Nullable final VALUETYPE aValue)
  {
    if (aValue != null)
      put (aKey, aValue);
  }

  @Nonnull
  default EChange removeAll ()
  {
    if (isEmpty ())
      return EChange.UNCHANGED;
    clear ();
    return EChange.CHANGED;
  }

  @Nonnull
  default Map <KEYTYPE, VALUETYPE> getAsUnmodifiable ()
  {
    return Collections.unmodifiableMap (this);
  }
}
