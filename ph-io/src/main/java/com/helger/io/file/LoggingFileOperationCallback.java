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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A logging implementation of the {@link IFileOperationCallback} interface.
 *
 * @author Philip Helger
 */
public class LoggingFileOperationCallback implements IFileOperationCallback
{
  private static final Logger LOGGER = LoggerFactory.getLogger (LoggingFileOperationCallback.class);

  @Override
  public void onSuccess (@NonNull final EFileIOOperation eOperation,
                         @NonNull final File aFile1,
                         @Nullable final File aFile2)
  {
    if (LOGGER.isDebugEnabled ())
      LOGGER.debug ("File operation " +
                    eOperation.name () +
                    " succeeded on '" +
                    aFile1 +
                    "'" +
                    (aFile2 == null ? "" : " and '" + aFile2 + "'"));
  }

  @Override
  public void onError (@NonNull final EFileIOOperation eOperation,
                       @NonNull final EFileIOErrorCode eErrorCode,
                       @NonNull final File aFile1,
                       @Nullable final File aFile2,
                       @Nullable final Exception aException)
  {
    LOGGER.warn ("File operation " +
                 eOperation.name () +
                 " failed with error code " +
                 eErrorCode.name () +
                 " on '" +
                 aFile1 +
                 "'" +
                 (aFile2 == null ? "" : " and '" + aFile2 + "'"),
                 aException);
  }
}
