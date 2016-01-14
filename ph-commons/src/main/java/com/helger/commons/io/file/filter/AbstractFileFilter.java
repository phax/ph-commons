/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
package com.helger.commons.io.file.filter;

import java.io.File;
import java.io.FileFilter;

import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.filter.AbstractFilter;
import com.helger.commons.filter.EFilterMatchingStrategy;
import com.helger.commons.filter.IFilter;

/**
 * Abstract base implementation of {@link FileFilter} with some conversion
 * methods. Also implements {@link IFilter} and forwards the calls to the
 * {@link FileFilter} API.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public abstract class AbstractFileFilter extends AbstractFilter <File> implements IFileFilter
{
  public AbstractFileFilter ()
  {
    // For file filter the matching strategy is "match all"
    setMatchingStrategy (EFilterMatchingStrategy.MATCH_ALL);
  }
}
