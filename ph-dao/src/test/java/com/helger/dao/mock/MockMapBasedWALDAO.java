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
package com.helger.dao.mock;

import java.time.Duration;
import java.util.function.Predicate;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.base.state.EChange;
import com.helger.collection.commons.ICommonsList;
import com.helger.dao.DAOException;
import com.helger.dao.wal.AbstractMapBasedWALDAO;
import com.helger.io.relative.IFileRelativeIO;

/**
 * A concrete {@link AbstractMapBasedWALDAO} for testing purposes.
 *
 * @author Philip Helger
 */
public final class MockMapBasedWALDAO extends AbstractMapBasedWALDAO <MockDAOItem, MockDAOItem>
{
  public MockMapBasedWALDAO (@NonNull final IFileRelativeIO aIO, @Nullable final String sFilename) throws DAOException
  {
    super (MockDAOItem.class, aIO, sFilename, new InitSettings <MockDAOItem> ().setOrderedMapSupplier ());
  }

  public MockMapBasedWALDAO (@NonNull final IFileRelativeIO aIO,
                             @Nullable final String sFilename,
                             @NonNull final InitSettings <MockDAOItem> aInitSettings) throws DAOException
  {
    super (MockDAOItem.class, aIO, sFilename, aInitSettings);
  }

  @Override
  public void setWaitingTime (@NonNull final Duration aWaitingTime)
  {
    super.setWaitingTime (aWaitingTime);
  }

  @NonNull
  public MockDAOItem createItem (@NonNull final MockDAOItem aItem)
  {
    m_aRWLock.writeLocked (() -> internalCreateItem (aItem));
    return aItem;
  }

  public void updateItem (@NonNull final String sID, @NonNull final String sNewName)
  {
    m_aRWLock.writeLocked (() -> {
      final MockDAOItem aItem = internalGetOfID (sID);
      aItem.setName (sNewName);
      internalUpdateItem (aItem);
    });
  }

  @Nullable
  public MockDAOItem deleteItem (@Nullable final String sID)
  {
    return m_aRWLock.writeLockedGet (() -> internalDeleteItem (sID));
  }

  @NonNull
  public EChange removeAll ()
  {
    return m_aRWLock.writeLockedGet (this::internalRemoveAllItemsNoCallback);
  }

  @Nullable
  public MockDAOItem getItemOfID (@Nullable final String sID)
  {
    return getOfID (sID);
  }

  public void markItemDeleted (@NonNull final String sID)
  {
    m_aRWLock.writeLocked (() -> internalMarkItemDeleted (internalGetOfID (sID)));
  }

  public void markItemUndeleted (@NonNull final String sID)
  {
    m_aRWLock.writeLocked (() -> internalMarkItemUndeleted (internalGetOfID (sID)));
  }

  @Nullable
  public MockDAOItem getAtIdx (final int nIndex)
  {
    return getAtIndex (nIndex);
  }

  public boolean containsID (@Nullable final String sID)
  {
    return m_aRWLock.readLockedBoolean (() -> internalContainsWithID (sID));
  }

  @NonNull
  public MockDAOItem createItemNoCallback (@NonNull final MockDAOItem aItem)
  {
    m_aRWLock.writeLocked (() -> internalCreateItem (aItem, false));
    return aItem;
  }

  public void updateItemNoCallback (@NonNull final String sID, @NonNull final String sNewName)
  {
    m_aRWLock.writeLocked (() -> {
      final MockDAOItem aItem = internalGetOfID (sID);
      aItem.setName (sNewName);
      internalUpdateItem (aItem, false);
    });
  }

  @Nullable
  public MockDAOItem deleteItemNoCallback (@Nullable final String sID)
  {
    return m_aRWLock.writeLockedGet (() -> internalDeleteItem (sID, false));
  }

  public void markItemDeletedNoCallback (@NonNull final String sID)
  {
    m_aRWLock.writeLocked (() -> internalMarkItemDeleted (internalGetOfID (sID), false));
  }

  public void markItemUndeletedNoCallback (@NonNull final String sID)
  {
    m_aRWLock.writeLocked (() -> internalMarkItemUndeleted (internalGetOfID (sID), false));
  }

  @NonNull
  public ICommonsList <MockDAOItem> getAllFilteredImpl (@Nullable final Predicate <? super MockDAOItem> aFilter)
  {
    return m_aRWLock.readLockedGet (() -> internalGetAll (aFilter));
  }

  public int countDirect ()
  {
    return m_aRWLock.readLockedInt (() -> {
      int n = 0;
      for (final MockDAOItem aItem : internalDirectGetAll ())
      {
        assert aItem != null;
        n++;
      }
      return n;
    });
  }
}
