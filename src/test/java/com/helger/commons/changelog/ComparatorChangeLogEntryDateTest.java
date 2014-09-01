/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.helger.commons.collections.ContainerHelper;

/**
 * Test class for class {@link ComparatorChangeLogEntryDate}.
 * 
 * @author Philip Helger
 */
public final class ComparatorChangeLogEntryDateTest
{
  @Test
  public void testAll ()
  {
    final List <ChangeLogEntry> aEntries = new ArrayList <ChangeLogEntry> ();
    for (final ChangeLog aCL : ChangeLogSerializer.readAllChangeLogs ().values ())
      aEntries.addAll (aCL.getAllEntries ());
    assertSame (aEntries, ContainerHelper.getSortedInline (aEntries, new ComparatorChangeLogEntryDate ()));
    assertTrue (ContainerHelper.getFirstElement (aEntries).getDate ().getTime () < ContainerHelper.getLastElement (aEntries)
                                                                                                  .getDate ()
                                                                                                  .getTime ());
  }
}
