/**
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
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
package com.helger.commons.lang.priviledged;

import java.net.Authenticator;
import java.net.ProxySelector;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.security.Security;
import java.util.Properties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;

/**
 * Extension to {@link PrivilegedAction} with an {@link #invokeSafe()} method
 * that invokes the {@link java.security.AccessController} only if a security
 * manager is present.
 *
 * @author Philip Helger
 * @param <T>
 *        The action return type.
 */
@FunctionalInterface
public interface IPrivilegedAction <T> extends PrivilegedAction <T>
{
  /**
   * @return The result of {@link #run()} dependent if a system manager is
   *         installed or not.
   */
  @Nullable
  default T invokeSafe ()
  {
    return invokeSafe (this);
  }

  /**
   * @param aPA
   *        The privileged action to run. May not be <code>null</code>.
   * @return The result of {@link #run()} dependent if a system manager is
   *         installed or not.
   * @param <T>
   *        The return type of the privileged action
   * @since 9.4.7
   */
  @Nullable
  static <T> T invokeSafe (@Nonnull final IPrivilegedAction <T> aPA)
  {
    if (System.getSecurityManager () == null)
    {
      // No need to use AccessController
      return aPA.run ();
    }
    return AccessControllerHelper.call (aPA);
  }

  @Nonnull
  static IPrivilegedAction <Void> asVoid (@Nonnull final Runnable aRunnable)
  {
    return () -> {
      aRunnable.run ();
      return null;
    };
  }

  @Nonnull
  static IPrivilegedAction <ClassLoader> classLoaderGetParent (@Nonnull final ClassLoader aBaseClassLoader)
  {
    return aBaseClassLoader::getParent;
  }

  @Nonnull
  static IPrivilegedAction <ClassLoader> getClassLoader (@Nonnull final Class <?> aClass)
  {
    return aClass::getClassLoader;
  }

  @Nonnull
  static IPrivilegedAction <ClassLoader> getContextClassLoader ()
  {
    return Thread.currentThread ()::getContextClassLoader;
  }

  @Nonnull
  static IPrivilegedAction <Void> setContextClassLoader (@Nonnull final ClassLoader aClassLoader)
  {
    ValueEnforcer.notNull (aClassLoader, "ClassLoader");
    return asVoid ( () -> Thread.currentThread ().setContextClassLoader (aClassLoader));
  }

  @Nonnull
  static IPrivilegedAction <ClassLoader> getSystemClassLoader ()
  {
    return ClassLoader::getSystemClassLoader;
  }

  @Nonnull
  static IPrivilegedAction <String> systemClearProperty (@Nonnull final String sKey)
  {
    ValueEnforcer.notNull (sKey, "Key");
    return () -> System.clearProperty (sKey);
  }

  @Nonnull
  static IPrivilegedAction <String> systemGetProperty (@Nonnull final String sKey)
  {
    ValueEnforcer.notNull (sKey, "Key");
    return () -> System.getProperty (sKey);
  }

  @Nonnull
  static IPrivilegedAction <Properties> systemGetProperties ()
  {
    return System::getProperties;
  }

  @Nonnull
  static IPrivilegedAction <String> systemSetProperty (@Nonnull final String sKey, @Nonnull final String sValue)
  {
    ValueEnforcer.notNull (sKey, "Key");
    ValueEnforcer.notNull (sValue, "Value");
    return () -> System.setProperty (sKey, sValue);
  }

  @Nonnull
  static IPrivilegedAction <ProxySelector> proxySelectorGetDefault ()
  {
    return ProxySelector::getDefault;
  }

  @Nonnull
  static IPrivilegedAction <Void> proxySelectorSetDefault (@Nullable final ProxySelector aProxySelector)
  {
    return asVoid ( () -> ProxySelector.setDefault (aProxySelector));
  }

  @Nonnull
  static IPrivilegedAction <Void> authenticatorSetDefault (@Nullable final Authenticator aAuthenticator)
  {
    return asVoid ( () -> Authenticator.setDefault (aAuthenticator));
  }

  @Nonnull
  static IPrivilegedAction <Provider []> securityGetProviders ()
  {
    return Security::getProviders;
  }

  @Nonnull
  static IPrivilegedAction <Provider []> securityGetProviders (@Nonnull final String sFilter)
  {
    ValueEnforcer.notNull (sFilter, "Filter");
    return () -> Security.getProviders (sFilter);
  }

  @Nonnull
  static IPrivilegedAction <Provider> securityGetProvider (@Nonnull final String sName)
  {
    return () -> Security.getProvider (sName);
  }

  @Nonnull
  static IPrivilegedAction <Integer> securityAddProvider (@Nonnull final Provider aProvider)
  {
    ValueEnforcer.notNull (aProvider, "Provider");
    return () -> Integer.valueOf (Security.addProvider (aProvider));
  }

  @Nonnull
  static IPrivilegedAction <Integer> securityInsertProviderAt (@Nonnull final Provider aProvider, final int nIndex)
  {
    ValueEnforcer.notNull (aProvider, "Provider");
    return () -> Integer.valueOf (Security.insertProviderAt (aProvider, nIndex));
  }

  @Nonnull
  static IPrivilegedAction <Void> securityRemoveProvider (@Nonnull final String sName)
  {
    return asVoid ( () -> Security.removeProvider (sName));
  }
}
