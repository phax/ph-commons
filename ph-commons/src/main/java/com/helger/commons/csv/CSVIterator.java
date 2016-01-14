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
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;

/**
 * Provides an Iterator over the data found in opencsv.
 */
public class CSVIterator implements Iterator <List <String>>
{
  private final CSVReader m_aReader;
  private List <String> m_aNextLine;

  /**
   * @param aReader
   *        reader for the csv data. May not be <code>null</code>.
   * @throws IOException
   *         if unable to read data from the reader.
   */
  public CSVIterator (@Nonnull final CSVReader aReader) throws IOException
  {
    ValueEnforcer.notNull (aReader, "Reader");
    m_aReader = aReader;
    m_aNextLine = aReader.readNext ();
  }

  /**
   * Returns <code>true</code> if the iteration has more elements. In other
   * words, returns <code>true</code> if next() would return an element rather
   * than throwing an exception.
   *
   * @return <code>true</code> if this {@link CSVIterator} has more elements.
   */
  public boolean hasNext ()
  {
    return m_aNextLine != null;
  }

  /**
   * Returns the next element in the iterator.
   *
   * @return The next element of the iterator. Never <code>null</code>.
   */
  @Nonnull
  public List <String> next ()
  {
    final List <String> ret = m_aNextLine;
    try
    {
      m_aNextLine = m_aReader.readNext ();
    }
    catch (final IOException ex)
    {
      throw new RuntimeException ("Failed to read next CSV line", ex);
    }
    return ret;
  }

  /**
   * This method is not supported by openCSV and will throw a
   * UnsupportedOperationException if called.
   */
  public void remove ()
  {
    throw new UnsupportedOperationException ("This is a read-only CSV iterator.");
  }
}
