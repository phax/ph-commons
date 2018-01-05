/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.commons.io.relative;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import com.helger.commons.io.file.FileHelper;
import com.helger.commons.system.EOperatingSystem;

/**
 * Test class for class {@link PathRelativeIO}
 *
 * @author Philip Helger
 */
public final class PathRelativeIOTest
{
  @Test
  public void testFileBased ()
  {
    final String sBase = new File ("").getAbsolutePath ();
    final IPathRelativeIO aIO = new PathRelativeIO (sBase);

    assertEquals (sBase, aIO.getBasePath ());
    assertFalse (aIO.getResource ("non-existing").exists ());
    assertTrue (aIO.getResource ("pom.xml").exists ());
    // fails on Linux
    if (EOperatingSystem.WINDOWS.isCurrentOS ())
      assertTrue (aIO.getResource ("/pom.xml").exists ());
    assertTrue (aIO.getResource ("src/etc/javadoc.css").exists ());
    // Passing an absolute path does no longer work on Linux
    if (false)
      assertTrue (aIO.getResource (new File ("pom.xml").getAbsolutePath ()).exists ());
  }

  @Test
  public void testURLBased ()
  {
    final String sBase = FileHelper.getAsURLString (new File (""));
    final IPathRelativeIO aIO = new PathRelativeIO (sBase);

    assertEquals (sBase, aIO.getBasePath ());
    assertFalse (aIO.getResource ("non-existing").exists ());
    assertTrue (aIO.getResource ("pom.xml").exists ());
    // fails on Linux
    if (EOperatingSystem.WINDOWS.isCurrentOS ())
      assertTrue (aIO.getResource ("/pom.xml").exists ());
    assertTrue (aIO.getResource ("src/etc/javadoc.css").exists ());
    // Passing an absolute path does no longer work on Linux
    if (false)
      assertTrue (aIO.getResource (new File ("pom.xml").getAbsolutePath ()).exists ());
  }
}
