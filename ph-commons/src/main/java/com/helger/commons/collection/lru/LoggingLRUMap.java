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
package com.helger.commons.collection.lru;

import java.util.Map;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.annotation.UseDirectEqualsAndHashCode;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * A specific {@link LRUMap} that emits a warning once the map is full and the
 * oldest entry gets discarded.
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        Map key type
 * @param <VALUETYPE>
 *        Map value type
 */
@NotThreadSafe
@UseDirectEqualsAndHashCode
public class LoggingLRUMap <KEYTYPE, VALUETYPE> extends LRUMap <KEYTYPE, VALUETYPE>
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (LoggingLRUMap.class);

  private String m_sMapName;

  public LoggingLRUMap (@Nonnegative final int nMaxSize)
  {
    super (nMaxSize);
  }

  @Nullable
  public String getMapName ()
  {
    return m_sMapName;
  }

  @Nonnull
  public LoggingLRUMap <KEYTYPE, VALUETYPE> setMapName (@Nullable final String sMapName)
  {
    m_sMapName = sMapName;
    return this;
  }

  @Override
  protected void onRemoveEldestEntry (@Nonnegative final int nSize,
                                      @Nonnull final Map.Entry <KEYTYPE, VALUETYPE> aEntry)
  {
    s_aLogger.warn ("Map" +
                    (m_sMapName != null ? " '" + m_sMapName + "'" : "") +
                    " is full with " +
                    nSize +
                    " ≥ " +
                    getMaxSize () +
                    " items! Removed key (" +
                    aEntry.getKey () +
                    ") and value (" +
                    aEntry.getValue () +
                    ")");
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (!super.equals (o))
      return false;
    final LoggingLRUMap <?, ?> rhs = (LoggingLRUMap <?, ?>) o;
    return EqualsHelper.equals (m_sMapName, rhs.m_sMapName);
  }

  @Override
  public int hashCode ()
  {
    return HashCodeGenerator.getDerived (super.hashCode ()).append (m_sMapName).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("MapName", m_sMapName).toString ();
  }
}
