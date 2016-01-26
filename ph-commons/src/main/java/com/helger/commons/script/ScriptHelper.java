package com.helger.commons.script;

import java.nio.charset.Charset;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.helger.commons.charset.CCharset;

/**
 * Helper class for javax.script package.
 * 
 * @author Philip Helger
 */
@Immutable
public final class ScriptHelper
{
  public static final Charset DEFAULT_SCRIPT_CHARSET = CCharset.CHARSET_ISO_8859_1_OBJ;

  private static final ScriptEngineManager s_aScriptFactory = new ScriptEngineManager ();

  private ScriptHelper ()
  {}

  @Nonnull
  public static ScriptEngine createNashornEngine ()
  {
    // create a Nashorn script engine
    return s_aScriptFactory.getEngineByName ("nashorn");
  }
}
