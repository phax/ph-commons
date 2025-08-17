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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.exception.InitializationException;
import com.helger.base.string.StringImplode;
import com.helger.io.resource.IReadableResource;
import com.helger.xml.XMLFactory;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * A global factory for XML transformation objects.
 *
 * @author Philip Helger
 */
@Immutable
public final class XMLTransformerFactory
{
  private static final Logger LOGGER = LoggerFactory.getLogger (XMLTransformerFactory.class);
  private static final TransformerFactory DEFAULT_FACTORY;

  static
  {
    DEFAULT_FACTORY = createTransformerFactory (new LoggingTransformErrorListener (Locale.ROOT),
                                                new DefaultTransformURIResolver ());
    makeTransformerFactorySecure (DEFAULT_FACTORY);
  }

  @PresentForCodeCoverage
  private static final XMLTransformerFactory INSTANCE = new XMLTransformerFactory ();

  private XMLTransformerFactory ()
  {}

  @Nonnull
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
      return aFactory;
    }
    catch (final TransformerFactoryConfigurationError ex)
    {
      throw new InitializationException ("Failed to create XML TransformerFactory", ex);
    }
  }

  /**
   * Set the secure processing feature to a {@link TransformerFactory}. See
   * https://docs.oracle.com/javase/tutorial/jaxp/properties/properties.html for details.
   *
   * @param aFactory
   *        The factory to secure. May not be <code>null</code>.
   * @param aAllowedExternalSchemes
   *        Optional external URL schemes that are allowed to be accessed (as in "file" or "http")
   * @since 9.1.2
   */
  public static void makeTransformerFactorySecure (@Nonnull final TransformerFactory aFactory,
                                                   @Nullable final String... aAllowedExternalSchemes)
  {
    ValueEnforcer.notNull (aFactory, "Factory");

    try
    {
      aFactory.setFeature (XMLConstants.FEATURE_SECURE_PROCESSING, true);

      final String sCombinedSchemes = StringImplode.getImplodedNonEmpty (',', aAllowedExternalSchemes);
      if (sCombinedSchemes.length () > 0)
      {
        aFactory.setAttribute (XMLConstants.ACCESS_EXTERNAL_DTD, sCombinedSchemes);
        aFactory.setAttribute (XMLConstants.ACCESS_EXTERNAL_STYLESHEET, sCombinedSchemes);
        // external schema is unknown
      }
    }
    catch (final TransformerConfigurationException ex)
    {
      throw new InitializationException ("Failed to secure XML TransformerFactory", ex);
    }
  }

  /**
   * @return The default transformer factory.
   */
  @Nonnull
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
  public static Transformer newTransformer (@Nonnull final TransformerFactory aTransformerFactory)
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
  public static Transformer newTransformer (@Nonnull final IReadableResource aResource)
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
  public static Transformer newTransformer (@Nonnull final TransformerFactory aTransformerFactory,
                                            @Nonnull final IReadableResource aResource)
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
  public static Transformer newTransformer (@Nonnull final Source aSource)
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
  public static Transformer newTransformer (@Nonnull final TransformerFactory aTransformerFactory,
                                            @Nonnull final Source aSource)
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
  public static Templates newTemplates (@Nonnull final IReadableResource aResource)
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
  public static Templates newTemplates (@Nonnull final Source aSource)
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
  public static Templates newTemplates (@Nonnull final TransformerFactory aFactory,
                                        @Nonnull final IReadableResource aResource)
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
  public static Templates newTemplates (@Nonnull final TransformerFactory aTransformerFactory,
                                        @Nonnull final Source aSource)
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
