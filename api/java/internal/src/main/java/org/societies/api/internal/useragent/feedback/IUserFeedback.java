/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske druzbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVACAO, SA (PTIN), IBM Corp., 
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

package org.societies.api.internal.useragent.feedback;

import org.societies.api.identity.Requestor;
import org.societies.api.internal.schema.useragent.feedback.NegotiationDetailsBean;
import org.societies.api.internal.useragent.model.ExpProposalContent;
import org.societies.api.internal.useragent.model.FeedbackForm;
import org.societies.api.internal.useragent.model.ImpProposalContent;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.AccessControlResponseItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponsePolicy;

import java.util.List;
import java.util.concurrent.Future;

/**
 * @author S.Gallacher@hw.ac.uk, p.skillen@hw.ac.uk
 */
public interface IUserFeedback {

    /**
     * <p>Request explicit user feedback (Yes/No, Select One, Select Many, Simple Alert Message).</p>
     * <p>This is a blocking method (i.e. it will not be return until the result has been returned from the user). You
     * may retrieve the result using {@link java.util.concurrent.Future#get()}</p>
     *
     * @param type    {@link org.societies.api.internal.useragent.model.ExpProposalType}
     * @param content The contents of this request (including the message to be shown to the user, etc)
     */
    public Future<List<String>> getExplicitFB(int type, ExpProposalContent content);


    /**
     * <p>Request implicit user feedback (currently only Timed Abort).</p>
     * <p>This is a blocking method (i.e. it will not be return until the result has been returned from the user). You
     * may retrieve the result using {@link java.util.concurrent.Future#get()}</p>
     *
     * @param type    The type of user feedback request. May be one of {@link org.societies.api.internal.useragent.model.ImpProposalType}
     * @param content The contents of this request (including the message to be shown to the user, timeout in seconds, etc)
     */
    public Future<Boolean> getImplicitFB(int type, ImpProposalContent content);



    /**
     * <p>Send a Simple Alert Message to the user.</p>
     * <p>This is a non-blocking method and will not return a result. It is expected that the notification will be delivered to the user's
     * device(s), but no response is sent when the user views and subsequently acknowledges the message</p>
     *
     * @param notificationText The text to be displayed in the message popup
     */
    public void showNotification(String notificationText);

}