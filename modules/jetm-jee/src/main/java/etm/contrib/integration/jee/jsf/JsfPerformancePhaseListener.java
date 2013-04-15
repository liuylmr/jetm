/*
 *
 * Copyright (c) void.fm
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

package etm.contrib.integration.jee.jsf;

import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmPoint;
import etm.core.util.Log;
import etm.core.util.LogAdapter;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * A JSF phase listener that monitors overall request processing
 * and execution time per phase. Defaults to request uri for
 * outer measurement point unless {@link NameInterceptingActionListener}
 * detects a valid action name.
 *
 * @author void.fm
 * @version $Revision: 372 $
 */
public class JsfPerformancePhaseListener implements PhaseListener {


  public static final String ROOT_ETM_POINT = "ETM__RootRequestPoint";
  private static final String CURRENT_PHASE_POINT = "ETM__CurrentPhasePoint";

  private static final LogAdapter LOG = Log.getLog(JsfPerformancePhaseListener.class);

  public JsfPerformancePhaseListener() {
    LOG.debug("Activated JSF Phase Performance Monitoring.");
  }

  public void beforePhase(PhaseEvent event) {
    FacesContext facesContext = event.getFacesContext();

    if (event.getPhaseId().equals(PhaseId.RESTORE_VIEW)) {
      EtmPoint requestPoint = EtmManager.getEtmMonitor().createPoint(getDefaultRequestName(facesContext));
      facesContext.getAttributes().put(ROOT_ETM_POINT, requestPoint);
    }

    EtmPoint oldPoint = (EtmPoint) facesContext.getAttributes().get(CURRENT_PHASE_POINT);
    if (oldPoint != null) {
      // do some cleanup, should never happen actually
      oldPoint.alterName(oldPoint.getName() + " - uncollected");
      oldPoint.collect();
    }

    String symbolicName = "JSF Phase " + event.getPhaseId().getOrdinal() + " - " + String.valueOf(event.getPhaseId());
    EtmPoint point = EtmManager.getEtmMonitor().createPoint(symbolicName);
    facesContext.getAttributes().put(CURRENT_PHASE_POINT, point);
  }

  public void afterPhase(PhaseEvent event) {
    FacesContext facesContext = event.getFacesContext();

    // stop recording current phase time
    EtmPoint point = (EtmPoint) facesContext.getAttributes().get(CURRENT_PHASE_POINT);
    if (point != null) {
      point.collect();
      facesContext.getAttributes().put(CURRENT_PHASE_POINT, null);
    }

    // stop recording request time if response complete or response rendered
    if (event.getFacesContext().getResponseComplete() || event.getPhaseId().equals(PhaseId.RENDER_RESPONSE)) {
      EtmPoint requestPoint = (EtmPoint) facesContext.getAttributes().get(ROOT_ETM_POINT);
      if (requestPoint != null) {
        requestPoint.collect();
      }

    }
  }

  public PhaseId getPhaseId() {
    return PhaseId.ANY_PHASE;
  }

  protected String getDefaultRequestName(FacesContext context) {
    HttpServletRequest httpServletRequest = (HttpServletRequest) context.getExternalContext().getRequest();
    String request = httpServletRequest.getRequestURI();
    int index = request.indexOf(';'); // get rid of jession id's etc.
    if (index > 0) {
      request = request.substring(0, index);
    }

    String method = httpServletRequest.getMethod();
    return "HTTP " + method + " " + request;
  }

}
