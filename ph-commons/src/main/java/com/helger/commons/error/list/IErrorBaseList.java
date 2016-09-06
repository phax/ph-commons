package com.helger.commons.error.list;

import java.io.Serializable;

import com.helger.commons.error.IError;
import com.helger.commons.error.level.IHasErrorLevels;
import com.helger.commons.lang.IHasSize;

/**
 * Base list
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        Implementation type
 */
public interface IErrorBaseList <IMPLTYPE extends IError <IMPLTYPE>>
                                extends IHasErrorLevels <IMPLTYPE>, IHasSize, Serializable
{}
