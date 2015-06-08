/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.commons.scope;

import javax.annotation.Nonnull;

/**
 * A listener interfaces that is invoked before a scope is destroyed. If an
 * object implementing this interface is added into a scope, this destruction
 * method is automatically called!
 *
 * @author Philip Helger
 */
public interface IScopeDestructionAware
{
  /**
   * Called before the owning scope is destroyed. You may perform some cleanup
   * work in here.
   *
   * @param aScopeInDestruction
   *        The scope in destruction. Never <code>null</code>.
   * @throws Exception
   *         in case of an error
   */
  void onScopeDestruction (@Nonnull IScope aScopeInDestruction) throws Exception;
}
