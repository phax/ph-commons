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

import java.util.Arrays;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.Nonempty;

/**
 * <p>
 * Represent a base 64 mapping. The 64 characters used in the encoding can be
 * specified, since modified-UTF-7 uses other characters than UTF-7 (',' instead
 * of '/').
 * </p>
 * <p>
 * The exact type of the arguments and result values is adapted to the needs of
 * the encoder and decoder, as opposed to following a strict interpretation of
 * base 64.
 * </p>
 * <p>
 * Base 64, as specified in RFC 2045, is an encoding used to encode bytes as
 * characters. In (modified-)UTF-7 however, it is used to encode characters as
 * bytes, using some intermediate steps:
 * </p>
 * <ol>
 * <li>Encode all characters as a 16-bit (UTF-16) integer value</li>
 * <li>Write this as stream of bytes (most-significant first)</li>
 * <li>Encode these bytes using (modified) base 64 encoding</li>
 * <li>Write the thus formed stream of characters as a stream of bytes, using
 * ASCII encoding</li>
 * </ol>
 *
 * @author Jaap Beetstra
 */
final class UTF7Base64Helper
{
  private static final int ALPHABET_LENGTH = 64;

  private final char [] m_aAlphabet;
  private final int [] m_aInverseAlphabet;

  /**
   * Initializes the class with the specified encoding/decoding alphabet.
   *
   * @param sAlphabet
   *        alphabet
   * @throws IllegalArgumentException
   *         if alphabet is not 64 characters long or contains characters which
   *         are not 7-bit ASCII
   */
  public UTF7Base64Helper (@Nonnull @Nonempty final String sAlphabet)
  {
    m_aAlphabet = sAlphabet.toCharArray ();
    if (sAlphabet.length () != ALPHABET_LENGTH)
      throw new IllegalArgumentException ("alphabet has incorrect length (should be 64, not " +
                                          sAlphabet.length () +
                                          ")");
    m_aInverseAlphabet = new int [128];
    Arrays.fill (m_aInverseAlphabet, -1);
    for (int i = 0; i < m_aAlphabet.length; i++)
    {
      final char ch = m_aAlphabet[i];
      if (ch >= 128)
        throw new IllegalArgumentException ("invalid character in alphabet: " + ch);
      m_aInverseAlphabet[ch] = i;
    }
  }

  /**
   * Returns the integer value of the six bits represented by the specified
   * character.
   *
   * @param ch
   *        The character, as a ASCII encoded byte
   * @return The six bits, as an integer value, or -1 if the byte is not in the
   *         alphabet
   */
  public int getSextet (final int ch)
  {
    if (ch >= 128)
      return -1;
    return m_aInverseAlphabet[ch];
  }

  /**
   * Tells whether the alphabet contains the specified character.
   *
   * @param ch
   *        The character
   * @return true if the alphabet contains <code>ch</code>, false otherwise
   */
  public boolean contains (final char ch)
  {
    if (ch >= 128)
      return false;
    return m_aInverseAlphabet[ch] >= 0;
  }

  /**
   * Encodes the six bit group as a character.
   *
   * @param sextet
   *        The six bit group to be encoded
   * @return The ASCII value of the character
   */
  public byte getChar (final int sextet)
  {
    return (byte) m_aAlphabet[sextet];
  }
}
