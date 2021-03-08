package com.hugojosefson.mc.worldrescue.schedulable;

import org.springframework.stereotype.Component;

@Component
public interface Schedulable extends Runnable {
  String getName();
  boolean shouldSchedule();
  long getDelay();
  long getInterval();
}
