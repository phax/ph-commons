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
package com.helger.typeconvert.trait;

import org.jspecify.annotations.Nullable;

import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.CommonsHashMap;
import com.helger.collection.commons.ICommonsList;
import com.helger.collection.commons.ICommonsMap;

/**
 * Mock implementations of the three getter traits, for testing purposes.
 *
 * @author Philip Helger
 */
public final class MockGetterTraits
{
  /**
   * A single value.
   *
   * @author Philip Helger
   */
  public static final class Direct implements IGetterDirectTrait
  {
    private final Object m_aValue;

    public Direct (@Nullable final Object aValue)
    {
      m_aValue = aValue;
    }

    @Nullable
    public Object getValue ()
    {
      return m_aValue;
    }
  }

  /**
   * A list of values.
   *
   * @author Philip Helger
   */
  public static final class ByIndex implements IGetterByIndexTrait
  {
    private final ICommonsList <Object> m_aList = new CommonsArrayList <> ();

    public ByIndex (@Nullable final Object... aValues)
    {
      if (aValues != null)
        for (final Object aValue : aValues)
          m_aList.add (aValue);
    }

    @Nullable
    public Object getValue (final int nIndex)
    {
      return nIndex >= 0 && nIndex < m_aList.size () ? m_aList.get (nIndex) : null;
    }
  }

  /**
   * A map of values.
   *
   * @author Philip Helger
   */
  public static final class ByKey implements IGetterByKeyTrait <String>
  {
    private final ICommonsMap <String, Object> m_aMap = new CommonsHashMap <> ();

    public ByKey ()
    {}

    public ByKey put (final String sKey, @Nullable final Object aValue)
    {
      m_aMap.put (sKey, aValue);
      return this;
    }

    @Nullable
    public Object getValue (@Nullable final String sKey)
    {
      return sKey == null ? null : m_aMap.get (sKey);
    }
  }

  private MockGetterTraits ()
  {}
}
