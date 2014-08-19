package com.helger.commons.mime;

import java.util.Comparator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.compare.AbstractComparator;
import com.helger.commons.compare.ESortOrder;

public class ComparatorMimeTypeInfoPrimaryMimeType extends AbstractComparator <MimeTypeInfo>
{
  /**
   * Comparator with default sort order and no nested comparator.
   */
  public ComparatorMimeTypeInfoPrimaryMimeType ()
  {
    super ();
  }

  /**
   * Constructor with sort order.
   *
   * @param eSortOrder
   *        The sort order to use. May not be <code>null</code>.
   */
  public ComparatorMimeTypeInfoPrimaryMimeType (@Nonnull final ESortOrder eSortOrder)
  {
    super (eSortOrder);
  }

  /**
   * Comparator with default sort order and a nested comparator.
   *
   * @param aNestedComparator
   *        The nested comparator to be invoked, when the main comparison
   *        resulted in 0.
   */
  public ComparatorMimeTypeInfoPrimaryMimeType (@Nullable final Comparator <? super MimeTypeInfo> aNestedComparator)
  {
    super (aNestedComparator);
  }

  /**
   * Comparator with sort order and a nested comparator.
   *
   * @param eSortOrder
   *        The sort order to use. May not be <code>null</code>.
   * @param aNestedComparator
   *        The nested comparator to be invoked, when the main comparison
   *        resulted in 0.
   */
  public ComparatorMimeTypeInfoPrimaryMimeType (@Nonnull final ESortOrder eSortOrder,
                                                @Nullable final Comparator <? super MimeTypeInfo> aNestedComparator)
  {
    super (eSortOrder, aNestedComparator);
  }

  @Override
  protected int mainCompare (@Nonnull final MimeTypeInfo aElement1, @Nonnull final MimeTypeInfo aElement2)
  {
    return aElement1.getPrimaryMimeType ().compareToIgnoreCase (aElement2.getPrimaryMimeType ());
  }
}
