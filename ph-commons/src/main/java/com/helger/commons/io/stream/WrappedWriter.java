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

import java.io.FilterWriter;
import java.io.Writer;

import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.string.ToStringGenerator;

/**
 * A wrapper around another {@link FilterWriter} to make the wrapped
 * {@link Writer} accessible.
 *
 * @author Philip Helger
 */
public class WrappedWriter extends FilterWriter
{
  public WrappedWriter (@Nonnull final Writer aWrappedWriter)
  {
    super (ValueEnforcer.notNull (aWrappedWriter, "WrappedWriter"));
  }

  @Nonnull
  public final Writer getWrappedWriter ()
  {
    return out;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("wrappedWriter", out).toString ();
  }
}
