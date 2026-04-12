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
  private String [] _prefixAll (@NonNull final String @NonNull [] aKeys)
  {
    final String sPrefix = getPrefix ();
    final String [] ret = new String [aKeys.length];
    for (int i = 0; i < aKeys.length; i++)
      ret[i] = sPrefix + aKeys[i];
    return ret;
  }

  @Override
  @Nullable
  public ConfiguredValue getConfiguredValueOrFallback (@NonNull final String sPrimary,
                                                       @NonNull @Nonempty final String @NonNull... aOldOnes)
  {
    return m_aFallbackParent.getConfiguredValueOrFallback (getPrefix () + sPrimary, _prefixAll (aOldOnes));
  }

  @Override
  @Nullable
  public String getAsStringOrFallback (@NonNull final String sPrimary,
                                       @NonNull @Nonempty final String @NonNull... aOldOnes)
  {
    return m_aFallbackParent.getAsStringOrFallback (getPrefix () + sPrimary, _prefixAll (aOldOnes));
  }

  @Override
  public char @Nullable [] getAsCharArrayOrFallback (@NonNull final String sPrimary,
                                                     @NonNull @Nonempty final String @NonNull... aOldOnes)
  {
    return m_aFallbackParent.getAsCharArrayOrFallback (getPrefix () + sPrimary, _prefixAll (aOldOnes));
  }

  @Override
  @Nullable
  public BigDecimal getAsBigDecimalOrFallback (@NonNull final String sPrimary,
                                               @NonNull @Nonempty final String @NonNull... aOldOnes)
  {
    return m_aFallbackParent.getAsBigDecimalOrFallback (getPrefix () + sPrimary, _prefixAll (aOldOnes));
  }

  @Override
  public boolean getAsBooleanOrFallback (@NonNull final String sPrimary,
                                         final boolean bDefault,
                                         @NonNull @Nonempty final String @NonNull... aOldOnes)
  {
    return m_aFallbackParent.getAsBooleanOrFallback (getPrefix () + sPrimary, bDefault, _prefixAll (aOldOnes));
  }

  @Override
  public int getAsIntOrFallback (@NonNull final String sPrimary,
                                 final int nBogus,
                                 final int nDefault,
                                 @NonNull @Nonempty final String @NonNull... aOldOnes)
  {
    return m_aFallbackParent.getAsIntOrFallback (getPrefix () + sPrimary, nBogus, nDefault, _prefixAll (aOldOnes));
  }

  @Override
  public long getAsLongOrFallback (@NonNull final String sPrimary,
                                   final long nBogus,
                                   final long nDefault,
                                   @NonNull @Nonempty final String @NonNull... aOldOnes)
  {
    return m_aFallbackParent.getAsLongOrFallback (getPrefix () + sPrimary, nBogus, nDefault, _prefixAll (aOldOnes));
  }
}
