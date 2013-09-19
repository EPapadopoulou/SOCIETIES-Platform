/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.societies.useragent.decisionmaking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.personalisation.model.FeedbackEvent;
import org.societies.api.internal.personalisation.model.FeedbackTypes;
import org.societies.api.internal.personalisation.model.IOutcome;
import org.societies.api.internal.useragent.conflict.IConflictResolutionManager;
import org.societies.api.internal.useragent.decisionmaking.IDecisionMaker;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.internal.useragent.model.ExpProposalContent;
import org.societies.api.internal.useragent.model.ExpProposalType;
import org.societies.api.internal.useragent.model.ImpProposalContent;
import org.societies.api.internal.useragent.model.ImpProposalType;
import org.societies.api.osgi.event.EMSException;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.personalisation.model.IAction;
import org.societies.useragent.conflict.ConflictType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public abstract class AbstractDecisionMaker implements IDecisionMaker {
    IConflictResolutionManager manager;

    IIdentity entityID;

    @Autowired
    ICommManager commMgr;

    @Autowired
    IEventMgr pesoMgr;

    @Autowired
    IUserFeedback feedbackHandler;

    public HashSet<IOutcome> hasBeenChecked = new HashSet<IOutcome>();

    private Logger logging = LoggerFactory.getLogger(this.getClass());

    public IConflictResolutionManager getManager() {
        return manager;
    }

    public void setManager(IConflictResolutionManager manager) {
        this.manager = manager;
    }

    public IUserFeedback getFeedbackHandler() {
        return feedbackHandler;
    }

    public void setFeedbackHandler(IUserFeedback feedbackHandler) {
        this.feedbackHandler = feedbackHandler;
    }

    public void setPesoMgr(IEventMgr mgr) {
        this.pesoMgr = mgr;
    }

    public IIdentity getEntityID() {
        return entityID;
    }

    public void setEntityID(IIdentity entityID) {
        this.entityID = entityID;
    }

    public void setCommMgr(ICommManager commMgr) {
        this.commMgr = commMgr;
        this.entityID = this.commMgr.getIdManager().getThisNetworkNode();
    }

    @Override
    public void makeDecision(List<IOutcome> intents, List<IOutcome> preferences) {
        HashSet<IOutcome> conflicts = new HashSet<IOutcome>();
        logging.debug("start resolving DM");
        if (intents.size() == 0) {
            for (IOutcome action : preferences) {
                this.implementIAction(action);
            }
            return;
        }
        if (preferences.size() == 0) {
            for (IOutcome action : intents) {
                this.implementIAction(action);
            }
            return;
        }
        for (IOutcome intent : intents) {
            IOutcome action = intent;
            // unresolved preference ioutcomes
            for (IOutcome preference : preferences) {
                ConflictType conflict = detectConflict(intent, preference);
                if (conflict == ConflictType.PREFERNCE_INTENT_NOT_MATCH) {
                    action = manager.resolveConflict(action, preference);
                    if (action == null) {
                        conflicts.add(preference);
                    } else {
                        FeedbackEvent fedb = new FeedbackEvent(entityID,
                                action, true, FeedbackTypes.CONFLICT_RESOLVED);
                        InternalEvent event = new InternalEvent(
                                EventTypes.UI_EVENT, "feedback",
                                "org/societies/useragent/decisionmaker", fedb);
                        try {
                            pesoMgr.publishInternalEvent(event);
                        } catch (EMSException e) {
                            logging.error("", e);
                            e.printStackTrace();
                        }
                    }
                } else if (conflict == ConflictType.UNKNOWN_CONFLICT) {
                    /* handler the unknown work */
                }
            }
            if (conflicts.size() == 0) {// no unresolved conflicts
                logging.debug("no unresolved conflicts");
                this.implementIAction(action);
            } else {
                List<String> options = new ArrayList<String>();
                options.add(intent.toString());
                for (IOutcome conf : conflicts)
                    options.add(conf.toString());
                logging.debug("Call Feedback Manager");
                ExpProposalContent epc = new ExpProposalContent(
                        "Conflict Detected!",
                        options.toArray(new String[options.size()]));
                List<String> reply;
                try {
                    reply = feedbackHandler.getExplicitFB(
                            ExpProposalType.RADIOLIST, epc).get();
                    new DecisionMakingCallback(this, intent, conflicts)
                            .handleExpFeedback(reply);
                } catch (Exception e) {
                    logging.error("", e);
                    e.printStackTrace();
                }// ,
                // new DecisionMakingCallback(this,intent,conflicts));
            }
            conflicts.clear();
        }
        logging.debug("after resolving DM");
    }

    protected boolean getUserFeedback(String content, IAction action) {
        try {
            IOutcome iot = (IOutcome) action;
            if (hasBeenChecked.contains(iot)) {
                hasBeenChecked.remove(iot);
                return true;
            }
            if (iot.getConfidenceLevel() > 50) {
                ImpProposalContent ic = new ImpProposalContent(content, 10);
                boolean resb = feedbackHandler.getImplicitFB(
                        ImpProposalType.TIMED_ABORT, ic).get();
                if (resb == false) {
                    FeedbackEvent fedb = new FeedbackEvent(this.entityID,
                            action, resb, FeedbackTypes.USER_ABORTED);
                    InternalEvent event = new InternalEvent(
                            EventTypes.UI_EVENT, "feedback",
                            "org/societies/useragent/decisionmaker", fedb);
                    try {
                        pesoMgr.publishInternalEvent(event);
                    } catch (EMSException e) {
                        logging.error("", e);
                        e.printStackTrace();
                    }
                }
                return resb;
            } else {
                ExpProposalContent epc = new ExpProposalContent(content,
                        new String[]{"Yes", "No"});
                List<String> result = feedbackHandler.getExplicitFB(
                        ExpProposalType.RADIOLIST, epc).get();
                if (result.get(0).equals("Yes")) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
            System.err.println(e);
            return false;
        }
    }

    protected abstract ConflictType detectConflict(IOutcome intent,
                                                   IOutcome prefernce);

    protected abstract void implementIAction(IAction action);
}
