/*
 * Copyright (C) 2014-2024 Philip Helger (www.helger.com)
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
package com.helger.commons.log;

import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Conditional logger activity interface
 *
 * @author Philip Helger
 * @since 11.1.4
 */
public interface IConditionalLogger
{
  void trace (@Nonnull Supplier <String> aMsgSupplier);

  void trace (@Nonnull Supplier <String> aMsgSupplier, @Nullable Exception ex);

  void debug (@Nonnull Supplier <String> aMsgSupplier);

  void debug (@Nonnull Supplier <String> aMsgSupplier, @Nullable Exception ex);

  void info (@Nonnull String sMsg);

  void info (@Nonnull Supplier <String> aMsgSupplier);

  void info (@Nonnull String sMsg, @Nullable Exception ex);

  void info (@Nonnull Supplier <String> aMsgSupplier, @Nullable Exception ex);

  void warn (@Nonnull String sMsg);

  void warn (@Nonnull Supplier <String> aMsgSupplier);

  void warn (@Nonnull String sMsg, @Nullable Exception ex);

  void warn (@Nonnull Supplier <String> aMsgSupplier, @Nullable Exception ex);

  void error (@Nonnull String sMsg);

  void error (@Nonnull Supplier <String> aMsgSupplier);

  void error (@Nonnull String sMsg, @Nullable Exception ex);

  void error (@Nonnull Supplier <String> aMsgSupplier, @Nullable Exception ex);
}
