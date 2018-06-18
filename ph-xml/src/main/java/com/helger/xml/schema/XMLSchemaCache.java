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
package com.helger.xml.schema;

import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import javax.xml.XMLConstants;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;

import com.helger.commons.annotation.Singleton;
import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.concurrent.SimpleReadWriteLock;
import com.helger.commons.lang.IHasClassLoader;
import com.helger.commons.state.EChange;
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
    private static final XMLSchemaCache s_aInstance = new XMLSchemaCache ();
  }

  private static boolean s_bDefaultInstantiated = false;

  /**
   * Create a new XSD {@link SchemaFactory}.
   *
   * @return A new {@link SchemaFactory} and never <code>null</code>.
   */
  @Nonnull
  public static SchemaFactory createXSDSchemaFactory ()
  {
    return SchemaFactory.newInstance (XMLConstants.W3C_XML_SCHEMA_NS_URI);
  }

  public XMLSchemaCache ()
  {
    this (new LoggingSAXErrorHandler (), new SimpleLSResourceResolver ());
  }

  public XMLSchemaCache (@Nullable final ErrorHandler aErrorHandler)
  {
    this (aErrorHandler, (LSResourceResolver) null);
  }

  public XMLSchemaCache (@Nullable final LSResourceResolver aResourceResolver)
  {
    this ((ErrorHandler) null, aResourceResolver);
  }

  public XMLSchemaCache (@Nullable final ErrorHandler aErrorHandler,
                         @Nullable final LSResourceResolver aResourceResolver)
  {
    this (createXSDSchemaFactory (), aErrorHandler, aResourceResolver);
  }

  public XMLSchemaCache (@Nonnull final SchemaFactory aSchemaFactory,
                         @Nullable final ErrorHandler aErrorHandler,
                         @Nullable final LSResourceResolver aResourceResolver)
  {
    super ("XSD", aSchemaFactory, aErrorHandler, aResourceResolver);
  }

  public static boolean isInstantiated ()
  {
    return s_bDefaultInstantiated;
  }

  @Nonnull
  public static XMLSchemaCache getInstance ()
  {
    final XMLSchemaCache ret = SingletonHolder.s_aInstance;
    s_bDefaultInstantiated = true;
    return ret;
  }

  private static final SimpleReadWriteLock s_aRWLock = new SimpleReadWriteLock ();
  @GuardedBy ("s_aRWLock")
  private static final ICommonsMap <String, XMLSchemaCache> s_aPerClassLoaderCache = new CommonsHashMap <> ();

  @Nonnull
  public static XMLSchemaCache getInstanceOfClassLoader (@Nullable final IHasClassLoader aClassLoaderProvider)
  {
    return getInstanceOfClassLoader (aClassLoaderProvider == null ? null : aClassLoaderProvider.getClassLoader ());
  }

  @Nonnull
  public static XMLSchemaCache getInstanceOfClassLoader (@Nullable final ClassLoader aClassLoader)
  {
    if (aClassLoader == null)
    {
      // Use default instance
      return getInstance ();
    }

    final String sKey = String.valueOf (aClassLoader);

    XMLSchemaCache aCache = s_aRWLock.readLocked ( () -> s_aPerClassLoaderCache.get (sKey));
    if (aCache == null)
    {
      // Not found in read-lock
      // Try again in write lock
      aCache = s_aRWLock.writeLocked ( () -> s_aPerClassLoaderCache.computeIfAbsent (sKey,
                                                                                     x -> new XMLSchemaCache (new SimpleLSResourceResolver (aClassLoader))));
    }
    return aCache;
  }

  @Nonnull
  public static EChange clearPerClassLoaderCache ()
  {
    return s_aRWLock.writeLocked ((Supplier <EChange>) s_aPerClassLoaderCache::removeAll);
  }
}
