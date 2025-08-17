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
package com.helger.commons.base64;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.codec.base64.Base64;
import com.helger.base.codec.base64.Base64InputStream;
import com.helger.base.codec.base64.Base64OutputStream;
import com.helger.base.equals.ValueEnforcer;
import com.helger.base.io.nonblocking.NonBlockingBufferedInputStream;
import com.helger.base.io.nonblocking.NonBlockingBufferedOutputStream;
import com.helger.base.state.ESuccess;
import com.helger.io.file.FileHelper;

import jakarta.annotation.Nonnull;

/**
 * This class contains the {@link File} based APIs for Base64
 *
 * @author Robert Harder
 * @author Philip Helger
 * @version 2.3.7
 */
@Immutable
public final class Base64File
{
  /**
   * Convenience method for encoding data to a file.
   * <p>
   * As of v 2.3, if there is a error, the method will throw an IOException. <b>This is new to
   * v2.3!</b> In earlier versions, it just returned false, but in retrospect that's a pretty poor
   * way to handle it.
   * </p>
   *
   * @param aDataToEncode
   *        byte array of data to encode in base64 form
   * @param aFile
   *        File for saving encoded data
   * @return {@link ESuccess}
   * @throws IOException
   *         if there is an error
   * @throws NullPointerException
   *         if dataToEncode is null
   * @since 2.1
   */
  @Nonnull
  public static ESuccess encodeToFile (@Nonnull final byte [] aDataToEncode, @Nonnull final File aFile)
                                                                                                        throws IOException
  {
    ValueEnforcer.notNull (aDataToEncode, "DataToEncode");
    ValueEnforcer.notNull (aFile, "File");

    try (final NonBlockingBufferedOutputStream aBOS = FileHelper.getBufferedOutputStream (aFile))
    {
      if (aBOS == null)
        return ESuccess.FAILURE;
      try (final Base64OutputStream bos = new Base64OutputStream (aBOS, Base64.ENCODE))
      {
        bos.write (aDataToEncode);
        return ESuccess.SUCCESS;
      }
    }
  }

  /**
   * Convenience method for decoding data to a file.
   * <p>
   * As of v 2.3, if there is a error, the method will throw an IOException. <b>This is new to
   * v2.3!</b> In earlier versions, it just returned false, but in retrospect that's a pretty poor
   * way to handle it.
   * </p>
   *
   * @param sDataToDecode
   *        Base64-encoded data as a string
   * @param aFile
   *        File for saving decoded data
   * @return {@link ESuccess}
   * @throws IOException
   *         if there is an error
   * @since 2.1
   */
  @Nonnull
  public static ESuccess decodeToFile (@Nonnull final String sDataToDecode, @Nonnull final File aFile)
                                                                                                       throws IOException
  {
    ValueEnforcer.notNull (sDataToDecode, "DataToDecode");
    ValueEnforcer.notNull (aFile, "File");

    try (final NonBlockingBufferedOutputStream aBOS = FileHelper.getBufferedOutputStream (aFile))
    {
      if (aBOS == null)
        return ESuccess.FAILURE;
      try (final Base64OutputStream bos = new Base64OutputStream (aBOS, Base64.DECODE))
      {
        bos.write (sDataToDecode.getBytes (Base64.PREFERRED_ENCODING));
        return ESuccess.SUCCESS;
      }
    }
  }

  /**
   * Convenience method for reading a base64-encoded file and decoding it.
   * <p>
   * As of v 2.3, if there is a error, the method will throw an IOException. <b>This is new to
   * v2.3!</b> In earlier versions, it just returned false, but in retrospect that's a pretty poor
   * way to handle it.
   * </p>
   *
   * @param sFilename
   *        Filename for reading encoded data
   * @return decoded byte array
   * @throws IOException
   *         if there is an error
   * @since 2.1
   */
  @Nonnull
  @ReturnsMutableCopy
  public static byte [] decodeFromFile (@Nonnull final String sFilename) throws IOException
  {
    // Setup some useful variables
    final File aFile = new File (sFilename);

    // Check for size of file
    if (aFile.length () > Integer.MAX_VALUE)
      throw new IOException ("File '" +
                             sFilename +
                             "' is too big for this convenience method (" +
                             aFile.length () +
                             " bytes).");

    try (final NonBlockingBufferedInputStream aIS = FileHelper.getBufferedInputStream (aFile))
    {
      if (aIS == null)
        throw new IOException ("Failed to open file '" + aFile.getAbsolutePath () + "'");

      final byte [] buffer = new byte [(int) aFile.length ()];

      // Open a stream
      try (final Base64InputStream bis = new Base64InputStream (aIS, Base64.DECODE))
      {
        int nOfs = 0;
        int numBytes;

        // Read until done
        while ((numBytes = bis.read (buffer, nOfs, 4096)) >= 0)
        {
          nOfs += numBytes;
        }

        // Save in a variable to return
        final byte [] decodedData = new byte [nOfs];
        System.arraycopy (buffer, 0, decodedData, 0, nOfs);
        return decodedData;
      }
    }
  }

  /**
   * Convenience method for reading a binary file and base64-encoding it.
   * <p>
   * As of v 2.3, if there is a error, the method will throw an IOException. <b>This is new to
   * v2.3!</b> In earlier versions, it just returned false, but in retrospect that's a pretty poor
   * way to handle it.
   * </p>
   *
   * @param sFilename
   *        Filename for reading binary data
   * @return base64-encoded string
   * @throws IOException
   *         if there is an error
   * @since 2.1
   */
  @Nonnull
  public static String encodeFromFile (@Nonnull final String sFilename) throws IOException
  {
    // Setup some useful variables
    final File aFile = new File (sFilename);

    // Check for size of file
    // Need +1 for a few corner cases (v2.3.5)
    final long nTargetLen = (long) (aFile.length () * 1.4) + 1;
    if (nTargetLen > Integer.MAX_VALUE)
      throw new IOException ("File '" +
                             sFilename +
                             "' is too big for this convenience method (" +
                             aFile.length () +
                             " bytes).");

    try (final NonBlockingBufferedInputStream aIS = FileHelper.getBufferedInputStream (aFile))
    {
      if (aIS == null)
        throw new IOException ("Failed to open file '" + aFile.getAbsolutePath () + "'");

      // Open a stream
      try (final Base64InputStream bis = new Base64InputStream (aIS, Base64.ENCODE))
      {
        // Need max() for math on small files (v2.2.1);
        final byte [] aBuffer = new byte [Math.max ((int) nTargetLen, 40)];

        int nLength = 0;
        int nBytes;

        // Read until done
        while ((nBytes = bis.read (aBuffer, nLength, 4096)) >= 0)
        {
          nLength += nBytes;
        }

        // Save in a variable to return
        return new String (aBuffer, 0, nLength, Base64.PREFERRED_ENCODING);
      }
    }
  }

  /**
   * Reads <code>infile</code> and encodes it to <code>outfile</code>.
   *
   * @param infile
   *        Input file
   * @param aFile
   *        Output file
   * @throws IOException
   *         if there is an error
   * @since 2.2
   */
  public static void encodeFileToFile (@Nonnull final String infile, @Nonnull final File aFile) throws IOException
  {
    final String encoded = encodeFromFile (infile);
    try (final OutputStream out = FileHelper.getBufferedOutputStream (aFile))
    {
      // Strict, 7-bit output.
      out.write (encoded.getBytes (Base64.PREFERRED_ENCODING));
    }
  }

  /**
   * Reads <code>infile</code> and decodes it to <code>outfile</code>.
   *
   * @param aInFile
   *        Input file
   * @param aOutFile
   *        Output file
   * @throws IOException
   *         if there is an error
   * @since 2.2
   */
  public static void decodeFileToFile (@Nonnull final String aInFile, @Nonnull final File aOutFile) throws IOException
  {
    final byte [] decoded = decodeFromFile (aInFile);
    try (final OutputStream out = FileHelper.getBufferedOutputStream (aOutFile))
    {
      out.write (decoded);
    }
  }
}
