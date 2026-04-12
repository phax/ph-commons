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
package com.helger.xml.schema;

import javax.xml.XMLConstants;
import javax.xml.validation.SchemaFactory;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;

import com.helger.annotation.concurrent.GuardedBy;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.annotation.misc.Singleton;
import com.helger.base.classloader.IHasClassLoader;
import com.helger.base.concurrent.SimpleReadWriteLock;
import com.helger.base.state.EChange;
import com.helger.collection.commons.CommonsHashMap;
import com.helger.collection.commons.ICommonsMap;
import com.helger.xml.ls.SimpleLSResourceResolver;
import com.helger.xml.sax.LoggingSAXErrorHandler;

/**
 * This class is used to cache XML schema objects.
 *
 * @author Philip Helger
 */
@ThreadSafe
@Singleton ("can be instantiated directly as well")
public class XMLSchemaCache extends SchemaCache
{
  private static final class SingletonHolder
  {
    private static final XMLSchemaCache INSTANCE = new XMLSchemaCache ();
  }

  public static final String SCHEMA_TYPE_NAME = "XSD";
  private static final SimpleReadWriteLock RW_LOCK = new SimpleReadWriteLock ();
  @GuardedBy ("RW_LOCK")
  private static final ICommonsMap <String, XMLSchemaCache> PER_CL_CACHE = new CommonsHashMap <> ();
  private static volatile boolean s_bDefaultInstantiated = false;

  /**
   * Create a new XSD {@link SchemaFactory}.
   *
   * @return A new {@link SchemaFactory} and never <code>null</code>.
   */
  @NonNull
  public static SchemaFactory createXSDSchemaFactory ()
  {
    return SchemaFactory.newInstance (XMLConstants.W3C_XML_SCHEMA_NS_URI);
  }

  /**
   * Default constructor using a {@link LoggingSAXErrorHandler} and a
   * {@link SimpleLSResourceResolver}.
   */
  public XMLSchemaCache ()
  {
    this (new LoggingSAXErrorHandler (), new SimpleLSResourceResolver ());
  }

  /**
   * Constructor with a custom error handler.
   *
   * @param aErrorHandler
   *        The error handler to use. May be <code>null</code>.
   */
  public XMLSchemaCache (@Nullable final ErrorHandler aErrorHandler)
  {
    this (aErrorHandler, (LSResourceResolver) null);
  }

  /**
   * Constructor with a custom resource resolver.
   *
   * @param aResourceResolver
   *        The resource resolver to use. May be <code>null</code>.
   */
  public XMLSchemaCache (@Nullable final LSResourceResolver aResourceResolver)
  {
    this ((ErrorHandler) null, aResourceResolver);
  }

  /**
   * Constructor with a custom error handler and resource resolver.
   *
   * @param aErrorHandler
   *        The error handler to use. May be <code>null</code>.
   * @param aResourceResolver
   *        The resource resolver to use. May be <code>null</code>.
   */
  public XMLSchemaCache (@Nullable final ErrorHandler aErrorHandler, @Nullable final LSResourceResolver aResourceResolver)
  {
    this (createXSDSchemaFactory (), aErrorHandler, aResourceResolver);
  }

  /**
   * Constructor with a custom schema factory, error handler and resource
   * resolver.
   *
   * @param aSchemaFactory
   *        The schema factory to use. May not be <code>null</code>.
   * @param aErrorHandler
   *        The error handler to use. May be <code>null</code>.
   * @param aResourceResolver
   *        The resource resolver to use. May be <code>null</code>.
   */
  public XMLSchemaCache (@NonNull final SchemaFactory aSchemaFactory,
                         @Nullable final ErrorHandler aErrorHandler,
                         @Nullable final LSResourceResolver aResourceResolver)
  {
    super (SCHEMA_TYPE_NAME, aSchemaFactory, aErrorHandler, aResourceResolver);
  }

  /**
   * @return <code>true</code> if the default singleton instance has been
   *         instantiated, <code>false</code> otherwise.
   */
  public static boolean isInstantiated ()
  {
    return s_bDefaultInstantiated;
  }

  /**
   * @return The default singleton instance. Never <code>null</code>.
   */
  @NonNull
  public static XMLSchemaCache getInstance ()
  {
    final XMLSchemaCache ret = SingletonHolder.INSTANCE;
    s_bDefaultInstantiated = true;
    return ret;
  }

  /**
   * Get the {@link XMLSchemaCache} instance for the specified class loader
   * provider.
   *
   * @param aClassLoaderProvider
   *        The class loader provider. May be <code>null</code>.
   * @return The matching {@link XMLSchemaCache}. Never <code>null</code>.
   */
  @NonNull
  public static XMLSchemaCache getInstanceOfClassLoader (@Nullable final IHasClassLoader aClassLoaderProvider)
  {
    return getInstanceOfClassLoader (aClassLoaderProvider == null ? null : aClassLoaderProvider.getClassLoader ());
  }

  /**
   * Get the {@link XMLSchemaCache} instance for the specified class loader.
   *
   * @param aClassLoader
   *        The class loader to use. May be <code>null</code> in which case
   *        the default instance is returned.
   * @return The matching {@link XMLSchemaCache}. Never <code>null</code>.
   */
  @NonNull
  public static XMLSchemaCache getInstanceOfClassLoader (@Nullable final ClassLoader aClassLoader)
  {
    if (aClassLoader == null)
    {
      // Use default instance
      return getInstance ();
    }

    final String sKey = String.valueOf (aClassLoader);

    XMLSchemaCache aCache = RW_LOCK.readLockedGet ( () -> PER_CL_CACHE.get (sKey));
    if (aCache == null)
    {
      // Not found in read-lock
      // Try again in write lock
      aCache = RW_LOCK.writeLockedGet ( () -> PER_CL_CACHE.computeIfAbsent (sKey,
                                                                            x -> new XMLSchemaCache (new SimpleLSResourceResolver (aClassLoader))));
    }
    return aCache;
  }

  /**
   * Clear all per-class-loader cached schema instances.
   *
   * @return {@link EChange#CHANGED} if something was removed.
   */
  @NonNull
  public static EChange clearPerClassLoaderCache ()
  {
    return RW_LOCK.writeLockedGet (PER_CL_CACHE::removeAll);
  }
}
