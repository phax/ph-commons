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

/**
 * A group of mutually related {@link Option} instances, optionally marked as
 * required.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class OptionGroup extends CommonsArrayList <Option> implements IOptionBase
{
  private boolean m_bRequired = false;

  /**
   * Default constructor creating an empty, non-required option group.
   */
  public OptionGroup ()
  {}

  /**
   * Add an option to this group by building it from the provided builder.
   *
   * @param aBuilder
   *        The option builder to build and add. May not be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public OptionGroup addOption (@NonNull final OptionBuilder aBuilder)
  {
    return addOption (aBuilder.build ());
  }

  /**
   * Add an option to this group.
   *
   * @param aOption
   *        The option to add. May not be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public OptionGroup addOption (@NonNull final Option aOption)
  {
    ValueEnforcer.notNull (aOption, "Option");
    super.add (aOption);
    return this;
  }

  /**
   * @return <code>true</code> if this option group is required (i.e. at least one of the contained
   *         options must be present), <code>false</code> otherwise.
   */
  public final boolean isRequired ()
  {
    return m_bRequired;
  }

  /**
   * Set whether this option group is required.
   *
   * @param bRequired
   *        <code>true</code> if at least one option in this group must be present,
   *        <code>false</code> otherwise.
   * @return this for chaining
   */
  @NonNull
  public final OptionGroup setRequired (final boolean bRequired)
  {
    m_bRequired = bRequired;
    return this;
  }

  /**
   * @return A mutable copy of all options contained in this group. Never <code>null</code>.
   */
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
