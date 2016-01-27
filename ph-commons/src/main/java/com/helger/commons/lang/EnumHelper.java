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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.CGlobal;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.concurrent.SimpleReadWriteLock;
import com.helger.commons.id.IHasID;
import com.helger.commons.id.IHasIntID;
import com.helger.commons.name.IHasName;
import com.helger.commons.state.EChange;
import com.helger.commons.string.StringHelper;

/**
 * Some enum utility methods.
 *
 * @author Philip Helger
 */
@Immutable
public final class EnumHelper
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (EnumHelper.class);
  private static final Object [] NOT_CACHABLE = ArrayHelper.EMPTY_OBJECT_ARRAY;
  private static final SimpleReadWriteLock s_aRWLockInt = new SimpleReadWriteLock ();
  @GuardedBy ("s_aRWLockInt")
  private static final Map <String, Object []> s_aIntCache = new HashMap <> ();

  @PresentForCodeCoverage
  private static final EnumHelper s_aInstance = new EnumHelper ();

  private EnumHelper ()
  {}

  @Nonnull
  public static <ENUMTYPE extends Enum <ENUMTYPE>> ENUMTYPE findFirst (@Nonnull final Class <ENUMTYPE> aClass,
                                                                       @Nullable final Predicate <ENUMTYPE> aFilter)
  {
    return findFirst (aClass, aFilter, null);
  }

  @Nonnull
  public static <ENUMTYPE extends Enum <ENUMTYPE>> ENUMTYPE findFirst (@Nonnull final Class <ENUMTYPE> aClass,
                                                                       @Nullable final Predicate <ENUMTYPE> aFilter,
                                                                       @Nullable final ENUMTYPE eDefault)
  {
    return ArrayHelper.findFirst (aClass.getEnumConstants (), aFilter, eDefault);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <ENUMTYPE extends Enum <ENUMTYPE>> List <ENUMTYPE> findAll (@Nonnull final Class <ENUMTYPE> aClass,
                                                                            @Nullable final Predicate <ENUMTYPE> aFilter)
  {
    return ArrayHelper.getAll (aClass.getEnumConstants (), aFilter);
  }

  /**
   * Get the enum value with the passed ID
   *
   * @param <KEYTYPE>
   *        The ID type
   * @param <ENUMTYPE>
   *        The enum type
   * @param aClass
   *        The enum class
   * @param aID
   *        The ID to search
   * @return <code>null</code> if no enum item with the given ID is present.
   */
  @Nullable
  public static <KEYTYPE, ENUMTYPE extends Enum <ENUMTYPE> & IHasID <KEYTYPE>> ENUMTYPE getFromIDOrNull (@Nonnull final Class <ENUMTYPE> aClass,
                                                                                                         @Nullable final KEYTYPE aID)
  {
    return getFromIDOrDefault (aClass, aID, null);
  }

  /**
   * Get the enum value with the passed ID
   *
   * @param <KEYTYPE>
   *        The ID type
   * @param <ENUMTYPE>
   *        The enum type
   * @param aClass
   *        The enum class
   * @param aID
   *        The ID to search
   * @param eDefault
   *        The default value to be returned, if the ID was not found.
   * @return The default parameter if no enum item with the given ID is present.
   */
  @Nullable
  public static <KEYTYPE, ENUMTYPE extends Enum <ENUMTYPE> & IHasID <KEYTYPE>> ENUMTYPE getFromIDOrDefault (@Nonnull final Class <ENUMTYPE> aClass,
                                                                                                            @Nullable final KEYTYPE aID,
                                                                                                            @Nullable final ENUMTYPE eDefault)
  {
    ValueEnforcer.notNull (aClass, "Class");

    if (aID == null)
      return eDefault;
    return findFirst (aClass, e -> e.getID ().equals (aID), eDefault);
  }

  /**
   * Get the enum value with the passed ID. If no such ID is present, an
   * {@link IllegalArgumentException} is thrown.
   *
   * @param <KEYTYPE>
   *        The ID type
   * @param <ENUMTYPE>
   *        The enum type
   * @param aClass
   *        The enum class
   * @param aID
   *        The ID to search
   * @return The enum item with the given ID. Never <code>null</code>.
   * @throws IllegalArgumentException
   *         if no enum item with the given ID is present
   */
  @Nonnull
  public static <KEYTYPE, ENUMTYPE extends Enum <ENUMTYPE> & IHasID <KEYTYPE>> ENUMTYPE getFromIDOrThrow (@Nonnull final Class <ENUMTYPE> aClass,
                                                                                                          @Nullable final KEYTYPE aID)
  {
    final ENUMTYPE aEnum = getFromIDOrNull (aClass, aID);
    if (aEnum == null)
      throw new IllegalArgumentException ("Failed to resolve ID " + aID + " within class " + aClass);
    return aEnum;
  }

  /**
   * Get the enum value with the passed string ID case insensitive
   *
   * @param <ENUMTYPE>
   *        The enum type
   * @param aClass
   *        The enum class
   * @param sID
   *        The ID to search
   * @return <code>null</code> if no enum item with the given ID is present.
   */
  @Nullable
  public static <ENUMTYPE extends Enum <ENUMTYPE> & IHasID <String>> ENUMTYPE getFromIDCaseInsensitiveOrNull (@Nonnull final Class <ENUMTYPE> aClass,
                                                                                                              @Nullable final String sID)
  {
    return getFromIDCaseInsensitiveOrDefault (aClass, sID, null);
  }

  /**
   * Get the enum value with the passed string ID case insensitive
   *
   * @param <ENUMTYPE>
   *        The enum type
   * @param aClass
   *        The enum class
   * @param sID
   *        The ID to search
   * @param eDefault
   *        The default value to be returned, if the ID was not found.
   * @return The default parameter if no enum item with the given ID is present.
   */
  @Nullable
  public static <ENUMTYPE extends Enum <ENUMTYPE> & IHasID <String>> ENUMTYPE getFromIDCaseInsensitiveOrDefault (@Nonnull final Class <ENUMTYPE> aClass,
                                                                                                                 @Nullable final String sID,
                                                                                                                 @Nullable final ENUMTYPE eDefault)
  {
    ValueEnforcer.notNull (aClass, "Class");

    if (sID == null)
      return eDefault;
    return findFirst (aClass, e -> e.getID ().equalsIgnoreCase (sID), eDefault);
  }

  /**
   * Get the enum value with the passed string ID (case insensitive). If no such
   * ID is present, an {@link IllegalArgumentException} is thrown.
   *
   * @param <ENUMTYPE>
   *        The enum type
   * @param aClass
   *        The enum class
   * @param sID
   *        The ID to search
   * @return The enum item with the given ID. Never <code>null</code>.
   * @throws IllegalArgumentException
   *         if no enum item with the given ID is present
   */
  @Nonnull
  public static <ENUMTYPE extends Enum <ENUMTYPE> & IHasID <String>> ENUMTYPE getFromIDCaseInsensitiveOrThrow (@Nonnull final Class <ENUMTYPE> aClass,
                                                                                                               @Nullable final String sID)
  {
    final ENUMTYPE aEnum = getFromIDCaseInsensitiveOrNull (aClass, sID);
    if (aEnum == null)
      throw new IllegalArgumentException ("Failed to resolve ID " + sID + " within class " + aClass);
    return aEnum;
  }

  /**
   * Get the enum value with the passed ID
   *
   * @param <ENUMTYPE>
   *        The enum type
   * @param aClass
   *        The enum class
   * @param nID
   *        The ID to search
   * @return <code>null</code> if no enum item with the given ID is present.
   */
  @Nullable
  public static <ENUMTYPE extends Enum <ENUMTYPE> & IHasIntID> ENUMTYPE getFromIDOrNull (@Nonnull final Class <ENUMTYPE> aClass,
                                                                                         final int nID)
  {
    return getFromIDOrDefault (aClass, nID, null);
  }

  /**
   * Get the enum value with the passed ID
   *
   * @param <ENUMTYPE>
   *        The enum type
   * @param aClass
   *        The enum class
   * @param nID
   *        The ID to search
   * @param eDefault
   *        The default value to be returned, if the ID was not found.
   * @return The default parameter if no enum item with the given ID is present.
   */
  @Nullable
  public static <ENUMTYPE extends Enum <ENUMTYPE> & IHasIntID> ENUMTYPE getFromIDOrDefault (@Nonnull final Class <ENUMTYPE> aClass,
                                                                                            final int nID,
                                                                                            @Nullable final ENUMTYPE eDefault)
  {
    ValueEnforcer.notNull (aClass, "Class");

    final String sCacheKey = aClass.getName ();
    Object [] aCachedData = s_aRWLockInt.readLocked ( () -> s_aIntCache.get (sCacheKey));
    if (aCachedData == null)
    {
      aCachedData = s_aRWLockInt.writeLocked ( () -> {
        // Try again in write lock
        Object [] aWLCachedData = s_aIntCache.get (sCacheKey);
        if (aWLCachedData == null)
        {
          // Create new cache entry
          int nMinID = Integer.MAX_VALUE;
          int nMaxID = Integer.MIN_VALUE;
          for (final ENUMTYPE aElement : aClass.getEnumConstants ())
          {
            final int nElementID = aElement.getID ();
            if (nElementID < nMinID)
              nMinID = nElementID;
            if (nElementID > nMaxID)
              nMaxID = nElementID;
          }
          if (nMinID >= 0 && nMaxID <= CGlobal.MAX_BYTE_VALUE)
          {
            // Cachable!
            aWLCachedData = new Object [nMaxID + 1];
            for (final ENUMTYPE aElement : aClass.getEnumConstants ())
              aWLCachedData[aElement.getID ()] = aElement;
          }
          else
          {
            // Enum not cachable - too many items
            aWLCachedData = NOT_CACHABLE;
          }
          s_aIntCache.put (sCacheKey, aWLCachedData);
        }
        return aWLCachedData;
      });
    }

    if (aCachedData != NOT_CACHABLE)
    {
      if (nID < 0 || nID >= aCachedData.length)
        return eDefault;
      return GenericReflection.<Object, ENUMTYPE> uncheckedCast (aCachedData[nID]);
    }

    // Object is not cachable - traverse as usual
    return findFirst (aClass, e -> e.getID () == nID, eDefault);
  }

  /**
   * Get the enum value with the passed ID. If no such ID is present, an
   * {@link IllegalArgumentException} is thrown.
   *
   * @param <ENUMTYPE>
   *        The enum type
   * @param aClass
   *        The enum class
   * @param nID
   *        The ID to search
   * @return The enum item with the given ID. Never <code>null</code>.
   * @throws IllegalArgumentException
   *         if no enum item with the given ID is present
   */
  @Nonnull
  public static <ENUMTYPE extends Enum <ENUMTYPE> & IHasIntID> ENUMTYPE getFromIDOrThrow (@Nonnull final Class <ENUMTYPE> aClass,
                                                                                          final int nID)
  {
    final ENUMTYPE aEnum = getFromIDOrNull (aClass, nID);
    if (aEnum == null)
      throw new IllegalArgumentException ("Failed to resolve ID " + nID + " within class " + aClass);
    return aEnum;
  }

  /**
   * Get the enum value with the passed name
   *
   * @param <ENUMTYPE>
   *        The enum type
   * @param aClass
   *        The enum class
   * @param sName
   *        The name to search
   * @return <code>null</code> if no enum item with the given name is present.
   */
  @Nullable
  public static <ENUMTYPE extends Enum <ENUMTYPE> & IHasName> ENUMTYPE getFromNameOrNull (@Nonnull final Class <ENUMTYPE> aClass,
                                                                                          @Nullable final String sName)
  {
    return getFromNameOrDefault (aClass, sName, null);
  }

  /**
   * Get the enum value with the passed name
   *
   * @param <ENUMTYPE>
   *        The enum type
   * @param aClass
   *        The enum class
   * @param sName
   *        The name to search
   * @param eDefault
   *        The default value to be returned, if the name was not found.
   * @return The default parameter if no enum item with the given name is
   *         present.
   */
  @Nullable
  public static <ENUMTYPE extends Enum <ENUMTYPE> & IHasName> ENUMTYPE getFromNameOrDefault (@Nonnull final Class <ENUMTYPE> aClass,
                                                                                             @Nullable final String sName,
                                                                                             @Nullable final ENUMTYPE eDefault)
  {
    ValueEnforcer.notNull (aClass, "Class");

    if (StringHelper.hasNoText (sName))
      return eDefault;
    return findFirst (aClass, e -> e.getName ().equals (sName), eDefault);
  }

  /**
   * Get the enum value with the passed name. If no such name is present, an
   * {@link IllegalArgumentException} is thrown.
   *
   * @param <ENUMTYPE>
   *        The enum type
   * @param aClass
   *        The enum class
   * @param sName
   *        The name to search
   * @return The enum item with the given name. Never <code>null</code>.
   * @throws IllegalArgumentException
   *         if no enum item with the given name is present
   */
  @Nonnull
  public static <ENUMTYPE extends Enum <ENUMTYPE> & IHasName> ENUMTYPE getFromNameOrThrow (@Nonnull final Class <ENUMTYPE> aClass,
                                                                                           @Nullable final String sName)
  {
    final ENUMTYPE aEnum = getFromNameOrNull (aClass, sName);
    if (aEnum == null)
      throw new IllegalArgumentException ("Failed to resolve name " + sName + " within class " + aClass);
    return aEnum;
  }

  /**
   * Get the enum value with the passed name case insensitive
   *
   * @param <ENUMTYPE>
   *        The enum type
   * @param aClass
   *        The enum class
   * @param sName
   *        The name to search
   * @return <code>null</code> if no enum item with the given ID is present.
   */
  @Nullable
  public static <ENUMTYPE extends Enum <ENUMTYPE> & IHasName> ENUMTYPE getFromNameCaseInsensitiveOrNull (@Nonnull final Class <ENUMTYPE> aClass,
                                                                                                         @Nullable final String sName)
  {
    return getFromNameCaseInsensitiveOrDefault (aClass, sName, null);
  }

  /**
   * Get the enum value with the passed name case insensitive
   *
   * @param <ENUMTYPE>
   *        The enum type
   * @param aClass
   *        The enum class
   * @param sName
   *        The name to search
   * @param eDefault
   *        The default value to be returned, if the name was not found.
   * @return The default parameter if no enum item with the given name is
   *         present.
   */
  @Nullable
  public static <ENUMTYPE extends Enum <ENUMTYPE> & IHasName> ENUMTYPE getFromNameCaseInsensitiveOrDefault (@Nonnull final Class <ENUMTYPE> aClass,
                                                                                                            @Nullable final String sName,
                                                                                                            @Nullable final ENUMTYPE eDefault)
  {
    ValueEnforcer.notNull (aClass, "Class");

    if (StringHelper.hasNoText (sName))
      return eDefault;
    return findFirst (aClass, e -> e.getName ().equalsIgnoreCase (sName), eDefault);
  }

  /**
   * Get the enum value with the passed name (case insensitive). If no such name
   * is present, an {@link IllegalArgumentException} is thrown.
   *
   * @param <ENUMTYPE>
   *        The enum type
   * @param aClass
   *        The enum class
   * @param sName
   *        The name to search
   * @return The enum item with the given name. Never <code>null</code>.
   * @throws IllegalArgumentException
   *         if no enum item with the given name is present
   */
  @Nonnull
  public static <ENUMTYPE extends Enum <ENUMTYPE> & IHasName> ENUMTYPE getFromNameCaseInsensitiveOrThrow (@Nonnull final Class <ENUMTYPE> aClass,
                                                                                                          @Nullable final String sName)
  {
    final ENUMTYPE aEnum = getFromNameCaseInsensitiveOrNull (aClass, sName);
    if (aEnum == null)
      throw new IllegalArgumentException ("Failed to resolve name " + sName + " within class " + aClass);
    return aEnum;
  }

  /**
   * Get the unique name of the passed enum entry.
   *
   * @param aEnum
   *        The enum to use. May not be <code>null</code>.
   * @return The unique ID as a combination of the class name and the enum entry
   *         name. Never <code>null</code>.
   */
  @Nonnull
  public static String getEnumID (@Nonnull final Enum <?> aEnum)
  {
    // No explicit null check, because this method is used heavily in
    // locale resolving, so we want to spare some CPU cycles :)
    return aEnum.getClass ().getName () + '.' + aEnum.name ();
  }

  @Nonnull
  public static EChange clearCache ()
  {
    return s_aRWLockInt.writeLocked ( () -> {
      if (s_aIntCache.isEmpty ())
        return EChange.UNCHANGED;
      s_aIntCache.clear ();

      if (s_aLogger.isDebugEnabled ())
        s_aLogger.debug ("Cache was cleared: " + EnumHelper.class.getName ());
      return EChange.CHANGED;
    });
  }
}
