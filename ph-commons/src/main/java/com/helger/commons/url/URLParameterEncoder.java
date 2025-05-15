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
package com.helger.commons.url;

import java.nio.charset.Charset;

import com.helger.annotation.Nonnull;
import com.helger.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.codec.IEncoder;

/**
 * Encoder for URLs
 *
 * @author Philip Helger
 */
public class URLParameterEncoder implements IEncoder <String, String>
{
  private final Charset m_aCharset;

  public URLParameterEncoder (@Nonnull final Charset aCharset)
  {
    m_aCharset = ValueEnforcer.notNull (aCharset, "Charset");
  }

  /**
   * @return The charset passed in the constructor. Never <code>null</code>.
   * @since 9.4.1
   */
  @Nonnull
  public final Charset getCharset ()
  {
    return m_aCharset;
  }

  @Nullable
  public String getEncoded (@Nullable final String sInput)
  {
    return sInput == null ? null : URLHelper.urlEncode (sInput, m_aCharset);
  }
}
