package com.hugojosefson.mc.worldrescue.schedulable;

public interface Schedulable extends Runnable {
  String getName();
  boolean shouldSchedule();
  long getDelay();
  long getInterval();
}
