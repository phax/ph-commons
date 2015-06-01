/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.commons.io.resolver;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Test;

import com.helger.commons.io.EAppend;
import com.helger.commons.io.file.FileOperations;
import com.helger.commons.io.streams.StreamUtils;
import com.helger.commons.mock.PHTestUtils;

/**
 * Test class for class {@link FileSystemByteStreamResolver}.
 * 
 * @author Philip Helger
 */
public final class FileSystemByteStreamResolverTest
{
  @Test
  public void testAll ()
  {
    final FileSystemByteStreamResolver aFSSR = new FileSystemByteStreamResolver (new File ("."));
    final InputStream aIS = aFSSR.getInputStream ("pom.xml");
    assertNotNull (aIS);
    StreamUtils.close (aIS);

    final OutputStream aOS = aFSSR.getOutputStream ("$deleteme.txt", EAppend.DEFAULT);
    assertNotNull (aOS);
    StreamUtils.close (aOS);
    assertTrue (FileOperations.deleteFile (new File ("$deleteme.txt")).isSuccess ());

    PHTestUtils.testDefaultImplementationWithEqualContentObject (new FileSystemByteStreamResolver (new File (".")),
                                                                 new FileSystemByteStreamResolver (new File (".")));
    PHTestUtils.testDefaultImplementationWithEqualContentObject (new FileSystemByteStreamResolver (new File (".")),
                                                                 new FileSystemByteStreamResolver ("."));
    PHTestUtils.testDefaultImplementationWithDifferentContentObject (new FileSystemByteStreamResolver (new File (".")),
                                                                     new FileSystemByteStreamResolver (new File ("..")));
  }
}
