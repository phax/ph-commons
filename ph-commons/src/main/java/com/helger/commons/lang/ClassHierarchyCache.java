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

import java.lang.ref.WeakReference;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsLinkedHashSet;
import com.helger.commons.collection.impl.ICommonsIterable;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.collection.impl.ICommonsOrderedSet;
import com.helger.commons.collection.impl.ICommonsSet;
import com.helger.commons.collection.iterate.IIterableIterator;
import com.helger.commons.collection.map.LRUMap;
import com.helger.commons.concurrent.SimpleReadWriteLock;
import com.helger.commons.state.EChange;
import com.helger.commons.string.ToStringGenerator;

/**
 * A small class hierarchy cache
 *
 * @author Philip Helger
 */
@ThreadSafe
public final class ClassHierarchyCache
{
  @Immutable
  private static final class ClassList implements ICommonsIterable <WeakReference <Class <?>>>
  {
    // Store it in the correct order, but without duplicates
    private final ICommonsList <WeakReference <Class <?>>> m_aList = new CommonsArrayList <> ();

    public ClassList (@Nonnull final Class <?> aClass)
    {
      ValueEnforcer.notNull (aClass, "Class");

      // Check the whole class hierarchy of the source class
      final ICommonsOrderedSet <Class <?>> aUniqueOrderedClasses = new CommonsLinkedHashSet <> ();
      final ICommonsList <Class <?>> aOpenSrc = new CommonsArrayList <> ();
      aOpenSrc.add (aClass);
      while (!aOpenSrc.isEmpty ())
      {
        final Class <?> aCurClass = aOpenSrc.removeFirst ();
        aUniqueOrderedClasses.add (aCurClass);

        // Add super-classes and interfaces
        // Super-classes have precedence over interfaces!
        for (final Class <?> aInterface : aCurClass.getInterfaces ())
          aOpenSrc.add (0, aInterface);
        if (aCurClass.getSuperclass () != null)
          aOpenSrc.add (0, aCurClass.getSuperclass ());
      }

      // Now convert to list of WeakReference
      for (final Class <?> aCurClass : aUniqueOrderedClasses)
        m_aList.add (new WeakReference <> (aCurClass));
    }

    @Nonnull
    @ReturnsMutableCopy
    public ICommonsOrderedSet <Class <?>> getAsSet ()
    {
      // Use a linked hash set, to maintain the order
      final ICommonsOrderedSet <Class <?>> ret = new CommonsLinkedHashSet <> (m_aList.size ());
      for (final WeakReference <Class <?>> aRef : m_aList)
      {
        final Class <?> aClass = aRef.get ();
        if (aClass != null)
          ret.add (aClass);
      }
      return ret;
    }

    @Nonnull
    @ReturnsMutableCopy
    public ICommonsList <Class <?>> getAsList ()
    {
      // Use a list that may contain duplicates
      final ICommonsList <Class <?>> ret = new CommonsArrayList <> (m_aList.size ());
      for (final WeakReference <Class <?>> aRef : m_aList)
      {
        final Class <?> aClass = aRef.get ();
        if (aClass != null)
          ret.add (aClass);
      }
      return ret;
    }

    @Nonnull
    public IIterableIterator <WeakReference <Class <?>>> iterator ()
    {
      return m_aList.iterator2 ();
    }

    @Override
    public String toString ()
    {
      return new ToStringGenerator (this).append ("list", m_aList).getToString ();
    }
  }

  private static final Logger s_aLogger = LoggerFactory.getLogger (ClassHierarchyCache.class);

  private static final SimpleReadWriteLock s_aRWLock = new SimpleReadWriteLock ();
  @GuardedBy ("s_aRWLock")
  private static final ICommonsMap <String, ClassList> s_aClassHierarchy = new LRUMap <> (1000);

  @PresentForCodeCoverage
  private static final ClassHierarchyCache s_aInstance = new ClassHierarchyCache ();

  private ClassHierarchyCache ()
  {}

  /**
   * It's important to clear the cache upon application shutdown, because for
   * web applications, keeping a cache of classes may prevent the web
   * application from unloading
   *
   * @return {@link EChange}
   */
  @Nonnull
  public static EChange clearCache ()
  {
    final EChange ret = s_aRWLock.writeLocked (s_aClassHierarchy::removeAll);
    if (ret.isUnchanged ())
      return EChange.UNCHANGED;

    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug ("Cache was cleared: " + ClassHierarchyCache.class.getName ());
    return EChange.CHANGED;
  }

  @Nonnull
  private static ClassList _getClassList (@Nonnull final Class <?> aClass)
  {
    ValueEnforcer.notNull (aClass, "Class");
    final String sKey = aClass.getName ();

    // Get or update from cache
    ClassList aClassList = s_aRWLock.readLocked ( () -> s_aClassHierarchy.get (sKey));

    if (aClassList == null)
    {
      // try again in write lock
      aClassList = s_aRWLock.writeLocked ( () -> s_aClassHierarchy.computeIfAbsent (sKey, x -> new ClassList (aClass)));
    }
    return aClassList;
  }

  /**
   * Get the complete super class hierarchy of the passed class including all
   * super classes and all interfaces of the passed class and of all parent
   * classes.
   *
   * @param aClass
   *        The source class to get the hierarchy from.
   * @return A non-<code>null</code> and non-empty Set containing the passed
   *         class and all super classes, and all super-interfaces.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsSet <Class <?>> getClassHierarchy (@Nonnull final Class <?> aClass)
  {
    return _getClassList (aClass).getAsSet ();
  }

  /**
   * Get the complete super class hierarchy of the passed class including all
   * super classes and all interfaces of the passed class and of all parent
   * classes.
   *
   * @param aClass
   *        The source class to get the hierarchy from.
   * @return A non-<code>null</code> and non-empty list containing the passed
   *         class and all super classes, and all super-interfaces. Duplicates
   *         were already removed.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsList <Class <?>> getClassHierarchyList (@Nonnull final Class <?> aClass)
  {
    return _getClassList (aClass).getAsList ();
  }

  /**
   * Iterate the complete super class hierarchy of the passed class including
   * all super classes and all interfaces of the passed class and of all parent
   * classes.
   *
   * @param aClass
   *        The source class to get the hierarchy from.
   * @return A non-<code>null</code> and non-empty list containing the passed
   *         class and all super classes, and all super-interfaces. Duplicates
   *         were already removed.
   */
  @Nonnull
  public static ICommonsIterable <WeakReference <Class <?>>> getClassHierarchyIterator (@Nonnull final Class <?> aClass)
  {
    return _getClassList (aClass);
  }
}
