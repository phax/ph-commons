/**
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
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

import java.io.OutputStream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.io.stream.StreamHelper;

/**
 * A callback interface to retrieve {@link OutputStream} objects.
 *
 * @author Philip Helger
 */
public interface IHasOutputStream
{
  /**
   * Get the output stream to read from the object. Each time this method is
   * call, a new {@link OutputStream} needs to be created!
   *
   * @param eAppend
   *        appending mode. May not be <code>null</code>.
   * @return <code>null</code> if resolving failed.
   */
  @Nullable
  OutputStream getOutputStream (@Nonnull EAppend eAppend);

  /**
   * Get the output stream to read from the object. Each time this method is
   * call, a new {@link OutputStream} needs to be created. Internally invokes
   * {@link #getOutputStream(EAppend)}.
   *
   * @param eAppend
   *        appending mode. May not be <code>null</code>.
   * @return <code>null</code> if resolving failed.
   * @since 9.1.8
   */
  @Nullable
  default OutputStream getBufferedOutputStream (@Nonnull final EAppend eAppend)
  {
    final OutputStream aOS = getOutputStream (eAppend);
    return aOS == null ? null : StreamHelper.getBuffered (aOS);
  }

  /**
   * Check if the {@link OutputStream} from {@link #getOutputStream(EAppend)} or
   * {@link #getBufferedOutputStream(EAppend)} can be acquired more than once or
   * not.
   *
   * @return <code>true</code> if the output stream can be acquired more than
   *         once, <code>false</code> if not.
   */
  boolean isWriteMultiple ();
}
