/**
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
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
package com.helger.commons.lang;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.lang.priviledged.IPrivilegedAction;

/**
 * {@link ClassLoader} utility methods.
 *
 * @author Philip Helger
 */
@Immutable
public final class ClassLoaderHelper
{
  private static final Logger LOGGER = LoggerFactory.getLogger (ClassLoaderHelper.class);

  @PresentForCodeCoverage
  private static final ClassLoaderHelper INSTANCE = new ClassLoaderHelper ();

  private ClassLoaderHelper ()
  {}

  @Nonnull
  public static ClassLoader getSystemClassLoader ()
  {
    return IPrivilegedAction.getSystemClassLoader ().invokeSafe ();
  }

  @Nullable
  public static ClassLoader getContextClassLoader ()
  {
    // the context ClassLoader for this Thread, or null indicating the system
    // class loader
    return IPrivilegedAction.getContextClassLoader ().invokeSafe ();
  }

  public static void setContextClassLoader (@Nonnull final ClassLoader aClassLoader)
  {
    IPrivilegedAction.setContextClassLoader (aClassLoader).invokeSafe ();
  }

  @Nullable
  public static ClassLoader getClassClassLoader (@Nonnull final Class <?> aClass)
  {
    // If the class represents a primitive type or void, null is returned.
    return IPrivilegedAction.getClassLoader (aClass).invokeSafe ();
  }

  @Nullable
  public static ClassLoader getParentClassLoader (@Nonnull final ClassLoader aClassLoader)
  {
    // Some implementations mayuse null to represent the bootstrap class loader.
    return IPrivilegedAction.classLoaderGetParent (aClassLoader).invokeSafe ();
  }

  @Nonnull
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

  @Nonnull
  private static String _getPathWithoutLeadingSlash (@Nonnull @Nonempty final String sPath)
  {
    return sPath.charAt (0) == '/' ? sPath.substring (1) : sPath;
  }

  /**
   * Get the URL of the passed resource using the specified class loader only.
   * This is a sanity wrapper around
   * <code>classLoader.getResource (sPath)</code>.
   *
   * @param aClassLoader
   *        The class loader to be used. May not be <code>null</code>.
   * @param sPath
   *        The path to be resolved. May neither be <code>null</code> nor empty.
   *        Internally it is ensured that the provided path does NOT start with
   *        a slash.
   * @return <code>null</code> if the path could not be resolved using the
   *         specified class loader.
   */
  @Nullable
  public static URL getResource (@Nonnull final ClassLoader aClassLoader, @Nonnull @Nonempty final String sPath)
  {
    ValueEnforcer.notNull (aClassLoader, "ClassLoader");
    ValueEnforcer.notEmpty (sPath, "Path");

    // Ensure the path does NOT starts with a "/"
    final String sPathWithoutSlash = _getPathWithoutLeadingSlash (sPath);

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
   * Get all URLs of the passed resource using the specified class loader only.
   * This is a sanity wrapper around
   * <code>classLoader.getResources (sPath)</code>.
   *
   * @param aClassLoader
   *        The class loader to be used. May not be <code>null</code>.
   * @param sPath
   *        The path to be resolved. May neither be <code>null</code> nor empty.
   *        Internally it is ensured that the provided path does NOT start with
   *        a slash.
   * @return <code>null</code> if the path could not be resolved using the
   *         specified class loader.
   * @throws IOException
   *         In case an internal error occurs.
   */
  @Nonnull
  public static Enumeration <URL> getResources (@Nonnull final ClassLoader aClassLoader,
                                                @Nonnull @Nonempty final String sPath) throws IOException
  {
    ValueEnforcer.notNull (aClassLoader, "ClassLoader");
    ValueEnforcer.notEmpty (sPath, "Path");

    // Ensure the path does NOT starts with a "/"
    final String sPathWithoutSlash = _getPathWithoutLeadingSlash (sPath);

    // returns null if not found
    return aClassLoader.getResources (sPathWithoutSlash);
  }

  /**
   * Get the input stream of the passed resource using the specified class
   * loader only. This is a sanity wrapper around
   * <code>classLoader.getResourceAsStream (sPath)</code>.
   *
   * @param aClassLoader
   *        The class loader to be used. May not be <code>null</code>.
   * @param sPath
   *        The path to be resolved. May neither be <code>null</code> nor empty.
   *        Internally it is ensured that the provided path does NOT start with
   *        a slash.
   * @return <code>null</code> if the path could not be resolved using the
   *         specified class loader.
   */
  @Nullable
  public static InputStream getResourceAsStream (@Nonnull final ClassLoader aClassLoader, @Nonnull @Nonempty final String sPath)
  {
    ValueEnforcer.notNull (aClassLoader, "ClassLoader");
    ValueEnforcer.notEmpty (sPath, "Path");

    // Ensure the path does NOT starts with a "/"
    final String sPathWithoutSlash = _getPathWithoutLeadingSlash (sPath);

    // returns null if not found
    final InputStream aIS = aClassLoader.getResourceAsStream (sPathWithoutSlash);
    return StreamHelper.checkForInvalidFilterInputStream (aIS);
  }
}
