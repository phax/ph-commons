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
package com.helger.commons.functional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test class for class {@link IPredicate}
 *
 * @author Philip Helger
 */
public final class IPredicateFuncTest
{
  @Test
  public void testAll ()
  {
    final IPredicate <String> aFilter = IPredicate.all ();
    assertNotNull (aFilter);
    assertTrue (aFilter.test (null));
    assertTrue (aFilter.test (""));
    assertTrue (aFilter.test ("bla bla bla"));
  }

  @Test
  public void testNone ()
  {
    final IPredicate <String> aFilter = IPredicate.none ();
    assertNotNull (aFilter);
    assertFalse (aFilter.test (null));
    assertFalse (aFilter.test (""));
    assertFalse (aFilter.test ("bla bla bla"));
  }

  @Test
  public void testNotNull ()
  {
    final IPredicate <String> aFilter = IPredicate.notNull ();
    assertNotNull (aFilter);
    assertFalse (aFilter.test (null));
    assertTrue (aFilter.test (""));
    assertTrue (aFilter.test ("bla bla bla"));
  }

  @Test
  public void testIsNull ()
  {
    final IPredicate <String> aFilter = IPredicate.isNull ();
    assertNotNull (aFilter);
    assertTrue (aFilter.test (null));
    assertFalse (aFilter.test (""));
    assertFalse (aFilter.test ("bla bla bla"));
  }

  @Test
  public void testNegate ()
  {
    IPredicate <String> aFilter = IPredicate.<String> notNull ().negate ();
    assertNotNull (aFilter);
    assertTrue (aFilter.test (null));
    assertFalse (aFilter.test (""));
    assertFalse (aFilter.test ("bla bla bla"));

    aFilter = IPredicate.<String> isNull ().negate ();
    assertNotNull (aFilter);
    assertFalse (aFilter.test (null));
    assertTrue (aFilter.test (""));
    assertTrue (aFilter.test ("bla bla bla"));
  }
}
