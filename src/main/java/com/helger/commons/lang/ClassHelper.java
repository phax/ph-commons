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
package com.helger.commons.lang;

import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.AccessController;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.PresentForCodeCoverage;
import com.helger.commons.annotations.ReturnsImmutableObject;
import com.helger.commons.collections.ContainerHelper;
import com.helger.commons.priviledged.PrivilegedActionGetClassLoader;
import com.helger.commons.priviledged.PrivilegedActionGetContextClassLoader;
import com.helger.commons.priviledged.PrivilegedActionGetSystemClassLoader;

/**
 * Small class helper utility stuff class.
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
  @SuppressWarnings ("unused")
  private static final ClassHelper s_aInstance = new ClassHelper ();

  private ClassHelper ()
  {}

  @Nonnull
  public static ClassLoader getSystemClassLoader ()
  {
    if (System.getSecurityManager () == null)
      return ClassLoader.getSystemClassLoader ();

    return AccessController.doPrivileged (new PrivilegedActionGetSystemClassLoader ());
  }

  @Nonnull
  public static ClassLoader getContextClassLoader ()
  {
    if (System.getSecurityManager () == null)
      return Thread.currentThread ().getContextClassLoader ();

    return AccessController.doPrivileged (new PrivilegedActionGetContextClassLoader ());
  }

  @Nonnull
  public static ClassLoader getClassClassLoader (@Nonnull final Class <?> aClass)
  {
    if (System.getSecurityManager () == null)
      return aClass.getClassLoader ();

    return AccessController.doPrivileged (new PrivilegedActionGetClassLoader (aClass));
  }

  @Nonnull
  public static ClassLoader getDefaultClassLoader ()
  {
    ClassLoader ret = null;
    try
    {
      ret = getContextClassLoader ();
    }
    catch (final Exception ex) // NOPMD
    {
      // e.g. security exception
    }

    // Fallback to class loader of this class
    if (ret == null)
      ret = getClassClassLoader (ClassHelper.class);

    return ret;
  }

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
  @ReturnsImmutableObject
  public static Set <Class <?>> getAllPrimitiveClasses ()
  {
    return ContainerHelper.makeUnmodifiable (PRIMITIVE_TO_WRAPPER.keySet ());
  }

  @Nonnull
  @ReturnsImmutableObject
  public static Set <Class <?>> getAllPrimitiveWrapperClasses ()
  {
    return ContainerHelper.makeUnmodifiable (WRAPPER_TO_PRIMITIVE.keySet ());
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
}
