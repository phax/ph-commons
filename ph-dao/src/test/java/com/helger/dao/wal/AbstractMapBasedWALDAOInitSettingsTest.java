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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.dao.DAOException;
import com.helger.dao.IDAO;
import com.helger.dao.mock.MockDAOItem;
import com.helger.dao.mock.MockMapBasedWALDAO;
import com.helger.io.file.FileOperations;
import com.helger.io.relative.FileRelativeIO;
import com.helger.io.relative.IFileRelativeIO;
import com.helger.scope.mock.ScopeTestRule;

/**
 * Test class for the {@link AbstractMapBasedWALDAO.InitSettings}, the mark deleted handling and the
 * default methods of {@link IDAO}.
 *
 * @author Philip Helger
 */
public final class AbstractMapBasedWALDAOInitSettingsTest
{
  private static final File BASE_PATH = new File ("target/junittest-waldao-init").getAbsoluteFile ();
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

  @Test
  public void testWithoutInitialRead () throws DAOException
  {
    // Write a file first
    final MockMapBasedWALDAO aDAO = new MockMapBasedWALDAO (m_aIO, FILENAME);
    aDAO.setWaitingTime (Duration.ZERO);
    aDAO.createItem (new MockDAOItem ("id1", "name1"));
    assertTrue (new File (BASE_PATH, FILENAME).exists ());

    // The second DAO does not read it
    final MockMapBasedWALDAO aDAO2 = new MockMapBasedWALDAO (m_aIO,
                                                             FILENAME,
                                                             new AbstractMapBasedWALDAO.InitSettings <MockDAOItem> ().setDoInitialRead (false));
    assertTrue (aDAO2.isEmpty ());
    assertEquals (0, aDAO2.getInitCount ());
    assertEquals (0, aDAO2.getReadCount ());
  }

  @Test
  public void testCustomMapSupplier () throws DAOException
  {
    final MockMapBasedWALDAO aDAO = new MockMapBasedWALDAO (m_aIO,
                                                            FILENAME,
                                                            new AbstractMapBasedWALDAO.InitSettings <MockDAOItem> ().setMapSupplier (com.helger.collection.commons.CommonsTreeMap::new));
    aDAO.setWaitingTime (Duration.ZERO);
    aDAO.createItem (new MockDAOItem ("id2", "name2"));
    aDAO.createItem (new MockDAOItem ("id1", "name1"));

    // The tree map is sorted by key
    assertEquals (new CommonsArrayList <> ("id1", "id2"), new CommonsArrayList <> (aDAO.getAllIDs ()));
    assertNotNull (aDAO.getAtIdx (0));
    assertTrue (aDAO.containsID ("id1"));
    assertFalse (aDAO.containsID ("does-not-exist"));
  }

  @Test
  public void testReadElementFilter () throws DAOException
  {
    final MockMapBasedWALDAO aDAO = new MockMapBasedWALDAO (m_aIO, FILENAME);
    aDAO.setWaitingTime (Duration.ZERO);
    aDAO.createItem (new MockDAOItem ("id1", "name1"));
    aDAO.createItem (new MockDAOItem ("id2", "name2"));

    // Only read the items whose "id" attribute is "id1"
    final MockMapBasedWALDAO aDAO2 = new MockMapBasedWALDAO (m_aIO,
                                                             FILENAME,
                                                             new AbstractMapBasedWALDAO.InitSettings <MockDAOItem> ().setReadElementFilter (e -> "id1".equals (e.getAttributeValue ("id"))));
    assertEquals (1, aDAO2.size ());
    assertNotNull (aDAO2.getItemOfID ("id1"));
    assertNull (aDAO2.getItemOfID ("id2"));
  }

  @Test
  public void testMarkDeletedAndUndeleted () throws DAOException
  {
    final MockMapBasedWALDAO aDAO = new MockMapBasedWALDAO (m_aIO, FILENAME);
    aDAO.setWaitingTime (Duration.ZERO);
    aDAO.createItem (new MockDAOItem ("id1", "name1"));

    final ICommonsList <String> aEvents = new CommonsArrayList <> ();
    aDAO.callbacks ().add (new IDAOChangeCallback <MockDAOItem> ()
    {
      public void onCreateItem (final MockDAOItem aItem)
      {
        aEvents.add ("create");
      }

      public void onUpdateItem (final MockDAOItem aItem)
      {
        aEvents.add ("update");
      }

      public void onDeleteItem (final MockDAOItem aItem)
      {
        aEvents.add ("delete");
      }

      public void onMarkItemDeleted (final MockDAOItem aItem)
      {
        aEvents.add ("markDeleted");
      }

      public void onMarkItemUndeleted (final MockDAOItem aItem)
      {
        aEvents.add ("markUndeleted");
      }
    });

    aDAO.markItemDeleted ("id1");
    aDAO.markItemUndeleted ("id1");
    assertEquals (new CommonsArrayList <> ("markDeleted", "markUndeleted"), aEvents);
    // The item is still contained
    assertEquals (1, aDAO.size ());
  }

  @Test
  public void testIDAODefaults ()
  {
    final IDAO aDAO = new IDAO ()
    {
      public boolean isAutoSaveEnabled ()
      {
        return true;
      }

      public void beginWithoutAutoSave ()
      {}

      public void endWithoutAutoSave ()
      {}

      public boolean hasPendingChanges ()
      {
        return false;
      }

      public void writeToFileOnPendingChanges ()
      {}

      public int getInitCount ()
      {
        return 0;
      }

      public LocalDateTime getLastInitDateTime ()
      {
        return null;
      }

      public int getReadCount ()
      {
        return 0;
      }

      public LocalDateTime getLastReadDateTime ()
      {
        return null;
      }

      public int getWriteCount ()
      {
        return 0;
      }

      public LocalDateTime getLastWriteDateTime ()
      {
        return null;
      }
    };

    // Not reloadable by default, so reload is a no-op
    assertFalse (aDAO.isReloadable ());
    try
    {
      aDAO.reload ();
      fail ();
    }
    catch (final UnsupportedOperationException | DAOException ex)
    {
      // expected
    }
  }

  @Test
  public void testReloadableThrows ()
  {
    final IDAO aDAO = new IDAO ()
    {
      @Override
      public boolean isReloadable ()
      {
        return true;
      }

      public boolean isAutoSaveEnabled ()
      {
        return true;
      }

      public void beginWithoutAutoSave ()
      {}

      public void endWithoutAutoSave ()
      {}

      public boolean hasPendingChanges ()
      {
        return false;
      }

      public void writeToFileOnPendingChanges ()
      {}

      public int getInitCount ()
      {
        return 0;
      }

      public LocalDateTime getLastInitDateTime ()
      {
        return null;
      }

      public int getReadCount ()
      {
        return 0;
      }

      public LocalDateTime getLastReadDateTime ()
      {
        return null;
      }

      public int getWriteCount ()
      {
        return 0;
      }

      public LocalDateTime getLastWriteDateTime ()
      {
        return null;
      }
    };

    assertTrue (aDAO.isReloadable ());
    try
    {
      aDAO.reload ();
      fail ();
    }
    catch (final UnsupportedOperationException | DAOException ex)
    {
      // expected
    }
  }
}
