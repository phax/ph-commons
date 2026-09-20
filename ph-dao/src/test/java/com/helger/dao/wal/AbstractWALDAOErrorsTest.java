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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import com.helger.dao.AbstractDAO;
import com.helger.dao.DAOException;
import com.helger.dao.mock.MockDAOItem;
import com.helger.dao.mock.MockMapBasedWALDAO;
import com.helger.io.file.FileOperations;
import com.helger.io.file.SimpleFileIO;
import com.helger.io.relative.FileRelativeIO;
import com.helger.io.relative.IFileRelativeIO;
import com.helger.scope.mock.ScopeTestRule;

/**
 * Test class for the error handling and the silent mode of {@link AbstractWALDAO}.
 *
 * @author Philip Helger
 */
public final class AbstractWALDAOErrorsTest
{
  private static final File BASE_PATH = new File ("target/junittest-wal-errors").getAbsoluteFile ();
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
  public void cleanup ()
  {
    AbstractDAO.exceptionHandlersRead ().removeAll ();
    AbstractDAO.setSilentMode (false);
    FileOperations.deleteDirRecursiveIfExisting (BASE_PATH);
  }

  @Test
  public void testSilentMode ()
  {
    assertFalse (AbstractDAO.setSilentMode (true));
    assertTrue (AbstractDAO.isSilentMode ());
    assertTrue (AbstractDAO.setSilentMode (false));
    assertFalse (AbstractDAO.isSilentMode ());
  }

  @Test
  public void testExceptionHandlerLists ()
  {
    assertNotNull (AbstractDAO.exceptionHandlersRead ());
    assertNotNull (AbstractDAO.exceptionHandlersWrite ());
  }

  @Test
  public void testReadInvalidXML () throws DAOException
  {
    // Not valid XML at all
    SimpleFileIO.writeFile (new File (BASE_PATH, FILENAME), "this is not XML", StandardCharsets.ISO_8859_1);

    final AtomicInteger aExCount = new AtomicInteger (0);
    AbstractDAO.exceptionHandlersRead ().add ((t, bInit, aFile) -> aExCount.incrementAndGet ());

    // The DAO is created, but nothing was read
    final MockMapBasedWALDAO aDAO = new MockMapBasedWALDAO (m_aIO, FILENAME);
    aDAO.setWaitingTime (Duration.ZERO);
    assertTrue (aDAO.isEmpty ());
    assertEquals (0, aDAO.getReadCount ());
  }

  @Test
  public void testGetSafeFileOnDirectory ()
  {
    FileOperations.createDirRecursiveIfNotExisting (new File (BASE_PATH, "adir"));

    // A directory cannot be used as a DAO file
    try
    {
      new MockMapBasedWALDAO (m_aIO, "adir");
      fail ();
    }
    catch (final DAOException | IllegalStateException ex)
    {
      // expected
    }
  }

  @Test
  public void testNullFilename () throws DAOException
  {
    // A null filename means the DAO cannot persist anything
    final MockMapBasedWALDAO aDAO = new MockMapBasedWALDAO (m_aIO, null);
    aDAO.setWaitingTime (Duration.ZERO);
    assertTrue (aDAO.isEmpty ());

    aDAO.createItem (new MockDAOItem ("id1", "name1"));
    assertEquals (1, aDAO.size ());
    assertEquals (0, aDAO.getWriteCount ());
    assertNotNull (aDAO.toString ());
  }

  @Test
  public void testExistingNewFileIsRejected () throws DAOException
  {
    // A left over ".new" file means a previous write did not complete
    SimpleFileIO.writeFile (new File (BASE_PATH, FILENAME + ".new"), "<root />", StandardCharsets.ISO_8859_1);
    try
    {
      new MockMapBasedWALDAO (m_aIO, FILENAME);
      fail ();
    }
    catch (final IllegalStateException ex)
    {
      // expected
    }
  }

  @Test
  public void testGetWaitingTimeDefault () throws DAOException
  {
    final MockMapBasedWALDAO aDAO = new MockMapBasedWALDAO (m_aIO, FILENAME);
    assertEquals (AbstractWALDAO.DEFAULT_WAITING_TIME, aDAO.getWaitingTime ());

    try
    {
      aDAO.setWaitingTime (null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
  }
}
