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
 * The character set specified in RFC 3501 to use for IMAP4rev1 mailbox name
 * encoding.
 * </p>
 *
 * @see <a href="http://tools.ietf.org/html/rfc3501">RFC 3501</a>
 * @author Jaap Beetstra
 */
final class UTF7CharsetModified extends AbstractUTF7StyleCharset
{
  private static final String MODIFIED_BASE64_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                                                         "abcdefghijklmnopqrstuvwxyz" +
                                                         "0123456789+,";

  UTF7CharsetModified (@Nonnull @Nonempty final String sName, @Nullable final String [] aAliases)
  {
    super (sName, aAliases, MODIFIED_BASE64_ALPHABET, true);
  }

  @Override
  protected boolean canEncodeDirectly (final char ch)
  {
    if (ch == shift ())
      return false;
    return ch >= 0x20 && ch <= 0x7E;
  }

  @Override
  protected byte shift ()
  {
    return '&';
  }

  @Override
  protected byte unshift ()
  {
    return '-';
  }
}
