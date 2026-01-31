/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.function.Consumer;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.CheckForSigned;
import com.helger.annotation.Nonempty;
import com.helger.annotation.Nonnegative;
import com.helger.annotation.WillClose;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.CGlobal;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.io.iface.IHasInputStream;
import com.helger.base.io.nonblocking.NonBlockingBufferedReader;
import com.helger.base.io.stream.StreamHelper;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;

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
                                                       @NonNull final Charset aCharset)
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
                                                       @NonNull final Charset aCharset,
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
                                                       @NonNull @Nonempty final Charset aCharset)
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
                                      @NonNull final Charset aCharset,
                                      @NonNull final List <String> aTargetList)
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
                                                       @NonNull final Charset aCharset,
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
                                      @NonNull @Nonempty final Charset aCharset,
                                      @NonNull final Consumer <? super String> aLineCallback)
  {
    if (aIS != null)
      readStreamLines (aIS, aCharset, 0, CGlobal.ILLEGAL_UINT, aLineCallback);
  }

  private static void _readFromReader (final int nLinesToSkip,
                                       final int nLinesToRead,
                                       @NonNull final Consumer <? super String> aLineCallback,
                                       final boolean bReadAllLines,
                                       @NonNull final NonBlockingBufferedReader aBR) throws IOException
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
                                      @NonNull @Nonempty final Charset aCharset,
                                      @Nonnegative final int nLinesToSkip,
                                      final int nLinesToRead,
                                      @NonNull final Consumer <? super String> aLineCallback)
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
}
