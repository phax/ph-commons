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
package com.helger.base.io.stream;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.jspecify.annotations.NonNull;

import com.helger.base.io.nonblocking.NonBlockingByteArrayInputStream;

/**
 * An input stream based on an input String.
 *
 * @author Philip Helger
 */
public class StringInputStream extends NonBlockingByteArrayInputStream
{
  public StringInputStream (@NonNull final String sInput, @NonNull final Charset aCharset)
  {
    super (sInput.getBytes (aCharset));
  }

  /**
   * Shortcut factory method for a UTF-8 based {@link StringInputStream}.
   *
   * @param sInput
   *        The input String. May not be <code>null</code>.
   * @return Never <code>null</code>.
   * @since 10.1.5
   */
  @NonNull
  public static StringInputStream utf8 (@NonNull final String sInput)
  {
    return new StringInputStream (sInput, StandardCharsets.UTF_8);
  }
}
