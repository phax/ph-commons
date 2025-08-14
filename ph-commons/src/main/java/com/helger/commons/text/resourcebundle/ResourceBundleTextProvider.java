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
package com.helger.commons.text.resourcebundle;

import java.util.Locale;

import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.MustImplementEqualsAndHashcode;
import com.helger.base.equals.ValueEnforcer;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.commons.text.AbstractHasText;
import com.helger.commons.text.IHasTextWithArgs;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * This class encapsulates the resource bundle handling.
 *
 * @author Philip Helger
 */
@Immutable
@MustImplementEqualsAndHashcode
public class ResourceBundleTextProvider extends AbstractHasText implements IHasTextWithArgs
{
  private final ResourceBundleKey m_aResBundleKey;

  public ResourceBundleTextProvider (@Nonnull final String sBundleName, @Nonnull final String sKey)
  {
    this (new ResourceBundleKey (sBundleName, sKey));
  }

  public ResourceBundleTextProvider (@Nonnull final ResourceBundleKey aResBundleKey)
  {
    m_aResBundleKey = ValueEnforcer.notNull (aResBundleKey, "ResBundleKey");
  }

  @Override
  @Nullable
  protected String internalGetText (@Nonnull final Locale aContentLocale)
  {
    return m_aResBundleKey.getString (aContentLocale);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final ResourceBundleTextProvider rhs = (ResourceBundleTextProvider) o;
    return m_aResBundleKey.equals (rhs.m_aResBundleKey);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aResBundleKey).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("resBundleKey", m_aResBundleKey).getToString ();
  }
}
