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

import com.helger.commons.mock.PHTestUtils;

/**
 * Test class for class {@link FormattedObject}.
 * 
 * @author Philip Helger
 */
public final class FormattedObjectTest
{
  @Test
  public void testAll ()
  {
    FormattedObject aFO = new FormattedObject ("Any", new BracketFormatter ());
    assertEquals ("Any", aFO.getValue ());
    assertEquals (BracketFormatter.class, aFO.getFormatter ().getClass ());
    assertEquals ("[Any]", aFO.getAsString ());
    PHTestUtils.testToStringImplementation (aFO);

    aFO = new FormattedObject ("Any", new BracketFormatter (new BracketFormatter ()));
    assertEquals ("Any", aFO.getValue ());
    assertEquals (BracketFormatter.class, aFO.getFormatter ().getClass ());
    assertEquals ("[[Any]]", aFO.getAsString ());
    PHTestUtils.testToStringImplementation (aFO);

    aFO = new FormattedObject ("Any", new IFormatterProvider ()
    {
      public IFormatter getFormatter ()
      {
        return new BracketFormatter ();
      }
    });
    assertEquals ("Any", aFO.getValue ());
    assertEquals (BracketFormatter.class, aFO.getFormatter ().getClass ());
    assertEquals ("[Any]", aFO.getAsString ());
    PHTestUtils.testToStringImplementation (aFO);

    aFO = new FormattedObject (null, new BracketFormatter (new BracketFormatter ()));
    assertNull (aFO.getValue ());
    assertEquals (BracketFormatter.class, aFO.getFormatter ().getClass ());
    assertEquals ("[[]]", aFO.getAsString ());
    PHTestUtils.testToStringImplementation (aFO);

    aFO = new FormattedObject ("Any", new StringPrefixFormatter ("x "));
    assertEquals ("Any", aFO.getValue ());
    assertEquals (StringPrefixFormatter.class, aFO.getFormatter ().getClass ());
    assertEquals ("x Any", aFO.getAsString ());
    PHTestUtils.testToStringImplementation (aFO);

    aFO = new FormattedObject ("Any", new StringPrefixFormatter (new BracketFormatter (), "x "));
    assertEquals ("Any", aFO.getValue ());
    assertEquals (StringPrefixFormatter.class, aFO.getFormatter ().getClass ());
    assertEquals ("x [Any]", aFO.getAsString ());
    PHTestUtils.testToStringImplementation (aFO);

    aFO = new FormattedObject ("Any", new StringSuffixFormatter (" y"));
    assertEquals ("Any", aFO.getValue ());
    assertEquals (StringSuffixFormatter.class, aFO.getFormatter ().getClass ());
    assertEquals ("Any y", aFO.getAsString ());
    PHTestUtils.testToStringImplementation (aFO);

    aFO = new FormattedObject ("Any", new StringSuffixFormatter (new BracketFormatter (), " y"));
    assertEquals ("Any", aFO.getValue ());
    assertEquals (StringSuffixFormatter.class, aFO.getFormatter ().getClass ());
    assertEquals ("[Any] y", aFO.getAsString ());
    PHTestUtils.testToStringImplementation (aFO);

    aFO = new FormattedObject ("Any", new MinLengthAddLeadingFormatter (10, '@'));
    assertEquals ("Any", aFO.getValue ());
    assertEquals (MinLengthAddLeadingFormatter.class, aFO.getFormatter ().getClass ());
    assertEquals ("@@@@@@@Any", aFO.getAsString ());
    PHTestUtils.testToStringImplementation (aFO);

    aFO = new FormattedObject ("Any", new MinLengthAddLeadingFormatter (new BracketFormatter (), 10, '@'));
    assertEquals ("Any", aFO.getValue ());
    assertEquals (MinLengthAddLeadingFormatter.class, aFO.getFormatter ().getClass ());
    assertEquals ("@@@@@[Any]", aFO.getAsString ());
    PHTestUtils.testToStringImplementation (aFO);

    aFO = new FormattedObject ("Any", new MinLengthAddTrailingFormatter (10, '@'));
    assertEquals ("Any", aFO.getValue ());
    assertEquals (MinLengthAddTrailingFormatter.class, aFO.getFormatter ().getClass ());
    assertEquals ("Any@@@@@@@", aFO.getAsString ());
    PHTestUtils.testToStringImplementation (aFO);

    aFO = new FormattedObject ("Any", new MinLengthAddTrailingFormatter (new BracketFormatter (), 10, '@'));
    assertEquals ("Any", aFO.getValue ());
    assertEquals (MinLengthAddTrailingFormatter.class, aFO.getFormatter ().getClass ());
    assertEquals ("[Any]@@@@@", aFO.getAsString ());
    PHTestUtils.testToStringImplementation (aFO);
  }

  @Test
  public void testImpl ()
  {
    PHTestUtils.testDefaultImplementationWithEqualContentObject (new FormattedObject ("Any", new BracketFormatter ()),
                                                                 new FormattedObject ("Any", new BracketFormatter ()));
    PHTestUtils.testDefaultImplementationWithDifferentContentObject (new FormattedObject ("Any",
                                                                                          new BracketFormatter ()),
                                                                     new FormattedObject ("Any2",
                                                                                          new BracketFormatter ()));
    PHTestUtils.testDefaultImplementationWithDifferentContentObject (new FormattedObject ("Any",
                                                                                          new BracketFormatter ()),
                                                                     new FormattedObject ("Any",
                                                                                          new StringPrefixFormatter ("oprefix")));

    PHTestUtils.testDefaultImplementationWithEqualContentObject (new BracketFormatter (), new BracketFormatter ());

    PHTestUtils.testDefaultImplementationWithEqualContentObject (new MinLengthAddLeadingFormatter (10, ' '),
                                                                 new MinLengthAddLeadingFormatter (10, ' '));
    PHTestUtils.testDefaultImplementationWithDifferentContentObject (new MinLengthAddLeadingFormatter (10, ' '),
                                                                     new MinLengthAddLeadingFormatter (10, 'x'));
    PHTestUtils.testDefaultImplementationWithDifferentContentObject (new MinLengthAddLeadingFormatter (10, ' '),
                                                                     new MinLengthAddLeadingFormatter (5, ' '));

    PHTestUtils.testDefaultImplementationWithEqualContentObject (new MinLengthAddTrailingFormatter (10, ' '),
                                                                 new MinLengthAddTrailingFormatter (10, ' '));
    PHTestUtils.testDefaultImplementationWithDifferentContentObject (new MinLengthAddTrailingFormatter (10, ' '),
                                                                     new MinLengthAddTrailingFormatter (10, 'x'));
    PHTestUtils.testDefaultImplementationWithDifferentContentObject (new MinLengthAddTrailingFormatter (10, ' '),
                                                                     new MinLengthAddTrailingFormatter (5, ' '));

    PHTestUtils.testDefaultImplementationWithEqualContentObject (new StringPrefixAndSuffixFormatter ("p", "s"),
                                                                 new StringPrefixAndSuffixFormatter ("p", "s"));
    PHTestUtils.testDefaultImplementationWithDifferentContentObject (new StringPrefixAndSuffixFormatter ("p", "s"),
                                                                     new StringPrefixAndSuffixFormatter ("p", "ss"));
    PHTestUtils.testDefaultImplementationWithDifferentContentObject (new StringPrefixAndSuffixFormatter ("p", "s"),
                                                                     new StringPrefixAndSuffixFormatter ("pp", "s"));

    PHTestUtils.testDefaultImplementationWithEqualContentObject (new StringPrefixFormatter ("p"),
                                                                 new StringPrefixFormatter ("p"));
    PHTestUtils.testDefaultImplementationWithDifferentContentObject (new StringPrefixFormatter ("p"),
                                                                     new StringPrefixFormatter ("pp"));

    PHTestUtils.testDefaultImplementationWithEqualContentObject (new StringSuffixFormatter ("s"),
                                                                 new StringSuffixFormatter ("s"));
    PHTestUtils.testDefaultImplementationWithDifferentContentObject (new StringSuffixFormatter ("s"),
                                                                     new StringSuffixFormatter ("ss"));
  }
}
