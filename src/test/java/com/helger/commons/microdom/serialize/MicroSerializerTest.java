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
package com.helger.commons.microdom.serialize;

import static org.junit.Assert.assertTrue;

import javax.annotation.Nonnull;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.io.streams.NonBlockingStringWriter;
import com.helger.commons.microdom.IMicroDocument;
import com.helger.commons.microdom.IMicroElement;
import com.helger.commons.microdom.impl.MicroDocument;
import com.helger.commons.timing.StopWatch;
import com.helger.commons.xml.DefaultXMLIterationHandler;

/**
 * Test class for class {@link MicroSerializer}
 *
 * @author Philip Helger
 */
public final class MicroSerializerTest
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (MicroSerializerTest.class);

  @Nonnull
  private IMicroDocument _createLargeDoc (@Nonnull final IMicroDocument doc,
                                          final boolean bWithText,
                                          final boolean bWithAttrs)
  {
    final IMicroElement aDocElement = doc.appendElement ("root");
    for (int i = 1; i <= 10; ++i)
    {
      final IMicroElement e1 = aDocElement.appendElement ("level1");
      if (bWithAttrs)
      {
        e1.setAttribute ("a1", "Supsi1");
        e1.setAttribute ("a1a", "Supsi1a");
      }
      for (int j = 1; j <= 20; ++j)
      {
        final IMicroElement e2 = e1.appendElement ("level2");
        if (bWithAttrs)
          e2.setAttribute ("a2", "Supsi");
        for (int k = 1; k <= 100; ++k)
        {
          final IMicroElement e3 = e2.appendElement ("level3");
          if (bWithAttrs)
            e3.setAttribute ("a3", "Supsi");
          if (bWithText)
            e3.appendText ("Level 3 text <> " + Double.toString (Math.random ()));
        }
        if (bWithText)
          e2.appendText ("Level 2 text " + Double.toString (Math.random ()));
      }
      if (bWithText)
        e1.appendText ("Level 1 text " + Double.toString (Math.random ()));
    }
    return doc;
  }

  @Test
  public void testLargeTree ()
  {
    final MicroSerializer aMS = new MicroSerializer ();
    final NonBlockingStringWriter aWriter = new NonBlockingStringWriter ();
    final boolean bWithText = false;
    final boolean bWithAttrs = false;
    final IMicroDocument doc = _createLargeDoc (new MicroDocument (), bWithText, bWithAttrs);

    int nMilliSecs = 0;
    int nRun = 0;
    int nWarmUpRuns = 0;
    final StopWatch aSW = new StopWatch ();
    for (; nRun < 200; ++nRun)
    {
      aWriter.reset ();

      /**
       * Current averages:<br>
       * Average: 122.1 millisecs<br>
       * After getNextSibling/getPrevSibling replacement:<br>
       * Average: 80.85 millisecs<br>
       * After changing StringBuilder to Writer:<br>
       * Average: 47.0 millisecs<br>
       */
      if (nRun < 2)
      {
        aMS.write (doc, aWriter);
        nWarmUpRuns++;
      }
      else
      {
        aSW.start ();
        aMS.write (doc, aWriter);
        nMilliSecs += aSW.stopAndGetMillis ();
        aSW.reset ();
      }
      assertTrue (!aWriter.isEmpty ());
    }
    s_aLogger.info ("Average MicroDOM write: " + ((double) nMilliSecs / (nRun - nWarmUpRuns)) + " millisecs");

    // Just do nothing :)
    aMS.write (doc, new DefaultXMLIterationHandler ());
  }
}
