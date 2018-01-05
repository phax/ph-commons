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
package com.helger.dao.container;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import javax.annotation.concurrent.ThreadSafe;

import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.concurrent.SimpleReadWriteLock;
import com.helger.dao.IDAO;

/**
 * Abstract base implementation of {@link IDAOContainer}.
 *
 * @author Philip Helger
 */
@ThreadSafe
public abstract class AbstractDAOContainer implements IDAOContainer
{
  protected final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();

  @OverridingMethodsMustInvokeSuper
  public boolean isAutoSaveEnabled ()
  {
    return m_aRWLock.readLocked ( () -> containsAny (x -> x != null && x.isAutoSaveEnabled ()));
  }

  public final void beginWithoutAutoSave ()
  {
    final ICommonsList <IDAO> aDAOs = getAllContainedDAOs ();
    m_aRWLock.writeLocked ( () -> {
      for (final IDAO aDAO : aDAOs)
        if (aDAO != null)
          aDAO.beginWithoutAutoSave ();
    });
  }

  public final void endWithoutAutoSave ()
  {
    final ICommonsList <IDAO> aDAOs = getAllContainedDAOs ();
    m_aRWLock.writeLocked ( () -> {
      for (final IDAO aDAO : aDAOs)
        if (aDAO != null)
          aDAO.endWithoutAutoSave ();
    });
  }
}
