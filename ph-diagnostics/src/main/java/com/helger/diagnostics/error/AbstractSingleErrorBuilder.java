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
package com.helger.diagnostics.error;

import java.time.LocalDateTime;

import javax.xml.stream.Location;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;

import com.helger.base.builder.IBuilder;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.location.ILocation;
import com.helger.base.location.SimpleLocation;
import com.helger.base.trait.IGenericImplTrait;
import com.helger.diagnostics.error.level.EErrorLevel;
import com.helger.diagnostics.error.level.IErrorLevel;
import com.helger.diagnostics.error.text.ConstantHasErrorText;
import com.helger.diagnostics.error.text.DynamicHasErrorText;
import com.helger.diagnostics.error.text.IHasErrorText;
import com.helger.text.IMultilingualText;

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

  protected AbstractSingleErrorBuilder (@NonNull final IError aError)
  {
    ValueEnforcer.notNull (aError, "Error");
    errorLevel (aError.getErrorLevel ());
    errorID (aError.getErrorID ());
    errorFieldName (aError.getErrorFieldName ());
    errorLocation (aError.getErrorLocation ());
    errorText (aError.getErrorTexts ());
    linkedException (aError.getLinkedException ());
  }

  /**
   * Set the error date time.
   *
   * @param aErrorDT
   *        The error date time. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public final IMPLTYPE dateTime (@Nullable final LocalDateTime aErrorDT)
  {
    m_aErrorDT = aErrorDT;
    return thisAsT ();
  }

  /**
   * Set the error level.
   *
   * @param aErrorLevel
   *        The error level to use. May not be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public final IMPLTYPE errorLevel (@NonNull final IErrorLevel aErrorLevel)
  {
    ValueEnforcer.notNull (aErrorLevel, "ErrorLevel");
    m_aErrorLevel = aErrorLevel;
    return thisAsT ();
  }

  /**
   * Set the error ID.
   *
   * @param sErrorID
   *        The error ID. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public final IMPLTYPE errorID (@Nullable final String sErrorID)
  {
    m_sErrorID = sErrorID;
    return thisAsT ();
  }

  /**
   * Set the error field name.
   *
   * @param sErrorFieldName
   *        The error field name. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
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
  @NonNull
  public final IMPLTYPE errorLocation (@Nullable final String sErrorLocation)
  {
    return errorLocation (new SimpleLocation (sErrorLocation));
  }

  /**
   * Set the error location from a SAX Locator.
   *
   * @param aLocator
   *        The SAX locator to use. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public final IMPLTYPE errorLocation (@Nullable final Locator aLocator)
  {
    return errorLocation (SimpleLocation.create (aLocator));
  }

  /**
   * Set the error location from a SAX parse exception.
   *
   * @param aLocator
   *        The SAX parse exception to extract the location from. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public final IMPLTYPE errorLocation (@Nullable final SAXParseException aLocator)
  {
    return errorLocation (SimpleLocation.create (aLocator));
  }

  /**
   * Set the error location from a StAX location.
   *
   * @param aLocator
   *        The StAX location to use. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public final IMPLTYPE errorLocation (@Nullable final Location aLocator)
  {
    return errorLocation (SimpleLocation.create (aLocator));
  }

  /**
   * Set the error location.
   *
   * @param aErrorLocation
   *        The error location. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public final IMPLTYPE errorLocation (@Nullable final ILocation aErrorLocation)
  {
    m_aErrorLocation = aErrorLocation;
    return thisAsT ();
  }

  /**
   * Set a constant error text.
   *
   * @param sErrorText
   *        The error text. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public final IMPLTYPE errorText (@Nullable final String sErrorText)
  {
    return errorText (ConstantHasErrorText.createOnDemand (sErrorText));
  }

  /**
   * Set a multilingual error text.
   *
   * @param aMLT
   *        The multilingual text to use. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
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

  /**
   * Set the error text provider.
   *
   * @param aErrorText
   *        The error text provider. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public final IMPLTYPE errorText (@Nullable final IHasErrorText aErrorText)
  {
    m_aErrorText = aErrorText;
    return thisAsT ();
  }

  /**
   * Set the linked exception.
   *
   * @param aLinkedException
   *        The linked exception. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public final IMPLTYPE linkedException (@Nullable final Throwable aLinkedException)
  {
    m_aLinkedException = aLinkedException;
    return thisAsT ();
  }
}
