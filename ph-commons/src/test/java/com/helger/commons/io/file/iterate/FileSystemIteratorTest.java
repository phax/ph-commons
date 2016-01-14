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
package com.helger.commons.io.file.iterate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Test;

import com.helger.commons.collection.iterate.IIterableIterator;
import com.helger.commons.io.file.filter.FileFilterFileOnly;
import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link FileSystemIterator}.
 *
 * @author Philip Helger
 */
public final class FileSystemIteratorTest
{
  @Test
  public void testCtor ()
  {
    try
    {
      // null not allowed
      new FileSystemIterator ((File) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // null not allowed
      new FileSystemIterator ((String) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    // create in current directory - don't know what that is -> expect nothing
    IIterableIterator <File> it = new FileSystemIterator (new File ("."));
    assertNotNull (it);
    assertTrue (it.hasNext ());
    while (it.hasNext ())
      it.next ();

    CommonsTestHelper.testToStringImplementation (it);

    // Non-existing directory
    it = new FileSystemIterator (new File ("anydir"));
    assertNotNull (it);
    assertFalse (it.hasNext ());

    // With one filter
    it = FileSystemIterator.create (new File ("."), new FileFilterFileOnly ());
    assertNotNull (it);
    assertTrue (it.hasNext ());
    while (it.hasNext ())
      it.next ();

    // With one filter
    it = FileSystemIterator.create (new File ("."), new FileFilterFileOnly (), new FileFilterFileOnly ());
    assertNotNull (it);
    assertTrue (it.hasNext ());
    while (it.hasNext ())
      it.next ();
  }
}
