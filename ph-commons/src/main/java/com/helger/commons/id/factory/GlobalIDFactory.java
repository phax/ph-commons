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
package com.helger.commons.id.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.base.concurrent.SimpleReadWriteLock;
import com.helger.base.enforcer.ValueEnforcer;
import com.helger.base.equals.EqualsHelper;
import com.helger.base.state.EChange;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * This class should not be static since it may have an impact if this class is used by different
 * projects which have a separate IntID factory.
 *
 * @author Philip Helger
 */
@ThreadSafe
public final class GlobalIDFactory
{
  /** The default prefix to use for creating IDs */
  public static final String DEFAULT_PREFIX = "id";

  /**
   * The maximum string length of IDs created by the String based ID factory. This length should at
   * least fit a UUID v4 (36 chars)
   */
  public static final int STRING_ID_MAX_LENGTH = 40;

  private static final Logger LOGGER = LoggerFactory.getLogger (GlobalIDFactory.class);
  private static final SimpleReadWriteLock RW_LOCK = new SimpleReadWriteLock ();

  private static IIntIDFactory s_aIntIDFactory = new MemoryIntIDFactory ();
  private static IIntIDFactory s_aPersistentIntIDFactory;
  private static ILongIDFactory s_aLongIDFactory = new MemoryLongIDFactory ();
  private static ILongIDFactory s_aPersistentLongIDFactory;
  private static IStringIDFactory s_aStringIDFactory = new StringIDFromGlobalIntIDFactory ();
  private static IStringIDFactory s_aPersistentStringIDFactory = new StringIDFromGlobalPersistentIntIDFactory ();

  @PresentForCodeCoverage
  private static final GlobalIDFactory INSTANCE = new GlobalIDFactory ();

  private GlobalIDFactory ()
  {}

  public static boolean hasIntIDFactory ()
  {
    return getIntIDFactory () != null;
  }

  /**
   * @return The factory to create non-persistent int IDs. May be <code>null</code>.
   */
  @Nullable
  public static IIntIDFactory getIntIDFactory ()
  {
    return RW_LOCK.readLockedGet ( () -> s_aIntIDFactory);
  }

  @Nonnull
  public static EChange setIntIDFactory (@Nullable final IIntIDFactory aFactory)
  {
    return RW_LOCK.writeLockedGet ( () -> {
      final Object aObj1 = s_aIntIDFactory;
      if (EqualsHelper.equals (aObj1, aFactory))
        return EChange.UNCHANGED;
      if (LOGGER.isDebugEnabled ())
        LOGGER.debug ("Setting in-memory int ID factory " + aFactory);
      s_aIntIDFactory = aFactory;
      return EChange.CHANGED;
    });
  }

  public static boolean hasPersistentIntIDFactory ()
  {
    return getPersistentIntIDFactory () != null;
  }

  /**
   * @return The factory to create persistent int IDs. May be <code>null</code>.
   */
  @Nullable
  public static IIntIDFactory getPersistentIntIDFactory ()
  {
    return RW_LOCK.readLockedGet ( () -> s_aPersistentIntIDFactory);
  }

  @Nonnull
  public static EChange setPersistentIntIDFactory (@Nullable final IIntIDFactory aFactory)
  {
    return RW_LOCK.writeLockedGet ( () -> {
      final Object aObj1 = s_aPersistentIntIDFactory;
      if (EqualsHelper.equals (aObj1, aFactory))
        return EChange.UNCHANGED;
      if (LOGGER.isDebugEnabled ())
        LOGGER.debug ("Setting persistent int ID factory " + aFactory);
      s_aPersistentIntIDFactory = aFactory;
      return EChange.CHANGED;
    });
  }

  public static boolean hasLongIDFactory ()
  {
    return getLongIDFactory () != null;
  }

  /**
   * @return The factory to create non-persistent long IDs. May be <code>null</code>.
   */
  @Nullable
  public static ILongIDFactory getLongIDFactory ()
  {
    return RW_LOCK.readLockedGet ( () -> s_aLongIDFactory);
  }

  @Nonnull
  public static EChange setLongIDFactory (@Nullable final ILongIDFactory aFactory)
  {
    return RW_LOCK.writeLockedGet ( () -> {
      final Object aObj1 = s_aLongIDFactory;
      if (EqualsHelper.equals (aObj1, aFactory))
        return EChange.UNCHANGED;
      if (LOGGER.isDebugEnabled ())
        LOGGER.debug ("Setting in-memory long ID factory " + aFactory);
      s_aLongIDFactory = aFactory;
      return EChange.CHANGED;
    });
  }

  public static boolean hasPersistentLongIDFactory ()
  {
    return getPersistentLongIDFactory () != null;
  }

  /**
   * @return The factory to create persistent long IDs. May be <code>null</code>.
   */
  @Nullable
  public static ILongIDFactory getPersistentLongIDFactory ()
  {
    return RW_LOCK.readLockedGet ( () -> s_aPersistentLongIDFactory);
  }

  @Nonnull
  public static EChange setPersistentLongIDFactory (@Nullable final ILongIDFactory aFactory)
  {
    return RW_LOCK.writeLockedGet ( () -> {
      final Object aObj1 = s_aPersistentLongIDFactory;
      if (EqualsHelper.equals (aObj1, aFactory))
        return EChange.UNCHANGED;
      if (LOGGER.isDebugEnabled ())
        LOGGER.debug ("Setting persistent long ID factory " + aFactory);
      s_aPersistentLongIDFactory = aFactory;
      return EChange.CHANGED;
    });
  }

  public static boolean hasStringIDFactory ()
  {
    return getStringIDFactory () != null;
  }

  /**
   * @return The factory to create non-persistent string IDs. May be <code>null</code>.
   */
  @Nullable
  public static IStringIDFactory getStringIDFactory ()
  {
    return RW_LOCK.readLockedGet ( () -> s_aStringIDFactory);
  }

  @Nonnull
  public static EChange setStringIDFactory (@Nullable final IStringIDFactory aFactory)
  {
    return RW_LOCK.writeLockedGet ( () -> {
      final Object aObj1 = s_aStringIDFactory;
      if (EqualsHelper.equals (aObj1, aFactory))
        return EChange.UNCHANGED;
      if (LOGGER.isDebugEnabled ())
        LOGGER.debug ("Setting in-memory string ID factory " + aFactory);
      s_aStringIDFactory = aFactory;
      return EChange.CHANGED;
    });
  }

  public static boolean hasPersistentStringIDFactory ()
  {
    return getPersistentStringIDFactory () != null;
  }

  /**
   * @return The factory to create persistent string IDs. May be <code>null</code>.
   */
  @Nullable
  public static IStringIDFactory getPersistentStringIDFactory ()
  {
    return RW_LOCK.readLockedGet ( () -> s_aPersistentStringIDFactory);
  }

  @Nonnull
  public static EChange setPersistentStringIDFactory (@Nullable final IStringIDFactory aFactory)
  {
    return RW_LOCK.writeLockedGet ( () -> {
      final Object aObj1 = s_aPersistentStringIDFactory;
      if (EqualsHelper.equals (aObj1, aFactory))
        return EChange.UNCHANGED;
      if (LOGGER.isDebugEnabled ())
        LOGGER.debug ("Setting persistent string ID factory " + aFactory);
      s_aPersistentStringIDFactory = aFactory;
      return EChange.CHANGED;
    });
  }

  /**
   * @return A new int ID
   */
  public static int getNewIntID ()
  {
    return RW_LOCK.readLockedInt ( () -> {
      final IIntIDFactory aFactory = s_aIntIDFactory;
      if (aFactory == null)
        throw new IllegalStateException ("No in-memory int ID factory has been supplied!");
      return aFactory.getNewID ();
    });
  }

  /**
   * @return A new persistent int ID
   */
  public static int getNewPersistentIntID ()
  {
    return RW_LOCK.readLockedInt ( () -> {
      final IIntIDFactory aFactory = s_aPersistentIntIDFactory;
      if (aFactory == null)
        throw new IllegalStateException ("No persistent int ID factory has been supplied. Don't know how to create persistent IDs!");
      return aFactory.getNewID ();
    });
  }

  /**
   * @return A new long ID
   */
  public static long getNewLongID ()
  {
    return RW_LOCK.readLockedLong ( () -> {
      final ILongIDFactory aFactory = s_aLongIDFactory;
      if (aFactory == null)
        throw new IllegalStateException ("No in-memory long ID factory has been supplied!");
      return aFactory.getNewID ();
    });
  }

  /**
   * @return A new persistent long ID
   */
  public static long getNewPersistentLongID ()
  {
    return RW_LOCK.readLockedLong ( () -> {
      final ILongIDFactory aFactory = s_aPersistentLongIDFactory;
      if (aFactory == null)
        throw new IllegalStateException ("No persistent long ID factory has been supplied. Don't know how to create persistent IDs!");
      return aFactory.getNewID ();
    });
  }

  /**
   * @return A new String ID
   */
  @Nonnull
  public static String getNewStringID ()
  {
    return RW_LOCK.readLockedGet ( () -> {
      final IStringIDFactory aFactory = s_aStringIDFactory;
      if (aFactory == null)
        throw new IllegalStateException ("No in-memory string ID factory has been supplied!");

      final String ret = aFactory.getNewID ();
      if (ret.length () > STRING_ID_MAX_LENGTH)
        throw new IllegalStateException ("The created String ID has a length of " +
                                         ret.length () +
                                         " which exceeds the maximum allowed length of " +
                                         STRING_ID_MAX_LENGTH);
      return ret;
    });
  }

  /**
   * @return A new persistent String ID
   */
  @Nonnull
  public static String getNewPersistentStringID ()
  {
    return RW_LOCK.readLockedGet ( () -> {
      final IStringIDFactory aFactory = s_aPersistentStringIDFactory;
      if (aFactory == null)
        throw new IllegalStateException ("No persistent string ID factory has been supplied!");

      final String ret = aFactory.getNewID ();
      if (ret.length () > STRING_ID_MAX_LENGTH)
        throw new IllegalStateException ("The created String ID has a length of " +
                                         ret.length () +
                                         " which exceeds the maximum allowed length of " +
                                         STRING_ID_MAX_LENGTH);
      return ret;
    });
  }

  /**
   * @param nCount
   *        The number of IDs to retrieve. Must be &gt; 0.
   * @return An array of new int IDs
   */
  public static int [] getBulkNewIntIDs (@Nonnegative final int nCount)
  {
    ValueEnforcer.isGT0 (nCount, "Count");

    return RW_LOCK.readLockedGet ( () -> {
      final IIntIDFactory aFactory = s_aIntIDFactory;
      if (aFactory == null)
        throw new IllegalStateException ("No in-memory int ID factory has been supplied!");

      final int [] ret = new int [nCount];
      for (int i = 0; i < nCount; ++i)
        ret[i] = aFactory.getNewID ();
      return ret;
    });
  }

  /**
   * @param nCount
   *        The number of IDs to retrieve. Must be &gt; 0.
   * @return An array of new persistent int IDs
   */
  public static int [] getBulkNewPersistentIntIDs (@Nonnegative final int nCount)
  {
    ValueEnforcer.isGT0 (nCount, "Count");

    return RW_LOCK.readLockedGet ( () -> {
      final IIntIDFactory aFactory = s_aPersistentIntIDFactory;
      if (aFactory == null)
        throw new IllegalStateException ("No persistent int ID factory has been supplied. Don't know how to create persistent IDs!");

      final int [] ret = new int [nCount];
      for (int i = 0; i < nCount; ++i)
        ret[i] = aFactory.getNewID ();
      return ret;
    });
  }

  /**
   * @param nCount
   *        The number of IDs to retrieve. Must be &gt; 0.
   * @return An array of new long IDs
   */
  public static long [] getBulkNewLongIDs (@Nonnegative final int nCount)
  {
    ValueEnforcer.isGT0 (nCount, "Count");

    return RW_LOCK.readLockedGet ( () -> {
      final ILongIDFactory aFactory = s_aLongIDFactory;
      if (aFactory == null)
        throw new IllegalStateException ("No in-memory long ID factory has been supplied!");

      final long [] ret = new long [nCount];
      for (int i = 0; i < nCount; ++i)
        ret[i] = aFactory.getNewID ();
      return ret;
    });
  }

  /**
   * @param nCount
   *        The number of IDs to retrieve. Must be &gt; 0.
   * @return An array of new persistent long IDs
   */
  public static long [] getBulkNewPersistentLongIDs (@Nonnegative final int nCount)
  {
    ValueEnforcer.isGT0 (nCount, "Count");

    return RW_LOCK.readLockedGet ( () -> {
      final ILongIDFactory aFactory = s_aPersistentLongIDFactory;
      if (aFactory == null)
        throw new IllegalStateException ("No persistent long ID factory has been supplied. Don't know how to create persistent IDs!");

      final long [] ret = new long [nCount];
      for (int i = 0; i < nCount; ++i)
        ret[i] = aFactory.getNewID ();
      return ret;
    });
  }

  /**
   * @param nCount
   *        The number of IDs to retrieve
   * @return An array of new String IDs
   */
  @Nonnull
  public static String [] getBulkNewStringIDs (@Nonnegative final int nCount)
  {
    ValueEnforcer.isGT0 (nCount, "Count");

    return RW_LOCK.readLockedGet ( () -> {
      final IStringIDFactory aFactory = s_aStringIDFactory;
      if (aFactory == null)
        throw new IllegalStateException ("No in-memory string ID factory has been supplied!");

      final String [] ret = new String [nCount];
      for (int i = 0; i < nCount; ++i)
      {
        ret[i] = aFactory.getNewID ();
        if (ret[i].length () > STRING_ID_MAX_LENGTH)
          throw new IllegalStateException ("The created String ID has a length of " +
                                           ret[i].length () +
                                           " which exceeds the maximum allowed length of " +
                                           STRING_ID_MAX_LENGTH);
      }
      return ret;
    });
  }

  /**
   * @param nCount
   *        The number of IDs to retrieve. Must be &gt; 0.
   * @return An array of new persistent String IDs
   */
  @Nonnull
  public static String [] getBulkNewPersistentStringIDs (@Nonnegative final int nCount)
  {
    ValueEnforcer.isGT0 (nCount, "Count");

    return RW_LOCK.readLockedGet ( () -> {
      final IStringIDFactory aFactory = s_aPersistentStringIDFactory;
      if (aFactory == null)
        throw new IllegalStateException ("No persistent string ID factory has been supplied!");

      final String [] ret = new String [nCount];
      for (int i = 0; i < nCount; ++i)
      {
        ret[i] = aFactory.getNewID ();
        if (ret[i].length () > STRING_ID_MAX_LENGTH)
          throw new IllegalStateException ("The created String ID has a length of " +
                                           ret[i].length () +
                                           " which exceeds the maximum allowed length of " +
                                           STRING_ID_MAX_LENGTH);
      }
      return ret;
    });
  }
}
