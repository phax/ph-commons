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
package com.helger.commons.collections.iterate;

import java.util.Enumeration;
import java.util.NoSuchElementException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.string.ToStringGenerator;

/**
 * A specific enumeration iterating over two consecutive enumerations.
 * 
 * @author Philip Helger
 * @param <ELEMENTTYPE>
 *        The type to be enumerated
 */
public final class CombinedEnumeration <ELEMENTTYPE> implements Enumeration <ELEMENTTYPE>
{
  private final Enumeration <? extends ELEMENTTYPE> m_aEnum1;
  private final Enumeration <? extends ELEMENTTYPE> m_aEnum2;
  private boolean m_bFirstEnum;

  public CombinedEnumeration (@Nullable final Enumeration <? extends ELEMENTTYPE> aEnum1,
                              @Nullable final Enumeration <? extends ELEMENTTYPE> aEnum2)
  {
    m_aEnum1 = aEnum1;
    m_aEnum2 = aEnum2;
    m_bFirstEnum = aEnum1 != null;
  }

  public boolean hasMoreElements ()
  {
    boolean ret = false;
    if (m_bFirstEnum)
    {
      ret = m_aEnum1 != null && m_aEnum1.hasMoreElements ();
      if (!ret)
        m_bFirstEnum = false;
    }
    if (!m_bFirstEnum)
      ret = m_aEnum2 != null && m_aEnum2.hasMoreElements ();
    return ret;
  }

  @Nullable
  public ELEMENTTYPE nextElement ()
  {
    if (m_bFirstEnum)
      return m_aEnum1.nextElement ();
    if (m_aEnum2 == null)
      throw new NoSuchElementException ();
    return m_aEnum2.nextElement ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("enum1", m_aEnum1).append ("enum2", m_aEnum2).toString ();
  }

  @Nonnull
  public static <ELEMENTTYPE> Enumeration <ELEMENTTYPE> create (@Nullable final Enumeration <? extends ELEMENTTYPE> aEnum1,
                                                                @Nullable final Enumeration <? extends ELEMENTTYPE> aEnum2)
  {
    if (aEnum1 == null && aEnum2 == null)
      return EmptyEnumeration.<ELEMENTTYPE> getInstance ();
    return new CombinedEnumeration <ELEMENTTYPE> (aEnum1, aEnum2);
  }
}
