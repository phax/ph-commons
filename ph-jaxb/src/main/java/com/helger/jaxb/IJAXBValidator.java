package com.helger.jaxb;

import javax.annotation.Nonnull;

import com.helger.commons.error.IResourceErrorGroup;

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
   * @return The validation results. Never <code>null</code>.
   */
  @Nonnull
  IResourceErrorGroup validate (@Nonnull JAXBTYPE aJAXBDocument);
}
