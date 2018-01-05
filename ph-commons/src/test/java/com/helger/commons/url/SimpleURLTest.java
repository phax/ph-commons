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
package com.helger.commons.url;

import static org.junit.Assert.assertEquals;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.helger.commons.collection.attr.StringMap;
import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link SimpleURL}.
 *
 * @author Philip Helger
 */
public final class SimpleURLTest
{
  private static void _checkAsStringNoEncode (final String sHref)
  {
    assertEquals (sHref, new SimpleURL (sHref).getAsStringWithoutEncodedParameters ());
  }

  @Test
  public void testGetAsStringWithoutEncodedParameters ()
  {
    _checkAsStringNoEncode ("http://www.helger.com");
    _checkAsStringNoEncode ("http://www.helger.com/directory");
    _checkAsStringNoEncode ("http://www.helger.com/#anchor");
    _checkAsStringNoEncode ("http://www.helger.com/?x=y");
    _checkAsStringNoEncode ("http://www.helger.com/?x=y#anchor");
    _checkAsStringNoEncode ("http://www.helger.com/?x=y&ab=cd");
    _checkAsStringNoEncode ("/?x=y&ab=cd");
    _checkAsStringNoEncode ("http://www.helger.com/?this&that&thatalso");
    _checkAsStringNoEncode ("?this&that&thatalso");
    _checkAsStringNoEncode ("http://www.helger.com/?upper=LOWER&äöü=aou");
    _checkAsStringNoEncode ("http://www.helger.com/?upper=LOWER&äöü=aou#anchor");
    _checkAsStringNoEncode ("?upper=LOWER&äöü=aou");

    // asString results in slightly different but semantically equivalent URLs
    assertEquals ("http://www.helger.com/",
                  new SimpleURL ("http://www.helger.com/?").getAsStringWithoutEncodedParameters ());
    assertEquals ("http://www.helger.com/#anchor",
                  new SimpleURL ("http://www.helger.com/?#anchor").getAsStringWithoutEncodedParameters ());
  }

  private static void _checkAsStringEncodeDefault (final String sHref)
  {
    assertEquals (sHref, new SimpleURL (sHref).getAsStringWithEncodedParameters ());
  }

  @Test
  public void testGetAsStringWithEncodedParametersDefault ()
  {
    _checkAsStringEncodeDefault ("http://www.helger.com");
    _checkAsStringEncodeDefault ("http://www.helger.com/directory");
    _checkAsStringEncodeDefault ("http://www.helger.com/#anchor");
    _checkAsStringEncodeDefault ("http://www.helger.com/?x=y");
    _checkAsStringEncodeDefault ("http://www.helger.com/?x=y#anchor");
    _checkAsStringEncodeDefault ("http://www.helger.com/?x=y&ab=cd");
    _checkAsStringEncodeDefault ("/?x=y&ab=cd");
    _checkAsStringEncodeDefault ("http://www.helger.com/?this&that&thatalso");
    _checkAsStringEncodeDefault ("?this&that&thatalso");
    _checkAsStringEncodeDefault ("http://www.helger.com/?upper=LOWER&%C3%A4%C3%B6%C3%BC=aou&%C3%A4=a");
    _checkAsStringEncodeDefault ("http://www.helger.com/?upper=LOWER&%C3%A4%C3%B6%C3%BC=aou#anchor");
    _checkAsStringEncodeDefault ("?upper=LOWER&%C3%A4%C3%B6%C3%BC=aou");
  }

  private static void _checkAsStringEncodeISO88591 (final String sHref)
  {
    assertEquals (sHref,
                  new SimpleURL (sHref,
                                 StandardCharsets.ISO_8859_1).getAsStringWithEncodedParameters (StandardCharsets.ISO_8859_1));
  }

  @Test
  public void testGetAsStringWithEncodedParametersISO88591 ()
  {
    _checkAsStringEncodeISO88591 ("http://www.helger.com");
    _checkAsStringEncodeISO88591 ("http://www.helger.com/directory");
    _checkAsStringEncodeISO88591 ("http://www.helger.com/#anchor");
    _checkAsStringEncodeISO88591 ("http://www.helger.com/?x=y");
    _checkAsStringEncodeISO88591 ("http://www.helger.com/?x=y#anchor");
    _checkAsStringEncodeISO88591 ("http://www.helger.com/?x=y&ab=cd");
    _checkAsStringEncodeISO88591 ("/?x=y&ab=cd");
    _checkAsStringEncodeISO88591 ("http://www.helger.com/?this&that&thatalso");
    _checkAsStringEncodeISO88591 ("?this&that&thatalso");
    _checkAsStringEncodeISO88591 ("http://www.helger.com/?upper=LOWER&%E4%F6%FC=aou&%E4=a");
    _checkAsStringEncodeISO88591 ("http://www.helger.com/?upper=LOWER&%E4%F6%FC=aou#anchor");
    _checkAsStringEncodeISO88591 ("?upper=LOWER&%E4%F6%FC=aou");
  }

  private static void _checkAsEncodedString (final String sHref)
  {
    _checkAsEncodedString (sHref, sHref);
  }

  private static void _checkAsEncodedString (final String sHref, final String sEncodedHref)
  {
    assertEquals (sEncodedHref, new SimpleURL (sHref).getAsStringWithEncodedParameters ());
  }

  @Test
  public void testAsEncodedString ()
  {
    _checkAsEncodedString ("http://www.helger.com");
    _checkAsEncodedString ("http://www.helger.com/directory");
    _checkAsEncodedString ("http://www.helger.com/#anchor");
    _checkAsEncodedString ("http://www.helger.com/?#anchor", "http://www.helger.com/#anchor");
    _checkAsEncodedString ("http://www.helger.com/?x=y");
    _checkAsEncodedString ("http://www.helger.com/?x=y#anchor");
    _checkAsEncodedString ("http://www.helger.com/?x=y&ab=cd");
    _checkAsEncodedString ("/?x=y&ab=cd");
    _checkAsEncodedString ("http://www.helger.com/?this&that&thatalso");
    _checkAsEncodedString ("?this&that&thatalso");
    _checkAsEncodedString ("http://www.helger.com/?upper=LOWER&äöü=aou",
                           "http://www.helger.com/?upper=LOWER&%C3%A4%C3%B6%C3%BC=aou");
    _checkAsEncodedString ("http://www.helger.com/?upper=LOWER&äöü=aou#anchor",
                           "http://www.helger.com/?upper=LOWER&%C3%A4%C3%B6%C3%BC=aou#anchor");
    _checkAsEncodedString ("?upper=LOWER&äöü=aou", "?upper=LOWER&%C3%A4%C3%B6%C3%BC=aou");

    // asString results in slightly different but semantically equivalent URLs
    assertEquals ("http://www.helger.com/",
                  new SimpleURL ("http://www.helger.com/?").getAsStringWithEncodedParameters ());
  }

  @Test
  public void testCtor ()
  {
    // only href
    SimpleURL aURL = new SimpleURL ();
    assertEquals ("", aURL.getAsStringWithEncodedParameters ());

    aURL = new SimpleURL ("");
    assertEquals ("", aURL.getAsStringWithEncodedParameters ());

    aURL = new SimpleURL ("#");
    assertEquals ("", aURL.getAsStringWithEncodedParameters ());

    aURL = new SimpleURL ("?");
    assertEquals ("", aURL.getAsStringWithEncodedParameters ());

    aURL = new SimpleURL ("?#");
    assertEquals ("", aURL.getAsStringWithEncodedParameters ());

    aURL = new SimpleURL ("  ?  #  ");
    assertEquals ("", aURL.getAsStringWithEncodedParameters ());

    aURL = new SimpleURL ("http://www.helger.com");
    assertEquals ("http://www.helger.com", aURL.getAsStringWithEncodedParameters ());

    // params
    // 1. default
    aURL = new SimpleURL ("http://www.helger.com", new StringMap ("a", "b"));
    assertEquals ("http://www.helger.com?a=b", aURL.getAsStringWithEncodedParameters ());
    // 2. plus params in href
    aURL = new SimpleURL ("http://www.helger.com?x=y", new StringMap ("a", "b"));
    assertEquals ("http://www.helger.com?x=y&a=b", aURL.getAsStringWithEncodedParameters ());
    // 3. add parameter with same name in href
    aURL = new SimpleURL ("http://www.helger.com?a=a", new StringMap ("a", "b"));
    assertEquals ("http://www.helger.com?a=a&a=b", aURL.getAsStringWithEncodedParameters ());
    // 4. only params
    aURL = new SimpleURL ("", new StringMap ("a", "b"));
    assertEquals ("?a=b", aURL.getAsStringWithEncodedParameters ());
    // 4a. only params
    aURL = new SimpleURL ("?", new StringMap ("a", "b"));
    assertEquals ("?a=b", aURL.getAsStringWithEncodedParameters ());
    // 4b. only params
    aURL = new SimpleURL ("#", new StringMap ("a", "b"));
    assertEquals ("?a=b", aURL.getAsStringWithEncodedParameters ());
    assertEquals ("?a=b", aURL.getAsStringWithEncodedParameters ());
    // 4c. only params
    aURL = new SimpleURL ("#", new StringMap ().add ("a", null));
    assertEquals ("?a", aURL.getAsStringWithEncodedParameters ());
    // 4d. only params
    aURL = new SimpleURL ("#", new StringMap ().add ("a", ""));
    assertEquals ("?a", aURL.getAsStringWithEncodedParameters ());
    // 4e. only params
    aURL = new SimpleURL ("#").add ("a");
    assertEquals ("?a", aURL.getAsStringWithEncodedParameters ());
    // 4f. only params
    aURL = new SimpleURL ("#").add ("a", (String) null);
    assertEquals ("?a", aURL.getAsStringWithEncodedParameters ());

    // anchor
    // 1. default
    aURL = new SimpleURL ("http://www.helger.com", new StringMap ("a", "b"), "root");
    assertEquals ("http://www.helger.com?a=b#root", aURL.getAsStringWithEncodedParameters ());
    // 2. overwrite anchor
    aURL = new SimpleURL ("http://www.helger.com#main", new StringMap ("a", "b"), "root");
    assertEquals ("http://www.helger.com?a=b#root", aURL.getAsStringWithEncodedParameters ());
    // 3. only anchor in href
    aURL = new SimpleURL ("http://www.helger.com#main", new StringMap ("a", "b"));
    assertEquals ("http://www.helger.com?a=b#main", aURL.getAsStringWithEncodedParameters ());
    // 4. only params and anchor
    aURL = new SimpleURL ("#main", new StringMap ("a", "b"));
    assertEquals ("?a=b#main", aURL.getAsStringWithEncodedParameters ());
    // 5. only anchor
    aURL = new SimpleURL ("#main");
    assertEquals ("#main", aURL.getAsStringWithEncodedParameters ());
    // 5a. only anchor
    aURL = new SimpleURL ("", (Map <String, String>) null, "main");
    assertEquals ("#main", aURL.getAsStringWithEncodedParameters ());
    // 5b. only anchor
    aURL = new SimpleURL ("", (List <URLParameter>) null, "main");
    assertEquals ("#main", aURL.getAsStringWithEncodedParameters ());

    // Copy ctor
    final ISimpleURL aURL2 = new SimpleURL (aURL);
    assertEquals (aURL, aURL2);

    CommonsTestHelper.testGetClone (aURL);
  }

  @Test
  public void testDataUrls ()
  {
    final String sURL = "data:image/gif;base64,R0lGODlhEAAOALMAAOazToeHh0tLS/7LZv/0jvb29t/f3//Ub//ge8WSLf/rhf/3kdbW1mxsbP//mf///yH5BAAAAAAALAAAAAAQAA4AAARe8L1Ekyky67QZ1hLnjM5UUde0ECwLJoExKcppV0aCcGCmTIHEIUEqjgaORCMxIC6e0CcguWw6aFjsVMkkIr7g77ZKPJjPZqIyd7sJAgVGoEGv2xsBxqNgYPj/gAwXEQA7";
    final SimpleURL aURL = new SimpleURL (sURL);
    assertEquals (sURL, aURL.getAsStringWithEncodedParameters ());
    assertEquals (0, aURL.params ().size ());
  }
}
