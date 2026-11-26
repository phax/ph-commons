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
package com.helger.xml.stax;

import java.util.List;

import javax.xml.namespace.QName;

import org.jspecify.annotations.NonNull;

/**
 * Decides which parts of a document a {@link ShallowXMLStreamReader} does not report.
 *
 * @author Philip Helger
 * @since 12.9.0
 */
@FunctionalInterface
public interface IShallowElementSelector
{
  /**
   * Check if the passed element should be reported shallow. A shallow element is reported with its
   * name, its attributes and its namespace declarations, but without any content.
   *
   * @param aAncestors
   *        The names of all ancestor elements, starting with the document element and ending with
   *        the parent of the element. Empty for the document element. Never <code>null</code>.
   * @param aElement
   *        The name of the element to check. Never <code>null</code>.
   * @return <code>true</code> if the content of the element should be skipped, <code>false</code>
   *         if it should be reported.
   */
  boolean isShallowElement (@NonNull List <QName> aAncestors, @NonNull QName aElement);

  /**
   * Check if the character data directly inside the current element should be skipped. This does
   * not affect child elements. By default no character data is skipped.
   *
   * @param aElementPath
   *        The names of all open elements, starting with the document element and ending with the
   *        element that directly contains the character data. Never <code>null</code> nor empty.
   * @return <code>true</code> if the character data should be skipped, <code>false</code> if it
   *         should be reported.
   */
  default boolean isSkipText (@NonNull final List <QName> aElementPath)
  {
    return false;
  }
}
