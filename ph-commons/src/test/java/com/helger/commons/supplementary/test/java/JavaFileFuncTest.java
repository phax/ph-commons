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

import java.io.File;
import java.io.IOException;

import org.jspecify.annotations.NonNull;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.base.system.EOperatingSystem;
import com.helger.io.file.FileHelper;
import com.helger.io.file.FilenameHelper;

public final class JavaFileFuncTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (JavaFileFuncTest.class);

  private static void _log (@NonNull final File f)
  {
    LOGGER.info ("Next file:");
    LOGGER.info ("  AbsolutePath:  " + f.getAbsolutePath ());
    try
    {
      LOGGER.info ("  CanonicalPath: " + f.getCanonicalPath ());
    }
    catch (final IOException ex)
    {
      LOGGER.info ("  CanonicalPath: " + ex.getMessage ());
    }
    LOGGER.info ("  Name:          " + f.getName ());
    LOGGER.info ("  Path:          " + f.getPath ());
    LOGGER.info ("  Parent:        " + f.getParent ());
    LOGGER.info ("  Parent2:       " + f.getAbsoluteFile ().getParent ());
    LOGGER.info ("  Parent3:       " + f.getParentFile ());
    LOGGER.info ("  Parent4:       " + f.getAbsoluteFile ().getParentFile ());
    LOGGER.info ("  isAbsolute:    " + f.isAbsolute ());
    LOGGER.info ("  exists:        " + f.exists ());
    if (f.exists ())
    {
      LOGGER.info ("  isDirectory:   " + f.isDirectory ());
      LOGGER.info ("  isFile:        " + f.isFile ());
      LOGGER.info ("  isHidden:      " + f.isHidden ());
      LOGGER.info ("  canRead:       " + f.canRead ());
      LOGGER.info ("  canWrite:      " + f.canWrite ());
      LOGGER.info ("  canExecute:    " + f.canExecute ());
    }
  }

  @Test
  public void testGetPath ()
  {
    _log (new File ("pom.xml"));
    if (EOperatingSystem.getCurrentOS ().isWindowsBased ())
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
