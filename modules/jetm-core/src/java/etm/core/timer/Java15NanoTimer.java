/*
 *
 * Copyright (c) 2004, 2005, 2006 void.fm
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name void.fm nor the names of its contributors may be
 * used to endorse or promote products derived from this software without specific
 * prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package etm.core.timer;

import etm.core.metadata.TimerMetaData;

/**
 * The Java15NanoTimer uses the new JDK 1.5 System.nanotime.
 *
 * @author void.fm
 * @version $Id: Java15NanoTimer.java,v 1.9 2006/05/23 10:00:45 french_c Exp $
 */
public class Java15NanoTimer implements ExecutionTimer {
  private static final long DEFAULT_TICKS_PER_SECOND = 1000000000L;
  private static final String DESCRIPTION = "Java VM 1.5 System NanoTimer";

  public Java15NanoTimer() {
    // ensure we fail early
    System.nanoTime();
  }

  public long getCurrentTime() {
    return System.nanoTime();
  }

  public long getTicksPerSecond() {
    return DEFAULT_TICKS_PER_SECOND;
  }

  public String toString() {
    return getClass().getName();
  }

  public TimerMetaData getMetaData() {
    return new TimerMetaData(Java15NanoTimer.class,
      DESCRIPTION,
      getTicksPerSecond()
    );
  }
}
