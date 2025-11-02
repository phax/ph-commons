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
package com.helger.io.rt;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Properties;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.concurrent.Immutable;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.rt.NonBlockingProperties;
import com.helger.base.rt.PropertiesHelper;
import com.helger.io.resource.FileSystemResource;
import com.helger.io.resource.IReadableResource;

/**
 * Helper class to ease the use of {@link Properties} class.
 *
 * @author Philip Helger
 */
@Immutable
public final class PropertiesLoader
{
  // No logger here!

  private PropertiesLoader ()
  {}

  @Nullable
  public static NonBlockingProperties loadProperties (@NonNull final File aFile)
  {
    ValueEnforcer.notNull (aFile, "File");

    return loadProperties (new FileSystemResource (aFile));
  }

  @Nullable
  public static NonBlockingProperties loadProperties (@NonNull final File aFile, @NonNull final Charset aCharset)
  {
    ValueEnforcer.notNull (aFile, "File");

    return loadProperties (new FileSystemResource (aFile), aCharset);
  }

  @Nullable
  public static NonBlockingProperties loadProperties (@NonNull final IReadableResource aRes)
  {
    ValueEnforcer.notNull (aRes, "Resource");

    final InputStream aIS = aRes.getInputStream ();
    if (aIS == null)
      return null;
    return PropertiesHelper.loadProperties (aIS);
  }

  @Nullable
  public static NonBlockingProperties loadProperties (@NonNull final IReadableResource aRes,
                                                      @NonNull final Charset aCharset)
  {
    ValueEnforcer.notNull (aRes, "Resource");
    ValueEnforcer.notNull (aCharset, "Charset");

    final Reader aReader = aRes.getReader (aCharset);
    if (aReader == null)
      return null;
    return PropertiesHelper.loadProperties (aReader);
  }
}
