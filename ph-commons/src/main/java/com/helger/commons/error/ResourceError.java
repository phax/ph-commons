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
package com.helger.commons.error;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.OverrideOnDemand;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;
import com.helger.commons.text.display.ConstantHasDisplayText;
import com.helger.commons.text.display.IHasDisplayText;

/**
 * Default implementation of the {@link IResourceError} interface. The
 * implementation is immutable.
 *
 * @author Philip Helger
 */
@Immutable
public class ResourceError implements IResourceError
{
  private final IResourceLocation m_aLocation;
  private final IErrorLevel m_aErrorLevel;
  private final IHasDisplayText m_aErrorText;
  private final Throwable m_aLinkedException;

  public ResourceError (@Nonnull final IResourceLocation aLocation,
                        @Nonnull final IErrorLevel aErrorLevel,
                        @Nonnull final String sErrorText)
  {
    this (aLocation, aErrorLevel, sErrorText, null);
  }

  public ResourceError (@Nonnull final IResourceLocation aLocation,
                        @Nonnull final IErrorLevel aErrorLevel,
                        @Nonnull final String sErrorText,
                        @Nullable final Throwable aLinkedException)
  {
    this (aLocation, aErrorLevel, new ConstantHasDisplayText (sErrorText), aLinkedException);
  }

  public ResourceError (@Nonnull final IResourceLocation aLocation,
                        @Nonnull final IErrorLevel aErrorLevel,
                        @Nonnull final IHasDisplayText aErrorText)
  {
    this (aLocation, aErrorLevel, aErrorText, null);
  }

  public ResourceError (@Nonnull final IResourceLocation aLocation,
                        @Nonnull final IErrorLevel aErrorLevel,
                        @Nonnull final IHasDisplayText aErrorText,
                        @Nullable final Throwable aLinkedException)
  {
    m_aLocation = ValueEnforcer.notNull (aLocation, "Location");
    m_aErrorLevel = ValueEnforcer.notNull (aErrorLevel, "ErrorLevel");
    m_aErrorText = ValueEnforcer.notNull (aErrorText, "ErrorText");
    m_aLinkedException = aLinkedException;
  }

  @Nonnull
  public final IResourceLocation getLocation ()
  {
    return m_aLocation;
  }

  @Nonnull
  public final IErrorLevel getErrorLevel ()
  {
    return m_aErrorLevel;
  }

  @Nullable
  @OverrideOnDemand
  public String getDisplayText (@Nonnull final Locale aContentLocale)
  {
    return m_aErrorText.getDisplayText (aContentLocale);
  }

  @Nullable
  public final Throwable getLinkedException ()
  {
    return m_aLinkedException;
  }

  @Nonnull
  @OverrideOnDemand
  public String getAsString (@Nonnull final Locale aDisplayLocale)
  {
    String ret = "[" + m_aErrorLevel.getID () + "] ";

    // Location
    final String sLocation = m_aLocation.getAsString ();
    if (StringHelper.hasText (sLocation))
      ret += sLocation + ": ";

    // Message
    ret += getDisplayText (aDisplayLocale);

    // Linked exception
    if (hasLinkedException ())
      ret += " (" + m_aLinkedException.getClass ().getName () + ": " + m_aLinkedException.getMessage () + ")";
    return ret;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final ResourceError rhs = (ResourceError) o;
    // Do not include the exception, because it is not comparable
    return m_aLocation.equals (rhs.m_aLocation) &&
           m_aErrorLevel.equals (rhs.m_aErrorLevel) &&
           m_aErrorText.equals (rhs.m_aErrorText);
  }

  @Override
  public int hashCode ()
  {
    // Do not include the exception, because it is not comparable
    return new HashCodeGenerator (this).append (m_aLocation)
                                       .append (m_aErrorLevel)
                                       .append (m_aErrorText)
                                       .getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("location", m_aLocation)
                                       .append ("errorLevel", m_aErrorLevel)
                                       .append ("errorText", m_aErrorText)
                                       .appendIfNotNull ("linkedException", m_aLinkedException)
                                       .toString ();
  }
}
