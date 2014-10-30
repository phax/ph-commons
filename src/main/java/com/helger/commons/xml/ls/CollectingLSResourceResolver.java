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
package com.helger.commons.xml.ls;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

import com.helger.commons.annotations.ReturnsMutableCopy;
import com.helger.commons.collections.ContainerHelper;

/**
 * A class that collects all requested resources.
 *
 * @author Philip Helger
 */
@ThreadSafe
public class CollectingLSResourceResolver implements LSResourceResolver
{
  private final ReadWriteLock m_aRWLock = new ReentrantReadWriteLock ();
  @GuardedBy ("m_aRWLock")
  private final List <LSResourceData> m_aList = new ArrayList <LSResourceData> ();
  private final LSResourceResolver m_aWrappedResourceResolver;

  public CollectingLSResourceResolver ()
  {
    this (null);
  }

  public CollectingLSResourceResolver (@Nullable final LSResourceResolver aWrappedResourceResolver)
  {
    m_aWrappedResourceResolver = aWrappedResourceResolver;
  }

  @Nullable
  public LSResourceResolver getWrappedResourceResolver ()
  {
    return m_aWrappedResourceResolver;
  }

  @Nonnull
  @ReturnsMutableCopy
  public List <LSResourceData> getAllRequestedResources ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return ContainerHelper.newList (m_aList);
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Nullable
  public LSInput resolveResource (@Nullable final String sType,
                                  @Nullable final String sNamespaceURI,
                                  @Nullable final String sPublicId,
                                  @Nullable final String sSystemId,
                                  @Nullable final String sBaseURI)
  {
    final LSResourceData aData = new LSResourceData (sType, sNamespaceURI, sPublicId, sSystemId, sBaseURI);
    m_aRWLock.writeLock ().lock ();
    try
    {
      m_aList.add (aData);
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }

    // Pass to parent (if available)
    return m_aWrappedResourceResolver == null ? null : m_aWrappedResourceResolver.resolveResource (sType,
                                                                                                   sNamespaceURI,
                                                                                                   sPublicId,
                                                                                                   sSystemId,
                                                                                                   sBaseURI);
  }
}
