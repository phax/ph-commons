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
package com.helger.scope.mock;

import java.io.File;

import org.junit.rules.ExternalResource;

import com.helger.annotation.style.OverrideOnDemand;

/**
 * Special JUnit test rule to initialize scopes correctly.
 *
 * @author Philip Helger
 */
public class ScopeTestRule extends ExternalResource
{
  public static final File STORAGE_PATH = ScopeAwareTestSetup.STORAGE_PATH;

  @Override
  @OverrideOnDemand
  public void before ()
  {
    ScopeAwareTestSetup.setupScopeTests ();
  }

  @Override
  @OverrideOnDemand
  public void after ()
  {
    ScopeAwareTestSetup.shutdownScopeTests ();
  }
}
