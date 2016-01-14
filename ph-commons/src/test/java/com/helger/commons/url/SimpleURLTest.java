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
package com.helger.commons.url;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link SimpleURL}.
 *
 * @author Philip Helger
 */
public final class SimpleURLTest
{
  private static void _checkAsString (final String sHref)
  {
    assertEquals (sHref, new SimpleURL (sHref).getAsString ());
  }

  @Test
  public void testAsString ()
  {
    _checkAsString ("http://www.helger.com");
    _checkAsString ("http://www.helger.com/directory");
    _checkAsString ("http://www.helger.com/#anchor");
    _checkAsString ("http://www.helger.com/?x=y");
    _checkAsString ("http://www.helger.com/?x=y#anchor");
    _checkAsString ("http://www.helger.com/?x=y&ab=cd");
    _checkAsString ("/?x=y&ab=cd");
    _checkAsString ("http://www.helger.com/?this&that&thatalso");
    _checkAsString ("?this&that&thatalso");
    _checkAsString ("http://www.helger.com/?upper=LOWER&äöü=aou");
    _checkAsString ("http://www.helger.com/?upper=LOWER&äöü=aou#anchor");
    _checkAsString ("?upper=LOWER&äöü=aou");

    // asString results in slightly different but semantically equivalent URLs
    assertEquals ("http://www.helger.com/", new SimpleURL ("http://www.helger.com/?").getAsString ());
    assertEquals ("http://www.helger.com/#anchor", new SimpleURL ("http://www.helger.com/?#anchor").getAsString ());
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
    assertEquals ("", aURL.getAsString ());

    aURL = new SimpleURL ("");
    assertEquals ("", aURL.getAsString ());

    aURL = new SimpleURL ("#");
    assertEquals ("", aURL.getAsString ());

    aURL = new SimpleURL ("?");
    assertEquals ("", aURL.getAsString ());

    aURL = new SimpleURL ("?#");
    assertEquals ("", aURL.getAsString ());

    aURL = new SimpleURL ("  ?  #  ");
    assertEquals ("", aURL.getAsString ());

    aURL = new SimpleURL ("http://www.helger.com");
    assertEquals ("http://www.helger.com", aURL.getAsString ());

    // params
    // 1. default
    aURL = new SimpleURL ("http://www.helger.com", new SMap ("a", "b"));
    assertEquals ("http://www.helger.com?a=b", aURL.getAsString ());
    // 2. plus params in href
    aURL = new SimpleURL ("http://www.helger.com?x=y", new SMap ("a", "b"));
    assertEquals ("http://www.helger.com?x=y&a=b", aURL.getAsString ());
    // 3. overwrite parameter in href
    aURL = new SimpleURL ("http://www.helger.com?a=a", new SMap ("a", "b"));
    assertEquals ("http://www.helger.com?a=b", aURL.getAsString ());
    // 4. only params
    aURL = new SimpleURL ("", new SMap ("a", "b"));
    assertEquals ("?a=b", aURL.getAsString ());
    // 4a. only params
    aURL = new SimpleURL ("?", new SMap ("a", "b"));
    assertEquals ("?a=b", aURL.getAsString ());
    // 4b. only params
    aURL = new SimpleURL ("#", new SMap ("a", "b"));
    assertEquals ("?a=b", aURL.getAsString ());

    // anchor
    // 1. default
    aURL = new SimpleURL ("http://www.helger.com", new SMap ("a", "b"), "root");
    assertEquals ("http://www.helger.com?a=b#root", aURL.getAsString ());
    // 2. overwrite anchor
    aURL = new SimpleURL ("http://www.helger.com#main", new SMap ("a", "b"), "root");
    assertEquals ("http://www.helger.com?a=b#root", aURL.getAsString ());
    // 3. only anchor in href
    aURL = new SimpleURL ("http://www.helger.com#main", new SMap ("a", "b"));
    assertEquals ("http://www.helger.com?a=b#main", aURL.getAsString ());
    // 4. only params and anchor
    aURL = new SimpleURL ("#main", new SMap ("a", "b"));
    assertEquals ("?a=b#main", aURL.getAsString ());
    // 5. only anchor
    aURL = new SimpleURL ("#main");
    assertEquals ("#main", aURL.getAsString ());
    // 5a. only anchor
    aURL = new SimpleURL ("", null, "main");
    assertEquals ("#main", aURL.getAsString ());

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
    assertEquals (sURL, aURL.getAsString ());
    assertEquals (0, aURL.getParamCount ());
  }
}
