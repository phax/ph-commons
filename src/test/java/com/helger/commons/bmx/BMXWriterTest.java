/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.commons.bmx;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Random;

import javax.annotation.Nonnull;

import org.junit.Ignore;
import org.junit.Test;

import com.helger.commons.microdom.IMicroDocument;
import com.helger.commons.microdom.IMicroElement;
import com.helger.commons.microdom.IMicroNode;
import com.helger.commons.microdom.impl.MicroDocument;
import com.helger.commons.microdom.serialize.MicroReader;
import com.helger.commons.microdom.serialize.MicroWriter;
import com.helger.commons.random.VerySecureRandom;
import com.helger.commons.timing.StopWatch;

/**
 * Test class for class {@link BMXWriter}
 *
 * @author Philip Helger
 */
public final class BMXWriterTest
{
  @Test
  public void testBasic ()
  {
    final IMicroDocument aDoc = MicroReader.readMicroXML (new File ("pom.xml"));
    assertNotNull (aDoc);

    final File aFile1 = new File ("target/junittest", "test.bmx");
    new BMXWriter ().writeToFile (aDoc, aFile1);

    IMicroNode aNode = BMXReader.readFromFile (aFile1);
    assertNotNull (aNode);
    assertTrue (aDoc.isEqualContent (aNode));

    final File aFile2 = new File ("target/junittest", "test.nost.bmx");
    new BMXWriter (BMXSettings.createDefault ().set (EBMXSetting.NO_STRINGTABLE)).writeToFile (aDoc, aFile2);

    aNode = BMXReader.readFromFile (aFile2);
    assertNotNull (aNode);
    assertTrue (aDoc.isEqualContent (aNode));
  }

  @Nonnull
  private IMicroDocument _createLargeDoc ()
  {
    final Random aRandom = VerySecureRandom.getInstance ();
    final IMicroDocument aDoc = new MicroDocument ();
    final IMicroElement eRoot = aDoc.appendElement ("root");
    for (int i = 0; i < 100; ++i)
    {
      final IMicroElement eChild1 = eRoot.appendElement ("element" + (i / 3));
      for (int j = 0; j < 100; ++j)
      {
        final IMicroElement eChild2 = eChild1.appendElement ("element" + (j / 3));
        for (int j2 = 0; j2 < 26; ++j2)
        {
          final IMicroElement eChild3 = eChild2.appendElement ("element" + (j2 / 2));
          for (int k = 0; k < 5; ++k)
          {
            final String sElementName = "element" + i + j + k;
            final IMicroElement eChild4 = eChild3.appendElement (sElementName);
            if ((k & 1) == 1)
            {
              eChild4.setAttribute ("k", k);
              final char [] aChar = new char [30 + aRandom.nextInt (20)];
              for (int x = 0; x < aChar.length; ++x)
                aChar[x] = (char) ('a' + aRandom.nextInt (7));
              eChild4.appendText (aChar, 0, aChar.length);
            }
            else
              eChild4.setAttribute ("iandj", i + j);
          }
        }
      }
    }
    return aDoc;
  }

  @Ignore
  @Test
  public void testStandardXML ()
  {
    final StopWatch aSW = new StopWatch ();
    final File aFile0 = new File ("target/junittest", "standard.xml");
    final File aFile1 = new File ("target/junittest", "standard.bmx");
    final File aFile2 = new File ("target/junittest", "standard2.bmx");
    final int nMax = 10;
    final IMicroDocument aDoc = _createLargeDoc ();
    System.out.println ("Created test document");

    for (int i = 0; i < nMax; ++i)
    {
      aSW.restart ();
      MicroWriter.writeToFile (aDoc, aFile0);
      System.out.println ("[" + i + "] Writing via MicroWriter took " + aSW.stopAndGetMillis () + "ms");
    }

    for (int i = 0; i < nMax; ++i)
    {
      aSW.restart ();
      final IMicroNode aDoc2 = MicroReader.readMicroXML (aFile0);
      System.out.println ("[" + i + "] Reading via MicroReader took " + aSW.stopAndGetMillis () + "ms");
      assertNotNull (aDoc2);
    }

    for (int i = 0; i < nMax; ++i)
    {
      aSW.restart ();
      final BMXWriter aWriter = new BMXWriter (BMXSettings.createDefault ());
      aWriter.writeToFile (aDoc, aFile1);
      System.out.println ("[" + i + "] Writing BMX took " + aSW.stopAndGetMillis () + "ms");
    }

    for (int i = 0; i < nMax; ++i)
    {
      aSW.restart ();
      final IMicroNode aNode = BMXReader.readFromFile (aFile1);
      assertNotNull (aNode);
      System.out.println ("[" + i + "] Reading BMX took " + aSW.stopAndGetMillis () + "ms");
      assertTrue (aDoc.isEqualContent (aNode));
    }

    for (int i = 0; i < nMax; ++i)
    {
      aSW.restart ();
      final BMXWriter aWriter = new BMXWriter (BMXSettings.createDefault ().set (EBMXSetting.NO_STRINGTABLE));
      aWriter.writeToFile (aDoc, aFile2);
      System.out.println ("[" + i + "] Writing BMX without ST took " + aSW.stopAndGetMillis () + "ms");
    }

    for (int i = 0; i < nMax; ++i)
    {
      aSW.restart ();
      final IMicroNode aNode = BMXReader.readFromFile (aFile2);
      assertNotNull (aNode);
      System.out.println ("[" + i + "] Reading BMX without ST took " + aSW.stopAndGetMillis () + "ms");
      assertTrue (aDoc.isEqualContent (aNode));
    }
  }
}
