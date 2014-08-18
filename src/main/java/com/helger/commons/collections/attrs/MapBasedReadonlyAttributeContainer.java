/**
 * Copyright (C) 2006-2014 phloc systems (www.phloc.com)
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.commons.collections.attrs;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.ReturnsMutableCopy;
import com.helger.commons.collections.ContainerHelper;
import com.helger.commons.hash.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * Default implementation of the {@link IReadonlyAttributeContainer} based on a
 * hash map. This implementation may carry <code>null</code> values but that is
 * not recommended.
 * 
 * @author Philip Helger
 */
@NotThreadSafe
public final class MapBasedReadonlyAttributeContainer extends AbstractReadonlyAttributeContainer
{
  private final Map <String, Object> m_aAttrs;

  public MapBasedReadonlyAttributeContainer (@Nonnull final Map <String, ?> aMap)
  {
    ValueEnforcer.notNull (aMap, "Map");
    m_aAttrs = ContainerHelper.newMap (aMap);
  }

  public MapBasedReadonlyAttributeContainer (@Nonnull final IReadonlyAttributeContainer aCont)
  {
    ValueEnforcer.notNull (aCont, "Container");
    // Must already be a copy!
    m_aAttrs = aCont.getAllAttributes ();
  }

  public boolean containsAttribute (@Nullable final String sName)
  {
    return m_aAttrs.containsKey (sName);
  }

  @Nonnull
  @ReturnsMutableCopy
  public Map <String, Object> getAllAttributes ()
  {
    return ContainerHelper.newMap (m_aAttrs);
  }

  @Nonnull
  public Enumeration <String> getAttributeNames ()
  {
    return ContainerHelper.getEnumeration (m_aAttrs.keySet ());
  }

  @Nonnull
  @ReturnsMutableCopy
  public Set <String> getAllAttributeNames ()
  {
    return ContainerHelper.newSet (m_aAttrs.keySet ());
  }

  @Nonnull
  @ReturnsMutableCopy
  public Collection <Object> getAllAttributeValues ()
  {
    return ContainerHelper.newList (m_aAttrs.values ());
  }

  @Nullable
  public Object getAttributeObject (@Nullable final String sName)
  {
    return m_aAttrs.get (sName);
  }

  @Nonnegative
  public int getAttributeCount ()
  {
    return m_aAttrs.size ();
  }

  public boolean containsNoAttribute ()
  {
    return m_aAttrs.isEmpty ();
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (!(o instanceof MapBasedReadonlyAttributeContainer))
      return false;
    final MapBasedReadonlyAttributeContainer rhs = (MapBasedReadonlyAttributeContainer) o;
    return m_aAttrs.equals (rhs.m_aAttrs);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aAttrs).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("map", m_aAttrs).toString ();
  }
}
