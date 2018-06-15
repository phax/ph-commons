/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.commons.collection.map;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.CodingStyleguideUnaware;
import com.helger.commons.annotation.OverrideOnDemand;
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.lang.GenericReflection;

/**
 * Soft {@link Map} implementation based on
 * http://www.javaspecialists.eu/archive/Issue015.html<br>
 * The <code>entrySet</code> implementation is from
 * <code>org.hypergraphdb.util</code><br>
 * Note: {@link AbstractSoftMap} is <b>NOT</b> serializable!
 *
 * @author Philip Helger
 * @param <K>
 *        Key type
 * @param <V>
 *        Value type
 */
public abstract class AbstractSoftMap <K, V> extends AbstractMap <K, V> implements ICommonsMap <K, V>
{
  /**
   * We define our own subclass of SoftReference which contains not only the
   * value but also the key to make it easier to find the entry in the HashMap
   * after it's been garbage collected.
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

  private static class SoftMapEntry <K, V> implements Map.Entry <K, SoftValue <K, V>>
  {
    private final K m_aKey;
    private SoftValue <K, V> m_aSoftValue;

    public SoftMapEntry (final K aKey, final V aValue, @Nullable final ReferenceQueue <V> aQueue)
    {
      m_aKey = aKey;
      m_aSoftValue = new SoftValue <> (aKey, aValue, aQueue);
    }

    public K getKey ()
    {
      return m_aKey;
    }

    @Nonnull
    public final SoftValue <K, V> getValue ()
    {
      return m_aSoftValue;
    }

    @Nonnull
    public final SoftValue <K, V> setValue (@Nonnull final SoftValue <K, V> aNew)
    {
      final SoftValue <K, V> aOld = aNew;
      m_aSoftValue = aNew;
      return aOld;
    }
  }

  private static final class SoftEntrySet <K, V> implements Set <Map.Entry <K, V>>
  {
    private static final class SoftIterator <K, V> implements Iterator <Map.Entry <K, V>>
    {
      private final Iterator <Entry <K, SoftValue <K, V>>> m_aSrcIter;

      private SoftIterator (@Nonnull final Iterator <Entry <K, SoftValue <K, V>>> aSrcIter)
      {
        m_aSrcIter = aSrcIter;
      }

      public boolean hasNext ()
      {
        return m_aSrcIter.hasNext ();
      }

      public Map.Entry <K, V> next ()
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
    private final Set <Entry <K, SoftValue <K, V>>> m_aSrcEntrySet;
    private final ReferenceQueue <V> m_aQueue;

    private SoftEntrySet (@Nonnull final Set <Entry <K, SoftValue <K, V>>> aSrcEntrySet,
                          @Nonnull final ReferenceQueue <V> aQueue)
    {
      m_aSrcEntrySet = aSrcEntrySet;
      m_aQueue = aQueue;
    }

    public boolean add (@Nonnull final Map.Entry <K, V> aEntry)
    {
      return m_aSrcEntrySet.add (new SoftMapEntry <> (aEntry.getKey (), aEntry.getValue (), m_aQueue));
    }

    public boolean addAll (@Nonnull final Collection <? extends Map.Entry <K, V>> c)
    {
      boolean result = false;
      for (final Map.Entry <K, V> e : c)
        if (this.add (e))
          result = true;
      return result;
    }

    public void clear ()
    {
      m_aSrcEntrySet.clear ();
    }

    public boolean contains (final Object aEntryObj)
    {
      if (!(aEntryObj instanceof Map.Entry))
        return false;
      final Map.Entry <K, V> aEntry = GenericReflection.uncheckedCast (aEntryObj);
      return m_aSrcEntrySet.contains (new SoftMapEntry <> (aEntry.getKey (), aEntry.getValue (), m_aQueue));
    }

    public boolean containsAll (@Nonnull final Collection <?> c)
    {
      for (final Object x : c)
        if (!this.contains (x))
          return false;
      return true;
    }

    public boolean isEmpty ()
    {
      return m_aSrcEntrySet.isEmpty ();
    }

    @Nonnull
    public Iterator <Map.Entry <K, V>> iterator ()
    {
      final Iterator <Map.Entry <K, SoftValue <K, V>>> aSrcIter = m_aSrcEntrySet.iterator ();
      return new SoftIterator <> (aSrcIter);
    }

    public boolean remove (final Object aEntryObj)
    {
      if (!(aEntryObj instanceof Map.Entry <?, ?>))
        return false;
      final Map.Entry <K, V> aEntry = GenericReflection.uncheckedCast (aEntryObj);
      return m_aSrcEntrySet.remove (new SoftMapEntry <> (aEntry.getKey (), aEntry.getValue (), m_aQueue));
    }

    public boolean removeAll (final Collection <?> c)
    {
      boolean result = false;
      for (final Object x : c)
        if (this.remove (x))
          result = true;
      return result;
    }

    public boolean retainAll (final Collection <?> c)
    {
      throw new UnsupportedOperationException ();
    }

    public int size ()
    {
      return m_aSrcEntrySet.size ();
    }

    @Nonnull
    public Object [] toArray ()
    {
      return toArray (new MapEntry <?, ?> [0]);
    }

    @Nonnull
    public <T> T [] toArray (@Nullable final T [] a)
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
  }

  /** The internal HashMap that will hold the SoftReference. */
  @CodingStyleguideUnaware
  protected final Map <K, SoftValue <K, V>> m_aSrcMap;
  /** Reference queue for cleared SoftReference objects. */
  private final ReferenceQueue <V> m_aQueue = new ReferenceQueue <> ();

  public AbstractSoftMap (@Nonnull final Map <K, SoftValue <K, V>> aSrcMap)
  {
    m_aSrcMap = ValueEnforcer.notNull (aSrcMap, "SrcMap");
  }

  /**
   * Callback method invoked after a map entry is garbage collected
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

  /**
   * Here we go through the ReferenceQueue and remove garbage collected
   * SoftValue objects from the HashMap by looking them up using the
   * SoftValue.m_aKey data member.
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
    // throw out garbage collected values first
    _processQueue ();
    final SoftValue <K, V> aOld = m_aSrcMap.put (aKey, new SoftValue <> (aKey, aValue, m_aQueue));
    return aOld == null ? null : aOld.get ();
  }

  @Override
  public V remove (final Object aKey)
  {
    // throw out garbage collected values first
    _processQueue ();
    final SoftValue <K, V> aRemoved = m_aSrcMap.remove (aKey);
    return aRemoved == null ? null : aRemoved.get ();
  }

  @Override
  public void clear ()
  {
    // throw out garbage collected values
    _processQueue ();
    m_aSrcMap.clear ();
  }

  @Override
  public int size ()
  {
    // throw out garbage collected values first
    _processQueue ();
    return m_aSrcMap.size ();
  }

  @Override
  @ReturnsMutableObject ("design")
  @CodingStyleguideUnaware
  public Set <Map.Entry <K, V>> entrySet ()
  {
    final Set <Map.Entry <K, SoftValue <K, V>>> aSrcEntrySet = m_aSrcMap.entrySet ();
    return new SoftEntrySet <> (aSrcEntrySet, m_aQueue);
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
