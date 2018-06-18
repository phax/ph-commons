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
package com.helger.xml.xpath;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFunctionResolver;
import javax.xml.xpath.XPathVariableResolver;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.DevelopersNote;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.lang.ClassLoaderHelper;
import com.helger.xml.EXMLParserFeature;

/**
 * Utility class to create {@link XPath} and {@link XPathExpression} objects
 * more easily.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public final class XPathHelper
{
  private static final XPathFactory s_aXPathFactory;

  static
  {
    s_aXPathFactory = XPathFactory.newInstance ();
    EXMLParserFeature.SECURE_PROCESSING.applyTo (s_aXPathFactory, true);
  }

  @PresentForCodeCoverage
  private static final XPathHelper s_aInstance = new XPathHelper ();

  private XPathHelper ()
  {}

  /**
   * @return The default XPath factory. Be careful when manipulating this
   *         object! Never <code>null</code>.
   */
  @Nonnull
  public static XPathFactory getDefaultXPathFactory ()
  {
    return s_aXPathFactory;
  }

  /**
   * Create a new {@link XPathFactory} trying to instantiate Saxon class
   * <code>net.sf.saxon.xpath.XPathFactoryImpl</code> first. If that fails, the
   * default XPathFactory is created.
   *
   * @return A new {@link XPathFactory} and never <code>null</code>.
   * @throws IllegalStateException
   *         In case neither Saxon nor default factory could be instantiated!
   */
  @Nonnull
  public static XPathFactory createXPathFactorySaxonFirst ()
  {
    // The XPath object used to compile the expressions
    XPathFactory aXPathFactory;
    try
    {
      // First try to use Saxon, using the context class loader
      aXPathFactory = XPathFactory.newInstance (XPathFactory.DEFAULT_OBJECT_MODEL_URI,
                                                "net.sf.saxon.xpath.XPathFactoryImpl",
                                                ClassLoaderHelper.getContextClassLoader ());
    }
    catch (final Exception ex)
    {
      // Must be Throwable because of e.g. IllegalAccessError (see issue #19)
      // Seems like Saxon is not in the class path - fall back to default JAXP
      try
      {
        aXPathFactory = XPathFactory.newInstance (XPathFactory.DEFAULT_OBJECT_MODEL_URI);
      }
      catch (final Exception ex2)
      {
        throw new IllegalStateException ("Failed to create JAXP XPathFactory", ex2);
      }
    }

    // Secure processing by default
    EXMLParserFeature.SECURE_PROCESSING.applyTo (aXPathFactory, true);
    return aXPathFactory;
  }

  /**
   * Create a new {@link XPath} without any special settings using the default
   * {@link XPathFactory}.
   *
   * @return The created non-<code>null</code> {@link XPath} object
   */
  @Nonnull
  public static XPath createNewXPath ()
  {
    return createNewXPath (s_aXPathFactory,
                           (XPathVariableResolver) null,
                           (XPathFunctionResolver) null,
                           (NamespaceContext) null);
  }

  /**
   * Create a new {@link XPath} without any special settings.
   *
   * @param aXPathFactory
   *        The XPath factory object to use. May not be <code>null</code>.
   * @return The created non-<code>null</code> {@link XPath} object
   */
  @Nonnull
  public static XPath createNewXPath (@Nonnull final XPathFactory aXPathFactory)
  {
    return createNewXPath (aXPathFactory,
                           (XPathVariableResolver) null,
                           (XPathFunctionResolver) null,
                           (NamespaceContext) null);
  }

  /**
   * Create a new {@link XPath} with the passed variable resolver using the
   * default {@link XPathFactory}.
   *
   * @param aVariableResolver
   *        Variable resolver to be used. May be <code>null</code>.
   * @return The created non-<code>null</code> {@link XPath} object
   */
  @Nonnull
  public static XPath createNewXPath (@Nullable final XPathVariableResolver aVariableResolver)
  {
    return createNewXPath (s_aXPathFactory, aVariableResolver, (XPathFunctionResolver) null, (NamespaceContext) null);
  }

  /**
   * Create a new {@link XPath} with the passed variable resolver.
   *
   * @param aXPathFactory
   *        The XPath factory object to use. May not be <code>null</code>.
   * @param aVariableResolver
   *        Variable resolver to be used. May be <code>null</code>.
   * @return The created non-<code>null</code> {@link XPath} object
   */
  @Nonnull
  public static XPath createNewXPath (@Nonnull final XPathFactory aXPathFactory,
                                      @Nullable final XPathVariableResolver aVariableResolver)
  {
    return createNewXPath (aXPathFactory, aVariableResolver, (XPathFunctionResolver) null, (NamespaceContext) null);
  }

  /**
   * Create a new {@link XPath} with the passed function resolver using the
   * default {@link XPathFactory}.
   *
   * @param aFunctionResolver
   *        Function resolver to be used. May be <code>null</code>.
   * @return The created non-<code>null</code> {@link XPath} object
   */
  @Nonnull
  public static XPath createNewXPath (@Nullable final XPathFunctionResolver aFunctionResolver)
  {
    return createNewXPath (s_aXPathFactory, (XPathVariableResolver) null, aFunctionResolver, (NamespaceContext) null);
  }

  /**
   * Create a new {@link XPath} with the passed function resolver.
   *
   * @param aXPathFactory
   *        The XPath factory object to use. May not be <code>null</code>.
   * @param aFunctionResolver
   *        Function resolver to be used. May be <code>null</code>.
   * @return The created non-<code>null</code> {@link XPath} object
   */
  @Nonnull
  public static XPath createNewXPath (@Nonnull final XPathFactory aXPathFactory,
                                      @Nullable final XPathFunctionResolver aFunctionResolver)
  {
    return createNewXPath (aXPathFactory, (XPathVariableResolver) null, aFunctionResolver, (NamespaceContext) null);
  }

  /**
   * Create a new {@link XPath} with the passed namespace context using the
   * default {@link XPathFactory}.
   *
   * @param aNamespaceContext
   *        Namespace context to be used. May be <code>null</code>.
   * @return The created non-<code>null</code> {@link XPath} object
   */
  @Nonnull
  public static XPath createNewXPath (@Nullable final NamespaceContext aNamespaceContext)
  {
    return createNewXPath (s_aXPathFactory,
                           (XPathVariableResolver) null,
                           (XPathFunctionResolver) null,
                           aNamespaceContext);
  }

  /**
   * Create a new {@link XPath} with the passed namespace context.
   *
   * @param aXPathFactory
   *        The XPath factory object to use. May not be <code>null</code>.
   * @param aNamespaceContext
   *        Namespace context to be used. May be <code>null</code>.
   * @return The created non-<code>null</code> {@link XPath} object
   */
  @Nonnull
  public static XPath createNewXPath (@Nonnull final XPathFactory aXPathFactory,
                                      @Nullable final NamespaceContext aNamespaceContext)
  {
    return createNewXPath (aXPathFactory,
                           (XPathVariableResolver) null,
                           (XPathFunctionResolver) null,
                           aNamespaceContext);
  }

  /**
   * Create a new {@link XPath} with the passed variable resolver and namespace
   * context using the default {@link XPathFactory}.
   *
   * @param aVariableResolver
   *        Variable resolver to be used. May be <code>null</code>.
   * @param aNamespaceContext
   *        Namespace context to be used. May be <code>null</code>.
   * @return The created non-<code>null</code> {@link XPath} object
   */
  @Nonnull
  public static XPath createNewXPath (@Nullable final XPathVariableResolver aVariableResolver,
                                      @Nullable final NamespaceContext aNamespaceContext)
  {
    return createNewXPath (s_aXPathFactory, aVariableResolver, (XPathFunctionResolver) null, aNamespaceContext);
  }

  /**
   * Create a new {@link XPath} with the passed variable resolver, function
   * resolver and namespace context using the default {@link XPathFactory}.
   *
   * @param aVariableResolver
   *        Variable resolver to be used. May be <code>null</code>.
   * @param aFunctionResolver
   *        Function resolver to be used. May be <code>null</code>.
   * @param aNamespaceContext
   *        Namespace context to be used. May be <code>null</code>.
   * @return The created non-<code>null</code> {@link XPath} object
   */
  @Nonnull
  public static XPath createNewXPath (@Nullable final XPathVariableResolver aVariableResolver,
                                      @Nullable final XPathFunctionResolver aFunctionResolver,
                                      @Nullable final NamespaceContext aNamespaceContext)
  {
    return createNewXPath (s_aXPathFactory, aVariableResolver, aFunctionResolver, aNamespaceContext);
  }

  /**
   * Create a new {@link XPath} with the passed variable resolver, function
   * resolver and namespace context.
   *
   * @param aXPathFactory
   *        The XPath factory object to use. May not be <code>null</code>.
   * @param aVariableResolver
   *        Variable resolver to be used. May be <code>null</code>.
   * @param aFunctionResolver
   *        Function resolver to be used. May be <code>null</code>.
   * @param aNamespaceContext
   *        Namespace context to be used. May be <code>null</code>.
   * @return The created non-<code>null</code> {@link XPath} object
   */
  @Nonnull
  public static XPath createNewXPath (@Nonnull final XPathFactory aXPathFactory,
                                      @Nullable final XPathVariableResolver aVariableResolver,
                                      @Nullable final XPathFunctionResolver aFunctionResolver,
                                      @Nullable final NamespaceContext aNamespaceContext)
  {
    ValueEnforcer.notNull (aXPathFactory, "XPathFactory");

    final XPath aXPath = aXPathFactory.newXPath ();
    if (aVariableResolver != null)
      aXPath.setXPathVariableResolver (aVariableResolver);
    if (aFunctionResolver != null)
      aXPath.setXPathFunctionResolver (aFunctionResolver);
    if (aNamespaceContext != null)
      aXPath.setNamespaceContext (aNamespaceContext);
    return aXPath;
  }

  /**
   * Create a new XPath expression for evaluation using the default
   * {@link XPathFactory}.
   *
   * @param aVariableResolver
   *        Variable resolver to be used. May be <code>null</code>.
   * @param aFunctionResolver
   *        Function resolver to be used. May be <code>null</code>.
   * @param aNamespaceContext
   *        Namespace context to be used. May be <code>null</code>.
   * @param sXPath
   *        The main XPath string to be evaluated
   * @return The {@link XPathExpression} object to be used.
   * @throws IllegalArgumentException
   *         if the XPath cannot be compiled
   */
  @Nonnull
  public static XPathExpression createNewXPathExpression (@Nullable final XPathVariableResolver aVariableResolver,
                                                          @Nullable final XPathFunctionResolver aFunctionResolver,
                                                          @Nullable final NamespaceContext aNamespaceContext,
                                                          @Nonnull @Nonempty final String sXPath)
  {
    return createNewXPathExpression (createNewXPath (aVariableResolver, aFunctionResolver, aNamespaceContext), sXPath);
  }

  @Nonnull
  @Deprecated
  @DevelopersNote ("Typo in name")
  public static XPathExpression createNewXPathExpresion (@Nullable final XPathVariableResolver aVariableResolver,
                                                         @Nullable final XPathFunctionResolver aFunctionResolver,
                                                         @Nullable final NamespaceContext aNamespaceContext,
                                                         @Nonnull @Nonempty final String sXPath)
  {
    return createNewXPathExpression (aVariableResolver, aFunctionResolver, aNamespaceContext, sXPath);
  }

  /**
   * Create a new XPath expression for evaluation using the default
   * {@link XPathFactory}.
   *
   * @param aVariableResolver
   *        Variable resolver to be used. May be <code>null</code>.
   * @param sXPath
   *        The main XPath string to be evaluated
   * @return The {@link XPathExpression} object to be used.
   * @throws IllegalArgumentException
   *         if the XPath cannot be compiled
   */
  @Nonnull
  public static XPathExpression createNewXPathExpression (@Nullable final XPathVariableResolver aVariableResolver,
                                                          @Nonnull @Nonempty final String sXPath)
  {
    return createNewXPathExpression (createNewXPath (aVariableResolver), sXPath);
  }

  @Nonnull
  @Deprecated
  @DevelopersNote ("Typo in name")
  public static XPathExpression createNewXPathExpresion (@Nullable final XPathVariableResolver aVariableResolver,
                                                         @Nonnull @Nonempty final String sXPath)
  {
    return createNewXPathExpression (aVariableResolver, sXPath);
  }

  /**
   * Create a new XPath expression for evaluation using the default
   * {@link XPathFactory}.
   *
   * @param aFunctionResolver
   *        Function resolver to be used. May be <code>null</code>.
   * @param sXPath
   *        The main XPath string to be evaluated
   * @return The {@link XPathExpression} object to be used.
   * @throws IllegalArgumentException
   *         if the XPath cannot be compiled
   */
  @Nonnull
  public static XPathExpression createNewXPathExpression (@Nullable final XPathFunctionResolver aFunctionResolver,
                                                          @Nonnull @Nonempty final String sXPath)
  {
    return createNewXPathExpression (createNewXPath (aFunctionResolver), sXPath);
  }

  @Nonnull
  @Deprecated
  @DevelopersNote ("Typo in name")
  public static XPathExpression createNewXPathExpresion (@Nullable final XPathFunctionResolver aFunctionResolver,
                                                         @Nonnull @Nonempty final String sXPath)
  {
    return createNewXPathExpression (aFunctionResolver, sXPath);
  }

  /**
   * Create a new XPath expression for evaluation using the default
   * {@link XPathFactory}.
   *
   * @param aNamespaceContext
   *        Namespace context to be used. May be <code>null</code>.
   * @param sXPath
   *        The main XPath string to be evaluated
   * @return The {@link XPathExpression} object to be used.
   * @throws IllegalArgumentException
   *         if the XPath cannot be compiled
   */
  @Nonnull
  public static XPathExpression createNewXPathExpression (@Nullable final NamespaceContext aNamespaceContext,
                                                          @Nonnull @Nonempty final String sXPath)
  {
    return createNewXPathExpression (createNewXPath (aNamespaceContext), sXPath);
  }

  @Nonnull
  @Deprecated
  @DevelopersNote ("Typo in name")
  public static XPathExpression createNewXPathExpresion (@Nullable final NamespaceContext aNamespaceContext,
                                                         @Nonnull @Nonempty final String sXPath)
  {
    return createNewXPathExpression (aNamespaceContext, sXPath);
  }

  /**
   * Create a new XPath expression for evaluation using the default
   * {@link XPathFactory}.
   *
   * @param sXPath
   *        The main XPath string to be evaluated
   * @return The {@link XPathExpression} object to be used.
   * @throws IllegalArgumentException
   *         if the XPath cannot be compiled
   */
  @Nonnull
  public static XPathExpression createNewXPathExpression (@Nonnull @Nonempty final String sXPath)
  {
    return createNewXPathExpression (createNewXPath (), sXPath);
  }

  @Nonnull
  @Deprecated
  @DevelopersNote ("Typo in name")
  public static XPathExpression createNewXPathExpresion (@Nonnull @Nonempty final String sXPath)
  {
    return createNewXPathExpression (sXPath);
  }

  /**
   * Create a new XPath expression for evaluation.
   *
   * @param aXPath
   *        The pre-created XPath object. May not be <code>null</code>.
   * @param sXPath
   *        The main XPath string to be evaluated
   * @return The {@link XPathExpression} object to be used.
   * @throws IllegalArgumentException
   *         if the XPath cannot be compiled
   */
  @Nonnull
  public static XPathExpression createNewXPathExpression (@Nonnull final XPath aXPath,
                                                          @Nonnull @Nonempty final String sXPath)
  {
    ValueEnforcer.notNull (aXPath, "XPath");
    ValueEnforcer.notNull (sXPath, "XPathExpression");

    try
    {
      return aXPath.compile (sXPath);
    }
    catch (final XPathExpressionException ex)
    {
      throw new IllegalArgumentException ("Failed to compile XPath expression '" + sXPath + "'", ex);
    }
  }

  @Nonnull
  @Deprecated
  @DevelopersNote ("Typo in name")
  public static XPathExpression createNewXPathExpresion (@Nonnull final XPath aXPath,
                                                         @Nonnull @Nonempty final String sXPath)
  {
    return createNewXPathExpression (aXPath, sXPath);
  }
}
