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
package com.helger.commons.io;

import java.io.Writer;
import java.nio.charset.Charset;

import com.helger.annotation.Nonnull;
import com.helger.annotation.Nullable;

import com.helger.commons.io.stream.StreamHelper;

/**
 * A callback interface to retrieve {@link Writer} objects based on
 * InputStreams.
 *
 * @author Philip Helger
 */
public interface IHasOutputStreamAndWriter extends IHasOutputStream
{
  /**
   * Get a {@link Writer} based on this output stream provider using the given
   * charset.
   *
   * @param aCharset
   *        The charset to use. May not be <code>null</code>.
   * @param eAppend
   *        Appending mode. May not be <code>null</code>.
   * @return <code>null</code> if no output stream could be retrieved.
   */
  @Nullable
  default Writer getWriter (@Nonnull final Charset aCharset, @Nonnull final EAppend eAppend)
  {
    return StreamHelper.createWriter (getOutputStream (eAppend), aCharset);
  }

  /**
   * Get a buffered {@link Writer} based on this output stream provider using
   * the given charset.
   *
   * @param aCharset
   *        The charset to use. May not be <code>null</code>.
   * @param eAppend
   *        Appending mode. May not be <code>null</code>.
   * @return <code>null</code> if no output stream could be retrieved.
   * @since 9.1.8
   */
  @Nullable
  default Writer getBufferedWriter (@Nonnull final Charset aCharset, @Nonnull final EAppend eAppend)
  {
    return StreamHelper.getBuffered (getWriter (aCharset, eAppend));
  }
}
