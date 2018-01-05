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
package com.helger.xml.transform;

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

/**
 * Extended {@link ErrorListener} interface with chaining method.
 *
 * @author Philip Helger
 * @since 8.5.1
 */
public interface ITransformErrorListener extends ErrorListener, Serializable
{
  @Nonnull
  default ITransformErrorListener andThen (@Nullable final ErrorListener aOther)
  {
    final ITransformErrorListener aThis = this;
    if (aOther == null)
      return aThis;

    return new ITransformErrorListener ()
    {
      public void warning (@Nonnull final TransformerException aEx) throws TransformerException
      {
        aThis.warning (aEx);
        aOther.warning (aEx);
      }

      public void error (@Nonnull final TransformerException aEx) throws TransformerException
      {
        aThis.error (aEx);
        aOther.error (aEx);
      }

      public void fatalError (@Nonnull final TransformerException aEx) throws TransformerException
      {
        aThis.fatalError (aEx);
        aOther.fatalError (aEx);
      }
    };
  }
}
