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
package com.helger.commons.id.factory;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.concurrent.SimpleReadWriteLock;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.state.EChange;

/**
 * This class should not be static since it may have an impact if this class is
 * used by different projects which have a separate IntID factory.
 *
 * @author Philip Helger
 */
@ThreadSafe
public final class GlobalIDFactory
{
  /** The default prefix to use for creating IDs */
  public static final String DEFAULT_PREFIX = "id";

  private static final Logger s_aLogger = LoggerFactory.getLogger (GlobalIDFactory.class);
  private static final SimpleReadWriteLock s_aRWLock = new SimpleReadWriteLock ();

  private static IIntIDFactory s_aIntIDFactory = new MemoryIntIDFactory ();
  private static IIntIDFactory s_aPersistentIntIDFactory;
  private static ILongIDFactory s_aLongIDFactory = new MemoryLongIDFactory ();
  private static ILongIDFactory s_aPersistentLongIDFactory;
  private static IStringIDFactory s_aStringIDFactory = new StringIDFromGlobalIntIDFactory ();
  private static IStringIDFactory s_aPersistentStringIDFactory = new StringIDFromGlobalPersistentIntIDFactory ();

  @PresentForCodeCoverage
  private static final GlobalIDFactory s_aInstance = new GlobalIDFactory ();

  private GlobalIDFactory ()
  {}

  public static boolean hasIntIDFactory ()
  {
    return getIntIDFactory () != null;
  }

  @Nullable
  public static IIntIDFactory getIntIDFactory ()
  {
    return s_aRWLock.readLocked ( () -> s_aIntIDFactory);
  }

  @Nonnull
  public static EChange setIntIDFactory (@Nullable final IIntIDFactory aFactory)
  {
    return s_aRWLock.writeLocked ( () -> {
      if (EqualsHelper.equals (s_aIntIDFactory, aFactory))
        return EChange.UNCHANGED;
      if (s_aLogger.isInfoEnabled ())
        s_aLogger.info ("Setting in-memory int ID factory " + aFactory);
      s_aIntIDFactory = aFactory;
      return EChange.CHANGED;
    });
  }

  public static boolean hasPersistentIntIDFactory ()
  {
    return getPersistentIntIDFactory () != null;
  }

  @Nullable
  public static IIntIDFactory getPersistentIntIDFactory ()
  {
    return s_aRWLock.readLocked ( () -> s_aPersistentIntIDFactory);
  }

  @Nonnull
  public static EChange setPersistentIntIDFactory (@Nullable final IIntIDFactory aFactory)
  {
    return s_aRWLock.writeLocked ( () -> {
      if (EqualsHelper.equals (s_aPersistentIntIDFactory, aFactory))
        return EChange.UNCHANGED;
      if (s_aLogger.isInfoEnabled ())
        s_aLogger.info ("Setting persistent int ID factory " + aFactory);
      s_aPersistentIntIDFactory = aFactory;
      return EChange.CHANGED;
    });
  }

  public static boolean hasLongIDFactory ()
  {
    return getLongIDFactory () != null;
  }

  @Nullable
  public static ILongIDFactory getLongIDFactory ()
  {
    return s_aRWLock.readLocked ( () -> s_aLongIDFactory);
  }

  @Nonnull
  public static EChange setLongIDFactory (@Nullable final ILongIDFactory aFactory)
  {
    return s_aRWLock.writeLocked ( () -> {
      if (EqualsHelper.equals (s_aLongIDFactory, aFactory))
        return EChange.UNCHANGED;
      if (s_aLogger.isInfoEnabled ())
        s_aLogger.info ("Setting in-memory long ID factory " + aFactory);
      s_aLongIDFactory = aFactory;
      return EChange.CHANGED;
    });
  }

  public static boolean hasPersistentLongIDFactory ()
  {
    return getPersistentLongIDFactory () != null;
  }

  @Nullable
  public static ILongIDFactory getPersistentLongIDFactory ()
  {
    return s_aRWLock.readLocked ( () -> s_aPersistentLongIDFactory);
  }

  @Nonnull
  public static EChange setPersistentLongIDFactory (@Nullable final ILongIDFactory aFactory)
  {
    return s_aRWLock.writeLocked ( () -> {
      if (EqualsHelper.equals (s_aPersistentLongIDFactory, aFactory))
        return EChange.UNCHANGED;
      if (s_aLogger.isInfoEnabled ())
        s_aLogger.info ("Setting persistent long ID factory " + aFactory);
      s_aPersistentLongIDFactory = aFactory;
      return EChange.CHANGED;
    });
  }

  public static boolean hasStringIDFactory ()
  {
    return getStringIDFactory () != null;
  }

  @Nullable
  public static IStringIDFactory getStringIDFactory ()
  {
    return s_aRWLock.readLocked ( () -> s_aStringIDFactory);
  }

  @Nonnull
  public static EChange setStringIDFactory (@Nullable final IStringIDFactory aFactory)
  {
    return s_aRWLock.writeLocked ( () -> {
      if (EqualsHelper.equals (s_aStringIDFactory, aFactory))
        return EChange.UNCHANGED;
      if (s_aLogger.isInfoEnabled ())
        s_aLogger.info ("Setting in-memory string ID factory " + aFactory);
      s_aStringIDFactory = aFactory;
      return EChange.CHANGED;
    });
  }

  public static boolean hasPersistentStringIDFactory ()
  {
    return getPersistentStringIDFactory () != null;
  }

  @Nullable
  public static IStringIDFactory getPersistentStringIDFactory ()
  {
    return s_aRWLock.readLocked ( () -> s_aPersistentStringIDFactory);
  }

  @Nonnull
  public static EChange setPersistentStringIDFactory (@Nullable final IStringIDFactory aFactory)
  {
    return s_aRWLock.writeLocked ( () -> {
      if (EqualsHelper.equals (s_aPersistentStringIDFactory, aFactory))
        return EChange.UNCHANGED;
      if (s_aLogger.isInfoEnabled ())
        s_aLogger.info ("Setting persistent string ID factory " + aFactory);
      s_aPersistentStringIDFactory = aFactory;
      return EChange.CHANGED;
    });
  }

  /**
   * @return A new int ID
   */
  public static int getNewIntID ()
  {
    return s_aRWLock.readLocked ( () -> {
      if (s_aIntIDFactory == null)
        throw new IllegalStateException ("No in-memory int ID factory has been supplied!");
      return s_aIntIDFactory.getNewID ();
    });
  }

  /**
   * @return A new persistent int ID
   */
  public static int getNewPersistentIntID ()
  {
    return s_aRWLock.readLocked ( () -> {
      if (s_aPersistentIntIDFactory == null)
        throw new IllegalStateException ("No persistent int ID factory has been supplied. Don't know how to create persistent IDs!");
      return s_aPersistentIntIDFactory.getNewID ();
    });
  }

  /**
   * @return A new long ID
   */
  public static long getNewLongID ()
  {
    return s_aRWLock.readLocked ( () -> {
      if (s_aLongIDFactory == null)
        throw new IllegalStateException ("No in-memory long ID factory has been supplied!");
      return s_aLongIDFactory.getNewID ();
    });
  }

  /**
   * @return A new persistent long ID
   */
  public static long getNewPersistentLongID ()
  {
    return s_aRWLock.readLocked ( () -> {
      if (s_aPersistentLongIDFactory == null)
        throw new IllegalStateException ("No persistent long ID factory has been supplied. Don't know how to create persistent IDs!");
      return s_aPersistentLongIDFactory.getNewID ();
    });
  }

  /**
   * @return A new String ID
   */
  @Nonnull
  public static String getNewStringID ()
  {
    return s_aRWLock.readLocked ( () -> {
      if (s_aStringIDFactory == null)
        throw new IllegalStateException ("No in-memory string ID factory has been supplied!");
      return s_aStringIDFactory.getNewID ();
    });
  }

  /**
   * @return A new persistent String ID
   */
  @Nonnull
  public static String getNewPersistentStringID ()
  {
    return s_aRWLock.readLocked ( () -> {
      if (s_aPersistentStringIDFactory == null)
        throw new IllegalStateException ("No persistent string ID factory has been supplied!");
      return s_aPersistentStringIDFactory.getNewID ();
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

    return s_aRWLock.readLocked ( () -> {
      if (s_aIntIDFactory == null)
        throw new IllegalStateException ("No in-memory int ID factory has been supplied!");
      final int [] ret = new int [nCount];
      for (int i = 0; i < nCount; ++i)
        ret[i] = s_aIntIDFactory.getNewID ();
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

    return s_aRWLock.readLocked ( () -> {
      if (s_aPersistentIntIDFactory == null)
        throw new IllegalStateException ("No persistent int ID factory has been supplied. Don't know how to create persistent IDs!");
      final int [] ret = new int [nCount];
      for (int i = 0; i < nCount; ++i)
        ret[i] = s_aPersistentIntIDFactory.getNewID ();
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

    return s_aRWLock.readLocked ( () -> {
      if (s_aLongIDFactory == null)
        throw new IllegalStateException ("No in-memory long ID factory has been supplied!");
      final long [] ret = new long [nCount];
      for (int i = 0; i < nCount; ++i)
        ret[i] = s_aLongIDFactory.getNewID ();
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

    return s_aRWLock.readLocked ( () -> {
      if (s_aPersistentLongIDFactory == null)
        throw new IllegalStateException ("No persistent long ID factory has been supplied. Don't know how to create persistent IDs!");
      final long [] ret = new long [nCount];
      for (int i = 0; i < nCount; ++i)
        ret[i] = s_aPersistentLongIDFactory.getNewID ();
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

    return s_aRWLock.readLocked ( () -> {
      if (s_aStringIDFactory == null)
        throw new IllegalStateException ("No in-memory string ID factory has been supplied!");
      final String [] ret = new String [nCount];
      for (int i = 0; i < nCount; ++i)
        ret[i] = s_aStringIDFactory.getNewID ();
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

    return s_aRWLock.readLocked ( () -> {
      if (s_aPersistentStringIDFactory == null)
        throw new IllegalStateException ("No persistent string ID factory has been supplied!");
      final String [] ret = new String [nCount];
      for (int i = 0; i < nCount; ++i)
        ret[i] = s_aPersistentStringIDFactory.getNewID ();
      return ret;
    });
  }
}
