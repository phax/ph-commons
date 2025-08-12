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
package com.helger.xml.sax;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Extended {@link ErrorHandler} interface with chaining method.
 *
 * @author Philip Helger
 * @since 8.5.1
 */
public interface ISAXErrorHandler extends ErrorHandler
{
  /**
   * Create a new combined error handler that first invoked this handler, and
   * afterwards the passed on.
   *
   * @param aOther
   *        The other handler to use. May be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @Nonnull
  default ISAXErrorHandler andThen (@Nullable final ErrorHandler aOther)
  {
    final ISAXErrorHandler aThis = this;
    if (aOther == null)
      return aThis;

    return new ISAXErrorHandler ()
    {
      public void warning (@Nonnull final SAXParseException aEx) throws SAXException
      {
        aThis.warning (aEx);
        aOther.warning (aEx);
      }

      public void error (@Nonnull final SAXParseException aEx) throws SAXException
      {
        aThis.error (aEx);
        aOther.error (aEx);
      }

      public void fatalError (@Nonnull final SAXParseException aEx) throws SAXException
      {
        aThis.fatalError (aEx);
        aOther.fatalError (aEx);
      }
    };
  }
}
