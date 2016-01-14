/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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

import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.string.StringHelper;

/**
 * {@link Class} helper methods.
 *
 * @author Philip Helger
 */
@Immutable
public final class ClassHelper
{
  // WeakHashMap because class is used as a key
  private static final Map <Class <?>, Class <?>> PRIMITIVE_TO_WRAPPER = new WeakHashMap <Class <?>, Class <?>> (8);
  private static final Map <Class <?>, Class <?>> WRAPPER_TO_PRIMITIVE = new WeakHashMap <Class <?>, Class <?>> (8);

  static
  {
    _registerPrimitiveMapping (boolean.class, Boolean.class);
    _registerPrimitiveMapping (byte.class, Byte.class);
    _registerPrimitiveMapping (char.class, Character.class);
    _registerPrimitiveMapping (double.class, Double.class);
    _registerPrimitiveMapping (float.class, Float.class);
    _registerPrimitiveMapping (int.class, Integer.class);
    _registerPrimitiveMapping (long.class, Long.class);
    _registerPrimitiveMapping (short.class, Short.class);
  }

  private static void _registerPrimitiveMapping (@Nonnull final Class <?> aPrimitiveType,
                                                 @Nonnull final Class <?> aPrimitiveWrapperType)
  {
    PRIMITIVE_TO_WRAPPER.put (aPrimitiveType, aPrimitiveWrapperType);
    WRAPPER_TO_PRIMITIVE.put (aPrimitiveWrapperType, aPrimitiveType);
  }

  @PresentForCodeCoverage
  private static final ClassHelper s_aInstance = new ClassHelper ();

  private ClassHelper ()
  {}

  public static boolean isPublicClass (@Nullable final Class <?> aClass)
  {
    if (aClass == null)
      return false;

    // Interfaces or annotations are not allowed
    if (aClass.isInterface () || aClass.isAnnotation ())
      return false;

    // Only public classes are allowed
    if (!isPublic (aClass))
      return false;

    // Abstract classes are not allowed
    if (isAbstractClass (aClass))
      return false;

    return true;
  }

  /**
   * Check if the passed class is public, instancable and has a no-argument
   * constructor.
   *
   * @param aClass
   *        The class to check. May be <code>null</code>.
   * @return <code>true</code> if the class is public, instancable and has a
   *         no-argument constructor that is public.
   */
  public static boolean isInstancableClass (@Nullable final Class <?> aClass)
  {
    if (!isPublicClass (aClass))
      return false;

    // Check if a default constructor is present
    try
    {
      aClass.getConstructor ((Class <?> []) null);
    }
    catch (final NoSuchMethodException ex)
    {
      return false;
    }
    return true;
  }

  public static boolean isPublic (@Nullable final Class <?> aClass)
  {
    return aClass != null && Modifier.isPublic (aClass.getModifiers ());
  }

  /**
   * Check if the passed class is an interface or not. Please note that
   * annotations are also interfaces!
   *
   * @param aClass
   *        The class to check.
   * @return <code>true</code> if the class is an interface (or an annotation)
   */
  public static boolean isInterface (@Nullable final Class <?> aClass)
  {
    return aClass != null && Modifier.isInterface (aClass.getModifiers ());
  }

  public static boolean isAnnotationClass (@Nullable final Class <?> aClass)
  {
    return aClass != null && aClass.isAnnotation ();
  }

  public static boolean isEnumClass (@Nullable final Class <?> aClass)
  {
    return aClass != null && aClass.isEnum ();
  }

  /**
   * Check if the passed class is abstract or not. Note: interfaces and
   * annotations are also considered as abstract whereas arrays are never
   * abstract.
   *
   * @param aClass
   *        The class to check.
   * @return <code>true</code> if the passed class is abstract
   */
  public static boolean isAbstractClass (@Nullable final Class <?> aClass)
  {
    // Special case for arrays (see documentation of Class.getModifiers: only
    // final and interface are set, the rest is indeterministic)
    return aClass != null && !aClass.isArray () && Modifier.isAbstract (aClass.getModifiers ());
  }

  public static boolean isArrayClass (@Nullable final Class <?> aClass)
  {
    return aClass != null && aClass.isArray ();
  }

  public static boolean isPrimitiveType (@Nullable final Class <?> aClass)
  {
    return aClass != null && PRIMITIVE_TO_WRAPPER.containsKey (aClass);
  }

  public static boolean isPrimitiveWrapperType (@Nullable final Class <?> aClass)
  {
    return aClass != null && WRAPPER_TO_PRIMITIVE.containsKey (aClass);
  }

  /**
   * Get the primitive wrapper class of the passed primitive class.
   *
   * @param aClass
   *        The primitive class. May be <code>null</code>.
   * @return <code>null</code> if the passed class is not a primitive class.
   */
  @Nullable
  public static Class <?> getPrimitiveWrapperClass (@Nullable final Class <?> aClass)
  {
    if (isPrimitiveWrapperType (aClass))
      return aClass;
    return PRIMITIVE_TO_WRAPPER.get (aClass);
  }

  /**
   * Get the primitive class of the passed primitive wrapper class.
   *
   * @param aClass
   *        The primitive wrapper class. May be <code>null</code>.
   * @return <code>null</code> if the passed class is not a primitive wrapper
   *         class.
   */
  @Nullable
  public static Class <?> getPrimitiveClass (@Nullable final Class <?> aClass)
  {
    if (isPrimitiveType (aClass))
      return aClass;
    return WRAPPER_TO_PRIMITIVE.get (aClass);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static Set <Class <?>> getAllPrimitiveClasses ()
  {
    return CollectionHelper.newSet (PRIMITIVE_TO_WRAPPER.keySet ());
  }

  @Nonnull
  @ReturnsMutableCopy
  public static Set <Class <?>> getAllPrimitiveWrapperClasses ()
  {
    return CollectionHelper.newSet (WRAPPER_TO_PRIMITIVE.keySet ());
  }

  public static boolean isStringClass (@Nullable final Class <?> aClass)
  {
    if (aClass == null)
      return false;
    // Base class of String, StringBuffer and StringBuilder
    return CharSequence.class.isAssignableFrom (aClass);
  }

  public static boolean isCharacterClass (@Nullable final Class <?> aClass)
  {
    if (aClass == null)
      return false;
    return Character.class.isAssignableFrom (aClass) || char.class.isAssignableFrom (aClass);
  }

  public static boolean isBooleanClass (@Nullable final Class <?> aClass)
  {
    if (aClass == null)
      return false;
    return Boolean.class.isAssignableFrom (aClass) || boolean.class.isAssignableFrom (aClass);
  }

  public static boolean isFloatingPointClass (@Nullable final Class <?> aClass)
  {
    if (aClass == null)
      return false;
    return Double.class.isAssignableFrom (aClass) ||
           double.class.isAssignableFrom (aClass) ||
           Float.class.isAssignableFrom (aClass) ||
           float.class.isAssignableFrom (aClass) ||
           BigDecimal.class.isAssignableFrom (aClass);
  }

  public static boolean isIntegerClass (@Nullable final Class <?> aClass)
  {
    if (aClass == null)
      return false;
    return Byte.class.isAssignableFrom (aClass) ||
           byte.class.isAssignableFrom (aClass) ||
           Integer.class.isAssignableFrom (aClass) ||
           int.class.isAssignableFrom (aClass) ||
           Long.class.isAssignableFrom (aClass) ||
           long.class.isAssignableFrom (aClass) ||
           Short.class.isAssignableFrom (aClass) ||
           short.class.isAssignableFrom (aClass) ||
           BigInteger.class.isAssignableFrom (aClass);
  }

  /**
   * Check if the passed classes are convertible. Includes conversion checks
   * between primitive types and primitive wrapper types.
   *
   * @param aSrcClass
   *        First class. May not be <code>null</code>.
   * @param aDstClass
   *        Second class. May not be <code>null</code>.
   * @return <code>true</code> if the classes are directly convertible.
   */
  public static boolean areConvertibleClasses (@Nonnull final Class <?> aSrcClass, @Nonnull final Class <?> aDstClass)
  {
    ValueEnforcer.notNull (aSrcClass, "SrcClass");
    ValueEnforcer.notNull (aDstClass, "DstClass");

    // Same class?
    if (aDstClass.equals (aSrcClass))
      return true;

    // Default assignable
    if (aDstClass.isAssignableFrom (aSrcClass))
      return true;

    // Special handling for "int.class" == "Integer.class" etc.
    if (aDstClass == getPrimitiveWrapperClass (aSrcClass))
      return true;
    if (aDstClass == getPrimitiveClass (aSrcClass))
      return true;

    // Not convertible
    return false;
  }

  /**
   * <code>null</code>-safe helper method to determine the class of an object.
   *
   * @param aObject
   *        The object to query. May be <code>null</code>.
   * @return <code>null</code> if the passed object is <code>null</code>.
   */
  @Nullable
  public static Class <?> getClass (@Nullable final Object aObject)
  {
    return aObject == null ? null : aObject.getClass ();
  }

  /**
   * <code>null</code>-safe helper method to determine the class name of an
   * object.
   *
   * @param aObject
   *        The object to query. May be <code>null</code>.
   * @return <code>null</code> if the passed object is <code>null</code>.
   */
  @Nullable
  public static String getClassName (@Nullable final Object aObject)
  {
    return aObject == null ? null : aObject.getClass ().getName ();
  }

  /**
   * Get the name of the object's class without the package.
   *
   * @param aObject
   *        The object to get the information from. May be <code>null</code> .
   * @return The local name of the passed object's class.
   */
  @Nullable
  public static String getClassLocalName (@Nullable final Object aObject)
  {
    return aObject == null ? null : getClassLocalName (aObject.getClass ());
  }

  /**
   * Get the name of the class without the package.
   *
   * @param aClass
   *        The class to get the information from. May be <code>null</code>.
   * @return The local name of the passed class.
   */
  @Nullable
  public static String getClassLocalName (@Nullable final Class <?> aClass)
  {
    return aClass == null ? null : getClassLocalName (aClass.getName ());
  }

  /**
   * Get the name of the class without the package.
   *
   * @param sClassName
   *        The fully qualified name of the class. May be <code>null</code>.
   * @return The local name of the passed class. Never <code>null</code>.
   */
  @Nullable
  public static String getClassLocalName (@Nullable final String sClassName)
  {
    if (sClassName == null)
      return null;

    final int nIndex = sClassName.lastIndexOf ('.');
    return nIndex == -1 ? sClassName : sClassName.substring (nIndex + 1);
  }

  /**
   * Get the name of the package the passed object resides in.
   *
   * @param aObject
   *        The class to get the information from. May be <code>null</code>.
   * @return The package name of the passed object.
   */
  @Nullable
  public static String getClassPackageName (@Nullable final Object aObject)
  {
    return aObject == null ? null : getClassPackageName (aObject.getClass ());
  }

  /**
   * Get the name of the package the passed class resides in.
   *
   * @param aClass
   *        The class to get the information from. May not be <code>null</code>.
   * @return The package name of the passed class.
   */
  @Nullable
  public static String getClassPackageName (@Nullable final Class <?> aClass)
  {
    return aClass == null ? null : getClassPackageName (aClass.getName ());
  }

  /**
   * Get the name of the package the passed class resides in.
   *
   * @param sClassName
   *        The name class to get the information from. May be <code>null</code>
   *        .
   * @return The package name of the passed class.
   */
  @Nullable
  public static String getClassPackageName (@Nullable final String sClassName)
  {
    if (sClassName == null)
      return null;

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
  @Nonempty
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
   *        The package to be converted. May be <code>null</code>.
   * @return The directory name using forward slashes (/) instead of the dots.
   */
  @Nullable
  public static String getDirectoryFromPackage (@Nullable final Package aPackage)
  {
    // No differentiation
    return aPackage == null ? null : getPathFromClass (aPackage.getName ());
  }

  /**
   * Convert a package name to a relative directory name.
   *
   * @param sPackage
   *        The name of the package to be converted. May be <code>null</code>.
   * @return The directory name using forward slashes (/) instead of the dots.
   */
  @Nullable
  public static String getDirectoryFromPackage (@Nullable final String sPackage)
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
   *        The class of which the path is to be retrieved. May be
   *        <code>null</code>.
   * @return The path representation. Never <code>null</code>.
   */
  @Nullable
  public static String getPathFromClass (@Nullable final Class <?> aClass)
  {
    return aClass == null ? null : getPathFromClass (aClass.getName ());
  }

  /**
   * Get the path representation of the passed class name. The path
   * representation is achieved by replacing all dots (.) with forward slashes
   * (/) in the class name.
   *
   * @param sClassName
   *        The class name of which the path is to be retrieved. May be
   *        <code>null</code>.
   * @return The path representation
   */
  @Nullable
  public static String getPathFromClass (@Nullable final String sClassName)
  {
    return sClassName == null ? null : sClassName.replace ('.', '/');
  }

  /**
   * Get the class name of the passed path. The class name is retrieved by
   * replacing all path separators (\ and /) with dots (.). This method does not
   * handle the file extension, so it's up to the caller to skip of any file
   * extension!
   *
   * @param sPath
   *        The path to be converted. May be <code>null</code>.
   * @return The class name.
   */
  @Nullable
  public static String getClassFromPath (@Nullable final String sPath)
  {
    return sPath == null ? null : sPath.replace ('\\', '.').replace ('/', '.');
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

  @Nonnull
  private static String _getPathWithLeadingSlash (@Nonnull @Nonempty final String sPath)
  {
    return sPath.charAt (0) == '/' ? sPath : "/" + sPath;
  }

  /**
   * Get the URL of the passed resource using the class loader of the specified
   * class only. This is a sanity wrapper around
   * <code>class.getResource (sPath)</code>.
   *
   * @param aClass
   *        The class to be used. May not be <code>null</code>.
   * @param sPath
   *        The path to be resolved. May neither be <code>null</code> nor empty.
   *        Internally it is ensured that the provided path does start with a
   *        slash.
   * @return <code>null</code> if the path could not be resolved using the
   *         specified class loader.
   */
  @Nullable
  public static URL getResource (@Nonnull final Class <?> aClass, @Nonnull @Nonempty final String sPath)
  {
    ValueEnforcer.notNull (aClass, "Class");
    ValueEnforcer.notEmpty (sPath, "Path");

    // Ensure the path does start with a "/"
    final String sPathWithSlash = _getPathWithLeadingSlash (sPath);

    // returns null if not found
    return aClass.getResource (sPathWithSlash);
  }

  /**
   * Get the input stream of the passed resource using the class loader of the
   * specified class only. This is a sanity wrapper around
   * <code>class.getResourceAsStream (sPath)</code>.
   *
   * @param aClass
   *        The class to be used. May not be <code>null</code>.
   * @param sPath
   *        The path to be resolved. May neither be <code>null</code> nor empty.
   *        Internally it is ensured that the provided path does start with a
   *        slash.
   * @return <code>null</code> if the path could not be resolved using the
   *         specified class loader.
   */
  @Nullable
  public static InputStream getResourceAsStream (@Nonnull final Class <?> aClass, @Nonnull @Nonempty final String sPath)
  {
    ValueEnforcer.notNull (aClass, "Class");
    ValueEnforcer.notEmpty (sPath, "Path");

    // Ensure the path does start with a "/"
    final String sPathWithSlash = _getPathWithLeadingSlash (sPath);

    // returns null if not found
    final InputStream aIS = aClass.getResourceAsStream (sPathWithSlash);
    return StreamHelper.checkForInvalidFilterInputStream (aIS);
  }
}
