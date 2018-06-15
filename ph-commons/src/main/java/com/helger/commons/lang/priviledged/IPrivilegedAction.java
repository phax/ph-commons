/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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

import java.security.PrivilegedAction;
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
  @Nullable
  default T invokeSafe ()
  {
    if (System.getSecurityManager () == null)
    {
      // No need to use AccessController
      return run ();
    }
    return AccessControllerHelper.call (this);
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
  static IPrivilegedAction <Object> setContextClassLoader (@Nonnull final ClassLoader aClassLoader)
  {
    ValueEnforcer.notNull (aClassLoader, "ClassLoader");
    return () -> {
      Thread.currentThread ().setContextClassLoader (aClassLoader);
      return null;
    };
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
}
