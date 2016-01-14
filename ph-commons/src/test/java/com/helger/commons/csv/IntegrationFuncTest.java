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
package com.helger.commons.csv;

/**
 Copyright 2005 Bytecode Pty Ltd.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public final class IntegrationFuncTest
{
  private File m_aTempFile;

  @Before
  public void setUp () throws IOException
  {
    m_aTempFile = File.createTempFile ("csvWriterTest", ".csv");
    m_aTempFile.deleteOnExit ();
  }

  /**
   * Test the full cycle of write-read
   *
   * @throws IOException
   *         never
   */
  @Test
  public void testWriteRead () throws IOException
  {
    final String [] [] data = new String [] [] { { "hello, a test", "one nested \" test" },
                                                 { "\"\"", "test", null, "8" } };

    try (final CSVWriter writer = new CSVWriter (new FileWriter (m_aTempFile)))
    {
      for (final String [] aData : data)
      {
        writer.writeNext (aData);
      }
    }

    try (final CSVReader reader = new CSVReader (new FileReader (m_aTempFile)))
    {
      List <String> line;
      for (int row = 0; (line = reader.readNext ()) != null; row++)
      {
        assertTrue (line.size () == data[row].length);

        for (int col = 0; col < line.size (); col++)
        {
          if (data[row][col] == null)
            assertEquals ("", line.get (col));
          else
            assertEquals (data[row][col], line.get (col));
        }
      }
    }
  }
}
