/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.config.fallback;

import java.math.BigDecimal;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.config.ConfigSubset;
import com.helger.config.value.ConfiguredValue;

/**
 * A subset view of an existing {@link IConfigWithFallback} that automatically prepends a key prefix
 * to all lookups, including fallback key resolution. This is the fallback-aware counterpart to
 * {@link ConfigSubset}.
 * <p>
 * All fallback methods prefix both the primary key and all alternative (old) keys before delegating
 * to the parent. This preserves the parent's outdated key notifier behavior.
 *
 * @author Philip Helger
 * @since 12.1.6
 */
public class ConfigSubsetWithFallback extends ConfigSubset implements IConfigWithFallback
{
  private final IConfigWithFallback m_aFallbackParent;

  /**
   * Constructor.
   *
   * @param aParent
   *        The parent config with fallback support to delegate to. May not be <code>null</code>.
   * @param sPrefix
   *        The key prefix. May not be <code>null</code> or empty. A trailing dot is optional.
   */
  public ConfigSubsetWithFallback (@NonNull final IConfigWithFallback aParent, @NonNull @Nonempty final String sPrefix)
  {
    super (aParent, sPrefix);
    m_aFallbackParent = ValueEnforcer.notNull (aParent, "Parent");
  }

  @NonNull
  private String [] _getAllPrefixed (@NonNull final String @NonNull [] aKeys)
  {
    final String [] ret = new String [aKeys.length];
    for (int i = 0; i < aKeys.length; i++)
      ret[i] = getPrefixed (aKeys[i]);
    return ret;
  }

  @Override
  @Nullable
  public ConfiguredValue getConfiguredValueOrFallback (@NonNull final String sPrimary,
                                                       @NonNull @Nonempty final String @NonNull... aOldOnes)
  {
    return m_aFallbackParent.getConfiguredValueOrFallback (getPrefixed (sPrimary), _getAllPrefixed (aOldOnes));
  }

  @Override
  @Nullable
  public String getAsStringOrFallback (@NonNull final String sPrimary,
                                       @NonNull @Nonempty final String @NonNull... aOldOnes)
  {
    return m_aFallbackParent.getAsStringOrFallback (getPrefixed (sPrimary), _getAllPrefixed (aOldOnes));
  }

  @Override
  public char @Nullable [] getAsCharArrayOrFallback (@NonNull final String sPrimary,
                                                     @NonNull @Nonempty final String @NonNull... aOldOnes)
  {
    return m_aFallbackParent.getAsCharArrayOrFallback (getPrefixed (sPrimary), _getAllPrefixed (aOldOnes));
  }

  @Override
  @Nullable
  public BigDecimal getAsBigDecimalOrFallback (@NonNull final String sPrimary,
                                               @NonNull @Nonempty final String @NonNull... aOldOnes)
  {
    return m_aFallbackParent.getAsBigDecimalOrFallback (getPrefixed (sPrimary), _getAllPrefixed (aOldOnes));
  }

  @Override
  public boolean getAsBooleanOrFallback (@NonNull final String sPrimary,
                                         final boolean bDefault,
                                         @NonNull @Nonempty final String @NonNull... aOldOnes)
  {
    return m_aFallbackParent.getAsBooleanOrFallback (getPrefixed (sPrimary), bDefault, _getAllPrefixed (aOldOnes));
  }

  @Override
  public int getAsIntOrFallback (@NonNull final String sPrimary,
                                 final int nDefault,
                                 @NonNull @Nonempty final String @NonNull... aOldOnes)
  {
    return m_aFallbackParent.getAsIntOrFallback (getPrefixed (sPrimary), nDefault, _getAllPrefixed (aOldOnes));
  }

  @Override
  public long getAsLongOrFallback (@NonNull final String sPrimary,
                                   final long nDefault,
                                   @NonNull @Nonempty final String @NonNull... aOldOnes)
  {
    return m_aFallbackParent.getAsLongOrFallback (getPrefixed (sPrimary), nDefault, _getAllPrefixed (aOldOnes));
  }

  @Override
  public String toString ()
  {
    return super.toString ();
  }
}
