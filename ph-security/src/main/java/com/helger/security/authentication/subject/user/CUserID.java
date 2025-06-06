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
package com.helger.security.authentication.subject.user;

import javax.annotation.concurrent.Immutable;

/**
 * Constants for user handling
 *
 * @author Philip Helger
 */
@Immutable
public final class CUserID
{
  /** The user ID to be used, if no user is logged in */
  public static final String USER_ID_GUEST = "$GUEST$";

  private CUserID ()
  {}
}
