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
package com.helger.commons.name;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import com.helger.commons.collections.CollectionHelper;
import com.helger.commons.compare.ESortOrder;
import com.helger.commons.mock.AbstractPHTestCase;

/**
 * Test class for class {@link CollatingComparatorHasDisplayText}.
 *
 * @author Philip Helger
 */
public final class CollatingComparatorHasDisplayTextTest extends AbstractPHTestCase
{
  @Test
  public void testAll ()
  {
    final List <MockHasDisplayText> aList = CollectionHelper.newList (MockHasDisplayText.createDE_EN ("de1", "en3"),
                                                                      MockHasDisplayText.createDE_EN ("de2", "en2"),
                                                                      MockHasDisplayText.createDE_EN ("de3", "en1"));
    List <MockHasDisplayText> l2 = CollectionHelper.getSorted (aList,
                                                               new CollatingComparatorHasDisplayText <IHasDisplayText> (L_DE,
                                                                                                                        L_DE));
    assertEquals (3, l2.size ());
    assertEquals ("de1", l2.get (0).getDisplayText (L_DE));
    assertEquals ("de2", l2.get (1).getDisplayText (L_DE));
    assertEquals ("de3", l2.get (2).getDisplayText (L_DE));
    assertEquals ("en3", l2.get (0).getDisplayText (L_EN));
    assertEquals ("en2", l2.get (1).getDisplayText (L_EN));
    assertEquals ("en1", l2.get (2).getDisplayText (L_EN));

    l2 = CollectionHelper.getSorted (aList, new CollatingComparatorHasDisplayText <IHasDisplayText> (L_DE, L_EN));
    assertEquals (3, l2.size ());
    assertEquals ("de3", l2.get (0).getDisplayText (L_DE));
    assertEquals ("de2", l2.get (1).getDisplayText (L_DE));
    assertEquals ("de1", l2.get (2).getDisplayText (L_DE));
    assertEquals ("en1", l2.get (0).getDisplayText (L_EN));
    assertEquals ("en2", l2.get (1).getDisplayText (L_EN));
    assertEquals ("en3", l2.get (2).getDisplayText (L_EN));

    l2 = CollectionHelper.getSorted (aList,
                                     new CollatingComparatorHasDisplayText <IHasDisplayText> (L_DE, L_DE).setSortOrder (ESortOrder.DESCENDING));
    assertEquals (3, l2.size ());
    assertEquals ("de3", l2.get (0).getDisplayText (L_DE));
    assertEquals ("de2", l2.get (1).getDisplayText (L_DE));
    assertEquals ("de1", l2.get (2).getDisplayText (L_DE));
    assertEquals ("en1", l2.get (0).getDisplayText (L_EN));
    assertEquals ("en2", l2.get (1).getDisplayText (L_EN));
    assertEquals ("en3", l2.get (2).getDisplayText (L_EN));

    l2 = CollectionHelper.getSorted (aList,
                                     new CollatingComparatorHasDisplayText <IHasDisplayText> (L_DE, L_EN).setSortOrder (ESortOrder.DESCENDING));
    assertEquals (3, l2.size ());
    assertEquals ("de1", l2.get (0).getDisplayText (L_DE));
    assertEquals ("de2", l2.get (1).getDisplayText (L_DE));
    assertEquals ("de3", l2.get (2).getDisplayText (L_DE));
    assertEquals ("en3", l2.get (0).getDisplayText (L_EN));
    assertEquals ("en2", l2.get (1).getDisplayText (L_EN));
    assertEquals ("en1", l2.get (2).getDisplayText (L_EN));

    try
    {
      new CollatingComparatorHasDisplayText <IHasDisplayText> (L_DE, null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      new CollatingComparatorHasDisplayText <IHasDisplayText> (L_DE, L_EN).setSortOrder (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }
}
