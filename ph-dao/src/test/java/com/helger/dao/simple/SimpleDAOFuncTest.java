/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.annotation.Nonnull;

import org.junit.Test;

import com.helger.commons.io.relative.FileRelativeIO;
import com.helger.commons.state.EChange;
import com.helger.dao.DAOException;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.MicroDocument;

/**
 * Test class for class {@link AbstractSimpleDAO}.
 *
 * @author Philip Helger
 */
public final class SimpleDAOFuncTest
{
  private static final class MockSimpleDAO extends AbstractSimpleDAO
  {
    public MockSimpleDAO (final String sFilename) throws DAOException
    {
      super (FileRelativeIO.createForCurrentDir (), () -> sFilename);
      initialRead ();
    }

    @Override
    @Nonnull
    protected EChange onRead (@Nonnull final IMicroDocument aDoc)
    {
      return EChange.UNCHANGED;
    }

    @Override
    @Nonnull
    protected IMicroDocument createWriteData ()
    {
      final MicroDocument aDoc = new MicroDocument ();
      return aDoc;
    }
  }

  @Test
  public void testBasic () throws DAOException
  {
    final MockSimpleDAO aDAO = new MockSimpleDAO ("notexisting.xml");
    assertNotNull (aDAO.getLastInitDateTime ());
    assertEquals (1, aDAO.getInitCount ());
    assertNull (aDAO.getLastReadDateTime ());
    assertEquals (0, aDAO.getReadCount ());
    assertNull (aDAO.getLastWriteDateTime ());
    assertEquals (0, aDAO.getWriteCount ());
  }
}
