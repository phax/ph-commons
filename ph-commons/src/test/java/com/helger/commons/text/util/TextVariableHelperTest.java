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
package com.helger.commons.text.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.base.string.Strings;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsMap;

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
    ICommonsList <String> aList = TextVariableHelper.splitByVariables ("a");
    assertEquals (new CommonsArrayList <> ("a"), aList);

    aList = TextVariableHelper.splitByVariables ("a${b}c");
    assertEquals (new CommonsArrayList <> ("a", "b", "c"), aList);

    aList = TextVariableHelper.splitByVariables ("a${b}");
    assertEquals (new CommonsArrayList <> ("a", "b"), aList);

    aList = TextVariableHelper.splitByVariables ("${b}");
    assertEquals (new CommonsArrayList <> ("", "b"), aList);

    aList = TextVariableHelper.splitByVariables ("${b}${c}");
    assertEquals (new CommonsArrayList <> ("", "b", "", "c"), aList);

    aList = TextVariableHelper.splitByVariables ("${}${}");
    assertEquals (new CommonsArrayList <> ("", "", "", ""), aList);

    aList = TextVariableHelper.splitByVariables ("${}${}a");
    assertEquals (new CommonsArrayList <> ("", "", "", "", "a"), aList);

    aList = TextVariableHelper.splitByVariables ("This ${is} a more ${real world} example");
    assertEquals (new CommonsArrayList <> ("This ", "is", " a more ", "real world", " example"), aList);

    // Masking tests
    aList = TextVariableHelper.splitByVariables ("\\a");
    assertEquals (new CommonsArrayList <> ("a"), aList);

    aList = TextVariableHelper.splitByVariables ("\\\\");
    assertEquals (new CommonsArrayList <> ("\\"), aList);

    aList = TextVariableHelper.splitByVariables ("$");
    assertEquals (new CommonsArrayList <> ("$"), aList);

    aList = TextVariableHelper.splitByVariables ("\\$");
    assertEquals (new CommonsArrayList <> ("$"), aList);

    aList = TextVariableHelper.splitByVariables ("\\\\a");
    assertEquals (new CommonsArrayList <> ("\\a"), aList);

    aList = TextVariableHelper.splitByVariables ("\\\\a\\\\\\\\");
    assertEquals (new CommonsArrayList <> ("\\a\\\\"), aList);

    aList = TextVariableHelper.splitByVariables ("${\\}}");
    assertEquals (new CommonsArrayList <> ("", "}"), aList);

    aList = TextVariableHelper.splitByVariables ("${\\\\}");
    assertEquals (new CommonsArrayList <> ("", "\\"), aList);

    aList = TextVariableHelper.splitByVariables ("${{{ so what \\}\\}}");
    assertEquals (new CommonsArrayList <> ("", "{{ so what }}"), aList);

    aList = TextVariableHelper.splitByVariables ("Thi\\$ ${is\\}} a more ${$$}");
    assertEquals (new CommonsArrayList <> ("Thi$ ", "is}", " a more ", "$$"), aList);

    aList = TextVariableHelper.splitByVariables ("\\T\\h\\i\\$\\ ${\\i\\s\\}}\\ \\a\\ \\m\\o\\r\\e\\ ${\\$\\$}");
    assertEquals (new CommonsArrayList <> ("Thi$ ", "is}", " a more ", "$$"), aList);

    // Test broken things

    aList = TextVariableHelper.splitByVariables ("${");
    assertEquals (new CommonsArrayList <> ("${"), aList);

    aList = TextVariableHelper.splitByVariables ("bla ${foo");
    assertEquals (new CommonsArrayList <> ("bla ${foo"), aList);

    aList = TextVariableHelper.splitByVariables ("bla $foo");
    assertEquals (new CommonsArrayList <> ("bla $foo"), aList);

    aList = TextVariableHelper.splitByVariables ("bla $$foo");
    assertEquals (new CommonsArrayList <> ("bla $$foo"), aList);

    aList = TextVariableHelper.splitByVariables ("$");
    assertEquals (new CommonsArrayList <> ("$"), aList);

    aList = TextVariableHelper.splitByVariables ("$$");
    assertEquals (new CommonsArrayList <> ("$$"), aList);

    aList = TextVariableHelper.splitByVariables ("$${var}");
    assertEquals (new CommonsArrayList <> ("$", "var"), aList);

    aList = TextVariableHelper.splitByVariables ("${var}$");
    assertEquals (new CommonsArrayList <> ("", "var", "$"), aList);

    aList = TextVariableHelper.splitByVariables ("bla ${foo\\}");
    assertEquals (new CommonsArrayList <> ("bla ${foo\\}"), aList);

    aList = TextVariableHelper.splitByVariables ("bla ${good}${foo");
    assertEquals (new CommonsArrayList <> ("bla ", "good", "${foo"), aList);
  }

  @Test
  public void testGetWithReplacedVariables ()
  {
    final ICommonsMap <String, String> aVars = new CommonsHashMap <> ();
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
                                                               x -> Strings.getNotNull (aVars.get (x),
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
