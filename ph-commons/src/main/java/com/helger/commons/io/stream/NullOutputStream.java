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
package com.helger.commons.io.stream;

import java.io.IOException;
import java.io.OutputStream;

/**
 * An {@link OutputStream} that discards all bytes to be written
 *
 * @author Philip Helger
 */
public class NullOutputStream extends OutputStream
{
  /**
   * A singleton.
   */
  public static final NullOutputStream NULL_OUTPUT_STREAM = new NullOutputStream ();

  /**
   * Does nothing - output to <code>/dev/null</code>.
   *
   * @param b
   *        The bytes to write
   * @param off
   *        The start offset
   * @param len
   *        The number of bytes to write
   */
  @Override
  public void write (final byte [] b, final int off, final int len)
  {
    // do nothing
  }

  /**
   * Does nothing - output to <code>/dev/null</code>.
   *
   * @param b
   *        The byte to write
   */
  @Override
  public void write (final int b)
  {
    // do nothing
  }

  /**
   * Does nothing - output to <code>/dev/null</code>.
   *
   * @param b
   *        The bytes to write
   * @throws IOException
   *         never
   */
  @Override
  public void write (final byte [] b) throws IOException
  {
    // do nothing
  }

  /**
   * Does not nothing and therefore does not throw an Exception.
   */
  @Override
  public void flush ()
  {}

  /**
   * Does not nothing and therefore does not throw an Exception.
   */
  @Override
  public void close ()
  {}
}
