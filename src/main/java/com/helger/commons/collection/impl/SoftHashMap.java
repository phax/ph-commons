package com.helger.commons.collection.impl;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.OverrideOnDemand;

/**
 * Soft {@link HashMap} implementation based on
 * http://www.javaspecialists.eu/archive/Issue015.html<br />
 * The entrySet implementation is from org.hypergraphdb.util
 *
 * @author Philip Helger
 * @param <K>
 *        Key type
 * @param <V>
 *        Value type
 */
public class SoftHashMap <K, V> extends AbstractMap <K, V>
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
  private static class SoftValue <K, V> extends SoftReference <V>
  {
    private final K m_aKey;

    private SoftValue (final K aKey, final V aValue, final ReferenceQueue <? super V> aQueue)
    {
      super (aValue, aQueue);
      m_aKey = aKey;
    }
  }

  private static class SoftMapEntry <K, V> implements Map.Entry <K, SoftValue <K, V>>
  {
    private final K m_aKey;
    private SoftValue <K, V> m_aSoftValue;

    public SoftMapEntry (final K aKey, final V aValue, final ReferenceQueue <V> aQueue)
    {
      m_aKey = aKey;
      m_aSoftValue = new SoftValue <K, V> (aKey, aValue, aQueue);
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

    public final SoftValue <K, V> setValue (final SoftValue <K, V> aNew)
    {
      final SoftValue <K, V> aOld = aNew;
      m_aSoftValue = aNew;
      return aOld;
    }
  }

  private static final class SoftEntrySet <K, V> implements Set <Map.Entry <K, V>>
  {
    private final Set <Entry <K, SoftValue <K, V>>> m_aSrcEntrySet;
    private final ReferenceQueue <V> m_aQueue;

    private SoftEntrySet (final Set <Entry <K, SoftValue <K, V>>> aSrcEntrySet, final ReferenceQueue <V> aQueue)
    {
      m_aSrcEntrySet = aSrcEntrySet;
      m_aQueue = aQueue;
    }

    public boolean add (final Map.Entry <K, V> aEntry)
    {
      return m_aSrcEntrySet.add (new SoftMapEntry <K, V> (aEntry.getKey (), aEntry.getValue (), m_aQueue));
    }

    public boolean addAll (final Collection <? extends Map.Entry <K, V>> c)
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

    @SuppressWarnings ("unchecked")
    public boolean contains (final Object o)
    {
      if (!(o instanceof Map.Entry))
        return false;
      final Map.Entry <K, V> e = (Map.Entry <K, V>) o;
      return m_aSrcEntrySet.contains (new SoftMapEntry <K, V> (e.getKey (), e.getValue (), m_aQueue));
    }

    public boolean containsAll (final Collection <?> c)
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

    public Iterator <Map.Entry <K, V>> iterator ()
    {
      final Iterator <Map.Entry <K, SoftValue <K, V>>> SrcIter = m_aSrcEntrySet.iterator ();
      return new Iterator <Map.Entry <K, V>> ()
      {
        public boolean hasNext ()
        {
          return SrcIter.hasNext ();
        }

        public Map.Entry <K, V> next ()
        {
          final Map.Entry <K, SoftValue <K, V>> e = SrcIter.next ();
          return new MapEntry <K, V> (e.getKey (), e.getValue ().get ());
        }

        public void remove ()
        {
          SrcIter.remove ();
        }
      };
    }

    @SuppressWarnings ("unchecked")
    public boolean remove (final Object o)
    {
      if (!(o instanceof Map.Entry <?, ?>))
        return false;
      final Map.Entry <K, V> aEntry = (Map.Entry <K, V>) o;
      return m_aSrcEntrySet.remove (new SoftMapEntry <K, V> (aEntry.getKey (), aEntry.getValue (), m_aQueue));
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
    @SuppressWarnings ("unchecked")
    public <T> T [] toArray (@Nullable final T [] a)
    {
      MapEntry <K, V> [] result = null;
      if (a != null && a instanceof MapEntry <?, ?> [] && a.length >= size ())
        result = (MapEntry <K, V> []) a;
      else
        result = (MapEntry <K, V> []) new Object [size ()];

      final Object [] aSrcArray = m_aSrcEntrySet.toArray ();
      for (int i = 0; i < aSrcArray.length; i++)
      {
        final Map.Entry <K, SoftValue <K, V>> e = (Map.Entry <K, SoftValue <K, V>>) aSrcArray[i];
        result[i] = new MapEntry <K, V> (e.getKey (), e.getValue ().get ());
      }
      return (T []) result;
    }
  }

  /** The internal HashMap that will hold the SoftReference. */
  private final Map <K, SoftValue <K, V>> m_aSrcMap = new HashMap <K, SoftValue <K, V>> ();
  /** Reference queue for cleared SoftReference objects. */
  private final ReferenceQueue <V> m_aQueue = new ReferenceQueue <V> ();

  public SoftHashMap ()
  {}

  /**
   * Callback method invoked when a value is garbage collected
   *
   * @param aKey
   *        Key
   * @param aValue
   *        Value
   */
  @OverrideOnDemand
  protected void onRemoved (final K aKey)
  {}

  @SuppressWarnings ("unchecked")
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
        m_aSrcMap.remove (aKey);
        onRemoved ((K) aKey);
      }
    }
    return ret;
  }

  /**
   * Here we go through the ReferenceQueue and remove garbage collected
   * SoftValue objects from the HashMap by looking them up using the
   * SoftValue.m_aKey data member.
   */
  @SuppressWarnings ("unchecked")
  private void _processQueue ()
  {
    SoftValue <K, V> aSoftValue;
    while ((aSoftValue = ((SoftValue <K, V>) m_aQueue.poll ())) != null)
    {
      m_aSrcMap.remove (aSoftValue.m_aKey);
      onRemoved (aSoftValue.m_aKey);
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
    final SoftValue <K, V> aOld = m_aSrcMap.put (aKey, new SoftValue <K, V> (aKey, aValue, m_aQueue));
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
  public Set <Map.Entry <K, V>> entrySet ()
  {
    final Set <Map.Entry <K, SoftValue <K, V>>> aSrcEntrySet = m_aSrcMap.entrySet ();
    return new SoftEntrySet <K, V> (aSrcEntrySet, m_aQueue);
  }
}
