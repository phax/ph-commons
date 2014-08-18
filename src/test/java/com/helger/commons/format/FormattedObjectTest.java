/**
 * Copyright (C) 2006-2014 phloc systems (www.phloc.com)
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
package com.helger.commons.format;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.helger.commons.format.impl.BracketFormatter;
import com.helger.commons.format.impl.MinLengthAddLeadingFormatter;
import com.helger.commons.format.impl.MinLengthAddTrailingFormatter;
import com.helger.commons.format.impl.StringPrefixAndSuffixFormatter;
import com.helger.commons.format.impl.StringPrefixFormatter;
import com.helger.commons.format.impl.StringSuffixFormatter;
import com.helger.commons.mock.PhlocTestUtils;

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
    PhlocTestUtils.testToStringImplementation (aFO);

    aFO = new FormattedObject ("Any", new BracketFormatter (new BracketFormatter ()));
    assertEquals ("Any", aFO.getValue ());
    assertEquals (BracketFormatter.class, aFO.getFormatter ().getClass ());
    assertEquals ("[[Any]]", aFO.getAsString ());
    PhlocTestUtils.testToStringImplementation (aFO);

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
    PhlocTestUtils.testToStringImplementation (aFO);

    aFO = new FormattedObject (null, new BracketFormatter (new BracketFormatter ()));
    assertNull (aFO.getValue ());
    assertEquals (BracketFormatter.class, aFO.getFormatter ().getClass ());
    assertEquals ("[[]]", aFO.getAsString ());
    PhlocTestUtils.testToStringImplementation (aFO);

    aFO = new FormattedObject ("Any", new StringPrefixFormatter ("x "));
    assertEquals ("Any", aFO.getValue ());
    assertEquals (StringPrefixFormatter.class, aFO.getFormatter ().getClass ());
    assertEquals ("x Any", aFO.getAsString ());
    PhlocTestUtils.testToStringImplementation (aFO);

    aFO = new FormattedObject ("Any", new StringPrefixFormatter (new BracketFormatter (), "x "));
    assertEquals ("Any", aFO.getValue ());
    assertEquals (StringPrefixFormatter.class, aFO.getFormatter ().getClass ());
    assertEquals ("x [Any]", aFO.getAsString ());
    PhlocTestUtils.testToStringImplementation (aFO);

    aFO = new FormattedObject ("Any", new StringSuffixFormatter (" y"));
    assertEquals ("Any", aFO.getValue ());
    assertEquals (StringSuffixFormatter.class, aFO.getFormatter ().getClass ());
    assertEquals ("Any y", aFO.getAsString ());
    PhlocTestUtils.testToStringImplementation (aFO);

    aFO = new FormattedObject ("Any", new StringSuffixFormatter (new BracketFormatter (), " y"));
    assertEquals ("Any", aFO.getValue ());
    assertEquals (StringSuffixFormatter.class, aFO.getFormatter ().getClass ());
    assertEquals ("[Any] y", aFO.getAsString ());
    PhlocTestUtils.testToStringImplementation (aFO);

    aFO = new FormattedObject ("Any", new MinLengthAddLeadingFormatter (10, '@'));
    assertEquals ("Any", aFO.getValue ());
    assertEquals (MinLengthAddLeadingFormatter.class, aFO.getFormatter ().getClass ());
    assertEquals ("@@@@@@@Any", aFO.getAsString ());
    PhlocTestUtils.testToStringImplementation (aFO);

    aFO = new FormattedObject ("Any", new MinLengthAddLeadingFormatter (new BracketFormatter (), 10, '@'));
    assertEquals ("Any", aFO.getValue ());
    assertEquals (MinLengthAddLeadingFormatter.class, aFO.getFormatter ().getClass ());
    assertEquals ("@@@@@[Any]", aFO.getAsString ());
    PhlocTestUtils.testToStringImplementation (aFO);

    aFO = new FormattedObject ("Any", new MinLengthAddTrailingFormatter (10, '@'));
    assertEquals ("Any", aFO.getValue ());
    assertEquals (MinLengthAddTrailingFormatter.class, aFO.getFormatter ().getClass ());
    assertEquals ("Any@@@@@@@", aFO.getAsString ());
    PhlocTestUtils.testToStringImplementation (aFO);

    aFO = new FormattedObject ("Any", new MinLengthAddTrailingFormatter (new BracketFormatter (), 10, '@'));
    assertEquals ("Any", aFO.getValue ());
    assertEquals (MinLengthAddTrailingFormatter.class, aFO.getFormatter ().getClass ());
    assertEquals ("[Any]@@@@@", aFO.getAsString ());
    PhlocTestUtils.testToStringImplementation (aFO);
  }

  @Test
  public void testImpl ()
  {
    PhlocTestUtils.testDefaultImplementationWithEqualContentObject (new FormattedObject ("Any", new BracketFormatter ()),
                                                                    new FormattedObject ("Any", new BracketFormatter ()));
    PhlocTestUtils.testDefaultImplementationWithDifferentContentObject (new FormattedObject ("Any",
                                                                                             new BracketFormatter ()),
                                                                        new FormattedObject ("Any2",
                                                                                             new BracketFormatter ()));
    PhlocTestUtils.testDefaultImplementationWithDifferentContentObject (new FormattedObject ("Any",
                                                                                             new BracketFormatter ()),
                                                                        new FormattedObject ("Any",
                                                                                             new StringPrefixFormatter ("oprefix")));

    PhlocTestUtils.testDefaultImplementationWithEqualContentObject (new BracketFormatter (), new BracketFormatter ());

    PhlocTestUtils.testDefaultImplementationWithEqualContentObject (new MinLengthAddLeadingFormatter (10, ' '),
                                                                    new MinLengthAddLeadingFormatter (10, ' '));
    PhlocTestUtils.testDefaultImplementationWithDifferentContentObject (new MinLengthAddLeadingFormatter (10, ' '),
                                                                        new MinLengthAddLeadingFormatter (10, 'x'));
    PhlocTestUtils.testDefaultImplementationWithDifferentContentObject (new MinLengthAddLeadingFormatter (10, ' '),
                                                                        new MinLengthAddLeadingFormatter (5, ' '));

    PhlocTestUtils.testDefaultImplementationWithEqualContentObject (new MinLengthAddTrailingFormatter (10, ' '),
                                                                    new MinLengthAddTrailingFormatter (10, ' '));
    PhlocTestUtils.testDefaultImplementationWithDifferentContentObject (new MinLengthAddTrailingFormatter (10, ' '),
                                                                        new MinLengthAddTrailingFormatter (10, 'x'));
    PhlocTestUtils.testDefaultImplementationWithDifferentContentObject (new MinLengthAddTrailingFormatter (10, ' '),
                                                                        new MinLengthAddTrailingFormatter (5, ' '));

    PhlocTestUtils.testDefaultImplementationWithEqualContentObject (new StringPrefixAndSuffixFormatter ("p", "s"),
                                                                    new StringPrefixAndSuffixFormatter ("p", "s"));
    PhlocTestUtils.testDefaultImplementationWithDifferentContentObject (new StringPrefixAndSuffixFormatter ("p", "s"),
                                                                        new StringPrefixAndSuffixFormatter ("p", "ss"));
    PhlocTestUtils.testDefaultImplementationWithDifferentContentObject (new StringPrefixAndSuffixFormatter ("p", "s"),
                                                                        new StringPrefixAndSuffixFormatter ("pp", "s"));

    PhlocTestUtils.testDefaultImplementationWithEqualContentObject (new StringPrefixFormatter ("p"),
                                                                    new StringPrefixFormatter ("p"));
    PhlocTestUtils.testDefaultImplementationWithDifferentContentObject (new StringPrefixFormatter ("p"),
                                                                        new StringPrefixFormatter ("pp"));

    PhlocTestUtils.testDefaultImplementationWithEqualContentObject (new StringSuffixFormatter ("s"),
                                                                    new StringSuffixFormatter ("s"));
    PhlocTestUtils.testDefaultImplementationWithDifferentContentObject (new StringSuffixFormatter ("s"),
                                                                        new StringSuffixFormatter ("ss"));
  }
}
