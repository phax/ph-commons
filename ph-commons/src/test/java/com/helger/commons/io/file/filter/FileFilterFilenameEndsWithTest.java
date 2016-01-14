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
package com.helger.commons.io.file.filter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FilenameFilter;

import org.junit.Test;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Test class for class {@link FileFilterFilenameEndsWith}.
 *
 * @author Philip Helger
 */
public final class FileFilterFilenameEndsWithTest
{
  @Test
  @SuppressFBWarnings (value = "NP_NONNULL_PARAM_VIOLATION")
  public void testAll ()
  {
    try
    {
      // null not allowed
      new FileFilterFilenameEndsWith (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    final FilenameFilter ff = new FileFilterFilenameEndsWith (".htm");
    assertNotNull (ff);
    assertTrue (ff.accept (null, "file.htm"));
    assertTrue (ff.accept (new File ("dir"), "file.htm"));
    assertFalse (ff.accept (null, "file.html"));
    assertFalse (ff.accept (new File ("dir"), "file.html"));
    assertFalse (ff.accept (null, null));
    assertFalse (ff.accept (null, ""));
  }
}
