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
 * Small enum to identify whether something was handled or not.
 *
 * @author Philip Helger
 * @since 9.0.0
 */
public enum EHandled implements IHandledIndicator
{
  HANDLED,
  UNHANDLED;

  public boolean isHandled ()
  {
    return this == HANDLED;
  }

  @Nonnull
  public static EHandled valueOf (final boolean bHandleded)
  {
    return bHandleded ? HANDLED : UNHANDLED;
  }

  @Nonnull
  public static EHandled valueOf (@Nonnull final IHandledIndicator aHandledIndicator)
  {
    return valueOf (aHandledIndicator.isHandled ());
  }
}
