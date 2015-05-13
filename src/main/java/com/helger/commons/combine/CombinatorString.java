/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.commons.combine;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.string.ToStringGenerator;

/**
 * A simple combinator that simply concatenates 2 strings.
 * 
 * @author Philip Helger
 */
@Immutable
public final class CombinatorString implements ICombinator <String>
{
  private static final CombinatorString s_aInstance = new CombinatorString ();

  private CombinatorString ()
  {}

  @Nonnull
  public static CombinatorString getInstance ()
  {
    return s_aInstance;
  }

  public String getCombined (@Nullable final String sFirst, @Nullable final String sSecond)
  {
    return sFirst + sSecond;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).toString ();
  }
}
