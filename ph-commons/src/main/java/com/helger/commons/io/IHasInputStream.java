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

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.io.stream.StreamHelper;

/**
 * A callback interface to retrieve {@link InputStream} objects.
 *
 * @author Philip Helger
 */
public interface IHasInputStream
{
  /**
   * Get the input stream to read from the object. Each time this method is
   * called, a new {@link InputStream} needs to be created.
   *
   * @return <code>null</code> if resolving failed.
   */
  @Nullable
  InputStream getInputStream ();

  /**
   * Get a buffered input stream to read from the object. Each time this method
   * is called, a new {@link InputStream} needs to be created. Internally
   * invokes {@link #getInputStream()}.
   *
   * @return <code>null</code> if resolving failed.
   * @since 9.1.8
   */
  @Nullable
  default InputStream getBufferedInputStream ()
  {
    return StreamHelper.getBuffered (getInputStream ());
  }

  /**
   * Perform something with the {@link InputStream} of this object and making
   * sure, that it gets closed correctly afterwards.
   *
   * @param <T>
   *        The result type of handling the {@link InputStream}
   * @param aFunc
   *        The function to be invoked to read from the {@link InputStream}.
   *        This function needs to be able to deal with a
   *        <code>null</code>-parameter.
   * @return The result of the function. May be <code>null</code>.
   * @throws IOException
   *         In case reading from the InputStream fails
   * @since 11.1.5
   */
  @Nullable
  default <T> T withInputStreamDo (@Nonnull final Function <InputStream, T> aFunc) throws IOException
  {
    ValueEnforcer.notNull (aFunc, "Func");
    try (final InputStream aIS = getInputStream ())
    {
      return aFunc.apply (aIS);
    }
  }

  /**
   * Perform something with the buffered {@link InputStream} of this object and
   * making sure, that it gets closed correctly afterwards.
   *
   * @param <T>
   *        The result type of handling the buffered {@link InputStream}
   * @param aFunc
   *        The function to be invoked to read from the buffered
   *        {@link InputStream}. This function needs to be able to deal with a
   *        <code>null</code>-parameter.
   * @return The result of the function. May be <code>null</code>.
   * @throws IOException
   *         In case reading from the InputStream fails
   * @since 11.1.5
   */
  @Nullable
  default <T> T withBufferedInputStreamDo (@Nonnull final Function <InputStream, T> aFunc) throws IOException
  {
    ValueEnforcer.notNull (aFunc, "Func");
    try (final InputStream aIS = getBufferedInputStream ())
    {
      return aFunc.apply (aIS);
    }
  }

  /**
   * Check if the {@link InputStream} from {@link #getInputStream()} and
   * {@link #getBufferedInputStream()} can be acquired more than once.
   *
   * @return <code>true</code> if the input stream can be acquired more than
   *         once, <code>false</code> if not.
   */
  boolean isReadMultiple ();
}
