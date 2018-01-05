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
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;

/**
 * <p>
 * Abstract base class for UTF-7 style encoding and decoding.
 * </p>
 *
 * @author Jaap Beetstra
 */
public abstract class AbstractUTF7StyleCharset extends Charset
{
  private static final ICommonsList <String> CONTAINED = new CommonsArrayList<> ("US-ASCII",
                                                                                 "ISO-8859-1",
                                                                                 "UTF-8",
                                                                                 "UTF-16",
                                                                                 "UTF-16LE",
                                                                                 "UTF-16BE");
  private final boolean m_bStrict;
  private final UTF7Base64Helper m_aBase64;

  /**
   * <p>
   * Besides the name and aliases, two additional parameters are required. First
   * the base 64 alphabet used; in modified UTF-7 a slightly different alphabet
   * is used. Additionally, it should be specified if encoders and decoders
   * should be strict about the interpretation of malformed encoded sequences.
   * This is used since modified UTF-7 specifically disallows some constructs
   * which are allowed (or not specifically disallowed) in UTF-7 (RFC 2152).
   * </p>
   *
   * @param sCanonicalName
   *        The name as defined in java.nio.charset.Charset
   * @param aAliases
   *        The aliases as defined in java.nio.charset.Charset
   * @param sAlphabet
   *        The base 64 alphabet used
   * @param bStrict
   *        True if strict handling of sequences is requested
   */
  protected AbstractUTF7StyleCharset (@Nonnull @Nonempty final String sCanonicalName,
                                      @Nullable final String [] aAliases,
                                      @Nonnull @Nonempty final String sAlphabet,
                                      final boolean bStrict)
  {
    super (sCanonicalName, aAliases);
    m_aBase64 = new UTF7Base64Helper (sAlphabet);
    m_bStrict = bStrict;
  }

  @Override
  public boolean contains (@Nonnull final Charset aCharset)
  {
    return CONTAINED.contains (aCharset.name ());
  }

  @Override
  public CharsetDecoder newDecoder ()
  {
    return new UTF7StyleCharsetDecoder (this, m_aBase64, m_bStrict);
  }

  @Override
  public CharsetEncoder newEncoder ()
  {
    return new UTF7StyleCharsetEncoder (this, m_aBase64, m_bStrict);
  }

  /**
   * Tells if a character can be encoded using simple (US-ASCII) encoding or
   * requires base 64 encoding.
   *
   * @param c
   *        The character
   * @return True if the character can be encoded directly, false otherwise
   */
  protected abstract boolean canEncodeDirectly (char c);

  /**
   * Returns character used to switch to base 64 encoding.
   *
   * @return The shift character
   */
  protected abstract byte shift ();

  /**
   * Returns character used to switch from base 64 encoding to simple encoding.
   *
   * @return The unshift character
   */
  protected abstract byte unshift ();
}
