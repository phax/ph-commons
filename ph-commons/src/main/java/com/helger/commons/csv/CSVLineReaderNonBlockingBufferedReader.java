/*
 * Copyright (C) 2014-2024 Philip Helger (www.helger.com)
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
package com.helger.commons.csv;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillNotClose;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.io.stream.NonBlockingBufferedReader;

/**
 * A special implementation of {@link ICSVLineReader} using a
 * {@link NonBlockingBufferedReader}.
 *
 * @author Philip Helger
 */
public class CSVLineReaderNonBlockingBufferedReader implements ICSVLineReader
{
  private final NonBlockingBufferedReader m_aReader;

  /**
   * Constructor.
   *
   * @param aReader
   *        Reader that data will be read from. May not be <code>null</code>.
   *        Will not be closed in this class.
   */
  public CSVLineReaderNonBlockingBufferedReader (@Nonnull @WillNotClose final NonBlockingBufferedReader aReader)
  {
    m_aReader = ValueEnforcer.notNull (aReader, "Reader");
  }

  @Nullable
  public String readLine () throws IOException
  {
    return m_aReader.readLine ();
  }
}
