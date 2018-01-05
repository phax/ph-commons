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
package com.helger.commons.concurrent;

import java.util.concurrent.locks.Lock;

import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;

/**
 * Small wrapper around {@link Lock} interface to be used in a
 * try-with-resources statement, so that the unlock happened.
 *
 * @author Philip Helger
 * @since 9.0.0
 */
public class AutoLock implements AutoCloseable
{
  private final Lock m_aLock;

  public AutoLock (@Nonnull final Lock aLock)
  {
    m_aLock = ValueEnforcer.notNull (aLock, "Lock");
    m_aLock.lock ();
  }

  public void close () throws Exception
  {
    m_aLock.unlock ();
  }
}
