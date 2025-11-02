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
package com.helger.io.file;

import java.io.File;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.base.callback.ICallback;

/**
 * Callback interface for {@link FileOperationManager}.
 *
 * @author Philip Helger
 */
public interface IFileOperationCallback extends ICallback
{
  /**
   * Called upon operation success.
   *
   * @param eOperation
   *        The operation that succeeded.
   * @param aFile1
   *        The first file worked upon. May not be <code>null</code>.
   * @param aFile2
   *        The second file worked upon. May be <code>null</code>.
   */
  default void onSuccess (@NonNull final EFileIOOperation eOperation, @NonNull final File aFile1, @Nullable final File aFile2)
  {}

  /**
   * Called upon operation error.
   *
   * @param eOperation
   *        The operation that failed.
   * @param eErrorCode
   *        The error code that occurred.
   * @param aFile1
   *        The first file worked upon. May not be <code>null</code>.
   * @param aFile2
   *        The second file worked upon. May be <code>null</code>.
   * @param aException
   *        The exception that occurred. May be <code>null</code>.
   */
  default void onError (@NonNull final EFileIOOperation eOperation,
                        @NonNull final EFileIOErrorCode eErrorCode,
                        @NonNull final File aFile1,
                        @Nullable final File aFile2,
                        @Nullable final Exception aException)
  {}
}
