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
package com.helger.commons.io;

import java.io.File;
import java.nio.file.Path;

import com.helger.annotation.style.IsSPIImplementation;
import com.helger.commons.equals.IEqualsImplementation;
import com.helger.commons.equals.IEqualsImplementationRegistrarSPI;
import com.helger.commons.equals.IEqualsImplementationRegistry;
import com.helger.commons.io.file.FileHelper;
import com.helger.commons.io.file.PathHelper;

import jakarta.annotation.Nonnull;

/**
 * This class registers the default equals implementations for IO related classes. The
 * implementations in here should be aligned with the implementations in the
 * {@link com.helger.commons.io.IOHashCodeImplementationRegistrarSPI}
 *
 * @author Philip Helger
 * @deprecated For simplicity
 */
@IsSPIImplementation
@Deprecated (forRemoval = true, since = "12.0.0")
public final class IOEqualsImplementationRegistrarSPI implements IEqualsImplementationRegistrarSPI
{
  public void registerEqualsImplementations (@Nonnull final IEqualsImplementationRegistry aRegistry)
  {
    // Special handling for File
    aRegistry.registerEqualsImplementation (File.class, FileHelper::equalFiles);

    aRegistry.registerEqualsImplementation (Path.class, new IEqualsImplementation <Path> ()
    {
      public boolean areEqual (final Path aObj1, final Path aObj2)
      {
        return PathHelper.equalPaths (aObj1, aObj2);
      }

      @Override
      public boolean implementationEqualsOverridesInterface ()
      {
        return false;
      }
    });
  }
}
