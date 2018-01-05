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
package com.helger.dao;

import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.callback.IThrowingRunnable;
import com.helger.commons.functional.IThrowingSupplier;

/**
 * Abstraction layer around auto save stuff.
 *
 * @author Philip Helger
 */
public interface IAutoSaveAware
{
  /**
   * @return <code>true</code> if auto save is enabled, <code>false</code>
   *         otherwise.
   */
  boolean isAutoSaveEnabled ();

  /**
   * Start doing something without auto save.
   */
  void beginWithoutAutoSave ();

  /**
   * End doing something without auto save. It must be ensure that each call to
   * {@link #beginWithoutAutoSave()} is always correctly ended with a call to
   * this method.
   */
  void endWithoutAutoSave ();

  /**
   * This method is used for batch processing of instructions (like the initial
   * read). If first turns automatic saving off, runs the desired operations and
   * finally restores the initial state of the "automatic save" flag and tries
   * to write any pending changes.
   *
   * @param aRunnable
   *        The runnable to be executed. May not be <code>null</code>.
   */
  default void performWithoutAutoSave (@Nonnull final Runnable aRunnable)
  {
    ValueEnforcer.notNull (aRunnable, "Runnable");

    beginWithoutAutoSave ();
    try
    {
      aRunnable.run ();
    }
    finally
    {
      endWithoutAutoSave ();
    }
  }

  /**
   * This method is used for batch processing of instructions (like the initial
   * read). If first turns automatic saving off, runs the desired operations and
   * finally restores the initial state of the "automatic save" flag and tries
   * to write any pending changes.
   *
   * @param aCallable
   *        The runnable to be executed. May not be <code>null</code>.
   * @return the result of the callable.
   * @param <RETURNTYPE>
   *        Return type of the callable
   */
  @Nullable
  default <RETURNTYPE> RETURNTYPE performWithoutAutoSave (@Nonnull final Supplier <RETURNTYPE> aCallable)
  {
    ValueEnforcer.notNull (aCallable, "Callable");

    beginWithoutAutoSave ();
    try
    {
      return aCallable.get ();
    }
    finally
    {
      endWithoutAutoSave ();
    }
  }

  /**
   * Execute a callback with autosave being disabled. Must be called outside a
   * writeLock, as this method locks itself!
   *
   * @param aRunnable
   *        The callback to be executed
   * @throws EXTYPE
   *         In case of an error
   * @param <EXTYPE>
   *        Exception type that may be thrown
   */
  default <EXTYPE extends Exception> void performWithoutAutoSaveThrowing (@Nonnull final IThrowingRunnable <EXTYPE> aRunnable) throws EXTYPE
  {
    ValueEnforcer.notNull (aRunnable, "Runnable");

    beginWithoutAutoSave ();
    try
    {
      aRunnable.run ();
    }
    finally
    {
      endWithoutAutoSave ();
    }
  }

  /**
   * Execute a callback with autosave being disabled. Must be called outside a
   * writeLock, as this method locks itself!
   *
   * @param aCallable
   *        The callback to be executed
   * @return The result of the callback. May be <code>null</code>.
   * @throws EXTYPE
   *         In case of an error
   * @param <RETURNTYPE>
   *        Return type of the callable
   * @param <EXTYPE>
   *        Exception type that may be thrown
   */
  @Nullable
  default <RETURNTYPE, EXTYPE extends Exception> RETURNTYPE performWithoutAutoSaveThrowing (@Nonnull final IThrowingSupplier <RETURNTYPE, EXTYPE> aCallable) throws EXTYPE
  {
    ValueEnforcer.notNull (aCallable, "Callable");

    beginWithoutAutoSave ();
    try
    {
      // Main call of callable
      return aCallable.get ();
    }
    finally
    {
      endWithoutAutoSave ();
    }
  }
}
