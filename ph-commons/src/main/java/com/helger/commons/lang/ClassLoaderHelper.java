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
package com.helger.commons.lang;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

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
  @PresentForCodeCoverage
  private static final ClassLoaderHelper s_aInstance = new ClassLoaderHelper ();

  private ClassLoaderHelper ()
  {}

  @Nonnull
  public static ClassLoader getSystemClassLoader ()
  {
    return IPrivilegedAction.getSystemClassLoader ().invokeSafe ();
  }

  @Nonnull
  public static ClassLoader getContextClassLoader ()
  {
    return IPrivilegedAction.getContextClassLoader ().invokeSafe ();
  }

  public static void setContextClassLoader (@Nonnull final ClassLoader aClassLoader)
  {
    IPrivilegedAction.setContextClassLoader (aClassLoader).invokeSafe ();
  }

  @Nonnull
  public static ClassLoader getClassClassLoader (@Nonnull final Class <?> aClass)
  {
    return IPrivilegedAction.getClassLoader (aClass).invokeSafe ();
  }

  @Nonnull
  public static ClassLoader getParentClassLoader (@Nonnull final ClassLoader aClassLoader)
  {
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

    // returns null if not found
    return aClassLoader.getResource (sPathWithoutSlash);
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
  public static InputStream getResourceAsStream (@Nonnull final ClassLoader aClassLoader,
                                                 @Nonnull @Nonempty final String sPath)
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
