/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
import java.util.BitSet;
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
import com.helger.commons.codec.IDecoder;
import com.helger.commons.codec.IEncoder;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.impl.CommonsLinkedHashMap;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsOrderedMap;
import com.helger.commons.debug.GlobalDebug;
import com.helger.commons.io.file.FilenameHelper;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.io.stream.NonBlockingCharArrayWriter;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.lang.ClassHelper;
import com.helger.commons.lang.ClassLoaderHelper;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.StringParser;
import com.helger.commons.wrapper.IMutableWrapper;

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

  private static final Logger s_aLogger = LoggerFactory.getLogger (URLHelper.class);

  private static char [] s_aCleanURLOld;
  private static char [] [] s_aCleanURLNew;

  private static final boolean DEBUG_GET_IS = false;

  private static final BitSet NO_URL_ENCODE = new BitSet (256);

  // Must be upper case for URL encode!
  private static final char [] URL_ENCODE_CHARS = "0123456789ABCDEF".toCharArray ();

  static
  {
    /**
     * <pre>
     * The list of characters that are not encoded has been
     * determined as follows:
     *
     * RFC 2396 states:
     * -----
     * Data characters that are allowed in a URI but do not have a
     * reserved purpose are called unreserved.  These include upper
     * and lower case letters, decimal digits, and a limited set of
     * punctuation marks and symbols.
     *
     * unreserved  = alphanum | mark
     *
     * mark        = "-" | "_" | "." | "!" | "~" | "*" | "'" | "(" | ")"
     *
     * Unreserved characters can be escaped without changing the
     * semantics of the URI, but this should not be done unless the
     * URI is being used in a context that does not allow the
     * unescaped character to appear.
     * -----
     *
     * It appears that both Netscape and Internet Explorer escape
     * all special characters from this list with the exception
     * of "-", "_", ".", "*". While it is not clear why they are
     * escaping the other characters, perhaps it is safest to
     * assume that there might be contexts in which the others
     * are unsafe if not escaped. Therefore, we will use the same
     * list. It is also noteworthy that this is consistent with
     * O'Reilly's "HTML: The Definitive Guide" (page 164).
     *
     * As a last note, Internet Explorer does not encode the "@"
     * character which is clearly not unreserved according to the
     * RFC. We are being consistent with the RFC in this matter,
     * as is Netscape.
     * </pre>
     */
    for (int i = 'a'; i <= 'z'; i++)
      NO_URL_ENCODE.set (i);
    for (int i = 'A'; i <= 'Z'; i++)
      NO_URL_ENCODE.set (i);
    for (int i = '0'; i <= '9'; i++)
      NO_URL_ENCODE.set (i);
    /*
     * encoding a space to a + is done in the encode() method
     */
    NO_URL_ENCODE.set (' ');
    NO_URL_ENCODE.set ('-');
    NO_URL_ENCODE.set ('_');
    NO_URL_ENCODE.set ('.');
    NO_URL_ENCODE.set ('*');
  }

  @PresentForCodeCoverage
  private static final URLHelper s_aInstance = new URLHelper ();

  private URLHelper ()
  {}

  /**
   * URL-decode the passed value automatically handling charset issues. The used
   * char set is determined by {@link #CHARSET_URL_OBJ}.
   *
   * @param sValue
   *        The value to be decoded. May not be <code>null</code>.
   * @return The decoded value.
   */
  @Nonnull
  public static String urlDecode (@Nonnull final String sValue)
  {
    return urlDecode (sValue, CHARSET_URL_OBJ);
  }

  /**
   * URL-decode the passed value automatically handling charset issues. The
   * implementation is ripped from URLDecoder but does not throw an
   * UnsupportedEncodingException for nothing.
   *
   * @param sValue
   *        The value to be decoded. May not be <code>null</code>.
   * @param aCharset
   *        The charset to use. May not be <code>null</code>.
   * @return The decoded value.
   * @see URLDecoder#decode(String, String)
   */
  @Nonnull
  public static String urlDecode (@Nonnull final String sValue, @Nonnull final Charset aCharset)
  {
    ValueEnforcer.notNull (sValue, "Value");
    ValueEnforcer.notNull (aCharset, "Charset");

    boolean bNeedToChange = false;
    final int nLen = sValue.length ();
    final StringBuilder aSB = new StringBuilder (nLen);
    int nIndex = 0;
    byte [] aBytes = null;
    while (nIndex < nLen)
    {
      char c = sValue.charAt (nIndex);
      switch (c)
      {
        case '+':
          aSB.append (' ');
          nIndex++;
          bNeedToChange = true;
          break;
        case '%':
          /*
           * Starting with this instance of %, process all consecutive
           * substrings of the form %xy. Each substring %xy will yield a byte.
           * Convert all consecutive bytes obtained this way to whatever
           * character(s) they represent in the provided encoding.
           */
          try
          {
            // (numChars-i)/3 is an upper bound for the number
            // of remaining bytes
            if (aBytes == null)
              aBytes = new byte [(nLen - nIndex) / 3];
            int nPos = 0;

            while ((nIndex + 2) < nLen && c == '%')
            {
              final int nValue = Integer.parseInt (sValue.substring (nIndex + 1, nIndex + 3), 16);
              if (nValue < 0)
                throw new IllegalArgumentException ("URLDecoder: Illegal hex characters in escape (%) pattern - negative value");
              aBytes[nPos++] = (byte) nValue;
              nIndex += 3;
              if (nIndex < nLen)
                c = sValue.charAt (nIndex);
            }

            // A trailing, incomplete byte encoding such as
            // "%x" will cause an exception to be thrown
            if (nIndex < nLen && c == '%')
              throw new IllegalArgumentException ("URLDecoder: Incomplete trailing escape (%) pattern");

            aSB.append (StringHelper.decodeBytesToChars (aBytes, 0, nPos, aCharset));
          }
          catch (final NumberFormatException e)
          {
            throw new IllegalArgumentException ("URLDecoder: Illegal hex characters in escape (%) pattern - " +
                                                e.getMessage ());
          }
          bNeedToChange = true;
          break;
        default:
          aSB.append (c);
          nIndex++;
          break;
      }
    }

    return bNeedToChange ? aSB.toString () : sValue;
  }

  /**
   * URL-encode the passed value automatically handling charset issues. The used
   * char set is determined by {@link #CHARSET_URL_OBJ}.
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
   * URL-encode the passed value automatically handling charset issues. This is
   * a ripped, optimized version of URLEncoder.encode but without the
   * UnsupportedEncodingException.
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
    ValueEnforcer.notNull (aCharset, "Charset");

    final int nLen = sValue.length ();
    boolean bChanged = false;
    final StringBuilder aSB = new StringBuilder (nLen * 2);
    NonBlockingCharArrayWriter aCAW = null;

    final char [] aSrcChars = sValue.toCharArray ();
    int nIndex = 0;
    while (nIndex < nLen)
    {
      char c = aSrcChars[nIndex];
      if (NO_URL_ENCODE.get (c))
      {
        if (c == ' ')
        {
          c = '+';
          bChanged = true;
        }
        aSB.append (c);
        nIndex++;
      }
      else
      {
        // convert to external encoding before hex conversion
        if (aCAW == null)
          aCAW = new NonBlockingCharArrayWriter ();
        else
          aCAW.reset ();

        while (true)
        {
          aCAW.write (c);
          /*
           * If this character represents the start of a Unicode surrogate pair,
           * then pass in two characters. It's not clear what should be done if
           * a bytes reserved in the surrogate pairs range occurs outside of a
           * legal surrogate pair. For now, just treat it as if it were any
           * other character.
           */
          if (Character.isHighSurrogate (c) && nIndex + 1 < nLen)
          {
            final char d = aSrcChars[nIndex + 1];
            if (Character.isLowSurrogate (d))
            {
              aCAW.write (d);
              nIndex++;
            }
          }
          nIndex++;
          if (nIndex >= nLen)
            break;

          // Try next char
          c = aSrcChars[nIndex];
          if (NO_URL_ENCODE.get (c))
            break;
        }

        final byte [] aEncodedBytes = aCAW.toByteArray (aCharset);
        for (final byte nEncByte : aEncodedBytes)
        {
          aSB.append ('%').append (URL_ENCODE_CHARS[(nEncByte >> 4) & 0xF]).append (URL_ENCODE_CHARS[nEncByte & 0xF]);
        }
        bChanged = true;
      }
    }

    return bChanged ? aSB.toString () : sValue;
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
    StreamHelper.readStreamLines (ClassPathResource.getInputStream ("codelists/cleanurl-data.dat"),
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
   * @return the corresponding {@link ISimpleURL} representation of the passed
   *         URL
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
          if (s_aLogger.isWarnEnabled ())
            s_aLogger.warn ("java.net.URL claims URL '" + sRealHref + "' to be invalid: " + ex.getMessage ());
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
      if (StringHelper.hasText (sQueryString))
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
    if (StringHelper.hasText (sQueryString))
    {
      for (final String sKeyValuePair : StringHelper.getExploded (AMPERSAND, sQueryString))
        if (sKeyValuePair.length () > 0)
        {
          final ICommonsList <String> aParts = StringHelper.getExploded (EQUALS, sKeyValuePair, 2);
          final String sKey = aParts.get (0);
          // Maybe empty when passing something like "url?=value"
          if (StringHelper.hasText (sKey))
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

    if (GlobalDebug.isDebugMode ())
    {
      // Consistency checks
      if (bHasPath)
      {
        if (sPath.contains (QUESTIONMARK_STR))
          if (s_aLogger.isWarnEnabled ())
            s_aLogger.warn ("Path contains the question mark ('?') character: '" + sPath + "'");
        if (sPath.contains (AMPERSAND_STR))
          if (s_aLogger.isWarnEnabled ())
            s_aLogger.warn ("Path contains the ampersand ('&') character: '" + sPath + "'");
        if (sPath.contains (HASH_STR))
          if (s_aLogger.isWarnEnabled ())
            s_aLogger.warn ("Path contains the hash ('#') character: '" + sPath + "'");
      }

      if (bHasQueryParams)
      {
        if (sQueryParams.contains (QUESTIONMARK_STR))
          if (s_aLogger.isWarnEnabled ())
            s_aLogger.warn ("Query parameters contain the question mark ('?') character: '" + sQueryParams + "'");
      }

      if (bHasAnchor)
      {
        if (sAnchor.contains (HASH_STR))
          if (s_aLogger.isWarnEnabled ())
            s_aLogger.warn ("Anchor contains the hash ('#') character: '" + sAnchor + "'");
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
   * Create a parameter string. This is also suitable for POST body (e.g. for
   * web form submission).
   *
   * @param aQueryParams
   *        Parameter map. May be <code>null</code> or empty.
   * @param aQueryParameterEncoder
   *        The encoder to be used to encode parameter names and parameter
   *        values. May be <code>null</code>. This may be e.g. a
   *        {@link URLParameterEncoder}.
   * @return <code>null</code> if no parameter is present.
   */
  @Nullable
  public static String getQueryParametersAsString (@Nullable final List <? extends URLParameter> aQueryParams,
                                                   @Nullable final IEncoder <String, String> aQueryParameterEncoder)
  {
    if (CollectionHelper.isEmpty (aQueryParams))
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
   *        The list of query parameters to be appended. May be
   *        <code>null</code> .
   * @param sAnchor
   *        An optional anchor to be added. May be <code>null</code>.
   * @param aQueryParameterEncoder
   *        The parameters encoding to be used. May be <code>null</code>.
   * @return May be <code>null</code> if path, anchor and parameters are
   *         <code>null</code>.
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
   *        If not <code>null</code> the parameters are encoded using this
   *        charset.
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
   * Get the passed String as an URL. If the string is empty or not an URL
   * <code>null</code> is returned.
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
    if (StringHelper.hasText (sURL))
      try
      {
        return new URL (sURL);
      }
      catch (final MalformedURLException ex)
      {
        // fall-through
        if (bWhine && GlobalDebug.isDebugMode ())
          if (s_aLogger.isWarnEnabled ())
            s_aLogger.warn ("Debug warn: failed to convert '" + sURL + "' to a URL!");
      }
    return null;
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
    return getAsURL (sURL, true);
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
        if (GlobalDebug.isDebugMode ())
          if (s_aLogger.isWarnEnabled ())
            s_aLogger.warn ("Debug warn: failed to convert '" + aURI + "' to a URL!");
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
        if (GlobalDebug.isDebugMode ())
          if (s_aLogger.isWarnEnabled ())
            s_aLogger.warn ("Debug warn: failed to convert '" + sURI + "' to a URI!");
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
        if (GlobalDebug.isDebugMode ())
          if (s_aLogger.isWarnEnabled ())
            s_aLogger.warn ("Debug warn: failed to convert '" + aURL + "' to a URI!");
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
                                            @Nullable final Consumer <? super URLConnection> aConnectionModifier,
                                            @Nullable final IMutableWrapper <IOException> aExceptionHolder)
  {
    ValueEnforcer.notNull (aURL, "URL");

    if (DEBUG_GET_IS)
      if (s_aLogger.isInfoEnabled ())
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
            if (s_aLogger.isWarnEnabled ())
              s_aLogger.warn ("Heuristically determined " + aURL + " to be a directory!");
            return null;
          }
        }
      }

      // by default follow-redirects is true for HTTPUrlConnections
      final InputStream ret = aConnection.getInputStream ();

      if (DEBUG_GET_IS)
        if (s_aLogger.isInfoEnabled ())
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
        {
          if (s_aLogger.isWarnEnabled ())
            s_aLogger.warn ("Timeout to open input stream for '" +
                            aURL +
                            "': " +
                            ex.getClass ().getName () +
                            " - " +
                            ex.getMessage ());
        }
        else
        {
          if (s_aLogger.isWarnEnabled ())
            s_aLogger.warn ("Failed to open input stream for '" +
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
          if (s_aLogger.isWarnEnabled ())
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
