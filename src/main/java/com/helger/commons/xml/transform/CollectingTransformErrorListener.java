/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.commons.xml.transform;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import javax.xml.transform.ErrorListener;

import com.helger.commons.annotations.ReturnsMutableCopy;
import com.helger.commons.error.IHasResourceErrorGroup;
import com.helger.commons.error.IResourceError;
import com.helger.commons.error.IResourceErrorGroup;
import com.helger.commons.error.ResourceErrorGroup;
import com.helger.commons.state.EChange;
import com.helger.commons.string.ToStringGenerator;

/**
 * This implementation of {@link javax.xml.transform.ErrorListener} saves all
 * occurred warnings/errors/fatals in a list for later evaluation.
 * 
 * @author Philip Helger
 */
@ThreadSafe
public class CollectingTransformErrorListener extends AbstractTransformErrorListener implements IHasResourceErrorGroup
{
  protected final ReadWriteLock m_aRWLock = new ReentrantReadWriteLock ();
  private final ResourceErrorGroup m_aErrors = new ResourceErrorGroup ();

  public CollectingTransformErrorListener ()
  {
    super ();
  }

  public CollectingTransformErrorListener (@Nullable final ErrorListener aWrappedErrorListener)
  {
    super (aWrappedErrorListener);
  }

  @Override
  protected void internalLog (@Nonnull final IResourceError aResError)
  {
    m_aRWLock.writeLock ().lock ();
    try
    {
      m_aErrors.addResourceError (aResError);
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  @Nonnull
  @ReturnsMutableCopy
  public IResourceErrorGroup getResourceErrors ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return m_aErrors.getClone ();
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  /**
   * Clear all currently stored errors.
   * 
   * @return {@link EChange#CHANGED} if at least one item was cleared.
   */
  @Nonnull
  public EChange clearResourceErrors ()
  {
    m_aRWLock.writeLock ().lock ();
    try
    {
      return m_aErrors.clear ();
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  @Override
  public String toString ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return ToStringGenerator.getDerived (super.toString ()).append ("errors", m_aErrors).toString ();
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }
}
