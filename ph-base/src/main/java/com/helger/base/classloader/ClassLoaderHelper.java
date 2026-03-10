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
package com.helger.base.classloader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.io.stream.StreamHelper;

/**
 * {@link ClassLoader} utility methods.
 *
 * @author Philip Helger
 */
@Immutable
public class ClassLoaderHelper
{
  private static final Logger LOGGER = LoggerFactory.getLogger (ClassLoaderHelper.class);

  @PresentForCodeCoverage
  private static final ClassLoaderHelper INSTANCE = new ClassLoaderHelper ();

  protected ClassLoaderHelper ()
  {}

  /**
   * Get the system class loader.
   *
   * @return The system class loader. Never <code>null</code>.
   * @see ClassLoader#getSystemClassLoader()
   */
  @NonNull
  public static ClassLoader getSystemClassLoader ()
  {
    return ClassLoader.getSystemClassLoader ();
  }

  /**
   * Get the context class loader of the current thread.
   *
   * @return The context class loader, or <code>null</code> if the context class loader indicates
   *         the system class loader.
   * @see Thread#getContextClassLoader()
   */
  @Nullable
  public static ClassLoader getContextClassLoader ()
  {
    // the context ClassLoader for this Thread, or null indicating the system
    // class loader
    return Thread.currentThread ().getContextClassLoader ();
  }

  /**
   * Set the context class loader of the current thread.
   *
   * @param aClassLoader
   *        The class loader to set as context class loader. May not be <code>null</code>.
   * @see Thread#setContextClassLoader(ClassLoader)
   */
  public static void setContextClassLoader (@NonNull final ClassLoader aClassLoader)
  {
    Thread.currentThread ().setContextClassLoader (aClassLoader);
  }

  /**
   * Get the class loader that loaded the given class.
   *
   * @param aClass
   *        The class whose class loader is to be retrieved. May not be <code>null</code>.
   * @return The class loader of the given class, or <code>null</code> if the class was loaded by
   *         the bootstrap class loader.
   * @see Class#getClassLoader()
   */
  @Nullable
  public static ClassLoader getClassClassLoader (@NonNull final Class <?> aClass)
  {
    // If the class represents a primitive type or void, null is returned.
    return aClass.getClassLoader ();
  }

  /**
   * Get the parent class loader of the given class loader.
   *
   * @param aClassLoader
   *        The class loader whose parent is to be retrieved. May not be <code>null</code>.
   * @return The parent class loader, or <code>null</code> if the parent is the bootstrap class
   *         loader.
   * @see ClassLoader#getParent()
   */
  @Nullable
  public static ClassLoader getParentClassLoader (@NonNull final ClassLoader aClassLoader)
  {
    // Some implementations may use null to represent the bootstrap class loader.
    return aClassLoader.getParent ();
  }

  /**
   * Get the default class loader to use. This first tries to get the context class loader of the
   * current thread, and falls back to the class loader of the {@link ClassLoaderHelper} class
   * itself.
   *
   * @return The default class loader. Never <code>null</code>.
   */
  @NonNull
  public static ClassLoader getDefaultClassLoader ()
  {
    ClassLoader ret = null;
    try
    {
      ret = getContextClassLoader ();
    }
    catch (final RuntimeException ex)
    {
      // e.g. security exception
    }

    // Fallback to class loader of this class
    if (ret == null)
      ret = getClassClassLoader (ClassLoaderHelper.class);

    return ret;
  }

  @NonNull
  protected static String internalGetPathWithoutLeadingSlash (@NonNull @Nonempty final String sPath)
  {
    return sPath.charAt (0) == '/' ? sPath.substring (1) : sPath;
  }

  /**
   * Get the URL of the passed resource using the specified class loader only. This is a sanity
   * wrapper around <code>classLoader.getResource (sPath)</code>.
   *
   * @param aClassLoader
   *        The class loader to be used. May not be <code>null</code>.
   * @param sPath
   *        The path to be resolved. May neither be <code>null</code> nor empty. Internally it is
   *        ensured that the provided path does NOT start with a slash.
   * @return <code>null</code> if the path could not be resolved using the specified class loader.
   */
  @Nullable
  public static URL getResource (@NonNull final ClassLoader aClassLoader, @NonNull @Nonempty final String sPath)
  {
    ValueEnforcer.notNull (aClassLoader, "ClassLoader");
    ValueEnforcer.notEmpty (sPath, "Path");

    // Ensure the path does NOT starts with a "/"
    final String sPathWithoutSlash = internalGetPathWithoutLeadingSlash (sPath);

    try
    {
      // returns null if not found
      return aClassLoader.getResource (sPathWithoutSlash);
    }
    catch (final RuntimeException ex)
    {
      /**
       * Source: https://github.com/phax/as2-lib/issues/99
       *
       * <pre>
       * java.lang.IllegalArgumentException: name
        at sun.misc.URLClassPath$Loader.findResource(URLClassPath.java:703) ~[na:1.8.0_212]
        at sun.misc.URLClassPath.findResource(URLClassPath.java:225) ~[na:1.8.0_212]
        at java.net.URLClassLoader$2.run(URLClassLoader.java:572) ~[na:1.8.0_212]
        at java.net.URLClassLoader$2.run(URLClassLoader.java:570) ~[na:1.8.0_212]
        at java.security.AccessController.doPrivileged(Native Method) ~[na:1.8.0_212]
        at java.net.URLClassLoader.findResource(URLClassLoader.java:569) ~[na:1.8.0_212]
        at org.springframework.boot.loader.LaunchedURLClassLoader.findResource(LaunchedURLClassLoader.java:58) ~[as2-client-0.0.1-SNAPSHOT.jar:0.0.1-SNAPSHOT]
        at java.lang.ClassLoader.getResource(ClassLoader.java:1096) ~[na:1.8.0_212]
        at org.apache.catalina.loader.WebappClassLoaderBase.getResource(WebappClassLoaderBase.java:1048) ~[tomcat-embed-core-9.0.26.jar!/:9.0.26]
        at com.helger.commons.lang.ClassLoaderHelper.getResource(ClassLoaderHelper.java:131) ~[ph-commons-9.3.7.jar!/:9.3.7]
       * </pre>
       */
      LOGGER.warn ("Unexpected runtime exception gathering resource '" + sPathWithoutSlash + "'", ex);
      return null;
    }
  }

  /**
   * Get all URLs of the passed resource using the specified class loader only. This is a sanity
   * wrapper around <code>classLoader.getResources (sPath)</code>.
   *
   * @param aClassLoader
   *        The class loader to be used. May not be <code>null</code>.
   * @param sPath
   *        The path to be resolved. May neither be <code>null</code> nor empty. Internally it is
   *        ensured that the provided path does NOT start with a slash.
   * @return <code>null</code> if the path could not be resolved using the specified class loader.
   * @throws IOException
   *         In case an internal error occurs.
   */
  @NonNull
  public static Enumeration <URL> getResources (@NonNull final ClassLoader aClassLoader,
                                                @NonNull @Nonempty final String sPath) throws IOException
  {
    ValueEnforcer.notNull (aClassLoader, "ClassLoader");
    ValueEnforcer.notEmpty (sPath, "Path");

    // Ensure the path does NOT starts with a "/"
    final String sPathWithoutSlash = internalGetPathWithoutLeadingSlash (sPath);

    // returns null if not found
    return aClassLoader.getResources (sPathWithoutSlash);
  }

  /**
   * Get the input stream of the passed resource using the specified class loader only. This is a
   * sanity wrapper around <code>classLoader.getResourceAsStream (sPath)</code>.
   *
   * @param aClassLoader
   *        The class loader to be used. May not be <code>null</code>.
   * @param sPath
   *        The path to be resolved. May neither be <code>null</code> nor empty. Internally it is
   *        ensured that the provided path does NOT start with a slash.
   * @return <code>null</code> if the path could not be resolved using the specified class loader.
   */
  @Nullable
  public static InputStream getResourceAsStream (@NonNull final ClassLoader aClassLoader,
                                                 @NonNull @Nonempty final String sPath)
  {
    ValueEnforcer.notNull (aClassLoader, "ClassLoader");
    ValueEnforcer.notEmpty (sPath, "Path");

    // Ensure the path does NOT starts with a "/"
    final String sPathWithoutSlash = internalGetPathWithoutLeadingSlash (sPath);

    // returns null if not found
    final InputStream aIS = aClassLoader.getResourceAsStream (sPathWithoutSlash);
    return StreamHelper.checkForInvalidFilterInputStream (aIS);
  }
}
