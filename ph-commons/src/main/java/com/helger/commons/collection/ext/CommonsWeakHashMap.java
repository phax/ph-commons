package com.helger.commons.collection.ext;

import java.util.Map;
import java.util.WeakHashMap;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.WrappedCollection;
import com.helger.commons.collection.impl.WrappedSet;

public class CommonsWeakHashMap <KEYTYPE, VALUETYPE> extends WeakHashMap <KEYTYPE, VALUETYPE>
                                implements ICommonsMap <KEYTYPE, VALUETYPE>
{
  private transient volatile WrappedSet <KEYTYPE> m_aKeySet;
  private transient volatile WrappedCollection <VALUETYPE> m_aValues;
  private transient volatile WrappedSet <Map.Entry <KEYTYPE, VALUETYPE>> m_aEntrySet;

  public CommonsWeakHashMap ()
  {}

  public CommonsWeakHashMap (final int nInitialCapacity)
  {
    super (nInitialCapacity);
  }

  public CommonsWeakHashMap (final int nInitialCapacity, final float fLoadFactor)
  {
    super (nInitialCapacity, fLoadFactor);
  }

  public CommonsWeakHashMap (@Nonnull final Map <? extends KEYTYPE, ? extends VALUETYPE> aMap)
  {
    super (aMap);
  }

  @Override
  public ICommonsSet <KEYTYPE> keySet ()
  {
    final ICommonsSet <KEYTYPE> ks = m_aKeySet;
    if (ks != null)
      return ks;
    m_aKeySet = new WrappedSet <> (super.keySet ());
    return m_aKeySet;
  }

  @Override
  public ICommonsCollection <VALUETYPE> values ()
  {
    final ICommonsCollection <VALUETYPE> vs = m_aValues;
    if (vs != null)
      return vs;
    m_aValues = new WrappedCollection <> (super.values ());
    return m_aValues;
  }

  @Override
  public ICommonsSet <Map.Entry <KEYTYPE, VALUETYPE>> entrySet ()
  {
    final ICommonsSet <Map.Entry <KEYTYPE, VALUETYPE>> es = m_aEntrySet;
    if (es != null)
      return es;
    m_aEntrySet = new WrappedSet <> (super.entrySet ());
    return m_aEntrySet;
  }

  @Nonnull
  @ReturnsMutableCopy
  public CommonsWeakHashMap <KEYTYPE, VALUETYPE> getClone ()
  {
    return new CommonsWeakHashMap <> (this);
  }
}
