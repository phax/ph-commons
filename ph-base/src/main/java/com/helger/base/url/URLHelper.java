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
package com.helger.base.url;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.function.Consumer;
import java.util.jar.JarEntry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.CheckForSigned;
import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.base.classloader.ClassLoaderHelper;
import com.helger.base.debug.GlobalDebug;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.equals.EqualsHelper;
import com.helger.base.io.stream.StreamHelper;
import com.helger.base.lang.clazz.ClassHelper;
import com.helger.base.string.StringHelper;
import com.helger.base.wrapper.IMutableWrapper;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * URL utilities.
 *
 * @author Philip Helger
 */
@ThreadSafe
public final class URLHelper
{
  private static final Logger LOGGER = LoggerFactory.getLogger (URLHelper.class);

  private static final boolean DEBUG_GET_IS = false;

  @PresentForCodeCoverage
  private static final URLHelper INSTANCE = new URLHelper ();

  private URLHelper ()
  {}

  /**
   * Special equals implementation for URLs because <code>URL.equals</code> performs a host
   * lookup.<br>
   * <a href= "http://michaelscharf.blogspot.com/2006/11/javaneturlequals-and-hashcode-make.html"
   * >Click here for details</a>
   *
   * @param aObj1
   *        first URL
   * @param aObj2
   *        secondURL
   * @return <code>true</code> if they contain the same string
   */
  public static boolean equalURLs (@Nonnull final URL aObj1, @Nonnull final URL aObj2)
  {
    return EqualsHelper.equalsCustom (aObj1, aObj2, (x, y) -> x.toExternalForm ().equals (y.toExternalForm ()));
  }

  /**
   * Get the final representation of the URL using the specified elements.
   *
   * @param sPath
   *        The main path. May be <code>null</code>.
   * @param sQueryParams
   *        The set of all query parameters already concatenated with the correct characters (&amp;
   *        and =). May be <code>null</code>.
   * @param sAnchor
   *        An optional anchor to be added. May be <code>null</code>.
   * @return May be <code>null</code> if path, anchor and parameters are <code>null</code>.
   */
  @Nullable
  public static String getURLString (@Nullable final String sPath,
                                     @Nullable final String sQueryParams,
                                     @Nullable final String sAnchor)
  {
    final boolean bHasPath = StringHelper.isNotEmpty (sPath);
    final boolean bHasQueryParams = StringHelper.isNotEmpty (sQueryParams);
    final boolean bHasAnchor = StringHelper.isNotEmpty (sAnchor);

    if (GlobalDebug.isDebugMode ())
    {
      // Consistency checks
      if (bHasPath)
      {
        if (sPath.contains (CURL.QUESTIONMARK_STR))
          LOGGER.warn ("Path contains the question mark ('?') character: '" + sPath + "'");
        if (sPath.contains (CURL.AMPERSAND_STR))
          LOGGER.warn ("Path contains the ampersand ('&') character: '" + sPath + "'");
        if (sPath.contains (CURL.HASH_STR))
          LOGGER.warn ("Path contains the hash ('#') character: '" + sPath + "'");
      }
      if (bHasQueryParams)
      {
        if (sQueryParams.contains (CURL.QUESTIONMARK_STR))
          LOGGER.warn ("Query parameters contain the question mark ('?') character: '" + sQueryParams + "'");
      }
      if (bHasAnchor)
      {
        if (sAnchor.contains (CURL.HASH_STR))
          LOGGER.warn ("Anchor contains the hash ('#') character: '" + sAnchor + "'");
      }
    }

    // return URL as is?
    if (!bHasQueryParams && !bHasAnchor)
    {
      // Return URL as is (may be null)
      return sPath;
    }

    final StringBuilder aSB = new StringBuilder ();
    if (bHasPath)
      aSB.append (sPath);
    if (bHasQueryParams)
    {
      final boolean bHasQuestionMark = aSB.indexOf (CURL.QUESTIONMARK_STR) >= 0;
      if (bHasQuestionMark)
      {
        // Only if the "?" is not the last char otherwise the base href already
        // contains a parameter!
        final char cLast = StringHelper.getLastChar (aSB);
        if (cLast != CURL.QUESTIONMARK && cLast != CURL.AMPERSAND)
          aSB.append (CURL.AMPERSAND);
      }
      else
      {
        // First parameter
        aSB.append (CURL.QUESTIONMARK);
      }
      // add all parameters
      aSB.append (sQueryParams);
    }
    // Append anchor
    if (bHasAnchor)
    {
      if (StringHelper.getLastChar (aSB) != CURL.HASH)
        aSB.append (CURL.HASH);
      aSB.append (sAnchor);
    }
    // Avoid empty URLs
    if (aSB.length () == 0)
      return CURL.QUESTIONMARK_STR;

    return aSB.toString ();
  }

  /**
   * Get the passed String as an URL. If the string is empty or not an URL <code>null</code> is
   * returned.
   *
   * @param sURL
   *        Source URL. May be <code>null</code>.
   * @param bWhine
   *        <code>true</code> to debug log if conversion failed
   * @return <code>null</code> if the passed URL is empty or invalid.
   */
  @Nullable
  public static URL getAsURL (@Nullable final String sURL, final boolean bWhine)
  {
    if (StringHelper.isNotEmpty (sURL))
      try
      {
        return new URL (sURL);
      }
      catch (final MalformedURLException ex)
      {
        // fall-through
        if (bWhine && GlobalDebug.isDebugMode ())
          LOGGER.warn ("Debug warn: failed to convert '" + sURL + "' to a URL!");
      }
    return null;
  }

  /**
   * Get the passed String as an URL. If the string is empty or not an URL <code>null</code> is
   * returned.
   *
   * @param sURL
   *        Source URL. May be <code>null</code>.
   * @return <code>null</code> if the passed URL is empty or invalid.
   */
  @Nullable
  public static URL getAsURL (@Nullable final String sURL)
  {
    return getAsURL (sURL, true);
  }

  /**
   * Get the passed URI as an URL. If the URI is null or cannot be converted to an URL
   * <code>null</code> is returned.
   *
   * @param aURI
   *        Source URI. May be <code>null</code>.
   * @return <code>null</code> if the passed URI is null or cannot be converted to an URL.
   */
  @Nullable
  public static URL getAsURL (@Nullable final URI aURI)
  {
    if (aURI != null)
      try
      {
        return aURI.toURL ();
      }
      catch (final MalformedURLException ex)
      {
        // fall-through
        if (GlobalDebug.isDebugMode ())
          LOGGER.warn ("Debug warn: failed to convert '" + aURI + "' to a URL!");
      }
    return null;
  }

  /**
   * Get the passed String as an URI. If the string is empty or not an URI <code>null</code> is
   * returned.
   *
   * @param sURI
   *        Source URI. May be <code>null</code>.
   * @return <code>null</code> if the passed URI is empty or invalid.
   */
  @Nullable
  public static URI getAsURI (@Nullable final String sURI)
  {
    if (StringHelper.isNotEmpty (sURI))
      try
      {
        return new URI (sURI);
      }
      catch (final URISyntaxException ex)
      {
        // fall-through
        if (GlobalDebug.isDebugMode ())
          LOGGER.warn ("Debug warn: failed to convert '" + sURI + "' to a URI!");
      }
    return null;
  }

  /**
   * Get the passed URL as an URI. If the URL is null or not an URI <code>null</code> is returned.
   *
   * @param aURL
   *        Source URL. May be <code>null</code>.
   * @return <code>null</code> if the passed URL is empty or invalid.
   */
  @Nullable
  public static URI getAsURI (@Nullable final URL aURL)
  {
    if (aURL != null)
      try
      {
        return aURL.toURI ();
      }
      catch (final URISyntaxException ex)
      {
        // fall-through
        if (GlobalDebug.isDebugMode ())
          LOGGER.warn ("Debug warn: failed to convert '" + aURL + "' to a URI!");
      }
    return null;
  }

  private static boolean _isTimeout (final IOException ex)
  {
    return ex instanceof SocketTimeoutException;
  }

  /**
   * Get an input stream from the specified URL. By default caching is disabled. This method only
   * handles GET requests - POST requests are not possible.
   *
   * @param aURL
   *        The URL to use. May not be <code>null</code>.
   * @param nConnectTimeoutMS
   *        Connect timeout milliseconds. 0 == infinite. &lt; 0: ignored.
   * @param nReadTimeoutMS
   *        Read timeout milliseconds. 0 == infinite. &lt; 0: ignored.
   * @param aConnectionModifier
   *        An optional callback object to modify the URLConnection before it is opened.
   * @param aExceptionHolder
   *        An optional exception holder for further outside investigation.
   * @return <code>null</code> if the input stream could not be opened.
   */
  @Nullable
  public static InputStream getInputStream (@Nonnull final URL aURL,
                                            @CheckForSigned final int nConnectTimeoutMS,
                                            @CheckForSigned final int nReadTimeoutMS,
                                            @Nullable final Consumer <? super URLConnection> aConnectionModifier,
                                            @Nullable final IMutableWrapper <IOException> aExceptionHolder)
  {
    ValueEnforcer.notNull (aURL, "URL");

    if (DEBUG_GET_IS)
      LOGGER.info ("getInputStream ('" +
                   aURL +
                   "', " +
                   nConnectTimeoutMS +
                   ", " +
                   nReadTimeoutMS +
                   ", " +
                   aConnectionModifier +
                   ", " +
                   aExceptionHolder +
                   ")",
                   new Exception ());

    URLConnection aConnection;
    HttpURLConnection aHTTPConnection = null;
    try
    {
      aConnection = aURL.openConnection ();
      if (nConnectTimeoutMS >= 0)
        aConnection.setConnectTimeout (nConnectTimeoutMS);
      if (nReadTimeoutMS >= 0)
        aConnection.setReadTimeout (nReadTimeoutMS);
      if (aConnection instanceof final HttpURLConnection aHUC)
        aHTTPConnection = aHUC;

      // Disable caching
      aConnection.setUseCaches (false);

      // Apply optional callback
      if (aConnectionModifier != null)
        aConnectionModifier.accept (aConnection);
      if (aConnection instanceof final JarURLConnection aJUC)
      {
        final JarEntry aJarEntry = aJUC.getJarEntry ();
        if (aJarEntry != null)
        {
          // Directories are identified by ending with a "/"
          if (aJarEntry.isDirectory ())
          {
            // Cannot open an InputStream on a directory
            return null;
          }
          // Heuristics for directories not ending with a "/"
          if (aJarEntry.getSize () == 0 && aJarEntry.getCrc () == 0)
          {
            // Cannot open an InputStream on a directory
            LOGGER.warn ("Heuristically determined " + aURL + " to be a directory!");
            return null;
          }
        }
      }
      // by default follow-redirects is true for HTTPUrlConnections
      final InputStream ret = aConnection.getInputStream ();

      if (DEBUG_GET_IS)
        LOGGER.info ("  returning " + ret);

      return ret;
    }
    catch (final IOException ex)
    {
      if (aExceptionHolder != null)
      {
        // Remember the exception
        aExceptionHolder.set (ex);
      }
      else
      {
        if (_isTimeout (ex))
        {
          LOGGER.warn ("Timeout to open input stream for '" +
                       aURL +
                       "': " +
                       ex.getClass ().getName () +
                       " - " +
                       ex.getMessage ());
        }
        else
        {
          LOGGER.warn ("Failed to open input stream for '" +
                       aURL +
                       "': " +
                       ex.getClass ().getName () +
                       " - " +
                       ex.getMessage ());
        }
      }
      if (aHTTPConnection != null)
      {
        // Read error completely for keep-alive (see
        // http://docs.oracle.com/javase/6/docs/technotes/guides/net/http-keepalive.html)
        InputStream aErrorIS = null;
        try
        {
          aErrorIS = aHTTPConnection.getErrorStream ();
          if (aErrorIS != null)
          {
            final byte [] aBuf = new byte [1024];
            // read the response body
            while (aErrorIS.read (aBuf) > 0)
            {
              // Read next
            }
          }
        }
        catch (final IOException ex2)
        {
          // deal with the exception
          LOGGER.warn ("Failed to consume error stream for '" +
                       aURL +
                       "': " +
                       ex2.getClass ().getName () +
                       " - " +
                       ex2.getMessage ());
        }
        finally
        {
          StreamHelper.close (aErrorIS);
        }
      }
    }
    return null;
  }

  @Nonnull
  public static File getAsFile (@Nonnull final URL aURL)
  {
    ValueEnforcer.notNull (aURL, "URL");
    ValueEnforcer.isEqual (CURL.PROTOCOL_FILE, aURL.getProtocol (), () -> "Not a file URL: " + aURL.toExternalForm ());

    String sPath;
    File aFile;
    try
    {
      sPath = aURL.toURI ().getSchemeSpecificPart ();
      aFile = new File (sPath);
    }
    catch (final URISyntaxException ex)
    {
      // Fallback for URLs that are not valid URIs
      sPath = aURL.getPath ();
      aFile = new File (sPath);
    }
    // In case the URL starts with a slash, make it absolute
    if (sPath.length () > 0 && (sPath.charAt (0) == '/' || sPath.charAt (0) == '\\'))
      aFile = aFile.getAbsoluteFile ();

    // This file may be non-existing
    return aFile;
  }

  @Nullable
  public static File getAsFileOrNull (@Nullable final URL aURL)
  {
    if (aURL != null)
      try
      {
        return getAsFile (aURL);
      }
      catch (final IllegalArgumentException ex)
      {
        // Happens for non-file URLs
      }
    return null;
  }

  /**
   * Get the URL for the specified path using automatic class loader handling. The class loaders are
   * iterated in the following order:
   * <ol>
   * <li>Default class loader (usually the context class loader)</li>
   * <li>The class loader of this class</li>
   * <li>The system class loader</li>
   * </ol>
   *
   * @param sPath
   *        The path to be resolved. May neither be <code>null</code> nor empty.
   * @return <code>null</code> if the path could not be resolved.
   */
  @Nullable
  public static URL getClassPathURL (@Nonnull @Nonempty final String sPath)
  {
    ValueEnforcer.notEmpty (sPath, "Path");

    // Use the default class loader. Returns null if not found
    URL ret = ClassLoaderHelper.getResource (ClassLoaderHelper.getDefaultClassLoader (), sPath);
    if (ret == null)
    {
      // This is essential if we're running as a web application!!!
      ret = ClassHelper.getResource (URLHelper.class, sPath);
      if (ret == null)
      {
        // this is a fix for a user that needed to have the application
        // loaded by the bootstrap class loader
        ret = ClassLoaderHelper.getResource (ClassLoaderHelper.getSystemClassLoader (), sPath);
      }
    }
    return ret;
  }

  public static boolean isClassPathURLExisting (@Nonnull @Nonempty final String sPath)
  {
    return getClassPathURL (sPath) != null;
  }

  public static boolean isClassPathURLExisting (@Nonnull @Nonempty final String sPath,
                                                @Nonnull final ClassLoader aClassLoader)
  {
    return ClassLoaderHelper.getResource (aClassLoader, sPath) != null;
  }
}
