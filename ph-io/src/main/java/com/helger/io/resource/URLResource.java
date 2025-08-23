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
package com.helger.io.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.function.Consumer;

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

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

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

  public URLResource (@Nonnull final String sURL) throws MalformedURLException
  {
    this (new URL (sURL));
  }

  public URLResource (@Nonnull final URI aURI) throws MalformedURLException
  {
    this (aURI.toURL ());
  }

  public URLResource (@Nonnull final URL aURL)
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

  @Nonnull
  public String getResourceID ()
  {
    return getPath ();
  }

  @Nonnull
  public String getPath ()
  {
    return m_aURL.toExternalForm ();
  }

  @Nullable
  public static InputStream getInputStream (@Nonnull final URL aURL)
  {
    return URLHelper.getInputStream (aURL,
                                     DEFAULT_CONNECT_TIMEOUT,
                                     DEFAULT_READ_TIMEOUT,
                                     null,
                                     (IMutableWrapper <IOException>) null);
  }

  @Nullable
  public InputStream getInputStream ()
  {
    return getInputStream (DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT);
  }

  @Nullable
  public InputStream getInputStream (@CheckForSigned final int nConnectTimeoutMS,
                                     @CheckForSigned final int nReadTimeoutMS)
  {
    return getInputStream (nConnectTimeoutMS,
                           nReadTimeoutMS,
                           (Consumer <? super URLConnection>) null,
                           (IMutableWrapper <IOException>) null);
  }

  @Nullable
  public InputStream getInputStream (@Nullable final IMutableWrapper <IOException> aExceptionHolder)
  {
    return getInputStream (DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT, aExceptionHolder);
  }

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

  @Nullable
  public InputStream getInputStream (@CheckForSigned final int nConnectTimeoutMS,
                                     @CheckForSigned final int nReadTimeoutMS,
                                     @Nullable final Consumer <? super URLConnection> aConnectionModifier,
                                     @Nullable final IMutableWrapper <IOException> aExceptionHolder)
  {
    return URLHelper.getInputStream (m_aURL, nConnectTimeoutMS, nReadTimeoutMS, aConnectionModifier, aExceptionHolder);
  }

  public final boolean isReadMultiple ()
  {
    return true;
  }

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

  @Nonnull
  public URL getAsURL ()
  {
    return m_aURL;
  }

  @Nullable
  public URI getAsURI ()
  {
    return URLHelper.getAsURI (m_aURL);
  }

  @Nullable
  public File getAsFile ()
  {
    return URLHelper.getAsFileOrNull (m_aURL);
  }

  @Nonnull
  public URLResource getReadableCloneForPath (@Nonnull final URL aURL)
  {
    return new URLResource (aURL);
  }

  @Nonnull
  public URLResource getReadableCloneForPath (@Nonnull final String sPath)
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
