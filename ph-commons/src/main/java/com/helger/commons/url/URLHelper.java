/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
package com.helger.commons.url;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.jar.JarEntry;

import javax.annotation.CheckForSigned;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.charset.CCharset;
import com.helger.commons.codec.IDecoder;
import com.helger.commons.codec.IEncoder;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.debug.GlobalDebug;
import com.helger.commons.exception.InitializationException;
import com.helger.commons.io.file.FilenameHelper;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.lang.ClassHelper;
import com.helger.commons.lang.ClassLoaderHelper;
import com.helger.commons.microdom.util.XMLMapHandler;
import com.helger.commons.mime.IMimeType;
import com.helger.commons.string.StringHelper;
import com.helger.commons.wrapper.IMutableWrapper;
import com.helger.commons.wrapper.Wrapper;

/**
 * URL utilities.
 *
 * @author Philip Helger
 */
@ThreadSafe
public final class URLHelper
{
  /** Default URL charset is UTF-8 */
  public static final String CHARSET_URL = CCharset.CHARSET_UTF_8;
  /** Default URL charset is UTF-8 */
  public static final Charset CHARSET_URL_OBJ = CCharset.CHARSET_UTF_8_OBJ;

  /** Separator before first param: ? */
  public static final char QUESTIONMARK = '?';
  public static final String QUESTIONMARK_STR = Character.toString (QUESTIONMARK);

  /** Separator between params: &amp; */
  public static final char AMPERSAND = '&';
  public static final String AMPERSAND_STR = Character.toString (AMPERSAND);

  /** Separator between param name and param value: = */
  public static final char EQUALS = '=';
  public static final String EQUALS_STR = Character.toString (EQUALS);

  /** Separator between URL path and anchor name: # */
  public static final char HASH = '#';
  public static final String HASH_STR = Character.toString (HASH);

  /** The protocol for file resources */
  public static final String PROTOCOL_FILE = "file";

  private static final Logger s_aLogger = LoggerFactory.getLogger (URLHelper.class);

  private static char [] s_aCleanURLOld;
  private static char [] [] s_aCleanURLNew;

  /** Internal debug logging flag */
  private static final boolean DEBUG_GET_IS = false;

  @PresentForCodeCoverage
  private static final URLHelper s_aInstance = new URLHelper ();

  private URLHelper ()
  {}

  /**
   * URL-decode the passed value automatically handling charset issues. The used
   * char set is determined by {@link #CHARSET_URL}.
   *
   * @param sValue
   *        The value to be decoded. May not be <code>null</code>.
   * @return The decoded value.
   */
  @Nonnull
  public static String urlDecode (@Nonnull final String sValue)
  {
    return urlDecode (sValue, CHARSET_URL);
  }

  /**
   * URL-decode the passed value automatically handling charset issues.
   *
   * @param sValue
   *        The value to be decoded. May not be <code>null</code>.
   * @param aCharset
   *        The charset to use. May not be <code>null</code>.
   * @return The decoded value.
   */
  @Nonnull
  public static String urlDecode (@Nonnull final String sValue, @Nonnull final Charset aCharset)
  {
    ValueEnforcer.notNull (aCharset, "Charset");

    return urlDecode (sValue, aCharset.name ());
  }

  /**
   * URL-decode the passed value automatically handling charset issues.
   *
   * @param sValue
   *        The value to be decoded. May not be <code>null</code>.
   * @param sCharset
   *        The charset to use. May not be <code>null</code>.
   * @return The decoded value.
   */
  @SuppressWarnings ("deprecation")
  @Nonnull
  public static String urlDecode (@Nonnull final String sValue, @Nonnull @Nonempty final String sCharset)
  {
    try
    {
      return URLDecoder.decode (sValue, sCharset);
    }
    catch (final UnsupportedEncodingException ex)
    {
      // Using the charset determined by the system property file.encoding
      return URLDecoder.decode (sValue);
    }
  }

  /**
   * URL-encode the passed value automatically handling charset issues. The used
   * char set is determined by {@link #CHARSET_URL}.
   *
   * @param sValue
   *        The value to be encoded. May not be <code>null</code>.
   * @return The encoded value.
   */
  @Nonnull
  public static String urlEncode (@Nonnull final String sValue)
  {
    return urlEncode (sValue, CHARSET_URL);
  }

  /**
   * URL-encode the passed value automatically handling charset issues.
   *
   * @param sValue
   *        The value to be encoded. May not be <code>null</code>.
   * @param aCharset
   *        The charset to use. May not be <code>null</code>.
   * @return The encoded value.
   */
  @Nonnull
  public static String urlEncode (@Nonnull final String sValue, @Nonnull final Charset aCharset)
  {
    ValueEnforcer.notNull (aCharset, "Charset");

    return urlEncode (sValue, aCharset.name ());
  }

  /**
   * URL-encode the passed value automatically handling charset issues.
   *
   * @param sValue
   *        The value to be encoded. May not be <code>null</code>.
   * @param sCharset
   *        The charset to use. May not be <code>null</code>.
   * @return The encoded value.
   */
  @SuppressWarnings ("deprecation")
  @Nonnull
  public static String urlEncode (@Nonnull final String sValue, @Nonnull @Nonempty final String sCharset)
  {
    try
    {
      return URLEncoder.encode (sValue, sCharset);
    }
    catch (final UnsupportedEncodingException ex)
    {
      // Using the charset determined by the system property file.encoding
      return URLEncoder.encode (sValue);
    }
  }

  private static void _initCleanURL ()
  {
    // This one cannot be in the static initializer of the class, because
    // ClassPathResource internally uses
    // URLUtils.getInputStream and this static initialization code of this class
    // can therefore not use ClasspathResource because it would create a
    // recursive dependency!
    // Ever trickier is the when running multiple threads for reading XML (e.g.
    // in the unit test) this code would wait forever in the static initializer
    // because XMLMapHandler internally also acquires an XML reader....
    final Map <String, String> aCleanURLMap = new HashMap <String, String> ();
    if (XMLMapHandler.readMap (new ClassPathResource ("codelists/cleanurl-data.xml"), aCleanURLMap).isFailure ())
      throw new InitializationException ("Failed to init CleanURL data!");

    s_aCleanURLOld = new char [aCleanURLMap.size ()];
    s_aCleanURLNew = new char [aCleanURLMap.size ()] [];

    // Convert to char array
    int i = 0;
    for (final Map.Entry <String, String> aEntry : aCleanURLMap.entrySet ())
    {
      final String sKey = aEntry.getKey ();
      if (sKey.length () != 1)
        throw new IllegalStateException ("Clean URL source character has an invalid length: " + sKey.length ());
      s_aCleanURLOld[i] = sKey.charAt (0);
      s_aCleanURLNew[i] = aEntry.getValue ().toCharArray ();
      ++i;
    }
  }

  /**
   * Clean an URL part from nasty Umlauts. This mapping needs extension!
   *
   * @param sURLPart
   *        The original URL part. May be <code>null</code>.
   * @return The cleaned version or <code>null</code> if the input was
   *         <code>null</code>.
   */
  @Nullable
  public static String getCleanURLPartWithoutUmlauts (@Nullable final String sURLPart)
  {
    if (s_aCleanURLOld == null)
      _initCleanURL ();
    final char [] ret = StringHelper.replaceMultiple (sURLPart, s_aCleanURLOld, s_aCleanURLNew);
    return new String (ret);
  }

  @Nonnull
  public static IURLData getAsURLData (@Nonnull final String sHref)
  {
    return getAsURLData (sHref, null);
  }

  /**
   * Parses the passed URL into a structured form
   *
   * @param sHref
   *        The URL to be parsed
   * @param aParameterDecoder
   *        The parameter decoder to use. May be <code>null</code>.
   * @return the corresponding {@link IURLData} representation of the passed URL
   */
  @Nonnull
  public static IURLData getAsURLData (@Nonnull final String sHref, @Nullable final IDecoder <String> aParameterDecoder)
  {
    ValueEnforcer.notNull (sHref, "Href");

    final String sRealHref = sHref.trim ();

    // Is it a protocol that does not allow for query parameters?
    final IURLProtocol eProtocol = URLProtocolRegistry.getInstance ().getProtocol (sRealHref);
    if (eProtocol != null && !eProtocol.allowsForQueryParameters ())
      return new URLData (sRealHref, null, null);

    if (GlobalDebug.isDebugMode ())
      if (eProtocol != null)
        try
        {
          new URL (sRealHref);
        }
        catch (final MalformedURLException ex)
        {
          s_aLogger.warn ("java.net.URL claims URL '" + sRealHref + "' to be invalid: " + ex.getMessage ());
        }

    String sPath;
    Map <String, String> aParams = null;
    String sAnchor;

    // First get the anchor out
    String sRemainingHref = sRealHref;
    final int nIndexAnchor = sRemainingHref.indexOf (HASH);
    if (nIndexAnchor >= 0)
    {
      // Extract anchor
      sAnchor = sRemainingHref.substring (nIndexAnchor + 1).trim ();
      sRemainingHref = sRemainingHref.substring (0, nIndexAnchor).trim ();
    }
    else
      sAnchor = null;

    // Find parameters
    final int nQuestionIndex = sRemainingHref.indexOf (QUESTIONMARK);
    if (nQuestionIndex >= 0)
    {
      // Use everything after the '?'
      final String sQueryString = sRemainingHref.substring (nQuestionIndex + 1).trim ();

      // Maybe empty, if the URL ends with a '?'
      if (StringHelper.hasText (sQueryString))
        aParams = getQueryStringAsMap (sQueryString, aParameterDecoder);

      sPath = sRemainingHref.substring (0, nQuestionIndex).trim ();
    }
    else
      sPath = sRemainingHref;

    return new URLData (sPath, aParams, sAnchor);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static Map <String, String> getQueryStringAsMap (@Nullable final String sQueryString,
                                                          @Nullable final IDecoder <String> aParameterDecoder)
  {
    final Map <String, String> aMap = new LinkedHashMap <String, String> ();
    if (StringHelper.hasText (sQueryString))
    {
      for (final String sKeyValuePair : StringHelper.getExploded (AMPERSAND, sQueryString))
        if (sKeyValuePair.length () > 0)
        {
          final List <String> aParts = StringHelper.getExploded (EQUALS, sKeyValuePair, 2);
          final String sKey = aParts.get (0);
          // Maybe empty when passing something like "url?=value"
          if (StringHelper.hasText (sKey))
          {
            final String sValue = aParts.size () == 2 ? aParts.get (1) : "";
            if (sValue == null)
              throw new NullPointerException ("parameter value may not be null");
            if (aParameterDecoder != null)
            {
              // Now decode the name and the value
              aMap.put (aParameterDecoder.getDecoded (sKey), aParameterDecoder.getDecoded (sValue));
            }
            else
              aMap.put (sKey, sValue);
          }
        }
    }
    return aMap;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static Map <String, String> getQueryStringAsMap (@Nullable final String sQueryString)
  {
    return getQueryStringAsMap (sQueryString, null);
  }

  @Nonnull
  public static String getURLString (@Nonnull final IURLData aURL, @Nullable final Charset aParameterCharset)
  {
    return getURLString (aURL.getPath (), aURL.getAllParams (), aURL.getAnchor (), aParameterCharset);
  }

  /**
   * Get the final representation of the URL using the specified elements.
   *
   * @param sPath
   *        The main path. May be <code>null</code>.
   * @param sQueryParams
   *        The set of all query parameters already concatenated with the
   *        correct characters (&amp; and =). May be <code>null</code>.
   * @param sAnchor
   *        An optional anchor to be added. May be <code>null</code>.
   * @return May be <code>null</code> if path, anchor and parameters are
   *         <code>null</code>.
   */
  @Nullable
  public static String getURLString (@Nullable final String sPath,
                                     @Nullable final String sQueryParams,
                                     @Nullable final String sAnchor)
  {
    final boolean bHasPath = StringHelper.hasText (sPath);
    final boolean bHasQueryParams = StringHelper.hasText (sQueryParams);
    final boolean bHasAnchor = StringHelper.hasText (sAnchor);

    // return URL as is?
    if (!bHasQueryParams && !bHasAnchor)
    {
      // Return URL as is (may be null)
      return sPath;
    }

    final StringBuilder aSB = new StringBuilder ();
    if (bHasPath)
    {
      aSB.append (sPath);
      if (sPath.contains (QUESTIONMARK_STR))
        s_aLogger.warn ("Path contains the question mark ('?') character: '" + sPath + "'");
      if (sPath.contains (AMPERSAND_STR))
        s_aLogger.warn ("Path contains the ampersand ('&') character: '" + sPath + "'");
      if (sPath.contains (HASH_STR))
        s_aLogger.warn ("Path contains the hash ('#') character: '" + sPath + "'");
    }

    if (bHasQueryParams)
    {
      if (sQueryParams.contains (QUESTIONMARK_STR))
        s_aLogger.warn ("Query parameters contain the question mark ('?') character: '" + sQueryParams + "'");

      final boolean bHasQuestionMark = aSB.indexOf (QUESTIONMARK_STR) >= 0;

      if (bHasQuestionMark)
      {
        // Only if the "?" is not the last char otherwise the base href already
        // contains a parameter!
        final char cLast = StringHelper.getLastChar (aSB);
        if (cLast != QUESTIONMARK && cLast != AMPERSAND)
          aSB.append (AMPERSAND);
      }
      else
      {
        // First parameter
        aSB.append (QUESTIONMARK);
      }

      // add all parameters
      aSB.append (sQueryParams);
    }

    // Append anchor
    if (bHasAnchor)
    {
      if (sAnchor.contains (HASH_STR))
        s_aLogger.warn ("Anchor contains the hash ('#') character: '" + sAnchor + "'");
      if (StringHelper.getLastChar (aSB) != HASH)
        aSB.append (HASH);
      aSB.append (sAnchor);
    }

    // Avoid empty URLs
    if (aSB.length () == 0)
      return QUESTIONMARK_STR;

    return aSB.toString ();
  }

  @Nullable
  public static String getQueryParametersAsString (@Nullable final Map <String, String> aQueryParams,
                                                   @Nullable final IEncoder <String> aQueryParameterEncoder)
  {
    if (CollectionHelper.isEmpty (aQueryParams))
      return null;

    final StringBuilder aSB = new StringBuilder ();
    // add all values
    for (final Map.Entry <String, String> aEntry : aQueryParams.entrySet ())
    {
      // Key
      final String sKey = aEntry.getKey ();
      if (aQueryParameterEncoder != null)
        aSB.append (aQueryParameterEncoder.getEncoded (sKey));
      else
        aSB.append (sKey);

      // Value
      final String sValue = aEntry.getValue ();
      if (StringHelper.hasText (sValue))
        if (aQueryParameterEncoder != null)
          aSB.append (EQUALS).append (aQueryParameterEncoder.getEncoded (sValue));
        else
          aSB.append (EQUALS).append (sValue);

      // Separator
      aSB.append (AMPERSAND);
    }

    // delete the last AMPERSAND
    aSB.deleteCharAt (aSB.length () - 1);
    return aSB.toString ();
  }

  /**
   * Get the final representation of the URL using the specified elements.
   *
   * @param sPath
   *        The main path. May be <code>null</code>.
   * @param aQueryParams
   *        The set of query parameters to be appended. May be <code>null</code>
   *        .
   * @param sAnchor
   *        An optional anchor to be added. May be <code>null</code>.
   * @param aQueryParameterEncoder
   *        The parameters encoding to be used. May be <code>null</code>.
   * @return May be <code>null</code> if path, anchor and parameters are
   *         <code>null</code>.
   */
  @Nullable
  public static String getURLString (@Nullable final String sPath,
                                     @Nullable final Map <String, String> aQueryParams,
                                     @Nullable final String sAnchor,
                                     @Nullable final IEncoder <String> aQueryParameterEncoder)
  {
    return getURLString (sPath, getQueryParametersAsString (aQueryParams, aQueryParameterEncoder), sAnchor);
  }

  /**
   * Get the final representation of the URL using the specified elements.
   *
   * @param sPath
   *        The main path. May be <code>null</code>.
   * @param aQueryParams
   *        The set of parameters to be appended. May be <code>null</code>.
   * @param sAnchor
   *        An optional anchor to be added. May be <code>null</code>.
   * @param aParameterCharset
   *        If not <code>null</code> the parameters are encoded using this
   *        charset.
   * @return May be <code>null</code> if all parameters are <code>null</code>.
   */
  @Nullable
  public static String getURLString (@Nullable final String sPath,
                                     @Nullable final Map <String, String> aQueryParams,
                                     @Nullable final String sAnchor,
                                     @Nullable final Charset aParameterCharset)
  {
    final IEncoder <String> aQueryParameterEncoder = aParameterCharset == null ? null
                                                                               : new URLParameterEncoder (aParameterCharset);
    return getURLString (sPath, getQueryParametersAsString (aQueryParams, aQueryParameterEncoder), sAnchor);
  }

  /**
   * Get the passed String as an URL. If the string is empty or not an URL
   * <code>null</code> is returned.
   *
   * @param sURL
   *        Source URL. May be <code>null</code>.
   * @return <code>null</code> if the passed URL is empty or invalid.
   */
  @Nullable
  public static URL getAsURL (@Nullable final String sURL)
  {
    if (StringHelper.hasText (sURL))
      try
      {
        return new URL (sURL);
      }
      catch (final MalformedURLException ex)
      {
        // fall-through
      }
    return null;
  }

  /**
   * Get the passed URI as an URL. If the URI is null or cannot be converted to
   * an URL <code>null</code> is returned.
   *
   * @param aURI
   *        Source URI. May be <code>null</code>.
   * @return <code>null</code> if the passed URI is null or cannot be converted
   *         to an URL.
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
      }
    return null;
  }

  /**
   * Get the passed String as an URI. If the string is empty or not an URI
   * <code>null</code> is returned.
   *
   * @param sURI
   *        Source URI. May be <code>null</code>.
   * @return <code>null</code> if the passed URI is empty or invalid.
   */
  @Nullable
  public static URI getAsURI (@Nullable final String sURI)
  {
    if (StringHelper.hasText (sURI))
      try
      {
        return new URI (sURI);
      }
      catch (final URISyntaxException ex)
      {
        // fall-through
      }
    return null;
  }

  /**
   * Get the passed URL as an URI. If the URL is null or not an URI
   * <code>null</code> is returned.
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
      }
    return null;
  }

  /**
   * Get an input stream from the specified URL. By default caching is disabled.
   * This method only handles GET requests - POST requests are not possible.
   *
   * @param aURL
   *        The URL to use. May not be <code>null</code>.
   * @param nConnectTimeoutMS
   *        Connect timeout milliseconds. 0 == infinite. &lt; 0: ignored.
   * @param nReadTimeoutMS
   *        Read timeout milliseconds. 0 == infinite. &lt; 0: ignored.
   * @param aConnectionModifier
   *        An optional callback object to modify the URLConnection before it is
   *        opened.
   * @param aExceptionHolder
   *        An optional exception holder for further outside investigation.
   * @return <code>null</code> if the input stream could not be opened.
   */
  @Nullable
  public static InputStream getInputStream (@Nonnull final URL aURL,
                                            @CheckForSigned final int nConnectTimeoutMS,
                                            @CheckForSigned final int nReadTimeoutMS,
                                            @Nullable final Consumer <URLConnection> aConnectionModifier,
                                            @Nullable final IMutableWrapper <IOException> aExceptionHolder)
  {
    ValueEnforcer.notNull (aURL, "URL");

    if (DEBUG_GET_IS)
      s_aLogger.info ("getInputStream ('" +
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
      if (aConnection instanceof HttpURLConnection)
        aHTTPConnection = (HttpURLConnection) aConnection;

      // Disable caching
      aConnection.setUseCaches (false);

      // Apply optional callback
      if (aConnectionModifier != null)
        aConnectionModifier.accept (aConnection);

      if (aConnection instanceof JarURLConnection)
      {
        final JarEntry aJarEntry = ((JarURLConnection) aConnection).getJarEntry ();
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
            s_aLogger.warn ("Heuristically determined " + aURL + " to be a directory!");
            return null;
          }
        }
      }

      // by default follow-redirects is true for HTTPUrlConnections
      final InputStream ret = aConnection.getInputStream ();

      if (DEBUG_GET_IS)
        s_aLogger.info ("  returning " + ret);

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
        if (ex instanceof SocketTimeoutException)
          s_aLogger.warn ("Timeout to open input stream for '" +
                          aURL +
                          "': " +
                          ex.getClass ().getName () +
                          " - " +
                          ex.getMessage ());
        else
          s_aLogger.warn ("Failed to open input stream for '" +
                          aURL +
                          "': " +
                          ex.getClass ().getName () +
                          " - " +
                          ex.getMessage ());
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
          s_aLogger.warn ("Failed to consume error stream for '" +
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

  /**
   * POST something to a URL.
   *
   * @param aURL
   *        The destination URL. May not be <code>null</code>.
   * @param nConnectTimeoutMS
   *        Connect timeout milliseconds. 0 == infinite. &lt; 0: ignored.
   * @param nReadTimeoutMS
   *        Read timeout milliseconds. 0 == infinite. &lt; 0: ignored.
   * @param aContentType
   *        The MIME type to be send as the <code>Content-Type</code> header.
   *        May be <code>null</code>.
   * @param aContentBytes
   *        The main content to be send via POST. May not be <code>null</code>
   *        but maybe empty. The <code>Content-Length</code> HTTP header is
   *        automatically filled with the specified byte length.
   * @param aAdditionalHTTPHeaders
   *        An optional map of HTTP headers to be send. This map should not
   *        contain the <code>Content-Type</code> and the
   *        <code>Content-Length</code> headers. May be <code>null</code>.
   * @param aConnectionModifier
   *        An optional callback object to modify the URLConnection before it is
   *        opened.
   * @param aExceptionHolder
   *        An optional exception holder for further outside investigation.
   * @return <code>null</code> if the input stream could not be opened.
   * @deprecated Use Apache http client or the like for a much better handling
   */
  @Nullable
  @Deprecated
  public static InputStream postAndGetInputStream (@Nonnull final URL aURL,
                                                   final int nConnectTimeoutMS,
                                                   final int nReadTimeoutMS,
                                                   @Nullable final IMimeType aContentType,
                                                   @Nonnull final byte [] aContentBytes,
                                                   @Nullable final Map <String, String> aAdditionalHTTPHeaders,
                                                   @Nullable final Consumer <URLConnection> aConnectionModifier,
                                                   @Nullable final IMutableWrapper <IOException> aExceptionHolder)
  {
    ValueEnforcer.notNull (aURL, "URL");
    ValueEnforcer.notNull (aContentBytes, "ContentBytes");

    final Wrapper <OutputStream> aOpenedOS = new Wrapper <OutputStream> ();
    final Consumer <URLConnection> aPOSTModifier = aURLConnection -> {
      final HttpURLConnection aHTTPURLConnection = (HttpURLConnection) aURLConnection;
      try
      {
        aHTTPURLConnection.setRequestMethod ("POST");
        aHTTPURLConnection.setDoInput (true);
        aHTTPURLConnection.setDoOutput (true);
        if (aContentType != null)
          aHTTPURLConnection.setRequestProperty ("Content-Type", aContentType.getAsString ());
        aHTTPURLConnection.setRequestProperty ("Content-Length", Integer.toString (aContentBytes.length));
        if (aAdditionalHTTPHeaders != null)
          for (final Map.Entry <String, String> aEntry : aAdditionalHTTPHeaders.entrySet ())
            aHTTPURLConnection.setRequestProperty (aEntry.getKey (), aEntry.getValue ());

        final OutputStream aOS = aHTTPURLConnection.getOutputStream ();
        aOpenedOS.set (aOS);
        aOS.write (aContentBytes);
        aOS.flush ();
      }
      catch (final IOException ex)
      {
        throw new IllegalStateException ("Failed to POST data to " + aURL.toExternalForm (), ex);
      }

      // Run provided modifier (if any)
      if (aConnectionModifier != null)
        aConnectionModifier.accept (aURLConnection);
    };

    try
    {
      return getInputStream (aURL, nConnectTimeoutMS, nReadTimeoutMS, aPOSTModifier, aExceptionHolder);
    }
    finally
    {
      // Close the OutputStream opened for POSTing
      StreamHelper.close (aOpenedOS.get ());
    }
  }

  /**
   * Create a parameter string suitable for POST body (e.g. for web form
   * submission).
   *
   * @param aParams
   *        Parameter map. May be <code>null</code> or empty.
   * @param aParameterEncoder
   *        The encoder to be used to encode parameter names and parameter
   *        values. May be <code>null</code>. This may be e.g. a
   *        {@link URLParameterEncoder}.
   * @return A non-<code>null</code> string
   */
  @Nonnull
  public static String getApplicationFormEncoded (@Nullable final Map <String, String> aParams,
                                                  @Nullable final IEncoder <String> aParameterEncoder)
  {
    if (CollectionHelper.isEmpty (aParams))
      return "";

    final StringBuilder aSB = new StringBuilder ();
    if (aParams != null)
      for (final Map.Entry <String, String> aEntry : aParams.entrySet ())
      {
        // Separator
        if (aSB.length () > 0)
          aSB.append (AMPERSAND);

        // Key
        final String sKey = aEntry.getKey ();
        if (aParameterEncoder != null)
          aSB.append (aParameterEncoder.getEncoded (sKey));
        else
          aSB.append (sKey);

        // Value
        final String sValue = aEntry.getValue ();
        if (StringHelper.hasText (sValue))
          if (aParameterEncoder != null)
            aSB.append (EQUALS).append (aParameterEncoder.getEncoded (sValue));
          else
            aSB.append (EQUALS).append (sValue);
      }
    return aSB.toString ();
  }

  @Nonnull
  public static File getAsFile (@Nonnull final URL aURL)
  {
    ValueEnforcer.notNull (aURL, "URL");
    if (!PROTOCOL_FILE.equals (aURL.getProtocol ()))
      throw new IllegalArgumentException ("Not a file URL: " + aURL.toExternalForm ());

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
    if (FilenameHelper.startsWithPathSeparatorChar (sPath))
      aFile = aFile.getAbsoluteFile ();

    // This file may be non-existing
    return aFile;
  }

  @Nullable
  public static File getAsFileOrNull (@Nonnull final URL aURL)
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
   * Get the URL for the specified path using automatic class loader handling.
   * The class loaders are iterated in the following order:
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

  /**
   * Get the input stream of the passed resource using the specified class
   * loader only.
   *
   * @param sPath
   *        The path to be resolved. May neither be <code>null</code> nor empty.
   * @param aClassLoader
   *        The class loader to be used. May not be <code>null</code>.
   * @return <code>null</code> if the path could not be resolved using the
   *         specified class loader.
   */
  @Nullable
  @Deprecated
  public static URL getClassPathURL (@Nonnull @Nonempty final String sPath, @Nonnull final ClassLoader aClassLoader)
  {
    return ClassLoaderHelper.getResource (aClassLoader, sPath);
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
