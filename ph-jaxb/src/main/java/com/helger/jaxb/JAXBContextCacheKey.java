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
package com.helger.jaxb;

import java.lang.ref.WeakReference;
import java.util.Map;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.MustImplementEqualsAndHashcode;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.CGlobal;
import com.helger.base.classloader.ClassLoaderHelper;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.equals.EqualsHelper;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.log.IConditionalLogger;
import com.helger.base.reflection.GenericReflection;
import com.helger.base.string.StringImplode;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.CommonsHashMap;
import com.helger.collection.commons.ICommonsList;
import com.helger.collection.commons.ICommonsMap;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.annotation.XmlSchema;

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

  private final ICommonsList <Package> m_aPackages;
  private final WeakReference <ClassLoader> m_aClassLoader;
  private final ICommonsList <WeakReference <Class <?>>> m_aClasses;
  private final ICommonsMap <String, ?> m_aProperties;
  private final String m_sEqualsHashCodeKey;

  /**
   * Cache key using package and optional class loader
   *
   * @param aPackage
   *        Package to be used. May not be <code>null</code>.
   * @param aClassLoader
   *        Class loader. May be <code>null</code>.
   */
  @Deprecated (forRemoval = true, since = "11.0.4")
  public JAXBContextCacheKey (@NonNull final Package aPackage, @Nullable final ClassLoader aClassLoader)
  {
    this (new CommonsArrayList <> (aPackage), aClassLoader);
  }

  /**
   * Cache key using package and optional class loader
   *
   * @param aPackages
   *        Packages to be used. May not be <code>null</code>.
   * @param aClassLoader
   *        Class loader. May be <code>null</code>.
   * @since 11.0.4
   */
  public JAXBContextCacheKey (@NonNull final ICommonsList <Package> aPackages, @Nullable final ClassLoader aClassLoader)
  {
    ValueEnforcer.notEmptyNoNullValue (aPackages, "Packages");
    m_aPackages = aPackages.getClone ();
    m_aClassLoader = new WeakReference <> (aClassLoader);
    m_aClasses = null;
    m_aProperties = null;
    m_sEqualsHashCodeKey = StringImplode.imploder ().source (aPackages, Package::getName).separator (':').build ();

    // When using "-npa" on JAXB no package-info class is created!
    for (final Package aPackage : aPackages)
      if (aPackage.getAnnotation (XmlSchema.class) == null &&
          GenericReflection.getClassFromNameSafe (aPackage.getName () + ".ObjectFactory") == null)
      {
        LOGGER.warn ("The package '" +
                     aPackage.getName () +
                     "' does not seem to be JAXB generated! Trying to create a JAXBContext anyway.");
      }
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
  public JAXBContextCacheKey (@NonNull final ICommonsList <Class <?>> aClasses,
                              @Nullable final Map <String, ?> aProperties)
  {
    ValueEnforcer.notEmptyNoNullValue (aClasses, "Classes");
    m_aPackages = null;
    m_aClassLoader = null;
    m_aClasses = new CommonsArrayList <> (aClasses, WeakReference::new);
    m_aProperties = new CommonsHashMap <> (aProperties);
    m_sEqualsHashCodeKey = StringImplode.imploder ().source (aClasses, Class::getName).separator (':').build ();
  }

  /**
   * @return The list of classes passed in the constructor. May be <code>null</code>.
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
   * @return The class loader passed in the constructor or the default class loader. May be
   *         <code>null</code>.
   */
  @Nullable
  private ClassLoader _getClassLoader ()
  {
    ClassLoader ret = null;
    if (m_aClassLoader != null)
      ret = m_aClassLoader.get ();
    return ret != null ? ret : ClassLoaderHelper.getDefaultClassLoader ();
  }

  @NonNull
  private JAXBContext _createFromPackageAndClassLoader (@NonNull final IConditionalLogger aCondLog)
  {
    final ClassLoader aClassLoader = _getClassLoader ();

    aCondLog.info ( () -> "Creating JAXB context for packages " +
                          StringImplode.imploder ()
                                       .source (m_aPackages, x -> '\'' + x.getName () + '\'')
                                       .separator (", ")
                                       .build () +
                          " using ClassLoader " +
                          aClassLoader.toString ());

    try
    {
      // Use all packages, colon separated
      return JAXBContext.newInstance (m_sEqualsHashCodeKey, aClassLoader);
    }
    catch (final JAXBException ex)
    {
      final String sMsg = "Failed to create JAXB context for packages " +
                          StringImplode.imploder ()
                                       .source (m_aPackages, x -> '\'' + x.getName () + '\'')
                                       .separator (", ")
                                       .build () +
                          " using ClassLoader " +
                          aClassLoader;
      LOGGER.error (sMsg + ": " + ex.getMessage ());
      throw new IllegalArgumentException (sMsg, ex);
    }
  }

  @NonNull
  private JAXBContext _createFromClassesAndProperties (@NonNull final IConditionalLogger aCondLog)
  {
    final ICommonsList <Class <?>> aClasses = _getAllClasses ();

    // E.g. an internal class - try anyway!
    aCondLog.info ( () -> "Creating JAXB context for classes " +
                          StringImplode.imploder ()
                                       .source (aClasses, x -> '\'' + x.getName () + '\'')
                                       .separator (", ")
                                       .build () +
                          (m_aProperties.isEmpty () ? "" : " with properties " + m_aProperties.keySet ()));

    try
    {
      // Using the version with a ClassLoader would require an
      // ObjectFactory.class or an jaxb.index file in the same package!
      final Class <?> [] aClassArray = aClasses.toArray (CGlobal.EMPTY_CLASS_ARRAY);
      return JAXBContext.newInstance (aClassArray, m_aProperties);
    }
    catch (final JAXBException ex)
    {
      final String sMsg = "Failed to create JAXB context for classes " +
                          StringImplode.imploder ()
                                       .source (aClasses, x -> '\'' + x.getName () + '\'')
                                       .separator (", ")
                                       .build () +
                          (m_aProperties.isEmpty () ? "" : " with properties " + m_aProperties.keySet ());
      LOGGER.error (sMsg + ": " + ex.getMessage ());
      throw new IllegalArgumentException (sMsg, ex);
    }
  }

  @NonNull
  public JAXBContext createJAXBContext (@NonNull final IConditionalLogger aCondLog)
  {
    if (m_aPackages != null)
      return _createFromPackageAndClassLoader (aCondLog);
    return _createFromClassesAndProperties (aCondLog);
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
    return new HashCodeGenerator (this).append (m_sEqualsHashCodeKey)
                                       .append (_getClassLoader ())
                                       .append (m_aProperties)
                                       .getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).appendIfNotNull ("Packages", m_aPackages)
                                       .appendIfNotNull ("ClassLoader", m_aClassLoader)
                                       .appendIfNotNull ("Classes", m_aClasses)
                                       .appendIfNotNull ("Properties", m_aProperties)
                                       .appendIfNotNull ("EqualsHashCodeKey", m_sEqualsHashCodeKey)
                                       .getToString ();
  }

  /**
   * Factory method with a single package and the default {@link ClassLoader}.
   *
   * @param aPackage
   *        Package to load. May not be <code>null</code>.
   * @return The created object. Never <code>null</code>.
   * @since 11.0.4
   */
  @NonNull
  public static JAXBContextCacheKey createForPackage (@NonNull final Package aPackage)
  {
    return createForPackage (aPackage, null);
  }

  /**
   * Factory method with a single package and the provided {@link ClassLoader}.
   *
   * @param aPackage
   *        Package to load. May not be <code>null</code>.
   * @param aClassLoader
   *        Class loader. May be <code>null</code>.
   * @return The created object. Never <code>null</code>.
   * @since 11.0.4
   */
  @NonNull
  public static JAXBContextCacheKey createForPackage (@NonNull final Package aPackage,
                                                      @Nullable final ClassLoader aClassLoader)
  {
    ValueEnforcer.notNull (aPackage, "Package");
    return new JAXBContextCacheKey (new CommonsArrayList <> (aPackage), aClassLoader);
  }

  /**
   * Factory method with a list of packages and the default {@link ClassLoader}.
   *
   * @param aPackages
   *        List of packages to load. May not be <code>null</code>.
   * @return The created object. Never <code>null</code>.
   * @since 11.0.4
   */
  @NonNull
  public static JAXBContextCacheKey createForPackages (@NonNull final ICommonsList <Package> aPackages)
  {
    return createForPackages (aPackages, null);
  }

  /**
   * Factory method with a list of packages and the default {@link ClassLoader}.
   *
   * @param aPackages
   *        List of packages to load. May not be <code>null</code>.
   * @return The created object. Never <code>null</code>.
   * @since 11.0.4
   */
  @NonNull
  public static JAXBContextCacheKey createForPackages (@NonNull final Package... aPackages)
  {
    return createForPackages (new CommonsArrayList <> (aPackages), null);
  }

  /**
   * Factory method with a list of packages and the provided {@link ClassLoader}.
   *
   * @param aPackages
   *        List of packages to load. May not be <code>null</code>.
   * @param aClassLoader
   *        Class loader. May be <code>null</code>.
   * @return The created object. Never <code>null</code>.
   * @since 11.0.4
   */
  @NonNull
  public static JAXBContextCacheKey createForPackages (@NonNull final ICommonsList <Package> aPackages,
                                                       @Nullable final ClassLoader aClassLoader)
  {
    return new JAXBContextCacheKey (aPackages, aClassLoader);
  }

  /**
   * Get the {@link JAXBContext} from an existing {@link Class} object. If the class's owning
   * package is a valid JAXB package, this method redirects to {@link #createForPackage(Package)}
   *
   * @param aClass
   *        The class for which the JAXB context is to be created. May not be <code>null</code>.
   * @return The created object. Never <code>null</code>.
   * @since 11.0.4
   */
  @NonNull
  public static JAXBContextCacheKey createForClass (@NonNull final Class <?> aClass)
  {
    return createForClass (aClass, null);
  }

  /**
   * Get the {@link JAXBContext} from an existing {@link Class} object. If the class's owning
   * package is a valid JAXB package, this method redirects to
   * {@link #createForPackage(Package, ClassLoader)}
   *
   * @param aClass
   *        The class for which the JAXB context is to be created. May not be <code>null</code>.
   * @param aClassLoader
   *        Class loader to use. May be <code>null</code> in which case the default class loader is
   *        used.
   * @return The created object. Never <code>null</code>.
   * @since 11.0.4
   */
  @NonNull
  public static JAXBContextCacheKey createForClass (@NonNull final Class <?> aClass,
                                                    @Nullable final ClassLoader aClassLoader)
  {
    ValueEnforcer.notNull (aClass, "Class");

    final Package aPackage = aClass.getPackage ();
    if (aPackage.getAnnotation (XmlSchema.class) != null)
    {
      // Redirect to cached version
      return createForPackage (aPackage, aClassLoader);
    }
    return new JAXBContextCacheKey (new CommonsArrayList <> (aClass), (Map <String, ?>) null);
  }

  /**
   * Get the {@link JAXBContext} from existing {@link Class} objects.
   *
   * @param aClasses
   *        The classes for which the JAXB context is to be created. May not be <code>null</code>
   *        nor empty.
   * @return The created object. Never <code>null</code>.
   * @since 11.0.4
   */
  @NonNull
  public static JAXBContextCacheKey createForClasses (@NonNull final ICommonsList <Class <?>> aClasses)
  {
    return createForClasses (aClasses, null);
  }

  /**
   * Get the {@link JAXBContext} from existing {@link Class} objects.
   *
   * @param aClasses
   *        The classes for which the JAXB context is to be created. May not be <code>null</code>
   *        nor empty.
   * @return The created object. Never <code>null</code>.
   * @since 11.0.4
   */
  @NonNull
  public static JAXBContextCacheKey createForClasses (@NonNull final Class <?>... aClasses)
  {
    return createForClasses (new CommonsArrayList <> (aClasses), null);
  }

  /**
   * Get the {@link JAXBContext} from existing {@link Class} objects and optional JAXB Context
   * properties.
   *
   * @param aClasses
   *        The classes for which the JAXB context is to be created. May not be <code>null</code>
   *        nor empty.
   * @param aProperties
   *        JAXB context properties. May be <code>null</code>.
   * @return The created object. Never <code>null</code>.
   * @since 11.0.4
   */
  @NonNull
  public static JAXBContextCacheKey createForClasses (@NonNull final ICommonsList <Class <?>> aClasses,
                                                      @Nullable final Map <String, ?> aProperties)
  {
    ValueEnforcer.notEmptyNoNullValue (aClasses, "Classes");
    return new JAXBContextCacheKey (aClasses, aProperties);
  }
}
