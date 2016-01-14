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
package com.helger.commons.lang;

import java.security.Permission;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;

/**
 * Utility methods to handle the {@link SecurityManager} more easily.
 *
 * @author Philip Helger
 */
@Immutable
public final class SecurityManagerHelper
{
  private SecurityManagerHelper ()
  {}

  @Nonnull
  public static ThreadGroup getThreadGroup ()
  {
    final SecurityManager aSM = System.getSecurityManager ();
    return aSM != null ? aSM.getThreadGroup () : Thread.currentThread ().getThreadGroup ();
  }

  public static void checkPermission (@Nonnull final Permission aPermission) throws SecurityException
  {
    ValueEnforcer.notNull (aPermission, "Permission");

    final SecurityManager aSM = System.getSecurityManager ();
    if (aSM != null)
      aSM.checkPermission (aPermission);
  }
}
