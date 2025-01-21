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
package com.helger.commons.supplementary.test.java;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Ignore;
import org.junit.Test;

import com.helger.commons.io.file.FilenameHelper;
import com.helger.commons.system.EOperatingSystem;

public final class JavaFileUNCFuncTest
{
  @Test
  @Ignore ("See https://bugs.openjdk.org/browse/JDK-8285445")
  public void testUNC ()
  {
    if (EOperatingSystem.getCurrentOS ().isWindowsBased ())
    {
      // UNC hack: convert arbitrary path to UNC ;-)
      final File f = new File (FilenameHelper.WINDOWS_UNC_PREFIX_LOCAL1 + new File ("pom.xml").getAbsolutePath ());
      assertTrue (f.exists ());
    }
  }
}
