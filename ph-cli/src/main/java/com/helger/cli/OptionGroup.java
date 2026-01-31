/*
 * Original copyright by Apache Software Foundation
 * Copyright (C) 2017-2026 Philip Helger (www.helger.com)
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
package com.helger.cli;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;

@NotThreadSafe
public class OptionGroup extends CommonsArrayList <Option> implements IOptionBase
{
  private boolean m_bRequired = false;

  public OptionGroup ()
  {}

  @NonNull
  public OptionGroup addOption (@NonNull final OptionBuilder aBuilder)
  {
    return addOption (aBuilder.build ());
  }

  @NonNull
  public OptionGroup addOption (@NonNull final Option aOption)
  {
    ValueEnforcer.notNull (aOption, "Option");
    super.add (aOption);
    return this;
  }

  public final boolean isRequired ()
  {
    return m_bRequired;
  }

  @NonNull
  public final OptionGroup setRequired (final boolean bRequired)
  {
    m_bRequired = bRequired;
    return this;
  }

  @NonNull
  @ReturnsMutableCopy
  public ICommonsList <Option> getAllOptions ()
  {
    return getClone ();
  }

  @Override
  public boolean equals (final Object o)
  {
    if (this == o)
      return true;
    if (o == null || getClass () != o.getClass ())
      return false;

    return super.equals (o);
  }

  @Override
  public int hashCode ()
  {
    return super.hashCode ();
  }
}
