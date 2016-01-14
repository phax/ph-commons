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
package com.helger.commons.xml.transform;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import com.helger.commons.error.EErrorLevel;
import com.helger.commons.error.IErrorLevel;
import com.helger.commons.error.IResourceError;
import com.helger.commons.error.IResourceLocation;
import com.helger.commons.error.ResourceError;
import com.helger.commons.error.ResourceLocation;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;
import com.helger.commons.text.display.IHasDisplayText;

/**
 * Abstract implementation of a transformation {@link ErrorListener}.
 *
 * @author Philip Helger
 */
public abstract class AbstractTransformErrorListener implements ErrorListener
{
  private final ErrorListener m_aWrappedErrorListener;

  public AbstractTransformErrorListener ()
  {
    this (null);
  }

  public AbstractTransformErrorListener (@Nullable final ErrorListener aWrappedErrorListener)
  {
    m_aWrappedErrorListener = aWrappedErrorListener;
  }

  /**
   * @return The wrapped error listener. May be <code>null</code>.
   */
  @Nullable
  public ErrorListener getWrappedErrorListener ()
  {
    return m_aWrappedErrorListener;
  }

  @Nonnull
  private static IResourceError _buildError (@Nonnull final TransformerException ex,
                                             @Nonnull final IErrorLevel aErrorLevel,
                                             @Nonnull final IHasDisplayText aErrorMsg)
  {
    final SourceLocator aLocator = ex.getLocator ();
    final IResourceLocation aLocation = aLocator != null ? new ResourceLocation (StringHelper.getConcatenatedOnDemand (aLocator.getPublicId (),
                                                                                                                       "/",
                                                                                                                       aLocator.getSystemId ()),
                                                                                 aLocator.getLineNumber (),
                                                                                 aLocator.getColumnNumber ())
                                                         : new ResourceLocation (ex.getLocationAsString ());
    return new ResourceError (aLocation, aErrorLevel, aErrorMsg, ex);
  }

  /**
   * Handle the passed resource error.
   *
   * @param aResError
   *        The resource error to be handled. Never <code>null</code>.
   */
  protected abstract void internalLog (@Nonnull final IResourceError aResError);

  public final void warning (@Nonnull final TransformerException ex) throws TransformerException
  {
    internalLog (_buildError (ex, EErrorLevel.WARN, EXMLTransformTexts.TRANSFORMATION_WARNING));

    final ErrorListener aWrappedErrorListener = getWrappedErrorListener ();
    if (aWrappedErrorListener != null)
      aWrappedErrorListener.warning (ex);
  }

  public final void error (@Nonnull final TransformerException ex) throws TransformerException
  {
    internalLog (_buildError (ex, EErrorLevel.ERROR, EXMLTransformTexts.TRANSFORMATION_ERROR));

    final ErrorListener aWrappedErrorListener = getWrappedErrorListener ();
    if (aWrappedErrorListener != null)
      aWrappedErrorListener.error (ex);
  }

  public final void fatalError (@Nonnull final TransformerException ex) throws TransformerException
  {
    internalLog (_buildError (ex, EErrorLevel.FATAL_ERROR, EXMLTransformTexts.TRANSFORMATION_FATAL_ERROR));

    final ErrorListener aWrappedErrorListener = getWrappedErrorListener ();
    if (aWrappedErrorListener != null)
      aWrappedErrorListener.fatalError (ex);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).appendIfNotNull ("wrappedErrorListener", m_aWrappedErrorListener).toString ();
  }
}
