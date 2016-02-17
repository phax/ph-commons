package com.helger.commons.collection.ext;

import java.util.Map;

import javax.annotation.Nullable;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.CollectionHelper;

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
   * Get the first element of this map.
   *
   * @return <code>null</code> if the map is empty, the first element otherwise.
   */
  @Nullable
  default Map.Entry <KEYTYPE, VALUETYPE> getFirstElement ()
  {
    return isEmpty () ? null : entrySet ().iterator ().next ();
  }

  /**
   * Get the first key of this map.
   *
   * @return <code>null</code> if the map is empty, the first key otherwise.
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
}
