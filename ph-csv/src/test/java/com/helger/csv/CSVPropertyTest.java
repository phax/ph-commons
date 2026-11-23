/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.csv;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.helger.base.io.nonblocking.NonBlockingStringReader;
import com.helger.base.io.nonblocking.NonBlockingStringWriter;
import com.helger.unittest.property.Generators;
import com.helger.unittest.property.IGenerator;
import com.helger.unittest.property.PropertyChecker;

/**
 * Property based tests for {@link CSVWriter} and {@link CSVReader}: everything written must be read
 * back unchanged.
 *
 * @author Philip Helger
 */
public final class CSVPropertyTest
{
  /** Separators, quotes, line ends, whitespace and letters */
  private static final IGenerator <String> FIELDS = Generators.oneOf (Generators.strings (",;\"'\\\r\n \tab", 0, 10),
                                                                      Generators.unicodeStrings (0, 10));

  /**
   * At least one field per line, and not just one empty field: CSV cannot distinguish an empty line,
   * a line with one empty field and no line at all.
   */
  private static final IGenerator <List <List <String>>> LINES = Generators.lists (Generators.lists (FIELDS, 1, 6)
                                                                                             .filter (aLine -> aLine.size () > 1 ||
                                                                                                               !aLine.get (0)
                                                                                                                     .isEmpty ()),
                                                                                   0,
                                                                                   6);

  private static List <List <String>> _writeAndRead (final List <List <String>> aLines,
                                                     final char cSeparator,
                                                     final boolean bApplyQuotesToAll) throws Exception
  {
    final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
    try (final CSVWriter aWriter = new CSVWriter (aSW))
    {
      aWriter.setSeparatorChar (cSeparator);
      aWriter.writeAll (aLines, bApplyQuotesToAll);
    }
    try (final CSVReader aReader = new CSVReader (new NonBlockingStringReader (aSW.getAsString ()), true))
    {
      aReader.setSeparatorChar (cSeparator);
      return (List) aReader.readAll ();
    }
  }

  @Test
  public void testWriteReadRoundTrip ()
  {
    new PropertyChecker ().setTries (1000)
                          .forAll (LINES,
                                   Generators.of (Character.valueOf (','), Character.valueOf (';'), Character.valueOf ('\t')),
                                   Generators.booleans (),
                                   (aLines, aSeparator, aApplyQuotesToAll) -> assertEquals (aLines,
                                                                                            _writeAndRead (aLines,
                                                                                                           aSeparator.charValue (),
                                                                                                           aApplyQuotesToAll.booleanValue ())));
  }
}
