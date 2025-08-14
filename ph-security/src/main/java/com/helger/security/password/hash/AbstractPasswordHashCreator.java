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
package com.helger.security.password.hash;

import com.helger.annotation.Nonempty;
import com.helger.base.equals.ValueEnforcer;
import com.helger.base.tostring.ToStringGenerator;

import jakarta.annotation.Nonnull;

/**
 * Abstract base class of {@link IPasswordHashCreator}.
 *
 * @author Philip Helger
 */
public abstract class AbstractPasswordHashCreator implements IPasswordHashCreator
{
  private final String m_sAlgorithmName;

  protected AbstractPasswordHashCreator (@Nonnull @Nonempty final String sAlgorithmName)
  {
    m_sAlgorithmName = ValueEnforcer.notEmpty (sAlgorithmName, "Algorithm");
  }

  @Nonnull
  @Nonempty
  public final String getAlgorithmName ()
  {
    return m_sAlgorithmName;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("AlgorithmName", m_sAlgorithmName).getToString ();
  }
}
