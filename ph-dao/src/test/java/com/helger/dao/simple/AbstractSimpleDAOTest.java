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
package com.helger.dao.simple;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.jspecify.annotations.NonNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.helger.base.state.EChange;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.dao.DAOException;
import com.helger.dao.IDAO.EMode;
import com.helger.io.file.FileOperations;
import com.helger.io.file.SimpleFileIO;
import com.helger.io.relative.FileRelativeIO;
import com.helger.io.relative.IFileRelativeIO;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroDocument;
import com.helger.xml.serialize.write.IXMLWriterSettings;

/**
 * Test class for class {@link AbstractSimpleDAO} covering the read and write paths.
 *
 * @author Philip Helger
 */
public final class AbstractSimpleDAOTest
{
  private static final File BASE_PATH = new File ("target/junittest-simpledao").getAbsoluteFile ();
  private static final String FILENAME = "values.xml";
  private static final String ELEMENT_ROOT = "root";
  private static final String ELEMENT_ITEM = "item";
  private static final String ATTR_VALUE = "value";

  /**
   * A simple DAO holding a list of Strings.
   *
   * @author Philip Helger
   */
  private static final class MockValueDAO extends AbstractSimpleDAO
  {
    private final ICommonsList <String> m_aValues = new CommonsArrayList <> ();
    private boolean m_bInitWithDefault = false;
    private int m_nModifyWriteData = 0;
    private int m_nBeforeWriteToFile = 0;
    private boolean m_bReadReportsChange = false;
    private int m_nFilenameChanges = 0;

    MockValueDAO (@NonNull final IFileRelativeIO aIO, final String sFilename)
    {
      super (aIO, () -> sFilename);
    }

    MockValueDAO initWithDefault (final boolean b) throws DAOException
    {
      m_bInitWithDefault = b;
      initialRead ();
      return this;
    }

    @Override
    @NonNull
    protected EChange onInit ()
    {
      if (!m_bInitWithDefault)
        return EChange.UNCHANGED;
      m_aValues.add ("default");
      return EChange.CHANGED;
    }

    @Override
    @NonNull
    protected EChange onRead (@NonNull final IMicroDocument aDoc)
    {
      for (final IMicroElement eItem : aDoc.getDocumentElement ().getAllChildElements (ELEMENT_ITEM))
        m_aValues.add (eItem.getAttributeValue (ATTR_VALUE));
      return m_bReadReportsChange ? EChange.CHANGED : EChange.UNCHANGED;
    }

    MockValueDAO readReportsChange (final boolean b)
    {
      m_bReadReportsChange = b;
      return this;
    }

    int getFilenameChangeCount ()
    {
      return m_nFilenameChanges;
    }

    @Override
    @NonNull
    protected IMicroDocument createWriteData ()
    {
      final IMicroDocument aDoc = new MicroDocument ();
      final IMicroElement eRoot = aDoc.addElement (ELEMENT_ROOT);
      for (final String sValue : m_aValues)
        eRoot.addElement (ELEMENT_ITEM).setAttribute (ATTR_VALUE, sValue);
      return aDoc;
    }

    @Override
    protected void modifyWriteData (@NonNull final IMicroDocument aDoc)
    {
      super.modifyWriteData (aDoc);
      m_nModifyWriteData++;
    }

    @Override
    protected void beforeWriteToFile (@NonNull final String sFilename, @NonNull final File aFile)
    {
      super.beforeWriteToFile (sFilename, aFile);
      m_nBeforeWriteToFile++;
    }

    @Override
    @NonNull
    protected IXMLWriterSettings getXMLWriterSettings ()
    {
      return super.getXMLWriterSettings ();
    }

    @Override
    protected void onFilenameChange (final String sPreviousFilename, @NonNull final String sNewFilename)
    {
      super.onFilenameChange (sPreviousFilename, sNewFilename);
      m_nFilenameChanges++;
    }

    int getModifyWriteDataCount ()
    {
      return m_nModifyWriteData;
    }

    int getBeforeWriteToFileCount ()
    {
      return m_nBeforeWriteToFile;
    }

    void addValue (@NonNull final String sValue)
    {
      m_aRWLock.writeLocked (() -> {
        m_aValues.add (sValue);
        markAsChanged ();
      });
    }

    @NonNull
    ICommonsList <String> getAllValues ()
    {
      return m_aRWLock.readLockedGet (m_aValues::getClone);
    }

    File safeFile (final String sFilename, final EMode eMode) throws DAOException
    {
      return getSafeFile (sFilename, eMode);
    }
  }

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
  public void testInitWithoutChange () throws DAOException
  {
    final MockValueDAO aDAO = new MockValueDAO (m_aIO, FILENAME).initWithDefault (false);
    assertTrue (aDAO.getAllValues ().isEmpty ());
    assertEquals (1, aDAO.getInitCount ());
    assertNotNull (aDAO.getLastInitDateTime ());
    assertEquals (0, aDAO.getReadCount ());
    assertEquals (0, aDAO.getWriteCount ());
    // onInit returned UNCHANGED, so no file is written
    assertFalse (new File (BASE_PATH, FILENAME).exists ());
    assertNotNull (aDAO.toString ());
    assertSame (m_aIO, aDAO.getIO ());
    assertNotNull (aDAO.getFilenameProvider ());
  }

  @Test
  public void testInitWithChangeWritesFile () throws DAOException
  {
    final MockValueDAO aDAO = new MockValueDAO (m_aIO, FILENAME).initWithDefault (true);
    assertEquals (new CommonsArrayList <> ("default"), aDAO.getAllValues ());
    assertEquals (1, aDAO.getWriteCount ());
    assertNotNull (aDAO.getLastWriteDateTime ());
    assertEquals (FILENAME, aDAO.getLastFilename ());
    assertTrue (new File (BASE_PATH, FILENAME).exists ());
    assertFalse (aDAO.hasPendingChanges ());
  }

  @Test
  public void testWriteAndReadBack () throws DAOException
  {
    final MockValueDAO aDAO = new MockValueDAO (m_aIO, FILENAME).initWithDefault (false);
    aDAO.addValue ("v1");
    aDAO.addValue ("v2");
    assertTrue (new File (BASE_PATH, FILENAME).exists ());
    assertEquals (2, aDAO.getWriteCount ());

    // Read it again
    final MockValueDAO aDAO2 = new MockValueDAO (m_aIO, FILENAME).initWithDefault (false);
    assertEquals (new CommonsArrayList <> ("v1", "v2"), aDAO2.getAllValues ());
    assertEquals (1, aDAO2.getReadCount ());
    assertNotNull (aDAO2.getLastReadDateTime ());
    assertEquals (0, aDAO2.getInitCount ());
  }

  @Test
  public void testWithoutFilename () throws DAOException
  {
    // A null filename means "no persistence" - this is used for testing
    final MockValueDAO aDAO = new MockValueDAO (m_aIO, null).initWithDefault (true);
    assertEquals (new CommonsArrayList <> ("default"), aDAO.getAllValues ());
    assertEquals (1, aDAO.getInitCount ());
    assertEquals (0, aDAO.getWriteCount ());
    assertNull (aDAO.getLastFilename ());

    // Changes are kept in memory only
    aDAO.addValue ("v1");
    assertEquals (2, aDAO.getAllValues ().size ());
  }

  @Test
  public void testAutoSaveHandling () throws DAOException
  {
    final MockValueDAO aDAO = new MockValueDAO (m_aIO, FILENAME).initWithDefault (false);
    assertTrue (aDAO.isAutoSaveEnabled ());

    aDAO.performWithoutAutoSave (() -> {
      assertFalse (aDAO.isAutoSaveEnabled ());
      aDAO.addValue ("v1");
      assertTrue (aDAO.hasPendingChanges ());
    });
    assertFalse (aDAO.hasPendingChanges ());
    assertEquals (1, aDAO.getWriteCount ());

    // Nothing pending anymore
    aDAO.writeToFileOnPendingChanges ();
    assertEquals (1, aDAO.getWriteCount ());
  }

  @Test
  public void testGetSafeFile () throws DAOException
  {
    final MockValueDAO aDAO = new MockValueDAO (m_aIO, FILENAME).initWithDefault (false);
    assertNotNull (aDAO.safeFile (FILENAME, EMode.READ));
    assertNotNull (aDAO.safeFile (FILENAME, EMode.WRITE));

    // A directory is not a valid target
    FileOperations.createDirIfNotExisting (new File (BASE_PATH, "adir"));
    try
    {
      aDAO.safeFile ("adir", EMode.READ);
      fail ();
    }
    catch (final DAOException ex)
    {
      // expected
    }
  }

  @Test
  public void testTemplateMethodsAreInvoked () throws DAOException
  {
    final MockValueDAO aDAO = new MockValueDAO (m_aIO, FILENAME).initWithDefault (false);
    assertEquals (0, aDAO.getModifyWriteDataCount ());
    assertEquals (0, aDAO.getBeforeWriteToFileCount ());
    assertNotNull (aDAO.getXMLWriterSettings ());

    aDAO.addValue ("v1");
    assertEquals (1, aDAO.getModifyWriteDataCount ());
    assertEquals (1, aDAO.getBeforeWriteToFileCount ());

    aDAO.addValue ("v2");
    assertEquals (2, aDAO.getModifyWriteDataCount ());
    assertEquals (2, aDAO.getBeforeWriteToFileCount ());
  }

  @Test
  public void testReadReportingChangeRewritesFile () throws DAOException
  {
    // Prepare an existing file
    final MockValueDAO aDAO = new MockValueDAO (m_aIO, FILENAME).initWithDefault (false);
    aDAO.addValue ("v1");
    assertTrue (new File (BASE_PATH, FILENAME).exists ());

    // The second DAO reports a change while reading, so the file is written
    // again right away
    final MockValueDAO aDAO2 = new MockValueDAO (m_aIO, FILENAME).readReportsChange (true).initWithDefault (false);
    assertEquals (new CommonsArrayList <> ("v1"), aDAO2.getAllValues ());
    assertEquals (1, aDAO2.getReadCount ());
    assertEquals (1, aDAO2.getWriteCount ());
    // The pending changes were reset by the initial read
    assertFalse (aDAO2.hasPendingChanges ());
  }

  @Test
  public void testFilenameChangeIsReported () throws DAOException
  {
    final MockValueDAO aDAO = new MockValueDAO (m_aIO, FILENAME).initWithDefault (false);
    assertEquals (0, aDAO.getFilenameChangeCount ());

    aDAO.addValue ("v1");
    assertEquals (FILENAME, aDAO.getLastFilename ());
    // The first write establishes the filename
    assertEquals (1, aDAO.getFilenameChangeCount ());

    aDAO.addValue ("v2");
    // The filename did not change, so no further notification
    assertEquals (1, aDAO.getFilenameChangeCount ());
  }

  @Test
  public void testGetSafeFileAccessRights () throws DAOException
  {
    final MockValueDAO aDAO = new MockValueDAO (m_aIO, FILENAME).initWithDefault (false);

    final File aReadOnly = new File (BASE_PATH, "readonly.xml");
    SimpleFileIO.writeFile (aReadOnly, "<root />", java.nio.charset.StandardCharsets.ISO_8859_1);
    assertTrue (aReadOnly.setWritable (false, false));
    try
    {
      // Reading is fine
      assertNotNull (aDAO.safeFile ("readonly.xml", EMode.READ));
      try
      {
        aDAO.safeFile ("readonly.xml", EMode.WRITE);
        fail ();
      }
      catch (final DAOException ex)
      {
        // expected
      }
    }
    finally
    {
      aReadOnly.setWritable (true, false);
    }
  }

  @Test
  public void testInvalidParams ()
  {
    try
    {
      new MockValueDAO (null, FILENAME);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
  }
}
