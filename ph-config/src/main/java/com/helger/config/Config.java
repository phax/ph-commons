/*
 * Copyright (C) 2014-2022 Philip Helger (www.helger.com)
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

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.impl.CommonsLinkedHashSet;
import com.helger.commons.collection.impl.ICommonsSet;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;
import com.helger.commons.text.util.TextVariableHelper;
import com.helger.config.source.IConfigurationSource;
import com.helger.config.source.MultiConfigurationValueProvider;
import com.helger.config.value.ConfiguredValue;
import com.helger.config.value.IConfigurationValueProvider;
import com.helger.config.value.IConfigurationValueProviderWithPriorityCallback;

/**
 * Default implementation of {@link IConfig}. It is recommended to use
 * {@link ConfigFactory} for accessing {@link IConfig} objects.
 *
 * @author Philip Helger
 */
public class Config implements IConfig
{
  /**
   * For backwards compatibility reason, variable replacement is disabled by
   * default.
   */
  public static final boolean DEFAULT_REPLACE_VARIABLES = false;
  public static final UnaryOperator <String> DEFAULT_UNRESOLVED_VARIABLE_PROVIDER = x -> "unresolved-var(" + x + ")";

  private static final Logger LOGGER = LoggerFactory.getLogger (Config.class);

  private final IConfigurationValueProvider m_aValueProvider;
  private BiConsumer <String, ConfiguredValue> m_aKeyFoundConsumer;
  private Consumer <String> m_aKeyNotFoundConsumer;
  private boolean m_bReplaceVariables = DEFAULT_REPLACE_VARIABLES;
  private UnaryOperator <String> m_aUnresolvedVariableProvider = DEFAULT_UNRESOLVED_VARIABLE_PROVIDER;

  /**
   * Constructor
   *
   * @param aValueProvider
   *        The main configuration value provider. May not be <code>null</code>.
   */
  public Config (@Nonnull final IConfigurationValueProvider aValueProvider)
  {
    ValueEnforcer.notNull (aValueProvider, "ValueProvider");
    m_aValueProvider = aValueProvider;
  }

  /**
   * @return The configuration value provider as provided in the constructor.
   *         Never <code>null</code>.
   */
  @Nonnull
  public final IConfigurationValueProvider getConfigurationValueProvider ()
  {
    return m_aValueProvider;
  }

  /**
   * @return The callback to be invoked if a configuration value was found. May
   *         be <code>null</code>.
   */
  @Nullable
  public final BiConsumer <String, ConfiguredValue> getFoundKeyConsumer ()
  {
    return m_aKeyFoundConsumer;
  }

  /**
   * @param aKeyFoundConsumer
   *        The callback to be invoked if a configuration value was found. The
   *        parameters are key and value. May be <code>null</code>.
   * @return this for chaining
   * @since 9.4.5
   */
  @Nullable
  public final Config setFoundKeyConsumer (@Nullable final BiConsumer <String, ConfiguredValue> aKeyFoundConsumer)
  {
    m_aKeyFoundConsumer = aKeyFoundConsumer;
    return this;
  }

  /**
   * @return The callback to be invoked if a configuration value was <b>not</b>
   *         found. May be <code>null</code>.
   */
  @Nullable
  public final Consumer <String> getKeyNotFoundConsumer ()
  {
    return m_aKeyNotFoundConsumer;
  }

  /**
   * @param aKeyNotFoundConsumer
   *        The callback to be invoked if a configuration value was <b>not</b>
   *        found. The parameter is the key. May be <code>null</code>.
   * @return this for chaining
   * @since 9.4.5
   */
  @Nullable
  public final Config setKeyNotFoundConsumer (@Nullable final Consumer <String> aKeyNotFoundConsumer)
  {
    m_aKeyNotFoundConsumer = aKeyNotFoundConsumer;
    return this;
  }

  /**
   * @return <code>true</code> if variables in configuration properties should
   *         be replaced, <code>false</code> if not. The default value is
   *         {@value #DEFAULT_REPLACE_VARIABLES}.
   * @since 10.2.0
   */
  public final boolean isReplaceVariables ()
  {
    return m_bReplaceVariables;
  }

  /**
   * Enable or disable the replacement of variables in configuration values.
   *
   * @param bReplaceVariables
   *        <code>true</code> to enable replacement, <code>false</code> to
   *        disable it.
   * @return this for chaining
   * @since 10.2.0
   */
  @Nonnull
  public final Config setReplaceVariables (final boolean bReplaceVariables)
  {
    m_bReplaceVariables = bReplaceVariables;
    return this;
  }

  /**
   * @return The unresolved variable provider to be used. Never
   *         <code>null</code>.
   */
  @Nonnull
  public final UnaryOperator <String> getUnresolvedVariableProvider ()
  {
    return m_aUnresolvedVariableProvider;
  }

  /**
   * Set the handler to be invoked when a variable could not be resolved.
   *
   * @param aUnresolvedVariableProvider
   *        The unresolved variable provider to be used. May not be
   *        <code>null</code>.
   * @return this for chaining
   * @since 10.2.0
   */
  @Nonnull
  public final Config setUnresolvedVariableProvider (@Nonnull final UnaryOperator <String> aUnresolvedVariableProvider)
  {
    ValueEnforcer.notNull (aUnresolvedVariableProvider, "UnresolvedVariableProvider");
    m_aUnresolvedVariableProvider = aUnresolvedVariableProvider;
    return this;
  }

  @Nullable
  public ConfiguredValue getConfiguredValue (@Nullable final String sKey)
  {
    // Resolve value
    final ConfiguredValue ret;
    if (StringHelper.hasNoText (sKey))
      ret = null;
    else
      ret = m_aValueProvider.getConfigurationValue (sKey);

    // Call consumers if configured
    if (ret != null)
    {
      if (m_aKeyFoundConsumer != null)
        m_aKeyFoundConsumer.accept (sKey, ret);
    }
    else
    {
      if (m_aKeyNotFoundConsumer != null)
        m_aKeyNotFoundConsumer.accept (sKey);
    }
    return ret;
  }

  @Nonnull
  @Nonempty
  private String _getWithVariablesReplacedRecursive (@Nonnull @Nonempty final String sConfiguredValue,
                                                     @Nonnull final ICommonsSet <String> aUsedVarContainer)
  {
    final UnaryOperator <String> aVarProvider = sVarName -> {
      if (!aUsedVarContainer.add (sVarName))
      {
        // Variable is used more then once
        throw new IllegalStateException ("Found a variable cyclic dependency: " +
                                         StringHelper.imploder ()
                                                     .source (aUsedVarContainer, y -> '"' + y + '"')
                                                     .separator (" -> ")
                                                     .build () +
                                         " -> \"" +
                                         sVarName +
                                         '"');
      }

      // First time usage of variable name
      final ConfiguredValue aCV = getConfiguredValue (sVarName);
      if (aCV == null)
      {
        // Failed to resolve variable
        if (LOGGER.isDebugEnabled ())
          LOGGER.debug ("Failed to resolve configuration variable '" + sVarName + "'");

        return m_aUnresolvedVariableProvider.apply (sVarName);
      }

      String sNestedConfiguredValue = aCV.getValue ();
      if (StringHelper.hasText (sNestedConfiguredValue))
      {
        // Recursive call
        sNestedConfiguredValue = _getWithVariablesReplacedRecursive (sNestedConfiguredValue, aUsedVarContainer);
      }

      // Remove the variable again, because resolution worked so far
      aUsedVarContainer.remove (sVarName);

      return sNestedConfiguredValue;
    };

    // Main replacement with
    return TextVariableHelper.getWithReplacedVariables (sConfiguredValue, aVarProvider);
  }

  @Nullable
  public String getValue (@Nullable final String sKey)
  {
    final ConfiguredValue aCV = getConfiguredValue (sKey);
    if (aCV == null)
      return null;

    String sConfiguredValue = aCV.getValue ();
    if (m_bReplaceVariables && StringHelper.hasText (sConfiguredValue))
    {
      if (LOGGER.isDebugEnabled ())
        LOGGER.debug ("Resolving variables in configuration value '" + sConfiguredValue + "'");

      try
      {
        sConfiguredValue = _getWithVariablesReplacedRecursive (sConfiguredValue, new CommonsLinkedHashSet <> ());
      }
      catch (final IllegalStateException ex)
      {
        // Handle exception only on top-level
        if (LOGGER.isErrorEnabled ())
          LOGGER.error ("Failed to replace variables in configuration value '" +
                        sConfiguredValue +
                        "': " +
                        ex.getMessage ());
      }
    }
    return sConfiguredValue;
  }

  private static void _forEachConfigurationValueProviderRecursive (@Nonnull final IConfigurationValueProvider aValueProvider,
                                                                   final int nParentPriority,
                                                                   @Nonnull final IConfigurationValueProviderWithPriorityCallback aCallback)
  {
    if (aValueProvider instanceof MultiConfigurationValueProvider)
    {
      final MultiConfigurationValueProvider aMulti = (MultiConfigurationValueProvider) aValueProvider;
      // Descend recursively
      aMulti.forEachConfigurationValueProvider ( (cvp,
                                                  prio) -> _forEachConfigurationValueProviderRecursive (cvp,
                                                                                                        prio,
                                                                                                        aCallback));
    }
    else
    {
      // By default no priority
      int nPriority = nParentPriority;
      if (nPriority < 0 && aValueProvider instanceof IConfigurationSource)
      {
        // Top-level configuration source
        final IConfigurationSource aSource = (IConfigurationSource) aValueProvider;
        nPriority = aSource.getPriority ();
      }
      aCallback.onConfigurationValueProvider (aValueProvider, nPriority);
    }
  }

  public static void forEachConfigurationValueProviderRecursive (@Nonnull final IConfigurationValueProvider aValueProvider,
                                                                 @Nonnull final IConfigurationValueProviderWithPriorityCallback aCallback)
  {
    _forEachConfigurationValueProviderRecursive (aValueProvider, -1, aCallback);
  }

  public void forEachConfigurationValueProvider (@Nonnull final IConfigurationValueProviderWithPriorityCallback aCallback)
  {
    ValueEnforcer.notNull (aCallback, "Callback");
    forEachConfigurationValueProviderRecursive (m_aValueProvider, aCallback);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("ValueProvider", m_aValueProvider)
                                       .append ("KeyFoundConsumer", m_aKeyFoundConsumer)
                                       .append ("KeyNotFoundConsumer", m_aKeyNotFoundConsumer)
                                       .append ("ReplaceVariables", m_bReplaceVariables)
                                       .append ("UnresolvedVariableProvider", m_aUnresolvedVariableProvider)
                                       .getToString ();
  }

  @Nonnull
  public static Config create (@Nonnull final IConfigurationValueProvider aCVP)
  {
    return new Config (aCVP);
  }
}
