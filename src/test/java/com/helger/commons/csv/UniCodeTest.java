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
package com.helger.commons.csv;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import org.junit.Test;

import com.helger.commons.collections.CollectionHelper;

public final class UniCodeTest
{
  private static final String COMPOUND_STRING = "ä,ö";
  private static final String COMPOUND_STRING_WITH_QUOTES = "\"ä\",\"ö\"";
  private static final String FIRST_STRING = "ä";
  private static final String SECOND_STRING = "ö";
  private static final List <String> UNICODE_ARRAY = CollectionHelper.newList (FIRST_STRING, SECOND_STRING);
  private static final List <String> MIXED_ARRAY = CollectionHelper.newList ("eins, 1",
                                                                             "ichi",
                                                                             FIRST_STRING,
                                                                             SECOND_STRING);
  private static final List <String> ASCII_ARRAY = CollectionHelper.newList ("foo", "bar");
  private static final String ASCII_STRING_WITH_QUOTES = "\"foo\",\"bar\"";

  private CSVParser csvParser;

  @Test
  public void canParseUnicode () throws IOException
  {
    csvParser = new CSVParser ();
    final String simpleString = COMPOUND_STRING;
    final List <String> items = csvParser.parseLine (simpleString);
    assertEquals (2, items.size ());
    assertEquals (FIRST_STRING, items.get (0));
    assertEquals (SECOND_STRING, items.get (1));
    assertEquals (UNICODE_ARRAY, items);
  }

  @Test
  public void readerTest () throws IOException
  {
    final BufferedReader reader = new BufferedReader (new StringReader (FIRST_STRING));
    final String testString = reader.readLine ();
    assertEquals (FIRST_STRING, testString);
  }

  @Test
  public void writerTest ()
  {
    final StringWriter sw = new StringWriter ();
    sw.write (FIRST_STRING);
    assertEquals (FIRST_STRING, sw.toString ());
  }

  @Test
  public void runUniCodeThroughCSVReader () throws IOException
  {
    final CSVReader reader = new CSVReader (new StringReader (COMPOUND_STRING));
    final List <String> items = reader.readNext ();
    assertEquals (2, items.size ());
    assertEquals (FIRST_STRING, items.get (0));
    assertEquals (SECOND_STRING, items.get (1));
    assertEquals (UNICODE_ARRAY, items);
  }

  @Test
  public void runUniCodeThroughCSVWriter ()
  {
    final StringWriter sw = new StringWriter ();
    final CSVWriter writer = new CSVWriter (sw);
    writer.writeNext (UNICODE_ARRAY);
    assertEquals (COMPOUND_STRING_WITH_QUOTES.trim (), sw.toString ().trim ());
  }

  @Test
  public void runASCIIThroughCSVWriter ()
  {
    final StringWriter sw = new StringWriter ();
    final CSVWriter writer = new CSVWriter (sw);
    writer.writeNext (ASCII_ARRAY);
    assertEquals (ASCII_STRING_WITH_QUOTES.trim (), sw.toString ().trim ());
  }

  @Test
  public void writeThenReadAscii () throws IOException
  {
    final StringWriter sw = new StringWriter ();
    final CSVWriter writer = new CSVWriter (sw);
    writer.writeNext (ASCII_ARRAY);

    final CSVReader reader = new CSVReader (new StringReader (sw.toString ()));
    final List <String> items = reader.readNext ();
    assertEquals (2, items.size ());
    assertEquals (ASCII_ARRAY, items);
  }

  @Test
  public void writeThenReadTwiceAscii () throws IOException
  {
    final StringWriter sw = new StringWriter ();
    final CSVWriter writer = new CSVWriter (sw);
    writer.writeNext (ASCII_ARRAY);
    writer.writeNext (ASCII_ARRAY);

    final CSVReader reader = new CSVReader (new StringReader (sw.toString ()));
    final List <List <String>> lines = reader.readAll ();
    assertEquals (2, lines.size ());

    List <String> items = lines.get (0);
    assertEquals (2, items.size ());
    assertEquals (ASCII_ARRAY, items);

    items = lines.get (1);
    assertEquals (2, items.size ());
    assertEquals (ASCII_ARRAY, items);
  }

  @Test
  public void writeThenReadTwiceUnicode () throws IOException
  {
    final StringWriter sw = new StringWriter ();
    final CSVWriter writer = new CSVWriter (sw);
    writer.writeNext (UNICODE_ARRAY);
    writer.writeNext (UNICODE_ARRAY);

    final CSVReader reader = new CSVReader (new StringReader (sw.toString ()));
    final List <List <String>> lines = reader.readAll ();
    assertEquals (2, lines.size ());

    List <String> items = lines.get (0);
    assertEquals (2, items.size ());
    assertEquals (UNICODE_ARRAY, items);

    items = lines.get (1);
    assertEquals (2, items.size ());
    assertEquals (UNICODE_ARRAY, items);
  }

  @Test
  public void writeThenReadTwiceMixedUnicode () throws IOException
  {
    final StringWriter sw = new StringWriter ();
    final CSVWriter writer = new CSVWriter (sw);
    writer.writeNext (MIXED_ARRAY);
    writer.writeNext (MIXED_ARRAY);

    final CSVReader reader = new CSVReader (new StringReader (sw.toString ()));
    final List <List <String>> lines = reader.readAll ();
    assertEquals (2, lines.size ());

    List <String> items = lines.get (0);
    assertEquals (4, items.size ());
    assertEquals (MIXED_ARRAY, items);

    items = lines.get (1);
    assertEquals (4, items.size ());
    assertEquals (MIXED_ARRAY, items);
  }
}
