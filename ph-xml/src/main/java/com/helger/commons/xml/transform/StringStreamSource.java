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
package com.helger.commons.xml.transform;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.xml.transform.stream.StreamSource;

import com.helger.commons.io.stream.NonBlockingStringReader;
import com.helger.commons.string.ToStringGenerator;

/**
 * Special {@link StreamSource} implementation that reads from {@link String}
 * objects.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class StringStreamSource extends StreamSource
{
  public StringStreamSource (@Nonnull final char [] aInput)
  {
    super (new NonBlockingStringReader (aInput));
  }

  public StringStreamSource (@Nonnull final char [] aInput, @Nonnegative final int nOfs, @Nonnegative final int nLen)
  {
    super (new NonBlockingStringReader (aInput, nOfs, nLen));
  }

  public StringStreamSource (@Nonnull final CharSequence aInput)
  {
    this (aInput instanceof String ? (String) aInput : aInput.toString ());
  }

  public StringStreamSource (@Nonnull final String sInput)
  {
    this (sInput, null);
  }

  public StringStreamSource (@Nonnull final String sInput, @Nullable final String sSystemID)
  {
    super (new NonBlockingStringReader (sInput));
    setSystemId (sSystemID);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("systemID", getSystemId ()).toString ();
  }
}
