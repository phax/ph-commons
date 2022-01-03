/*
 * Copyright (C) 2014-2022 Philip Helger (www.helger.com)
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
package com.helger.json;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.io.file.FileHelper;
import com.helger.commons.io.misc.SizeHelper;
import com.helger.commons.io.stream.CountingReader;
import com.helger.commons.timing.StopWatch;
import com.helger.json.parser.JsonParseException;
import com.helger.json.parser.JsonParser;
import com.helger.json.parser.handler.CollectingJsonParserHandler;
import com.helger.json.parser.handler.DoNothingJsonParserHandler;

public class JsonProfileTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (JsonProfileTest.class);

  public static void main (final String [] args) throws IOException
  {
    final File f = new File ("\\dev\\svn-philip\\dev\\peana-data\\all2019v3\\serviceregs.json");
    if (f.exists ())
    {
      final StopWatch aSW = StopWatch.createdStarted ();
      final SizeHelper aSH = SizeHelper.getSizeHelperOfLocale (Locale.GERMANY);
      try (final CountingReader aReader = new CountingReader (FileHelper.getBufferedReader (f, StandardCharsets.UTF_8)))
      {
        int c = 0;
        while (true)
        {
          final JsonParser aParser = new JsonParser (aReader,
                                                     false ? new CollectingJsonParserHandler () : new DoNothingJsonParserHandler ());
          aParser.setCheckForEOI (false);
          try
          {
            if (aParser.parse ().isEndOfInput ())
              break;
          }
          catch (final JsonParseException ex)
          {
            break;
          }

          ++c;
          if ((c % 10_000) == 0)
            LOGGER.info ("Parsed already " +
                         c +
                         " JSONs; read chars: " +
                         aSH.getAsMatching (aReader.getCharsRead (), 2) +
                         " = " +
                         ((double) aReader.getCharsRead () / c) +
                         " chars/object");
        }
        aSW.stop ();
        LOGGER.info ("Read " + c + " objects in " + aSW.getMillis () + "ms");
      }
    }
  }
}
