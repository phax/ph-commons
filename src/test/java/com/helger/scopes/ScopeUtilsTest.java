/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.scopes;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test class for class {@link ScopeUtils}.
 *
 * @author Philip Helger
 */
public final class ScopeUtilsTest
{
  @Test
  public void testSettings ()
  {
    assertTrue (ScopeUtils.DEFAULT_DEBUG_GLOBAL_SCOPE == ScopeUtils.isDebugGlobalScopeEnabled ());
    assertTrue (ScopeUtils.DEFAULT_DEBUG_APPLICATION_SCOPE == ScopeUtils.isDebugApplicationScopeEnabled ());
    assertTrue (ScopeUtils.DEFAULT_DEBUG_SESSION_SCOPE == ScopeUtils.isDebugSessionScopeEnabled ());
    assertTrue (ScopeUtils.DEFAULT_DEBUG_SESSION_APPLICATION_SCOPE == ScopeUtils.isDebugSessionApplicationScopeEnabled ());
    assertTrue (ScopeUtils.DEFAULT_DEBUG_REQUEST_SCOPE == ScopeUtils.isDebugRequestScopeEnabled ());

    ScopeUtils.setDebugGlobalScopeEnabled (!ScopeUtils.DEFAULT_DEBUG_GLOBAL_SCOPE);
    ScopeUtils.setDebugApplicationScopeEnabled (!ScopeUtils.DEFAULT_DEBUG_APPLICATION_SCOPE);
    ScopeUtils.setDebugSessionScopeEnabled (!ScopeUtils.DEFAULT_DEBUG_SESSION_SCOPE);
    ScopeUtils.setDebugSessionApplicationScopeEnabled (!ScopeUtils.DEFAULT_DEBUG_SESSION_APPLICATION_SCOPE);
    ScopeUtils.setDebugRequestScopeEnabled (!ScopeUtils.DEFAULT_DEBUG_REQUEST_SCOPE);

    assertFalse (ScopeUtils.DEFAULT_DEBUG_GLOBAL_SCOPE == ScopeUtils.isDebugGlobalScopeEnabled ());
    assertFalse (ScopeUtils.DEFAULT_DEBUG_APPLICATION_SCOPE == ScopeUtils.isDebugApplicationScopeEnabled ());
    assertFalse (ScopeUtils.DEFAULT_DEBUG_SESSION_SCOPE == ScopeUtils.isDebugSessionScopeEnabled ());
    assertFalse (ScopeUtils.DEFAULT_DEBUG_SESSION_APPLICATION_SCOPE == ScopeUtils.isDebugSessionApplicationScopeEnabled ());
    assertFalse (ScopeUtils.DEFAULT_DEBUG_REQUEST_SCOPE == ScopeUtils.isDebugRequestScopeEnabled ());

    ScopeUtils.setDebugGlobalScopeEnabled (ScopeUtils.DEFAULT_DEBUG_GLOBAL_SCOPE);
    ScopeUtils.setDebugApplicationScopeEnabled (ScopeUtils.DEFAULT_DEBUG_APPLICATION_SCOPE);
    ScopeUtils.setDebugSessionScopeEnabled (ScopeUtils.DEFAULT_DEBUG_SESSION_SCOPE);
    ScopeUtils.setDebugSessionApplicationScopeEnabled (ScopeUtils.DEFAULT_DEBUG_SESSION_APPLICATION_SCOPE);
    ScopeUtils.setDebugRequestScopeEnabled (ScopeUtils.DEFAULT_DEBUG_REQUEST_SCOPE);
  }
}
