package com.helger.commons.collection.ext;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.collection.impl.WrappedCollection;
import com.helger.commons.collection.impl.WrappedSet;

public class CommonsTreeMap <KEYTYPE, VALUETYPE> extends TreeMap <KEYTYPE, VALUETYPE>
                            implements ICommonsMap <KEYTYPE, VALUETYPE>
{
  private WrappedSet <KEYTYPE> m_aKeySet;
  private WrappedCollection <VALUETYPE> m_aValues;
  private WrappedSet <Map.Entry <KEYTYPE, VALUETYPE>> m_aEntrySet;

  public CommonsTreeMap ()
  {}

  public CommonsTreeMap (@Nullable final Comparator <? super KEYTYPE> aComparator)
  {
    super (aComparator);
  }

  public CommonsTreeMap (@Nonnull final Map <? extends KEYTYPE, ? extends VALUETYPE> aMap)
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
