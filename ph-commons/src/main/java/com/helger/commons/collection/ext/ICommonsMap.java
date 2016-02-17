package com.helger.commons.collection.ext;

import java.util.Map;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.state.EChange;

public interface ICommonsMap <KEYTYPE, VALUETYPE> extends Map <KEYTYPE, VALUETYPE>
{
  /**
   * @return <code>true</code> if the map is not empty, <code>false</code>
   *         otherwise.
   */
  default boolean isNotEmpty ()
  {
    return !isEmpty ();
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
}
