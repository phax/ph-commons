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
package com.helger.commons.mime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.commons.charset.CCharset;
import com.helger.commons.mock.AbstractCommonsTestCase;
import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link MimeType}
 *
 * @author Philip Helger
 */
public final class MimeTypeTest extends AbstractCommonsTestCase
{
  @Test
  public void testCtor ()
  {
    final MimeType mt = new MimeType (EMimeContentType.TEXT, "junit");
    assertSame (EMimeContentType.TEXT, mt.getContentType ());
    assertEquals ("junit", mt.getContentSubType ());
    assertEquals ("text/junit", mt.getAsString ());
    assertEquals ("text/junit", mt.getAsStringWithoutParameters ());

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (mt,
                                                                       new MimeType (EMimeContentType.TEXT, "junit"));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (mt,
                                                                           new MimeType (EMimeContentType.APPLICATION,
                                                                                         "junit"));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (mt,
                                                                           new MimeType (EMimeContentType.TEXT,
                                                                                         "testng"));
    CommonsTestHelper.testGetClone (mt);
    CommonsTestHelper.testDefaultSerialization (mt);

    mt.addParameter ("charset", CCharset.CHARSET_UTF_8);
    CommonsTestHelper.testGetClone (mt);
    CommonsTestHelper.testDefaultSerialization (mt);

    try
    {
      new MimeType (null, "foo");
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      new MimeType (EMimeContentType.APPLICATION, "");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testParameters ()
  {
    final MimeType mt = new MimeType (EMimeContentType.TEXT, "junit");
    assertEquals ("text/junit", mt.getAsString ());
    assertEquals ("text/junit", mt.getAsStringWithoutParameters ());
    assertFalse (mt.hasAnyParameters ());
    assertEquals (0, mt.getParameterCount ());
    assertNotNull (mt.getAllParameters ());
    assertEquals (0, mt.getAllParameters ().size ());
    assertNull (mt.getParameterAtIndex (0));

    mt.addParameter ("charset", "iso-8859-1");
    assertEquals ("text/junit;charset=iso-8859-1", mt.getAsString ());
    assertEquals ("text/junit", mt.getAsStringWithoutParameters ());
    assertTrue (mt.hasAnyParameters ());
    assertEquals (1, mt.getParameterCount ());
    assertNotNull (mt.getAllParameters ());
    assertEquals (1, mt.getAllParameters ().size ());
    assertNotNull (mt.getParameterAtIndex (0));
    assertEquals ("charset", mt.getParameterAtIndex (0).getAttribute ());

    mt.addParameter ("param1", "value");
    assertEquals ("text/junit;charset=iso-8859-1;param1=value", mt.getAsString ());
    assertEquals ("text/junit;charset=iso-8859-1;param1=value", mt.getAsString (EMimeQuoting.QUOTED_STRING));
    assertEquals ("text/junit;charset=iso-8859-1;param1=value", mt.getAsString (EMimeQuoting.QUOTED_PRINTABLE));
    assertEquals ("text/junit;charset=iso-8859-1;param1=value", mt.getAsString (EMimeQuoting.URL_ESCAPE));
    assertEquals ("text/junit", mt.getAsStringWithoutParameters ());
    assertTrue (mt.hasAnyParameters ());
    assertEquals (2, mt.getParameterCount ());
    assertNotNull (mt.getAllParameters ());
    assertEquals (2, mt.getAllParameters ().size ());
    assertEquals ("charset", mt.getParameterAtIndex (0).getAttribute ());
    assertEquals ("param1", mt.getParameterAtIndex (1).getAttribute ());

    mt.addParameter ("param2", "foo bar");
    assertEquals ("text/junit;charset=iso-8859-1;param1=value;param2=\"foo bar\"", mt.getAsString ());
    assertEquals ("text/junit;charset=iso-8859-1;param1=value;param2=\"foo bar\"",
                  mt.getAsString (EMimeQuoting.QUOTED_STRING));
    assertEquals ("text/junit;charset=iso-8859-1;param1=value;param2=foo=20bar",
                  mt.getAsString (EMimeQuoting.QUOTED_PRINTABLE));
    assertEquals ("text/junit;charset=iso-8859-1;param1=value;param2=foo%20bar",
                  mt.getAsString (EMimeQuoting.URL_ESCAPE));
    assertEquals ("text/junit", mt.getAsStringWithoutParameters ());
    assertTrue (mt.hasAnyParameters ());
    assertEquals (3, mt.getParameterCount ());
    assertNotNull (mt.getAllParameters ());
    assertEquals (3, mt.getAllParameters ().size ());
    assertEquals ("charset", mt.getParameterAtIndex (0).getAttribute ());
    assertEquals ("param1", mt.getParameterAtIndex (1).getAttribute ());
    assertEquals ("param2", mt.getParameterAtIndex (2).getAttribute ());

    assertTrue (mt.removeParameter (new MimeTypeParameter ("param1", "value")).isChanged ());
    assertEquals ("text/junit;charset=iso-8859-1;param2=\"foo bar\"", mt.getAsString ());
    assertEquals ("text/junit;charset=iso-8859-1;param2=\"foo bar\"", mt.getAsString (EMimeQuoting.QUOTED_STRING));
    assertEquals ("text/junit;charset=iso-8859-1;param2=foo=20bar", mt.getAsString (EMimeQuoting.QUOTED_PRINTABLE));
    assertEquals ("text/junit;charset=iso-8859-1;param2=foo%20bar", mt.getAsString (EMimeQuoting.URL_ESCAPE));
    assertEquals ("text/junit", mt.getAsStringWithoutParameters ());
    assertTrue (mt.hasAnyParameters ());
    assertEquals (2, mt.getParameterCount ());
    assertNotNull (mt.getAllParameters ());
    assertEquals (2, mt.getAllParameters ().size ());
    assertEquals ("charset", mt.getParameterAtIndex (0).getAttribute ());
    assertEquals ("param2", mt.getParameterAtIndex (1).getAttribute ());

    assertTrue (mt.removeParameterAtIndex (0).isChanged ());
    assertEquals ("text/junit;param2=\"foo bar\"", mt.getAsString ());
    assertEquals ("text/junit;param2=\"foo bar\"", mt.getAsString (EMimeQuoting.QUOTED_STRING));
    assertEquals ("text/junit;param2=foo=20bar", mt.getAsString (EMimeQuoting.QUOTED_PRINTABLE));
    assertEquals ("text/junit;param2=foo%20bar", mt.getAsString (EMimeQuoting.URL_ESCAPE));
    assertEquals ("text/junit", mt.getAsStringWithoutParameters ());
    assertTrue (mt.hasAnyParameters ());
    assertEquals (1, mt.getParameterCount ());
    assertNotNull (mt.getAllParameters ());
    assertEquals (1, mt.getAllParameters ().size ());
    assertEquals ("param2", mt.getParameterAtIndex (0).getAttribute ());

    assertTrue (mt.removeAllParameters ().isChanged ());
    assertEquals ("text/junit", mt.getAsString ());
    assertEquals ("text/junit", mt.getAsString (EMimeQuoting.QUOTED_STRING));
    assertEquals ("text/junit", mt.getAsString (EMimeQuoting.QUOTED_PRINTABLE));
    assertEquals ("text/junit", mt.getAsString (EMimeQuoting.URL_ESCAPE));
    assertEquals ("text/junit", mt.getAsStringWithoutParameters ());
    assertFalse (mt.hasAnyParameters ());
    assertEquals (0, mt.getParameterCount ());
    assertNotNull (mt.getAllParameters ());
    assertEquals (0, mt.getAllParameters ().size ());
    assertNull (mt.getParameterAtIndex (0));
  }

  @Test
  public void testSpecialCharsInParams ()
  {
    MimeType mt = new MimeType (EMimeContentType.TEXT, "junit");
    mt.addParameter ("param2", "foo;bar");
    assertEquals ("text/junit;param2=\"foo;bar\"", mt.getAsString ());
    assertEquals ("text/junit;param2=\"foo;bar\"", mt.getAsString (EMimeQuoting.QUOTED_STRING));
    assertEquals ("text/junit;param2=foo=3Bbar", mt.getAsString (EMimeQuoting.QUOTED_PRINTABLE));
    assertEquals ("text/junit;param2=foo%3Bbar", mt.getAsString (EMimeQuoting.URL_ESCAPE));

    mt = new MimeType (EMimeContentType.TEXT, "junit");
    mt.addParameter ("param2", "foo,bar");
    assertEquals ("text/junit;param2=\"foo,bar\"", mt.getAsString ());
    assertEquals ("text/junit;param2=\"foo,bar\"", mt.getAsString (EMimeQuoting.QUOTED_STRING));
    assertEquals ("text/junit;param2=foo=2Cbar", mt.getAsString (EMimeQuoting.QUOTED_PRINTABLE));
    assertEquals ("text/junit;param2=foo%2Cbar", mt.getAsString (EMimeQuoting.URL_ESCAPE));
  }
}
