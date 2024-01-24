/*
 * Copyright (C) 2014-2024 Philip Helger (www.helger.com)
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

import java.util.function.Predicate;

import org.junit.Test;

/**
 * Test class for class {@link Predicate}
 *
 * @author Philip Helger
 */
public final class PredicatesTest
{
  @Test
  public void testAll ()
  {
    final Predicate <String> aFilter = Predicates.all ();
    assertNotNull (aFilter);
    assertTrue (aFilter.test (null));
    assertTrue (aFilter.test (""));
    assertTrue (aFilter.test ("bla bla bla"));
  }

  @Test
  public void testNone ()
  {
    final Predicate <String> aFilter = Predicates.none ();
    assertNotNull (aFilter);
    assertFalse (aFilter.test (null));
    assertFalse (aFilter.test (""));
    assertFalse (aFilter.test ("bla bla bla"));
  }

  @Test
  public void testNotNull ()
  {
    final Predicate <String> aFilter = Predicates.notNull ();
    assertNotNull (aFilter);
    assertFalse (aFilter.test (null));
    assertTrue (aFilter.test (""));
    assertTrue (aFilter.test ("bla bla bla"));
  }

  @Test
  public void testIsNull ()
  {
    final Predicate <String> aFilter = Predicates.isNull ();
    assertNotNull (aFilter);
    assertTrue (aFilter.test (null));
    assertFalse (aFilter.test (""));
    assertFalse (aFilter.test ("bla bla bla"));
  }

  @Test
  public void testNegate ()
  {
    Predicate <String> aFilter = Predicates.<String> notNull ().negate ();
    assertNotNull (aFilter);
    assertTrue (aFilter.test (null));
    assertFalse (aFilter.test (""));
    assertFalse (aFilter.test ("bla bla bla"));

    aFilter = Predicates.<String> isNull ().negate ();
    assertNotNull (aFilter);
    assertFalse (aFilter.test (null));
    assertTrue (aFilter.test (""));
    assertTrue (aFilter.test ("bla bla bla"));
  }
}
