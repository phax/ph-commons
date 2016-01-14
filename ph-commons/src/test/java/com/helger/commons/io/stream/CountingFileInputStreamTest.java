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
package com.helger.commons.io.stream;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link CountingFileInputStream}.
 *
 * @author Philip Helger
 */
public final class CountingFileInputStreamTest
{
  @Test
  public void testAll () throws IOException
  {
    CountingFileInputStream aCIS = new CountingFileInputStream ("pom.xml");
    try
    {
      aCIS.read ();
      assertEquals (1, aCIS.read (new byte [5], 1, 1));
      StreamHelper.copyInputStreamToOutputStream (aCIS, new NonBlockingByteArrayOutputStream ());
      CommonsTestHelper.testToStringImplementation (aCIS);
    }
    finally
    {
      StreamHelper.close (aCIS);
    }

    aCIS = new CountingFileInputStream (new File ("pom.xml"));
    try
    {
      aCIS.read ();
      assertEquals (1, aCIS.read (new byte [5], 1, 1));
      StreamHelper.copyInputStreamToOutputStream (aCIS, new NonBlockingByteArrayOutputStream ());
      CommonsTestHelper.testToStringImplementation (aCIS);
    }
    finally
    {
      StreamHelper.close (aCIS);
    }
  }
}
