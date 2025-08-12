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
package com.helger.graph.impl;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.GuardedBy;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.commons.concurrent.SimpleReadWriteLock;
import com.helger.commons.id.factory.GlobalIDFactory;
import com.helger.commons.id.factory.IIDFactory;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Factory class that handles the generation of graph object IDs. It allows to
 * provide another ID factory. If no custom ID factory is present (which is the
 * default), {@link GlobalIDFactory#getNewStringID()} is used to create Graph
 * object IDs.
 *
 * @author Philip Helger
 */
@ThreadSafe
public final class GraphObjectIDFactory
{
  private static final SimpleReadWriteLock RW_LOCK = new SimpleReadWriteLock ();
  @GuardedBy ("RW_LOCK")
  private static IIDFactory <String> s_aIDFactory;

  @PresentForCodeCoverage
  private static final GraphObjectIDFactory INSTANCE = new GraphObjectIDFactory ();

  private GraphObjectIDFactory ()
  {}

  /**
   * @return The custom ID factory if defined. May be <code>null</code> if no
   *         custom ID factory is set.
   */
  @Nullable
  public static IIDFactory <String> getIDFactory ()
  {
    return RW_LOCK.readLockedGet ( () -> s_aIDFactory);
  }

  /**
   * Set a custom ID factory.
   *
   * @param aIDFactory
   *        The new ID factory to use. May be <code>null</code> to indicate that
   *        the default should be used.
   */
  public static void setIDFactory (@Nullable final IIDFactory <String> aIDFactory)
  {
    RW_LOCK.writeLocked ( () -> s_aIDFactory = aIDFactory);
  }

  /**
   * Get a new ID for a graph object. If a custom ID factory is defined, the ID
   * is retrieved from there. Otherwise the ID is retrieved from
   * {@link GlobalIDFactory}.
   *
   * @return A new graph object ID. Never <code>null</code>.
   */
  @Nonnull
  @Nonempty
  public static String createNewGraphObjectID ()
  {
    return RW_LOCK.readLockedGet ( () -> s_aIDFactory != null ? s_aIDFactory.getNewID () : GlobalIDFactory.getNewStringID ());
  }
}
