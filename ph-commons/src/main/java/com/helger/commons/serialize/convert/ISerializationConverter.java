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
package com.helger.commons.serialize.convert;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.annotation.Nonnull;

/**
 * Interface to be implemented to read and write objects.
 *
 * @author Philip Helger
 */
public interface ISerializationConverter
{
  /**
   * Write the passed source object to the passed {@link ObjectOutputStream}.
   *
   * @param aSourceObject
   *        The source object to write. Never <code>null</code>.
   * @param aOOS
   *        The output stream to write to. Never <code>null</code>.
   * @throws IOException
   *         In case of a stream error
   */
  void writeConvertedObject (@Nonnull Object aSourceObject, @Nonnull ObjectOutputStream aOOS) throws IOException;

  /**
   * Read the object from the specified {@link ObjectInputStream}.
   *
   * @param aOIS
   *        The object input stream to read from. Never <code>null</code>.
   * @return The read object. May not be <code>null</code>.
   * @throws IOException
   *         In case of a stream error
   */
  @Nonnull
  Object readConvertedObject (@Nonnull ObjectInputStream aOIS) throws IOException;
}
