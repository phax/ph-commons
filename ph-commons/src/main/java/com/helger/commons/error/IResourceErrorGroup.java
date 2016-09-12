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
package com.helger.commons.error;

import java.util.function.Consumer;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.ext.ICommonsList;
import com.helger.commons.error.level.EErrorLevel;
import com.helger.commons.error.list.IErrorBaseList;
import com.helger.commons.lang.IHasSize;

/**
 * Contains a list of resource errors and some sanity access methods.
 *
 * @author Philip Helger
 * @deprecated Use {@link com.helger.commons.error.list.IErrorList} instead.
 */
@Deprecated
public interface IResourceErrorGroup extends IErrorBaseList <IResourceError>, IHasSize
{
  /**
   * Get a resource error group containing only the failure elements. All error
   * levels except {@link EErrorLevel#SUCCESS} are considered to be a failure!
   *
   * @return A resource error group containing only the failures.
   */
  @Nonnull
  default IResourceErrorGroup getAllFailures ()
  {
    final ResourceErrorGroup ret = new ResourceErrorGroup ();
    findAll (IResourceError::isFailure, ret::addResourceError);
    return ret;
  }

  /**
   * Get a resource error group containing only the error elements. All error
   * levels &ge; {@link EErrorLevel#ERROR} are considered to be an error!
   *
   * @return A resource error group containing only the errors.
   */
  @Nonnull
  default IResourceErrorGroup getAllErrors ()
  {
    final ResourceErrorGroup ret = new ResourceErrorGroup ();
    findAll (IResourceError::isError, ret::addResourceError);
    return ret;
  }

  /**
   * Get a list of all contained resource errors.
   *
   * @return A non-<code>null</code> list of all contained error objects
   */
  @Nonnull
  @ReturnsMutableCopy
  ICommonsList <IResourceError> getAllResourceErrors ();

  /**
   * Call the provided consumer for all contained resource errors.
   *
   * @param aConsumer
   *        The consumer to be invoked. May not be <code>null</code>. May only
   *        perform reading actions!
   * @deprecated Use {@link #forEach(Consumer)} instead
   */
  @Deprecated
  default void forEachResourceError (@Nonnull final Consumer <? super IResourceError> aConsumer)
  {
    getAllResourceErrors ().forEach (aConsumer);
  }
}
