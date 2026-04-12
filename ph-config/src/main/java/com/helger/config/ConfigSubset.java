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
package com.helger.config;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.string.StringHelper;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.config.value.ConfiguredValue;
import com.helger.config.value.IConfigurationValueProvider;
import com.helger.config.value.IConfigurationValueProviderWithPriorityCallback;

/**
 * A subset view of an existing {@link IConfig} that automatically prepends a key prefix to all
 * lookups. This allows passing a scoped configuration to subsystems without them needing to know
 * their own prefix.
 * <p>
 * Example: given a config with keys <code>db.host</code>, <code>db.port</code>, calling
 * <code>config.getSubConfig ("db")</code> returns a view where <code>getAsString ("host")</code>
 * resolves <code>db.host</code>.
 * <p>
 * Sub-configs are nestable: <code>config.getSubConfig ("a").getSubConfig ("b")</code> produces a
 * view with effective prefix <code>a.b.</code>.
 * <p>
 * Variable substitution, callbacks, and reload all delegate to the parent config — this is a thin
 * view, not a copy.
 *
 * @author Philip Helger
 * @since 12.1.6
 */
public class ConfigSubset implements IConfig
{
  private final IConfig m_aParent;
  private final String m_sPrefix;

  /**
   * Constructor.
   *
   * @param aParent
   *        The parent config to delegate to. May not be <code>null</code>.
   * @param sPrefix
   *        The key prefix. May not be <code>null</code> or empty. A trailing dot is optional — it
   *        will be ensured automatically (e.g. both <code>"db"</code> and <code>"db."</code> result
   *        in the prefix <code>"db."</code>).
   */
  public ConfigSubset (@NonNull final IConfig aParent, @NonNull @Nonempty final String sPrefix)
  {
    ValueEnforcer.notNull (aParent, "Parent");
    ValueEnforcer.notEmpty (sPrefix, "Prefix");

    m_aParent = aParent;
    // Ensure the prefix always ends with exactly one dot
    if (StringHelper.endsWith (sPrefix, '.'))
      m_sPrefix = sPrefix;
    else
      m_sPrefix = sPrefix + ".";
  }

  /**
   * @return The parent config this subset delegates to. Never <code>null</code>.
   */
  @NonNull
  public final IConfig getParent ()
  {
    return m_aParent;
  }

  /**
   * @return The key prefix including the trailing dot (e.g. <code>"db."</code>). Never
   *         <code>null</code>.
   */
  @NonNull
  @Nonempty
  public final String getPrefix ()
  {
    return m_sPrefix;
  }

  @NonNull
  protected final String getPrefixed (@NonNull final String sKey)
  {
    return m_sPrefix + sKey;
  }

  @Override
  @Nullable
  public Object getValue (@Nullable final String sKey)
  {
    return sKey == null ? null : m_aParent.getValue (getPrefixed (sKey));
  }

  @Override
  @NonNull
  public IConfigurationValueProvider getConfigurationValueProvider ()
  {
    return m_aParent.getConfigurationValueProvider ();
  }

  @Override
  public boolean containsConfiguredValue (@Nullable final String sKey)
  {
    return sKey != null && m_aParent.containsConfiguredValue (getPrefixed (sKey));
  }

  @Override
  @Nullable
  public ConfiguredValue getConfiguredValue (@Nullable final String sKey)
  {
    return sKey == null ? null : m_aParent.getConfiguredValue (getPrefixed (sKey));
  }

  @Override
  public void forEachConfigurationValueProvider (@NonNull final IConfigurationValueProviderWithPriorityCallback aCallback)
  {
    m_aParent.forEachConfigurationValueProvider (aCallback);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Parent", m_aParent).append ("Prefix", m_sPrefix).getToString ();
  }
}
