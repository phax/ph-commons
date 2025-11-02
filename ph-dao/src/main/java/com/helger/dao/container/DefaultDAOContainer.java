/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
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

  public DefaultDAOContainer (@NonNull @Nonempty final IDAO... aDAOs)
  {
    ValueEnforcer.notEmptyNoNullValue (aDAOs, "DAOs");
    m_aDAOs = new CommonsArrayList <> (aDAOs);
  }

  public DefaultDAOContainer (@NonNull @Nonempty final Iterable <? extends IDAO> aDAOs)
  {
    ValueEnforcer.notEmptyNoNullValue (aDAOs, "DAOs");
    m_aDAOs = new CommonsArrayList <> (aDAOs);
  }

  @NonNull
  @ReturnsMutableCopy
  public ICommonsList <IDAO> getAllContainedDAOs ()
  {
    return m_aRWLock.readLockedGet (m_aDAOs::getClone);
  }

  public boolean containsAny (@Nullable final Predicate <? super IDAO> aFilter)
  {
    return m_aRWLock.readLockedBoolean ( () -> m_aDAOs.containsAny (aFilter));
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("DAOs", m_aDAOs).getToString ();
  }
}
