package com.helger.commons.collection.ext;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import com.helger.commons.collection.impl.WrappedCollection;
import com.helger.commons.collection.impl.WrappedSet;

public class CommonsHashMap <KEYTYPE, VALUETYPE> extends HashMap <KEYTYPE, VALUETYPE>
                            implements ICommonsMap <KEYTYPE, VALUETYPE>
{
  private WrappedSet <KEYTYPE> m_aKeySet;
  private WrappedCollection <VALUETYPE> m_aValues;
  private WrappedSet <Map.Entry <KEYTYPE, VALUETYPE>> m_aEntrySet;

  public CommonsHashMap ()
  {}

  public CommonsHashMap (final int nInitialCapacity)
  {
    super (nInitialCapacity);
  }

  public CommonsHashMap (final int nInitialCapacity, final float fLoadFactor)
  {
    super (nInitialCapacity, fLoadFactor);
  }

  public CommonsHashMap (@Nonnull final Map <? extends KEYTYPE, ? extends VALUETYPE> aMap)
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
}
