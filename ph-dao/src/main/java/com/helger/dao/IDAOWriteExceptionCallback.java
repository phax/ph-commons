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
package com.helger.dao;

import com.helger.base.callback.ICallback;
import com.helger.io.resource.IReadableResource;

import jakarta.annotation.Nonnull;

/**
 * Callback interface to handle thrown exception objects in DAO write actions.
 *
 * @author Philip Helger
 */
@FunctionalInterface
public interface IDAOWriteExceptionCallback extends ICallback
{
  /**
   * Called when an exception of the specified type occurred
   *
   * @param t
   *        The exception. Never <code>null</code>.
   * @param aResource
   *        The resource that failed to be written to. Never <code>null</code>.
   * @param aFileContent
   *        the content that should be written to the file. Never
   *        <code>null</code>.
   */
  void onDAOWriteException (@Nonnull Throwable t, @Nonnull IReadableResource aResource, @Nonnull CharSequence aFileContent);
}
