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
package com.helger.settings.exchange.configfile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.state.EChange;
import com.helger.commons.string.StringHelper;
import com.helger.settings.Settings;

public final class TrimmedValueSettings extends Settings
{
  public TrimmedValueSettings (@Nonnull @Nonempty final String sName)
  {
    super (sName);
  }

  @Override
  @Nonnull
  public EChange putIn (@Nonnull @Nonempty final String sFieldName, @Nullable final Object aNewValue)
  {
    return super.putIn (sFieldName, StringHelper.trim ((String) aNewValue));
  }
}
