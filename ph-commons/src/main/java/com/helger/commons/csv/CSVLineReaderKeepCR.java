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
package com.helger.commons.csv;

import java.io.IOException;
import java.io.Reader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;

/**
 * This class was created for issue #106
 * (https://sourceforge.net/p/opencsv/bugs/106/) where carriage returns were
 * being removed. This class allows the user to determine if they wish to keep
 * or remove them from the data being read.
 *
 * @author scott on 2/19/15.
 */
public class CSVLineReaderKeepCR implements ICSVLineReader
{
  private final Reader m_aReader;

  /**
   * Constructor.
   *
   * @param aReader
   *        Reader that data will be read from. May not be <code>null</code>.
   */
  public CSVLineReaderKeepCR (@Nonnull final Reader aReader)
  {
    m_aReader = ValueEnforcer.notNull (aReader, "Reader");
  }

  @Nullable
  public String readLine () throws IOException
  {
    final StringBuilder aSB = new StringBuilder (CCSV.INITIAL_STRING_SIZE);
    for (int c = m_aReader.read (); c > -1 && c != '\n'; c = m_aReader.read ())
      aSB.append ((char) c);

    return aSB.length () > 0 ? aSB.toString () : null;
  }
}
