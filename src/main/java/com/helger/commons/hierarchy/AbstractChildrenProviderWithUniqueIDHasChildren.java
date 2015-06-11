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
package com.helger.commons.hierarchy;

/**
 * An abstract implementation of the {@link IChildrenProviderWithUniqueID}
 * interface that works with all types that implement {@link IHasChildren}. The
 * {@link #getItemWithID(Object)} method must be implemented by overriding
 * classes.
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        The data type of the keys
 * @param <CHILDTYPE>
 *        The data type of the child objects.
 */
public abstract class AbstractChildrenProviderWithUniqueIDHasChildren <KEYTYPE, CHILDTYPE extends IHasChildren <CHILDTYPE>> extends ChildrenProviderHasChildren <CHILDTYPE> implements IChildrenProviderWithUniqueID <KEYTYPE, CHILDTYPE>
{
  /* empty */
}
