/**
 * Copyright (C) 2014-2017 Philip Helger (www.helger.com)
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
package com.helger.settings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.callback.ICallback;

/**
 * Interface for ISettings callback.
 *
 * @author Philip Helger
 */
public interface ISettingsAfterChangeCallback extends ICallback
{
  /**
   * Method that is invoked after a setting changed.
   *
   * @param sFieldName
   *        The changed field name. Neither <code>null</code> nor empty.
   * @param aOldValue
   *        The old value. May be <code>null</code>.
   * @param aNewValue
   *        The new value. May be <code>null</code> in which case the value was
   *        removed.
   */
  void onAfterSettingsChanged (@Nonnull @Nonempty String sFieldName,
                               @Nullable Object aOldValue,
                               @Nullable Object aNewValue);

}
