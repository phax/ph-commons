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
package com.helger.commons.io.provider;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Test;

import com.helger.commons.io.EAppend;
import com.helger.commons.io.file.FileOperations;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link FileSystemByteStreamProvider}.
 *
 * @author Philip Helger
 */
public final class FileSystemByteStreamProviderTest
{
  @Test
  public void testAll ()
  {
    final FileSystemByteStreamProvider aFSSR = new FileSystemByteStreamProvider (new File ("."));
    final InputStream aIS = aFSSR.getInputStream ("pom.xml");
    assertNotNull (aIS);
    StreamHelper.close (aIS);

    final OutputStream aOS = aFSSR.getOutputStream ("$deleteme.txt", EAppend.DEFAULT);
    assertNotNull (aOS);
    StreamHelper.close (aOS);
    assertTrue (FileOperations.deleteFile (new File ("$deleteme.txt")).isSuccess ());

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new FileSystemByteStreamProvider (new File (".")),
                                                                       new FileSystemByteStreamProvider (new File (".")));
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new FileSystemByteStreamProvider (new File (".")),
                                                                       new FileSystemByteStreamProvider ("."));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new FileSystemByteStreamProvider (new File (".")),
                                                                           new FileSystemByteStreamProvider (new File ("..")));
  }
}
