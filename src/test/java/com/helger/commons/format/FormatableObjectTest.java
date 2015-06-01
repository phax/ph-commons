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
package com.helger.commons.format;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.helger.commons.mock.CommonsTestUtils;

/**
 * Test class for class {@link FormatableObject}.
 * 
 * @author Philip Helger
 */
public final class FormatableObjectTest
{
  @Test
  public void testAll ()
  {
    FormatableObject aFO = new FormatableObject ("Any", new FormatterBracket ());
    assertEquals ("Any", aFO.getValue ());
    assertEquals (FormatterBracket.class, aFO.getFormatter ().getClass ());
    assertEquals ("[Any]", aFO.getAsString ());
    CommonsTestUtils.testToStringImplementation (aFO);

    aFO = new FormatableObject ("Any", new FormatterBracket (new FormatterBracket ()));
    assertEquals ("Any", aFO.getValue ());
    assertEquals (FormatterBracket.class, aFO.getFormatter ().getClass ());
    assertEquals ("[[Any]]", aFO.getAsString ());
    CommonsTestUtils.testToStringImplementation (aFO);

    aFO = new FormatableObject ("Any", new IFormatterProvider ()
    {
      public IFormatter getFormatter ()
      {
        return new FormatterBracket ();
      }
    });
    assertEquals ("Any", aFO.getValue ());
    assertEquals (FormatterBracket.class, aFO.getFormatter ().getClass ());
    assertEquals ("[Any]", aFO.getAsString ());
    CommonsTestUtils.testToStringImplementation (aFO);

    aFO = new FormatableObject (null, new FormatterBracket (new FormatterBracket ()));
    assertNull (aFO.getValue ());
    assertEquals (FormatterBracket.class, aFO.getFormatter ().getClass ());
    assertEquals ("[[]]", aFO.getAsString ());
    CommonsTestUtils.testToStringImplementation (aFO);

    aFO = new FormatableObject ("Any", new FormatterStringPrefix ("x "));
    assertEquals ("Any", aFO.getValue ());
    assertEquals (FormatterStringPrefix.class, aFO.getFormatter ().getClass ());
    assertEquals ("x Any", aFO.getAsString ());
    CommonsTestUtils.testToStringImplementation (aFO);

    aFO = new FormatableObject ("Any", new FormatterStringPrefix (new FormatterBracket (), "x "));
    assertEquals ("Any", aFO.getValue ());
    assertEquals (FormatterStringPrefix.class, aFO.getFormatter ().getClass ());
    assertEquals ("x [Any]", aFO.getAsString ());
    CommonsTestUtils.testToStringImplementation (aFO);

    aFO = new FormatableObject ("Any", new FormatterStringSuffix (" y"));
    assertEquals ("Any", aFO.getValue ());
    assertEquals (FormatterStringSuffix.class, aFO.getFormatter ().getClass ());
    assertEquals ("Any y", aFO.getAsString ());
    CommonsTestUtils.testToStringImplementation (aFO);

    aFO = new FormatableObject ("Any", new FormatterStringSuffix (new FormatterBracket (), " y"));
    assertEquals ("Any", aFO.getValue ());
    assertEquals (FormatterStringSuffix.class, aFO.getFormatter ().getClass ());
    assertEquals ("[Any] y", aFO.getAsString ());
    CommonsTestUtils.testToStringImplementation (aFO);

    aFO = new FormatableObject ("Any", new FormatterMinLengthAddLeading (10, '@'));
    assertEquals ("Any", aFO.getValue ());
    assertEquals (FormatterMinLengthAddLeading.class, aFO.getFormatter ().getClass ());
    assertEquals ("@@@@@@@Any", aFO.getAsString ());
    CommonsTestUtils.testToStringImplementation (aFO);

    aFO = new FormatableObject ("Any", new FormatterMinLengthAddLeading (new FormatterBracket (), 10, '@'));
    assertEquals ("Any", aFO.getValue ());
    assertEquals (FormatterMinLengthAddLeading.class, aFO.getFormatter ().getClass ());
    assertEquals ("@@@@@[Any]", aFO.getAsString ());
    CommonsTestUtils.testToStringImplementation (aFO);

    aFO = new FormatableObject ("Any", new FormatterMinLengthAddTrailing (10, '@'));
    assertEquals ("Any", aFO.getValue ());
    assertEquals (FormatterMinLengthAddTrailing.class, aFO.getFormatter ().getClass ());
    assertEquals ("Any@@@@@@@", aFO.getAsString ());
    CommonsTestUtils.testToStringImplementation (aFO);

    aFO = new FormatableObject ("Any", new FormatterMinLengthAddTrailing (new FormatterBracket (), 10, '@'));
    assertEquals ("Any", aFO.getValue ());
    assertEquals (FormatterMinLengthAddTrailing.class, aFO.getFormatter ().getClass ());
    assertEquals ("[Any]@@@@@", aFO.getAsString ());
    CommonsTestUtils.testToStringImplementation (aFO);
  }

  @Test
  public void testImpl ()
  {
    CommonsTestUtils.testDefaultImplementationWithEqualContentObject (new FormatableObject ("Any", new FormatterBracket ()),
                                                                 new FormatableObject ("Any", new FormatterBracket ()));
    CommonsTestUtils.testDefaultImplementationWithDifferentContentObject (new FormatableObject ("Any",
                                                                                          new FormatterBracket ()),
                                                                     new FormatableObject ("Any2",
                                                                                          new FormatterBracket ()));
    CommonsTestUtils.testDefaultImplementationWithDifferentContentObject (new FormatableObject ("Any",
                                                                                          new FormatterBracket ()),
                                                                     new FormatableObject ("Any",
                                                                                          new FormatterStringPrefix ("oprefix")));

    CommonsTestUtils.testDefaultImplementationWithEqualContentObject (new FormatterBracket (), new FormatterBracket ());

    CommonsTestUtils.testDefaultImplementationWithEqualContentObject (new FormatterMinLengthAddLeading (10, ' '),
                                                                 new FormatterMinLengthAddLeading (10, ' '));
    CommonsTestUtils.testDefaultImplementationWithDifferentContentObject (new FormatterMinLengthAddLeading (10, ' '),
                                                                     new FormatterMinLengthAddLeading (10, 'x'));
    CommonsTestUtils.testDefaultImplementationWithDifferentContentObject (new FormatterMinLengthAddLeading (10, ' '),
                                                                     new FormatterMinLengthAddLeading (5, ' '));

    CommonsTestUtils.testDefaultImplementationWithEqualContentObject (new FormatterMinLengthAddTrailing (10, ' '),
                                                                 new FormatterMinLengthAddTrailing (10, ' '));
    CommonsTestUtils.testDefaultImplementationWithDifferentContentObject (new FormatterMinLengthAddTrailing (10, ' '),
                                                                     new FormatterMinLengthAddTrailing (10, 'x'));
    CommonsTestUtils.testDefaultImplementationWithDifferentContentObject (new FormatterMinLengthAddTrailing (10, ' '),
                                                                     new FormatterMinLengthAddTrailing (5, ' '));

    CommonsTestUtils.testDefaultImplementationWithEqualContentObject (new FormatterStringPrefixAndSuffix ("p", "s"),
                                                                 new FormatterStringPrefixAndSuffix ("p", "s"));
    CommonsTestUtils.testDefaultImplementationWithDifferentContentObject (new FormatterStringPrefixAndSuffix ("p", "s"),
                                                                     new FormatterStringPrefixAndSuffix ("p", "ss"));
    CommonsTestUtils.testDefaultImplementationWithDifferentContentObject (new FormatterStringPrefixAndSuffix ("p", "s"),
                                                                     new FormatterStringPrefixAndSuffix ("pp", "s"));

    CommonsTestUtils.testDefaultImplementationWithEqualContentObject (new FormatterStringPrefix ("p"),
                                                                 new FormatterStringPrefix ("p"));
    CommonsTestUtils.testDefaultImplementationWithDifferentContentObject (new FormatterStringPrefix ("p"),
                                                                     new FormatterStringPrefix ("pp"));

    CommonsTestUtils.testDefaultImplementationWithEqualContentObject (new FormatterStringSuffix ("s"),
                                                                 new FormatterStringSuffix ("s"));
    CommonsTestUtils.testDefaultImplementationWithDifferentContentObject (new FormatterStringSuffix ("s"),
                                                                     new FormatterStringSuffix ("ss"));
  }
}
