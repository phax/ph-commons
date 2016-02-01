package com.helger.commons.supplementary.tools.collection;

import javax.annotation.Nonnull;

enum ECollectionType
{
  LIST ("ArrayList", "List", "List"),
  VECTOR ("Vector", "List", "Vector"),
  SET ("HashSet", "Set", "Set"),
  ORDERED_SET ("LinkedHashSet", "Set", "OrderedSet"),
  SORTED_SET ("TreeSet", "Set", "SortedSet"),
  MAP ("HashMap", "Map", "Map"),
  ORDERED_MAP ("LinkedHashMap", "Map", "OrderedMap"),
  SORTED_MAP ("TreeMap", "Map", "SortedMap"),
  STACK ("NonBlockingStack", "List", "Stack"),
  QUEUE ("PriorityQueue", "Collection", "Queue");

  final String m_sClassName;
  final String m_sSuffix;
  final String m_sUnmodifiableClassName;

  private ECollectionType (@Nonnull final String sClassName,
                           @Nonnull final String sUnmodifiableClassName,
                           @Nonnull final String sSuffix)
  {
    m_sClassName = sClassName;
    m_sSuffix = sSuffix;
    m_sUnmodifiableClassName = sUnmodifiableClassName;
  }

  public boolean isMap ()
  {
    return this == MAP || this == ORDERED_MAP || this == SORTED_MAP;
  }

  public boolean isSorted ()
  {
    return this == SORTED_SET || this == SORTED_MAP;
  }
}
