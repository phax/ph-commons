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

import java.nio.charset.Charset;
import java.nio.charset.spi.CharsetProvider;
import java.util.Iterator;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.IsSPIImplementation;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;

/**
 * <p>
 * Charset service-provider class used for both variants of the UTF-7 charset
 * and the modified-UTF-7 charset.
 * </p>
 *
 * @author Jaap Beetstra
 */
@IsSPIImplementation
public class UTF7CharsetProviderSPI extends CharsetProvider
{
  private static final String UTF7_NAME = "UTF-7";
  private static final String UTF7_O_NAME = "X-UTF-7-OPTIONAL";
  private static final String UTF7_M_NAME = "X-MODIFIED-UTF-7";
  private static final String [] UTF7_ALIASES = new String [] { "UNICODE-1-1-UTF-7",
                                                                "CSUNICODE11UTF7",
                                                                "X-RFC2152",
                                                                "X-RFC-2152" };
  private static final String [] UTF7_O_ALIASES = new String [] { "X-RFC2152-OPTIONAL", "X-RFC-2152-OPTIONAL" };
  private static final String [] UTF7_M_ALIASES = new String [] { "X-IMAP-MODIFIED-UTF-7",
                                                                  "X-IMAP4-MODIFIED-UTF7",
                                                                  "X-IMAP4-MODIFIED-UTF-7",
                                                                  "X-RFC3501",
                                                                  "X-RFC-3501" };
  private static final Charset CHARSET_UTF7 = new UTF7Charset (UTF7_NAME, UTF7_ALIASES, false);
  private static final Charset CHARSET_UTF7_O = new UTF7Charset (UTF7_O_NAME, UTF7_O_ALIASES, true);
  private static final Charset CHARSET_UTF7_M = new UTF7CharsetModified (UTF7_M_NAME, UTF7_M_ALIASES);

  private final ICommonsList <Charset> m_aCharsets;

  public UTF7CharsetProviderSPI ()
  {
    m_aCharsets = new CommonsArrayList<> (CHARSET_UTF7, CHARSET_UTF7_M, CHARSET_UTF7_O);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Nullable
  public Charset charsetForName (@Nonnull final String sCharsetName)
  {
    final String sRealCharsetName = sCharsetName.toUpperCase (Locale.US);
    for (final Charset aCharset : m_aCharsets)
      if (aCharset.name ().equals (sRealCharsetName))
        return aCharset;
    for (final Charset aCharset : m_aCharsets)
      if (aCharset.aliases ().contains (sRealCharsetName))
        return aCharset;
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Iterator <Charset> charsets ()
  {
    return m_aCharsets.iterator ();
  }
}
