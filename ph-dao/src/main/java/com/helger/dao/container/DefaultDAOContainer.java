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

import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.string.ToStringGenerator;
import com.helger.dao.IDAO;

/**
 * The default implementation of {@link IDAOContainer} using a list of DAOs
 * provider in the constructor.
 *
 * @author Philip Helger
 */
public class DefaultDAOContainer extends AbstractDAOContainer
{
  private final ICommonsList <IDAO> m_aDAOs;

  public DefaultDAOContainer (@Nonnull @Nonempty final IDAO... aDAOs)
  {
    ValueEnforcer.notEmptyNoNullValue (aDAOs, "DAOs");
    m_aDAOs = new CommonsArrayList <> (aDAOs);
  }

  public DefaultDAOContainer (@Nonnull @Nonempty final Iterable <? extends IDAO> aDAOs)
  {
    ValueEnforcer.notEmptyNoNullValue (aDAOs, "DAOs");
    m_aDAOs = new CommonsArrayList <> (aDAOs);
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <IDAO> getAllContainedDAOs ()
  {
    return m_aRWLock.readLocked (m_aDAOs::getClone);
  }

  public boolean containsAny (@Nullable final Predicate <? super IDAO> aFilter)
  {
    return m_aRWLock.readLocked ( () -> m_aDAOs.containsAny (aFilter));
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("DAOs", m_aDAOs).getToString ();
  }
}
