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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.time.Duration;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import com.helger.base.state.EChange;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.collection.paging.PagingSpec;
import com.helger.dao.DAOException;
import com.helger.dao.mock.MockDAOItem;
import com.helger.dao.mock.MockMapBasedWALDAO;
import com.helger.io.file.FileOperations;
import com.helger.io.relative.FileRelativeIO;
import com.helger.io.relative.IFileRelativeIO;
import com.helger.scope.mock.ScopeTestRule;

/**
 * Test class for class {@link AbstractMapBasedWALDAO}.
 *
 * @author Philip Helger
 */
public final class AbstractMapBasedWALDAOTest
{
  private static final File BASE_PATH = new File ("target/junittest-waldao").getAbsoluteFile ();
  private static final String FILENAME = "items.xml";

  @Rule
  public final TestRule m_aScopeRule = new ScopeTestRule ();

  private IFileRelativeIO m_aIO;

  @Before
  public void createBasePath ()
  {
    FileOperations.deleteDirRecursiveIfExisting (BASE_PATH);
    m_aIO = new FileRelativeIO (BASE_PATH);
  }

  @After
  public void deleteBasePath ()
  {
    FileOperations.deleteDirRecursiveIfExisting (BASE_PATH);
  }

  private MockMapBasedWALDAO _createDAO () throws DAOException
  {
    final MockMapBasedWALDAO aDAO = new MockMapBasedWALDAO (m_aIO, FILENAME);
    // Write directly instead of going through the WAL
    aDAO.setWaitingTime (Duration.ZERO);
    return aDAO;
  }

  @Test
  public void testEmptyDAO () throws DAOException
  {
    final MockMapBasedWALDAO aDAO = _createDAO ();
    assertTrue (aDAO.isEmpty ());
    assertFalse (aDAO.isNotEmpty ());
    assertEquals (0, aDAO.size ());
    assertTrue (aDAO.getAll ().isEmpty ());
    assertTrue (aDAO.getAllIDs ().isEmpty ());
    assertTrue (aDAO.getNone ().isEmpty ());
    assertNull (aDAO.getItemOfID ("does-not-exist"));
    assertNull (aDAO.getItemOfID (null));
    assertFalse (aDAO.containsWithID ("does-not-exist"));
    assertNotNull (aDAO.toString ());

    assertEquals (1, aDAO.getInitCount ());
    assertNotNull (aDAO.getLastInitDateTime ());
    assertEquals (0, aDAO.getReadCount ());
    assertNull (aDAO.getLastReadDateTime ());
  }

  @Test
  public void testCreateUpdateDelete () throws DAOException
  {
    final MockMapBasedWALDAO aDAO = _createDAO ();

    final MockDAOItem aItem = aDAO.createItem (new MockDAOItem ("id1", "name1"));
    assertEquals (1, aDAO.size ());
    assertTrue (aDAO.isNotEmpty ());
    assertTrue (aDAO.containsWithID ("id1"));
    assertSame (aItem, aDAO.getItemOfID ("id1"));
    assertEquals (new CommonsArrayList <> (aItem), aDAO.getAll ());
    assertTrue (aDAO.getWriteCount () > 0);
    assertNotNull (aDAO.getLastWriteDateTime ());

    aDAO.updateItem ("id1", "name2");
    assertEquals ("name2", aDAO.getItemOfID ("id1").getName ());

    final MockDAOItem aDeleted = aDAO.deleteItem ("id1");
    assertSame (aItem, aDeleted);
    assertTrue (aDAO.isEmpty ());

    // Deleting again returns null
    assertNull (aDAO.deleteItem ("id1"));
    assertNull (aDAO.deleteItem (null));
  }

  @Test
  public void testPersistenceRoundTrip () throws DAOException
  {
    final MockMapBasedWALDAO aDAO = _createDAO ();
    aDAO.createItem (new MockDAOItem ("id1", "name1"));
    aDAO.createItem (new MockDAOItem ("id2", "name2"));
    assertEquals (2, aDAO.size ());
    assertTrue (new File (BASE_PATH, FILENAME).exists ());

    // Read again from the very same file
    final MockMapBasedWALDAO aDAO2 = _createDAO ();
    assertEquals (2, aDAO2.size ());
    assertEquals ("name1", aDAO2.getItemOfID ("id1").getName ());
    assertEquals ("name2", aDAO2.getItemOfID ("id2").getName ());
    assertEquals (1, aDAO2.getReadCount ());
    assertNotNull (aDAO2.getLastReadDateTime ());
  }

  @Test
  public void testQueryMethods () throws DAOException
  {
    final MockMapBasedWALDAO aDAO = _createDAO ();
    aDAO.createItem (new MockDAOItem ("id1", "alpha"));
    aDAO.createItem (new MockDAOItem ("id2", "beta"));
    aDAO.createItem (new MockDAOItem ("id3", "alpha"));

    assertEquals (3, aDAO.getAll (null).size ());
    assertEquals (2, aDAO.getAll (x -> "alpha".equals (x.getName ())).size ());
    assertEquals (2, aDAO.getCount (x -> "alpha".equals (x.getName ())));
    assertEquals (3, aDAO.getCount (null));

    assertNotNull (aDAO.findFirst (x -> "beta".equals (x.getName ())));
    assertNull (aDAO.findFirst (x -> "gamma".equals (x.getName ())));
    assertEquals ("beta", aDAO.findFirstMapped (x -> "beta".equals (x.getName ()), MockDAOItem::getName));

    assertTrue (aDAO.containsAny (x -> "alpha".equals (x.getName ())));
    assertFalse (aDAO.containsAny (x -> "gamma".equals (x.getName ())));
    assertTrue (aDAO.containsNone (x -> "gamma".equals (x.getName ())));
    assertFalse (aDAO.containsOnly (x -> "alpha".equals (x.getName ())));

    assertEquals (3, aDAO.getAllMapped (null, MockDAOItem::getName).size ());
    assertEquals (2, aDAO.getAllPaged (null, new PagingSpec (1, 2), null).size ());

    assertTrue (aDAO.containsAllIDs (new CommonsArrayList <> ("id1", "id2")));
    assertFalse (aDAO.containsAllIDs (new CommonsArrayList <> ("id1", "does-not-exist")));

    final ICommonsList <String> aNames = new CommonsArrayList <> ();
    aDAO.forEachValue (x -> aNames.add (x.getName ()));
    assertEquals (3, aNames.size ());

    final ICommonsList <String> aIDs = new CommonsArrayList <> ();
    aDAO.forEachKey (aIDs::add);
    assertEquals (3, aIDs.size ());

    final ICommonsList <String> aBoth = new CommonsArrayList <> ();
    aDAO.forEach ((sID, aItem) -> aBoth.add (sID));
    assertEquals (3, aBoth.size ());

    aDAO.findAll (x -> "alpha".equals (x.getName ()), x -> aBoth.add (x.getID ()));
    assertEquals (5, aBoth.size ());

    // The filtered overloads
    final ICommonsList <String> aFiltered = new CommonsArrayList <> ();
    aDAO.forEach ((sID, aItem) -> "alpha".equals (aItem.getName ()), (sID, aItem) -> aFiltered.add (sID));
    assertEquals (2, aFiltered.size ());

    aFiltered.clear ();
    aDAO.forEachKey (sID -> "id1".equals (sID), aFiltered::add);
    assertEquals (1, aFiltered.size ());

    aFiltered.clear ();
    aDAO.forEachValue (x -> "beta".equals (x.getName ()), x -> aFiltered.add (x.getID ()));
    assertEquals (1, aFiltered.size ());

    aFiltered.clear ();
    aDAO.findAllMapped (x -> "alpha".equals (x.getName ()), MockDAOItem::getID, aFiltered::add);
    assertEquals (2, aFiltered.size ());
  }

  @Test
  public void testNoCallbackVariants () throws DAOException
  {
    final MockMapBasedWALDAO aDAO = _createDAO ();
    final ICommonsList <String> aEvents = new CommonsArrayList <> ();
    aDAO.callbacks ().add ((IDAOChangeSimpleCallback <MockDAOItem>) aItem -> aEvents.add (aItem.getID ()));

    aDAO.createItemNoCallback (new MockDAOItem ("id1", "name1"));
    aDAO.updateItemNoCallback ("id1", "name2");
    aDAO.markItemDeletedNoCallback ("id1");
    aDAO.markItemUndeletedNoCallback ("id1");
    aDAO.deleteItemNoCallback ("id1");

    // No callback was invoked at all
    assertTrue (aEvents.isEmpty ());
    assertTrue (aDAO.isEmpty ());
  }

  @Test
  public void testInternalAccessors () throws DAOException
  {
    final MockMapBasedWALDAO aDAO = _createDAO ();
    aDAO.createItem (new MockDAOItem ("id1", "alpha"));
    aDAO.createItem (new MockDAOItem ("id2", "beta"));

    assertEquals (2, aDAO.countDirect ());
    assertEquals (2, aDAO.getAllFilteredImpl (null).size ());
    assertEquals (1, aDAO.getAllFilteredImpl (x -> "beta".equals (x.getName ())).size ());
  }

  @Test
  public void testReadChangeAwareTriggersRewrite () throws DAOException
  {
    final MockMapBasedWALDAO aDAO = _createDAO ();
    aDAO.createItem (new MockDAOItem ("id1", MockDAOItem.NAME_READ_CHANGED));
    assertTrue (new File (BASE_PATH, FILENAME).exists ());

    // The item reports a change while reading, so the file is written again
    final MockMapBasedWALDAO aDAO2 = _createDAO ();
    assertEquals (1, aDAO2.size ());
    assertEquals (1, aDAO2.getReadCount ());
    assertEquals (1, aDAO2.getWriteCount ());
    assertFalse (aDAO2.hasPendingChanges ());
  }

  @Test
  public void testRemoveAll () throws DAOException
  {
    final MockMapBasedWALDAO aDAO = _createDAO ();
    aDAO.createItem (new MockDAOItem ("id1", "name1"));

    assertSame (EChange.CHANGED, aDAO.removeAll ());
    assertTrue (aDAO.isEmpty ());
    assertSame (EChange.UNCHANGED, aDAO.removeAll ());
  }

  @Test
  public void testCallbacks () throws DAOException
  {
    final MockMapBasedWALDAO aDAO = _createDAO ();
    final ICommonsList <String> aEvents = new CommonsArrayList <> ();
    // The simple callback is invoked for every action
    aDAO.callbacks ().add ((IDAOChangeSimpleCallback <MockDAOItem>) aItem -> aEvents.add ("change:" + aItem.getID ()));

    aDAO.createItem (new MockDAOItem ("id1", "name1"));
    aDAO.updateItem ("id1", "name2");
    aDAO.deleteItem ("id1");

    assertEquals (new CommonsArrayList <> ("change:id1", "change:id1", "change:id1"), aEvents);
  }

  @Test
  public void testAutoSaveHandling () throws DAOException
  {
    final MockMapBasedWALDAO aDAO = _createDAO ();
    assertTrue (aDAO.isAutoSaveEnabled ());
    assertFalse (aDAO.hasPendingChanges ());

    aDAO.performWithoutAutoSave (() -> {
      assertFalse (aDAO.isAutoSaveEnabled ());
      aDAO.createItem (new MockDAOItem ("id1", "name1"));
      assertTrue (aDAO.hasPendingChanges ());
    });

    assertTrue (aDAO.isAutoSaveEnabled ());
    assertFalse (aDAO.hasPendingChanges ());
    assertTrue (new File (BASE_PATH, FILENAME).exists ());

    // Nothing pending anymore
    aDAO.writeToFileOnPendingChanges ();
  }
}
