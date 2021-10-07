/*
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
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
package com.helger.config.source.envvar;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Test class for class {@link EnvVarHelper}.
 *
 * @author Philip Helger
 */
public final class EnvVarHelperTest
{
  @Test
  public void testUnification ()
  {
    assertEquals ("LANG", EnvVarHelper.getUnifiedSysEnvName ("LANG", '_'));
    assertEquals ("LANG", EnvVarHelper.getUnifiedSysEnvName ("lanG", '_'));
    assertEquals ("LA_NG", EnvVarHelper.getUnifiedSysEnvName ("LA_NG", '_'));
    assertEquals ("LA_NG", EnvVarHelper.getUnifiedSysEnvName ("LA?NG", '_'));
    assertEquals ("_ABC", EnvVarHelper.getUnifiedSysEnvName ("0ABC", '_'));
    assertEquals ("_123", EnvVarHelper.getUnifiedSysEnvName ("0123", '_'));
    assertEquals ("____", EnvVarHelper.getUnifiedSysEnvName ("0?*#", '_'));
    assertEquals ("I_REALLY_DON_T_KNOW_THAT_ENV_VAR_", EnvVarHelper.getUnifiedSysEnvName ("I really don't know that env var!", '_'));
  }
}
