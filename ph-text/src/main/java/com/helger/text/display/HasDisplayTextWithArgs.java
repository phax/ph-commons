/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.text.display;

import java.util.Locale;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.array.ArrayHelper;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.text.TextFormatter;
import com.helger.base.tostring.ToStringGenerator;

/**
 * A special implementation of {@link IHasDisplayText} that encapsulates arguments to be put into
 * the message.
 *
 * @author Philip Helger
 */
@Immutable
public class HasDisplayTextWithArgs implements IHasDisplayText
{
  private final IHasDisplayText m_aParentText;
  private final Object [] m_aArgs;

  public HasDisplayTextWithArgs (@NonNull final IHasDisplayText aParentText, @NonNull @Nonempty final Object... aArgs)
  {
    m_aParentText = ValueEnforcer.notNull (aParentText, "ParentText");
    m_aArgs = ValueEnforcer.notEmpty (aArgs, "Arguments");
  }

  @NonNull
  public IHasDisplayText getParentText ()
  {
    return m_aParentText;
  }

  /**
   * Get all arguments from the constructor.
   *
   * @return a copy of all arguments. Neither <code>null</code> nor empty-
   */
  @NonNull
  @Nonempty
  @ReturnsMutableCopy
  public Object [] getAllArgs ()
  {
    return ArrayHelper.getCopy (m_aArgs);
  }

  @Nullable
  public String getDisplayText (@NonNull final Locale aContentLocale)
  {
    final String sText = m_aParentText.getDisplayText (aContentLocale);
    return TextFormatter.getFormattedText (sText, m_aArgs);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("parentText", m_aParentText).append ("args", m_aArgs).getToString ();
  }
}
