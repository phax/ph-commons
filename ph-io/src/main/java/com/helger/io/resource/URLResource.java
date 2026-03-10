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
package com.helger.io.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.function.Consumer;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.CheckForSigned;
import com.helger.annotation.concurrent.Immutable;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.equals.EqualsHelper;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.io.stream.StreamHelper;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.base.url.CURL;
import com.helger.base.url.URLHelper;
import com.helger.base.wrapper.IMutableWrapper;

/**
 * Implementation of the {@link IReadableResource} interface for URL objects.
 *
 * @author Philip Helger
 */
@Immutable
public class URLResource implements IReadableResource
{
  public static final int DEFAULT_CONNECT_TIMEOUT = -1;
  public static final int DEFAULT_READ_TIMEOUT = -1;

  @SuppressWarnings ("unused")
  private static final Logger LOGGER = LoggerFactory.getLogger (URLResource.class);

  private final URL m_aURL;

  /**
   * Create a new URL resource from a URL string.
   *
   * @param sURL
   *        The URL string. May not be <code>null</code>.
   * @throws MalformedURLException
   *         If the URL string is not a valid URL.
   */
  public URLResource (@NonNull final String sURL) throws MalformedURLException
  {
    this (new URL (sURL));
  }

  /**
   * Create a new URL resource from a URI.
   *
   * @param aURI
   *        The URI. May not be <code>null</code>.
   * @throws MalformedURLException
   *         If the URI cannot be converted to a URL.
   */
  public URLResource (@NonNull final URI aURI) throws MalformedURLException
  {
    this (aURI.toURL ());
  }

  /**
   * Create a new URL resource from a URL.
   *
   * @param aURL
   *        The URL to use. May not be <code>null</code>.
   */
  public URLResource (@NonNull final URL aURL)
  {
    m_aURL = ValueEnforcer.notNull (aURL, "URL");
  }

  /**
   * Check if the passed resource name is an explicit URL resource.
   *
   * @param sName
   *        The name to check. May be <code>null</code>.
   * @return <code>true</code> if the passed name is an explicit URL resource.
   */
  public static boolean isExplicitURLResource (@Nullable final String sName)
  {
    return URLHelper.getAsURL (sName, false) != null;
  }

  /**
   * Get the unique resource ID of this URL resource.
   *
   * @return The external form of the URL. Never <code>null</code>.
   */
  @NonNull
  public String getResourceID ()
  {
    return getPath ();
  }

  /**
   * Get the path of this URL resource.
   *
   * @return The external form of the URL. Never <code>null</code>.
   */
  @NonNull
  public String getPath ()
  {
    return m_aURL.toExternalForm ();
  }

  /**
   * Get an input stream for the passed URL using default connect and read timeouts.
   *
   * @param aURL
   *        The URL to open. May not be <code>null</code>.
   * @return <code>null</code> if the URL could not be opened.
   */
  @Nullable
  public static InputStream getInputStream (@NonNull final URL aURL)
  {
    return URLHelper.getInputStream (aURL,
                                     DEFAULT_CONNECT_TIMEOUT,
                                     DEFAULT_READ_TIMEOUT,
                                     null,
                                     (IMutableWrapper <IOException>) null);
  }

  /**
   * Get an input stream for this URL resource using default timeouts.
   *
   * @return <code>null</code> if the URL could not be opened.
   */
  @Nullable
  public InputStream getInputStream ()
  {
    return getInputStream (DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT);
  }

  /**
   * Get an input stream for this URL resource with specified timeouts.
   *
   * @param nConnectTimeoutMS
   *        The connect timeout in milliseconds. Use -1 for no timeout.
   * @param nReadTimeoutMS
   *        The read timeout in milliseconds. Use -1 for no timeout.
   * @return <code>null</code> if the URL could not be opened.
   */
  @Nullable
  public InputStream getInputStream (@CheckForSigned final int nConnectTimeoutMS,
                                     @CheckForSigned final int nReadTimeoutMS)
  {
    return getInputStream (nConnectTimeoutMS,
                           nReadTimeoutMS,
                           (Consumer <? super URLConnection>) null,
                           (IMutableWrapper <IOException>) null);
  }

  /**
   * Get an input stream for this URL resource with an exception holder.
   *
   * @param aExceptionHolder
   *        An optional holder for the exception. May be <code>null</code>.
   * @return <code>null</code> if the URL could not be opened.
   */
  @Nullable
  public InputStream getInputStream (@Nullable final IMutableWrapper <IOException> aExceptionHolder)
  {
    return getInputStream (DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT, aExceptionHolder);
  }

  /**
   * Get an input stream for this URL resource with specified timeouts and exception holder.
   *
   * @param nConnectTimeoutMS
   *        The connect timeout in milliseconds. Use -1 for no timeout.
   * @param nReadTimeoutMS
   *        The read timeout in milliseconds. Use -1 for no timeout.
   * @param aExceptionHolder
   *        An optional holder for the exception. May be <code>null</code>.
   * @return <code>null</code> if the URL could not be opened.
   */
  @Nullable
  public InputStream getInputStream (@CheckForSigned final int nConnectTimeoutMS,
                                     @CheckForSigned final int nReadTimeoutMS,
                                     @Nullable final IMutableWrapper <IOException> aExceptionHolder)
  {
    return getInputStream (nConnectTimeoutMS,
                           nReadTimeoutMS,
                           (Consumer <? super URLConnection>) null,
                           aExceptionHolder);
  }

  /**
   * Get an input stream for this URL resource with full control over timeouts, connection
   * modification and exception handling.
   *
   * @param nConnectTimeoutMS
   *        The connect timeout in milliseconds. Use -1 for no timeout.
   * @param nReadTimeoutMS
   *        The read timeout in milliseconds. Use -1 for no timeout.
   * @param aConnectionModifier
   *        An optional consumer to modify the URL connection before connecting. May be
   *        <code>null</code>.
   * @param aExceptionHolder
   *        An optional holder for the exception. May be <code>null</code>.
   * @return <code>null</code> if the URL could not be opened.
   */
  @Nullable
  public InputStream getInputStream (@CheckForSigned final int nConnectTimeoutMS,
                                     @CheckForSigned final int nReadTimeoutMS,
                                     @Nullable final Consumer <? super URLConnection> aConnectionModifier,
                                     @Nullable final IMutableWrapper <IOException> aExceptionHolder)
  {
    return URLHelper.getInputStream (m_aURL, nConnectTimeoutMS, nReadTimeoutMS, aConnectionModifier, aExceptionHolder);
  }

  /**
   * Check if this resource can be read multiple times. URL resources always return
   * <code>true</code>.
   *
   * @return Always <code>true</code>.
   */
  public final boolean isReadMultiple ()
  {
    return true;
  }

  /**
   * Check if this URL resource exists. For file URLs, the file existence is checked. For other
   * protocols, an input stream is attempted to be opened.
   *
   * @return <code>true</code> if the resource exists, <code>false</code> otherwise.
   */
  public boolean exists ()
  {
    // 1. as file
    if (CURL.PROTOCOL_FILE.equals (m_aURL.getProtocol ()))
      return getAsFile ().exists ();

    // Not a file URL
    InputStream aIS = null;
    try
    {
      // 2. as stream
      aIS = getInputStream ();
      return aIS != null;
    }
    catch (final Exception ex)
    {
      // 3. no
      return false;
    }
    finally
    {
      StreamHelper.close (aIS);
    }
  }

  /**
   * Get this URL resource as a URL.
   *
   * @return The URL. Never <code>null</code>.
   */
  @NonNull
  public URL getAsURL ()
  {
    return m_aURL;
  }

  /**
   * Get this URL resource as a URI.
   *
   * @return The URI or <code>null</code> if the conversion failed.
   */
  @Nullable
  public URI getAsURI ()
  {
    return URLHelper.getAsURI (m_aURL);
  }

  /**
   * Get this URL resource as a file. Only works for file-protocol URLs.
   *
   * @return The file or <code>null</code> if conversion is not possible.
   */
  @Nullable
  public File getAsFile ()
  {
    return URLHelper.getAsFileOrNull (m_aURL);
  }

  /**
   * Create a new {@link URLResource} for a different URL.
   *
   * @param aURL
   *        The new URL to use. May not be <code>null</code>.
   * @return A new {@link URLResource} instance. Never <code>null</code>.
   */
  @NonNull
  public URLResource getReadableCloneForPath (@NonNull final URL aURL)
  {
    return new URLResource (aURL);
  }

  /**
   * Create a new {@link URLResource} for a different path.
   *
   * @param sPath
   *        The new URL string to use. May not be <code>null</code>.
   * @return A new {@link URLResource} instance. Never <code>null</code>.
   * @throws IllegalArgumentException
   *         If the path cannot be converted to a URL.
   */
  @NonNull
  public URLResource getReadableCloneForPath (@NonNull final String sPath)
  {
    try
    {
      return new URLResource (sPath);
    }
    catch (final MalformedURLException ex)
    {
      throw new IllegalArgumentException ("Cannot convert to an URL: " + sPath, ex);
    }
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final URLResource rhs = (URLResource) o;
    return EqualsHelper.equals (m_aURL, rhs.m_aURL);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aURL).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("url", m_aURL).getToString ();
  }
}
