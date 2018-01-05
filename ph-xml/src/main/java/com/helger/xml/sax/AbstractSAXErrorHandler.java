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
package com.helger.xml.sax;

import javax.annotation.Nonnull;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.helger.commons.error.IError;
import com.helger.commons.error.SingleError;
import com.helger.commons.error.level.EErrorLevel;
import com.helger.commons.error.level.IErrorLevel;
import com.helger.commons.location.SimpleLocation;
import com.helger.commons.string.ToStringGenerator;

/**
 * java.xml error handler base class.
 *
 * @author Philip Helger
 */
public abstract class AbstractSAXErrorHandler implements ISAXErrorHandler
{
  /**
   * Constructor without parent error handler.
   */
  protected AbstractSAXErrorHandler ()
  {}

  /**
   * Utility method to convert a {@link SAXParseException} into an
   * {@link IError}.
   *
   * @param aErrorLevel
   *        The occurred error level. May not be <code>null</code>.
   * @param ex
   *        The exception to convert. May not be <code>null</code>.
   * @return The {@link IError} representation. Never <code>null</code>.
   */
  @Nonnull
  public static IError getSaxParseError (@Nonnull final IErrorLevel aErrorLevel, @Nonnull final SAXParseException ex)
  {
    return SingleError.builder ()
                      .setErrorLevel (aErrorLevel)
                      .setErrorLocation (SimpleLocation.create (ex))
                      .setErrorText ("[SAX] " + ex.getMessage ())
                      .setLinkedException (ex)
                      .build ();
  }

  protected abstract void internalLog (@Nonnull IErrorLevel aErrorLevel, @Nonnull SAXParseException aException);

  public final void warning (final SAXParseException ex) throws SAXException
  {
    internalLog (EErrorLevel.WARN, ex);
  }

  public final void error (final SAXParseException ex) throws SAXException
  {
    internalLog (EErrorLevel.ERROR, ex);
  }

  public final void fatalError (final SAXParseException ex) throws SAXException
  {
    internalLog (EErrorLevel.FATAL_ERROR, ex);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).getToString ();
  }
}
