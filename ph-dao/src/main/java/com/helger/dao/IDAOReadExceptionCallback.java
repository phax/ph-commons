/*
 * Copyright (C) 2014-2024 Philip Helger (www.helger.com)
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
package com.helger.dao;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.callback.ICallback;
import com.helger.commons.io.resource.IReadableResource;

/**
 * Callback interface to handle thrown exception objects on DAO reading.
 *
 * @author Philip Helger
 */
@FunctionalInterface
public interface IDAOReadExceptionCallback extends ICallback
{
  /**
   * Called when an exception of the specified type occurred
   *
   * @param t
   *        The exception. Never <code>null</code>.
   * @param bInit
   *        <code>true</code> if it is an init action <code>false</code> if it
   *        is a read action.
   * @param aResource
   *        The resource that failed. May be <code>null</code> if no file is
   *        defined.
   */
  void onDAOReadException (@Nonnull Throwable t, boolean bInit, @Nullable IReadableResource aResource);
}
