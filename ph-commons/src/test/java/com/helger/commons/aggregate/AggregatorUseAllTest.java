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
package com.helger.commons.aggregate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;
import java.util.List;

import org.junit.Test;

import com.helger.commons.collection.CollectionHelper;

/**
 * Test class for class {@link IAggregator}.
 *
 * @author Philip Helger
 */
public final class AggregatorUseAllTest
{
  @Test
  public void testAll ()
  {
    final IAggregator <String, Collection <String>> a1 = IAggregator.createUseAll ();
    final IAggregator <String, Collection <String>> a2 = IAggregator.createUseAll ();
    assertEquals (a1, a1);
    assertFalse (a1.equals (null));
    assertFalse (a1.equals ("any other"));
    assertEquals (a1.hashCode (), a1.hashCode ());
    assertFalse (a1.hashCode () == 0);
    assertFalse (a1.hashCode () == "any other".hashCode ());
    assertNotNull (a1.toString ());
    final List <String> l = CollectionHelper.newList ("a", null, "b", "", "c");
    assertEquals (l, a1.aggregate (l));
    assertEquals (l, a2.aggregate (l));
  }
}
