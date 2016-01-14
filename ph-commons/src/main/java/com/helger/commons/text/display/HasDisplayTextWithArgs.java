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
package com.helger.commons.text.display;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.string.ToStringGenerator;
import com.helger.commons.text.util.TextHelper;

/**
 * A special implementation of {@link IHasDisplayText} that encapsulates
 * arguments to be put into the message.
 *
 * @author Philip Helger
 */
public final class HasDisplayTextWithArgs implements IHasDisplayText
{
  private final IHasDisplayText m_aParentText;
  private final Object [] m_aArgs;

  public HasDisplayTextWithArgs (@Nonnull final IHasDisplayText aParentText, @Nonnull @Nonempty final Object... aArgs)
  {
    m_aParentText = ValueEnforcer.notNull (aParentText, "ParentText");
    m_aArgs = ValueEnforcer.notEmpty (aArgs, "Arguments");
  }

  @Nonnull
  public IHasDisplayText getParentText ()
  {
    return m_aParentText;
  }

  @Nonnull
  @ReturnsMutableCopy
  public Object [] getArgs ()
  {
    return ArrayHelper.getCopy (m_aArgs);
  }

  @Nullable
  public String getDisplayText (@Nonnull final Locale aContentLocale)
  {
    final String sText = m_aParentText.getDisplayText (aContentLocale);
    return TextHelper.getFormattedText (sText, m_aArgs);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("parentText", m_aParentText).append ("args", m_aArgs).toString ();
  }
}
