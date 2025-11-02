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
package com.helger.io.file;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.base.string.StringHelper;

/**
 * Represents a set of predefined open modes for RandomAccessFile objects.
 *
 * @author Philip Helger
 */
public enum ERandomAccessFileMode
{
  /** Open for reading only. */
  READ_ONLY ("r"),
  /**
   * Open for reading and writing. If the file does not already exist then an attempt will be made
   * to create it.
   */
  READ_WRITE ("rw"),
  /**
   * Open for reading and writing, as with <code>"rw"</code>, and also require that every update to
   * the file's content or metadata be written synchronously to the underlying storage device.
   */
  READ_WRITE_SYNCHRONOUSLY ("rws"),
  /**
   * Open for reading and writing, as with <code>"rw"</code>, and also require that every update to
   * the file's content be written synchronously to the underlying storage device.
   */
  READ_WRITE_SYNCHRONOUSLY_ONLY_CONTENT ("rwd");

  private final String m_sMode;

  ERandomAccessFileMode (@NonNull @Nonempty final String sMode)
  {
    m_sMode = sMode;
  }

  @NonNull
  @Nonempty
  public String getMode ()
  {
    return m_sMode;
  }

  @Nullable
  public static ERandomAccessFileMode getFromModeOrNull (@Nullable final String sMode)
  {
    if (StringHelper.isNotEmpty (sMode))
      for (final ERandomAccessFileMode e : values ())
        if (e.getMode ().equals (sMode))
          return e;
    return null;
  }
}
