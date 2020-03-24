/**
 * Copyright (C) 2014-2020 Philip Helger (www.helger.com)
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
package com.helger.config.source;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;

/**
 * An implementation of {@link IConfigurationValueProvider} that supports
 * multiple sources, ordered by priority, descending.
 *
 * @author Philip Helger
 */
public class MultiConfigurationSourceValueProvider implements IConfigurationValueProvider
{
  private final ICommonsList <IConfigurationSource> m_aSources = new CommonsArrayList <> ();

  public MultiConfigurationSourceValueProvider ()
  {}

  public MultiConfigurationSourceValueProvider (@Nullable final Iterable <? extends IConfigurationSource> aSources)
  {
    if (aSources != null)
      for (final IConfigurationSource aSource : aSources)
        addConfigurationSource (aSource);
  }

  public MultiConfigurationSourceValueProvider (@Nullable final IConfigurationSource... aSources)
  {
    if (aSources != null)
      for (final IConfigurationSource aSource : aSources)
        addConfigurationSource (aSource);
  }

  public final void addConfigurationSource (@Nonnull final IConfigurationSource aSource)
  {
    ValueEnforcer.notNull (aSource, "ConfigSource");
    m_aSources.add (aSource);
    // Ensure entry with highest priority comes first
    m_aSources.sort ( (x, y) -> y.getPriority () - x.getPriority ());
  }

  /**
   * @return A copy of all configuration sources in the order they are executed.
   *         Never <code>null</code> but maybe empty.
   */
  @Nonnull
  public ICommonsList <IConfigurationSource> getAllSources ()
  {
    return m_aSources.getClone ();
  }

  @Nullable
  public String getConfigurationValue (@Nonnull @Nonempty final String sKey)
  {
    String ret = null;
    for (final IConfigurationSource aSource : m_aSources)
    {
      ret = aSource.getConfigurationValue (sKey);
      if (ret != null)
      {
        // Use the first one that is not null
        break;
      }
    }
    return ret;
  }
}
