/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.base.io.iface;

import java.io.IOException;
import java.io.Writer;

import com.helger.annotation.WillClose;
import com.helger.annotation.WillNotClose;
import com.helger.base.io.stream.StreamHelper;

import jakarta.annotation.Nonnull;

/**
 * A simple interface for objects that can write to a {@link Writer}.
 *
 * @author Philip Helger
 */
@FunctionalInterface
public interface IWriteToWriter
{
  /**
   * Write everything to the passed writer.
   *
   * @param aWriter
   *        The writer to write to. May not be <code>null</code>. The writer must not closed by
   *        implementations of this class.
   * @throws IOException
   *         In case of IO error
   */
  void writeTo (@Nonnull @WillNotClose Writer aWriter) throws IOException;

  /**
   * Write everything to the passed writer and close it.
   *
   * @param aWriter
   *        The writer to write to. May not be <code>null</code>.
   * @throws IOException
   *         In case of IO error. Even than the writer is closed!
   */
  default void writeToAndClose (@Nonnull @WillClose final Writer aWriter) throws IOException
  {
    try
    {
      writeTo (aWriter);
    }
    finally
    {
      StreamHelper.close (aWriter);
    }
  }
}
