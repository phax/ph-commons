/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
package com.helger.commons.text.display;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.text.util.TextHelper;

public final class MockHasDisplayText implements IHasDisplayText
{
  private final Map <Locale, String> m_aNames = new HashMap <Locale, String> ();

  public MockHasDisplayText (@Nonnull final Locale aLocale, @Nullable final String sText)
  {
    m_aNames.put (aLocale, sText);
  }

  public MockHasDisplayText (@Nonnull final Map <Locale, String> aNames)
  {
    m_aNames.putAll (aNames);
  }

  @Nullable
  public String getDisplayText (@Nonnull final Locale aLocale)
  {
    return m_aNames.get (aLocale);
  }

  @Nonnull
  public static MockHasDisplayText createDE_EN (@Nullable final String sDE, @Nullable final String sEN)
  {
    return new MockHasDisplayText (CollectionHelper.newMap (new Locale [] { TextHelper.DE, TextHelper.EN },
                                                            new String [] { sDE, sEN }));
  }
}
