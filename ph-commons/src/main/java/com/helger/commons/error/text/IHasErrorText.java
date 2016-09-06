package com.helger.commons.error.text;

import com.helger.commons.annotation.MustImplementEqualsAndHashcode;
import com.helger.commons.text.display.IHasDisplayText;

/**
 * Base interface for objects having an error text. Compared to
 * {@link IHasDisplayText} it is required to implement equals and hashCode.
 *
 * @author Philip Helger
 */
@MustImplementEqualsAndHashcode
public interface IHasErrorText extends IHasDisplayText
{
  /**
   * @return <code>true</code> if the error text is multilingual,
   *         <code>false</code> otherwise.
   */
  boolean isMultiLingual ();
}
