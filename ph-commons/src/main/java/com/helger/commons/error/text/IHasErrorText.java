package com.helger.commons.error.text;

import com.helger.commons.annotation.MustImplementEqualsAndHashcode;
import com.helger.commons.text.display.IHasDisplayText;

/**
 * Base interface for objects having an error text. Compared to
 * {@link IHasDisplayText} it is required to implement equals and hashCode.
 *
 * @author Philip Helger
 */
@FunctionalInterface
@MustImplementEqualsAndHashcode
public interface IHasErrorText extends IHasDisplayText
{
  /* empty */
}
