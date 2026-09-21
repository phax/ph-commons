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
package com.helger.xml.transform;

import java.util.Locale;

import javax.xml.XMLConstants;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.URIResolver;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.exception.InitializationException;
import com.helger.base.string.StringHelper;
import com.helger.base.string.StringImplode;
import com.helger.io.resource.IReadableResource;
import com.helger.xml.XMLFactory;

/**
 * A global factory for XML transformation objects.
 *
 * @author Philip Helger
 */
@Immutable
public final class XMLTransformerFactory
{
  /**
   * The value for {@link XMLConstants#ACCESS_EXTERNAL_DTD} and
   * {@link XMLConstants#ACCESS_EXTERNAL_STYLESHEET} that denies all external access.
   * <p>
   * The JAXP specification uses the empty String for that purpose, but Saxon maps
   * {@link XMLConstants#ACCESS_EXTERNAL_STYLESHEET} onto its own "allowedProtocols" feature, where
   * the empty String is explicitly documented to be ignored, falling back to the default "all" - an
   * empty String therefore silently allows everything there. <code>#none</code> is not a valid URL
   * scheme, so it matches nothing and denies all access in both worlds.
   * </p>
   *
   * @since 12.5.1
   */
  public static final String ACCESS_EXTERNAL_DENY_ALL = "#none";

  private static final Logger LOGGER = LoggerFactory.getLogger (XMLTransformerFactory.class);
  private static final TransformerFactory DEFAULT_FACTORY;

  static
  {
    DEFAULT_FACTORY = createTransformerFactory (new LoggingTransformErrorListener (Locale.ROOT),
                                                new DefaultTransformURIResolver ());
  }

  @PresentForCodeCoverage
  private static final XMLTransformerFactory INSTANCE = new XMLTransformerFactory ();

  private XMLTransformerFactory ()
  {}

  /**
   * Create a new {@link TransformerFactory} with the passed error listener and URI resolver. The
   * returned factory is secured via
   * {@link #makeTransformerFactorySecure(TransformerFactory, String...)} without any allowed
   * external scheme, so all external DTD and stylesheet access is denied. To allow specific
   * schemes, call that method again on the returned factory.
   *
   * @param aErrorListener
   *        The error listener to use. May be <code>null</code>.
   * @param aURIResolver
   *        The URI resolver to use. May be <code>null</code>.
   * @return A new {@link TransformerFactory}. Never <code>null</code>.
   */
  @NonNull
  public static TransformerFactory createTransformerFactory (@Nullable final ErrorListener aErrorListener,
                                                             @Nullable final URIResolver aURIResolver)
  {
    try
    {
      final TransformerFactory aFactory = XMLFactory.createDefaultTransformerFactory ();
      if (aErrorListener != null)
        aFactory.setErrorListener (aErrorListener);
      if (aURIResolver != null)
        aFactory.setURIResolver (aURIResolver);
      // Deny all external DTD and stylesheet access - a factory that is handed out unsecured
      // lets XSLTC fetch every URI that the URIResolver did not resolve itself (SSRF)
      makeTransformerFactorySecure (aFactory);
      return aFactory;
    }
    catch (final TransformerFactoryConfigurationError ex)
    {
      throw new InitializationException ("Failed to create XML TransformerFactory", ex);
    }
  }

  private static void _setSecureAttribute (@NonNull final TransformerFactory aFactory,
                                           @NonNull final String sAttribute,
                                           @NonNull final String sValue)
  {
    try
    {
      aFactory.setAttribute (sAttribute, sValue);
    }
    catch (final IllegalArgumentException ex)
    {
      // Attribute not supported by this implementation
      LOGGER.warn ("Failed to set attribute " +
                   sAttribute +
                   " to '" +
                   sValue +
                   "' on XML TransformerFactory: " +
                   ex.getMessage ());
    }
  }

  /**
   * Set the secure processing feature to a {@link TransformerFactory}. See
   * https://docs.oracle.com/javase/tutorial/jaxp/properties/properties.html for details.
   *
   * @param aFactory
   *        The factory to secure. May not be <code>null</code>.
   * @param aAllowedExternalSchemes
   *        Optional external URL schemes that are allowed to be accessed (as in "file" or "http").
   *        If none is provided, {@link #ACCESS_EXTERNAL_DENY_ALL} is applied, so that all external
   *        DTD and stylesheet access is denied to prevent Server Side Request Forgery (SSRF) via
   *        <code>document()</code>, <code>xsl:import</code> or <code>xsl:include</code>.
   * @since 9.1.2
   */
  public static void makeTransformerFactorySecure (@NonNull final TransformerFactory aFactory,
                                                   @Nullable final String... aAllowedExternalSchemes)
  {
    ValueEnforcer.notNull (aFactory, "Factory");

    try
    {
      aFactory.setFeature (XMLConstants.FEATURE_SECURE_PROCESSING, true);
    }
    catch (final TransformerConfigurationException ex)
    {
      throw new InitializationException ("Failed to secure XML TransformerFactory", ex);
    }

    // Restrict external DTD and stylesheet access to the explicitly allowed schemes.
    // If no scheme is provided, deny all external access - see ACCESS_EXTERNAL_DENY_ALL on why
    // that is not the empty String.
    String sCombinedSchemes = StringImplode.getImplodedNonEmpty (',', aAllowedExternalSchemes);
    if (StringHelper.isEmpty (sCombinedSchemes))
      sCombinedSchemes = ACCESS_EXTERNAL_DENY_ALL;
    _setSecureAttribute (aFactory, XMLConstants.ACCESS_EXTERNAL_DTD, sCombinedSchemes);
    _setSecureAttribute (aFactory, XMLConstants.ACCESS_EXTERNAL_STYLESHEET, sCombinedSchemes);
    // external schema is unknown
  }

  /**
   * @return The default transformer factory.
   */
  @NonNull
  public static TransformerFactory getDefaultTransformerFactory ()
  {
    return DEFAULT_FACTORY;
  }

  /**
   * Create a new XSLT transformer for no specific resource. This uses the central <b>not thread
   * safe</b> transformer factory.
   *
   * @return <code>null</code> if something goes wrong
   */
  @Nullable
  public static Transformer newTransformer ()
  {
    return newTransformer (DEFAULT_FACTORY);
  }

  /**
   * Create a new XSLT transformer for no specific resource.
   *
   * @param aTransformerFactory
   *        The transformer factory to be used. May not be <code>null</code>.
   * @return <code>null</code> if something goes wrong
   */
  @Nullable
  public static Transformer newTransformer (@NonNull final TransformerFactory aTransformerFactory)
  {
    ValueEnforcer.notNull (aTransformerFactory, "TransformerFactory");

    try
    {
      return aTransformerFactory.newTransformer ();
    }
    catch (final TransformerConfigurationException ex)
    {
      LOGGER.error ("Failed to create transformer", ex);
      return null;
    }
  }

  /**
   * Create a new XSLT transformer for the passed resource. This uses the central <b>not thread
   * safe</b> transformer factory.
   *
   * @param aResource
   *        The resource to be transformed. May not be <code>null</code>.
   * @return <code>null</code> if something goes wrong
   */
  @Nullable
  public static Transformer newTransformer (@NonNull final IReadableResource aResource)
  {
    return newTransformer (DEFAULT_FACTORY, aResource);
  }

  /**
   * Create a new XSLT transformer for the passed resource.
   *
   * @param aTransformerFactory
   *        The transformer factory to be used. May not be <code>null</code>.
   * @param aResource
   *        The resource to be transformed. May not be <code>null</code>.
   * @return <code>null</code> if something goes wrong
   */
  @Nullable
  public static Transformer newTransformer (@NonNull final TransformerFactory aTransformerFactory,
                                            @NonNull final IReadableResource aResource)
  {
    ValueEnforcer.notNull (aResource, "Resource");

    return newTransformer (aTransformerFactory, TransformSourceFactory.create (aResource));
  }

  /**
   * Create a new XSLT transformer for the passed resource. This uses the central <b>not thread
   * safe</b> transformer factory.
   *
   * @param aSource
   *        The resource to be transformed. May not be <code>null</code>.
   * @return <code>null</code> if something goes wrong
   */
  @Nullable
  public static Transformer newTransformer (@NonNull final Source aSource)
  {
    return newTransformer (DEFAULT_FACTORY, aSource);
  }

  /**
   * Create a new XSLT transformer for the passed resource.
   *
   * @param aTransformerFactory
   *        The transformer factory to be used. May not be <code>null</code>.
   * @param aSource
   *        The resource to be transformed. May not be <code>null</code>.
   * @return <code>null</code> if something goes wrong
   */
  @Nullable
  public static Transformer newTransformer (@NonNull final TransformerFactory aTransformerFactory,
                                            @NonNull final Source aSource)
  {
    ValueEnforcer.notNull (aTransformerFactory, "TransformerFactory");
    ValueEnforcer.notNull (aSource, "Source");

    try
    {
      return aTransformerFactory.newTransformer (aSource);
    }
    catch (final TransformerConfigurationException ex)
    {
      LOGGER.error ("Failed to parse " + aSource, ex);
      return null;
    }
  }

  /**
   * Create a new XSLT Template for the passed resource. This uses the central <b>not thread
   * safe</b> transformer factory.
   *
   * @param aResource
   *        The resource to be templated. May not be <code>null</code>.
   * @return <code>null</code> if something goes wrong
   */
  @Nullable
  public static Templates newTemplates (@NonNull final IReadableResource aResource)
  {
    return newTemplates (DEFAULT_FACTORY, aResource);
  }

  /**
   * Create a new XSLT Template for the passed resource. This uses the central <b>not thread
   * safe</b> transformer factory.
   *
   * @param aSource
   *        The resource to be templated. May not be <code>null</code>.
   * @return <code>null</code> if something goes wrong
   */
  @Nullable
  public static Templates newTemplates (@NonNull final Source aSource)
  {
    return newTemplates (DEFAULT_FACTORY, aSource);
  }

  /**
   * Create a new XSLT Template for the passed resource.
   *
   * @param aFactory
   *        The transformer factory to be used. May not be <code>null</code>.
   * @param aResource
   *        The resource to be templated. May not be <code>null</code>.
   * @return <code>null</code> if something goes wrong
   */
  @Nullable
  public static Templates newTemplates (@NonNull final TransformerFactory aFactory,
                                        @NonNull final IReadableResource aResource)
  {
    ValueEnforcer.notNull (aResource, "Resource");

    return newTemplates (aFactory, TransformSourceFactory.create (aResource));
  }

  /**
   * Create a new XSLT Template for the passed resource.
   *
   * @param aTransformerFactory
   *        The transformer factory to be used. May not be <code>null</code>.
   * @param aSource
   *        The resource to be templated. May not be <code>null</code>.
   * @return <code>null</code> if something goes wrong
   */
  @Nullable
  public static Templates newTemplates (@NonNull final TransformerFactory aTransformerFactory,
                                        @NonNull final Source aSource)
  {
    ValueEnforcer.notNull (aTransformerFactory, "TransformerFactory");
    ValueEnforcer.notNull (aSource, "Source");

    try
    {
      return aTransformerFactory.newTemplates (aSource);
    }
    catch (final TransformerConfigurationException ex)
    {
      LOGGER.error ("Failed to parse " + aSource, ex);
      return null;
    }
  }
}
