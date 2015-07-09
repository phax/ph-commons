/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.commons.xml.schema;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import javax.xml.XMLConstants;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;

import com.helger.commons.annotation.Singleton;
import com.helger.commons.xml.ls.SimpleLSResourceResolver;
import com.helger.commons.xml.sax.LoggingSAXErrorHandler;

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
    static final XMLSchemaCache s_aInstance = new XMLSchemaCache ();
  }

  private static boolean s_bDefaultInstantiated = false;

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
    this (aErrorHandler, null);
  }

  public XMLSchemaCache (@Nullable final LSResourceResolver aResourceResolver)
  {
    this (null, aResourceResolver);
  }

  public XMLSchemaCache (@Nullable final ErrorHandler aErrorHandler,
                         @Nullable final LSResourceResolver aResourceResolver)
  {
    super ("XSD", createXSDSchemaFactory (), aErrorHandler, aResourceResolver);
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

  private static final ReadWriteLock s_aRWLock = new ReentrantReadWriteLock ();
  @GuardedBy ("s_aRWLock")
  private static final Map <String, XMLSchemaCache> s_aPerClassLoaderCache = new HashMap <String, XMLSchemaCache> ();

  @Nonnull
  public static XMLSchemaCache getInstanceOfClassLoader (@Nullable final ClassLoader aClassLoader)
  {
    if (aClassLoader == null)
      return getInstance ();

    final String sKey = aClassLoader.toString ();
    XMLSchemaCache aCache = null;
    s_aRWLock.readLock ().lock ();
    try
    {
      aCache = s_aPerClassLoaderCache.get (sKey);
    }
    finally
    {
      s_aRWLock.readLock ().unlock ();
    }
    if (aCache == null)
    {
      // Not found in read-lock
      s_aRWLock.writeLock ().lock ();
      try
      {
        // Try again in write lock
        aCache = s_aPerClassLoaderCache.get (sKey);
        if (aCache == null)
        {
          aCache = new XMLSchemaCache (new SimpleLSResourceResolver (aClassLoader));
          s_aPerClassLoaderCache.put (sKey, aCache);
        }
      }
      finally
      {
        s_aRWLock.writeLock ().unlock ();
      }
    }
    return aCache;
  }

  public static void clearPerClassLoaderCache ()
  {
    s_aRWLock.writeLock ().lock ();
    try
    {
      s_aPerClassLoaderCache.clear ();
    }
    finally
    {
      s_aRWLock.writeLock ().unlock ();
    }
  }
}
