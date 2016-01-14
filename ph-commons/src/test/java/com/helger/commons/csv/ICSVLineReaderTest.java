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

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import javax.annotation.Nonnull;

import org.junit.Test;

/**
 * Test class for class {@link ICSVLineReader}.
 *
 * @author Philip Helger
 */
public final class ICSVLineReaderTest
{
  private static final String ORIGINAL = "This is the original string\r\n";
  private static final String WITH_CR = "This is the original string\r";
  private static final String NO_CR = "This is the original string";
  private static final String EMPTY_STRING = "";
  private static final String NULL_STRING = null;

  @Nonnull
  private static ICSVLineReader createLineReaderforString (@Nonnull final String s, final boolean bKeepCR)
  {
    final StringReader sr = new StringReader (s);
    if (bKeepCR)
      return new CSVLineReaderKeepCR (new BufferedReader (sr));
    return new CSVLineReaderBufferedReader (new BufferedReader (sr));
  }

  @Test
  public void lineReaderWillKeepCR () throws IOException
  {
    final ICSVLineReader keepCRReader = createLineReaderforString (ORIGINAL, true);
    assertEquals (WITH_CR, keepCRReader.readLine ());
  }

  @Test
  public void lineReaderWillRemoveCR () throws IOException
  {
    final ICSVLineReader noCRReader = createLineReaderforString (ORIGINAL, false);
    assertEquals (NO_CR, noCRReader.readLine ());
  }

  @Test
  public void lineReaderKeepingCRWillHandleStringWithNoLinefeed () throws IOException
  {
    final ICSVLineReader reader = createLineReaderforString (NO_CR, true);
    assertEquals (NO_CR, reader.readLine ());
  }

  @Test
  public void lineReaderNoCRWillHandleStringWithNoLinefeed () throws IOException
  {
    final ICSVLineReader reader = createLineReaderforString (NO_CR, false);
    assertEquals (NO_CR, reader.readLine ());
  }

  @Test
  public void lineReaderKeepingCRWillHandleEmptyString () throws IOException
  {
    final ICSVLineReader reader = createLineReaderforString (EMPTY_STRING, true);
    assertEquals (NULL_STRING, reader.readLine ());
  }

  @Test
  public void lineReaderNoCRWillHandleEmptyString () throws IOException
  {
    final ICSVLineReader reader = createLineReaderforString (EMPTY_STRING, false);
    assertEquals (NULL_STRING, reader.readLine ());
  }
}
