package com.helger.commons.mutable;

/**
 * Base implementation class for {@link IMutableInteger} extending
 * {@link Number} class.
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        Real implementation type
 */
public abstract class AbstractMutableInteger <IMPLTYPE extends AbstractMutableInteger <IMPLTYPE>> extends AbstractMutableNumeric <IMPLTYPE> implements IMutableInteger <IMPLTYPE>
{
  /* empty */
}
