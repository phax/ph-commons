/*
 * Copyright (C) 2014-2022 Philip Helger (www.helger.com)
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
package com.helger.jaxb;

import java.lang.ref.WeakReference;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlSchema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.MustImplementEqualsAndHashcode;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.lang.ClassLoaderHelper;
import com.helger.commons.lang.GenericReflection;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;

/**
 * The key class for the {@link JAXBContextCache}
 *
 * @author Philip Helger
 */
@Immutable
@MustImplementEqualsAndHashcode
public class JAXBContextCacheKey
{
  private static final Logger LOGGER = LoggerFactory.getLogger (JAXBContextCacheKey.class);

  private final Package m_aPackage;
  private final WeakReference <ClassLoader> m_aClassLoader;
  private final ICommonsList <WeakReference <Class <?>>> m_aClasses;
  private final Map <String, ?> m_aProperties;
  private final String m_sEqualsHashCodeKey;

  /**
   * Cache key using package and optional class loader
   *
   * @param aPackage
   *        Package to be used. May not be <code>null</code>.
   * @param aClassLoader
   *        Class loader. May be <code>null</code>.
   */
  public JAXBContextCacheKey (@Nonnull final Package aPackage, @Nullable final ClassLoader aClassLoader)
  {
    ValueEnforcer.notNull (aPackage, "Package");
    m_aPackage = aPackage;
    m_aClassLoader = new WeakReference <> (aClassLoader);
    m_aClasses = null;
    m_aProperties = null;
    m_sEqualsHashCodeKey = m_aPackage.getName ();
  }

  /**
   * Cache key using package and optional class loader
   *
   * @param aClasses
   *        Classes to be used. May not be <code>null</code>.
   * @param aProperties
   *        JAXB context properties. May be <code>null</code>.
   * @since v9.4.2
   */
  public JAXBContextCacheKey (@Nonnull final ICommonsList <Class <?>> aClasses, @Nullable final Map <String, ?> aProperties)
  {
    ValueEnforcer.notEmptyNoNullValue (aClasses, "Classes");
    m_aPackage = null;
    m_aClassLoader = null;
    m_aClasses = new CommonsArrayList <> (aClasses, WeakReference::new);
    m_aProperties = new CommonsHashMap <> (aProperties);
    m_sEqualsHashCodeKey = StringHelper.getImplodedMapped (':', aClasses, Class::getName);
  }

  /**
   * @return The list of classes passed in the constructor. May be
   *         <code>null</code>.
   */
  @Nullable
  @ReturnsMutableCopy
  private ICommonsList <Class <?>> _getAllClasses ()
  {
    ICommonsList <Class <?>> ret = null;
    if (m_aClasses != null)
    {
      ret = new CommonsArrayList <> ();
      for (final WeakReference <Class <?>> aItem : m_aClasses)
      {
        final Class <?> aClass = aItem.get ();
        if (aClass != null)
          ret.add (aClass);
      }
    }
    return ret;
  }

  /**
   * @return The class loader passed in the constructor or the default class
   *         loader. May be <code>null</code>.
   */
  @Nullable
  private final ClassLoader _getClassLoader ()
  {
    ClassLoader ret = null;
    if (m_aClassLoader != null)
      ret = m_aClassLoader.get ();
    return ret != null ? ret : ClassLoaderHelper.getDefaultClassLoader ();
  }

  @Nonnull
  private JAXBContext _createFromPackageAndClassLoader (final boolean bSilentMode)
  {
    final ClassLoader aClassLoader = _getClassLoader ();

    if (!bSilentMode)
      if (LOGGER.isInfoEnabled ())
        LOGGER.info ("Creating JAXB context for package " + m_aPackage.getName () + " using ClassLoader " + aClassLoader.toString ());

    try
    {
      // When using "-npa" on JAXB no package-info class is created!
      if (m_aPackage.getAnnotation (XmlSchema.class) == null &&
          GenericReflection.getClassFromNameSafe (m_aPackage.getName () + ".ObjectFactory") == null)
      {
        LOGGER.warn ("The package " +
                     m_aPackage.getName () +
                     " does not seem to be JAXB generated! Trying to create a JAXBContext anyway.");
      }

      return JAXBContext.newInstance (m_aPackage.getName (), aClassLoader);
    }
    catch (final JAXBException ex)
    {
      final String sMsg = "Failed to create JAXB context for package '" +
                          m_aPackage.getName () +
                          "'" +
                          " using ClassLoader " +
                          aClassLoader;
      LOGGER.error (sMsg + ": " + ex.getMessage ());
      throw new IllegalArgumentException (sMsg, ex);
    }
  }

  @Nonnull
  private JAXBContext _createFromClassesAndProperties (final boolean bSilentMode)
  {
    final ICommonsList <Class <?>> aClasses = _getAllClasses ();

    // E.g. an internal class - try anyway!
    if (!bSilentMode)
      if (LOGGER.isInfoEnabled ())
        LOGGER.info ("Creating JAXB context for classes " +
                     StringHelper.getImplodedMapped (", ", aClasses, x -> '\'' + x.getName () + '\'') +
                     (m_aProperties.isEmpty () ? "" : " with properties " + m_aProperties.keySet ()));

    try
    {
      // Using the version with a ClassLoader would require an
      // ObjectFactory.class or an jaxb.index file in the same package!
      final Class <?> [] aClassArray = aClasses.toArray (ArrayHelper.EMPTY_CLASS_ARRAY);
      return JAXBContext.newInstance (aClassArray, m_aProperties);
    }
    catch (final JAXBException ex)
    {
      final String sMsg = "Failed to create JAXB context for classes " +
                          StringHelper.getImplodedMapped (", ", aClasses, x -> '\'' + x.getName () + '\'') +
                          (m_aProperties.isEmpty () ? "" : " with properties " + m_aProperties.keySet ());
      LOGGER.error (sMsg + ": " + ex.getMessage ());
      throw new IllegalArgumentException (sMsg, ex);
    }
  }

  @Nonnull
  public JAXBContext createJAXBContext (final boolean bSilentMode)
  {
    if (m_aPackage != null)
      return _createFromPackageAndClassLoader (bSilentMode);
    return _createFromClassesAndProperties (bSilentMode);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final JAXBContextCacheKey rhs = (JAXBContextCacheKey) o;
    return m_sEqualsHashCodeKey.equals (rhs.m_sEqualsHashCodeKey) &&
           EqualsHelper.equals (_getClassLoader (), rhs._getClassLoader ()) &&
           EqualsHelper.equals (m_aProperties, rhs.m_aProperties);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_sEqualsHashCodeKey).append (_getClassLoader ()).append (m_aProperties).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).appendIfNotNull ("Package", m_aPackage)
                                       .appendIfNotNull ("ClassLoader", m_aClassLoader)
                                       .appendIfNotNull ("Classes", m_aClasses)
                                       .appendIfNotNull ("Properties", m_aProperties)
                                       .getToString ();
  }
}
