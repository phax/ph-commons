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
package com.helger.commons.io.file.filter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;

import org.junit.Test;

import com.helger.commons.filter.IFilter;
import com.helger.commons.mock.PhlocTestUtils;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Test class for class {@link FileFilterToIFilterAdapter}.
 * 
 * @author Philip Helger
 */
public final class FileFilterToIFilterAdapterTest
{
  @Test
  @SuppressFBWarnings (value = "NP_NONNULL_PARAM_VIOLATION")
  public void testAll ()
  {
    try
    {
      new FileFilterToIFilterAdapter ((FilenameFilter) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      new FileFilterToIFilterAdapter ((FileFilter) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    IFilter <File> aFilter = new FileFilterToIFilterAdapter (FileFilterParentDirectoryPublic.getInstance ());

    // file
    assertTrue (aFilter.matchesFilter (new File ("pom.xml")));
    // not existing file
    assertTrue (aFilter.matchesFilter (new File ("file.htm")));
    // directory
    assertTrue (aFilter.matchesFilter (new File ("src")));
    // null
    assertFalse (aFilter.matchesFilter (null));

    PhlocTestUtils.testToStringImplementation (aFilter);

    aFilter = FileFilterToIFilterAdapter.getANDChained (FileFilterParentDirectoryPublic.getInstance (),
                                                        FileFilterFileOnly.getInstance ());
    assertNotNull (aFilter);

    // file
    assertTrue (aFilter.matchesFilter (new File ("pom.xml")));
    // not existing file
    assertFalse (aFilter.matchesFilter (new File ("file.htm")));
    // directory
    assertFalse (aFilter.matchesFilter (new File ("src")));
    // null
    assertFalse (aFilter.matchesFilter (null));

    PhlocTestUtils.testToStringImplementation (aFilter);

    aFilter = FileFilterToIFilterAdapter.getORChained (FileFilterParentDirectoryPublic.getInstance (),
                                                       FileFilterFileOnly.getInstance ());
    assertNotNull (aFilter);

    // file
    assertTrue (aFilter.matchesFilter (new File ("pom.xml")));
    // not existing file
    assertTrue (aFilter.matchesFilter (new File ("file.htm")));
    // directory
    assertTrue (aFilter.matchesFilter (new File ("src")));
    // null
    assertFalse (aFilter.matchesFilter (null));

    PhlocTestUtils.testToStringImplementation (aFilter);

    try
    {
      FileFilterToIFilterAdapter.getANDChained ((FileFilter []) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      FileFilterToIFilterAdapter.getANDChained (new FileFilter [0]);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    try
    {
      FileFilterToIFilterAdapter.getORChained ((FileFilter []) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      FileFilterToIFilterAdapter.getORChained (new FileFilter [0]);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }
}
