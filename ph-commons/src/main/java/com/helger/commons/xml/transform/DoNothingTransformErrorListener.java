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
import javax.annotation.concurrent.Immutable;
import javax.xml.transform.ErrorListener;

import com.helger.commons.error.IResourceError;

/**
 * {@link javax.xml.transform.ErrorListener} that does nothing.
 *
 * @author Philip Helger
 */
@Immutable
public class DoNothingTransformErrorListener extends AbstractTransformErrorListener
{
  public DoNothingTransformErrorListener ()
  {
    this (null);
  }

  public DoNothingTransformErrorListener (@Nullable final ErrorListener aWrappedErrorListener)
  {
    super (aWrappedErrorListener);
  }

  @Override
  protected void internalLog (@Nonnull final IResourceError aResError)
  {}
}
