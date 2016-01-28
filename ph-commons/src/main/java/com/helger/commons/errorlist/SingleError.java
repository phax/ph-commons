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
package com.helger.commons.errorlist;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.error.EErrorLevel;
import com.helger.commons.error.IErrorLevel;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * Represents an overall form error. Default implementation of {@link IError}.
 * <br>
 * Note: cannot be named 'Error' as it should be because of the standard java
 * exception class 'Error'.
 *
 * @author Philip Helger
 */
@Immutable
public class SingleError implements IError
{
  private final String m_sErrorID;
  private final IErrorLevel m_aErrorLevel;
  private final String m_sErrorFieldName;
  private final String m_sErrorText;

  public SingleError (@Nonnull final IErrorLevel aErrorLevel, @Nonnull @Nonempty final String sErrorText)
  {
    this (aErrorLevel, (String) null, sErrorText);
  }

  public SingleError (@Nonnull final IErrorLevel aErrorLevel,
                      @Nullable final String sErrorFieldName,
                      @Nonnull @Nonempty final String sErrorText)
  {
    this ((String) null, aErrorLevel, sErrorFieldName, sErrorText);
  }

  public SingleError (@Nullable final String sErrorID,
                      @Nonnull final IErrorLevel aErrorLevel,
                      @Nullable final String sErrorFieldName,
                      @Nonnull @Nonempty final String sErrorText)
  {
    ValueEnforcer.notNull (aErrorLevel, "ErrorLevel");
    ValueEnforcer.notEmpty (sErrorText, "ErrorText");
    m_sErrorID = sErrorID;
    m_aErrorLevel = aErrorLevel;
    m_sErrorFieldName = sErrorFieldName;
    m_sErrorText = sErrorText;
  }

  @Nullable
  public String getErrorID ()
  {
    return m_sErrorID;
  }

  @Nonnull
  public IErrorLevel getErrorLevel ()
  {
    return m_aErrorLevel;
  }

  @Nullable
  public String getErrorFieldName ()
  {
    return m_sErrorFieldName;
  }

  @Nonnull
  public String getErrorText ()
  {
    return m_sErrorText;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final SingleError rhs = (SingleError) o;
    return EqualsHelper.equals (m_sErrorID, rhs.m_sErrorID) &&
           m_aErrorLevel.equals (rhs.m_aErrorLevel) &&
           EqualsHelper.equals (m_sErrorFieldName, rhs.m_sErrorFieldName) &&
           m_sErrorText.equals (rhs.m_sErrorText);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_sErrorID)
                                       .append (m_aErrorLevel)
                                       .append (m_sErrorFieldName)
                                       .append (m_sErrorText)
                                       .getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).appendIfNotNull ("errorID", m_sErrorID)
                                       .append ("errorLevel", m_aErrorLevel)
                                       .appendIfNotNull ("errorFieldName", m_sErrorFieldName)
                                       .append ("errorText", m_sErrorText)
                                       .toString ();
  }

  @Nonnull
  public static SingleError createSuccess (@Nonnull @Nonempty final String sErrorText)
  {
    return createSuccess (null, null, sErrorText);
  }

  @Nonnull
  public static SingleError createSuccess (@Nullable final String sErrorFieldName,
                                           @Nonnull @Nonempty final String sErrorText)
  {
    return createSuccess (null, sErrorFieldName, sErrorText);
  }

  @Nonnull
  public static SingleError createSuccess (@Nullable final String sErrorID,
                                           @Nullable final String sErrorFieldName,
                                           @Nonnull @Nonempty final String sErrorText)
  {
    return new SingleError (sErrorID, EErrorLevel.SUCCESS, sErrorFieldName, sErrorText);
  }

  @Nonnull
  public static SingleError createInfo (@Nonnull @Nonempty final String sErrorText)
  {
    return createInfo (null, null, sErrorText);
  }

  @Nonnull
  public static SingleError createInfo (@Nullable final String sErrorFieldName,
                                        @Nonnull @Nonempty final String sErrorText)
  {
    return createInfo (null, sErrorFieldName, sErrorText);
  }

  @Nonnull
  public static SingleError createInfo (@Nullable final String sErrorID,
                                        @Nullable final String sErrorFieldName,
                                        @Nonnull @Nonempty final String sErrorText)
  {
    return new SingleError (sErrorID, EErrorLevel.INFO, sErrorFieldName, sErrorText);
  }

  @Nonnull
  public static SingleError createWarning (@Nonnull @Nonempty final String sErrorText)
  {
    return createWarning (null, null, sErrorText);
  }

  @Nonnull
  public static SingleError createWarning (@Nullable final String sErrorFieldName,
                                           @Nonnull @Nonempty final String sErrorText)
  {
    return createWarning (null, sErrorFieldName, sErrorText);
  }

  @Nonnull
  public static SingleError createWarning (@Nullable final String sErrorID,
                                           @Nullable final String sErrorFieldName,
                                           @Nonnull @Nonempty final String sErrorText)
  {
    return new SingleError (sErrorID, EErrorLevel.WARN, sErrorFieldName, sErrorText);
  }

  @Nonnull
  public static SingleError createError (@Nonnull @Nonempty final String sErrorText)
  {
    return createError (null, null, sErrorText);
  }

  @Nonnull
  public static SingleError createError (@Nullable final String sErrorFieldName,
                                         @Nonnull @Nonempty final String sErrorText)
  {
    return createError (null, sErrorFieldName, sErrorText);
  }

  @Nonnull
  public static SingleError createError (@Nullable final String sErrorID,
                                         @Nullable final String sErrorFieldName,
                                         @Nonnull @Nonempty final String sErrorText)
  {
    return new SingleError (sErrorID, EErrorLevel.ERROR, sErrorFieldName, sErrorText);
  }
}
