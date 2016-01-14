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

import java.io.FilterOutputStream;
import java.io.OutputStream;

import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.string.ToStringGenerator;

/**
 * A wrapper around another {@link FilterOutputStream} to make the wrapped
 * {@link OutputStream} accessible.
 *
 * @author Philip Helger
 */
public class WrappedOutputStream extends FilterOutputStream
{
  public WrappedOutputStream (@Nonnull final OutputStream aWrappedOS)
  {
    super (ValueEnforcer.notNull (aWrappedOS, "WrappedOutputStream"));
  }

  @Nonnull
  public final OutputStream getWrappedOutputStream ()
  {
    return out;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("wrappedOS", out).toString ();
  }
}
