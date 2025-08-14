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
package com.helger.base.io.stream;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.function.LongConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.Nonempty;
import com.helger.annotation.Nonnegative;
import com.helger.annotation.WillClose;
import com.helger.annotation.WillNotClose;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.CGlobal;
import com.helger.base.builder.IBuilder;
import com.helger.base.callback.exception.IExceptionCallback;
import com.helger.base.equals.ValueEnforcer;
import com.helger.base.io.iface.IHasInputStream;
import com.helger.base.io.nonblocking.NonBlockingBufferedInputStream;
import com.helger.base.io.nonblocking.NonBlockingBufferedOutputStream;
import com.helger.base.io.nonblocking.NonBlockingBufferedReader;
import com.helger.base.io.nonblocking.NonBlockingBufferedWriter;
import com.helger.base.io.nonblocking.NonBlockingByteArrayInputStream;
import com.helger.base.io.nonblocking.NonBlockingByteArrayOutputStream;
import com.helger.base.io.nonblocking.NonBlockingStringReader;
import com.helger.base.io.nonblocking.NonBlockingStringWriter;
import com.helger.base.mock.exception.IMockException;
import com.helger.base.numeric.mutable.MutableLong;
import com.helger.base.state.ESuccess;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

@Immutable
public class StreamHelper
{
  /** buffer size for copy operations */
  public static final int DEFAULT_BUFSIZE = 16 * CGlobal.BYTES_PER_KILOBYTE;

  private static final Logger LOGGER = LoggerFactory.getLogger (StreamHelper.class);

  protected StreamHelper ()
  {}

  /**
   * @return A newly created copy buffer using {@link #DEFAULT_BUFSIZE}. Never <code>null</code>.
   * @since 9.3.6
   */
  @Nonnull
  @ReturnsMutableCopy
  public static byte [] createDefaultCopyBufferBytes ()
  {
    return new byte [DEFAULT_BUFSIZE];
  }

  /**
   * @return A newly created copy buffer using {@link #DEFAULT_BUFSIZE}. Never <code>null</code>.
   * @since 9.3.6
   */
  @Nonnull
  @ReturnsMutableCopy
  public static char [] createDefaultCopyBufferChars ()
  {
    return new char [DEFAULT_BUFSIZE];
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
  protected static Exception internalGetPropagatableException (@Nonnull final Exception ex)
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
          LOGGER.error ("Failed to close object " + aCloseable.getClass ().getName (),
                        internalGetPropagatableException (ex));
      }
    }
    return ESuccess.FAILURE;
  }

  /**
   * Close the passed stream by encapsulating the declared {@link IOException}. If the passed object
   * also implements the {@link Flushable} interface, it is tried to be flushed before it is closed.
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
        if (aCloseable instanceof final Flushable aFlushable)
          flush (aFlushable);

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
          LOGGER.error ("Failed to close object " + aCloseable.getClass ().getName (),
                        internalGetPropagatableException (ex));
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
          LOGGER.error ("Failed to flush object " + aFlushable.getClass ().getName (),
                        internalGetPropagatableException (ex));
      }
    return ESuccess.FAILURE;
  }

  public static boolean isBuffered (@Nullable final InputStream aIS)
  {
    return aIS instanceof BufferedInputStream ||
           aIS instanceof NonBlockingBufferedInputStream ||
           aIS instanceof ByteArrayInputStream ||
           aIS instanceof NonBlockingByteArrayInputStream ||
           aIS instanceof ByteBufferInputStream ||
           (aIS instanceof final WrappedInputStream aWrappedIS && isBuffered (aWrappedIS.getWrappedInputStream ()));
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
           (aOS instanceof final WrappedOutputStream aWrappedOS && isBuffered (aWrappedOS.getWrappedOutputStream ()));
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
           (aReader instanceof final WrappedReader aWrappedReader && isBuffered (aWrappedReader.getWrappedReader ()));
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
           (aWriter instanceof final WrappedWriter aWrappedWriter && isBuffered (aWrappedWriter.getWrappedWriter ()));
  }

  @Nullable
  public static Writer getBuffered (@Nullable final Writer aWriter)
  {
    return aWriter == null || isBuffered (aWriter) ? aWriter : new NonBlockingBufferedWriter (aWriter);
  }

  /**
   * Pass the content of the given input stream to the given output stream. The input stream is
   * automatically closed, whereas the output stream stays open!
   *
   * @param aIS
   *        The input stream to read from. May be <code>null</code>. Automatically closed!
   * @param aOS
   *        The output stream to write to. May be <code>null</code>. Not automatically closed!
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
   * Pass the content of the given input stream to the given output stream. Both the input stream
   * and the output stream are automatically closed.
   *
   * @param aIS
   *        The input stream to read from. May be <code>null</code>. Automatically closed!
   * @param aOS
   *        The output stream to write to. May be <code>null</code>. Automatically closed!
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
   * @return A new {@link CopyByteStreamBuilder}. Never <code>null</code>.
   */
  @Nonnull
  public static CopyByteStreamBuilder copyByteStream ()
  {
    return new CopyByteStreamBuilder ();
  }

  /**
   * A simple builder to copy an InputStream ({@link #from(InputStream)}) to an OutputStream
   * ({@link #to(OutputStream)}) with certain parameters. Call {@link #build()} to execute the
   * copying.
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
    private LongConsumer m_aProgressCallback;

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
     *        <code>true</code> to close the InputStream, <code>false</code> to leave it open.
     *        Default is {@link #DEFAULT_CLOSE_SOURCE}
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
     *        <code>true</code> to close the OutputStream, <code>false</code> to leave it open.
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
     *        An optional maximum number of bytes to copied from the InputStream to the
     *        OutputStream. May be &lt; 0 to indicate no limit, meaning all bytes are copied.
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
     *        An optional maximum number of bytes to copied from the InputStream to the
     *        OutputStream. May be &lt; 0 to indicate no limit, meaning all bytes are copied. If
     *        <code>null</code> no limit is set
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
     *        The Exception callback to be invoked, if an exception occurs. May be
     *        <code>null</code>.
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
     *        An optional mutable long object that will receive the total number of copied bytes.
     *        Note: and optional old value is overwritten. Note: this is only called, if copying was
     *        successful, and not in case of an exception.
     * @return this for chaining
     */
    @Nonnull
    public CopyByteStreamBuilder copyByteCount (@Nullable final MutableLong a)
    {
      m_aCopyByteCount = a;
      return this;
    }

    /**
     * @param a
     *        An optional progress callback that takes the number of total bytes written during the
     *        copy action. It is first invoked after some byte were written.
     * @return this for chaining
     * @since 11.0.3
     */
    @Nonnull
    public CopyByteStreamBuilder progressCallback (@Nullable final LongConsumer a)
    {
      m_aProgressCallback = a;
      return this;
    }

    @Nonnegative
    private static long _copyInputStreamToOutputStream (@Nonnull @WillNotClose final InputStream aIS,
                                                        @Nonnull @WillNotClose final OutputStream aOS,
                                                        @Nonnull final byte [] aBuffer,
                                                        @Nullable final LongConsumer aProgressCallback) throws IOException
    {
      final int nBufferLength = aBuffer.length;
      long nTotalBytesWritten = 0;
      int nBytesRead;
      // Potentially blocking read
      while ((nBytesRead = aIS.read (aBuffer, 0, nBufferLength)) > -1)
      {
        if (nBytesRead > 0)
        {
          aOS.write (aBuffer, 0, nBytesRead);
          nTotalBytesWritten += nBytesRead;

          if (aProgressCallback != null)
            aProgressCallback.accept (nTotalBytesWritten);
        }
      }
      return nTotalBytesWritten;
    }

    @Nonnegative
    private static long _copyInputStreamToOutputStreamWithLimit (@Nonnull @WillNotClose final InputStream aIS,
                                                                 @Nonnull @WillNotClose final OutputStream aOS,
                                                                 @Nonnull final byte [] aBuffer,
                                                                 @Nonnegative final long nLimit,
                                                                 @Nullable final LongConsumer aProgressCallback) throws IOException
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

          if (aProgressCallback != null)
            aProgressCallback.accept (nTotalBytesWritten);
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
          nTotalBytesCopied = _copyInputStreamToOutputStream (m_aIS, m_aOS, aBuffer, m_aProgressCallback);
        else
          nTotalBytesCopied = _copyInputStreamToOutputStreamWithLimit (m_aIS,
                                                                       m_aOS,
                                                                       aBuffer,
                                                                       m_nLimit,
                                                                       m_aProgressCallback);

        // Add to statistics
        // STATS_COPY_BYTES.addSize (nTotalBytesCopied);

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
            LOGGER.error ("Failed to copy from InputStream to OutputStream", internalGetPropagatableException (ex));
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
   * Get a byte buffer with all the available content of the passed input stream.
   *
   * @param aIS
   *        The source input stream. May not be <code>null</code>.
   * @return A new {@link NonBlockingByteArrayOutputStream} with all available content inside. The
   *         {@link OutputStream} must be closed by the caller since v10. Since v9.3.6 this method
   *         returns <code>null</code> if copying fails.
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
   * Get a byte buffer with all the available content of the passed input stream.
   *
   * @param aIS
   *        The source input stream. May not be <code>null</code>.
   * @param nLimit
   *        The maximum number of bytes to be copied to the output stream. Must be &ge; 0.
   * @return A new {@link NonBlockingByteArrayOutputStream} with all available content inside. The
   *         {@link OutputStream} must be closed by the caller since v10. Since v9.3.6 this method
   *         returns <code>null</code> if copying fails.
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
   * @return The byte array or <code>null</code> if the parameter or the resolved input stream is
   *         <code>null</code>.
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
   * @return The byte array or <code>null</code> if the input stream is <code>null</code>.
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
   * @return The String or <code>null</code> if the parameter or the resolved input stream is
   *         <code>null</code>.
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
   * @return The String or <code>null</code> if the input stream is <code>null</code>.
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
   * Pass the content of the given reader to the given writer. The reader is automatically closed,
   * whereas the writer stays open!
   *
   * @param aReader
   *        The reader to read from. May be <code>null</code>. Automatically closed!
   * @param aWriter
   *        The writer to write to. May be <code>null</code>. Not automatically closed!
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
   * Pass the content of the given reader to the given writer. The reader and the writer are
   * automatically closed!
   *
   * @param aReader
   *        The reader to read from. May be <code>null</code>. Automatically closed!
   * @param aWriter
   *        The writer to write to. May be <code>null</code>. Automatically closed!
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
   * @return A new {@link CopyCharStreamBuilder}. Never <code>null</code>.
   */
  @Nonnull
  public static CopyCharStreamBuilder copyCharStream ()
  {
    return new CopyCharStreamBuilder ();
  }

  /**
   * A simple builder to copy a Reader ({@link #from(Reader)}) to an Writer ({@link #to(Writer)})
   * with certain parameters. Call {@link #build()} to execute the copying.
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
    private LongConsumer m_aProgressCallback;

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
     *        <code>true</code> to close the Reader, <code>false</code> to leave it open. Default is
     *        {@link #DEFAULT_CLOSE_FROM}
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
     *        <code>true</code> to close the Writer, <code>false</code> to leave it open.
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
     *        An optional maximum number of chars to copied from the Reader to the Writer. May be
     *        &lt; 0 to indicate no limit, meaning all chars are copied.
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
     *        An optional maximum number of chars to copied from the InputStream to the
     *        OutputStream. May be &lt; 0 to indicate no limit, meaning all bytes are copied. If
     *        <code>null</code> no limit is set
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
     *        The Exception callback to be invoked, if an exception occurs. May be
     *        <code>null</code>.
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
     *        An optional mutable long object that will receive the total number of copied chars.
     *        Note: and optional old value is overwritten. Note: this is only called, if copying was
     *        successful, and not in case of an exception.
     * @return this for chaining
     */
    @Nonnull
    public CopyCharStreamBuilder copyCharCount (@Nullable final MutableLong a)
    {
      m_aCopyCharCount = a;
      return this;
    }

    /**
     * @param a
     *        An optional progress callback that takes the number of total chars written during the
     *        copy action. It is first invoked after some chars were written.
     * @return this for chaining
     * @since 11.0.3
     */
    @Nonnull
    public CopyCharStreamBuilder progressCallback (@Nullable final LongConsumer a)
    {
      m_aProgressCallback = a;
      return this;
    }

    @Nonnegative
    private static long _copyReaderToWriter (@Nonnull @WillNotClose final Reader aReader,
                                             @Nonnull @WillNotClose final Writer aWriter,
                                             @Nonnull final char [] aBuffer,
                                             @Nullable final LongConsumer aProgressCallback) throws IOException
    {
      long nTotalCharsWritten = 0;
      int nCharsRead;
      // Potentially blocking read
      while ((nCharsRead = aReader.read (aBuffer, 0, aBuffer.length)) > -1)
      {
        if (nCharsRead > 0)
        {
          aWriter.write (aBuffer, 0, nCharsRead);
          nTotalCharsWritten += nCharsRead;

          if (aProgressCallback != null)
            aProgressCallback.accept (nTotalCharsWritten);
        }
      }
      return nTotalCharsWritten;
    }

    @Nonnegative
    private static long _copyReaderToWriterWithLimit (@Nonnull @WillNotClose final Reader aReader,
                                                      @Nonnull @WillNotClose final Writer aWriter,
                                                      @Nonnull final char [] aBuffer,
                                                      @Nonnegative final long nLimit,
                                                      @Nullable final LongConsumer aProgressCallback) throws IOException
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

          if (aProgressCallback != null)
            aProgressCallback.accept (nTotalCharsWritten);
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
          nTotalCharsCopied = _copyReaderToWriter (m_aReader, m_aWriter, aBuffer, m_aProgressCallback);
        else
          nTotalCharsCopied = _copyReaderToWriterWithLimit (m_aReader,
                                                            m_aWriter,
                                                            aBuffer,
                                                            m_nLimit,
                                                            m_aProgressCallback);

        // Add to statistics
        // STATS_COPY_CHARS.addSize (nTotalCharsCopied);

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
            LOGGER.error ("Failed to copy from Reader to Writer", internalGetPropagatableException (ex));
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
   * @return The character array or <code>null</code> if the reader is <code>null</code>.
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
   * @return The character array or <code>null</code> if the reader is <code>null</code>.
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
}
