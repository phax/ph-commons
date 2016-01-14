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

import java.io.FilterInputStream;
import java.io.InputStream;

import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.string.ToStringGenerator;

/**
 * A wrapper around another {@link FilterInputStream} to make the wrapped
 * {@link InputStream} accessible.
 *
 * @author Philip Helger
 */
public class WrappedInputStream extends FilterInputStream
{
  public WrappedInputStream (@Nonnull final InputStream aWrappedIS)
  {
    super (ValueEnforcer.notNull (aWrappedIS, "WrappedInputStream"));
  }

  @Nonnull
  public final InputStream getWrappedInputStream ()
  {
    return in;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("wrappedIS", in).toString ();
  }
}
