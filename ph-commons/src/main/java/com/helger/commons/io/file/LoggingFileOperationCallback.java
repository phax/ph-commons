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
package com.helger.commons.io.file;

import java.io.File;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A logging implementation of the {@link IFileOperationCallback} interface.
 *
 * @author Philip Helger
 */
public class LoggingFileOperationCallback implements IFileOperationCallback
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (LoggingFileOperationCallback.class);

  public void onSuccess (@Nonnull final EFileIOOperation eOperation,
                         @Nonnull final File aFile1,
                         @Nullable final File aFile2)
  {
    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug ("File operation " +
                       eOperation.name () +
                       " succeeded on '" +
                       aFile1 +
                       "'" +
                       (aFile2 == null ? "" : " and '" + aFile2 + "'"));
  }

  public void onError (@Nonnull final EFileIOOperation eOperation,
                       @Nonnull final EFileIOErrorCode eErrorCode,
                       @Nonnull final File aFile1,
                       @Nullable final File aFile2,
                       @Nullable final Exception aException)
  {
    s_aLogger.warn ("File operation " +
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
