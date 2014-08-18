/**
 * Copyright (C) 2006-2014 phloc systems (www.phloc.com)
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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

import java.io.File;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.io.file.FileUtils;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public final class FuncTestJavaFile
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (FuncTestJavaFile.class);

  private static void _log (final File f)
  {
    s_aLogger.info ("Next file:");
    s_aLogger.info ("  AbsolutePath: " + f.getAbsolutePath ());
    s_aLogger.info ("  Name:         " + f.getName ());
    s_aLogger.info ("  Path:         " + f.getPath ());
    s_aLogger.info ("  Parent:       " + f.getParent ());
    s_aLogger.info ("  Parent2:      " + f.getAbsoluteFile ().getParent ());
    s_aLogger.info ("  Parent3:      " + f.getParentFile ());
    s_aLogger.info ("  Parent4:      " + f.getAbsoluteFile ().getParentFile ());
    s_aLogger.info ("  isAbsolute:   " + f.isAbsolute ());
    s_aLogger.info ("  exists:       " + f.exists ());
    if (f.exists ())
    {
      s_aLogger.info ("  isDirectory:  " + f.isDirectory ());
      s_aLogger.info ("  isFile:       " + f.isFile ());
      s_aLogger.info ("  isHidden:     " + f.isHidden ());
      s_aLogger.info ("  canRead:      " + FileUtils.canRead (f));
      s_aLogger.info ("  canWrite:     " + FileUtils.canWrite (f));
      s_aLogger.info ("  canExecute:   " + FileUtils.canExecute (f));
    }
  }

  @Test
  @SuppressFBWarnings (value = "DMI_HARDCODED_ABSOLUTE_FILENAME")
  public void testGetPath ()
  {
    _log (new File ("pom.xml"));
    _log (new File ("c:\\pom.xml"));
    _log (new File ("c:\\"));
    File f = new File ("pom.xml\u0000.txt");
    _log (f);

    f = FileUtils.getSecureFile (f);
    _log (f);
  }
}
