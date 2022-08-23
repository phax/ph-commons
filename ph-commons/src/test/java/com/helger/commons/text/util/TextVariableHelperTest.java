package com.helger.commons.text.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.string.StringHelper;

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
                                                               x -> StringHelper.getNotNull (aVars.get (x),
                                                                                             () -> "unresolved(" +
                                                                                                   x +
                                                                                                   ")")));

    // Unexpected cases
    assertEquals ("a${open", TextVariableHelper.getWithReplacedVariables ("a${open", aVars::get));
    assertEquals ("a PH ${open", TextVariableHelper.getWithReplacedVariables ("a ${name} ${open", aVars::get));
  }
}
