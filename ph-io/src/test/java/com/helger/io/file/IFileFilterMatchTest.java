/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.io.file;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Test;

/**
 * Additional test class for the factory methods of {@link IFileFilter} that are not covered by
 * {@link IFileFilterFuncTest}.
 *
 * @author Philip Helger
 */
public final class IFileFilterMatchTest
{
  private static final File FILE = new File ("target/junittest-filefilter.txt");

  @Test
  public void testFilenameNotEquals ()
  {
    final IFileFilter aFilter = IFileFilter.filenameNotEquals ("junittest-filefilter.txt");
    assertFalse (aFilter.test (FILE));
    assertFalse (aFilter.accept (FILE));
    assertTrue (aFilter.test (new File ("target/other.txt")));
    assertFalse (aFilter.test (null));
    // The (dir, name) overload
    assertFalse (aFilter.accept (FILE.getParentFile (), FILE.getName ()));
    assertTrue (aFilter.accept (FILE.getParentFile (), "other.txt"));

    try
    {
      IFileFilter.filenameNotEquals (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {
      // expected
    }
  }

  @Test
  public void testFilenameNotEqualsIgnoreCase ()
  {
    final IFileFilter aFilter = IFileFilter.filenameNotEqualsIgnoreCase ("JUNITTEST-FILEFILTER.TXT");
    assertFalse (aFilter.test (FILE));
    assertTrue (aFilter.test (new File ("target/other.txt")));
    assertFalse (aFilter.test (null));

    try
    {
      IFileFilter.filenameNotEqualsIgnoreCase (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {
      // expected
    }
  }

  @Test
  public void testFilenameMatchAny ()
  {
    final IFileFilter aFilter = IFileFilter.filenameMatchAny ("junittest-filefilter.txt", "other.txt");
    assertTrue (aFilter.test (FILE));
    assertTrue (aFilter.test (new File ("target/other.txt")));
    assertFalse (aFilter.test (new File ("target/third.txt")));
    assertFalse (aFilter.test (null));

    // Nothing matches an empty list
    assertFalse (IFileFilter.filenameMatchAny (new String [0]).test (FILE));

    try
    {
      IFileFilter.filenameMatchAny ((String []) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {
      // expected
    }
  }

  @Test
  public void testFilenameMatchNone ()
  {
    final IFileFilter aFilter = IFileFilter.filenameMatchNone ("junittest-filefilter.txt", "other.txt");
    assertFalse (aFilter.test (FILE));
    assertFalse (aFilter.test (new File ("target/other.txt")));
    assertTrue (aFilter.test (new File ("target/third.txt")));
    assertFalse (aFilter.test (null));

    // Everything matches an empty list
    assertTrue (IFileFilter.filenameMatchNone (new String [0]).test (FILE));

    try
    {
      IFileFilter.filenameMatchNone ((String []) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {
      // expected
    }
  }

  @Test
  public void testAcceptNullParams ()
  {
    final IFileFilter aFilter = IFileFilter.fileOnly ();
    assertFalse (aFilter.accept (null));
    assertFalse (aFilter.accept (null, null));
    assertFalse (aFilter.accept (FILE.getParentFile (), null));
  }
}
