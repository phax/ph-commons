package com.helger.cli2;

import java.io.Serializable;

/**
 * Base interface for {@link Option} and {@link OptionGroup}.
 *
 * @author Philip Helger
 */
public interface IOptionBase extends Serializable
{
  /**
   * @return <code>true</code> if this element is required, <code>false</code>
   *         if not.
   */
  boolean isRequired ();
}
