/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.base.io;

import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.util.function.BooleanSupplier;

import org.jspecify.annotations.NonNull;

/**
 * Small enum for determining the append or truncate state of output streams.
 *
 * @author Philip Helger
 */
public enum EAppend implements BooleanSupplier
{
  /** Append to an existing object */
  APPEND,
  /** Truncate an eventually existing object and start over */
  TRUNCATE;

  /** The default is {@link #TRUNCATE} */
  @NonNull
  public static final EAppend DEFAULT = TRUNCATE;

  /** {@inheritDoc} */
  public boolean getAsBoolean ()
  {
    return isAppend ();
  }

  /**
   * @return <code>true</code> if this is {@link #APPEND}, <code>false</code>
   *         otherwise.
   */
  public boolean isAppend ()
  {
    return this == APPEND;
  }

  /**
   * @return <code>true</code> if this is {@link #TRUNCATE},
   *         <code>false</code> otherwise.
   */
  public boolean isTruncate ()
  {
    return this == TRUNCATE;
  }

  /**
   * @return The matching NIO {@link OpenOption} array for this append mode.
   *         Never <code>null</code>.
   */
  @NonNull
  public OpenOption [] getAsOpenOptions ()
  {
    return this == APPEND ? new OpenOption [] { StandardOpenOption.CREATE, StandardOpenOption.APPEND }
                          : new OpenOption [] { StandardOpenOption.CREATE };
  }
}
