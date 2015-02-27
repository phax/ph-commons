/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.commons.changelog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.helger.commons.collections.CollectionHelper;

/**
 * Test class for class {@link ComparatorChangeLogComponent}.
 *
 * @author Philip Helger
 */
public final class ComparatorChangeLogComponentTest
{
  @Test
  public void testAll ()
  {
    final Map <URI, ChangeLog> aAllChangeLogs = ChangeLogSerializer.readAllChangeLogs ();
    assertNotNull (aAllChangeLogs);
    assertTrue (aAllChangeLogs.size () >= 1);
    final List <ChangeLog> aEntries = CollectionHelper.newList (aAllChangeLogs.values ());
    aEntries.add (new ChangeLog ("1.0", "aaa-first"));
    aEntries.add (new ChangeLog ("1.0", "zzz-last"));
    assertSame (aEntries, CollectionHelper.getSortedInline (aEntries, new ComparatorChangeLogComponent ()));
    assertEquals ("aaa-first", aEntries.get (0).getComponent ());
    assertEquals ("ph-commons", aEntries.get (1).getComponent ());
    assertEquals ("zzz-last", CollectionHelper.getLastElement (aEntries).getComponent ());
  }
}
