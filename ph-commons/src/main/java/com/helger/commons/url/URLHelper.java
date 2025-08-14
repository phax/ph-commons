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
package com.helger.commons.url;

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
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.jar.JarEntry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.CheckForSigned;
import com.helger.annotation.Nonempty;
import com.helger.annotation.RegEx;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.classloader.ClassLoaderHelper;
import com.helger.base.debug.GlobalDebug;
import com.helger.base.equals.EqualsHelper;
import com.helger.base.equals.ValueEnforcer;
import com.helger.base.io.stream.StreamHelper;
import com.helger.base.lang.clazz.ClassHelper;
import com.helger.base.string.StringReplace;
import com.helger.base.string.Strings;
import com.helger.base.wrapper.IMutableWrapper;
import com.helger.collection.commons.CommonsLinkedHashMap;
import com.helger.collection.commons.ICommonsList;
import com.helger.collection.commons.ICommonsOrderedMap;
import com.helger.collection.helper.CollectionHelperExt;
import com.helger.commons.codec.DecodeException;
import com.helger.commons.codec.IDecoder;
import com.helger.commons.codec.IEncoder;
import com.helger.commons.codec.URLCodec;
import com.helger.commons.io.file.FilenameHelper;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.io.stream.StreamHelperExt;
import com.helger.commons.regex.RegExHelper;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.StringParser;

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
  /** Default URL charset is UTF-8 */
  public static final Charset CHARSET_URL_OBJ = StandardCharsets.UTF_8;

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

  private static final Logger LOGGER = LoggerFactory.getLogger (URLHelper.class);

  private static char [] s_aCleanURLOld;
  private static char [] [] s_aCleanURLNew;

  private static final boolean DEBUG_GET_IS = false;

  private static final URLCodec URL_CODEC = new URLCodec ();

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
   * URL-decode the passed value automatically handling charset issues. The used char set is
   * determined by {@link #CHARSET_URL_OBJ}.
   *
   * @param sValue
   *        The value to be decoded. May not be <code>null</code>.
   * @return The decoded value.
   * @throws IllegalArgumentException
   *         if something goes wrong
   * @see #urlDecode(String, Charset)
   */
  @Nonnull
  public static String urlDecode (@Nonnull final String sValue)
  {
    return urlDecode (sValue, CHARSET_URL_OBJ);
  }

  /**
   * URL-decode the passed value automatically handling charset issues. The implementation uses
   * {@link URLCodec} to do the hard work.
   *
   * @param sValue
   *        The value to be decoded. May not be <code>null</code>.
   * @param aCharset
   *        The charset to use. May not be <code>null</code>.
   * @return The decoded value.
   * @throws IllegalArgumentException
   *         if something goes wrong
   * @see URLDecoder#decode(String, String)
   */
  @Nonnull
  public static String urlDecode (@Nonnull final String sValue, @Nonnull final Charset aCharset)
  {
    ValueEnforcer.notNull (sValue, "Value");
    try
    {
      return URL_CODEC.getDecodedAsString (sValue, aCharset);
    }
    catch (final DecodeException ex)
    {
      throw new IllegalArgumentException (ex);
    }
  }

  /**
   * URL-decode the passed value automatically handling charset issues. The used char set is
   * determined by {@link #CHARSET_URL_OBJ}.
   *
   * @param sValue
   *        The value to be decoded. May not be <code>null</code>.
   * @return The decoded value.
   * @see #urlDecode(String, Charset)
   * @since 9.4.1
   */
  @Nullable
  public static String urlDecodeOrNull (@Nonnull final String sValue)
  {
    return urlDecodeOrNull (sValue, CHARSET_URL_OBJ);
  }

  /**
   * URL-decode the passed value automatically handling charset issues. The implementation uses
   * {@link URLCodec} to do the hard work.
   *
   * @param sValue
   *        The value to be decoded. May be <code>null</code>.
   * @param aCharset
   *        The charset to use. May not be <code>null</code>.
   * @return The decoded value or <code>null</code>.
   * @see URLDecoder#decode(String, String)
   * @since 9.4.1
   */
  @Nullable
  public static String urlDecodeOrNull (@Nullable final String sValue, @Nonnull final Charset aCharset)
  {
    return urlDecodeOrDefault (sValue, aCharset, null);
  }

  /**
   * URL-decode the passed value automatically handling charset issues. The used char set is
   * determined by {@link #CHARSET_URL_OBJ}.
   *
   * @param sValue
   *        The value to be decoded. May not be <code>null</code>.
   * @param sDefault
   *        The default value to be returned if decoding fails.
   * @return The decoded value or the default.
   * @see #urlDecode(String, Charset)
   * @since 9.4.1
   */
  @Nullable
  public static String urlDecodeOrDefault (@Nonnull final String sValue, @Nullable final String sDefault)
  {
    return urlDecodeOrDefault (sValue, CHARSET_URL_OBJ, sDefault);
  }

  /**
   * URL-decode the passed value automatically handling charset issues. The implementation uses
   * {@link URLCodec} to do the hard work.
   *
   * @param sValue
   *        The value to be decoded. May be <code>null</code>.
   * @param aCharset
   *        The charset to use. May not be <code>null</code>.
   * @param sDefault
   *        The default value to be returned if decoding fails.
   * @return The decoded value or the default.
   * @see URLDecoder#decode(String, String)
   * @since 9.4.1
   */
  @Nullable
  public static String urlDecodeOrDefault (@Nullable final String sValue,
                                           @Nonnull final Charset aCharset,
                                           @Nullable final String sDefault)
  {
    if (sValue != null)
      try
      {
        return URL_CODEC.getDecodedAsString (sValue, aCharset);
      }
      catch (final DecodeException ex)
      {
        // Fall through
      }
    return sDefault;
  }

  /**
   * URL-encode the passed value automatically handling charset issues. The used char set is
   * determined by {@link #CHARSET_URL_OBJ}.
   *
   * @param sValue
   *        The value to be encoded. May not be <code>null</code>.
   * @return The encoded value.
   */
  @Nonnull
  public static String urlEncode (@Nonnull final String sValue)
  {
    return urlEncode (sValue, CHARSET_URL_OBJ);
  }

  /**
   * URL-encode the passed value automatically handling charset issues. This is a ripped, optimized
   * version of URLEncoder.encode but without the UnsupportedEncodingException.
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
    ValueEnforcer.notNull (sValue, "Value");
    return URL_CODEC.getEncodedAsString (sValue, aCharset);
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
    final ICommonsOrderedMap <String, String> aCleanURLMap = new CommonsLinkedHashMap <> ();
    StreamHelperExt.readStreamLines (ClassPathResource.getInputStream ("codelists/cleanurl-data.dat"),
                                     StandardCharsets.UTF_8,
                                     sLine -> {
                                       if (sLine.length () > 0 && sLine.charAt (0) == '"')
                                       {
                                         final String [] aParts = StringHelper.getExplodedArray ('=', sLine, 2);
                                         String sKey = StringHelper.trimStartAndEnd (aParts[0], '"');
                                         if (sKey.startsWith ("&#"))
                                         {
                                           // E.g. "&#12345;"
                                           sKey = StringHelper.trimStartAndEnd (sKey, "&#", ";");
                                           sKey = Character.toString ((char) StringParser.parseInt (sKey, -1));
                                         }
                                         final String sValue = StringHelper.trimStartAndEnd (aParts[1], '"');
                                         aCleanURLMap.put (sKey, sValue);
                                       }
                                     });
    // if (XMLMapHandler.readMap (new ClassPathResource
    // ("codelists/cleanurl-data.xml"), aCleanURLMap).isFailure ())
    // throw new InitializationException ("Failed to init CleanURL data!");

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
   * @return The cleaned version or <code>null</code> if the input was <code>null</code>.
   */
  @Nullable
  public static String getCleanURLPartWithoutUmlauts (@Nullable final String sURLPart)
  {
    if (s_aCleanURLOld == null)
      _initCleanURL ();
    final char [] ret = StringReplace.replaceMultiple (sURLPart, s_aCleanURLOld, s_aCleanURLNew);
    return new String (ret);
  }

  @Nonnull
  public static ISimpleURL getAsURLData (@Nonnull final String sHref)
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
   * @return the corresponding {@link ISimpleURL} representation of the passed URL
   */
  @Nonnull
  public static ISimpleURL getAsURLData (@Nonnull final String sHref,
                                         @Nullable final IDecoder <String, String> aParameterDecoder)
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
          LOGGER.warn ("java.net.URL claims URL '" + sRealHref + "' to be invalid: " + ex.getMessage ());
        }

    String sPath;
    URLParameterList aParams = null;
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
      if (Strings.isNotEmpty (sQueryString))
        aParams = getParsedQueryParameters (sQueryString, aParameterDecoder);

      sPath = sRemainingHref.substring (0, nQuestionIndex).trim ();
    }
    else
      sPath = sRemainingHref;
    return new URLData (sPath, aParams, sAnchor);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static URLParameterList getParsedQueryParameters (@Nullable final String sQueryString,
                                                           @Nullable final IDecoder <String, String> aParameterDecoder)
  {
    final URLParameterList aMap = new URLParameterList ();
    if (Strings.isNotEmpty (sQueryString))
    {
      for (final String sKeyValuePair : StringHelper.getExploded (AMPERSAND, sQueryString))
        if (sKeyValuePair.length () > 0)
        {
          final ICommonsList <String> aParts = StringHelper.getExploded (EQUALS, sKeyValuePair, 2);
          final String sKey = aParts.get (0);
          // Maybe empty when passing something like "url?=value"
          if (Strings.isNotEmpty (sKey))
          {
            final String sValue = aParts.size () == 2 ? aParts.get (1) : "";
            if (sValue == null)
              throw new IllegalArgumentException ("parameter value may not be null");
            if (aParameterDecoder != null)
            {
              // Now decode the name and the value
              aMap.add (aParameterDecoder.getDecoded (sKey), aParameterDecoder.getDecoded (sValue));
            }
            else
              aMap.add (sKey, sValue);
          }
        }
    }
    return aMap;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static URLParameterList getParsedQueryParameters (@Nullable final String sQueryString)
  {
    return getParsedQueryParameters (sQueryString, null);
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
    final boolean bHasPath = Strings.isNotEmpty (sPath);
    final boolean bHasQueryParams = Strings.isNotEmpty (sQueryParams);
    final boolean bHasAnchor = Strings.isNotEmpty (sAnchor);
    if (GlobalDebug.isDebugMode ())
    {
      // Consistency checks
      if (bHasPath)
      {
        if (sPath.contains (QUESTIONMARK_STR))
          LOGGER.warn ("Path contains the question mark ('?') character: '" + sPath + "'");
        if (sPath.contains (AMPERSAND_STR))
          LOGGER.warn ("Path contains the ampersand ('&') character: '" + sPath + "'");
        if (sPath.contains (HASH_STR))
          LOGGER.warn ("Path contains the hash ('#') character: '" + sPath + "'");
      }
      if (bHasQueryParams)
      {
        if (sQueryParams.contains (QUESTIONMARK_STR))
          LOGGER.warn ("Query parameters contain the question mark ('?') character: '" + sQueryParams + "'");
      }
      if (bHasAnchor)
      {
        if (sAnchor.contains (HASH_STR))
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
      if (StringHelper.getLastChar (aSB) != HASH)
        aSB.append (HASH);
      aSB.append (sAnchor);
    }
    // Avoid empty URLs
    if (aSB.length () == 0)
      return QUESTIONMARK_STR;

    return aSB.toString ();
  }

  /**
   * Create a parameter string. This is also suitable for POST body (e.g. for web form submission).
   *
   * @param aQueryParams
   *        Parameter map. May be <code>null</code> or empty.
   * @param aQueryParameterEncoder
   *        The encoder to be used to encode parameter names and parameter values. May be
   *        <code>null</code>. This may be e.g. a {@link URLParameterEncoder}.
   * @return <code>null</code> if no parameter is present.
   */
  @Nullable
  public static String getQueryParametersAsString (@Nullable final List <? extends URLParameter> aQueryParams,
                                                   @Nullable final IEncoder <String, String> aQueryParameterEncoder)
  {
    if (CollectionHelperExt.isEmpty (aQueryParams))
      return null;

    final StringBuilder aSB = new StringBuilder ();
    // add all values
    for (final URLParameter aParam : aQueryParams)
    {
      // Separator
      if (aSB.length () > 0)
        aSB.append (AMPERSAND);
      aParam.appendTo (aSB, aQueryParameterEncoder);
    }
    return aSB.toString ();
  }

  @Nonnull
  public static String getURLString (@Nonnull final ISimpleURL aURL, @Nullable final Charset aParameterCharset)
  {
    return getURLString (aURL.getPath (), aURL.params (), aURL.getAnchor (), aParameterCharset);
  }

  /**
   * Get the final representation of the URL using the specified elements.
   *
   * @param sPath
   *        The main path. May be <code>null</code>.
   * @param aQueryParams
   *        The list of query parameters to be appended. May be <code>null</code> .
   * @param sAnchor
   *        An optional anchor to be added. May be <code>null</code>.
   * @param aQueryParameterEncoder
   *        The parameters encoding to be used. May be <code>null</code>.
   * @return May be <code>null</code> if path, anchor and parameters are <code>null</code>.
   */
  @Nullable
  public static String getURLString (@Nullable final String sPath,
                                     @Nullable final List <? extends URLParameter> aQueryParams,
                                     @Nullable final String sAnchor,
                                     @Nullable final IEncoder <String, String> aQueryParameterEncoder)
  {
    return getURLString (sPath, getQueryParametersAsString (aQueryParams, aQueryParameterEncoder), sAnchor);
  }

  /**
   * Get the final representation of the URL using the specified elements.
   *
   * @param sPath
   *        The main path. May be <code>null</code>.
   * @param aQueryParams
   *        The list of parameters to be appended. May be <code>null</code>.
   * @param sAnchor
   *        An optional anchor to be added. May be <code>null</code>.
   * @param aParameterCharset
   *        If not <code>null</code> the parameters are encoded using this charset.
   * @return May be <code>null</code> if all parameters are <code>null</code>.
   */
  @Nullable
  public static String getURLString (@Nullable final String sPath,
                                     @Nullable final List <? extends URLParameter> aQueryParams,
                                     @Nullable final String sAnchor,
                                     @Nullable final Charset aParameterCharset)
  {
    final IEncoder <String, String> aQueryParameterEncoder = aParameterCharset == null ? null
                                                                                       : new URLParameterEncoder (aParameterCharset);
    return getURLString (sPath, getQueryParametersAsString (aQueryParams, aQueryParameterEncoder), sAnchor);
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
    if (Strings.isNotEmpty (sURL))
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
    if (Strings.isNotEmpty (sURI))
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
    ValueEnforcer.isEqual (PROTOCOL_FILE, aURL.getProtocol (), () -> "Not a file URL: " + aURL.toExternalForm ());

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

  @RegEx
  public static final String REGEX_URN = "^\\Qurn:\\E" +
                                         "[a-zA-Z0-9][a-zA-Z0-9-]{0,31}" +
                                         "\\Q:\\E" +
                                         "[a-zA-Z0-9()+,\\-.:=@;$_!*'%/?#]+" +
                                         "$";

  /**
   * Check if the provided string is valid according to RFC 2141. Leading and trailing spaces of the
   * value to check will result in a negative result.
   *
   * @param sURN
   *        the URN to be validated. May be <code>null</code>.
   * @return <code>true</code> if the provided URN is not empty and matches the regular expression
   *         {@link #REGEX_URN}.
   * @since 10.0.0
   */
  public static boolean isValidURN (@Nullable final String sURN)
  {
    if (Strings.isEmpty (sURN))
      return false;
    return RegExHelper.stringMatchesPattern (REGEX_URN, sURN);
  }
}
