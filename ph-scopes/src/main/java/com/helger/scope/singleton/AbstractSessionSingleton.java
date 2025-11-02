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
package com.helger.scope.singleton;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.style.MustImplementEqualsAndHashcode;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.collection.commons.ICommonsList;
import com.helger.scope.ISessionScope;
import com.helger.scope.mgr.ScopeManager;

/**
 * This is the base class for singleton objects that reside in the session
 * non-web scope.
 *
 * @see com.helger.scope.mgr.EScope#SESSION
 * @author Philip Helger
 */
@MustImplementEqualsAndHashcode
public abstract class AbstractSessionSingleton extends AbstractSingleton
{
  protected AbstractSessionSingleton ()
  {}

  private void writeObject (@NonNull final ObjectOutputStream aOOS) throws IOException
  {
    writeAbstractSingletonFields (aOOS);
  }

  private void readObject (@NonNull final ObjectInputStream aOIS) throws IOException, ClassNotFoundException
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
  @NonNull
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
  @NonNull
  public static final <T extends AbstractSessionSingleton> T getSessionSingleton (@NonNull final Class <T> aClass)
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
  public static final <T extends AbstractSessionSingleton> T getSessionSingletonIfInstantiated (@NonNull final Class <T> aClass)
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
  public static final boolean isSessionSingletonInstantiated (@NonNull final Class <? extends AbstractSessionSingleton> aClass)
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
  @NonNull
  @ReturnsMutableCopy
  public static final ICommonsList <AbstractSessionSingleton> getAllSessionSingletons ()
  {
    return getAllSingletons (_getStaticScope (false), AbstractSessionSingleton.class);
  }
}
