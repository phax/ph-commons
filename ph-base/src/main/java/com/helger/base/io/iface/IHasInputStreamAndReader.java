/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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

import java.io.Reader;
import java.nio.charset.Charset;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.base.io.stream.StreamHelper;

/**
 * A callback interface to retrieve {@link Reader} objects based on
 * InputStreams.
 *
 * @author Philip Helger
 */
public interface IHasInputStreamAndReader extends IHasInputStream
{
  /**
   * Get a {@link Reader} based on this input stream provider using the given
   * charset.
   *
   * @param aCharset
   *        The charset to use. May not be <code>null</code>.
   * @return <code>null</code> if no input stream could be retrieved.
   */
  @Nullable
  default Reader getReader (@NonNull final Charset aCharset)
  {
    return StreamHelper.createReader (getInputStream (), aCharset);
  }

  /**
   * Get a buffered {@link Reader} based on this input stream provider using the
   * given charset.
   *
   * @param aCharset
   *        The charset to use. May not be <code>null</code>.
   * @return <code>null</code> if no input stream could be retrieved.
   * @since 9.1.8
   */
  @Nullable
  default Reader getBufferedReader (@NonNull final Charset aCharset)
  {
    return StreamHelper.getBuffered (getReader (aCharset));
  }
}
