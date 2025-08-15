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
package com.helger.io.stream;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.ObjIntConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.CheckForSigned;
import com.helger.annotation.Nonempty;
import com.helger.annotation.Nonnegative;
import com.helger.annotation.WillClose;
import com.helger.annotation.WillNotClose;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.CGlobal;
import com.helger.base.equals.ValueEnforcer;
import com.helger.base.io.iface.IHasInputStream;
import com.helger.base.io.nonblocking.NonBlockingBufferedReader;
import com.helger.base.io.stream.StreamHelper;
import com.helger.base.state.ESuccess;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Some very basic IO stream utility stuff. All input stream (=reading) related stuff is quite
 * <code>null</code> aware, where on writing an output stream may never be null.
 *
 * @author Philip Helger
 */
@Immutable
public final class StreamHelperExt extends StreamHelper
{
  /** The logger to use. */
  private static final Logger LOGGER = LoggerFactory.getLogger (StreamHelperExt.class);

  @PresentForCodeCoverage
  private static final StreamHelperExt INSTANCE = new StreamHelperExt ();

  private StreamHelperExt ()
  {}

  /**
   * Get the content of the passed Spring resource as one big string in the passed character set.
   *
   * @param aISP
   *        The resource to read. May not be <code>null</code>.
   * @param aCharset
   *        The character set to use. May not be <code>null</code>.
   * @return <code>null</code> if the resolved input stream is <code>null</code> , the content
   *         otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static ICommonsList <String> readStreamLines (@Nullable final IHasInputStream aISP,
                                                       @Nonnull final Charset aCharset)
  {
    return readStreamLines (aISP, aCharset, 0, CGlobal.ILLEGAL_UINT);
  }

  /**
   * Get the content of the passed Spring resource as one big string in the passed character set.
   *
   * @param aISP
   *        The resource to read. May be <code>null</code>.
   * @param aCharset
   *        The character set to use. May not be <code>null</code>.
   * @param nLinesToSkip
   *        The 0-based index of the first line to read. Pass in 0 to indicate to read everything.
   * @param nLinesToRead
   *        The number of lines to read. Pass in {@link CGlobal#ILLEGAL_UINT} to indicate that all
   *        lines should be read. If the number passed here exceeds the number of lines in the file,
   *        nothing happens.
   * @return <code>null</code> if the resolved input stream is <code>null</code> , the content
   *         otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static ICommonsList <String> readStreamLines (@Nullable final IHasInputStream aISP,
                                                       @Nonnull final Charset aCharset,
                                                       @Nonnegative final int nLinesToSkip,
                                                       @CheckForSigned final int nLinesToRead)
  {
    if (aISP == null)
      return null;

    return readStreamLines (aISP.getInputStream (), aCharset, nLinesToSkip, nLinesToRead);
  }

  /**
   * Get the content of the passed stream as a list of lines in the passed character set.
   *
   * @param aIS
   *        The input stream to read from. May be <code>null</code>.
   * @param aCharset
   *        The character set to use. May not be <code>null</code>.
   * @return <code>null</code> if the input stream is <code>null</code>, the content lines
   *         otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static ICommonsList <String> readStreamLines (@WillClose @Nullable final InputStream aIS,
                                                       @Nonnull @Nonempty final Charset aCharset)
  {
    return readStreamLines (aIS, aCharset, 0, CGlobal.ILLEGAL_UINT);
  }

  /**
   * Get the content of the passed stream as a list of lines in the passed character set.
   *
   * @param aIS
   *        The input stream to read from. May be <code>null</code>.
   * @param aCharset
   *        The character set to use. May not be <code>null</code>.
   * @param aTargetList
   *        The list to be filled with the lines. May not be <code>null</code>.
   */
  public static void readStreamLines (@WillClose @Nullable final InputStream aIS,
                                      @Nonnull final Charset aCharset,
                                      @Nonnull final List <String> aTargetList)
  {
    if (aIS != null)
      readStreamLines (aIS, aCharset, 0, CGlobal.ILLEGAL_UINT, aTargetList::add);
  }

  /**
   * Get the content of the passed stream as a list of lines in the passed character set.
   *
   * @param aIS
   *        The input stream to read from. May be <code>null</code>.
   * @param aCharset
   *        The character set to use. May not be <code>null</code>.
   * @param nLinesToSkip
   *        The 0-based index of the first line to read. Pass in 0 to indicate to read everything.
   * @param nLinesToRead
   *        The number of lines to read. Pass in {@link CGlobal#ILLEGAL_UINT} to indicate that all
   *        lines should be read. If the number passed here exceeds the number of lines in the file,
   *        nothing happens.
   * @return <code>null</code> if the input stream is <code>null</code>, the content lines
   *         otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static ICommonsList <String> readStreamLines (@WillClose @Nullable final InputStream aIS,
                                                       @Nonnull final Charset aCharset,
                                                       @Nonnegative final int nLinesToSkip,
                                                       @CheckForSigned final int nLinesToRead)
  {
    if (aIS == null)
      return null;

    // Read stream and collect all read lines in a list
    final ICommonsList <String> ret = new CommonsArrayList <> ();
    readStreamLines (aIS, aCharset, nLinesToSkip, nLinesToRead, ret::add);
    return ret;
  }

  /**
   * Read the complete content of the passed stream and pass each line separately to the passed
   * callback.
   *
   * @param aIS
   *        The input stream to read from. May be <code>null</code>.
   * @param aCharset
   *        The character set to use. May not be <code>null</code>.
   * @param aLineCallback
   *        The callback that is invoked for all read lines. Each passed line does NOT contain the
   *        line delimiter!
   */
  public static void readStreamLines (@WillClose @Nullable final InputStream aIS,
                                      @Nonnull @Nonempty final Charset aCharset,
                                      @Nonnull final Consumer <? super String> aLineCallback)
  {
    if (aIS != null)
      readStreamLines (aIS, aCharset, 0, CGlobal.ILLEGAL_UINT, aLineCallback);
  }

  private static void _readFromReader (final int nLinesToSkip,
                                       final int nLinesToRead,
                                       @Nonnull final Consumer <? super String> aLineCallback,
                                       final boolean bReadAllLines,
                                       @Nonnull final NonBlockingBufferedReader aBR) throws IOException
  {
    // Skip all requested lines
    String sLine;
    for (int i = 0; i < nLinesToSkip; ++i)
    {
      sLine = aBR.readLine ();
      if (sLine == null)
        break;
    }

    if (bReadAllLines)
    {
      // Read all lines
      while (true)
      {
        sLine = aBR.readLine ();
        if (sLine == null)
          break;
        aLineCallback.accept (sLine);
      }
    }
    else
    {
      // Read only a certain amount of lines
      int nRead = 0;
      while (true)
      {
        sLine = aBR.readLine ();
        if (sLine == null)
          break;
        aLineCallback.accept (sLine);
        ++nRead;
        if (nRead >= nLinesToRead)
          break;
      }
    }
  }

  /**
   * Read the content of the passed stream line by line and invoking a callback on all matching
   * lines.
   *
   * @param aIS
   *        The input stream to read from. May be <code>null</code>.
   * @param aCharset
   *        The character set to use. May not be <code>null</code>.
   * @param nLinesToSkip
   *        The 0-based index of the first line to read. Pass in 0 to indicate to read everything.
   * @param nLinesToRead
   *        The number of lines to read. Pass in {@link CGlobal#ILLEGAL_UINT} to indicate that all
   *        lines should be read. If the number passed here exceeds the number of lines in the file,
   *        nothing happens.
   * @param aLineCallback
   *        The callback that is invoked for all read lines. Each passed line does NOT contain the
   *        line delimiter! Note: it is not invoked for skipped lines!
   */
  public static void readStreamLines (@WillClose @Nullable final InputStream aIS,
                                      @Nonnull @Nonempty final Charset aCharset,
                                      @Nonnegative final int nLinesToSkip,
                                      final int nLinesToRead,
                                      @Nonnull final Consumer <? super String> aLineCallback)
  {
    try
    {
      ValueEnforcer.notNull (aCharset, "Charset");
      ValueEnforcer.isGE0 (nLinesToSkip, "LinesToSkip");
      final boolean bReadAllLines = nLinesToRead == CGlobal.ILLEGAL_UINT;
      ValueEnforcer.isTrue (bReadAllLines || nLinesToRead >= 0,
                            () -> "Line count may not be that negative: " + nLinesToRead);
      ValueEnforcer.notNull (aLineCallback, "LineCallback");

      // Start the action only if there is something to read
      if (aIS != null)
        if (bReadAllLines || nLinesToRead > 0)
        {
          try (final NonBlockingBufferedReader aBR = new NonBlockingBufferedReader (createReader (aIS, aCharset)))
          {
            // read with the passed charset
            _readFromReader (nLinesToSkip, nLinesToRead, aLineCallback, bReadAllLines, aBR);
          }
          catch (final IOException ex)
          {
            LOGGER.error ("Failed to read from reader", internalGetPropagatableException (ex));
          }
        }
    }
    finally
    {
      // Close input stream anyway
      close (aIS);
    }
  }

  /**
   * Write bytes to an {@link OutputStream}.
   *
   * @param aOS
   *        The output stream to write to. May not be <code>null</code>. Is closed independent of
   *        error or success.
   * @param aBuf
   *        The byte array from which is to be written. May not be <code>null</code>.
   * @param nOfs
   *        The 0-based index to the first byte in the array to be written. May not be &lt; 0.
   * @param nLen
   *        The non-negative amount of bytes to be written. May not be &lt; 0.
   * @return {@link ESuccess}
   */
  @Nonnull
  public static ESuccess writeStream (@WillClose @Nonnull final OutputStream aOS,
                                      @Nonnull final byte [] aBuf,
                                      @Nonnegative final int nOfs,
                                      @Nonnegative final int nLen)
  {
    try
    {
      ValueEnforcer.notNull (aOS, "OutputStream");
      ValueEnforcer.isArrayOfsLen (aBuf, nOfs, nLen);

      aOS.write (aBuf, nOfs, nLen);
      aOS.flush ();
      return ESuccess.SUCCESS;
    }
    catch (final IOException ex)
    {
      LOGGER.error ("Failed to write to output stream", internalGetPropagatableException (ex));
      return ESuccess.FAILURE;
    }
    finally
    {
      close (aOS);
    }
  }

  /**
   * Write bytes to an {@link OutputStream}.
   *
   * @param aOS
   *        The output stream to write to. May not be <code>null</code>. Is closed independent of
   *        error or success.
   * @param aBuf
   *        The byte array to be written. May not be <code>null</code>.
   * @return {@link ESuccess}
   */
  @Nonnull
  public static ESuccess writeStream (@WillClose @Nonnull final OutputStream aOS, @Nonnull final byte [] aBuf)
  {
    return writeStream (aOS, aBuf, 0, aBuf.length);
  }

  /**
   * Write bytes to an {@link OutputStream}.
   *
   * @param aOS
   *        The output stream to write to. May not be <code>null</code>. Is closed independent of
   *        error or success.
   * @param sContent
   *        The string to be written. May not be <code>null</code>.
   * @param aCharset
   *        The charset to be used, to convert the String to a byte array.
   * @return {@link ESuccess}
   */
  @Nonnull
  public static ESuccess writeStream (@WillClose @Nonnull final OutputStream aOS,
                                      @Nonnull final String sContent,
                                      @Nonnull final Charset aCharset)
  {
    ValueEnforcer.notNull (sContent, "Content");
    ValueEnforcer.notNull (aCharset, "Charset");

    return writeStream (aOS, sContent.getBytes (aCharset));
  }

  /**
   * Fully skip the passed amounts in the input stream. Only forward skipping is possible!
   *
   * @param aIS
   *        The input stream to skip in.
   * @param nBytesToSkip
   *        The number of bytes to skip. Must be &ge; 0.
   * @throws IOException
   *         In case something goes wrong internally
   */
  public static void skipFully (@Nonnull final InputStream aIS, @Nonnegative final long nBytesToSkip) throws IOException
  {
    ValueEnforcer.notNull (aIS, "InputStream");
    ValueEnforcer.isGE0 (nBytesToSkip, "BytesToSkip");

    long nRemaining = nBytesToSkip;
    while (nRemaining > 0)
    {
      // May only return a partial skip
      final long nSkipped = aIS.skip (nRemaining);
      if (nSkipped == 0)
      {
        // Check if we're at the end of the file or not
        // -> blocking read!
        if (aIS.read () == -1)
        {
          throw new EOFException ("Failed to skip a total of " +
                                  nBytesToSkip +
                                  " bytes on input stream. Only skipped " +
                                  (nBytesToSkip - nRemaining) +
                                  " bytes so far!");
        }
        nRemaining--;
      }
      else
      {
        // Skipped at least one char
        nRemaining -= nSkipped;
      }
    }
  }

  /**
   * Read the whole buffer from the input stream.
   *
   * @param aIS
   *        The input stream to read from. May not be <code>null</code>.
   * @param aBuffer
   *        The buffer to write to. May not be <code>null</code>. Must be &ge; than the content to
   *        be read.
   * @return The number of read bytes
   * @throws IOException
   *         In case reading fails
   */
  @Nonnegative
  public static int readFully (@Nonnull final InputStream aIS, @Nonnull final byte [] aBuffer) throws IOException
  {
    return readFully (aIS, aBuffer, 0, aBuffer.length);
  }

  /**
   * Read the whole buffer from the input stream.
   *
   * @param aIS
   *        The input stream to read from. May not be <code>null</code>.
   * @param aBuffer
   *        The buffer to write to. May not be <code>null</code>. Must be &ge; than the content to
   *        be read.
   * @param nOfs
   *        The offset into the destination buffer to use. May not be &lt; 0.
   * @param nLen
   *        The number of bytes to read into the destination buffer to use. May not be &lt; 0.
   * @return The number of read bytes
   * @throws IOException
   *         In case reading fails
   */
  @Nonnegative
  public static int readFully (@Nonnull @WillNotClose final InputStream aIS,
                               @Nonnull final byte [] aBuffer,
                               @Nonnegative final int nOfs,
                               @Nonnegative final int nLen) throws IOException
  {
    ValueEnforcer.notNull (aIS, "InputStream");
    ValueEnforcer.isArrayOfsLen (aBuffer, nOfs, nLen);

    int nTotalBytesRead = 0;
    while (nTotalBytesRead < nLen)
    {
      // Potentially blocking read
      final int nBytesRead = aIS.read (aBuffer, nOfs + nTotalBytesRead, nLen - nTotalBytesRead);
      if (nBytesRead < 0)
        throw new EOFException ("Failed to read a total of " +
                                nLen +
                                " bytes from input stream. Only read " +
                                nTotalBytesRead +
                                " bytes so far.");
      nTotalBytesRead += nBytesRead;
    }
    return nTotalBytesRead;
  }

  private static void _readUntilEOF (@Nonnull @WillNotClose final InputStream aIS,
                                     @Nonnull final byte [] aBuffer,
                                     @Nonnull final ObjIntConsumer <? super byte []> aConsumer) throws IOException
  {
    ValueEnforcer.notNull (aIS, "InputStream");
    ValueEnforcer.notNull (aBuffer, "Buffer");
    ValueEnforcer.notNull (aConsumer, "Consumer");

    int nBytesRead;
    // Potentially blocking read
    while ((nBytesRead = aIS.read (aBuffer, 0, aBuffer.length)) > -1)
      aConsumer.accept (aBuffer, nBytesRead);
  }

  public static void readUntilEOF (@Nonnull @WillClose final InputStream aIS,
                                   @Nonnull final ObjIntConsumer <? super byte []> aConsumer) throws IOException
  {
    readUntilEOF (aIS, createDefaultCopyBufferBytes (), aConsumer);
  }

  public static void readUntilEOF (@Nonnull @WillClose final InputStream aIS,
                                   @Nonnull final byte [] aBuffer,
                                   @Nonnull final ObjIntConsumer <? super byte []> aConsumer) throws IOException
  {
    try
    {
      ValueEnforcer.notNull (aIS, "InputStream");
      ValueEnforcer.notNull (aBuffer, "Buffer");
      ValueEnforcer.notNull (aConsumer, "Consumer");

      _readUntilEOF (aIS, aBuffer, aConsumer);
    }
    finally
    {
      close (aIS);
    }
  }

  private static void _readUntilEOF (@Nonnull @WillNotClose final Reader aReader,
                                     @Nonnull final char [] aBuffer,
                                     @Nonnull final ObjIntConsumer <? super char []> aConsumer) throws IOException
  {
    ValueEnforcer.notNull (aReader, "Reader");
    ValueEnforcer.notNull (aBuffer, "Buffer");
    ValueEnforcer.notNull (aConsumer, "Consumer");

    int nCharsRead;
    // Potentially blocking read
    while ((nCharsRead = aReader.read (aBuffer, 0, aBuffer.length)) > -1)
      aConsumer.accept (aBuffer, nCharsRead);
  }

  public static void readUntilEOF (@Nonnull @WillClose final Reader aReader,
                                   @Nonnull final ObjIntConsumer <? super char []> aConsumer) throws IOException
  {
    readUntilEOF (aReader, new char [DEFAULT_BUFSIZE], aConsumer);
  }

  public static void readUntilEOF (@Nonnull @WillClose final Reader aReader,
                                   @Nonnull final char [] aBuffer,
                                   @Nonnull final ObjIntConsumer <? super char []> aConsumer) throws IOException
  {
    try
    {
      ValueEnforcer.notNull (aReader, "Reader");
      ValueEnforcer.notNull (aBuffer, "Buffer");
      ValueEnforcer.notNull (aConsumer, "Consumer");

      _readUntilEOF (aReader, aBuffer, aConsumer);
    }
    finally
    {
      close (aReader);
    }
  }

  @Nullable
  public static InputStream checkForInvalidFilterInputStream (@Nullable final InputStream aIS)
  {
    if (aIS != null)
      try
      {
        /*
         * This will fail if IS is a FilterInputStream with a <code>null</code> contained
         * InputStream. This happens e.g. when a JAR URL with a directory that does not end with a
         * slash is returned.
         */
        aIS.markSupported ();
      }
      catch (final NullPointerException ex)
      {
        // An InputStream for a directory was retrieved - ignore
        close (aIS);
        return null;
      }
    return aIS;
  }

  public static final int END_OF_STRING_MARKER = 0xfffdfffd;

  /**
   * Because {@link DataOutputStream#writeUTF(String)} has a limit of 64KB this methods provides a
   * similar solution but simply writing the bytes.
   *
   * @param aDO
   *        {@link DataOutput} to write to. May not be <code>null</code>.
   * @param sStr
   *        The string to be written. May be <code>null</code>.
   * @throws IOException
   *         on write error
   * @see #readSafeUTF(DataInput)
   */
  public static void writeSafeUTF (@Nonnull final DataOutput aDO, @Nullable final String sStr) throws IOException
  {
    ValueEnforcer.notNull (aDO, "DataOutput");
    if (sStr == null)
    {
      // Only write a 0 byte
      // Writing a length of 0 would mean that the differentiation between
      // "null" and
      // empty string would be lost.
      aDO.writeByte (0);
    }
    else
    {
      // Non-null indicator; basically the version of layout how the data was
      // written
      aDO.writeByte (2);

      final byte [] aUTF8Bytes = sStr.getBytes (StandardCharsets.UTF_8);

      // Write number of bytes
      aDO.writeInt (aUTF8Bytes.length);
      // Write main bytes
      aDO.write (aUTF8Bytes);

      // Was added in layout version 2:
      aDO.writeInt (END_OF_STRING_MARKER);
    }
  }

  /**
   * Because {@link DataOutputStream#writeUTF(String)} has a limit of 64KB this methods provides a
   * similar solution for reading like {@link DataInputStream#readUTF()} but what was written in
   * {@link #writeSafeUTF(DataOutput, String)}.
   *
   * @param aDI
   *        {@link DataInput} to read from. May not be <code>null</code>.
   * @return The read string. May be <code>null</code>.
   * @throws IOException
   *         on read error
   * @see #writeSafeUTF(DataOutput, String)
   */
  @Nullable
  public static String readSafeUTF (@Nonnull final DataInput aDI) throws IOException
  {
    ValueEnforcer.notNull (aDI, "DataInput");

    final int nLayout = aDI.readByte ();
    final String ret;
    switch (nLayout)
    {
      case 0:
      {
        // If the first byte has value "0" it means the whole String is simply
        // null
        ret = null;
        break;
      }
      case 1:
      {
        // length in UTF-8 bytes followed by the main bytes
        final int nLength = aDI.readInt ();
        final byte [] aData = new byte [nLength];
        aDI.readFully (aData);
        ret = new String (aData, StandardCharsets.UTF_8);
        break;
      }
      case 2:
      {
        // length in UTF-8 bytes followed by the main bytes, than the end of
        // byte marker
        final int nLength = aDI.readInt ();
        final byte [] aData = new byte [nLength];
        aDI.readFully (aData);
        ret = new String (aData, StandardCharsets.UTF_8);

        final int nEndOfString = aDI.readInt ();
        if (nEndOfString != END_OF_STRING_MARKER)
          throw new IOException ("Missing end of String marker");
        break;
      }
      default:
        throw new IOException ("Unsupported string layout version " + nLayout);
    }

    return ret;
  }
}
