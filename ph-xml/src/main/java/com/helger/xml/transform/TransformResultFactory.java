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
package com.helger.xml.transform;

import java.io.File;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.file.Path;

import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamResult;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.w3c.dom.Node;

import com.helger.annotation.WillNotClose;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.io.resource.IWritableResource;

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

  /**
   * Create a {@link StreamResult} from a {@link File}.
   *
   * @param aFile
   *        The file to write to. May not be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static StreamResult create (@NonNull final File aFile)
  {
    return new StreamResult (aFile);
  }

  /**
   * Create a {@link StreamResult} from a {@link Path}.
   *
   * @param aPath
   *        The path to write to. May not be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static StreamResult create (@NonNull final Path aPath)
  {
    return new StreamResult (aPath.toFile ());
  }

  /**
   * Create a {@link StreamResult} from an {@link IWritableResource}.
   *
   * @param aResource
   *        The resource to write to. May not be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static StreamResult create (@NonNull final IWritableResource aResource)
  {
    return new StreamResult (aResource.getAsFile ());
  }

  /**
   * Create a {@link StreamResult} from an {@link OutputStream}.
   *
   * @param aOS
   *        The output stream to write to. May be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static StreamResult create (@Nullable @WillNotClose final OutputStream aOS)
  {
    return new StreamResult (aOS);
  }

  /**
   * Create a {@link StreamResult} from a {@link Writer}.
   *
   * @param aWriter
   *        The writer to write to. May be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static StreamResult create (@Nullable @WillNotClose final Writer aWriter)
  {
    return new StreamResult (aWriter);
  }

  /**
   * Create a {@link DOMResult} from a {@link Node}.
   *
   * @param aNode
   *        The DOM node to use as the result target. May be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static DOMResult create (@Nullable final Node aNode)
  {
    return new DOMResult (aNode);
  }
}
