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
package com.helger.commons.lang.priviledged;

import java.security.AccessControlException;
import java.security.AccessController;
import java.security.PrivilegedAction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;

/**
 * Simple wrapper around {@link AccessController} to catch exceptions centrally.
 *
 * @author Philip Helger
 */
@Immutable
public final class AccessControllerHelper
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (AccessControllerHelper.class);

  private AccessControllerHelper ()
  {}

  @Nullable
  public static <T> T call (@Nonnull final PrivilegedAction <T> aAction)
  {
    ValueEnforcer.notNull (aAction, "Action");

    try
    {
      return AccessController.doPrivileged (aAction);
    }
    catch (final AccessControlException ex)
    {
      s_aLogger.error ("Failed to execute PrivilegedAction " + aAction, ex);
      return null;
    }
  }

  public static <T> void run (@Nonnull final PrivilegedAction <T> aAction)
  {
    // Just ignore the return value
    call (aAction);
  }
}
