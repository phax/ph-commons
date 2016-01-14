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
package com.helger.commons.vminit;

import com.helger.commons.annotation.IsSPIInterface;

/**
 * The callback to implemented by VM startup SPIs. Unfortunately you need to
 * manually call {@link VirtualMachineInitializer#runInitialization()} to get
 * this SPI up and running!
 *
 * @author Philip Helger
 */
@IsSPIInterface
public interface IVirtualMachineEventSPI
{
  /**
   * Called upon Java VM initialization.
   *
   * @throws Exception
   *         in case of an error.
   * @see VirtualMachineInitializer#runInitialization()
   */
  void onVirtualMachineStart () throws Exception;

  /**
   * Called upon Java VM shutdown.<br>
   * Note for web applications: this happens when the application server is shut
   * down and not when an application is shut down!
   *
   * @throws Exception
   *         in case of an error.
   */
  void onVirtualMachineStop () throws Exception;
}
