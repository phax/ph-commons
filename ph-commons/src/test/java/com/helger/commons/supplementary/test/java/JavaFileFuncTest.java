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
package com.helger.commons.supplementary.test.java;

import java.io.File;
import java.io.IOException;

import javax.annotation.Nonnull;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.io.file.FileHelper;
import com.helger.commons.io.file.FilenameHelper;
import com.helger.commons.system.EOperatingSystem;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public final class JavaFileFuncTest
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (JavaFileFuncTest.class);

  private static void _log (@Nonnull final File f)
  {
    s_aLogger.info ("Next file:");
    s_aLogger.info ("  AbsolutePath:  " + f.getAbsolutePath ());
    try
    {
      s_aLogger.info ("  CanonicalPath: " + f.getCanonicalPath ());
    }
    catch (final IOException ex)
    {
      s_aLogger.info ("  CanonicalPath: " + ex.getMessage ());
    }
    s_aLogger.info ("  Name:          " + f.getName ());
    s_aLogger.info ("  Path:          " + f.getPath ());
    s_aLogger.info ("  Parent:        " + f.getParent ());
    s_aLogger.info ("  Parent2:       " + f.getAbsoluteFile ().getParent ());
    s_aLogger.info ("  Parent3:       " + f.getParentFile ());
    s_aLogger.info ("  Parent4:       " + f.getAbsoluteFile ().getParentFile ());
    s_aLogger.info ("  isAbsolute:    " + f.isAbsolute ());
    s_aLogger.info ("  exists:        " + f.exists ());
    if (f.exists ())
    {
      s_aLogger.info ("  isDirectory:   " + f.isDirectory ());
      s_aLogger.info ("  isFile:        " + f.isFile ());
      s_aLogger.info ("  isHidden:      " + f.isHidden ());
      s_aLogger.info ("  canRead:       " + FileHelper.canRead (f));
      s_aLogger.info ("  canWrite:      " + FileHelper.canWrite (f));
      s_aLogger.info ("  canExecute:    " + FileHelper.canExecute (f));
    }
  }

  @Test
  @SuppressFBWarnings (value = "DMI_HARDCODED_ABSOLUTE_FILENAME")
  public void testGetPath ()
  {
    _log (new File ("pom.xml"));
    if (EOperatingSystem.WINDOWS.isCurrentOS ())
      _log (new File (FilenameHelper.WINDOWS_UNC_PREFIX_LOCAL1 + new File ("pom.xml").getAbsolutePath ()));
    _log (new File ("pom.xml."));
    _log (new File ("c:\\pom.xml"));
    _log (new File ("c:\\", "pom.xml"));
    _log (new File ("c:\\", "pom"));
    _log (new File ("c:\\"));
    File f = new File ("pom.xml\u0000.txt");
    _log (f);

    f = FileHelper.getSecureFile (f);
    _log (f);
  }
}
