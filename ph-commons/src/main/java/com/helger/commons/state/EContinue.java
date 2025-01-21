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
package com.helger.commons.state;

import javax.annotation.Nonnull;

/**
 * Small enum for manager actions to indicate continue or break states.
 *
 * @author Philip Helger
 */
public enum EContinue implements IContinueIndicator
{
  CONTINUE,
  BREAK;

  public boolean isContinue ()
  {
    return this == CONTINUE;
  }

  @Nonnull
  public static EContinue valueOf (final boolean bContinue)
  {
    return bContinue ? CONTINUE : BREAK;
  }

  @Nonnull
  public static EContinue valueOf (@Nonnull final IContinueIndicator aContinueIndicator)
  {
    return valueOf (aContinueIndicator.isContinue ());
  }
}
