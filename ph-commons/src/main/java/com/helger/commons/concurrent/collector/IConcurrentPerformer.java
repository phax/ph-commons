package com.helger.commons.concurrent.collector;

import javax.annotation.Nullable;

@FunctionalInterface
public interface IConcurrentPerformer <DATATYPE>
{
  void runAsync (@Nullable DATATYPE aCurrentObject) throws Exception;
}
