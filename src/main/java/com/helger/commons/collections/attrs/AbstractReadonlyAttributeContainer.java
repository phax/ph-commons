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
package com.helger.commons.collections.attrs;

import javax.annotation.concurrent.NotThreadSafe;

/**
 * Abstract base class for all kind of string-object mapping container. This
 * implementation provides a default implementation for all things that can be
 * independently implemented from the underlying data structure.
 *
 * @author Philip Helger
 */
@NotThreadSafe
@Deprecated
public abstract class AbstractReadonlyAttributeContainer extends
                                                        AbstractGenericReadonlyAttributeContainer <String, Object> implements
                                                                                                                  IReadonlyAttributeContainer
{
  /* empty */
}
