/**
 * Copyright (C) 2006-2014 phloc systems (www.phloc.com)
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.Nonempty;
import com.helger.commons.annotations.PresentForCodeCoverage;
import com.helger.commons.string.StringHelper;

/**
 * Special string helper especially for Java internal class name handling etc.
 * CG = Code Generation.
 * 
 * @author Philip Helger
 */
@Immutable
public final class CGStringHelper
{
  @PresentForCodeCoverage
  @SuppressWarnings ("unused")
  private static final CGStringHelper s_aInstance = new CGStringHelper ();

  private CGStringHelper ()
  {}

  /**
   * Get the name of the object's class without the package.
   * 
   * @param aObject
   *        The object to get the information from. May not be <code>null</code>
   *        .
   * @return The local name of the passed object's class.
   */
  @Nonnull
  public static String getClassLocalName (@Nonnull final Object aObject)
  {
    ValueEnforcer.notNull (aObject, "Object");

    return getClassLocalName (aObject.getClass ());
  }

  /**
   * Get the name of the class without the package.
   * 
   * @param aClass
   *        The class to get the information from. May not be <code>null</code>.
   * @return The local name of the passed class.
   */
  @Nonnull
  public static String getClassLocalName (@Nonnull final Class <?> aClass)
  {
    ValueEnforcer.notNull (aClass, "Class");

    return getClassLocalName (aClass.getName ());
  }

  /**
   * Get the name of the class without the package.
   * 
   * @param sClassName
   *        The fully qualified name of the class. May not be <code>null</code>.
   * @return The local name of the passed class. Never <code>null</code>.
   */
  @Nonnull
  public static String getClassLocalName (@Nonnull final String sClassName)
  {
    ValueEnforcer.notNull (sClassName, "ClassName");

    final int nIndex = sClassName.lastIndexOf ('.');
    return nIndex == -1 ? sClassName : sClassName.substring (nIndex + 1);
  }

  /**
   * Get the name of the package the passed class resides in.
   * 
   * @param aClass
   *        The class to get the information from. May not be <code>null</code>.
   * @return The package name of the passed class.
   */
  @Nonnull
  public static String getClassPackageName (@Nonnull final Class <?> aClass)
  {
    ValueEnforcer.notNull (aClass, "Class");

    return getClassPackageName (aClass.getName ());
  }

  /**
   * Get the name of the package the passed class resides in.
   * 
   * @param sClassName
   *        The name class to get the information from. May not be
   *        <code>null</code>.
   * @return The package name of the passed class.
   */
  @Nonnull
  public static String getClassPackageName (@Nonnull final String sClassName)
  {
    ValueEnforcer.notNull (sClassName, "ClassName");

    final int nIndex = sClassName.lastIndexOf ('.');
    return nIndex == -1 ? "" : sClassName.substring (0, nIndex);
  }

  /**
   * Get the class name of the passed object. If the object itself is of type
   * {@link Class}, its name is retrieved, other {@link #getClass()} is called.
   * 
   * @param aObject
   *        The object who's class name is to be retrieved.
   * @return <code>&quot;null&quot;</code> for a <code>null</code> parameter
   */
  @Nonnull
  public static String getSafeClassName (@Nullable final Object aObject)
  {
    if (aObject instanceof Class <?>)
      return ((Class <?>) aObject).getName ();
    if (aObject != null)
      return aObject.getClass ().getName ();
    return "null";
  }

  /**
   * Convert a package name to a relative directory name.
   * 
   * @param aPackage
   *        The package to be converted. May not be <code>null</code>.
   * @return The directory name using forward slashes (/) instead of the dots.
   */
  @Nonnull
  public static String getDirectoryFromPackage (@Nonnull final Package aPackage)
  {
    ValueEnforcer.notNull (aPackage, "Package");

    // No differentiation
    return getPathFromClass (aPackage.getName ());
  }

  /**
   * Convert a package name to a relative directory name.
   * 
   * @param sPackage
   *        The name of the package to be converted. May not be
   *        <code>null</code>.
   * @return The directory name using forward slashes (/) instead of the dots.
   */
  @Nonnull
  public static String getDirectoryFromPackage (@Nonnull final String sPackage)
  {
    // No differentiation
    return getPathFromClass (sPackage);
  }

  /**
   * Get the path representation of the passed class. The path representation is
   * achieved by replacing all dots (.) with forward slashes (/) in the class
   * name.
   * 
   * @param aClass
   *        The class of which the path is to be retrieved. May not be
   *        <code>null</code>.
   * @return The path representation. Never <code>null</code>.
   */
  @Nonnull
  public static String getPathFromClass (@Nonnull final Class <?> aClass)
  {
    ValueEnforcer.notNull (aClass, "Class");

    return getPathFromClass (aClass.getName ());
  }

  /**
   * Get the path representation of the passed class name. The path
   * representation is achieved by replacing all dots (.) with forward slashes
   * (/) in the class name.
   * 
   * @param sClassName
   *        The class name of which the path is to be retrieved.
   * @return The path representation
   */
  @Nonnull
  public static String getPathFromClass (@Nonnull final String sClassName)
  {
    ValueEnforcer.notNull (sClassName, "ClassName");

    return sClassName.replace ('.', '/');
  }

  /**
   * Get the class name of the passed path. The class name is retrieved by
   * replacing all path separators (\ and /) with dots (.). This method does not
   * handle the file extension, so it's up to the caller to skip of any file
   * extension!
   * 
   * @param sPath
   *        The path to be converted. May not be <code>null</code>.
   * @return The class name.
   */
  @Nonnull
  public static String getClassFromPath (@Nonnull final String sPath)
  {
    ValueEnforcer.notNull (sPath, "Path");

    return sPath.replace ('\\', '.').replace ('/', '.');
  }

  /**
   * Get the hex representation of the passed object's address. Note that this
   * method makes no differentiation between 32 and 64 bit architectures. The
   * result is always a hexadecimal value preceded by "0x" and followed by
   * exactly 8 characters.
   * 
   * @param aObject
   *        The object who's address is to be retrieved. May be
   *        <code>null</code>.
   * @return Depending on the current architecture. Always starting with "0x"
   *         and than containing the address.
   * @see System#identityHashCode(Object)
   */
  @Nonnull
  @Nonempty
  public static String getObjectAddress (@Nullable final Object aObject)
  {
    if (aObject == null)
      return "0x00000000";
    return "0x" + StringHelper.getHexStringLeadingZero (System.identityHashCode (aObject), 8);
  }
}
