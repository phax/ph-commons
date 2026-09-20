/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.xml.xpath;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPathFunction;

import org.junit.Test;

import com.helger.base.state.EChange;
import com.helger.unittest.support.TestHelper;

/**
 * Test class for class {@link MapBasedXPathFunctionResolver}.
 *
 * @author Philip Helger
 */
public final class MapBasedXPathFunctionResolverTest
{
  private static final QName FUNC1 = new QName ("urn:example", "func1");
  private static final QName FUNC2 = new QName ("urn:example", "func2");
  private static final XPathFunction FUNCTION = aArgs -> "result";

  @Test
  public void testEmpty ()
  {
    final MapBasedXPathFunctionResolver aFR = new MapBasedXPathFunctionResolver ();
    assertEquals (0, aFR.getFunctionCount ());
    assertTrue (aFR.getAllFunctions ().isEmpty ());
    assertNull (aFR.resolveFunction (FUNC1, 0));
    assertNull (aFR.resolveFunction ((XPathFunctionKey) null));
    assertNotNull (aFR.toString ());
    assertSame (EChange.UNCHANGED, aFR.clear ());
  }

  @Test
  public void testAddUniqueFunction ()
  {
    final MapBasedXPathFunctionResolver aFR = new MapBasedXPathFunctionResolver ();
    assertSame (EChange.CHANGED, aFR.addUniqueFunction (FUNC1, 1, FUNCTION));
    // Already contained
    assertSame (EChange.UNCHANGED, aFR.addUniqueFunction (FUNC1, 1, FUNCTION));
    // A different arity is a different function
    assertSame (EChange.CHANGED, aFR.addUniqueFunction (FUNC1, 2, FUNCTION));
    assertEquals (2, aFR.getFunctionCount ());

    assertSame (FUNCTION, aFR.resolveFunction (FUNC1, 1));
    assertNull (aFR.resolveFunction (FUNC1, 3));
  }

  @Test
  public void testAddUniqueFunctionWithStrings ()
  {
    final MapBasedXPathFunctionResolver aFR = new MapBasedXPathFunctionResolver ();
    assertSame (EChange.CHANGED, aFR.addUniqueFunction ("urn:example", "func1", 1, FUNCTION));
    assertSame (FUNCTION, aFR.resolveFunction (FUNC1, 1));
  }

  @Test
  public void testRemoveFunction ()
  {
    final MapBasedXPathFunctionResolver aFR = new MapBasedXPathFunctionResolver ();
    aFR.addUniqueFunction (FUNC1, 1, FUNCTION);
    aFR.addUniqueFunction (FUNC1, 2, FUNCTION);
    aFR.addUniqueFunction (FUNC2, 1, FUNCTION);

    assertSame (EChange.UNCHANGED, aFR.removeFunction ((XPathFunctionKey) null));
    assertSame (EChange.UNCHANGED, aFR.removeFunction (FUNC2, 99));
    assertSame (EChange.CHANGED, aFR.removeFunction (FUNC2, 1));
    assertEquals (2, aFR.getFunctionCount ());

    // Removes both arities at once
    assertSame (EChange.UNCHANGED, aFR.removeFunctionsWithName (null));
    assertSame (EChange.CHANGED, aFR.removeFunctionsWithName (FUNC1));
    assertEquals (0, aFR.getFunctionCount ());
  }

  @Test
  public void testAddAllFrom ()
  {
    final XPathFunction aOtherFunction = aArgs -> "other";

    final MapBasedXPathFunctionResolver aSrc = new MapBasedXPathFunctionResolver ();
    aSrc.addUniqueFunction (FUNC1, 1, aOtherFunction);
    aSrc.addUniqueFunction (FUNC2, 1, aOtherFunction);

    final MapBasedXPathFunctionResolver aDst = new MapBasedXPathFunctionResolver ();
    aDst.addUniqueFunction (FUNC1, 1, FUNCTION);

    assertSame (EChange.CHANGED, aDst.addAllFrom (aSrc, false));
    assertSame (FUNCTION, aDst.resolveFunction (FUNC1, 1));
    assertSame (aOtherFunction, aDst.resolveFunction (FUNC2, 1));

    assertSame (EChange.CHANGED, aDst.addAllFrom (aSrc, true));
    assertSame (aOtherFunction, aDst.resolveFunction (FUNC1, 1));
  }

  @Test
  public void testGetCloneAndEquals ()
  {
    final MapBasedXPathFunctionResolver aFR = new MapBasedXPathFunctionResolver ();
    aFR.addUniqueFunction (FUNC1, 1, FUNCTION);

    final MapBasedXPathFunctionResolver aClone = aFR.getClone ();
    assertNotSame (aFR, aClone);
    TestHelper.testDefaultImplementationWithEqualContentObject (aFR, aClone);

    final MapBasedXPathFunctionResolver aOther = new MapBasedXPathFunctionResolver ();
    aOther.addUniqueFunction (FUNC2, 1, FUNCTION);
    TestHelper.testDefaultImplementationWithDifferentContentObject (aFR, aOther);

    assertEquals (aFR, new MapBasedXPathFunctionResolver (aFR));
  }

  @Test
  public void testInvalidParams ()
  {
    final MapBasedXPathFunctionResolver aFR = new MapBasedXPathFunctionResolver ();
    try
    {
      aFR.addUniqueFunction (FUNC1, 1, null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
    try
    {
      aFR.addAllFrom (null, true);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
  }
}
