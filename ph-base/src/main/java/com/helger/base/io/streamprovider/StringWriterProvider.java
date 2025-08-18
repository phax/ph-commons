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
package com.helger.base.io.streamprovider;

import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.io.iface.IHasWriter;
import com.helger.base.io.nonblocking.NonBlockingStringWriter;
import com.helger.base.tostring.ToStringGenerator;

import jakarta.annotation.Nonnull;

/**
 * An {@link java.io.Writer} provider based on a {@link String}.
 *
 * @author Philip Helger
 */
public class StringWriterProvider implements IHasWriter
{
  public StringWriterProvider ()
  {}

  @Nonnull
  public final NonBlockingStringWriter getWriter ()
  {
    return new NonBlockingStringWriter ();
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;

    return true;
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).getToString ();
  }
}
