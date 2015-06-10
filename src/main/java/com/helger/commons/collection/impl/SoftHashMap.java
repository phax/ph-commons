package com.helger.commons.collection.impl;

import java.util.HashMap;

/**
 * Soft {@link HashMap} implementation based on
 * http://www.javaspecialists.eu/archive/Issue015.html<br />
 * The entrySet implementation is from org.hypergraphdb.util
 *
 * @author Philip Helger
 * @param <K>
 *        Key type
 * @param <V>
 *        Value type
 */
public class SoftHashMap <K, V> extends AbstractSoftMap <K, V>
{
  public SoftHashMap ()
  {
    super (new HashMap <K, SoftValue <K, V>> ());
  }
}
