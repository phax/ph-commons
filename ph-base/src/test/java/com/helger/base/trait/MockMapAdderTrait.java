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
package com.helger.base.trait;

import java.util.LinkedHashMap;
import java.util.Map;

import org.jspecify.annotations.NonNull;

/**
 * A minimal {@link IGenericMapAdderTrait} implementation, for testing purposes.
 *
 * @author Philip Helger
 */
public final class MockMapAdderTrait implements IGenericMapAdderTrait <String, MockStringValue, MockMapAdderTrait>
{
  private final Map <String, MockStringValue> m_aMap = new LinkedHashMap <> ();

  @NonNull
  public ITypeConverterTo <MockStringValue> getTypeConverterTo ()
  {
    return MockStringValue.CONVERTER;
  }

  @NonNull
  public MockMapAdderTrait add (@NonNull final String sName, final MockStringValue aValue)
  {
    m_aMap.put (sName, aValue);
    return this;
  }

  @NonNull
  public Map <String, String> getAllAsString ()
  {
    final Map <String, String> ret = new LinkedHashMap <> ();
    for (final Map.Entry <String, MockStringValue> aEntry : m_aMap.entrySet ())
      ret.put (aEntry.getKey (), aEntry.getValue () == null ? null : aEntry.getValue ().getValue ());
    return ret;
  }

  public int size ()
  {
    return m_aMap.size ();
  }

  public boolean containsKey (final String sName)
  {
    return m_aMap.containsKey (sName);
  }
}
