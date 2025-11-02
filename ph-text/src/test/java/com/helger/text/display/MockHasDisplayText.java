/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.text.display;

import java.util.Locale;
import java.util.Map;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.collection.commons.CommonsHashMap;
import com.helger.collection.commons.ICommonsMap;
import com.helger.collection.helper.CollectionHelperExt;
import com.helger.text.util.TextHelper;

public final class MockHasDisplayText implements IHasDisplayText
{
  private final ICommonsMap <Locale, String> m_aNames = new CommonsHashMap <> ();

  public MockHasDisplayText (@NonNull final Locale aLocale, @Nullable final String sText)
  {
    m_aNames.put (aLocale, sText);
  }

  public MockHasDisplayText (@NonNull final Map <Locale, String> aNames)
  {
    m_aNames.putAll (aNames);
  }

  @Nullable
  public String getDisplayText (@NonNull final Locale aLocale)
  {
    return m_aNames.get (aLocale);
  }

  @NonNull
  public static MockHasDisplayText createDE_EN (@Nullable final String sDE, @Nullable final String sEN)
  {
    return new MockHasDisplayText (CollectionHelperExt.createMap (new Locale [] { TextHelper.DE, TextHelper.EN },
                                                                  new String [] { sDE, sEN }));
  }
}
