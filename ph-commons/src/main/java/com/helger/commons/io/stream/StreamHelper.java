/**
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
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
package com.helger.commons.io.stream;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.ObjIntConsumer;

import javax.annotation.CheckForSigned;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillClose;
import javax.annotation.WillNotClose;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.CGlobal;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.builder.IBuilder;
import com.helger.commons.callback.exception.IExceptionCallback;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.exception.mock.IMockException;
import com.helger.commons.io.IHasInputStream;
import com.helger.commons.mutable.MutableLong;
import com.helger.commons.state.ESuccess;
import com.helger.commons.statistics.IMutableStatisticsHandlerSize;
import com.helger.commons.statistics.StatisticsManager;

/**
 * Some very basic IO stream utility stuff. All input stream (=reading) related
 * stuff is quite <code>null</code> aware, where on writing an output stream may
 * never be null.
 *
 * @author Philip Helger
 */
@Immutable
public final class StreamHelper
{
  /** buffer size for copy operations */
  public static final int DEFAULT_BUFSIZE = 16 * CGlobal.BYTES_PER_KILOBYTE;

  /** The logger to use. */
  private static final Logger LOGGER = LoggerFactory.getLogger (StreamHelper.class);

  private static final IMutableStatisticsHandlerSize STATS_COPY_BYTES = StatisticsManager.getSizeHandler (StreamHelper.class.getName () +
                                                                                                          "$COPY");
  private static final IMutableStatisticsHandlerSize STATS_COPY_CHARS = StatisticsManager.getSizeHandler (StreamHelper.class.getName () +
                                                                                                          "$COPYCHARS");
  @PresentForCodeCoverage
  private static final StreamHelper INSTANCE = new StreamHelper ();

  private StreamHelper ()
  {}

  /**
   * Check if the passed exception is a known EOF exception.
   *
   * @param t
   *        The throwable/exception to be checked. May be <code>null</code>.
   * @return <code>true</code> if it is a user-created EOF exception
   */
  public static boolean isKnownEOFException (@Nullable final Throwable t)
  {
    return t != null && isKnownEOFException (t.getClass ());
  }

  /**
   * Check if the passed class is a known EOF exception class.
   *
   * @param aClass
   *        The class to be checked. May be <code>null</code>.
   * @return <code>true</code> if it is a known EOF exception class.
   */
  public static boolean isKnownEOFException (@Nullable final Class <?> aClass)
  {
    if (aClass == null)
      return false;

    final String sClass = aClass.getName ();
    return sClass.equals ("java.io.EOFException") ||
           sClass.equals ("org.mortbay.jetty.EofException") ||
           sClass.equals ("org.eclipse.jetty.io.EofException") ||
           sClass.equals ("org.apache.catalina.connector.ClientAbortException");
  }

  @Nullable
  private static Exception _propagate (@Nonnull final Exception ex)
  {
    return ex instanceof IMockException ? null : ex;
  }

  /**
   * Close the passed object, without trying to call flush on it.
   *
   * @param aCloseable
   *        The object to be closed. May be <code>null</code>.
   * @return {@link ESuccess#SUCCESS} if the object was successfully closed.
   */
  @Nonnull
  public static ESuccess closeWithoutFlush (@Nullable @WillClose final AutoCloseable aCloseable)
  {
    if (aCloseable != null)
    {
      try
      {
        // close stream
        aCloseable.close ();
        return ESuccess.SUCCESS;
      }
      catch (final Exception ex)
      {
        if (!isKnownEOFException (ex))
          LOGGER.error ("Failed to close object " + aCloseable.getClass ().getName (), _propagate (ex));
      }
    }
    return ESuccess.FAILURE;
  }

  /**
   * Close the passed stream by encapsulating the declared {@link IOException}.
   * If the passed object also implements the {@link Flushable} interface, it is
   * tried to be flushed before it is closed.
   *
   * @param aCloseable
   *        The object to be closed. May be <code>null</code>.
   * @return {@link ESuccess} if the object was successfully closed.
   */
  @Nonnull
  public static ESuccess close (@Nullable @WillClose final AutoCloseable aCloseable)
  {
    if (aCloseable != null)
    {
      try
      {
        // flush object (if available)
        if (aCloseable instanceof Flushable)
          flush ((Flushable) aCloseable);

        // close object
        aCloseable.close ();
        return ESuccess.SUCCESS;
      }
      catch (final NullPointerException ex)
      {
        // Happens if a java.io.FilterInputStream or java.io.FilterOutputStream
        // has no underlying stream!
      }
      catch (final Exception ex)
      {
        if (!isKnownEOFException (ex))
          LOGGER.error ("Failed to close object " + aCloseable.getClass ().getName (), _propagate (ex));
      }
    }

    return ESuccess.FAILURE;
  }

  /**
   * Flush the passed object encapsulating the declared {@link IOException}.
   *
   * @param aFlushable
   *        The flushable to be flushed. May be <code>null</code>.
   * @return {@link ESuccess#SUCCESS} if the object was successfully flushed.
   */
  @Nonnull
  public static ESuccess flush (@Nullable final Flushable aFlushable)
  {
    if (aFlushable != null)
      try
      {
        aFlushable.flush ();
        return ESuccess.SUCCESS;
      }
      catch (final NullPointerException ex)
      {
        // Happens if a java.io.FilterOutputStream is already closed!
      }
      catch (final IOException ex)
      {
        if (!isKnownEOFException (ex))
          LOGGER.error ("Failed to flush object " + aFlushable.getClass ().getName (), _propagate (ex));
      }
    return ESuccess.FAILURE;
  }

  /**
   * @return A newly created copy buffer using {@link #DEFAULT_BUFSIZE}. Never
   *         <code>null</code>.
   * @since 9.3.6
   */
  @Nonnull
  @ReturnsMutableCopy
  public static byte [] createDefaultCopyBufferBytes ()
  {
    return new byte [DEFAULT_BUFSIZE];
  }

  /**
   * Pass the content of the given input stream to the given output stream. The
   * input stream is automatically closed, whereas the output stream stays open!
   *
   * @param aIS
   *        The input stream to read from. May be <code>null</code>.
   *        Automatically closed!
   * @param aOS
   *        The output stream to write to. May be <code>null</code>. Not
   *        automatically closed!
   * @return <code>{@link ESuccess#SUCCESS}</code> if copying took place, <code>
   *         {@link ESuccess#FAILURE}</code> otherwise
   */
  @Nonnull
  public static ESuccess copyInputStreamToOutputStream (@WillClose @Nullable final InputStream aIS,
                                                        @WillNotClose @Nullable final OutputStream aOS)
  {
    return copyByteStream ().from (aIS).closeFrom (true).to (aOS).closeTo (false).build ();
  }

  /**
   * Pass the content of the given input stream to the given output stream. Both
   * the input stream and the output stream are automatically closed.
   *
   * @param aIS
   *        The input stream to read from. May be <code>null</code>.
   *        Automatically closed!
   * @param aOS
   *        The output stream to write to. May be <code>null</code>.
   *        Automatically closed!
   * @return <code>{@link ESuccess#SUCCESS}</code> if copying took place, <code>
   *         {@link ESuccess#FAILURE}</code> otherwise
   */
  @Nonnull
  public static ESuccess copyInputStreamToOutputStreamAndCloseOS (@WillClose @Nullable final InputStream aIS,
                                                                  @WillClose @Nullable final OutputStream aOS)
  {
    return copyByteStream ().from (aIS).closeFrom (true).to (aOS).closeTo (true).build ();
  }

  /**
   * Pass the content of the given input stream to the given output stream. The
   * input stream is automatically closed, whereas the output stream stays open!
   *
   * @param aIS
   *        The input stream to read from. May be <code>null</code>.
   *        Automatically closed!
   * @param aOS
   *        The output stream to write to. May be <code>null</code>. Not
   *        automatically closed!
   * @param nLimit
   *        The maximum number of bytes to be copied to the output stream. Must
   *        be &ge; 0.
   * @return <code>{@link ESuccess#SUCCESS}</code> if copying took place, <code>
   *         {@link ESuccess#FAILURE}</code> otherwise
   * @deprecated Since v10.0. Use the builder instead
   */
  @Nonnull
  @Deprecated
  public static ESuccess copyInputStreamToOutputStreamWithLimit (@WillClose @Nullable final InputStream aIS,
                                                                 @WillNotClose @Nullable final OutputStream aOS,
                                                                 @Nonnegative final long nLimit)
  {
    return copyByteStream ().from (aIS).closeFrom (true).to (aOS).closeTo (false).limit (nLimit).build ();
  }

  /**
   * Pass the content of the given input stream to the given output stream. Both
   * the input stream and the output stream are automatically closed.
   *
   * @param aIS
   *        The input stream to read from. May be <code>null</code>.
   *        Automatically closed!
   * @param aOS
   *        The output stream to write to. May be <code>null</code>.
   *        Automatically closed!
   * @param nLimit
   *        The maximum number of bytes to be copied to the output stream. Must
   *        be &ge; 0.
   * @return <code>{@link ESuccess#SUCCESS}</code> if copying took place, <code>
   *         {@link ESuccess#FAILURE}</code> otherwise
   * @deprecated Since v10.0. Use the builder instead
   */
  @Deprecated
  @Nonnull
  public static ESuccess copyInputStreamToOutputStreamWithLimitAndCloseOS (@WillClose @Nullable final InputStream aIS,
                                                                           @WillClose @Nullable final OutputStream aOS,
                                                                           @Nonnegative final long nLimit)
  {
    return copyByteStream ().from (aIS).closeFrom (true).to (aOS).closeTo (true).limit (nLimit).build ();
  }

  /**
   * Pass the content of the given input stream to the given output stream. The
   * input stream is automatically closed, whereas the output stream stays open!
   *
   * @param aIS
   *        The input stream to read from. May be <code>null</code>.
   *        Automatically closed!
   * @param aOS
   *        The output stream to write to. May be <code>null</code>. Not
   *        automatically closed!
   * @param aCopyByteCount
   *        An optional mutable long object that will receive the total number
   *        of copied bytes. Note: and optional old value is overwritten!
   * @return <code>{@link ESuccess#SUCCESS}</code> if copying took place, <code>
   *         {@link ESuccess#FAILURE}</code> otherwise
   * @deprecated Since v10.0. Use the builder instead
   */
  @Deprecated
  @Nonnull
  public static ESuccess copyInputStreamToOutputStream (@WillClose @Nullable final InputStream aIS,
                                                        @WillNotClose @Nullable final OutputStream aOS,
                                                        @Nullable final MutableLong aCopyByteCount)
  {
    return copyByteStream ().from (aIS)
                            .closeFrom (true)
                            .to (aOS)
                            .closeTo (false)
                            .copyByteCount (aCopyByteCount)
                            .build ();
  }

  /**
   * Pass the content of the given input stream to the given output stream. The
   * input stream is automatically closed, whereas the output stream stays open!
   *
   * @param aIS
   *        The input stream to read from. May be <code>null</code>.
   *        Automatically closed!
   * @param aOS
   *        The output stream to write to. May be <code>null</code>. Not
   *        automatically closed!
   * @param aBuffer
   *        The buffer to use. May not be <code>null</code>.
   * @return <code>{@link ESuccess#SUCCESS}</code> if copying took place, <code>
   *         {@link ESuccess#FAILURE}</code> otherwise
   * @deprecated Since v10.0. Use the builder instead
   */
  @Deprecated
  @Nonnull
  public static ESuccess copyInputStreamToOutputStream (@WillClose @Nullable final InputStream aIS,
                                                        @WillNotClose @Nullable final OutputStream aOS,
                                                        @Nonnull final byte [] aBuffer)
  {
    return copyByteStream ().from (aIS).closeFrom (true).to (aOS).closeTo (false).buffer (aBuffer).build ();
  }

  /**
   * Pass the content of the given input stream to the given output stream. The
   * input stream is automatically closed, whereas the output stream stays open!
   *
   * @param aIS
   *        The input stream to read from. May be <code>null</code>.
   * @param bCloseIS
   *        <code>true</code> to close the InputStream, <code>false</code> to
   *        leave it open.
   * @param aOS
   *        The output stream to write to. May be <code>null</code>.
   * @param bCloseOS
   *        <code>true</code> to close the OutputStream, <code>false</code> to
   *        leave it open.
   * @param aBuffer
   *        The buffer to use. May not be <code>null</code>.
   * @param aLimit
   *        An optional maximum number of bytes to copied from the input stream
   *        to the output stream. May be <code>null</code> to indicate no limit,
   *        meaning all bytes are copied.
   * @param aExceptionCallback
   *        The Exception callback to be invoked, if an exception occurs. May be
   *        <code>null</code>.
   * @param aCopyByteCount
   *        An optional mutable long object that will receive the total number
   *        of copied bytes. Note: and optional old value is overwritten. Note:
   *        this is only called, if copying was successful, and not in case of
   *        an exception.
   * @return <code>{@link ESuccess#SUCCESS}</code> if copying took place, <code>
   *         {@link ESuccess#FAILURE}</code> otherwise
   * @since 9.3.6
   * @deprecated Since v10.0. Use the builder instead
   */
  @Deprecated
  @Nonnull
  public static ESuccess copyInputStreamToOutputStream (@Nullable final InputStream aIS,
                                                        final boolean bCloseIS,
                                                        @Nullable final OutputStream aOS,
                                                        final boolean bCloseOS,
                                                        @Nonnull @Nonempty final byte [] aBuffer,
                                                        @Nullable final Long aLimit,
                                                        @Nullable final IExceptionCallback <IOException> aExceptionCallback,
                                                        @Nullable final MutableLong aCopyByteCount)
  {
    return copyByteStream ().from (aIS)
                            .closeFrom (bCloseIS)
                            .to (aOS)
                            .closeTo (bCloseOS)
                            .buffer (aBuffer)
                            .limit (aLimit == null ? -1 : aLimit.longValue ())
                            .exceptionCallback (aExceptionCallback)
                            .copyByteCount (aCopyByteCount)
                            .build ();
  }

  /**
   * @return A new {@link CopyByteStreamBuilder}. Never <code>null</code>.
   */
  @Nonnull
  public static CopyByteStreamBuilder copyByteStream ()
  {
    return new CopyByteStreamBuilder ();
  }

  /**
   * A simple builder to copy an InputStream ({@link #from(InputStream)}) to an
   * OutputStream ({@link #to(OutputStream)}) with certain parameters. Call
   * {@link #build()} to execute the copying.
   *
   * @author Philip Helger
   * @since 10.0.0
   */
  public static class CopyByteStreamBuilder implements IBuilder <ESuccess>
  {
    public static final boolean DEFAULT_CLOSE_SOURCE = false;
    public static final boolean DEFAULT_CLOSE_DESTINATION = false;

    private InputStream m_aIS;
    private boolean m_bCloseIS = DEFAULT_CLOSE_SOURCE;
    private OutputStream m_aOS;
    private boolean m_bCloseOS = DEFAULT_CLOSE_DESTINATION;
    private byte [] m_aBuffer;
    private long m_nLimit = CGlobal.ILLEGAL_ULONG;
    private IExceptionCallback <IOException> m_aExceptionCallback;
    private MutableLong m_aCopyByteCount;

    /**
     * @param a
     *        The InputStream to read from. May be <code>null</code>.
     * @return this for chaining
     */
    @Nonnull
    public CopyByteStreamBuilder from (@Nullable final InputStream a)
    {
      m_aIS = a;
      return this;
    }

    /**
     * @param b
     *        <code>true</code> to close the InputStream, <code>false</code> to
     *        leave it open. Default is {@link #DEFAULT_CLOSE_SOURCE}
     * @return this for chaining
     */
    @Nonnull
    public CopyByteStreamBuilder closeFrom (final boolean b)
    {
      m_bCloseIS = b;
      return this;
    }

    /**
     * @param a
     *        The OutputStream to write to. May be <code>null</code>.
     * @return this for chaining
     */
    @Nonnull
    public CopyByteStreamBuilder to (@Nullable final OutputStream a)
    {
      m_aOS = a;
      return this;
    }

    /**
     * @param b
     *        <code>true</code> to close the OutputStream, <code>false</code> to
     *        leave it open.
     * @return this for chaining
     */
    @Nonnull
    public CopyByteStreamBuilder closeTo (final boolean b)
    {
      m_bCloseOS = b;
      return this;
    }

    /**
     * @param a
     *        The buffer to use. May be <code>null</code>.
     * @return this for chaining
     */
    @Nonnull
    public CopyByteStreamBuilder buffer (@Nullable final byte [] a)
    {
      m_aBuffer = a;
      return this;
    }

    /**
     * @param n
     *        An optional maximum number of bytes to copied from the InputStream
     *        to the OutputStream. May be &lt; 0 to indicate no limit, meaning
     *        all bytes are copied.
     * @return this for chaining
     * @see #unlimited()
     */
    @Nonnull
    public CopyByteStreamBuilder limit (final long n)
    {
      m_nLimit = n;
      return this;
    }

    /**
     * @param a
     *        An optional maximum number of bytes to copied from the InputStream
     *        to the OutputStream. May be &lt; 0 to indicate no limit, meaning
     *        all bytes are copied. If <code>null</code> no limit is set
     * @return this for chaining
     * @since 10.1.0
     * @see #unlimited()
     */
    @Nonnull
    public CopyByteStreamBuilder limit (@Nullable final Long a)
    {
      return a == null ? unlimited () : limit (a.longValue ());
    }

    /**
     * Ensure no limit in copying (which is also the default).
     *
     * @return this for chaining
     * @see #limit(long)
     */
    @Nonnull
    public CopyByteStreamBuilder unlimited ()
    {
      return limit (CGlobal.ILLEGAL_ULONG);
    }

    /**
     * @param a
     *        The Exception callback to be invoked, if an exception occurs. May
     *        be <code>null</code>.
     * @return this for chaining
     */
    @Nonnull
    public CopyByteStreamBuilder exceptionCallback (@Nullable final IExceptionCallback <IOException> a)
    {
      m_aExceptionCallback = a;
      return this;
    }

    /**
     * @param a
     *        An optional mutable long object that will receive the total number
     *        of copied bytes. Note: and optional old value is overwritten.
     *        Note: this is only called, if copying was successful, and not in
     *        case of an exception.
     * @return this for chaining
     */
    @Nonnull
    public CopyByteStreamBuilder copyByteCount (@Nullable final MutableLong a)
    {
      m_aCopyByteCount = a;
      return this;
    }

    @Nonnegative
    private static long _copyInputStreamToOutputStream (@Nonnull @WillNotClose final InputStream aIS,
                                                        @Nonnull @WillNotClose final OutputStream aOS,
                                                        @Nonnull final byte [] aBuffer) throws IOException
    {
      final int nBufferLength = aBuffer.length;
      long nTotalBytesWritten = 0;
      int nBytesRead;
      // Potentially blocking read
      while ((nBytesRead = aIS.read (aBuffer, 0, nBufferLength)) > -1)
      {
        aOS.write (aBuffer, 0, nBytesRead);
        nTotalBytesWritten += nBytesRead;
      }
      return nTotalBytesWritten;
    }

    @Nonnegative
    private static long _copyInputStreamToOutputStreamWithLimit (@Nonnull @WillNotClose final InputStream aIS,
                                                                 @Nonnull @WillNotClose final OutputStream aOS,
                                                                 @Nonnull final byte [] aBuffer,
                                                                 @Nonnegative final long nLimit) throws IOException
    {
      final int nBufferLength = aBuffer.length;
      long nRest = nLimit;
      long nTotalBytesWritten = 0;
      while (true)
      {
        // if nRest is smaller than aBuffer.length, which is an int, it is safe
        // to
        // cast nRest also to an int!
        final int nBytesToRead = nRest >= nBufferLength ? nBufferLength : (int) nRest;
        if (nBytesToRead == 0)
          break;
        // Potentially blocking read
        final int nBytesRead = aIS.read (aBuffer, 0, nBytesToRead);
        if (nBytesRead == -1)
        {
          // EOF
          break;
        }
        if (nBytesRead > 0)
        {
          // At least one byte read
          aOS.write (aBuffer, 0, nBytesRead);
          nTotalBytesWritten += nBytesRead;
          nRest -= nBytesRead;
        }
      }
      return nTotalBytesWritten;
    }

    /**
     * This method performs the main copying
     */
    @Nonnull
    public ESuccess build ()
    {
      try
      {
        if (m_aIS == null)
        {
          if (LOGGER.isDebugEnabled ())
            LOGGER.debug ("The source InputStream is not set - hence no copying is possible");
          return ESuccess.FAILURE;
        }
        if (m_aOS == null)
        {
          if (LOGGER.isDebugEnabled ())
            LOGGER.debug ("The target OutputStream is not set - hence no copying is possible");
          return ESuccess.FAILURE;
        }

        final byte [] aBuffer = m_aBuffer != null && m_aBuffer.length > 0 ? m_aBuffer : createDefaultCopyBufferBytes ();

        // both streams are not null
        final long nTotalBytesCopied;
        if (m_nLimit < 0)
          nTotalBytesCopied = _copyInputStreamToOutputStream (m_aIS, m_aOS, aBuffer);
        else
          nTotalBytesCopied = _copyInputStreamToOutputStreamWithLimit (m_aIS, m_aOS, aBuffer, m_nLimit);

        // Add to statistics
        STATS_COPY_BYTES.addSize (nTotalBytesCopied);

        // Remember copied bytes?
        if (m_aCopyByteCount != null)
          m_aCopyByteCount.set (nTotalBytesCopied);
        return ESuccess.SUCCESS;
      }
      catch (final IOException ex)
      {
        if (m_aExceptionCallback != null)
          m_aExceptionCallback.onException (ex);
        else
          if (!isKnownEOFException (ex))
            LOGGER.error ("Failed to copy from InputStream to OutputStream", _propagate (ex));
      }
      finally
      {
        // Ensure streams are closed under all circumstances
        if (m_bCloseIS)
          close (m_aIS);
        if (m_bCloseOS)
          close (m_aOS);
      }
      return ESuccess.FAILURE;
    }
  }

  /**
   * Get the number of available bytes in the passed input stream.
   *
   * @param aIS
   *        The input stream to use. May be <code>null</code>.
   * @return 0 in case of an error or if the parameter was <code>null</code>.
   */
  public static int getAvailable (@Nullable final InputStream aIS)
  {
    if (aIS != null)
      try
      {
        return aIS.available ();
      }
      catch (final IOException ex)
      {
        // Fall through
      }
    return 0;
  }

  /**
   * Get a byte buffer with all the available content of the passed input
   * stream.
   *
   * @param aIS
   *        The source input stream. May not be <code>null</code>.
   * @return A new {@link NonBlockingByteArrayOutputStream} with all available
   *         content inside. The {@link OutputStream} must be closed by the
   *         caller since v10. Since v9.3.6 this method returns
   *         <code>null</code> if copying fails.
   */
  @Nullable
  public static NonBlockingByteArrayOutputStream getCopy (@Nonnull @WillClose final InputStream aIS)
  {
    final int nAvailable = Math.max (DEFAULT_BUFSIZE, getAvailable (aIS));
    final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream (nAvailable);
    if (copyByteStream ().from (aIS).closeFrom (true).to (aBAOS).closeTo (false).build ().isFailure ())
      return null;
    return aBAOS;
  }

  /**
   * Get a byte buffer with all the available content of the passed input
   * stream.
   *
   * @param aIS
   *        The source input stream. May not be <code>null</code>.
   * @param nLimit
   *        The maximum number of bytes to be copied to the output stream. Must
   *        be &ge; 0.
   * @return A new {@link NonBlockingByteArrayOutputStream} with all available
   *         content inside. The {@link OutputStream} must be closed by the
   *         caller since v10. Since v9.3.6 this method returns
   *         <code>null</code> if copying fails.
   */
  @Nullable
  public static NonBlockingByteArrayOutputStream getCopyWithLimit (@Nonnull @WillClose final InputStream aIS,
                                                                   @Nonnegative final long nLimit)
  {
    final int nAvailable = Math.max (DEFAULT_BUFSIZE, getAvailable (aIS));
    final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream (nAvailable);
    if (copyByteStream ().from (aIS).closeFrom (true).to (aBAOS).closeTo (false).limit (nLimit).build ().isFailure ())
      return null;
    return aBAOS;
  }

  /**
   * Read all bytes from the passed input stream into a byte array.
   *
   * @param aISP
   *        The input stream provider to read from. May be <code>null</code> .
   * @return The byte array or <code>null</code> if the parameter or the
   *         resolved input stream is <code>null</code>.
   */
  @Nullable
  public static byte [] getAllBytes (@Nullable final IHasInputStream aISP)
  {
    if (aISP == null)
      return null;

    return getAllBytes (aISP.getInputStream ());
  }

  /**
   * Read all bytes from the passed input stream into a byte array.
   *
   * @param aIS
   *        The input stream to read from. May be <code>null</code>.
   * @return The byte array or <code>null</code> if the input stream is
   *         <code>null</code>.
   */
  @Nullable
  public static byte [] getAllBytes (@Nullable @WillClose final InputStream aIS)
  {
    if (aIS == null)
      return null;

    try (final NonBlockingByteArrayOutputStream aBAOS = getCopy (aIS))
    {
      if (aBAOS == null)
        return null;
      // No need to copy, because the BAOS goes out of scope anyway
      return aBAOS.getBufferOrCopy ();
    }
  }

  /**
   * Read all bytes from the passed input stream into a string.
   *
   * @param aISP
   *        The input stream provider to read from. May be <code>null</code> .
   * @param aCharset
   *        The charset to use. May not be <code>null</code> .
   * @return The String or <code>null</code> if the parameter or the resolved
   *         input stream is <code>null</code>.
   */
  @Nullable
  public static String getAllBytesAsString (@Nullable final IHasInputStream aISP,
                                            @Nonnull @Nonempty final Charset aCharset)
  {
    if (aISP == null)
      return null;

    return getAllBytesAsString (aISP.getInputStream (), aCharset);
  }

  /**
   * Read all bytes from the passed input stream into a string.
   *
   * @param aIS
   *        The input stream to read from. May be <code>null</code>.
   * @param aCharset
   *        The charset to use. May not be <code>null</code> .
   * @return The String or <code>null</code> if the input stream is
   *         <code>null</code>.
   */
  @Nullable
  public static String getAllBytesAsString (@Nullable @WillClose final InputStream aIS,
                                            @Nonnull @Nonempty final Charset aCharset)
  {
    ValueEnforcer.notNull (aCharset, "Charset");

    if (aIS == null)
      return null;

    try (final NonBlockingByteArrayOutputStream aBAOS = getCopy (aIS))
    {
      if (aBAOS == null)
        return null;

      return aBAOS.getAsString (aCharset);
    }
  }

  /**
   * Pass the content of the given reader to the given writer. The reader is
   * automatically closed, whereas the writer stays open!
   *
   * @param aReader
   *        The reader to read from. May be <code>null</code>.
   * @param bCloseReader
   *        <code>true</code> to close the reader, <code>false</code> to keep it
   *        open.
   * @param aWriter
   *        The writer to write to. May be <code>null</code>.
   * @param bCloseWriter
   *        <code>true</code> to close the writer, <code>false</code> to keep it
   *        open.
   * @param aBuffer
   *        The buffer to use. May not be <code>null</code>.
   * @param aLimit
   *        An optional maximum number of chars to copied from the reader to the
   *        writer. May be <code>null</code> to indicate no limit, meaning all
   *        chars are copied.
   * @param aExceptionCallback
   *        The Exception callback to be invoked, if an exception occurs. May
   *        not be <code>null</code>.
   * @param aCopyCharCount
   *        An optional mutable long object that will receive the total number
   *        of copied characters. Note: and optional old value is overwritten!
   * @return <code>{@link ESuccess#SUCCESS}</code> if copying took place, <code>
   *         {@link ESuccess#FAILURE}</code> otherwise
   * @deprecated Since v10.0. Use the builder instead
   */
  @Deprecated
  @Nonnull
  public static ESuccess copyReaderToWriter (@Nullable final Reader aReader,
                                             final boolean bCloseReader,
                                             @Nullable final Writer aWriter,
                                             final boolean bCloseWriter,
                                             @Nullable final char [] aBuffer,
                                             @Nullable final Long aLimit,
                                             @Nullable final IExceptionCallback <IOException> aExceptionCallback,
                                             @Nullable final MutableLong aCopyCharCount)
  {
    return copyCharStream ().from (aReader)
                            .closeFrom (bCloseReader)
                            .to (aWriter)
                            .closeTo (bCloseWriter)
                            .buffer (aBuffer)
                            .limit (aLimit == null ? -1 : aLimit.longValue ())
                            .exceptionCallback (aExceptionCallback)
                            .copyCharCount (aCopyCharCount)
                            .build ();
  }

  /**
   * @return A newly created copy buffer using {@link #DEFAULT_BUFSIZE}. Never
   *         <code>null</code>.
   * @since 9.3.6
   */
  @Nonnull
  @ReturnsMutableCopy
  public static char [] createDefaultCopyBufferChars ()
  {
    return new char [DEFAULT_BUFSIZE];
  }

  /**
   * Pass the content of the given reader to the given writer. The reader is
   * automatically closed, whereas the writer stays open!
   *
   * @param aReader
   *        The reader to read from. May be <code>null</code>. Automatically
   *        closed!
   * @param aWriter
   *        The writer to write to. May be <code>null</code>. Not automatically
   *        closed!
   * @param aBuffer
   *        The buffer to use. May not be <code>null</code>.
   * @param aCopyCharCount
   *        An optional mutable long object that will receive the total number
   *        of copied characters. Note: and optional old value is overwritten!
   * @param aLimit
   *        An optional maximum number of chars to copied from the reader to the
   *        writer. May be <code>null</code> to indicate no limit, meaning all
   *        chars are copied.
   * @return <code>{@link ESuccess#SUCCESS}</code> if copying took place, <code>
   *         {@link ESuccess#FAILURE}</code> otherwise
   * @deprecated Since v10.0. Use the builder instead
   */
  @Deprecated
  @Nonnull
  public static ESuccess copyReaderToWriter (@Nullable @WillClose final Reader aReader,
                                             @Nullable @WillNotClose final Writer aWriter,
                                             @Nonnull @Nonempty final char [] aBuffer,
                                             @Nullable final MutableLong aCopyCharCount,
                                             @Nullable final Long aLimit)
  {
    return copyCharStream ().from (aReader)
                            .closeFrom (true)
                            .to (aWriter)
                            .closeTo (false)
                            .buffer (aBuffer)
                            .limit (aLimit == null ? -1 : aLimit.longValue ())
                            .copyCharCount (aCopyCharCount)
                            .build ();
  }

  /**
   * Pass the content of the given reader to the given writer. The reader is
   * automatically closed, whereas the writer stays open!
   *
   * @param aReader
   *        The reader to read from. May be <code>null</code>. Automatically
   *        closed!
   * @param aWriter
   *        The writer to write to. May be <code>null</code>. Not automatically
   *        closed!
   * @return <code>{@link ESuccess#SUCCESS}</code> if copying took place, <code>
   *         {@link ESuccess#FAILURE}</code> otherwise
   */
  @Nonnull
  public static ESuccess copyReaderToWriter (@WillClose @Nullable final Reader aReader,
                                             @WillNotClose @Nullable final Writer aWriter)
  {
    return copyCharStream ().from (aReader).closeFrom (true).to (aWriter).closeTo (false).build ();
  }

  /**
   * Pass the content of the given reader to the given writer. The reader and
   * the writer are automatically closed!
   *
   * @param aReader
   *        The reader to read from. May be <code>null</code>. Automatically
   *        closed!
   * @param aWriter
   *        The writer to write to. May be <code>null</code>. Automatically
   *        closed!
   * @return <code>{@link ESuccess#SUCCESS}</code> if copying took place, <code>
   *         {@link ESuccess#FAILURE}</code> otherwise
   */
  @Nonnull
  public static ESuccess copyReaderToWriterAndCloseWriter (@Nullable @WillClose final Reader aReader,
                                                           @Nullable @WillClose final Writer aWriter)
  {
    return copyCharStream ().from (aReader).closeFrom (true).to (aWriter).closeTo (true).build ();
  }

  /**
   * Pass the content of the given reader to the given writer. The reader is
   * automatically closed, whereas the writer stays open!
   *
   * @param aReader
   *        The reader to read from. May be <code>null</code>. Automatically
   *        closed!
   * @param aWriter
   *        The writer to write to. May be <code>null</code>. Not automatically
   *        closed!
   * @param nLimit
   *        The maximum number of chars to be copied to the writer. Must be &ge;
   *        0.
   * @return <code>{@link ESuccess#SUCCESS}</code> if copying took place, <code>
   *         {@link ESuccess#FAILURE}</code> otherwise
   * @deprecated Since v10.0. Use the builder instead
   */
  @Deprecated
  @Nonnull
  public static ESuccess copyReaderToWriterWithLimit (@WillClose @Nullable final Reader aReader,
                                                      @WillNotClose @Nullable final Writer aWriter,
                                                      final long nLimit)
  {
    return copyCharStream ().from (aReader).closeFrom (true).to (aWriter).closeTo (false).limit (nLimit).build ();
  }

  /**
   * Pass the content of the given reader to the given writer. The reader and
   * the writer are automatically closed!
   *
   * @param aReader
   *        The reader to read from. May be <code>null</code>. Automatically
   *        closed!
   * @param aWriter
   *        The writer to write to. May be <code>null</code>. Automatically
   *        closed!
   * @param nLimit
   *        The maximum number of chars to be copied to the writer. Must be &ge;
   *        0.
   * @return <code>{@link ESuccess#SUCCESS}</code> if copying took place, <code>
   *         {@link ESuccess#FAILURE}</code> otherwise
   * @deprecated Since v10.0. Use the builder instead
   */
  @Deprecated
  @Nonnull
  public static ESuccess copyReaderToWriterWithLimitAndCloseWriter (@Nullable @WillClose final Reader aReader,
                                                                    @Nullable @WillClose final Writer aWriter,
                                                                    @Nonnegative final long nLimit)
  {
    return copyCharStream ().from (aReader).closeFrom (true).to (aWriter).closeTo (true).limit (nLimit).build ();
  }

  /**
   * Pass the content of the given reader to the given writer. The reader is
   * automatically closed, whereas the writer stays open!
   *
   * @param aReader
   *        The reader to read from. May be <code>null</code>. Automatically
   *        closed!
   * @param aWriter
   *        The writer to write to. May be <code>null</code>. Not automatically
   *        closed!
   * @param aCopyCharCount
   *        An optional mutable long object that will receive the total number
   *        of copied characters. Note: and optional old value is overwritten!
   * @return <code>{@link ESuccess#SUCCESS}</code> if copying took place, <code>
   *         {@link ESuccess#FAILURE}</code> otherwise
   * @deprecated Since v10.0. Use the builder instead
   */
  @Deprecated
  @Nonnull
  public static ESuccess copyReaderToWriter (@WillClose @Nullable final Reader aReader,
                                             @WillNotClose @Nullable final Writer aWriter,
                                             @Nullable final MutableLong aCopyCharCount)
  {
    return copyCharStream ().from (aReader)
                            .closeFrom (true)
                            .to (aWriter)
                            .closeTo (false)
                            .copyCharCount (aCopyCharCount)
                            .build ();
  }

  /**
   * Pass the content of the given reader to the given writer. The reader is
   * automatically closed, whereas the writer stays open!
   *
   * @param aReader
   *        The reader to read from. May be <code>null</code>. Automatically
   *        closed!
   * @param aWriter
   *        The writer to write to. May be <code>null</code>. Not automatically
   *        closed!
   * @param aBuffer
   *        The buffer to use. May not be <code>null</code>.
   * @return <code>{@link ESuccess#SUCCESS}</code> if copying took place, <code>
   *         {@link ESuccess#FAILURE}</code> otherwise
   * @deprecated Since v10.0. Use the builder instead
   */
  @Deprecated
  @Nonnull
  public static ESuccess copyReaderToWriter (@WillClose @Nullable final Reader aReader,
                                             @WillNotClose @Nullable final Writer aWriter,
                                             @Nonnull final char [] aBuffer)
  {
    return copyCharStream ().from (aReader).closeFrom (true).to (aWriter).closeTo (false).buffer (aBuffer).build ();
  }

  /**
   * @return A new {@link CopyCharStreamBuilder}. Never <code>null</code>.
   */
  @Nonnull
  public static CopyCharStreamBuilder copyCharStream ()
  {
    return new CopyCharStreamBuilder ();
  }

  /**
   * A simple builder to copy a Reader ({@link #from(Reader)}) to an Writer
   * ({@link #to(Writer)}) with certain parameters. Call {@link #build()} to
   * execute the copying.
   *
   * @author Philip Helger
   * @since 10.0.0
   */
  public static class CopyCharStreamBuilder implements IBuilder <ESuccess>
  {
    public static final boolean DEFAULT_CLOSE_FROM = false;
    public static final boolean DEFAULT_CLOSE_TO = false;

    private Reader m_aReader;
    private boolean m_bCloseReader = DEFAULT_CLOSE_FROM;
    private Writer m_aWriter;
    private boolean m_bCloseWriter = DEFAULT_CLOSE_TO;
    private char [] m_aBuffer;
    private long m_nLimit = CGlobal.ILLEGAL_ULONG;
    private IExceptionCallback <IOException> m_aExceptionCallback;
    private MutableLong m_aCopyCharCount;

    /**
     * @param a
     *        The Reader to read from. May be <code>null</code>.
     * @return this for chaining
     */
    @Nonnull
    public CopyCharStreamBuilder from (@Nullable final Reader a)
    {
      m_aReader = a;
      return this;
    }

    /**
     * @param b
     *        <code>true</code> to close the Reader, <code>false</code> to leave
     *        it open. Default is {@link #DEFAULT_CLOSE_FROM}
     * @return this for chaining
     */
    @Nonnull
    public CopyCharStreamBuilder closeFrom (final boolean b)
    {
      m_bCloseReader = b;
      return this;
    }

    /**
     * @param a
     *        The Writer to write to. May be <code>null</code>.
     * @return this for chaining
     */
    @Nonnull
    public CopyCharStreamBuilder to (@Nullable final Writer a)
    {
      m_aWriter = a;
      return this;
    }

    /**
     * @param b
     *        <code>true</code> to close the Writer, <code>false</code> to leave
     *        it open.
     * @return this for chaining
     */
    @Nonnull
    public CopyCharStreamBuilder closeTo (final boolean b)
    {
      m_bCloseWriter = b;
      return this;
    }

    /**
     * @param a
     *        The buffer to use. May be <code>null</code>.
     * @return this for chaining
     */
    @Nonnull
    public CopyCharStreamBuilder buffer (@Nullable final char [] a)
    {
      m_aBuffer = a;
      return this;
    }

    /**
     * @param n
     *        An optional maximum number of chars to copied from the Reader to
     *        the Writer. May be &lt; 0 to indicate no limit, meaning all chars
     *        are copied.
     * @return this for chaining
     * @see #unlimited()
     */
    @Nonnull
    public CopyCharStreamBuilder limit (final long n)
    {
      m_nLimit = n;
      return this;
    }

    /**
     * @param a
     *        An optional maximum number of chars to copied from the InputStream
     *        to the OutputStream. May be &lt; 0 to indicate no limit, meaning
     *        all bytes are copied. If <code>null</code> no limit is set
     * @return this for chaining
     * @since 10.1.0
     * @see #unlimited()
     */
    @Nonnull
    public CopyCharStreamBuilder limit (@Nullable final Long a)
    {
      return a == null ? unlimited () : limit (a.longValue ());
    }

    /**
     * Ensure no limit in copying (which is also the default).
     *
     * @return this for chaining
     * @see #limit(long)
     */
    @Nonnull
    public CopyCharStreamBuilder unlimited ()
    {
      return limit (CGlobal.ILLEGAL_ULONG);
    }

    /**
     * @param a
     *        The Exception callback to be invoked, if an exception occurs. May
     *        be <code>null</code>.
     * @return this for chaining
     */
    @Nonnull
    public CopyCharStreamBuilder exceptionCallback (@Nullable final IExceptionCallback <IOException> a)
    {
      m_aExceptionCallback = a;
      return this;
    }

    /**
     * @param a
     *        An optional mutable long object that will receive the total number
     *        of copied chars. Note: and optional old value is overwritten.
     *        Note: this is only called, if copying was successful, and not in
     *        case of an exception.
     * @return this for chaining
     */
    @Nonnull
    public CopyCharStreamBuilder copyCharCount (@Nullable final MutableLong a)
    {
      m_aCopyCharCount = a;
      return this;
    }

    @Nonnegative
    private static long _copyReaderToWriter (@Nonnull @WillNotClose final Reader aReader,
                                             @Nonnull @WillNotClose final Writer aWriter,
                                             @Nonnull final char [] aBuffer) throws IOException
    {
      long nTotalCharsWritten = 0;
      int nCharsRead;
      // Potentially blocking read
      while ((nCharsRead = aReader.read (aBuffer, 0, aBuffer.length)) > -1)
      {
        aWriter.write (aBuffer, 0, nCharsRead);
        nTotalCharsWritten += nCharsRead;
      }
      return nTotalCharsWritten;
    }

    @Nonnegative
    private static long _copyReaderToWriterWithLimit (@Nonnull @WillNotClose final Reader aReader,
                                                      @Nonnull @WillNotClose final Writer aWriter,
                                                      @Nonnull final char [] aBuffer,
                                                      @Nonnegative final long nLimit) throws IOException
    {
      long nRest = nLimit;
      long nTotalCharsWritten = 0;
      while (true)
      {
        // if nRest is smaller than aBuffer.length, which is an int, it is safe
        // to
        // cast nRest also to an int!
        final int nCharsToRead = nRest >= aBuffer.length ? aBuffer.length : (int) nRest;
        if (nCharsToRead == 0)
          break;
        // Potentially blocking read
        final int nCharsRead = aReader.read (aBuffer, 0, nCharsToRead);
        if (nCharsRead == -1)
        {
          // EOF
          break;
        }
        if (nCharsRead > 0)
        {
          // At least one byte read
          aWriter.write (aBuffer, 0, nCharsRead);
          nTotalCharsWritten += nCharsRead;
          nRest -= nCharsRead;
        }
      }
      return nTotalCharsWritten;
    }

    /**
     * This method performs the main copying
     */
    @Nonnull
    public ESuccess build ()
    {
      try
      {
        if (m_aReader == null)
        {
          if (LOGGER.isDebugEnabled ())
            LOGGER.debug ("The source Reader is not set - hence no copying is possible");
          return ESuccess.FAILURE;
        }
        if (m_aWriter == null)
        {
          if (LOGGER.isDebugEnabled ())
            LOGGER.debug ("The target Writer is not set - hence no copying is possible");
          return ESuccess.FAILURE;
        }

        final char [] aBuffer = m_aBuffer != null && m_aBuffer.length > 0 ? m_aBuffer : createDefaultCopyBufferChars ();
        // both streams are not null
        final long nTotalCharsCopied;
        if (m_nLimit < 0)
          nTotalCharsCopied = _copyReaderToWriter (m_aReader, m_aWriter, aBuffer);
        else
          nTotalCharsCopied = _copyReaderToWriterWithLimit (m_aReader, m_aWriter, aBuffer, m_nLimit);

        // Add to statistics
        STATS_COPY_CHARS.addSize (nTotalCharsCopied);

        // Remember copied bytes?
        if (m_aCopyCharCount != null)
          m_aCopyCharCount.set (nTotalCharsCopied);
        return ESuccess.SUCCESS;
      }
      catch (final IOException ex)
      {
        if (m_aExceptionCallback != null)
          m_aExceptionCallback.onException (ex);
        else
          if (!isKnownEOFException (ex))
            LOGGER.error ("Failed to copy from Reader to Writer", _propagate (ex));
      }
      finally
      {
        // Ensure reader/writer are closed under all circumstances
        if (m_bCloseReader)
          close (m_aReader);
        if (m_bCloseWriter)
          close (m_aWriter);
      }
      return ESuccess.FAILURE;
    }
  }

  @Nullable
  public static NonBlockingStringWriter getCopy (@Nonnull @WillClose final Reader aReader)
  {
    final NonBlockingStringWriter aWriter = new NonBlockingStringWriter (DEFAULT_BUFSIZE);
    if (copyCharStream ().from (aReader).closeFrom (true).to (aWriter).closeTo (false).build ().isFailure ())
      return null;
    return aWriter;
  }

  @Nullable
  public static NonBlockingStringWriter getCopyWithLimit (@Nonnull @WillClose final Reader aReader,
                                                          @Nonnegative final long nLimit)
  {
    final NonBlockingStringWriter aWriter = new NonBlockingStringWriter (DEFAULT_BUFSIZE);
    if (copyCharStream ().from (aReader)
                         .closeFrom (true)
                         .to (aWriter)
                         .closeTo (false)
                         .limit (nLimit)
                         .build ()
                         .isFailure ())
      return null;
    return aWriter;
  }

  /**
   * Read all characters from the passed reader into a char array.
   *
   * @param aReader
   *        The reader to read from. May be <code>null</code>.
   * @return The character array or <code>null</code> if the reader is
   *         <code>null</code>.
   */
  @Nullable
  public static char [] getAllCharacters (@Nullable @WillClose final Reader aReader)
  {
    if (aReader == null)
      return null;

    try (final NonBlockingStringWriter aWriter = getCopy (aReader))
    {
      if (aWriter == null)
        return null;

      return aWriter.getAsCharArray ();
    }
  }

  /**
   * Read all characters from the passed reader into a String.
   *
   * @param aReader
   *        The reader to read from. May be <code>null</code>.
   * @return The character array or <code>null</code> if the reader is
   *         <code>null</code>.
   */
  @Nullable
  public static String getAllCharactersAsString (@Nullable @WillClose final Reader aReader)
  {
    if (aReader == null)
      return null;

    try (final NonBlockingStringWriter aWriter = getCopy (aReader))
    {
      if (aWriter == null)
        return null;

      return aWriter.getAsString ();
    }
  }

  /**
   * Get the content of the passed Spring resource as one big string in the
   * passed character set.
   *
   * @param aISP
   *        The resource to read. May not be <code>null</code>.
   * @param aCharset
   *        The character set to use. May not be <code>null</code>.
   * @return <code>null</code> if the resolved input stream is <code>null</code>
   *         , the content otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static ICommonsList <String> readStreamLines (@Nullable final IHasInputStream aISP,
                                                       @Nonnull final Charset aCharset)
  {
    return readStreamLines (aISP, aCharset, 0, CGlobal.ILLEGAL_UINT);
  }

  /**
   * Get the content of the passed Spring resource as one big string in the
   * passed character set.
   *
   * @param aISP
   *        The resource to read. May be <code>null</code>.
   * @param aCharset
   *        The character set to use. May not be <code>null</code>.
   * @param nLinesToSkip
   *        The 0-based index of the first line to read. Pass in 0 to indicate
   *        to read everything.
   * @param nLinesToRead
   *        The number of lines to read. Pass in {@link CGlobal#ILLEGAL_UINT} to
   *        indicate that all lines should be read. If the number passed here
   *        exceeds the number of lines in the file, nothing happens.
   * @return <code>null</code> if the resolved input stream is <code>null</code>
   *         , the content otherwise.
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
   * Get the content of the passed stream as a list of lines in the passed
   * character set.
   *
   * @param aIS
   *        The input stream to read from. May be <code>null</code>.
   * @param aCharset
   *        The character set to use. May not be <code>null</code>.
   * @return <code>null</code> if the input stream is <code>null</code>, the
   *         content lines otherwise.
   */
  @Nullable
  @ReturnsMutableCopy
  public static ICommonsList <String> readStreamLines (@WillClose @Nullable final InputStream aIS,
                                                       @Nonnull @Nonempty final Charset aCharset)
  {
    return readStreamLines (aIS, aCharset, 0, CGlobal.ILLEGAL_UINT);
  }

  /**
   * Get the content of the passed stream as a list of lines in the passed
   * character set.
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
   * Get the content of the passed stream as a list of lines in the passed
   * character set.
   *
   * @param aIS
   *        The input stream to read from. May be <code>null</code>.
   * @param aCharset
   *        The character set to use. May not be <code>null</code>.
   * @param nLinesToSkip
   *        The 0-based index of the first line to read. Pass in 0 to indicate
   *        to read everything.
   * @param nLinesToRead
   *        The number of lines to read. Pass in {@link CGlobal#ILLEGAL_UINT} to
   *        indicate that all lines should be read. If the number passed here
   *        exceeds the number of lines in the file, nothing happens.
   * @return <code>null</code> if the input stream is <code>null</code>, the
   *         content lines otherwise.
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
   * Read the complete content of the passed stream and pass each line
   * separately to the passed callback.
   *
   * @param aIS
   *        The input stream to read from. May be <code>null</code>.
   * @param aCharset
   *        The character set to use. May not be <code>null</code>.
   * @param aLineCallback
   *        The callback that is invoked for all read lines. Each passed line
   *        does NOT contain the line delimiter!
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
   * Read the content of the passed stream line by line and invoking a callback
   * on all matching lines.
   *
   * @param aIS
   *        The input stream to read from. May be <code>null</code>.
   * @param aCharset
   *        The character set to use. May not be <code>null</code>.
   * @param nLinesToSkip
   *        The 0-based index of the first line to read. Pass in 0 to indicate
   *        to read everything.
   * @param nLinesToRead
   *        The number of lines to read. Pass in {@link CGlobal#ILLEGAL_UINT} to
   *        indicate that all lines should be read. If the number passed here
   *        exceeds the number of lines in the file, nothing happens.
   * @param aLineCallback
   *        The callback that is invoked for all read lines. Each passed line
   *        does NOT contain the line delimiter! Note: it is not invoked for
   *        skipped lines!
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
            LOGGER.error ("Failed to read from reader", _propagate (ex));
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
   *        The output stream to write to. May not be <code>null</code>. Is
   *        closed independent of error or success.
   * @param aBuf
   *        The byte array from which is to be written. May not be
   *        <code>null</code>.
   * @param nOfs
   *        The 0-based index to the first byte in the array to be written. May
   *        not be &lt; 0.
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
      LOGGER.error ("Failed to write to output stream", _propagate (ex));
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
   *        The output stream to write to. May not be <code>null</code>. Is
   *        closed independent of error or success.
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
   *        The output stream to write to. May not be <code>null</code>. Is
   *        closed independent of error or success.
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

  @Nonnull
  public static NonBlockingStringReader createReader (@Nonnull final String sText)
  {
    return new NonBlockingStringReader (sText);
  }

  @Nonnull
  public static NonBlockingStringReader createReader (@Nonnull final char [] aChars)
  {
    return new NonBlockingStringReader (aChars);
  }

  @Nullable
  public static InputStreamReader createReader (@Nullable final InputStream aIS, @Nonnull final Charset aCharset)
  {
    ValueEnforcer.notNull (aCharset, "Charset");
    return aIS == null ? null : new InputStreamReader (aIS, aCharset);
  }

  @Nullable
  public static OutputStreamWriter createWriter (@Nullable final OutputStream aOS, @Nonnull final Charset aCharset)
  {
    ValueEnforcer.notNull (aCharset, "Charset");
    return aOS == null ? null : new OutputStreamWriter (aOS, aCharset);
  }

  /**
   * Fully skip the passed amounts in the input stream. Only forward skipping is
   * possible!
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
   *        The buffer to write to. May not be <code>null</code>. Must be &ge;
   *        than the content to be read.
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
   *        The buffer to write to. May not be <code>null</code>. Must be &ge;
   *        than the content to be read.
   * @param nOfs
   *        The offset into the destination buffer to use. May not be &lt; 0.
   * @param nLen
   *        The number of bytes to read into the destination buffer to use. May
   *        not be &lt; 0.
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

  public static boolean isBuffered (@Nullable final InputStream aIS)
  {
    return aIS instanceof BufferedInputStream ||
           aIS instanceof NonBlockingBufferedInputStream ||
           aIS instanceof ByteArrayInputStream ||
           aIS instanceof NonBlockingByteArrayInputStream ||
           aIS instanceof ByteBufferInputStream ||
           (aIS instanceof WrappedInputStream && isBuffered (((WrappedInputStream) aIS).getWrappedInputStream ()));
  }

  @Nullable
  public static InputStream getBuffered (@Nullable final InputStream aIS)
  {
    return aIS == null || isBuffered (aIS) ? aIS : new NonBlockingBufferedInputStream (aIS);
  }

  public static boolean isBuffered (@Nullable final OutputStream aOS)
  {
    return aOS instanceof BufferedOutputStream ||
           aOS instanceof NonBlockingBufferedOutputStream ||
           aOS instanceof ByteArrayOutputStream ||
           aOS instanceof NonBlockingByteArrayOutputStream ||
           aOS instanceof ByteBufferOutputStream ||
           (aOS instanceof WrappedOutputStream && isBuffered (((WrappedOutputStream) aOS).getWrappedOutputStream ()));
  }

  @Nullable
  public static OutputStream getBuffered (@Nullable final OutputStream aOS)
  {
    return aOS == null || isBuffered (aOS) ? aOS : new NonBlockingBufferedOutputStream (aOS);
  }

  public static boolean isBuffered (@Nullable final Reader aReader)
  {
    return aReader instanceof BufferedReader ||
           aReader instanceof NonBlockingBufferedReader ||
           aReader instanceof StringReader ||
           aReader instanceof NonBlockingStringReader ||
           (aReader instanceof WrappedReader && isBuffered (((WrappedReader) aReader).getWrappedReader ()));
  }

  @Nullable
  public static Reader getBuffered (@Nullable final Reader aReader)
  {
    return aReader == null || isBuffered (aReader) ? aReader : new NonBlockingBufferedReader (aReader);
  }

  public static boolean isBuffered (@Nullable final Writer aWriter)
  {
    return aWriter instanceof BufferedWriter ||
           aWriter instanceof NonBlockingBufferedWriter ||
           aWriter instanceof StringWriter ||
           aWriter instanceof NonBlockingStringWriter ||
           (aWriter instanceof WrappedWriter && isBuffered (((WrappedWriter) aWriter).getWrappedWriter ()));
  }

  @Nullable
  public static Writer getBuffered (@Nullable final Writer aWriter)
  {
    return aWriter == null || isBuffered (aWriter) ? aWriter : new NonBlockingBufferedWriter (aWriter);
  }

  @Nullable
  public static InputStream checkForInvalidFilterInputStream (@Nullable final InputStream aIS)
  {
    if (aIS != null)
      try
      {
        /*
         * This will fail if IS is a FilterInputStream with a <code>null</code>
         * contained InputStream. This happens e.g. when a JAR URL with a
         * directory that does not end with a slash is returned.
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
   * Because {@link DataOutputStream#writeUTF(String)} has a limit of 64KB this
   * methods provides a similar solution but simply writing the bytes.
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
   * Because {@link DataOutputStream#writeUTF(String)} has a limit of 64KB this
   * methods provides a similar solution for reading like
   * {@link DataInputStream#readUTF()} but what was written in
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
