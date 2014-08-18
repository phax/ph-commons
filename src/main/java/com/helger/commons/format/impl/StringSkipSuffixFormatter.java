/**
 * Copyright (C) 2006-2014 phloc systems (www.phloc.com)
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.commons.format.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.format.IFormatter;

/**
 * A formatter that skips a suffix from a string.
 * 
 * @author Philip Helger
 */
public class StringSkipSuffixFormatter extends StringSkipPrefixAndSuffixFormatter
{
  public StringSkipSuffixFormatter (@Nonnull final String sSuffix)
  {
    super ("", sSuffix);
  }

  public StringSkipSuffixFormatter (@Nullable final IFormatter aNestedFormatter, @Nonnull final String sSuffix)
  {
    super (aNestedFormatter, "", sSuffix);
  }
}
