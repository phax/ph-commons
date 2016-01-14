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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.collection.ArrayHelper;

/**
 * This is a special helper class that provides many utility methods that
 * require the <code>SuppressWarnings("unchecked")</code> annotation.
 *
 * @author Philip Helger
 */
@Immutable
public final class GenericReflection
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (GenericReflection.class);

  private static final Class <?> [] EMPTY_CLASS_ARRAY = new Class <?> [0];

  @PresentForCodeCoverage
  private static final GenericReflection s_aInstance = new GenericReflection ();

  private GenericReflection ()
  {}

  @SuppressWarnings ("unchecked")
  public static <SRCTYPE, DSTTYPE> DSTTYPE uncheckedCast (@Nullable final SRCTYPE aObject)
  {
    return (DSTTYPE) aObject;
  }

  @Nonnull
  public static <DATATYPE> Class <DATATYPE> getClassFromName (@Nonnull final ClassLoader aClassLoader,
                                                              @Nonnull final String sName) throws ClassNotFoundException
  {
    ValueEnforcer.notNull (aClassLoader, "ClassLoader");
    return uncheckedCast (aClassLoader.loadClass (sName));
  }

  @Nonnull
  public static <DATATYPE> Class <DATATYPE> getClassFromName (@Nonnull final String sName) throws ClassNotFoundException
  {
    return uncheckedCast (Class.forName (sName));
  }

  /**
   * Get the class of the given name
   *
   * @param <DATATYPE>
   *        The return type
   * @param aClassLoader
   *        The class loader to be used. May not be <code>null</code>.
   * @param sName
   *        The name to be resolved.
   * @return <code>null</code> if the class could not be resolved
   */
  @Nullable
  public static <DATATYPE> Class <DATATYPE> getClassFromNameSafe (@Nonnull final ClassLoader aClassLoader,
                                                                  @Nonnull final String sName)
  {
    ValueEnforcer.notNull (aClassLoader, "ClassLoader");
    try
    {
      return getClassFromName (aClassLoader, sName);
    }
    catch (final ClassNotFoundException e)
    {
      return null;
    }
  }

  /**
   * Get the class of the given name
   *
   * @param <DATATYPE>
   *        The return type
   * @param sName
   *        The name to be resolved.
   * @return <code>null</code> if the class could not be resolved
   */
  @Nullable
  public static <DATATYPE> Class <DATATYPE> getClassFromNameSafe (@Nonnull final String sName)
  {
    try
    {
      return getClassFromName (sName);
    }
    catch (final ClassNotFoundException e)
    {
      return null;
    }
  }

  /**
   * Get an array with all the classes of the passed object array.
   *
   * @param aObjs
   *        The object array. May be <code>null</code>. No contained element may
   *        be <code>null</code>.
   * @return A non-<code>null</code> array of classes.
   */
  @Nonnull
  public static Class <?> [] getClassArray (@Nullable final Object... aObjs)
  {
    if (ArrayHelper.isEmpty (aObjs))
      return EMPTY_CLASS_ARRAY;

    final Class <?> [] ret = new Class <?> [aObjs.length];
    for (int i = 0; i < aObjs.length; ++i)
      ret[i] = aObjs[i].getClass ();
    return ret;
  }

  /**
   * This method dynamically invokes the method with the given name on the given
   * object.
   *
   * @param <RETURNTYPE>
   *        The method return type
   * @param aSrcObj
   *        The source object on which the method is to be invoked. May not be
   *        <code>null</code>.
   * @param sMethodName
   *        The method to be invoked.
   * @param aArgs
   *        The arguments to be passed into the method. May be <code>null</code>
   *        . If not <code>null</code>, the members of the array may not be
   *        <code>null</code> because otherwise the classes of the arguments
   *        cannot be determined and will throw an Exception!
   * @return The return value of the invoked method or <code>null</code> for
   *         void methods.
   * @throws NoSuchMethodException
   *         Thrown by reflection
   * @throws IllegalAccessException
   *         Thrown by reflection
   * @throws InvocationTargetException
   *         Thrown by reflection
   */
  @Nullable
  public static <RETURNTYPE> RETURNTYPE invokeMethod (@Nonnull final Object aSrcObj,
                                                      @Nonnull final String sMethodName,
                                                      @Nullable final Object... aArgs) throws NoSuchMethodException,
                                                                                       IllegalAccessException,
                                                                                       InvocationTargetException
  {
    return GenericReflection.<RETURNTYPE> invokeMethod (aSrcObj, sMethodName, getClassArray (aArgs), aArgs);
  }

  @Nullable
  public static <RETURNTYPE> RETURNTYPE invokeMethod (@Nonnull final Object aSrcObj,
                                                      @Nonnull final String sMethodName,
                                                      @Nullable final Class <?> [] aArgClasses,
                                                      @Nullable final Object [] aArgs) throws NoSuchMethodException,
                                                                                       IllegalAccessException,
                                                                                       InvocationTargetException
  {
    final Method aMethod = aSrcObj.getClass ().getDeclaredMethod (sMethodName, aArgClasses);
    final Object aReturn = aMethod.invoke (aSrcObj, aArgs);
    return GenericReflection.<Object, RETURNTYPE> uncheckedCast (aReturn);
  }

  @Nullable
  public static <RETURNTYPE> RETURNTYPE invokeStaticMethod (@Nonnull final String sClassName,
                                                            @Nonnull final String sMethodName,
                                                            @Nullable final Object... aArgs) throws NoSuchMethodException,
                                                                                             IllegalAccessException,
                                                                                             InvocationTargetException,
                                                                                             ClassNotFoundException
  {
    return GenericReflection.<RETURNTYPE> invokeStaticMethod (getClassFromName (sClassName), sMethodName, aArgs);
  }

  @Nullable
  public static <RETURNTYPE> RETURNTYPE invokeStaticMethod (@Nonnull final Class <?> aClass,
                                                            @Nonnull final String sMethodName,
                                                            @Nullable final Object... aArgs) throws NoSuchMethodException,
                                                                                             IllegalAccessException,
                                                                                             InvocationTargetException
  {
    return GenericReflection.<RETURNTYPE> invokeStaticMethod (aClass, sMethodName, getClassArray (aArgs), aArgs);
  }

  @Nullable
  public static <RETURNTYPE> RETURNTYPE invokeStaticMethod (@Nonnull final String sClassName,
                                                            @Nonnull final String sMethodName,
                                                            @Nullable final Class <?> [] aArgClasses,
                                                            @Nullable final Object [] aArgs) throws NoSuchMethodException,
                                                                                             IllegalAccessException,
                                                                                             InvocationTargetException,
                                                                                             ClassNotFoundException
  {
    return GenericReflection.<RETURNTYPE> invokeStaticMethod (getClassFromName (sClassName),
                                                              sMethodName,
                                                              aArgClasses,
                                                              aArgs);
  }

  @Nullable
  public static <RETURNTYPE> RETURNTYPE invokeStaticMethod (@Nonnull final Class <?> aClass,
                                                            @Nonnull final String sMethodName,
                                                            @Nullable final Class <?> [] aArgClasses,
                                                            @Nullable final Object [] aArgs) throws NoSuchMethodException,
                                                                                             IllegalAccessException,
                                                                                             InvocationTargetException
  {
    final Method aMethod = aClass.getDeclaredMethod (sMethodName, aArgClasses);
    final Object aReturn = aMethod.invoke (null, aArgs);
    return GenericReflection.<Object, RETURNTYPE> uncheckedCast (aReturn);
  }

  @Nonnull
  public static <DATATYPE> Constructor <DATATYPE> findConstructor (@Nonnull final DATATYPE aObj,
                                                                   final Class <?>... aCtorArgs) throws NoSuchMethodException
  {
    return uncheckedCast (aObj.getClass ().getConstructor (aCtorArgs));
  }

  /**
   * Create a new instance of the class identified by the passed object. The
   * default constructor will be invoked.
   *
   * @param <DATATYPE>
   *        The type of object to be created.
   * @param aObj
   *        The object from which the class should be used.
   * @return A new instance of the object or an exception is thrown.
   * @throws IllegalAccessException
   *         Reflection exception
   * @throws NoSuchMethodException
   *         Reflection exception
   * @throws InvocationTargetException
   *         Reflection exception
   * @throws InstantiationException
   *         Reflection exception
   */
  @Nonnull
  public static <DATATYPE> DATATYPE newInstance (@Nonnull final DATATYPE aObj) throws IllegalAccessException,
                                                                               NoSuchMethodException,
                                                                               InvocationTargetException,
                                                                               InstantiationException
  {
    return findConstructor (aObj).newInstance ();
  }

  @Nullable
  public static <DATATYPE> DATATYPE newInstance (@Nullable final Class <? extends DATATYPE> aClass)
  {
    if (aClass != null)
      try
      {
        return aClass.newInstance ();
      }
      catch (final Throwable t)
      {
        /*
         * Catch all exceptions because any exception thrown from the
         * constructor may also end up in this catch block
         */
        s_aLogger.error ("Failed to instantiate " + aClass, t);
      }
    return null;
  }

  @Nullable
  public static <DATATYPE> DATATYPE newInstance (@Nonnull final ClassLoader aClassLoader,
                                                 @Nullable final String sClassName,
                                                 @Nullable final Class <? extends DATATYPE> aDesiredType)
  {
    if (sClassName != null && aDesiredType != null)
      try
      {
        return aDesiredType.cast (getClassFromName (aClassLoader, sClassName).newInstance ());
      }
      catch (final Throwable t)
      {
        /*
         * Catch all exceptions because any exception thrown from the
         * constructor (indirectly invoked by newInstance) may also end up in
         * this catch block
         */
        s_aLogger.error ("Failed to instantiate '" + sClassName + "'", t);
      }
    return null;
  }

  @Nullable
  public static <DATATYPE> DATATYPE newInstance (@Nullable final String sClassName,
                                                 @Nullable final Class <? extends DATATYPE> aDesiredType)
  {
    if (sClassName != null && aDesiredType != null)
      try
      {
        return aDesiredType.cast (getClassFromName (sClassName).newInstance ());
      }
      catch (final Throwable t)
      {
        /*
         * Catch all exceptions because any exception thrown from the
         * constructor (indirectly invoked by newInstance) may also end up in
         * this catch block
         */
        s_aLogger.error ("Failed to instantiate '" + sClassName + "'", t);
      }
    return null;
  }

  @Nullable
  public static <DATATYPE> DATATYPE newInstance (@Nullable final String sClassName,
                                                 @Nullable final Class <DATATYPE> aDesiredType,
                                                 @Nullable final ClassLoader aClassLoaderToUse)
  {
    if (sClassName != null && aDesiredType != null && aClassLoaderToUse != null)
      try
      {
        return aDesiredType.cast (Class.forName (sClassName, true, aClassLoaderToUse).newInstance ());
      }
      catch (final Throwable t)
      {
        /*
         * Catch all exceptions because any exception thrown from the
         * constructor (indirectly invoked by newInstance) may also end up in
         * this catch block
         */
        s_aLogger.error ("Failed to instantiate '" + sClassName + "' with CL " + aClassLoaderToUse, t);
      }
    return null;
  }
}
