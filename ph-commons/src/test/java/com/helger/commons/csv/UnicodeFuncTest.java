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

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.io.stream.NonBlockingBufferedReader;
import com.helger.commons.io.stream.NonBlockingStringReader;
import com.helger.commons.io.stream.NonBlockingStringWriter;

public final class UnicodeFuncTest
{
  private static final String COMPOUND_STRING = "ä,ö";
  private static final String COMPOUND_STRING_WITH_QUOTES = "\"ä\",\"ö\"";
  private static final String FIRST_STRING = "ä";
  private static final String SECOND_STRING = "ö";
  private static final ICommonsList <String> UNICODE_ARRAY = new CommonsArrayList <> (FIRST_STRING, SECOND_STRING);
  private static final ICommonsList <String> MIXED_ARRAY = new CommonsArrayList <> ("eins, 1",
                                                                                    "ichi",
                                                                                    FIRST_STRING,
                                                                                    SECOND_STRING);
  private static final ICommonsList <String> ASCII_ARRAY = new CommonsArrayList <> ("foo", "bar");
  private static final String ASCII_STRING_WITH_QUOTES = "\"foo\",\"bar\"";

  @Test
  public void canParseUnicode () throws IOException
  {
    final CSVParser aParser = new CSVParser ();
    final String sSimpleString = COMPOUND_STRING;
    final ICommonsList <String> aItems = aParser.parseLine (sSimpleString);
    assertEquals (2, aItems.size ());
    assertEquals (FIRST_STRING, aItems.get (0));
    assertEquals (SECOND_STRING, aItems.get (1));
    assertEquals (UNICODE_ARRAY, aItems);
  }

  @Test
  public void readerTest () throws IOException
  {
    final NonBlockingBufferedReader aReader = new NonBlockingBufferedReader (new NonBlockingStringReader (FIRST_STRING));
    final String sTestString = aReader.readLine ();
    assertEquals (FIRST_STRING, sTestString);
  }

  @Test
  public void writerTest ()
  {
    final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
    aSW.write (FIRST_STRING);
    assertEquals (FIRST_STRING, aSW.getAsString ());
  }

  @Test
  public void runUniCodeThroughCSVReader () throws IOException
  {
    final CSVReader aReader = new CSVReader (new NonBlockingStringReader (COMPOUND_STRING));
    final ICommonsList <String> aItems = aReader.readNext ();
    assertEquals (2, aItems.size ());
    assertEquals (FIRST_STRING, aItems.get (0));
    assertEquals (SECOND_STRING, aItems.get (1));
    assertEquals (UNICODE_ARRAY, aItems);
  }

  @Test
  public void runUniCodeThroughCSVWriter ()
  {
    final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
    final CSVWriter aWriter = new CSVWriter (aSW);
    aWriter.writeNext (UNICODE_ARRAY);
    assertEquals (COMPOUND_STRING_WITH_QUOTES.trim (), aSW.getAsString ().trim ());
  }

  @Test
  public void runASCIIThroughCSVWriter ()
  {
    final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
    final CSVWriter aWriter = new CSVWriter (aSW);
    aWriter.writeNext (ASCII_ARRAY);
    assertEquals (ASCII_STRING_WITH_QUOTES.trim (), aSW.getAsString ().trim ());
  }

  @Test
  public void writeThenReadAscii () throws IOException
  {
    final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
    final CSVWriter aWriter = new CSVWriter (aSW);
    aWriter.writeNext (ASCII_ARRAY);

    final CSVReader aReader = new CSVReader (new NonBlockingStringReader (aSW.getAsString ()));
    final ICommonsList <String> aItems = aReader.readNext ();
    assertEquals (2, aItems.size ());
    assertEquals (ASCII_ARRAY, aItems);
  }

  @Test
  public void writeThenReadTwiceAscii () throws IOException
  {
    final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
    final CSVWriter aWriter = new CSVWriter (aSW);
    aWriter.writeNext (ASCII_ARRAY);
    aWriter.writeNext (ASCII_ARRAY);

    final CSVReader aReader = new CSVReader (new NonBlockingStringReader (aSW.getAsString ()));
    final ICommonsList <ICommonsList <String>> aLines = aReader.readAll ();
    assertEquals (2, aLines.size ());

    ICommonsList <String> aItems = aLines.get (0);
    assertEquals (2, aItems.size ());
    assertEquals (ASCII_ARRAY, aItems);

    aItems = aLines.get (1);
    assertEquals (2, aItems.size ());
    assertEquals (ASCII_ARRAY, aItems);
  }

  @Test
  public void writeThenReadTwiceUnicode () throws IOException
  {
    final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
    final CSVWriter aWriter = new CSVWriter (aSW);
    aWriter.writeNext (UNICODE_ARRAY);
    aWriter.writeNext (UNICODE_ARRAY);

    final CSVReader aReader = new CSVReader (new NonBlockingStringReader (aSW.getAsString ()));
    final ICommonsList <ICommonsList <String>> aLines = aReader.readAll ();
    assertEquals (2, aLines.size ());

    ICommonsList <String> aItems = aLines.get (0);
    assertEquals (2, aItems.size ());
    assertEquals (UNICODE_ARRAY, aItems);

    aItems = aLines.get (1);
    assertEquals (2, aItems.size ());
    assertEquals (UNICODE_ARRAY, aItems);
  }

  @Test
  public void writeThenReadTwiceMixedUnicode () throws IOException
  {
    final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
    final CSVWriter aWriter = new CSVWriter (aSW);
    aWriter.writeNext (MIXED_ARRAY);
    aWriter.writeNext (MIXED_ARRAY);

    final CSVReader aReader = new CSVReader (new NonBlockingStringReader (aSW.getAsString ()));
    final ICommonsList <ICommonsList <String>> aLines = aReader.readAll ();
    assertEquals (2, aLines.size ());

    ICommonsList <String> aItems = aLines.get (0);
    assertEquals (4, aItems.size ());
    assertEquals (MIXED_ARRAY, aItems);

    aItems = aLines.get (1);
    assertEquals (4, aItems.size ());
    assertEquals (MIXED_ARRAY, aItems);
  }
}
