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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileFilter;

import org.junit.Test;

/**
 * Test class for class {@link FileFilterParentDirectoryPublic}.
 *
 * @author Philip Helger
 */
public final class FileFilterParentDirectoryPublicTest
{
  @Test
  public void testGetParentDirectoryPublic ()
  {
    final FileFilter aFilter = new FileFilterParentDirectoryPublic ();
    assertNotNull (aFilter);

    // file
    assertTrue (aFilter.accept (new File ("pom.xml")));
    // not existing file
    assertTrue (aFilter.accept (new File ("file.htm")));
    // directory
    assertTrue (aFilter.accept (new File ("src")));
    // null
    assertFalse (aFilter.accept (null));
    // hidden file
    assertFalse (aFilter.accept (new File (".svn/pom.xml")));

    assertEquals (aFilter, new FileFilterParentDirectoryPublic ());
  }
}
