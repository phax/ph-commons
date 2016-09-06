package com.helger.commons.error.list;

import java.io.Serializable;

import com.helger.commons.error.IError;
import com.helger.commons.error.level.IHasErrorLevels;

/**
 * Base list containing errors.
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        Implementation type
 * @since 8.5.0
 */
public interface IErrorBaseList <IMPLTYPE extends IError> extends IHasErrorLevels <IMPLTYPE>, Serializable
{
  /* empty */
}
