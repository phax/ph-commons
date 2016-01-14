/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
package com.helger.commons.scope.singleton;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.MustImplementEqualsAndHashcode;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.scope.ISessionScope;
import com.helger.commons.scope.mgr.ScopeManager;

/**
 * This is the base class for singleton objects that reside in the session
 * non-web scope.
 *
 * @see com.helger.commons.scope.mgr.EScope#SESSION
 * @author Philip Helger
 */
@MustImplementEqualsAndHashcode
public abstract class AbstractSessionSingleton extends AbstractSingleton implements Serializable
{
  protected AbstractSessionSingleton ()
  {}

  private void writeObject (@Nonnull final ObjectOutputStream aOOS) throws IOException
  {
    writeAbstractSingletonFields (aOOS);
  }

  private void readObject (@Nonnull final ObjectInputStream aOIS) throws IOException, ClassNotFoundException
  {
    readAbstractSingletonFields (aOIS);
  }

  /**
   * @param bCreateIfNotExisting
   *        <code>true</code> to create a new scope, if none is present yet,
   *        <code>false</code> to return <code>null</code> if either no request
   *        scope or no session scope is present.
   * @return The scope to be used for this type of singleton.
   */
  @Nonnull
  private static ISessionScope _getStaticScope (final boolean bCreateIfNotExisting)
  {
    return ScopeManager.getSessionScope (bCreateIfNotExisting);
  }

  /**
   * Get the singleton object in the current session scope, using the passed
   * class. If the singleton is not yet instantiated, a new instance is created.
   *
   * @param <T>
   *        The type to be returned
   * @param aClass
   *        The class to be used. May not be <code>null</code>. The class must
   *        be public as needs to have a public no-argument constructor.
   * @return The singleton object and never <code>null</code>.
   */
  @Nonnull
  public static final <T extends AbstractSessionSingleton> T getSessionSingleton (@Nonnull final Class <T> aClass)
  {
    return getSingleton (_getStaticScope (true), aClass);
  }

  /**
   * Get the singleton object if it is already instantiated inside the current
   * session scope or <code>null</code> if it is not instantiated.
   *
   * @param <T>
   *        The type to be returned
   * @param aClass
   *        The class to be checked. May not be <code>null</code>.
   * @return The singleton for the specified class is already instantiated,
   *         <code>null</code> otherwise.
   */
  @Nullable
  public static final <T extends AbstractSessionSingleton> T getSessionSingletonIfInstantiated (@Nonnull final Class <T> aClass)
  {
    return getSingletonIfInstantiated (_getStaticScope (false), aClass);
  }

  /**
   * Check if a singleton is already instantiated inside the current session
   * scope
   *
   * @param aClass
   *        The class to be checked. May not be <code>null</code>.
   * @return <code>true</code> if the singleton for the specified class is
   *         already instantiated, <code>false</code> otherwise.
   */
  public static final boolean isSessionSingletonInstantiated (@Nonnull final Class <? extends AbstractSessionSingleton> aClass)
  {
    return isSingletonInstantiated (_getStaticScope (false), aClass);
  }

  /**
   * Get all instantiated singleton objects registered in the current session
   * scope.
   *
   * @return A non-<code>null</code> list with all instances of this class in
   *         the current session scope.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static final List <AbstractSessionSingleton> getAllSessionSingletons ()
  {
    return getAllSingletons (_getStaticScope (false), AbstractSessionSingleton.class);
  }
}
