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
package com.helger.commons.id.factory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Test;

import com.helger.commons.io.file.FileOperations;
import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link FileLongIDFactory}.
 *
 * @author Philip Helger
 */
public final class FileLongIDFactoryTest
{
  @Test
  public void testAll ()
  {
    final File f = new File ("my-file-with.ids");
    final File f2 = new File ("my-other-file-with.ids");
    try
    {
      final FileLongIDFactory x = new FileLongIDFactory (f);
      // Compare before retrieving an ID!
      CommonsTestHelper.testDefaultImplementationWithEqualContentObject (x, new FileLongIDFactory (f));
      CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (x, new FileLongIDFactory (f2));
      CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (x,
                                                                             new FileLongIDFactory (f,
                                                                                                    FileLongIDFactory.DEFAULT_RESERVE_COUNT *
                                                                                                       2));

      for (int i = 0; i < x.getReserveCount () * 10; ++i)
        assertEquals (i, x.getNewID ());
    }
    finally
    {
      FileOperations.deleteFile (f);
      FileOperations.deleteFile (f2);
    }

    try
    {
      new FileLongIDFactory (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      // Invalid reserve count
      new FileLongIDFactory (new File ("any"), 0);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }
}
