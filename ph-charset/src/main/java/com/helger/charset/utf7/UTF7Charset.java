/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.charset.utf7;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;

/**
 * <p>
 * The character set specified in RFC 2152. Two variants are supported using the
 * encodeOptional constructor flag
 * </p>
 *
 * @see <a href="http://tools.ietf.org/html/rfc2152">RFC 2152</a>
 * @author Jaap Beetstra
 */
final class UTF7Charset extends AbstractUTF7StyleCharset
{
  private static final String BASE64_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                                                "abcdefghijklmnopqrstuvwxyz" +
                                                "0123456789+/";
  private static final String SET_D = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789'(),-./:?";
  private static final String SET_OPTIONAL = "!\"#$%&*;<=>@[]^_`{|}";
  private static final String RULE_3 = " \t\r\n";

  private final String m_sDirectlyEncoded;

  UTF7Charset (@Nonnull @Nonempty final String sName,
               @Nullable final String [] aAliases,
               final boolean bIncludeOptional)
  {
    super (sName, aAliases, BASE64_ALPHABET, false);
    if (bIncludeOptional)
      m_sDirectlyEncoded = SET_D + SET_OPTIONAL + RULE_3;
    else
      m_sDirectlyEncoded = SET_D + RULE_3;
  }

  @Override
  protected boolean canEncodeDirectly (final char ch)
  {
    return m_sDirectlyEncoded.indexOf (ch) >= 0;
  }

  @Override
  protected byte shift ()
  {
    return '+';
  }

  @Override
  protected byte unshift ()
  {
    return '-';
  }
}
