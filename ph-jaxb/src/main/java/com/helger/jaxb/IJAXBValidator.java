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
package com.helger.jaxb;

import javax.annotation.Nonnull;

import com.helger.commons.error.list.ErrorList;
import com.helger.commons.error.list.IErrorList;

/**
 * Interface for validating JAXB documents.
 *
 * @author Philip Helger
 * @param <JAXBTYPE>
 *        The JAXB type to be written
 */
public interface IJAXBValidator <JAXBTYPE>
{
  /**
   * @return <code>true</code> if an eventually configured XML Schema should be
   *         used, <code>false</code> to explicitly disable the usage of XML
   *         Schema.
   * @since 11.0.3
   */
  boolean isUseSchema ();

  /**
   * Check if the passed JAXB document is valid according to the XSD or not.
   *
   * @param aJAXBDocument
   *        The JAXB document to be validated. May not be <code>null</code>.
   * @return <code>true</code> if the document is valid, <code>false</code> if
   *         not.
   * @see #validate(Object)
   */
  default boolean isValid (@Nonnull final JAXBTYPE aJAXBDocument)
  {
    return validate (aJAXBDocument).containsNoError ();
  }

  /**
   * Validate the passed JAXB document.
   *
   * @param aJAXBDocument
   *        The JAXB document to be validated. May not be <code>null</code>.
   * @param aErrorList
   *        The error list to be filled. May not be <code>null</code>.
   * @since v9.3.7
   */
  void validate (@Nonnull JAXBTYPE aJAXBDocument, @Nonnull ErrorList aErrorList);

  /**
   * Validate the passed JAXB document.
   *
   * @param aJAXBDocument
   *        The JAXB document to be validated. May not be <code>null</code>.
   * @return The validation results. Never <code>null</code>.
   */
  @Nonnull
  default IErrorList validate (@Nonnull final JAXBTYPE aJAXBDocument)
  {
    final ErrorList aErrorList = new ErrorList ();
    validate (aJAXBDocument, aErrorList);
    return aErrorList;
  }
}
