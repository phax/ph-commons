/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.junit.Before;
import org.junit.Test;

import com.helger.commons.collection.impl.ICommonsList;

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
    final Charset aCharset = StandardCharsets.UTF_8;

    try (final CSVWriter writer = new CSVWriter (new OutputStreamWriter (new FileOutputStream (m_aTempFile), aCharset)))
    {
      for (final String [] aData : data)
        writer.writeNext (aData);
    }

    try (final CSVReader reader = new CSVReader (new InputStreamReader (new FileInputStream (m_aTempFile), aCharset)))
    {
      int row = 0;
      while (true)
      {
        final ICommonsList <String> line = reader.readNext ();
        if (line == null)
          break;

        assertEquals (line.size (), data[row].length);

        for (int col = 0; col < line.size (); col++)
        {
          if (data[row][col] == null)
            assertEquals ("", line.get (col));
          else
            assertEquals (data[row][col], line.get (col));
        }
        row++;
      }
    }
  }
}
