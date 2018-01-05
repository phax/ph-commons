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
package com.helger.commons.format;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.helger.commons.mock.CommonsTestHelper;

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
    FormatableObject <?> aFO = new FormatableObject <> ("Any", FormatterStringPrefixAndSuffix.createWithBrackets ());
    assertEquals ("Any", aFO.getValue ());
    assertEquals (FormatterStringPrefixAndSuffix.class, aFO.getFormatter ().getClass ());
    assertEquals ("[Any]", aFO.getAsString ());
    CommonsTestHelper.testToStringImplementation (aFO);

    aFO = new FormatableObject <> ("Any",
                                   FormatterStringPrefixAndSuffix.createWithBrackets ()
                                                                 .andThen (FormatterStringPrefixAndSuffix.createWithBrackets ()));
    assertEquals ("Any", aFO.getValue ());
    assertEquals ("[[Any]]", aFO.getAsString ());
    CommonsTestHelper.testToStringImplementation (aFO);

    aFO = new FormatableObject <> ("Any", FormatterStringPrefixAndSuffix.createWithBrackets ());
    assertEquals ("Any", aFO.getValue ());
    assertEquals ("[Any]", aFO.getAsString ());
    CommonsTestHelper.testToStringImplementation (aFO);

    aFO = new FormatableObject <> (null,
                                   FormatterStringPrefixAndSuffix.createWithBrackets ()
                                                                 .andThen (FormatterStringPrefixAndSuffix.createWithBrackets ()));
    assertNull (aFO.getValue ());
    assertEquals ("[[]]", aFO.getAsString ());
    CommonsTestHelper.testToStringImplementation (aFO);

    aFO = new FormatableObject <> ("Any", FormatterStringPrefixAndSuffix.createPrefixOnly ("x "));
    assertEquals ("Any", aFO.getValue ());
    assertEquals ("x Any", aFO.getAsString ());
    CommonsTestHelper.testToStringImplementation (aFO);

    aFO = new FormatableObject <> ("Any",
                                   FormatterStringPrefixAndSuffix.createWithBrackets ()
                                                                 .andThen (FormatterStringPrefixAndSuffix.createPrefixOnly ("x ")));
    assertEquals ("Any", aFO.getValue ());
    assertEquals ("x [Any]", aFO.getAsString ());
    CommonsTestHelper.testToStringImplementation (aFO);

    aFO = new FormatableObject <> ("Any", FormatterStringPrefixAndSuffix.createSuffixOnly (" y"));
    assertEquals ("Any", aFO.getValue ());
    assertEquals ("Any y", aFO.getAsString ());
    CommonsTestHelper.testToStringImplementation (aFO);

    aFO = new FormatableObject <> ("Any",
                                   FormatterStringPrefixAndSuffix.createWithBrackets ()
                                                                 .andThen (FormatterStringPrefixAndSuffix.createSuffixOnly (" y")));
    assertEquals ("Any", aFO.getValue ());
    assertEquals ("[Any] y", aFO.getAsString ());
    CommonsTestHelper.testToStringImplementation (aFO);

    aFO = new FormatableObject <> ("Any", new FormatterMinLengthAddLeading (10, '@'));
    assertEquals ("Any", aFO.getValue ());
    assertEquals ("@@@@@@@Any", aFO.getAsString ());
    CommonsTestHelper.testToStringImplementation (aFO);

    aFO = new FormatableObject <> ("Any",
                                   FormatterStringPrefixAndSuffix.createWithBrackets ()
                                                                 .andThen (new FormatterMinLengthAddLeading (10, '@')));
    assertEquals ("Any", aFO.getValue ());
    assertEquals ("@@@@@[Any]", aFO.getAsString ());
    CommonsTestHelper.testToStringImplementation (aFO);

    aFO = new FormatableObject <> ("Any", new FormatterMinLengthAddTrailing (10, '@'));
    assertEquals ("Any", aFO.getValue ());
    assertEquals ("Any@@@@@@@", aFO.getAsString ());
    CommonsTestHelper.testToStringImplementation (aFO);

    aFO = new FormatableObject <> ("Any",
                                   FormatterStringPrefixAndSuffix.createWithBrackets ()
                                                                 .andThen (new FormatterMinLengthAddTrailing (10,
                                                                                                              '@')));
    assertEquals ("Any", aFO.getValue ());
    assertEquals ("[Any]@@@@@", aFO.getAsString ());
    CommonsTestHelper.testToStringImplementation (aFO);
  }

  @Test
  public void testImpl ()
  {
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new FormatableObject <> ("Any",
                                                                                                FormatterStringPrefixAndSuffix.createWithBrackets ()),
                                                                       new FormatableObject <> ("Any",
                                                                                                FormatterStringPrefixAndSuffix.createWithBrackets ()));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new FormatableObject <> ("Any",
                                                                                                    FormatterStringPrefixAndSuffix.createWithBrackets ()),
                                                                           new FormatableObject <> ("Any2",
                                                                                                    FormatterStringPrefixAndSuffix.createWithBrackets ()));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new FormatableObject <> ("Any",
                                                                                                    FormatterStringPrefixAndSuffix.createWithBrackets ()),
                                                                           new FormatableObject <> ("Any",
                                                                                                    FormatterStringPrefixAndSuffix.createPrefixOnly ("oprefix")));

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (FormatterStringPrefixAndSuffix.createWithBrackets (),
                                                                       FormatterStringPrefixAndSuffix.createWithBrackets ());

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new FormatterMinLengthAddLeading (10, ' '),
                                                                       new FormatterMinLengthAddLeading (10, ' '));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new FormatterMinLengthAddLeading (10, ' '),
                                                                           new FormatterMinLengthAddLeading (10, 'x'));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new FormatterMinLengthAddLeading (10, ' '),
                                                                           new FormatterMinLengthAddLeading (5, ' '));

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new FormatterMinLengthAddTrailing (10, ' '),
                                                                       new FormatterMinLengthAddTrailing (10, ' '));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new FormatterMinLengthAddTrailing (10, ' '),
                                                                           new FormatterMinLengthAddTrailing (10, 'x'));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new FormatterMinLengthAddTrailing (10, ' '),
                                                                           new FormatterMinLengthAddTrailing (5, ' '));

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new FormatterStringPrefixAndSuffix ("p", "s"),
                                                                       new FormatterStringPrefixAndSuffix ("p", "s"));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new FormatterStringPrefixAndSuffix ("p",
                                                                                                               "s"),
                                                                           new FormatterStringPrefixAndSuffix ("p",
                                                                                                               "ss"));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new FormatterStringPrefixAndSuffix ("p",
                                                                                                               "s"),
                                                                           new FormatterStringPrefixAndSuffix ("pp",
                                                                                                               "s"));

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (FormatterStringPrefixAndSuffix.createPrefixOnly ("p"),
                                                                       FormatterStringPrefixAndSuffix.createPrefixOnly ("p"));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (FormatterStringPrefixAndSuffix.createPrefixOnly ("p"),
                                                                           FormatterStringPrefixAndSuffix.createPrefixOnly ("pp"));

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (FormatterStringPrefixAndSuffix.createSuffixOnly ("s"),
                                                                       FormatterStringPrefixAndSuffix.createSuffixOnly ("s"));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (FormatterStringPrefixAndSuffix.createSuffixOnly ("s"),
                                                                           FormatterStringPrefixAndSuffix.createSuffixOnly ("ss"));
  }
}
