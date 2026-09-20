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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.xml.namespace.QName;

import org.junit.Test;

import com.helger.unittest.support.TestHelper;

/**
 * Test class for class {@link XPathFunctionKey}.
 *
 * @author Philip Helger
 */
public final class XPathFunctionKeyTest
{
  private static final QName FUNC1 = new QName ("urn:example", "func1");
  private static final QName FUNC2 = new QName ("urn:example", "func2");

  @Test
  public void testBasic ()
  {
    final XPathFunctionKey aKey = new XPathFunctionKey (FUNC1, 2);
    assertSame (FUNC1, aKey.getFunctionName ());
    assertEquals (2, aKey.getArity ());
    assertNotNull (aKey.toString ());

    TestHelper.testDefaultImplementationWithEqualContentObject (aKey, new XPathFunctionKey (FUNC1, 2));
    TestHelper.testDefaultImplementationWithDifferentContentObject (aKey, new XPathFunctionKey (FUNC1, 3));
    TestHelper.testDefaultImplementationWithDifferentContentObject (aKey, new XPathFunctionKey (FUNC2, 2));
  }

  @Test
  public void testCompareTo ()
  {
    final XPathFunctionKey aKey1 = new XPathFunctionKey (FUNC1, 2);
    assertEquals (0, aKey1.compareTo (new XPathFunctionKey (FUNC1, 2)));
    // Different name
    assertTrue (aKey1.compareTo (new XPathFunctionKey (FUNC2, 2)) < 0);
    assertTrue (new XPathFunctionKey (FUNC2, 2).compareTo (aKey1) > 0);
    // Same name, different arity
    assertTrue (aKey1.compareTo (new XPathFunctionKey (FUNC1, 3)) < 0);
    assertTrue (new XPathFunctionKey (FUNC1, 3).compareTo (aKey1) > 0);
  }

  @Test
  public void testInvalidParams ()
  {
    try
    {
      new XPathFunctionKey (null, 2);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
    try
    {
      new XPathFunctionKey (FUNC1, -1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
  }
}
