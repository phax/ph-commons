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
package com.helger.commons.pool;

import java.util.function.Supplier;

import com.helger.commons.state.ESuccess;

import jakarta.annotation.Nonnull;

/**
 * An extended factory for objects in the {@link ObjectPool}.
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        the type of results supplied by this factory
 * @since 11.1.0
 */
public interface IObjectPoolFactory <DATATYPE>
{
  /**
   * Create a new object for usage in the pool. This method is called if no object is in the pool,
   * or if activation of a pooled object failed.
   *
   * @return A new object of data type. Never <code>null</code>.
   */
  @Nonnull
  DATATYPE create ();

  /**
   * Called when an existing object is borrowed from the pool. If activation failed, a new object
   * will be created.
   *
   * @param aItem
   *        The item to be borrowed. Never <code>null</code>.
   * @return {@link ESuccess#SUCCESS} if the object can be reused, {@link ESuccess#FAILURE} if not.
   */
  @Nonnull
  ESuccess activate (@Nonnull DATATYPE aItem);

  /**
   * Called when an object is returned to the pool. This method has no return value - only
   * {@link #activate(Object)} can change the path.
   *
   * @param aItem
   *        The item to be returned. Never <code>null</code>.
   */
  void passivate (@Nonnull DATATYPE aItem);

  /**
   * Wrapper around {@link Supplier} to create an {@link IObjectPoolFactory}.
   *
   * @param <T>
   *        Type the object pool is supplying.
   * @param aSupplier
   *        The supplier to wrap. May not be <code>null</code>.
   * @return A new instance of {@link IObjectPoolFactory}.
   */
  @Nonnull
  static <T> IObjectPoolFactory <T> wrap (@Nonnull final Supplier <? extends T> aSupplier)
  {
    return new IObjectPoolFactory <> ()
    {
      @Nonnull
      public T create ()
      {
        return aSupplier.get ();
      }

      @Nonnull
      public ESuccess activate (@Nonnull final T aItem)
      {
        // empty
        return ESuccess.SUCCESS;
      }

      public void passivate (@Nonnull final T aItem)
      {
        // empty
      }
    };
  }
}
