/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.dao.wal;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.CodingStyleguideUnaware;
import com.helger.commons.annotation.DevelopersNote;
import com.helger.commons.annotation.ELockType;
import com.helger.commons.annotation.IsLocked;
import com.helger.commons.annotation.MustBeLocked;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.callback.CallbackList;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.CommonsLinkedHashMap;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.collection.impl.ICommonsSet;
import com.helger.commons.functional.IPredicate;
import com.helger.commons.id.IHasID;
import com.helger.commons.io.relative.IFileRelativeIO;
import com.helger.commons.lang.ClassHelper;
import com.helger.commons.state.EChange;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;
import com.helger.commons.wrapper.Wrapper;
import com.helger.dao.DAOException;
import com.helger.dao.EDAOActionType;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroDocument;
import com.helger.xml.microdom.convert.MicroTypeConverter;

/**
 * Base class for WAL based DAO that uses a simple {@link ICommonsMap} for data
 * storage.
 *
 * @author Philip Helger
 * @param <INTERFACETYPE>
 *        Interface type to be handled
 * @param <IMPLTYPE>
 *        Implementation type to be handled
 */
@ThreadSafe
public abstract class AbstractMapBasedWALDAO <INTERFACETYPE extends IHasID <String> & Serializable, IMPLTYPE extends INTERFACETYPE>
                                             extends
                                             AbstractWALDAO <IMPLTYPE> implements
                                             IMapBasedDAO <INTERFACETYPE>
{
  /**
   * Extensible constructor parameter builder. Must be static because it is used
   * in the constructor and no <code>this</code> is present.
   *
   * @author Philip Helger
   * @param <IMPLTYPE>
   *        Implementation type to use.
   */
  public static final class InitSettings <IMPLTYPE>
  {
    private boolean m_bDoInitialRead = true;
    private Supplier <ICommonsMap <String, IMPLTYPE>> m_aMapSupplier = CommonsHashMap::new;
    private IPredicate <IMicroElement> m_aReadElementFilter = IPredicate.all ();

    @Nonnull
    public InitSettings <IMPLTYPE> setDoInitialRead (final boolean bDoInitialRead)
    {
      m_bDoInitialRead = bDoInitialRead;
      return this;
    }

    @Nonnull
    public InitSettings <IMPLTYPE> setMapSupplier (@Nonnull final Supplier <ICommonsMap <String, IMPLTYPE>> aMapSupplier)
    {
      m_aMapSupplier = ValueEnforcer.notNull (aMapSupplier, "MapSupplier");
      return this;
    }

    @Nonnull
    public InitSettings <IMPLTYPE> setOrderedMapSupplier ()
    {
      return setMapSupplier (CommonsLinkedHashMap::new);
    }

    @Nonnull
    public InitSettings <IMPLTYPE> setReadElementFilter (@Nonnull final IPredicate <IMicroElement> aReadElementFilter)
    {
      m_aReadElementFilter = ValueEnforcer.notNull (aReadElementFilter, "ReadElementFilter");
      return this;
    }
  }

  protected static final String ELEMENT_ROOT = "root";
  protected static final String ELEMENT_ITEM = "item";

  @GuardedBy ("m_aRWLock")
  private final ICommonsMap <String, IMPLTYPE> m_aMap;
  private final CallbackList <IDAOChangeCallback <INTERFACETYPE>> m_aCallbacks = new CallbackList <> ();
  private final IPredicate <IMicroElement> m_aReadElementFilter;

  /**
   * Default constructor. Automatically tries to read the file in the
   * constructor (except this is changed in the init settings). WAL based
   * classes must have a fixed filename!
   *
   * @param aImplClass
   *        Implementation class. May not be <code>null</code>.
   * @param aIO
   *        IO abstraction to be used. May not be <code>null</code>.
   * @param sFilename
   *        The filename to read and write.
   * @param aInitSettings
   *        Optional initialization settings to be used. May not be
   *        <code>null</code>.
   * @throws DAOException
   *         If reading and reading fails
   */
  public AbstractMapBasedWALDAO (@Nonnull final Class <IMPLTYPE> aImplClass,
                                 @Nonnull final IFileRelativeIO aIO,
                                 @Nullable final String sFilename,
                                 @Nonnull final InitSettings <IMPLTYPE> aInitSettings) throws DAOException
  {
    super (aImplClass, aIO, () -> sFilename);
    m_aMap = aInitSettings.m_aMapSupplier.get ();
    m_aReadElementFilter = aInitSettings.m_aReadElementFilter;
    if (aInitSettings.m_bDoInitialRead)
      initialRead ();
  }

  @Override
  @MustBeLocked (ELockType.WRITE)
  protected void onRecoveryCreate (@Nonnull final IMPLTYPE aItem)
  {
    _addItem (aItem, EDAOActionType.CREATE);
  }

  @Override
  @MustBeLocked (ELockType.WRITE)
  protected void onRecoveryUpdate (@Nonnull final IMPLTYPE aItem)
  {
    _addItem (aItem, EDAOActionType.UPDATE);
  }

  @Override
  @MustBeLocked (ELockType.WRITE)
  protected void onRecoveryDelete (@Nonnull final IMPLTYPE aItem)
  {
    m_aMap.remove (aItem.getID (), aItem);
  }

  @Override
  @Nonnull
  protected EChange onRead (@Nonnull final IMicroDocument aDoc)
  {
    // Read all child elements independent of the name - soft migration
    final Class <IMPLTYPE> aDataTypeClass = getDataTypeClass ();
    final Wrapper <EChange> aChange = new Wrapper <> (EChange.UNCHANGED);

    aDoc.getDocumentElement ().forAllChildElements (m_aReadElementFilter, eItem -> {
      final IMPLTYPE aItem = MicroTypeConverter.convertToNative (eItem, aDataTypeClass);
      _addItem (aItem, EDAOActionType.CREATE);
      if (aItem instanceof IDAOReadChangeAware)
        if (((IDAOReadChangeAware) aItem).isReadChanged ())
        {
          // Remember that something was changed while reading
          aChange.set (EChange.CHANGED);
        }
    });
    return aChange.get ();
  }

  @MustBeLocked (ELockType.READ)
  @CodingStyleguideUnaware
  protected final Collection <IMPLTYPE> getAllSortedByKey ()
  {
    return m_aMap.getSortedByKey (Comparator.naturalOrder ()).values ();
  }

  @Override
  @Nonnull
  protected IMicroDocument createWriteData ()
  {
    final IMicroDocument aDoc = new MicroDocument ();
    final IMicroElement eRoot = aDoc.appendElement (ELEMENT_ROOT);
    for (final IMPLTYPE aItem : getAllSortedByKey ())
      eRoot.appendChild (MicroTypeConverter.convertToMicroElement (aItem, ELEMENT_ITEM));
    return aDoc;
  }

  @Nonnull
  @ReturnsMutableObject ("design")
  public CallbackList <IDAOChangeCallback <INTERFACETYPE>> callbacks ()
  {
    return m_aCallbacks;
  }

  /**
   * Add or update an item. Must only be invoked inside a write-lock.
   *
   * @param aItem
   *        The item to be added or updated
   * @param eActionType
   *        The action type. Must be CREATE or UPDATE!
   * @throws IllegalArgumentException
   *         If on CREATE an item with the same ID is already contained. If on
   *         UPDATE an item with the provided ID does NOT exist.
   */
  @MustBeLocked (ELockType.WRITE)
  private void _addItem (@Nonnull final IMPLTYPE aItem, @Nonnull final EDAOActionType eActionType)
  {
    ValueEnforcer.notNull (aItem, "Item");
    ValueEnforcer.isTrue (eActionType == EDAOActionType.CREATE || eActionType == EDAOActionType.UPDATE,
                          "Invalid action type provided!");

    final String sID = aItem.getID ();
    final IMPLTYPE aOldItem = m_aMap.get (sID);
    if (eActionType == EDAOActionType.CREATE)
    {
      if (aOldItem != null)
        throw new IllegalArgumentException (ClassHelper.getClassLocalName (getDataTypeClass ()) +
                                            " with ID '" +
                                            sID +
                                            "' is already in use and can therefore not be created again. Old item = " +
                                            aOldItem +
                                            "; New item = " +
                                            aItem);
    }
    else
    {
      // Update
      if (aOldItem == null)
        throw new IllegalArgumentException (ClassHelper.getClassLocalName (getDataTypeClass ()) +
                                            " with ID '" +
                                            sID +
                                            "' is not yet in use and can therefore not be updated! Updated item = " +
                                            aItem);
    }

    m_aMap.put (sID, aItem);
  }

  @Override
  @MustBeLocked (ELockType.WRITE)
  @Deprecated
  @DevelopersNote ("Avoid that this method is overridden!")
  protected final void markAsChanged (@Nonnull final IMPLTYPE aModifiedElement,
                                      @Nonnull final EDAOActionType eActionType)
  {
    super.markAsChanged (aModifiedElement, eActionType);
  }

  /**
   * Add an item including invoking the callback. Must only be invoked inside a
   * write-lock.
   *
   * @param aNewItem
   *        The item to be added. May not be <code>null</code>.
   * @return The passed parameter as-is
   * @throws IllegalArgumentException
   *         If an item with the same ID is already contained
   */
  @MustBeLocked (ELockType.WRITE)
  protected final IMPLTYPE internalCreateItem (@Nonnull final IMPLTYPE aNewItem)
  {
    // Add to map
    _addItem (aNewItem, EDAOActionType.CREATE);
    // Trigger save changes
    super.markAsChanged (aNewItem, EDAOActionType.CREATE);
    // Invoke callbacks
    m_aCallbacks.forEach (aCB -> aCB.onCreateItem (aNewItem));
    return aNewItem;
  }

  /**
   * Update an existing item including invoking the callback. Must only be
   * invoked inside a write-lock.
   *
   * @param aItem
   *        The item to be updated. May not be <code>null</code>.
   * @throws IllegalArgumentException
   *         If no item with the same ID is already contained
   */
  @MustBeLocked (ELockType.WRITE)
  protected final void internalUpdateItem (@Nonnull final IMPLTYPE aItem)
  {
    // Add to map - ensure to overwrite any existing
    _addItem (aItem, EDAOActionType.UPDATE);
    // Trigger save changes
    super.markAsChanged (aItem, EDAOActionType.UPDATE);
    // Invoke callbacks
    m_aCallbacks.forEach (aCB -> aCB.onUpdateItem (aItem));
  }

  /**
   * Delete the item by removing it from the map. If something was remove the
   * onDeleteItem callback is invoked. Must only be invoked inside a write-lock.
   *
   * @param sID
   *        The ID to be removed. May be <code>null</code>.
   * @return The deleted item. If <code>null</code> no such item was found and
   *         therefore nothing was removed.
   */
  @MustBeLocked (ELockType.WRITE)
  @Nullable
  protected final IMPLTYPE internalDeleteItem (@Nullable final String sID)
  {
    if (StringHelper.hasNoText (sID))
      return null;

    final IMPLTYPE aDeletedItem = m_aMap.remove (sID);
    if (aDeletedItem == null)
      return null;

    // Trigger save changes
    super.markAsChanged (aDeletedItem, EDAOActionType.DELETE);
    // Invoke callbacks
    m_aCallbacks.forEach (aCB -> aCB.onDeleteItem (aDeletedItem));
    return aDeletedItem;
  }

  /**
   * Mark an item as "deleted" without actually deleting it from the map. This
   * method only triggers the update action but does not alter the item. Must
   * only be invoked inside a write-lock.
   *
   * @param aItem
   *        The item that was marked as "deleted"
   */
  @MustBeLocked (ELockType.WRITE)
  protected final void internalMarkItemDeleted (@Nonnull final IMPLTYPE aItem)
  {
    // Trigger save changes
    super.markAsChanged (aItem, EDAOActionType.UPDATE);
    // Invoke callbacks
    m_aCallbacks.forEach (aCB -> aCB.onMarkItemDeleted (aItem));
  }

  /**
   * Mark an item as "no longer deleted" without actually adding it to the map.
   * This method only triggers the update action but does not alter the item.
   * Must only be invoked inside a write-lock.
   *
   * @param aItem
   *        The item that was marked as "no longer deleted"
   */
  @MustBeLocked (ELockType.WRITE)
  protected final void internalMarkItemUndeleted (@Nonnull final IMPLTYPE aItem)
  {
    // Trigger save changes
    super.markAsChanged (aItem, EDAOActionType.UPDATE);
    // Invoke callbacks
    m_aCallbacks.forEach (aCB -> aCB.onMarkItemUndeleted (aItem));
  }

  /**
   * Remove all items without triggering any callback. Must only be invoked
   * inside a write-lock.
   *
   * @return {@link EChange#CHANGED} if something was contained,
   *         {@link EChange#UNCHANGED} otherwise.
   */
  @MustBeLocked (ELockType.WRITE)
  @Nonnull
  protected final EChange internalRemoveAllItemsNoCallback ()
  {
    return m_aMap.removeAll ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public final <T> ICommonsList <T> getNone ()
  {
    return new CommonsArrayList <> ();
  }

  @Nonnull
  @ReturnsMutableCopy
  @IsLocked (ELockType.READ)
  public final ICommonsList <INTERFACETYPE> getAll ()
  {
    // Use new CommonsArrayList to get the return type to NOT use "? extends
    // INTERFACETYPE"
    return m_aRWLock.readLocked ( () -> new CommonsArrayList <> (m_aMap.values ()));
  }

  @Nonnull
  @ReturnsMutableCopy
  @IsLocked (ELockType.READ)
  public final ICommonsList <INTERFACETYPE> getAll (@Nullable final Predicate <? super INTERFACETYPE> aFilter)
  {
    if (aFilter == null)
      return getAll ();

    // Use new CommonsArrayList to get the return type to NOT use "? extends
    // INTERFACETYPE"
    final ICommonsList <INTERFACETYPE> ret = new CommonsArrayList <> ();
    // (Runnable) cast for Java 9
    m_aRWLock.readLocked ((Runnable) () -> CollectionHelper.findAll (m_aMap.values (), aFilter, ret::add));
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  @IsLocked (ELockType.READ)
  protected final Iterable <IMPLTYPE> internalDirectGetAll ()
  {
    return m_aRWLock.readLocked (m_aMap::values);
  }

  @Nonnull
  @ReturnsMutableCopy
  @IsLocked (ELockType.READ)
  protected final ICommonsList <IMPLTYPE> internalGetAll (@Nullable final Predicate <? super IMPLTYPE> aFilter)
  {
    return m_aRWLock.readLocked ( () -> m_aMap.copyOfValues (aFilter));
  }

  @IsLocked (ELockType.READ)
  public final void findAll (@Nullable final Predicate <? super INTERFACETYPE> aFilter,
                             @Nonnull final Consumer <? super INTERFACETYPE> aConsumer)
  {
    // (Runnable) cast for Java 9
    m_aRWLock.readLocked ((Runnable) () -> CollectionHelper.findAll (m_aMap.values (), aFilter, aConsumer));
  }

  @Nonnull
  @ReturnsMutableCopy
  @IsLocked (ELockType.READ)
  public final <RETTYPE> ICommonsList <RETTYPE> getAllMapped (@Nullable final Predicate <? super INTERFACETYPE> aFilter,
                                                              @Nonnull final Function <? super INTERFACETYPE, ? extends RETTYPE> aMapper)
  {
    return m_aRWLock.readLocked ( () -> m_aMap.copyOfValuesMapped (aFilter, aMapper));
  }

  @IsLocked (ELockType.READ)
  public final <RETTYPE> void findAllMapped (@Nullable final Predicate <? super INTERFACETYPE> aFilter,
                                             @Nonnull final Function <? super INTERFACETYPE, ? extends RETTYPE> aMapper,
                                             @Nonnull final Consumer <? super RETTYPE> aConsumer)
  {
    // (Runnable) cast for Java 9
    m_aRWLock.readLocked ((Runnable) () -> CollectionHelper.findAllMapped (m_aMap.values (),
                                                                           aFilter,
                                                                           aMapper,
                                                                           aConsumer));
  }

  @IsLocked (ELockType.READ)
  @Nullable
  public final INTERFACETYPE findFirst (@Nullable final Predicate <? super INTERFACETYPE> aFilter)
  {
    return m_aRWLock.readLocked ( () -> CollectionHelper.findFirst (m_aMap.values (), aFilter));
  }

  @Nullable
  @IsLocked (ELockType.READ)
  public final <RETTYPE> RETTYPE findFirstMapped (@Nullable final Predicate <? super INTERFACETYPE> aFilter,
                                                  @Nonnull final Function <? super INTERFACETYPE, ? extends RETTYPE> aMapper)
  {
    return m_aRWLock.readLocked ( () -> CollectionHelper.findFirstMapped (m_aMap.values (), aFilter, aMapper));
  }

  @Override
  @IsLocked (ELockType.READ)
  public final boolean isNotEmpty ()
  {
    return m_aRWLock.readLocked (m_aMap::isNotEmpty);
  }

  @IsLocked (ELockType.READ)
  public final boolean containsAny (@Nullable final Predicate <? super INTERFACETYPE> aFilter)
  {
    return m_aRWLock.readLocked ( () -> CollectionHelper.containsAny (m_aMap.values (), aFilter));
  }

  @IsLocked (ELockType.READ)
  public final boolean isEmpty ()
  {
    return m_aRWLock.readLocked (m_aMap::isEmpty);
  }

  @IsLocked (ELockType.READ)
  public final boolean containsNone (@Nullable final Predicate <? super INTERFACETYPE> aFilter)
  {
    return m_aRWLock.readLocked ( () -> CollectionHelper.containsNone (m_aMap.values (), aFilter));
  }

  @IsLocked (ELockType.READ)
  public final boolean containsOnly (@Nullable final Predicate <? super INTERFACETYPE> aFilter)
  {
    return m_aRWLock.readLocked ( () -> CollectionHelper.containsOnly (m_aMap.values (), aFilter));
  }

  @IsLocked (ELockType.READ)
  public final void forEach (@Nullable final BiConsumer <? super String, ? super INTERFACETYPE> aConsumer)
  {
    m_aRWLock.readLocked ( () -> m_aMap.forEach (aConsumer));
  }

  @IsLocked (ELockType.READ)
  public final void forEach (@Nullable final BiPredicate <? super String, ? super INTERFACETYPE> aFilter,
                             @Nullable final BiConsumer <? super String, ? super INTERFACETYPE> aConsumer)
  {
    m_aRWLock.readLocked ( () -> m_aMap.forEach (aFilter, aConsumer));
  }

  @IsLocked (ELockType.READ)
  public final void forEachKey (@Nullable final Consumer <? super String> aConsumer)
  {
    m_aRWLock.readLocked ( () -> m_aMap.forEachKey (aConsumer));
  }

  @IsLocked (ELockType.READ)
  public final void forEachKey (@Nullable final Predicate <? super String> aFilter,
                                @Nullable final Consumer <? super String> aConsumer)
  {
    m_aRWLock.readLocked ( () -> m_aMap.forEachKey (aFilter, aConsumer));
  }

  @IsLocked (ELockType.READ)
  public final void forEachValue (@Nullable final Consumer <? super INTERFACETYPE> aConsumer)
  {
    m_aRWLock.readLocked ( () -> m_aMap.forEachValue (aConsumer));
  }

  @IsLocked (ELockType.READ)
  protected final void internalForEachValue (@Nullable final Consumer <? super IMPLTYPE> aConsumer)
  {
    m_aRWLock.readLocked ( () -> m_aMap.forEachValue (aConsumer));
  }

  @IsLocked (ELockType.READ)
  public final void forEachValue (@Nullable final Predicate <? super INTERFACETYPE> aFilter,
                                  @Nullable final Consumer <? super INTERFACETYPE> aConsumer)
  {
    m_aRWLock.readLocked ( () -> m_aMap.forEachValue (aFilter, aConsumer));
  }

  @IsLocked (ELockType.READ)
  protected final void internalForEachValue (@Nullable final Predicate <? super IMPLTYPE> aFilter,
                                             @Nullable final Consumer <? super IMPLTYPE> aConsumer)
  {
    m_aRWLock.readLocked ( () -> m_aMap.forEachValue (aFilter, aConsumer));
  }

  @Nullable
  @MustBeLocked (ELockType.READ)
  protected final IMPLTYPE getOfIDLocked (@Nullable final String sID)
  {
    if (StringHelper.hasNoText (sID))
      return null;

    return m_aMap.get (sID);
  }

  @Nullable
  @IsLocked (ELockType.READ)
  protected final IMPLTYPE getOfID (@Nullable final String sID)
  {
    if (StringHelper.hasNoText (sID))
      return null;

    return m_aRWLock.readLocked ( () -> m_aMap.get (sID));
  }

  /**
   * Get the item at the specified index. This method only returns defined
   * results if a CommonLinkedHashMap is used for data storage.
   *
   * @param nIndex
   *        The index to retrieve. Should be &ge; 0.
   * @return <code>null</code> if an invalid index was provided.
   */
  @Nullable
  @IsLocked (ELockType.READ)
  protected final INTERFACETYPE getAtIndex (@Nonnegative final int nIndex)
  {
    return m_aRWLock.readLocked ( () -> CollectionHelper.getAtIndex (m_aMap.values (), nIndex));
  }

  @IsLocked (ELockType.READ)
  public final boolean containsWithID (@Nullable final String sID)
  {
    if (StringHelper.hasNoText (sID))
      return false;

    return m_aRWLock.readLocked ( () -> m_aMap.containsKey (sID));
  }

  @IsLocked (ELockType.READ)
  public final boolean containsAllIDs (@Nullable final Iterable <String> aIDs)
  {
    if (aIDs != null)
    {
      m_aRWLock.readLock ().lock ();
      try
      {
        for (final String sID : aIDs)
          if (!m_aMap.containsKey (sID))
            return false;
      }
      finally
      {
        m_aRWLock.readLock ().unlock ();
      }
    }
    return true;
  }

  @Nonnull
  @ReturnsMutableCopy
  public final ICommonsSet <String> getAllIDs ()
  {
    return m_aRWLock.readLocked ((Supplier <ICommonsSet <String>>) m_aMap::copyOfKeySet);
  }

  @Nonnegative
  public final int size ()
  {
    return m_aRWLock.readLocked (m_aMap::size);
  }

  @Nonnegative
  public final int getCount (@Nullable final Predicate <? super INTERFACETYPE> aFilter)
  {
    return m_aRWLock.readLocked ( () -> CollectionHelper.getCount (m_aMap.values (), aFilter));
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("Map", m_aMap).getToString ();
  }
}
