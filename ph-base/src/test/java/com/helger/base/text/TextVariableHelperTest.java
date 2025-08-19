/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.base.text;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.helger.base.string.StringHelper;

/**
 * Test class for class {@link TextVariableHelper}.
 *
 * @author Philip Helger
 */
public class TextVariableHelperTest
{
  @Test
  public void testSplitByVariables ()
  {
    List <String> aList = TextVariableHelper.splitByVariables ("a");
    assertEquals (Arrays.asList ("a"), aList);

    aList = TextVariableHelper.splitByVariables ("a${b}c");
    assertEquals (Arrays.asList ("a", "b", "c"), aList);

    aList = TextVariableHelper.splitByVariables ("a${b}");
    assertEquals (Arrays.asList ("a", "b"), aList);

    aList = TextVariableHelper.splitByVariables ("${b}");
    assertEquals (Arrays.asList ("", "b"), aList);

    aList = TextVariableHelper.splitByVariables ("${b}${c}");
    assertEquals (Arrays.asList ("", "b", "", "c"), aList);

    aList = TextVariableHelper.splitByVariables ("${}${}");
    assertEquals (Arrays.asList ("", "", "", ""), aList);

    aList = TextVariableHelper.splitByVariables ("${}${}a");
    assertEquals (Arrays.asList ("", "", "", "", "a"), aList);

    aList = TextVariableHelper.splitByVariables ("This ${is} a more ${real world} example");
    assertEquals (Arrays.asList ("This ", "is", " a more ", "real world", " example"), aList);

    // Masking tests
    aList = TextVariableHelper.splitByVariables ("\\a");
    assertEquals (Arrays.asList ("a"), aList);

    aList = TextVariableHelper.splitByVariables ("\\\\");
    assertEquals (Arrays.asList ("\\"), aList);

    aList = TextVariableHelper.splitByVariables ("$");
    assertEquals (Arrays.asList ("$"), aList);

    aList = TextVariableHelper.splitByVariables ("\\$");
    assertEquals (Arrays.asList ("$"), aList);

    aList = TextVariableHelper.splitByVariables ("\\\\a");
    assertEquals (Arrays.asList ("\\a"), aList);

    aList = TextVariableHelper.splitByVariables ("\\\\a\\\\\\\\");
    assertEquals (Arrays.asList ("\\a\\\\"), aList);

    aList = TextVariableHelper.splitByVariables ("${\\}}");
    assertEquals (Arrays.asList ("", "}"), aList);

    aList = TextVariableHelper.splitByVariables ("${\\\\}");
    assertEquals (Arrays.asList ("", "\\"), aList);

    aList = TextVariableHelper.splitByVariables ("${{{ so what \\}\\}}");
    assertEquals (Arrays.asList ("", "{{ so what }}"), aList);

    aList = TextVariableHelper.splitByVariables ("Thi\\$ ${is\\}} a more ${$$}");
    assertEquals (Arrays.asList ("Thi$ ", "is}", " a more ", "$$"), aList);

    aList = TextVariableHelper.splitByVariables ("\\T\\h\\i\\$\\ ${\\i\\s\\}}\\ \\a\\ \\m\\o\\r\\e\\ ${\\$\\$}");
    assertEquals (Arrays.asList ("Thi$ ", "is}", " a more ", "$$"), aList);

    // Test broken things

    aList = TextVariableHelper.splitByVariables ("${");
    assertEquals (Arrays.asList ("${"), aList);

    aList = TextVariableHelper.splitByVariables ("bla ${foo");
    assertEquals (Arrays.asList ("bla ${foo"), aList);

    aList = TextVariableHelper.splitByVariables ("bla $foo");
    assertEquals (Arrays.asList ("bla $foo"), aList);

    aList = TextVariableHelper.splitByVariables ("bla $$foo");
    assertEquals (Arrays.asList ("bla $$foo"), aList);

    aList = TextVariableHelper.splitByVariables ("$");
    assertEquals (Arrays.asList ("$"), aList);

    aList = TextVariableHelper.splitByVariables ("$$");
    assertEquals (Arrays.asList ("$$"), aList);

    aList = TextVariableHelper.splitByVariables ("$${var}");
    assertEquals (Arrays.asList ("$", "var"), aList);

    aList = TextVariableHelper.splitByVariables ("${var}$");
    assertEquals (Arrays.asList ("", "var", "$"), aList);

    aList = TextVariableHelper.splitByVariables ("bla ${foo\\}");
    assertEquals (Arrays.asList ("bla ${foo\\}"), aList);

    aList = TextVariableHelper.splitByVariables ("bla ${good}${foo");
    assertEquals (Arrays.asList ("bla ", "good", "${foo"), aList);
  }

  @Test
  public void testGetWithReplacedVariables ()
  {
    final Map <String, String> aVars = new HashMap <> ();
    aVars.put ("name", "PH");
    aVars.put ("bla", "foo");

    // Expected cases
    assertNull (TextVariableHelper.getWithReplacedVariables (null, aVars::get));
    assertEquals ("", TextVariableHelper.getWithReplacedVariables ("", aVars::get));
    assertEquals ("abc", TextVariableHelper.getWithReplacedVariables ("abc", aVars::get));
    assertEquals (" abc ", TextVariableHelper.getWithReplacedVariables (" abc ", aVars::get));
    assertEquals ("PH", TextVariableHelper.getWithReplacedVariables ("${name}", aVars::get));
    assertEquals ("PHPH", TextVariableHelper.getWithReplacedVariables ("${name}${name}", aVars::get));
    assertEquals ("Hello PH - so foo",
                  TextVariableHelper.getWithReplacedVariables ("Hello ${name} - so ${bla}", aVars::get));
    assertEquals (" PH$PH\\ ", TextVariableHelper.getWithReplacedVariables (" ${name}\\$${name}\\\\ ", aVars::get));

    // Unresolved variables
    assertEquals ("null", TextVariableHelper.getWithReplacedVariables ("${noidea}", aVars::get));
    assertEquals ("anullb", TextVariableHelper.getWithReplacedVariables ("a${noidea}b", aVars::get));
    assertEquals ("unresolved(noidea)",
                  TextVariableHelper.getWithReplacedVariables ("${noidea}",
                                                               x -> StringHelper.getNotNull (aVars.get (x),
                                                                                        () -> "unresolved(" +
                                                                                              x +
                                                                                              ")")));

    // Unexpected cases
    assertEquals ("a${open", TextVariableHelper.getWithReplacedVariables ("a${open", aVars::get));
    assertEquals ("a PH ${open", TextVariableHelper.getWithReplacedVariables ("a ${name} ${open", aVars::get));
  }

  @Test
  public void testContainsVariables ()
  {
    // Expected cases
    assertFalse (TextVariableHelper.containsVariables (null));
    assertFalse (TextVariableHelper.containsVariables (""));
    assertFalse (TextVariableHelper.containsVariables ("abc"));
    assertFalse (TextVariableHelper.containsVariables (" abc "));
    assertTrue (TextVariableHelper.containsVariables ("${name}"));
    assertTrue (TextVariableHelper.containsVariables ("${name}${name}"));
    assertTrue (TextVariableHelper.containsVariables ("Hello ${name} - so ${bla}"));
    assertTrue (TextVariableHelper.containsVariables (" ${name}\\$${name}\\\\ "));
    assertTrue (TextVariableHelper.containsVariables ("${noidea}"));
    assertTrue (TextVariableHelper.containsVariables ("a${noidea}b"));

    // Unexpected cases
    assertFalse (TextVariableHelper.containsVariables ("a${open"));
    assertTrue (TextVariableHelper.containsVariables ("a ${name} ${open"));
  }
}
