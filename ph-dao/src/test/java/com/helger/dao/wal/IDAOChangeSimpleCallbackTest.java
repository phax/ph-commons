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
package com.helger.dao.wal;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.dao.mock.MockDAOItem;

/**
 * Test class for the default methods of {@link IDAOChangeSimpleCallback}.
 *
 * @author Philip Helger
 */
public final class IDAOChangeSimpleCallbackTest
{
  @Test
  public void testAllActionsAreDelegatedToOnChange ()
  {
    final ICommonsList <String> aEvents = new CommonsArrayList <> ();
    final IDAOChangeSimpleCallback <MockDAOItem> aCB = aItem -> aEvents.add (aItem.getID ());

    final MockDAOItem aItem = new MockDAOItem ("id1", "name1");
    aCB.onCreateItem (aItem);
    aCB.onUpdateItem (aItem);
    aCB.onDeleteItem (aItem);
    aCB.onMarkItemDeleted (aItem);
    aCB.onMarkItemUndeleted (aItem);

    assertEquals (new CommonsArrayList <> ("id1", "id1", "id1", "id1", "id1"), aEvents);
  }
}
