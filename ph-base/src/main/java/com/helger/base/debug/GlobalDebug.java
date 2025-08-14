package com.helger.base.debug;

import java.util.concurrent.atomic.AtomicBoolean;

import com.helger.annotation.concurrent.ThreadSafe;

/**
 * Global class for handling the following typical application modes:
 * <ul>
 * <li>debug - to be enabled during development</li>
 * <li>production - to be enabled once an application is running in a production environment</li>
 * </ul>
 *
 * @author Philip
 */
@ThreadSafe
public class GlobalDebug
{
  /**
   * By default debug mode is disabled
   */
  public static final boolean DEFAULT_DEBUG_MODE = false;
  /**
   * By default production mode is disabled
   */
  public static final boolean DEFAULT_PRODUCTION_MODE = false;

  private static final AtomicBoolean DEBUG_MODE = new AtomicBoolean (DEFAULT_DEBUG_MODE);
  private static final AtomicBoolean PRODUCTION_MODE = new AtomicBoolean (DEFAULT_PRODUCTION_MODE);

  /**
   * Constructor
   */
  public GlobalDebug ()
  {}

  // to set it per dependency injection
  public void setDebugMode (final boolean bDebugMode)
  {
    setDebugModeDirect (bDebugMode);
  }

  // to set it per dependency injection
  public void setProductionMode (final boolean bProductionMode)
  {
    setProductionModeDirect (bProductionMode);
  }

  /**
   * Enable or disable debug mode. If debug mode is disabled, also trace mode is disabled.
   *
   * @param bDebugMode
   *        <code>true</code> to enable, <code>false</code> to disable
   */
  public static void setDebugModeDirect (final boolean bDebugMode)
  {
    DEBUG_MODE.set (bDebugMode);
  }

  /**
   * Enable or disable production mode. If production mode is enabled, also trace mode and debug
   * mode are disabled.
   *
   * @param bProductionMode
   *        <code>true</code> to enable, <code>false</code> to disable
   */
  public static void setProductionModeDirect (final boolean bProductionMode)
  {
    PRODUCTION_MODE.set (bProductionMode);

    // If enabling production mode, disable debug mode
    if (bProductionMode)
      setDebugModeDirect (false);
  }

  /**
   * @return <code>true</code> if debug mode is active, <code>false</code> if not
   */
  public static boolean isDebugMode ()
  {
    return DEBUG_MODE.get ();
  }

  /**
   * @return <code>true</code> if production mode is active, <code>false</code> if not
   */
  public static boolean isProductionMode ()
  {
    return PRODUCTION_MODE.get ();
  }
}
