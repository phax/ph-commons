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

import java.io.File;
import java.time.Duration;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import com.helger.dao.DAOException;
import com.helger.dao.mock.MockDAOItem;
import com.helger.dao.mock.MockMapBasedWALDAO;
import com.helger.io.file.FileOperations;
import com.helger.io.relative.FileRelativeIO;
import com.helger.io.relative.IFileRelativeIO;
import com.helger.scope.mock.ScopeTestRule;

/**
 * Test class for the write ahead log handling of {@link AbstractWALDAO}.
 *
 * @author Philip Helger
 */
public final class AbstractWALDAOWALTest
{
  private static final File BASE_PATH = new File ("target/junittest-wal").getAbsoluteFile ();
  private static final String FILENAME = "items.xml";
  private static final String WAL_FILENAME = FILENAME + ".wal";

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
  public void testWALFileIsWritten () throws DAOException
  {
    final MockMapBasedWALDAO aDAO = new MockMapBasedWALDAO (m_aIO, FILENAME);
    // Long enough so that the scheduled write does not happen during the test
    aDAO.setWaitingTime (Duration.ofHours (1));
    assertEquals (Duration.ofHours (1), aDAO.getWaitingTime ());

    aDAO.createItem (new MockDAOItem ("id1", "name1"));

    // The WAL file is written, the real file is not yet
    assertTrue (new File (BASE_PATH, WAL_FILENAME).exists ());
    assertFalse (new File (BASE_PATH, FILENAME).exists ());
    assertTrue (aDAO.hasPendingChanges ());
    assertEquals (0, aDAO.getWriteCount ());
  }

  @Test
  public void testWALRecoveryOnStartup () throws DAOException
  {
    final MockMapBasedWALDAO aDAO = new MockMapBasedWALDAO (m_aIO, FILENAME);
    aDAO.setWaitingTime (Duration.ofHours (1));

    aDAO.createItem (new MockDAOItem ("id1", "name1"));
    aDAO.createItem (new MockDAOItem ("id2", "name2"));
    aDAO.updateItem ("id1", "name1b");
    aDAO.deleteItem ("id2");
    assertTrue (new File (BASE_PATH, WAL_FILENAME).exists ());

    // A new DAO instance must recover the pending changes from the WAL file
    final MockMapBasedWALDAO aDAO2 = new MockMapBasedWALDAO (m_aIO, FILENAME);
    aDAO2.setWaitingTime (Duration.ZERO);
    assertEquals (1, aDAO2.size ());
    assertNotNull (aDAO2.getItemOfID ("id1"));
    assertEquals ("name1b", aDAO2.getItemOfID ("id1").getName ());
    assertNull (aDAO2.getItemOfID ("id2"));

    // After the recovery the WAL file is gone and the real file is present
    assertFalse (new File (BASE_PATH, WAL_FILENAME).exists ());
    assertTrue (new File (BASE_PATH, FILENAME).exists ());
  }

  @Test
  public void testScheduledWriteFlushesWAL () throws DAOException, InterruptedException
  {
    final MockMapBasedWALDAO aDAO = new MockMapBasedWALDAO (m_aIO, FILENAME);
    aDAO.setWaitingTime (Duration.ofMillis (50));

    aDAO.createItem (new MockDAOItem ("id1", "name1"));

    // The WAL file is written first
    final File aWALFile = new File (BASE_PATH, WAL_FILENAME);
    final File aRealFile = new File (BASE_PATH, FILENAME);
    assertTrue (aWALFile.exists ());

    // Wait at most 30 seconds until the scheduled writer wrote the real file
    // AND removed the WAL file again
    for (int i = 0; i < 300 && (!aRealFile.exists () || aWALFile.exists ()); ++i)
      Thread.sleep (100);

    assertTrue ("The scheduled write did not happen", aRealFile.exists ());
    assertFalse ("The WAL file was not deleted", aWALFile.exists ());
    assertTrue (aDAO.getWriteCount () > 0);
    assertFalse (aDAO.hasPendingChanges ());
  }

  @Test
  public void testWALListenerSingleton ()
  {
    final WALListener aListener = WALListener.getInstance ();
    assertNotNull (aListener);
    // Always the same instance within one global scope
    assertEquals (aListener, WALListener.getInstance ());
    assertNotNull (aListener.toString ());
  }
}
