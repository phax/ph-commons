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
package com.helger.url;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.codec.IDecoder;
import com.helger.base.codec.IEncoder;
import com.helger.base.debug.GlobalDebug;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.string.StringHelper;
import com.helger.base.url.CURL;
import com.helger.base.url.URLHelper;
import com.helger.collection.CollectionHelper;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.url.codec.URLParameterDecoder;
import com.helger.url.codec.URLParameterEncoder;
import com.helger.url.data.IURLData;
import com.helger.url.data.URLData;
import com.helger.url.param.URLParameter;
import com.helger.url.protocol.IURLProtocol;
import com.helger.url.protocol.URLProtocolRegistry;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

@Immutable
public final class SimpleURLHelper
{
  private static final Logger LOGGER = LoggerFactory.getLogger (SimpleURLHelper.class);

  private SimpleURLHelper ()
  {}

  public static void parseQueryParameters (@Nullable final String sQueryString,
                                           @Nullable final IDecoder <String, String> aParameterDecoder,
                                           @Nonnull final Consumer <URLParameter> aParameterHandler)
  {
    if (StringHelper.isNotEmpty (sQueryString))
      for (final String sKeyValuePair : StringHelper.getExploded (CURL.AMPERSAND, sQueryString))
        if (sKeyValuePair.length () > 0)
        {
          final List <String> aParts = StringHelper.getExploded (CURL.EQUALS, sKeyValuePair, 2);
          final String sKey = aParts.get (0);
          // Maybe empty when passing something like "url?=value"
          if (StringHelper.isNotEmpty (sKey))
          {
            final String sValue = aParts.size () == 2 ? aParts.get (1) : "";
            if (sValue == null)
              throw new IllegalArgumentException ("parameter value may not be null");

            if (aParameterDecoder != null)
            {
              // Now decode the name and the value
              aParameterHandler.accept (new URLParameter (aParameterDecoder.getDecoded (sKey),
                                                          aParameterDecoder.getDecoded (sValue)));
            }
            else
              aParameterHandler.accept (new URLParameter (sKey, sValue));
          }
        }
  }

  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsList <URLParameter> getParsedQueryParameters (@Nullable final String sQueryString,
                                                                      @Nullable final IDecoder <String, String> aParameterDecoder)
  {
    final ICommonsList <URLParameter> ret = new CommonsArrayList <> ();
    parseQueryParameters (sQueryString, aParameterDecoder, ret::add);
    return ret;
  }

  @Nonnull
  public static URLData getAsURLData (@Nonnull final String sHref, @Nullable final Charset aCharset)
  {
    return getAsURLData (sHref, aCharset, aCharset == null ? null : new URLParameterDecoder (aCharset));
  }

  /**
   * Parses the passed URL into a structured form
   *
   * @param sHref
   *        The URL to be parsed
   * @param aCharset
   *        The URL charset used.
   * @param aParameterDecoder
   *        The parameter decoder to use. May be <code>null</code>.
   * @return the corresponding {@link URLData} representation of the passed URL
   */
  @Nonnull
  public static URLData getAsURLData (@Nonnull final String sHref,
                                      @Nullable final Charset aCharset,
                                      @Nullable final IDecoder <String, String> aParameterDecoder)
  {
    ValueEnforcer.notNull (sHref, "Href");

    final String sRealHref = sHref.trim ();

    // Is it a protocol that does not allow for query parameters?
    final IURLProtocol eProtocol = URLProtocolRegistry.getInstance ().getProtocol (sRealHref);
    if (eProtocol != null && !eProtocol.allowsForQueryParameters ())
      return new URLData (sRealHref, null, null, URLData.DEFAULT_CHARSET);

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

    final URLData ret = URLData.createEmpty ();
    ret.setCharset (aCharset);

    // First get the anchor out
    String sRemainingHref = sRealHref;
    final int nIndexAnchor = sRemainingHref.indexOf (CURL.HASH);
    if (nIndexAnchor >= 0)
    {
      // Extract anchor
      ret.setAnchor (sRemainingHref.substring (nIndexAnchor + 1).trim ());
      sRemainingHref = sRemainingHref.substring (0, nIndexAnchor).trim ();
    }

    // Find parameters
    final int nQuestionIndex = sRemainingHref.indexOf (CURL.QUESTIONMARK);
    if (nQuestionIndex >= 0)
    {
      // Use everything after the '?'
      final String sQueryString = sRemainingHref.substring (nQuestionIndex + 1).trim ();

      // Maybe empty, if the URL ends with a '?'
      parseQueryParameters (sQueryString, aParameterDecoder, ret.params ()::add);

      ret.setPath (sRemainingHref.substring (0, nQuestionIndex).trim ());
    }
    else
      ret.setPath (sRemainingHref);

    return ret;
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
  public static String getQueryParametersAsString (@Nullable final List <URLParameter> aQueryParams,
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
        aSB.append (CURL.AMPERSAND);
      aParam.appendTo (aSB, aQueryParameterEncoder);
    }
    return aSB.toString ();
  }

  @Nonnull
  public static String getURLString (@Nonnull final IURLData aURL)
  {
    return getURLString (aURL.getPath (), aURL.params (), aURL.getAnchor (), aURL.getCharset ());
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
   *        The parameters are encoded using this charset. May be <code>null</code>.
   * @return May be <code>null</code> if all parameters are <code>null</code>.
   */
  @Nullable
  public static String getURLString (@Nullable final String sPath,
                                     @Nullable final ICommonsList <URLParameter> aQueryParams,
                                     @Nullable final String sAnchor,
                                     @Nullable final Charset aParameterCharset)
  {
    return URLHelper.getURLString (sPath,
                                   getQueryParametersAsString (aQueryParams,
                                                               aParameterCharset == null ? null
                                                                                         : new URLParameterEncoder (aParameterCharset)),
                                   sAnchor);
  }
}
