package com.helger.commons.pool;

import java.util.function.Supplier;

import javax.annotation.Nonnull;

import com.helger.commons.state.ESuccess;

/**
 * An extended
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        the type of results supplied by this supplier
 * @since 11.1.0
 */
public interface IObjectPoolSupplier <DATATYPE>
{
  /**
   * Create a new object.
   *
   * @return A new object of data type. Never <code>null</code>.
   */
  @Nonnull
  DATATYPE create ();

  /**
   * Called when an existing object is borrowed from the pool. If activation
   * failed, a new object will be created.
   *
   * @param aItem
   *        The item to be borrowed. Never <code>null</code>.
   * @return {@link ESuccess#SUCCESS} if the object can be reused,
   *         {@link ESuccess#FAILURE} if not.
   */
  @Nonnull
  ESuccess activate (@Nonnull DATATYPE aItem);

  /**
   * Called when an object is returned to the pool.
   *
   * @param aItem
   *        The item to be returned. Never <code>null</code>.
   */
  void passivate (@Nonnull DATATYPE aItem);

  /**
   * Wrapper around {@link Supplier} to create an {@link IObjectPoolSupplier}.
   *
   * @param <T>
   *        Type the object pool is supplying.
   * @param aSupplier
   *        The supplier to wrap. May not be <code>null</code>.
   * @return A new instance of {@link IObjectPoolSupplier}.
   */
  @Nonnull
  static <T> IObjectPoolSupplier <T> wrap (@Nonnull final Supplier <? extends T> aSupplier)
  {
    return new IObjectPoolSupplier <> ()
    {
      @Nonnull
      public T create ()
      {
        return aSupplier.get ();
      }

      @Nonnull
      public ESuccess activate (@Nonnull final T aItem)
      {
        // empty
        return ESuccess.SUCCESS;
      }

      public void passivate (@Nonnull final T aItem)
      {
        // empty
      }
    };
  }
}
