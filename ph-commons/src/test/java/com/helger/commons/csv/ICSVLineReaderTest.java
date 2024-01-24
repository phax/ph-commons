/*
 * Copyright (C) 2014-2024 Philip Helger (www.helger.com)
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

import java.io.IOException;

import javax.annotation.Nonnull;

import org.junit.Test;

import com.helger.commons.io.stream.NonBlockingBufferedReader;
import com.helger.commons.io.stream.NonBlockingStringReader;

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

  @SuppressWarnings ("resource")
  @Nonnull
  private static ICSVLineReader _createLineReaderforString (@Nonnull final String s, final boolean bKeepCR)
  {
    final NonBlockingStringReader sr = new NonBlockingStringReader (s);
    if (bKeepCR)
      return new CSVLineReaderKeepCR (new NonBlockingBufferedReader (sr));
    return new CSVLineReaderNonBlockingBufferedReader (new NonBlockingBufferedReader (sr));
  }

  @Test
  public void testLineReaderWillKeepCR () throws IOException
  {
    final ICSVLineReader keepCRReader = _createLineReaderforString (ORIGINAL, true);
    assertEquals (WITH_CR, keepCRReader.readLine ());
  }

  @Test
  public void testLineReaderWillRemoveCR () throws IOException
  {
    final ICSVLineReader noCRReader = _createLineReaderforString (ORIGINAL, false);
    assertEquals (NO_CR, noCRReader.readLine ());
  }

  @Test
  public void testLineReaderKeepingCRWillHandleStringWithNoLinefeed () throws IOException
  {
    final ICSVLineReader reader = _createLineReaderforString (NO_CR, true);
    assertEquals (NO_CR, reader.readLine ());
  }

  @Test
  public void testLineReaderNoCRWillHandleStringWithNoLinefeed () throws IOException
  {
    final ICSVLineReader reader = _createLineReaderforString (NO_CR, false);
    assertEquals (NO_CR, reader.readLine ());
  }

  @Test
  public void testLineReaderKeepingCRWillHandleEmptyString () throws IOException
  {
    final ICSVLineReader reader = _createLineReaderforString (EMPTY_STRING, true);
    assertEquals (NULL_STRING, reader.readLine ());
  }

  @Test
  public void testLineReaderNoCRWillHandleEmptyString () throws IOException
  {
    final ICSVLineReader reader = _createLineReaderforString (EMPTY_STRING, false);
    assertEquals (NULL_STRING, reader.readLine ());
  }
}
