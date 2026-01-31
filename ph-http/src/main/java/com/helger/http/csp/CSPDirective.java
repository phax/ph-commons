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
package com.helger.http.csp;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.base.codec.RFC5234Helper;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.equals.EqualsHelper;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.string.StringHelper;
import com.helger.base.tostring.ToStringGenerator;

/**
 * A single CSP directive. It's a name-value-pair.
 *
 * @author Philip Helger
 * @since 10.4.0
 */
public class CSPDirective implements ICSPDirective
{
  private final String m_sName;
  private final String m_sValue;

  public static boolean isValidName (@Nullable final String sName)
  {
    if (StringHelper.isEmpty (sName))
    {
      // Empty name is not allowed
      return false;
    }

    final char [] aChars = sName.toCharArray ();
    for (final char c : aChars)
      if (!RFC5234Helper.isAlpha (c) && !RFC5234Helper.isDigit (c) && c != '-')
        return false;

    return true;
  }

  public static boolean isValidValue (@Nullable final String sValue)
  {
    if (StringHelper.isEmpty (sValue))
    {
      // Empty values are allowed
      return true;
    }

    final char [] aChars = sValue.toCharArray ();
    for (final char c : aChars)
      if (!RFC5234Helper.isWSP (c) && (!RFC5234Helper.isVChar (c) || c == ';' || c == ','))
        return false;

    return true;
  }

  public CSPDirective (@NonNull @Nonempty final String sName, @Nullable final AbstractCSPSourceList <?> aValue)
  {
    this (sName, aValue == null ? null : aValue.getAsString ());
  }

  public CSPDirective (@NonNull @Nonempty final String sName, @Nullable final String sValue)
  {
    ValueEnforcer.isTrue (isValidName (sName), () -> "The CSP directive name '" + sName + "' is invalid!");
    ValueEnforcer.isTrue (isValidValue (sValue), () -> "The CSP directive value '" + sValue + "' is invalid!");
    m_sName = sName;
    m_sValue = sValue;
  }

  @NonNull
  @Nonempty
  public final String getName ()
  {
    return m_sName;
  }

  @Nullable
  public final String getValue ()
  {
    return m_sValue;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final CSPDirective rhs = (CSPDirective) o;
    return m_sName.equals (rhs.m_sName) && EqualsHelper.equals (m_sValue, rhs.m_sValue);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_sName).append (m_sValue).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Name", m_sName).appendIfNotNull ("Value", m_sValue).getToString ();
  }

  /**
   * Restricts the URLs which can be used in a document's &lt;base&gt; element. If this value is
   * absent, then any URI is allowed. If this directive is absent, the user agent will use the value
   * in the &lt;base&gt; element.
   *
   * @param sValue
   *        value
   * @return new directive
   * @since CSP v2
   */
  @NonNull
  public static CSPDirective createBaseURI (@Nullable final String sValue)
  {
    return new CSPDirective ("base-uri", sValue);
  }

  /**
   * Defines valid sources for web workers and nested browsing contexts loaded using elements such
   * as &lt;frame&gt; and &lt;iframe&gt;
   *
   * @param aValue
   *        Value list to use. May be be <code>null</code>.
   * @return New {@link CSPDirective}
   * @since CSP v2
   */
  @NonNull
  public static CSPDirective createChildSrc (@Nullable final AbstractCSPSourceList <?> aValue)
  {
    return new CSPDirective ("child-src", aValue);
  }

  /**
   * Applies to XMLHttpRequest (AJAX), WebSocket or EventSource. If not allowed the browser emulates
   * a 400 HTTP status code.
   *
   * @param aValue
   *        Value list to use. May be be <code>null</code>.
   * @return New {@link CSPDirective}
   * @since CSP v1
   */
  @NonNull
  public static CSPDirective createConnectSrc (@Nullable final AbstractCSPSourceList <?> aValue)
  {
    return new CSPDirective ("connect-src", aValue);
  }

  /**
   * The "default-src" is the default policy for loading content such as JavaScript, Images, CSS,
   * Fonts, AJAX requests, Frames, HTML5 Media.
   *
   * @param aValue
   *        Value list to use. May be be <code>null</code>.
   * @return New {@link CSPDirective}
   * @since CSP v1
   */
  @NonNull
  public static CSPDirective createDefaultSrc (@Nullable final AbstractCSPSourceList <?> aValue)
  {
    return new CSPDirective ("default-src", aValue);
  }

  /**
   * Defines valid sources of fonts.
   *
   * @param aValue
   *        Value list to use. May be be <code>null</code>.
   * @return New {@link CSPDirective}
   * @since CSP v1
   */
  @NonNull
  public static CSPDirective createFontSrc (@Nullable final AbstractCSPSourceList <?> aValue)
  {
    return new CSPDirective ("font-src", aValue);
  }

  /**
   * Defines valid sources that can be used as a HTML &lt;form&gt; action.
   *
   * @param aValue
   *        Value list to use. May be be <code>null</code>.
   * @return New {@link CSPDirective}
   * @since CSP v2
   */
  @NonNull
  public static CSPDirective createFormAction (@Nullable final AbstractCSPSourceList <?> aValue)
  {
    return new CSPDirective ("form-action", aValue);
  }

  /**
   * Defines valid sources for embedding the resource using &lt;frame&gt; &lt;iframe&gt;
   * &lt;object&gt; &lt;embed&gt; &lt;applet&gt;. Setting this directive to <code>'none'</code>
   * should be roughly equivalent to <code>X-Frame-Options: DENY</code>
   *
   * @param aValue
   *        Value list to use. May be be <code>null</code>.
   * @return New {@link CSPDirective}
   * @since CSP v2
   */
  @NonNull
  public static CSPDirective createFrameAncestors (@Nullable final AbstractCSPSourceList <?> aValue)
  {
    return new CSPDirective ("frame-ancestors", aValue);
  }

  /**
   * The HTTP Content-Security-Policy (CSP) "frame-src" directive specifies valid sources for nested
   * browsing contexts loading using elements such as &lt;frame&gt; and &lt;iframe&gt;.
   *
   * @param aValue
   *        Value list to use. May be be <code>null</code>.
   * @return New {@link CSPDirective}
   * @since CSP v1 and v10.4.0
   */
  @NonNull
  public static CSPDirective createFrameSrc (@Nullable final AbstractCSPSourceList <?> aValue)
  {
    return new CSPDirective ("frame-src", aValue);
  }

  /**
   * Defines valid sources of images.
   *
   * @param aValue
   *        Value list to use. May be be <code>null</code>.
   * @return New {@link CSPDirective}
   * @since CSP v1
   */
  @NonNull
  public static CSPDirective createImgSrc (@Nullable final AbstractCSPSourceList <?> aValue)
  {
    return new CSPDirective ("img-src", aValue);
  }

  /**
   * Specifies valid sources of application manifest files.
   *
   * @param aValue
   *        Value list to use. May be be <code>null</code>.
   * @return New {@link CSPDirective}
   * @since CSP v3
   * @since 9.3.5
   */
  @NonNull
  public static CSPDirective createManifestSrc (@Nullable final AbstractCSPSourceList <?> aValue)
  {
    return new CSPDirective ("manifest-src", aValue);
  }

  /**
   * Defines valid sources of audio and video, eg HTML5 &lt;audio&gt;, &lt;video&gt; elements.
   *
   * @param aValue
   *        Value list to use. May be be <code>null</code>.
   * @return New {@link CSPDirective}
   * @since CSP v1
   */
  @NonNull
  public static CSPDirective createMediaSrc (@Nullable final AbstractCSPSourceList <?> aValue)
  {
    return new CSPDirective ("media-src", aValue);
  }

  /**
   * Defines valid sources of plugins, eg &lt;object&gt;, &lt;embed&gt; or &lt;applet&gt;.
   *
   * @param aValue
   *        Value list to use. May be be <code>null</code>.
   * @return New {@link CSPDirective}
   * @since CSP v1
   */
  @NonNull
  public static CSPDirective createObjectSrc (@Nullable final AbstractCSPSourceList <?> aValue)
  {
    return new CSPDirective ("object-src", aValue);
  }

  /**
   * Specifies valid sources to be prefetched or prerendered (draft).
   *
   * @param aValue
   *        Value list to use. May be be <code>null</code>.
   * @return New {@link CSPDirective}
   * @since CSP v3
   * @since 9.3.5
   */
  @NonNull
  @Deprecated (forRemoval = true, since = "10.4.0")
  public static CSPDirective createPrefetchSrc (@Nullable final AbstractCSPSourceList <?> aValue)
  {
    return new CSPDirective ("prefetch-src", aValue);
  }

  /**
   * The report-uri directive specifies a URI to which the user agent sends reports about policy
   * violation.<br>
   * Deprecated in favour of {@link #createReportTo(String)} but browser support is not yet ideal.
   *
   * @param sValue
   *        Report URI
   * @return new directive
   * @since CSP v1
   */
  @NonNull
  public static CSPDirective createReportURI (@Nullable final String sValue)
  {
    return new CSPDirective ("report-uri", sValue);
  }

  /**
   * The Content-Security-Policy "report-to" directive indicates the name of the endpoint that the
   * browser should use for reporting CSP violations. This is not yet supported by Firefox as per
   * 2025-02
   *
   * @param sValue
   *        Report endpoint according to Reporting-Endpoints response header
   * @return new directive
   * @since CSP v3 and v10.4.0
   */
  @NonNull
  public static CSPDirective createReportTo (@Nullable final String sValue)
  {
    return new CSPDirective ("report-to", sValue);
  }

  /**
   * The sandbox directive specifies an HTML sandbox policy that the user agent applies to the
   * protected resource.
   *
   * @param sValue
   *        value
   * @return new directive
   * @since CSP v1
   */
  @NonNull
  public static CSPDirective createSandbox (@Nullable final String sValue)
  {
    return new CSPDirective ("sandbox", sValue);
  }

  /**
   * Defines valid sources of JavaScript.
   *
   * @param aValue
   *        Value list to use. May be be <code>null</code>.
   * @return New {@link CSPDirective}
   * @since CSP v1
   */
  @NonNull
  public static CSPDirective createScriptSrc (@Nullable final AbstractCSPSourceList <?> aValue)
  {
    return new CSPDirective ("script-src", aValue);
  }

  /**
   * The HTTP Content-Security-Policy (CSP) "script-src-attr" directive specifies valid sources for
   * JavaScript inline event handlers.
   *
   * @param aValue
   *        Value list to use. May be be <code>null</code>.
   * @return New {@link CSPDirective}
   * @since CSP v3 and v10.4.0
   */
  @NonNull
  public static CSPDirective createScriptSrcAttr (@Nullable final AbstractCSPSourceList <?> aValue)
  {
    return new CSPDirective ("script-src-attr", aValue);
  }

  /**
   * The HTTP Content-Security-Policy (CSP) "script-src-elem" directive specifies valid sources for
   * JavaScript &lt;script&gt; elements.
   *
   * @param aValue
   *        Value list to use. May be be <code>null</code>.
   * @return New {@link CSPDirective}
   * @since CSP v3 and v10.4.0
   */
  @NonNull
  public static CSPDirective createScriptSrcElem (@Nullable final AbstractCSPSourceList <?> aValue)
  {
    return new CSPDirective ("script-src-elem", aValue);
  }

  /**
   * Defines valid sources of stylesheets.
   *
   * @param aValue
   *        Value list to use. May be be <code>null</code>.
   * @return New {@link CSPDirective}
   * @since CSP v1
   */
  @NonNull
  public static CSPDirective createStyleSrc (@Nullable final AbstractCSPSourceList <?> aValue)
  {
    return new CSPDirective ("style-src", aValue);
  }

  /**
   * The HTTP Content-Security-Policy (CSP) "style-src-attr" directive specifies valid sources for
   * inline styles applied to individual DOM elements.
   *
   * @param aValue
   *        Value list to use. May be be <code>null</code>.
   * @return New {@link CSPDirective}
   * @since CSP v3 and v10.4.0
   */
  @NonNull
  public static CSPDirective createStyleSrcAttr (@Nullable final AbstractCSPSourceList <?> aValue)
  {
    return new CSPDirective ("style-src-attr", aValue);
  }

  /**
   * The HTTP Content-Security-Policy (CSP) "style-src-elem" directive specifies valid sources for
   * stylesheet <code>&lt;style&gt;</code> elements and <code>&lt;link&gt;</code> elements with
   * <code>rel="stylesheet"</code>.
   *
   * @param aValue
   *        Value list to use. May be be <code>null</code>.
   * @return New {@link CSPDirective}
   * @since CSP v3 and v10.4.0
   */
  @NonNull
  public static CSPDirective createStyleSrcElem (@Nullable final AbstractCSPSourceList <?> aValue)
  {
    return new CSPDirective ("style-src-elem", aValue);
  }

  /**
   * Specifies valid sources for Worker, SharedWorker, or ServiceWorker scripts. (draft).
   *
   * @param aValue
   *        Value list to use. May be be <code>null</code>.
   * @return New {@link CSPDirective}
   * @since CSP v3
   * @since 9.3.5
   */
  @NonNull
  public static CSPDirective createWorkerSrc (@Nullable final AbstractCSPSourceList <?> aValue)
  {
    return new CSPDirective ("worker-src", aValue);
  }
}
