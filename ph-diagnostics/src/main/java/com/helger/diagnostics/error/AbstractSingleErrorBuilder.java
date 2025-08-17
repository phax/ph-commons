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
package com.helger.diagnostics.error;

import java.time.LocalDateTime;

import javax.xml.stream.Location;

import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;

import com.helger.base.builder.IBuilder;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.location.ILocation;
import com.helger.base.location.SimpleLocation;
import com.helger.base.traits.IGenericImplTrait;
import com.helger.diagnostics.error.level.EErrorLevel;
import com.helger.diagnostics.error.level.IErrorLevel;
import com.helger.diagnostics.error.text.ConstantHasErrorText;
import com.helger.diagnostics.error.text.DynamicHasErrorText;
import com.helger.diagnostics.error.text.IHasErrorText;
import com.helger.text.IMultilingualText;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Abstract builder class for {@link SingleError} and derived classes.
 *
 * @author Philip Helger
 * @param <ERRTYPE>
 *        Result error type
 * @param <IMPLTYPE>
 *        Implementation type
 */
public abstract class AbstractSingleErrorBuilder <ERRTYPE extends SingleError, IMPLTYPE extends AbstractSingleErrorBuilder <ERRTYPE, IMPLTYPE>>
                                                 implements
                                                 IGenericImplTrait <IMPLTYPE>,
                                                 IBuilder <ERRTYPE>
{
  public static final IErrorLevel DEFAULT_ERROR_LEVEL = EErrorLevel.ERROR;

  protected LocalDateTime m_aErrorDT;
  protected IErrorLevel m_aErrorLevel = DEFAULT_ERROR_LEVEL;
  protected String m_sErrorID;
  protected String m_sErrorFieldName;
  protected ILocation m_aErrorLocation;
  protected IHasErrorText m_aErrorText;
  protected Throwable m_aLinkedException;

  protected AbstractSingleErrorBuilder ()
  {}

  protected AbstractSingleErrorBuilder (@Nonnull final IError aError)
  {
    ValueEnforcer.notNull (aError, "Error");
    errorLevel (aError.getErrorLevel ());
    errorID (aError.getErrorID ());
    errorFieldName (aError.getErrorFieldName ());
    errorLocation (aError.getErrorLocation ());
    errorText (aError.getErrorTexts ());
    linkedException (aError.getLinkedException ());
  }

  @Nonnull
  public final IMPLTYPE dateTime (@Nullable final LocalDateTime aErrorDT)
  {
    m_aErrorDT = aErrorDT;
    return thisAsT ();
  }

  @Nonnull
  public final IMPLTYPE errorLevel (@Nonnull final IErrorLevel aErrorLevel)
  {
    ValueEnforcer.notNull (aErrorLevel, "ErrorLevel");
    m_aErrorLevel = aErrorLevel;
    return thisAsT ();
  }

  @Nonnull
  public final IMPLTYPE errorID (@Nullable final String sErrorID)
  {
    m_sErrorID = sErrorID;
    return thisAsT ();
  }

  @Nonnull
  public final IMPLTYPE errorFieldName (@Nullable final String sErrorFieldName)
  {
    m_sErrorFieldName = sErrorFieldName;
    return thisAsT ();
  }

  /**
   * Set a simple error location without line and column number
   *
   * @param sErrorLocation
   *        Error location string
   * @return this for chaining
   * @since 9.0.2
   */
  @Nonnull
  public final IMPLTYPE errorLocation (@Nullable final String sErrorLocation)
  {
    return errorLocation (new SimpleLocation (sErrorLocation));
  }

  @Nonnull
  public final IMPLTYPE errorLocation (@Nullable final Locator aLocator)
  {
    return errorLocation (SimpleLocation.create (aLocator));
  }

  @Nonnull
  public final IMPLTYPE errorLocation (@Nullable final SAXParseException aLocator)
  {
    return errorLocation (SimpleLocation.create (aLocator));
  }

  @Nonnull
  public final IMPLTYPE errorLocation (@Nullable final Location aLocator)
  {
    return errorLocation (SimpleLocation.create (aLocator));
  }

  @Nonnull
  public final IMPLTYPE errorLocation (@Nullable final ILocation aErrorLocation)
  {
    m_aErrorLocation = aErrorLocation;
    return thisAsT ();
  }

  @Nonnull
  public final IMPLTYPE errorText (@Nullable final String sErrorText)
  {
    return errorText (ConstantHasErrorText.createOnDemand (sErrorText));
  }

  @Nonnull
  public final IMPLTYPE errorText (@Nullable final IMultilingualText aMLT)
  {
    if (aMLT == null)
      m_aErrorText = null;
    else
      if (aMLT.texts ().size () == 1)
      {
        // If the multilingual text contains only a single locale, use it as a
        // constant value
        // If you don't like this behavior directly call #setErrorText with a
        // DynamicHasErrorText
        m_aErrorText = ConstantHasErrorText.createOnDemand (aMLT.texts ().getFirstValue ());
      }
      else
        m_aErrorText = new DynamicHasErrorText (aMLT);
    return thisAsT ();
  }

  @Nonnull
  public final IMPLTYPE errorText (@Nullable final IHasErrorText aErrorText)
  {
    m_aErrorText = aErrorText;
    return thisAsT ();
  }

  @Nonnull
  public final IMPLTYPE linkedException (@Nullable final Throwable aLinkedException)
  {
    m_aLinkedException = aLinkedException;
    return thisAsT ();
  }
}
