/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.commons.io;

import java.io.IOException;
import java.io.OutputStream;

import javax.annotation.Nonnull;
import javax.annotation.WillClose;
import javax.annotation.WillNotClose;

import com.helger.commons.io.stream.StreamHelper;

/**
 * A simple interface for objects that can write to an {@link OutputStream}.
 *
 * @author Philip Helger
 */
@FunctionalInterface
public interface IWriteToStream
{
  /**
   * Write everything to the passed output stream.
   *
   * @param aOS
   *        The output stream to write to. May not be <code>null</code>. The
   *        OutputStream must not closed by implementations of this class.
   * @throws IOException
   *         In case of IO error
   */
  void writeTo (@Nonnull @WillNotClose OutputStream aOS) throws IOException;

  /**
   * Write everything to the passed output stream and close it.
   *
   * @param aOS
   *        The output stream to write to. May not be <code>null</code>.
   * @throws IOException
   *         In case of IO error. Even than the OutputStream is closed!
   */
  default void writeToAndClose (@Nonnull @WillClose final OutputStream aOS) throws IOException
  {
    try
    {
      writeTo (aOS);
    }
    finally
    {
      StreamHelper.close (aOS);
    }
  }
}
