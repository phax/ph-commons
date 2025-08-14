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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.WillClose;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.CGlobal;
import com.helger.base.enforcer.ValueEnforcer;
import com.helger.base.mock.exception.IMockException;
import com.helger.base.nonblocking.NonBlockingBufferedInputStream;
import com.helger.base.nonblocking.NonBlockingBufferedOutputStream;
import com.helger.base.nonblocking.NonBlockingBufferedReader;
import com.helger.base.nonblocking.NonBlockingBufferedWriter;
import com.helger.base.nonblocking.NonBlockingByteArrayInputStream;
import com.helger.base.nonblocking.NonBlockingByteArrayOutputStream;
import com.helger.base.nonblocking.NonBlockingStringReader;
import com.helger.base.nonblocking.NonBlockingStringWriter;
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
}
