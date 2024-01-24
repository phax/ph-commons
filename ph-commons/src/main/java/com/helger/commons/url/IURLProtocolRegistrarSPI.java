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
package com.helger.commons.url;

import javax.annotation.Nullable;

import com.helger.commons.annotation.IsSPIInterface;
import com.helger.commons.collection.impl.ICommonsSet;

/**
 * Interface for a registrar providing custom URL protocols
 *
 * @author Boris Gregorcic
 * @author Philip Helger
 */
@IsSPIInterface
public interface IURLProtocolRegistrarSPI
{
  /**
   * @return The set of protocols to be registered for this registrar. The
   *         returned set may be <code>null</code> but may not contain
   *         <code>null</code> elements!
   */
  @Nullable
  ICommonsSet <? extends IURLProtocol> getAllProtocols ();
}
