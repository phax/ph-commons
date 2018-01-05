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
package com.helger.commons.io.watchdir;

import java.nio.file.Path;

import javax.annotation.Nonnull;

import com.helger.commons.callback.ICallback;

/**
 * The callback interface to be implemented to get notified about changes in a
 * directory in the {@link WatchDir} class.
 *
 * @author Philip Helger
 * @since 8.6.6
 */
@FunctionalInterface
public interface IWatchDirCallback extends ICallback
{
  /**
   * Generic callback method
   *
   * @param eAction
   *        The action that was triggered. Never <code>null</code>.
   * @param aPath
   *        The affected path. Never <code>null</code>.
   */
  void onAction (@Nonnull EWatchDirAction eAction, @Nonnull Path aPath);
}
