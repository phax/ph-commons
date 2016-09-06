package com.helger.commons.error.text;

import java.io.Serializable;

import com.helger.commons.text.display.IHasDisplayText;

/**
 * Base interface for objects having an error text. Compared to
 * {@link IHasDisplayText} it also implements {@link Serializable}.
 *
 * @author Philip Helger
 */
@FunctionalInterface
public interface IHasErrorText extends IHasDisplayText, Serializable
{
  /* empty */
}
