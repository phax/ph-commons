/*
 * Copyright (C) 2014-2022 Philip Helger (www.helger.com)
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
package com.helger.scope;

import org.junit.Test;

import com.helger.commons.mock.CommonsAssert;

/**
 * Test class for class {@link ScopeHelper}.
 *
 * @author Philip Helger
 */
public final class ScopeHelperTest
{
  @Test
  public void testSettings ()
  {
    CommonsAssert.assertEquals (ScopeHelper.DEFAULT_DEBUG_GLOBAL_SCOPE, ScopeHelper.isDebugGlobalScopeEnabled ());
    CommonsAssert.assertEquals (ScopeHelper.DEFAULT_DEBUG_SESSION_SCOPE, ScopeHelper.isDebugSessionScopeEnabled ());
    CommonsAssert.assertEquals (ScopeHelper.DEFAULT_DEBUG_REQUEST_SCOPE, ScopeHelper.isDebugRequestScopeEnabled ());

    ScopeHelper.setDebugGlobalScopeEnabled (!ScopeHelper.DEFAULT_DEBUG_GLOBAL_SCOPE);
    ScopeHelper.setDebugSessionScopeEnabled (!ScopeHelper.DEFAULT_DEBUG_SESSION_SCOPE);
    ScopeHelper.setDebugRequestScopeEnabled (!ScopeHelper.DEFAULT_DEBUG_REQUEST_SCOPE);

    CommonsAssert.assertNotEquals (ScopeHelper.DEFAULT_DEBUG_GLOBAL_SCOPE, ScopeHelper.isDebugGlobalScopeEnabled ());
    CommonsAssert.assertNotEquals (ScopeHelper.DEFAULT_DEBUG_SESSION_SCOPE, ScopeHelper.isDebugSessionScopeEnabled ());
    CommonsAssert.assertNotEquals (ScopeHelper.DEFAULT_DEBUG_REQUEST_SCOPE, ScopeHelper.isDebugRequestScopeEnabled ());

    ScopeHelper.setDebugGlobalScopeEnabled (ScopeHelper.DEFAULT_DEBUG_GLOBAL_SCOPE);
    ScopeHelper.setDebugSessionScopeEnabled (ScopeHelper.DEFAULT_DEBUG_SESSION_SCOPE);
    ScopeHelper.setDebugRequestScopeEnabled (ScopeHelper.DEFAULT_DEBUG_REQUEST_SCOPE);
  }
}
