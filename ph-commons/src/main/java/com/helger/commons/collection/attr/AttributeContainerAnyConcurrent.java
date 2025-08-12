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
package com.helger.commons.collection.attr;

import java.util.Map;

import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.annotation.style.ReturnsMutableCopy;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Base class for all kind of string-object mapping container. This
 * implementation is thread-safe!
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        Key type
 */
@ThreadSafe
public class AttributeContainerAnyConcurrent <KEYTYPE> extends AttributeContainerConcurrent <KEYTYPE, Object> implements
                                             IAttributeContainerAny <KEYTYPE>
{
  public AttributeContainerAnyConcurrent ()
  {
    super ();
  }

  public AttributeContainerAnyConcurrent (@Nullable final Map <? extends KEYTYPE, ? extends Object> aMap)
  {
    super (aMap);
  }

  @Override
  @Nonnull
  @ReturnsMutableCopy
  public AttributeContainerAnyConcurrent <KEYTYPE> getClone ()
  {
    return new AttributeContainerAnyConcurrent <> (this);
  }
}
