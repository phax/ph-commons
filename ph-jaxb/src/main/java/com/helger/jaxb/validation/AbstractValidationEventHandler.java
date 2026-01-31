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
package com.helger.jaxb.validation;

import java.net.URL;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.annotation.style.OverrideOnDemand;
import com.helger.base.location.ILocation;
import com.helger.base.location.SimpleLocation;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.diagnostics.error.IError;
import com.helger.diagnostics.error.SingleError;
import com.helger.diagnostics.error.SingleErrorBuilder;
import com.helger.diagnostics.error.level.EErrorLevel;
import com.helger.diagnostics.error.level.IErrorLevel;
import com.helger.xml.serialize.write.XMLWriter;

import jakarta.xml.bind.ValidationEvent;
import jakarta.xml.bind.ValidationEventHandler;
import jakarta.xml.bind.ValidationEventLocator;

/**
 * An abstract implementation of the JAXB {@link ValidationEventHandler}
 * interface.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public abstract class AbstractValidationEventHandler implements IValidationEventHandler
{
  private static final Logger LOGGER = LoggerFactory.getLogger (AbstractValidationEventHandler.class);

  /**
   * Constructor not encapsulating any existing handler.
   */
  protected AbstractValidationEventHandler ()
  {}

  /**
   * Get the error level matching the passed JAXB severity.
   *
   * @param nSeverity
   *        The JAXB severity.
   * @return The matching {@link IErrorLevel}. Never <code>null</code>.
   */
  @NonNull
  @OverrideOnDemand
  protected IErrorLevel getErrorLevel (final int nSeverity)
  {
    switch (nSeverity)
    {
      case ValidationEvent.WARNING:
        return EErrorLevel.WARN;
      case ValidationEvent.ERROR:
        return EErrorLevel.ERROR;
      case ValidationEvent.FATAL_ERROR:
        return EErrorLevel.FATAL_ERROR;
      default:
        LOGGER.warn ("Unknown JAXB validation severity: " + nSeverity + "; defaulting to error");
        return EErrorLevel.ERROR;
    }
  }

  @Nullable
  @OverrideOnDemand
  protected String getLocationResourceID (@Nullable final ValidationEventLocator aLocator)
  {
    if (aLocator != null)
    {
      // Source file found?
      final URL aURL = aLocator.getURL ();
      if (aURL != null)
        return aURL.toString ();
    }
    return null;
  }

  @Nullable
  @OverrideOnDemand
  protected String getErrorFieldName (@Nullable final ValidationEventLocator aLocator)
  {
    if (aLocator != null)
    {
      // Source object found?
      final Object aObj = aLocator.getObject ();
      if (aObj != null)
        return "obj: " + aObj.toString ();

      // Source node found?
      final Node aNode = aLocator.getNode ();
      if (aNode != null)
        return XMLWriter.getNodeAsString (aNode);
    }
    return null;
  }

  /**
   * Callback method invoked when an error occurs.
   *
   * @param aError
   *        The occurred error.
   */
  @OverrideOnDemand
  protected abstract void onEvent (@NonNull final IError aError);

  /**
   * Should the processing be continued? By default it is always continued, as
   * long as no fatal error occurs. This method is only invoked, if no wrapped
   * handler is present.
   *
   * @param aErrorLevel
   *        The error level to be checked.
   * @return <code>true</code> if processing should be continued,
   *         <code>false</code> if processing should stop.
   */
  @OverrideOnDemand
  protected boolean continueProcessing (@NonNull final IErrorLevel aErrorLevel)
  {
    // Continue as long as it is no fatal error. On Fatal error stop!
    return aErrorLevel.isLT (EErrorLevel.FATAL_ERROR);
  }

  public final boolean handleEvent (@NonNull final ValidationEvent aEvent)
  {
    final IErrorLevel aErrorLevel = getErrorLevel (aEvent.getSeverity ());
    final SingleErrorBuilder aErrBuilder = SingleError.builder ().errorLevel (aErrorLevel);

    final ValidationEventLocator aLocator = aEvent.getLocator ();
    aErrBuilder.errorLocation (new SimpleLocation (getLocationResourceID (aLocator),
                                                   aLocator != null ? aLocator.getLineNumber ()
                                                                    : ILocation.ILLEGAL_NUMBER,
                                                   aLocator != null ? aLocator.getColumnNumber ()
                                                                    : ILocation.ILLEGAL_NUMBER))
               .errorFieldName (getErrorFieldName (aLocator));

    // Message may be null in some cases (e.g. when a linked exception is
    // present), but is not allowed to be null!
    String sMsg = aEvent.getMessage ();
    if (sMsg == null)
    {
      if (aEvent.getLinkedException () != null)
      {
        sMsg = aEvent.getLinkedException ().getMessage ();
        if (sMsg == null)
          sMsg = "Exception";
      }
      else
      {
        // Does this ever happen????
        sMsg = "Validation event";
      }
    }
    aErrBuilder.errorText (sMsg).linkedException (aEvent.getLinkedException ());

    // call our callback
    onEvent (aErrBuilder.build ());

    // Continue processing?
    return continueProcessing (aErrorLevel);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).getToString ();
  }
}
