/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.commons.messagedigest;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.WillClose;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.CGlobal;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.Nonempty;
import com.helger.commons.annotations.PresentForCodeCoverage;
import com.helger.commons.charset.CharsetManager;
import com.helger.commons.io.streams.StreamUtils;
import com.helger.commons.string.StringHelper;

/**
 * Contains helper classes for handling message digests and there results.
 *
 * @author Philip Helger
 */
@Immutable
public final class MessageDigestGeneratorHelper
{
  @PresentForCodeCoverage
  private static final MessageDigestGeneratorHelper s_aInstance = new MessageDigestGeneratorHelper ();

  private MessageDigestGeneratorHelper ()
  {}

  public static long getLongFromDigest (@Nonnull final byte [] aDigest)
  {
    long nDigest = 0;
    for (int i = 0; i < aDigest.length; i += 2)
    {
      // Since we have bytes, we can use the 8 bits constant
      nDigest += (byte) (aDigest[i] << CGlobal.BITS_PER_BYTE) | aDigest[i + 1];
    }
    return nDigest;
  }

  @Nonnull
  public static String getHexValueFromDigest (@Nonnull final byte [] aDigest)
  {
    return StringHelper.getHexEncoded (aDigest);
  }

  @Nonnull
  @Deprecated
  public static byte [] getDigest (@Nonnull final EMessageDigestAlgorithm eAlgorithm,
                                   @Nonnull final String sText,
                                   @Nonnull @Nonempty final String sCharset)
  {
    return getDigest (sText, sCharset, eAlgorithm);
  }

  @Nonnull
  public static byte [] getDigest (@Nonnull final EMessageDigestAlgorithm eAlgorithm,
                                   @Nonnull final String sText,
                                   @Nonnull final Charset aCharset)
  {
    return getDigest (sText, aCharset, eAlgorithm);
  }

  @Nonnull
  public static byte [] getDigest (@Nonnull final EMessageDigestAlgorithm eAlgorithm, @Nonnull final byte [] aBytes)
  {
    return getDigest (aBytes, eAlgorithm);
  }

  @Nonnull
  public static byte [] getDigest (@Nonnull final EMessageDigestAlgorithm eAlgorithm,
                                   @Nonnull final byte [] aBytes,
                                   @Nonnegative final int nOfs,
                                   @Nonnegative final int nLength)
  {
    return getDigest (aBytes, nOfs, nLength, eAlgorithm);
  }

  @Nonnull
  @Deprecated
  public static byte [] getDigest (@Nonnull final String sContent,
                                   @Nonnull @Nonempty final String sCharset,
                                   @Nonnull @Nonempty final EMessageDigestAlgorithm... aAlgorithms)
  {
    return getDigest (CharsetManager.getAsBytes (sContent, sCharset), aAlgorithms);
  }

  @Nonnull
  public static byte [] getDigest (@Nonnull final String sContent,
                                   @Nonnull final Charset aCharset,
                                   @Nonnull @Nonempty final EMessageDigestAlgorithm... aAlgorithms)
  {
    return getDigest (CharsetManager.getAsBytes (sContent, aCharset), aAlgorithms);
  }

  @Nonnull
  public static byte [] getDigest (@Nonnull final byte [] aContent,
                                   @Nonnull @Nonempty final EMessageDigestAlgorithm... aAlgorithms)
  {
    return getDigest (aContent, new NonBlockingMessageDigestGenerator (aAlgorithms));
  }

  @Nonnull
  public static byte [] getDigest (@Nonnull final byte [] aContent,
                                   @Nonnull final NonBlockingMessageDigestGenerator aMDGen)
  {
    return aMDGen.update (aContent).getDigest ();
  }

  @Nonnull
  public static byte [] getDigest (@Nonnull final byte [] aContent,
                                   @Nonnegative final int nOfs,
                                   @Nonnegative final int nLength,
                                   @Nonnull @Nonempty final EMessageDigestAlgorithm... aAlgorithms)
  {
    return getDigest (aContent, nOfs, nLength, new NonBlockingMessageDigestGenerator (aAlgorithms));
  }

  @Nonnull
  public static byte [] getDigest (@Nonnull final byte [] aContent,
                                   @Nonnegative final int nOfs,
                                   @Nonnegative final int nLength,
                                   @Nonnull final NonBlockingMessageDigestGenerator aMDGen)
  {
    return aMDGen.update (aContent, nOfs, nLength).getDigest ();
  }

  /**
   * Create a hash value from the complete input stream.
   *
   * @param aIS
   *        The input stream to create the hash value from. May not be
   *        <code>null</code>.
   * @param aAlgorithms
   *        The list of algorithms to choose the first one from. May neither be
   *        <code>null</code> nor empty.
   * @return The non-<code>null</code> message digest byte array
   */
  @Nonnull
  public static byte [] getDigestFromInputStream (@Nonnull @WillClose final InputStream aIS,
                                                  @Nonnull @Nonempty final EMessageDigestAlgorithm... aAlgorithms)
  {
    ValueEnforcer.notNull (aIS, "InputStream");

    final NonBlockingMessageDigestGenerator aMDGen = new NonBlockingMessageDigestGenerator (aAlgorithms);
    return getDigestFromInputStream (aIS, aMDGen);
  }

  /**
   * Create a hash value from the complete input stream.
   *
   * @param aIS
   *        The input stream to create the hash value from. May not be
   *        <code>null</code>.
   * @param aMDGen
   *        The generator to be used. May not be <code>null</code>.
   * @return The non-<code>null</code> message digest byte array
   */
  @Nonnull
  public static byte [] getDigestFromInputStream (@Nonnull @WillClose final InputStream aIS,
                                                  @Nonnull final NonBlockingMessageDigestGenerator aMDGen)
  {
    ValueEnforcer.notNull (aIS, "InputStream");
    ValueEnforcer.notNull (aMDGen, "MDGen");

    final byte [] aBuf = new byte [2048];
    try
    {
      int nBytesRead;
      while ((nBytesRead = aIS.read (aBuf)) > -1)
        aMDGen.update (aBuf, 0, nBytesRead);
      return aMDGen.getDigest ();
    }
    catch (final IOException ex)
    {
      throw new IllegalStateException ("Failed to read from InputStream for digesting!", ex);
    }
    finally
    {
      StreamUtils.close (aIS);
    }
  }
}
