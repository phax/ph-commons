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
package com.helger.xml.transform;

import java.io.File;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.file.Path;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillNotClose;
import javax.annotation.concurrent.Immutable;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Node;

import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.io.resource.IWritableResource;

/**
 * Factory class to create the correct {@link javax.xml.transform.Result}
 * objects for different output types.
 *
 * @author Philip Helger
 */
@Immutable
public final class TransformResultFactory
{
  @PresentForCodeCoverage
  private static final TransformResultFactory INSTANCE = new TransformResultFactory ();

  private TransformResultFactory ()
  {}

  @Nonnull
  public static StreamResult create (@Nonnull final File aFile)
  {
    return new StreamResult (aFile);
  }

  @Nonnull
  public static StreamResult create (@Nonnull final Path aPath)
  {
    return new StreamResult (aPath.toFile ());
  }

  @Nonnull
  public static StreamResult create (@Nonnull final IWritableResource aResource)
  {
    return new StreamResult (aResource.getAsFile ());
  }

  @Nonnull
  public static StreamResult create (@Nullable @WillNotClose final OutputStream aOS)
  {
    return new StreamResult (aOS);
  }

  @Nonnull
  public static StreamResult create (@Nullable @WillNotClose final Writer aWriter)
  {
    return new StreamResult (aWriter);
  }

  @Nonnull
  public static DOMResult create (@Nullable final Node aNode)
  {
    return new DOMResult (aNode);
  }
}
