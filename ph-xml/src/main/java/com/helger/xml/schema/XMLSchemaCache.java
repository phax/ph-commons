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
package com.helger.xml.schema;

import javax.xml.XMLConstants;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;

import com.helger.annotation.concurrent.GuardedBy;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.annotation.misc.Singleton;
import com.helger.base.concurrent.SimpleReadWriteLock;
import com.helger.base.lang.IHasClassLoader;
import com.helger.base.state.EChange;
import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.xml.ls.SimpleLSResourceResolver;
import com.helger.xml.sax.LoggingSAXErrorHandler;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

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

  public XMLSchemaCache (@Nullable final ErrorHandler aErrorHandler, @Nullable final LSResourceResolver aResourceResolver)
  {
    this (createXSDSchemaFactory (), aErrorHandler, aResourceResolver);
  }

  public XMLSchemaCache (@Nonnull final SchemaFactory aSchemaFactory,
                         @Nullable final ErrorHandler aErrorHandler,
                         @Nullable final LSResourceResolver aResourceResolver)
  {
    super (SCHEMA_TYPE_NAME, aSchemaFactory, aErrorHandler, aResourceResolver);
  }

  public static boolean isInstantiated ()
  {
    return s_bDefaultInstantiated;
  }

  @Nonnull
  public static XMLSchemaCache getInstance ()
  {
    final XMLSchemaCache ret = SingletonHolder.INSTANCE;
    s_bDefaultInstantiated = true;
    return ret;
  }

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

  @Nonnull
  public static EChange clearPerClassLoaderCache ()
  {
    return RW_LOCK.writeLockedGet (PER_CL_CACHE::removeAll);
  }
}
