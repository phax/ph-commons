/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.collection.map;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.annotation.style.CodingStyleguideUnaware;
import com.helger.annotation.style.OverrideOnDemand;
import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.base.concurrent.SimpleReadWriteLock;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.equals.EqualsHelper;
import com.helger.base.reflection.GenericReflection;
import com.helger.collection.commons.ICommonsMap;
import com.helger.collection.commons.MapEntry;

/**
 * Soft {@link Map} implementation based on http://www.javaspecialists.eu/archive/Issue015.html<br>
 * The <code>entrySet</code> implementation is from <code>org.hypergraphdb.util</code><br>
 * Note: {@link AbstractSoftMap} is <b>NOT</b> serializable!<br>
 * This class is thread-safe. Note that even seemingly read-only operations like
 * {@link #get(Object)} or {@link #size()} internally acquire the <b>write</b> lock, because no
 * operation of this map is truly read-only: garbage collected entries are removed on access, and
 * access-ordered source maps (like in {@link SoftLinkedHashMap}) reorder their entries on every
 * read.<br>
 * Attention: iterators (via {@link #entrySet()}, {@link #keySet()} or {@link #values()}) are NOT
 * thread-safe. As with {@link java.util.Collections#synchronizedMap(Map)} the caller must ensure
 * that the map is not modified concurrently while iterating. Compound default operations (like
 * <code>putIfAbsent</code> or <code>computeIfAbsent</code>) are thread-safe but not atomic.
 *
 * @author Philip Helger
 * @param <K>
 *        Key type
 * @param <V>
 *        Value type
 */
@ThreadSafe
public abstract class AbstractSoftMap <K, V> extends AbstractMap <K, V> implements ICommonsMap <K, V>
{
  /**
   * We define our own subclass of SoftReference which contains not only the value but also the key
   * to make it easier to find the entry in the HashMap after it's been garbage collected.
   *
   * @param <K>
   *        Key type
   * @param <V>
   *        Value type
   */
  protected static final class SoftValue <K, V> extends SoftReference <V>
  {
    private final K m_aKey;

    private SoftValue (final K aKey, final V aValue, @Nullable final ReferenceQueue <? super V> aQueue)
    {
      super (aValue, aQueue);
      m_aKey = aKey;
    }
  }

  private static final class SoftEntrySet <K, V> implements Set <Map.Entry <K, V>>
  {
    private static final class SoftIterator <K, V> implements Iterator <Map.Entry <K, V>>
    {
      private final Iterator <Entry <K, SoftValue <K, V>>> m_aSrcIter;

      private SoftIterator (@NonNull final Iterator <Entry <K, SoftValue <K, V>>> aSrcIter)
      {
        m_aSrcIter = aSrcIter;
      }

      /** {@inheritDoc} */
      public boolean hasNext ()
      {
        return m_aSrcIter.hasNext ();
      }

      /** {@inheritDoc} */
      public Map.@NonNull Entry <K, V> next ()
      {
        final Map.Entry <K, SoftValue <K, V>> e = m_aSrcIter.next ();
        return new MapEntry <> (e.getKey (), e.getValue ().get ());
      }

      @Override
      public void remove ()
      {
        m_aSrcIter.remove ();
      }
    }

    @CodingStyleguideUnaware
    private final Set <Map.Entry <K, SoftValue <K, V>>> m_aSrcEntrySet;
    private final SimpleReadWriteLock m_aRWLock;

    private SoftEntrySet (@NonNull final Set <Map.Entry <K, SoftValue <K, V>>> aSrcEntrySet,
                          @NonNull final SimpleReadWriteLock aRWLock)
    {
      m_aSrcEntrySet = aSrcEntrySet;
      m_aRWLock = aRWLock;
    }

    /**
     * This operation is not supported, as with all JDK Map implementations. Use
     * {@link AbstractSoftMap#put(Object, Object)} instead.
     */
    public boolean add (final Map.@NonNull Entry <K, V> aEntry)
    {
      throw new UnsupportedOperationException ();
    }

    /**
     * This operation is not supported, as with all JDK Map implementations. Use
     * {@link AbstractSoftMap#putAll(Map)} instead.
     */
    public boolean addAll (@NonNull final Collection <? extends Map.Entry <K, V>> c)
    {
      throw new UnsupportedOperationException ();
    }

    /** {@inheritDoc} */
    public void clear ()
    {
      m_aRWLock.writeLock ().lock ();
      try
      {
        m_aSrcEntrySet.clear ();
      }
      finally
      {
        m_aRWLock.writeLock ().unlock ();
      }
    }

    /**
     * {@inheritDoc} Consistent with {@link AbstractSoftMap#get(Object)}: an entry whose value was
     * garbage collected counts as "not contained".
     */
    public boolean contains (final Object aEntryObj)
    {
      if (!(aEntryObj instanceof Map.Entry <?, ?>))
        return false;

      final Map.Entry <K, V> aEntry = GenericReflection.uncheckedCast (aEntryObj);
      m_aRWLock.readLock ().lock ();
      try
      {
        for (final Map.Entry <K, SoftValue <K, V>> aSrcEntry : m_aSrcEntrySet)
          if (EqualsHelper.equals (aSrcEntry.getKey (), aEntry.getKey ()))
          {
            // Keys are unique - compare the referent with the probe value
            final V aSrcValue = aSrcEntry.getValue ().get ();
            return aSrcValue != null && aSrcValue.equals (aEntry.getValue ());
          }
        return false;
      }
      finally
      {
        m_aRWLock.readLock ().unlock ();
      }
    }

    /** {@inheritDoc} */
    public boolean containsAll (@NonNull final Collection <?> c)
    {
      for (final Object x : c)
        if (!this.contains (x))
          return false;
      return true;
    }

    /** {@inheritDoc} */
    public boolean isEmpty ()
    {
      m_aRWLock.readLock ().lock ();
      try
      {
        return m_aSrcEntrySet.isEmpty ();
      }
      finally
      {
        m_aRWLock.readLock ().unlock ();
      }
    }

    /**
     * {@inheritDoc} Attention: the returned iterator is NOT thread-safe - the caller must ensure
     * that the map is not modified concurrently while iterating.
     */
    @NonNull
    public Iterator <Map.Entry <K, V>> iterator ()
    {
      final Iterator <Map.Entry <K, SoftValue <K, V>>> aSrcIter = m_aSrcEntrySet.iterator ();
      return new SoftIterator <> (aSrcIter);
    }

    /**
     * {@inheritDoc} Consistent with {@link AbstractSoftMap#get(Object)}: an entry whose value was
     * garbage collected counts as "not contained" and is therefore not removed.
     */
    public boolean remove (final Object aEntryObj)
    {
      if (!(aEntryObj instanceof Map.Entry <?, ?>))
        return false;

      final Map.Entry <K, V> aEntry = GenericReflection.uncheckedCast (aEntryObj);
      m_aRWLock.writeLock ().lock ();
      try
      {
        final Iterator <Map.Entry <K, SoftValue <K, V>>> aSrcIter = m_aSrcEntrySet.iterator ();
        while (aSrcIter.hasNext ())
        {
          final Map.Entry <K, SoftValue <K, V>> aSrcEntry = aSrcIter.next ();
          if (EqualsHelper.equals (aSrcEntry.getKey (), aEntry.getKey ()))
          {
            // Keys are unique - compare the referent with the probe value
            final V aSrcValue = aSrcEntry.getValue ().get ();
            if (aSrcValue != null && aSrcValue.equals (aEntry.getValue ()))
            {
              aSrcIter.remove ();
              return true;
            }
            return false;
          }
        }
        return false;
      }
      finally
      {
        m_aRWLock.writeLock ().unlock ();
      }
    }

    /** {@inheritDoc} */
    public boolean removeAll (final Collection <?> c)
    {
      boolean result = false;
      for (final Object x : c)
        if (this.remove (x))
          result = true;
      return result;
    }

    /** {@inheritDoc} */
    public boolean retainAll (final Collection <?> c)
    {
      throw new UnsupportedOperationException ();
    }

    /** {@inheritDoc} */
    public int size ()
    {
      m_aRWLock.readLock ().lock ();
      try
      {
        return m_aSrcEntrySet.size ();
      }
      finally
      {
        m_aRWLock.readLock ().unlock ();
      }
    }

    /** {@inheritDoc} */
    @NonNull
    public Object [] toArray ()
    {
      return toArray (new MapEntry <?, ?> [0]);
    }

    /** {@inheritDoc} */
    @NonNull
    public <T> T [] toArray (@Nullable final T [] a)
    {
      m_aRWLock.readLock ().lock ();
      try
      {
        MapEntry <K, V> [] result = null;
        if (a instanceof MapEntry <?, ?> [] && a.length >= size ())
          result = GenericReflection.uncheckedCast (a);
        else
          result = GenericReflection.uncheckedCast (new MapEntry <?, ?> [size ()]);

        final Object [] aSrcArray = m_aSrcEntrySet.toArray ();
        for (int i = 0; i < aSrcArray.length; i++)
        {
          final Map.Entry <K, SoftValue <K, V>> e = GenericReflection.uncheckedCast (aSrcArray[i]);
          result[i] = new MapEntry <> (e.getKey (), e.getValue ().get ());
        }
        return GenericReflection.uncheckedCast (result);
      }
      finally
      {
        m_aRWLock.readLock ().unlock ();
      }
    }
  }

  /** The internal HashMap that will hold the SoftReference. */
  @CodingStyleguideUnaware
  protected final Map <K, SoftValue <K, V>> m_aSrcMap;
  /** Reference queue for cleared SoftReference objects. */
  private final ReferenceQueue <V> m_aQueue = new ReferenceQueue <> ();
  /**
   * The lock guarding all access to {@link #m_aSrcMap}. Mostly the write lock is used, because
   * nearly no operation of this map is read-only: garbage collected entries are removed on access,
   * and access-ordered source maps reorder their entries on every read.
   */
  protected final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();

  /**
   * Constructor.
   *
   * @param aSrcMap
   *        The internal map to use for storing soft references. May not be <code>null</code>.
   */
  protected AbstractSoftMap (@NonNull final Map <K, SoftValue <K, V>> aSrcMap)
  {
    m_aSrcMap = ValueEnforcer.notNull (aSrcMap, "SrcMap");
  }

  /**
   * Callback method invoked after a map entry is garbage collected. Note: this method is invoked
   * while the internal write lock is held.
   *
   * @param aKey
   *        Key the removed key
   */
  @OverrideOnDemand
  protected void onEntryRemoved (final K aKey)
  {}

  @Override
  public V get (final Object aKey)
  {
    // Write lock, because a read may remove garbage collected entries and may reorder
    // access-ordered source maps
    m_aRWLock.writeLock ().lock ();
    try
    {
      V ret = null;
      // We get the SoftReference represented by that key
      final SoftValue <K, V> aSoftValue = m_aSrcMap.get (aKey);
      if (aSoftValue != null)
      {
        // From the SoftReference we get the value, which can be
        // null if it was not in the map, or it was removed in
        // the processQueue() method defined below
        ret = aSoftValue.get ();
        if (ret == null)
        {
          // If the value has been garbage collected, remove the
          // entry from the HashMap.
          if (m_aSrcMap.remove (aKey) != null)
            onEntryRemoved (GenericReflection.uncheckedCast (aKey));
        }
      }
      return ret;
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  /**
   * {@inheritDoc} Consistent with {@link #get(Object)}: an entry whose value was garbage collected
   * counts as "not contained".
   */
  @Override
  public boolean containsKey (final Object aKey)
  {
    return get (aKey) != null;
  }

  /**
   * {@inheritDoc} Consistent with {@link #get(Object)}: entries whose value was garbage collected
   * are ignored.
   */
  @Override
  public boolean containsValue (final Object aValue)
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      for (final SoftValue <K, V> aSoftValue : m_aSrcMap.values ())
      {
        final V aExistingValue = aSoftValue.get ();
        if (aExistingValue != null && aExistingValue.equals (aValue))
          return true;
      }
      return false;
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  /**
   * Here we go through the ReferenceQueue and remove garbage collected SoftValue objects from the
   * HashMap by looking them up using the SoftValue.m_aKey data member.
   */
  private void _processQueue ()
  {
    SoftValue <K, V> aSoftValue;
    while ((aSoftValue = GenericReflection.uncheckedCast (m_aQueue.poll ())) != null)
    {
      m_aSrcMap.remove (aSoftValue.m_aKey);
    }
  }

  /**
   * Here we put the key, value pair into the HashMap using a SoftValue object.
   */
  @Override
  public V put (final K aKey, final V aValue)
  {
    m_aRWLock.writeLock ().lock ();
    try
    {
      // throw out garbage collected values first
      _processQueue ();
      final SoftValue <K, V> aOld = m_aSrcMap.put (aKey, new SoftValue <> (aKey, aValue, m_aQueue));
      return aOld == null ? null : aOld.get ();
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  @Override
  public V remove (final Object aKey)
  {
    m_aRWLock.writeLock ().lock ();
    try
    {
      // throw out garbage collected values first
      _processQueue ();
      final SoftValue <K, V> aRemoved = m_aSrcMap.remove (aKey);
      return aRemoved == null ? null : aRemoved.get ();
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  @Override
  public void clear ()
  {
    m_aRWLock.writeLock ().lock ();
    try
    {
      // throw out garbage collected values
      _processQueue ();
      m_aSrcMap.clear ();
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  @Override
  public int size ()
  {
    // Write lock, because the reference queue processing may remove entries
    m_aRWLock.writeLock ().lock ();
    try
    {
      // throw out garbage collected values first
      _processQueue ();
      return m_aSrcMap.size ();
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  /**
   * {@inheritDoc} Attention: iterators created from the returned Set are NOT thread-safe - the
   * caller must ensure that the map is not modified concurrently while iterating.
   */
  @Override
  @ReturnsMutableObject ("design")
  @CodingStyleguideUnaware
  public Set <Map.Entry <K, V>> entrySet ()
  {
    final Set <Map.Entry <K, SoftValue <K, V>>> aSrcEntrySet = m_aSrcMap.entrySet ();
    return new SoftEntrySet <> (aSrcEntrySet, m_aRWLock);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    return super.equals (o);
  }

  @Override
  public int hashCode ()
  {
    return super.hashCode ();
  }
}
