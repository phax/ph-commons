/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.ls.LSInput;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.concurrent.SimpleReadWriteLock;

/**
 * A class that collects all requested resources.
 *
 * @author Philip Helger
 */
@ThreadSafe
public class CollectingLSResourceResolver extends AbstractLSResourceResolver
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (CollectingLSResourceResolver.class);

  private final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();
  @GuardedBy ("m_aRWLock")
  private final List <LSResourceData> m_aList = new ArrayList <LSResourceData> ();

  public CollectingLSResourceResolver ()
  {}

  @Nonnull
  @ReturnsMutableCopy
  public List <LSResourceData> getAllRequestedResources ()
  {
    return m_aRWLock.readLocked ( () -> CollectionHelper.newList (m_aList));
  }

  @Override
  @Nullable
  public LSInput mainResolveResource (@Nullable final String sType,
                                      @Nullable final String sNamespaceURI,
                                      @Nullable final String sPublicId,
                                      @Nullable final String sSystemId,
                                      @Nullable final String sBaseURI)
  {
    if (DEBUG_RESOLVE)
      s_aLogger.info ("mainResolveResource (" +
                      sType +
                      ", " +
                      sNamespaceURI +
                      ", " +
                      sPublicId +
                      ", " +
                      sSystemId +
                      ", " +
                      sBaseURI +
                      ")");

    final LSResourceData aData = new LSResourceData (sType, sNamespaceURI, sPublicId, sSystemId, sBaseURI);
    m_aRWLock.writeLocked ( () -> m_aList.add (aData));
    return null;
  }
}
