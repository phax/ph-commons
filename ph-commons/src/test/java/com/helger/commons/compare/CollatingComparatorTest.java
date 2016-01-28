/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
package com.helger.commons.compare;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.text.Collator;
import java.util.List;
import java.util.Locale;

import org.junit.Test;

import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.mock.AbstractCommonsTestCase;
import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link CollatingComparator}.
 *
 * @author Philip Helger
 */
public final class CollatingComparatorTest extends AbstractCommonsTestCase
{
  @Test
  public void testAll ()
  {
    final List <String> l = CollectionHelper.newList ("a", null, "c");
    assertEquals (3, CollectionHelper.getSorted (l, new CollatingComparator (Locale.US)).size ());
    assertEquals (3, CollectionHelper.getSorted (l, new CollatingComparator (Locale.US).reversed ()).size ());
    assertEquals (3, CollectionHelper.getSorted (l, new CollatingComparator (L_EN)).size ());
    assertEquals (3, CollectionHelper.getSorted (l, new CollatingComparator (L_FR).reversed ()).size ());
    assertEquals (3, CollectionHelper.getSorted (l, new CollatingComparator (Collator.getInstance (L_FR))).size ());
    assertEquals (3,
                  CollectionHelper.getSorted (l, new CollatingComparator (Collator.getInstance (L_FR)).reversed ())
                                  .size ());
    CommonsTestHelper.testToStringImplementation (new CollatingComparator (Locale.US));

    try
    {
      new CollatingComparator ((Collator) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }
}
