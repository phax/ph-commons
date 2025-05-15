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
package com.helger.xml.microdom.convert;

import com.helger.annotation.Nonnull;
import com.helger.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.xml.microdom.IMicroElement;

/**
 * Interface to be implemented to marshal between IMicroElement and a native
 * object.
 *
 * @author Philip Helger
 * @param <T>
 *        Data type to handle.
 */
public interface IMicroTypeConverter <T>
{
  /**
   * Convert the passed object to a micro element using the specified tag name
   * and a <code>null</code> namespace URI.
   *
   * @param aObject
   *        The object to be converted. May not be <code>null</code>.
   * @param sTagName
   *        The tag name to be used. May neither be <code>null</code> nor empty.
   * @return <code>null</code> in case creation failed. A micro element with the
   *         specified namespace and tag name otherwise.
   */
  @Nullable
  default IMicroElement convertToMicroElement (@Nonnull final T aObject, @Nonnull @Nonempty final String sTagName)
  {
    return convertToMicroElement (aObject, (String) null, sTagName);
  }

  /**
   * Convert the passed object to a micro element using the specified tag name
   *
   * @param aObject
   *        The object to be converted. May not be <code>null</code>.
   * @param sNamespaceURI
   *        The namespace URI for the element to be created. May be
   *        <code>null</code>.
   * @param sTagName
   *        The tag name to be used. May neither be <code>null</code> nor empty.
   * @return <code>null</code> in case creation failed. A micro element with the
   *         specified namespace and tag name otherwise.
   */
  @Nullable
  IMicroElement convertToMicroElement (@Nonnull T aObject, @Nullable String sNamespaceURI, @Nonnull @Nonempty String sTagName);

  /**
   * Convert the passed object to a native element.
   *
   * @param aElement
   *        The micro element to be converted.
   * @return <code>null</code> if conversion to a native object failed.
   */
  @Nullable
  T convertToNative (@Nonnull IMicroElement aElement);
}
