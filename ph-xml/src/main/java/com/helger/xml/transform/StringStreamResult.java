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
package com.helger.xml.transform;

import javax.xml.transform.stream.StreamResult;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.io.nonblocking.NonBlockingStringWriter;
import com.helger.base.tostring.ToStringGenerator;

/**
 * Special {@link StreamResult} implementation that writes to {@link String}
 * objects.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class StringStreamResult extends StreamResult
{
  private final NonBlockingStringWriter m_aSW;

  /**
   * Default constructor.
   */
  public StringStreamResult ()
  {
    this (null);
  }

  /**
   * Constructor with a system ID.
   *
   * @param sSystemID
   *        The system ID. May be <code>null</code>.
   */
  public StringStreamResult (@Nullable final String sSystemID)
  {
    super (new NonBlockingStringWriter ());
    m_aSW = (NonBlockingStringWriter) getWriter ();
    setSystemId (sSystemID);
  }

  /**
   * @return The underlying string writer. Never <code>null</code>.
   */
  @NonNull
  public NonBlockingStringWriter getStringWriter ()
  {
    return m_aSW;
  }

  /**
   * @return The current content as a String. Never <code>null</code>.
   */
  @NonNull
  public String getAsString ()
  {
    return m_aSW.getAsString ();
  }

  /**
   * @return The current content as a char array. Never <code>null</code>.
   */
  @ReturnsMutableCopy
  public char @NonNull [] getAsCharArray ()
  {
    return m_aSW.getAsCharArray ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("stringWriter", m_aSW).append ("systemID", getSystemId ()).getToString ();
  }
}
