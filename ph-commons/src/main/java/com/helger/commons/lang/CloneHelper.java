/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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

import com.helger.annotation.Nonnull;
import com.helger.annotation.Nullable;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.misc.PresentForCodeCoverage;
import com.helger.annotation.misc.ReturnsMutableCopy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;

/**
 * Helper class for cloning objects.
 *
 * @author Philip Helger
 */
@Immutable
public final class CloneHelper
{
  private static final Logger LOGGER = LoggerFactory.getLogger (CloneHelper.class);

  @PresentForCodeCoverage
  private static final CloneHelper INSTANCE = new CloneHelper ();

  private CloneHelper ()
  {}

  @Nullable
  private static <DATATYPE> DATATYPE _getGenericClone (@Nonnull final DATATYPE aObject)
  {
    // 1. check ICloneable interface
    if (aObject instanceof ICloneable <?>)
      return GenericReflection.<DATATYPE, ICloneable <DATATYPE>> uncheckedCast (aObject).getClone ();
    try
    {
      // 2. find Object.clone method
      return GenericReflection.invokeMethod (aObject, "clone");
    }
    catch (final Exception ex)
    {
      LOGGER.warn ("Failed to invoke clone on " + aObject.getClass ().getName ());
      try
      {
        // 3. find copy-constructor
        final Constructor <DATATYPE> aCtor = GenericReflection.findConstructor (aObject, aObject.getClass ());
        if (aCtor != null)
          return aCtor.newInstance (aObject);
      }
      catch (final IllegalAccessException ex2)
      {
        LOGGER.error ("Failed to clone object of type '" +
                      aObject.getClass ().getName () +
                      "' because it has neither a (visible) clone method nor a copy constructor or the methods are invisible.");
      }
      catch (final NoSuchMethodException ex2)
      {
        LOGGER.error ("Failed to clone object of type '" +
                      aObject.getClass ().getName () +
                      "' because it has neither a clone method nor a (visible) copy constructor or the methods are invisible.");
      }
      catch (final Exception ex2)
      {
        LOGGER.error ("Failed to clone object of type '" +
                      aObject.getClass ().getName () +
                      "' because it has neither a (visible) clone method nor a copy constructor.",
                      ex2);
      }
    }
    return null;
  }

  /**
   * Get a clone (= deep copy) of the passed value. The following things are
   * tried for cloning:
   * <ol>
   * <li>If the object is immutable, it is returned as is (if it is a primitive
   * type or marked with the {@link Immutable} annotation.</li>
   * <li>If the object implements {@link ICloneable} it is invoked.</li>
   * <li>If the object implements {@link Cloneable} it is invoked.</li>
   * <li>If a copy constructor (a constructor taking one argument of the same
   * class as it declares)</li>
   * </ol>
   * If all tries fail, <code>null</code> is returned.
   *
   * @param <DATATYPE>
   *        The source and return type
   * @param aObject
   *        The object to be copied.
   * @return <code>null</code> if the passed value is <code>null</code> or if no
   *         cloning could be performed.
   */
  @Nullable
  public static <DATATYPE> DATATYPE getClonedValue (@Nullable final DATATYPE aObject)
  {
    // null -> null
    if (aObject == null)
      return null;

    final Class <?> aClass = aObject.getClass ();

    // special handling for immutable objects without equals or clone
    if (ClassHelper.isPrimitiveWrapperType (aClass) ||
        aObject instanceof String ||
        aClass.getAnnotation (Immutable.class) != null)
      return aObject;

    // generic clone
    return _getGenericClone (aObject);
  }

  /**
   * Get a clone (= deep copy) of the passed value for all objects implementing
   * {@link ICloneable}.
   *
   * @param aObject
   *        The object to be copied. May be <code>null</code>.
   * @return <code>null</code> if the passed value is <code>null</code> or a
   *         clone of the object.
   * @param <DATATYPE>
   *        The data type to be cloned
   */
  @Nullable
  public static <DATATYPE extends ICloneable <DATATYPE>> DATATYPE getCloneIfNotNull (@Nullable final DATATYPE aObject)
  {
    // null -> null
    if (aObject == null)
      return null;
    return aObject.getClone ();
  }

  /**
   * Get a list where each contained item is also cloned. Like a deep copy.
   *
   * @param aList
   *        Source list. May be <code>null</code>.
   * @return The cloned list. Never <code>null</code> but maybe empty if the
   *         source list is empty.
   * @param <DATATYPE>
   *        The list element type to be cloned
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <DATATYPE> ICommonsList <DATATYPE> getGenericClonedList (@Nullable final Iterable <DATATYPE> aList)
  {
    final ICommonsList <DATATYPE> ret = new CommonsArrayList <> ();
    if (aList != null)
      for (final DATATYPE aItem : aList)
        ret.add (getClonedValue (aItem));
    return ret;
  }

  /**
   * Get a list where each contained item is also cloned. Like a deep copy.
   *
   * @param aList
   *        Source list. May be <code>null</code>.
   * @return The cloned list. Never <code>null</code> but maybe empty if the
   *         source list is empty.
   * @param <DATATYPE>
   *        The set element type to be cloned
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <DATATYPE extends ICloneable <DATATYPE>> ICommonsList <DATATYPE> getClonedList (@Nullable final Iterable <DATATYPE> aList)
  {
    final ICommonsList <DATATYPE> ret = new CommonsArrayList <> ();
    if (aList != null)
      for (final DATATYPE aItem : aList)
        ret.add (getCloneIfNotNull (aItem));
    return ret;
  }
}
