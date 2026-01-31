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
package com.helger.base.log;

import java.util.function.Supplier;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Conditional logger activity interface
 *
 * @author Philip Helger
 * @since 11.1.4
 */
public interface IConditionalLogger
{
  void trace (@NonNull Supplier <String> aMsgSupplier);

  void trace (@NonNull Supplier <String> aMsgSupplier, @Nullable Exception ex);

  void debug (@NonNull Supplier <String> aMsgSupplier);

  void debug (@NonNull Supplier <String> aMsgSupplier, @Nullable Exception ex);

  void info (@NonNull String sMsg);

  void info (@NonNull Supplier <String> aMsgSupplier);

  void info (@NonNull String sMsg, @Nullable Exception ex);

  void info (@NonNull Supplier <String> aMsgSupplier, @Nullable Exception ex);

  void warn (@NonNull String sMsg);

  void warn (@NonNull Supplier <String> aMsgSupplier);

  void warn (@NonNull String sMsg, @Nullable Exception ex);

  void warn (@NonNull Supplier <String> aMsgSupplier, @Nullable Exception ex);

  void error (@NonNull String sMsg);

  void error (@NonNull Supplier <String> aMsgSupplier);

  void error (@NonNull String sMsg, @Nullable Exception ex);

  void error (@NonNull Supplier <String> aMsgSupplier, @Nullable Exception ex);
}
