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

import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.CheckForSigned;

/**
 * A minimal {@link IGenericAdderTrait} implementation collecting Strings, for testing purposes.
 *
 * @author Philip Helger
 */
public final class MockAdderTrait implements IGenericAdderTrait <MockStringValue, MockAdderTrait>
{
  private final List <MockStringValue> m_aList = new ArrayList <> ();

  @NonNull
  public ITypeConverterTo <MockStringValue> getTypeConverterTo ()
  {
    return MockStringValue.CONVERTER;
  }

  @NonNull
  public MockAdderTrait addAt (@CheckForSigned final int nIndex, @Nullable final MockStringValue aValue)
  {
    if (nIndex < 0)
      m_aList.add (aValue);
    else
      m_aList.add (nIndex, aValue);
    return this;
  }

  @NonNull
  public List <MockStringValue> getAll ()
  {
    return new ArrayList <> (m_aList);
  }

  @NonNull
  public List <String> getAllAsString ()
  {
    final List <String> ret = new ArrayList <> ();
    for (final MockStringValue aValue : m_aList)
      ret.add (aValue == null ? null : aValue.getValue ());
    return ret;
  }

  public int size ()
  {
    return m_aList.size ();
  }
}
