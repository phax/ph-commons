package com.helger.commons.collection.ext;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.WrappedCollection;
import com.helger.commons.collection.impl.WrappedSet;

public class CommonsLinkedHashMap <KEYTYPE, VALUETYPE> extends LinkedHashMap <KEYTYPE, VALUETYPE>
                                  implements ICommonsMap <KEYTYPE, VALUETYPE>
{
  private transient volatile WrappedSet <KEYTYPE> m_aKeySet;
  private transient volatile WrappedCollection <VALUETYPE> m_aValues;
  private transient volatile WrappedSet <Map.Entry <KEYTYPE, VALUETYPE>> m_aEntrySet;

  public CommonsLinkedHashMap ()
  {}

  public CommonsLinkedHashMap (final int nInitialCapacity)
  {
    super (nInitialCapacity);
  }

  public CommonsLinkedHashMap (final int nInitialCapacity, final float fLoadFactor)
  {
    super (nInitialCapacity, fLoadFactor);
  }

  public CommonsLinkedHashMap (@Nonnull final Map <? extends KEYTYPE, ? extends VALUETYPE> aMap)
  {
    super (aMap);
  }

  public CommonsLinkedHashMap (final int nInitialCapacity, final float fLoadFactor, final boolean bAccessOrder)
  {
    super (nInitialCapacity, fLoadFactor, bAccessOrder);
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
  public CommonsLinkedHashMap <KEYTYPE, VALUETYPE> getClone ()
  {
    return new CommonsLinkedHashMap <> (this);
  }
}
