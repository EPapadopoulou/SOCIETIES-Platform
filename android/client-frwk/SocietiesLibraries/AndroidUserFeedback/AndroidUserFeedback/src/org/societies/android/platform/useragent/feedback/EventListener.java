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
package org.societies.android.platform.useragent.feedback;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.*;
import android.util.Log;
import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.api.events.IAndroidSocietiesEvents;
import org.societies.android.api.events.IPlatformEventsCallback;
import org.societies.android.api.events.PlatformEventsHelperNotConnectedException;
import org.societies.android.platform.androidutils.AndroidNotifier;
import org.societies.android.platform.useragent.feedback.constants.UserFeedbackActivityIntentExtra;
import org.societies.android.platform.useragent.feedback.guis.*;
import org.societies.android.remote.helper.EventsHelper;
import org.societies.api.internal.schema.useragent.feedback.UserFeedbackAccessControlEvent;
import org.societies.api.internal.schema.useragent.feedback.UserFeedbackPrivacyNegotiationEvent;
import org.societies.api.schema.useragent.feedback.FeedbackMethodType;
import org.societies.api.schema.useragent.feedback.UserFeedbackBean;

import java.util.HashSet;
import java.util.Set;

/**
 * Describe your class here...
 *
 * @author aleckey
 */
public class EventListener extends Service {

    private static final String LOG_TAG = EventListener.class.getName();

    private AndroidNotifier notifier;


    //TRACKING CONNECTION TO EVENTS MANAGER
    private boolean boundToEventMgrService = false;
    private BroadcastReceiver receiver;
    @SuppressWarnings("FieldCanBeLocal")
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private EventsHelper eventsHelper;

    private final Set<String> processedIncomingEvents = new HashSet<String>();

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            Log.d(this.getClass().getName(), "Message received in Userfeedback event thread");
            if (!boundToEventMgrService) {
                setupBroadcastReceiver();
                subscribeToEvents();
            }
        }
    }


    public EventListener() {

    }


    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>UserFeedback SERVICE LIFECYCLE METHODS>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    @Override
    public IBinder onBind(Intent intent) {
        return null;    //NO-ONE ALLOWED TO BIND TO THIS SERVICE
    }

    @Override
    public void onCreate() {
        Log.d(this.getClass().getName(), "UserFeedback Service creating...");


        notifier = new AndroidNotifier(this.getApplicationContext(),
                Notification.DEFAULT_SOUND,
                new int[]{Notification.FLAG_AUTO_CANCEL}
        );

        // START BACKGROUND THREAD FOR SERVICE
        HandlerThread thread = new HandlerThread("UserFeedbackStartArguments", android.os.Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);

        // Timed abort processor needs a context to run in
        TimedAbortProcessor.getInstance().setContext(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // For each start request, send a message to start a job and deliver the
        //start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        //If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "UserFeedback service terminating");
        boundToEventMgrService = false;
        this.unregisterReceiver(receiver);
        this.eventsHelper.tearDownService(new IMethodCallback() {
            @Override
            public void returnAction(String result) {
            }

            @Override
            public void returnAction(boolean resultFlag) {
            }

            @Override
            public void returnException(String result) {
            }
        });

        TimedAbortProcessor.getInstance().stop();
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>REGISTER FOR EVENTS>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    /**
     * Create a broadcast receiver
     */
    private void setupBroadcastReceiver() {
        Log.d(LOG_TAG, "Setting up broadcast receiver...");
        receiver = new MainReceiver();
        this.registerReceiver(receiver, createIntentFilter());
    }

    /**
     * Broadcast receiver to receive intent return values from EventManager service
     */
    private class MainReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(LOG_TAG, "Received action: " + intent.getAction());

            //EVENT MANAGER INTENTS
            if (intent.getAction().equals(IAndroidSocietiesEvents.SUBSCRIBE_TO_EVENT)) {
                Log.d(LOG_TAG, "Subscribed to event: " + intent.getBooleanExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, false));
            } else if (intent.getAction().equals(IAndroidSocietiesEvents.SUBSCRIBE_TO_EVENTS)) {
                Log.d(LOG_TAG, "Subscribed to multiple events: " + intent.getBooleanExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, false));
            } else if (intent.getAction().equals(IAndroidSocietiesEvents.UNSUBSCRIBE_FROM_EVENTS)) {
                Log.d(LOG_TAG, "Un-subscribed to events: " + intent.getBooleanExtra(IAndroidSocietiesEvents.INTENT_RETURN_VALUE_KEY, false));
            }
            //PRIVACY NEGOTIATION EVENT - payload is UserFeedbackPrivacyNegotiatioEvent
            else if (intent.getAction().equals(IAndroidSocietiesEvents.UF_PRIVACY_NEGOTIATION_REQUEST_INTENT)) {
                UserFeedbackPrivacyNegotiationEvent eventPayload = intent.getParcelableExtra(IAndroidSocietiesEvents.GENERIC_INTENT_PAYLOAD_KEY);

                String id = String.valueOf(eventPayload.getRequestId());

                synchronized (processedIncomingEvents) {

                    if (processedIncomingEvents.contains(id)) {
                        Log.w(LOG_TAG, "Ignoring duplicate PPN event received: id=" + id);
                        return;
                    }

                    processedIncomingEvents.add(id);
                }

                Log.d(LOG_TAG, "Privacy Negotiation event received: id=" + id);
                displayPrivacyNegotiationNotification(EventListener.this.getApplicationContext(), eventPayload);
            }
            //ACCESS CONTROL EVENT - payload is UserFeedbackAccessControlEvent
            else if (intent.getAction().equals(IAndroidSocietiesEvents.UF_ACCESS_CONTROL_REQUEST_INTENT)) {
                UserFeedbackAccessControlEvent eventPayload = intent.getParcelableExtra(IAndroidSocietiesEvents.GENERIC_INTENT_PAYLOAD_KEY);

                String id = String.valueOf(eventPayload.getRequestId());

                synchronized (processedIncomingEvents) {

                    if (processedIncomingEvents.contains(id)) {
                        Log.w(LOG_TAG, "Ignoring duplicate AC event received: id=" + id);
                        return;
                    }

                    processedIncomingEvents.add(id);
                }

                Log.d(LOG_TAG, "Privacy Negotiation event received: id=" + id);
                displayAccessControlNotification(EventListener.this.getApplicationContext(), eventPayload);
            }
            //PERMISSION REQUEST EVENT - payload is UserFeedbackBean
            else if (intent.getAction().equals(IAndroidSocietiesEvents.UF_REQUEST_INTENT)) {
                UserFeedbackBean eventPayload = intent.getParcelableExtra(IAndroidSocietiesEvents.GENERIC_INTENT_PAYLOAD_KEY);

                String id = eventPayload.getRequestId();

                synchronized (processedIncomingEvents) {

                    if (processedIncomingEvents.contains(id)) {
                        Log.w(LOG_TAG, "Ignoring duplicate UF event received: id=" + id);
                        return;
                    }

                    processedIncomingEvents.add(id);
                }

                Log.d(LOG_TAG, "General Permission request event received: id=" + id);
                displayUserFeedbackNotification(eventPayload);
            }
        }
    }

    /**
     * Create a suitable intent filter
     *
     * @return IntentFilter
     */
    private static IntentFilter createIntentFilter() {
        //register broadcast receiver to receive SocietiesEvents return values
        IntentFilter intentFilter = new IntentFilter();
        //EVENT MANAGER INTENTS
        intentFilter.addAction(IAndroidSocietiesEvents.SUBSCRIBE_TO_EVENT);
        intentFilter.addAction(IAndroidSocietiesEvents.SUBSCRIBE_TO_EVENTS);
        intentFilter.addAction(IAndroidSocietiesEvents.UNSUBSCRIBE_FROM_EVENTS);
        //PUBSUB INTENTS
        intentFilter.addAction(IAndroidSocietiesEvents.UF_ACCESS_CONTROL_REQUEST_INTENT);
        intentFilter.addAction(IAndroidSocietiesEvents.UF_PRIVACY_NEGOTIATION_REQUEST_INTENT);
        intentFilter.addAction(IAndroidSocietiesEvents.UF_REQUEST_INTENT);
        return intentFilter;
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>SUBSCRIBE TO PUBSUB EVENTS>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    private void subscribeToEvents() {
        eventsHelper = new EventsHelper(EventListener.this.getApplicationContext());
        Log.d(LOG_TAG, "new EventService");
        eventsHelper.setUpService(new IMethodCallback() {
            @Override
            public void returnAction(String result) {
            }

            @Override
            public void returnAction(boolean resultFlag) {
                Log.d(LOG_TAG, "eventMgr callback: resultFlag: " + resultFlag);
                if (resultFlag) {
                    try {
                        //subscribing to all user feedback events.
                        EventListener.this.eventsHelper.subscribeToEvents(UserFeedbackActivityIntentExtra.USERFEEDBACK_NODES, new IPlatformEventsCallback() {
                            @Override
                            public void returnAction(int result) {
                                Log.d(LOG_TAG, "eventMgr callback: ReturnAction(int) called. ??");
                            }

                            @Override
                            public void returnAction(boolean resultFlag) {
                                Log.d(LOG_TAG, "eventMgr.subscribeToEvents resultFlag: " + resultFlag);
                                if (resultFlag)
                                    boundToEventMgrService = true;
                            }

                            @Override
                            public void returnException(int exception) {
                                Log.d(LOG_TAG, "eventMgr.returnException code: " + exception);
                            }
                        });
                    } catch (PlatformEventsHelperNotConnectedException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void returnException(String result) {
            }
        });
    }

    private static void displayAccessControlNotification(Context context, UserFeedbackAccessControlEvent event) {
        // CREATE INTENT FOR LAUNCHING ACTIVITY
        Intent intent = new Intent(context, AccessControlActivity.class);
        intent.putExtra(UserFeedbackActivityIntentExtra.EXTRA_PRIVACY_POLICY, (Parcelable) event);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        //CREATE ANDROID NOTIFICATION
        int notifierFlags[] = new int[1];
        notifierFlags[0] = Notification.FLAG_AUTO_CANCEL;
        AndroidNotifier notifier = new AndroidNotifier(context, Notification.DEFAULT_SOUND, notifierFlags);
        notifier.notifyMessage("Access control request",
                "Access Control Request",
                AccessControlActivity.class,
                intent,
                "SOCIETIES");

    }

    private static void displayPrivacyNegotiationNotification(Context context, UserFeedbackPrivacyNegotiationEvent policy) {
        //CREATE INTENT FOR LAUNCHING ACTIVITY
        Intent intent = new Intent(context, NegotiationActivity.class);
        intent.putExtra(UserFeedbackActivityIntentExtra.EXTRA_PRIVACY_POLICY, (Parcelable) policy);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        //CREATE ANDROID NOTIFICATION
        int notifierFlags[] = new int[1];
        notifierFlags[0] = Notification.FLAG_AUTO_CANCEL;
        AndroidNotifier notifier = new AndroidNotifier(context, Notification.DEFAULT_SOUND, notifierFlags);
        notifier.notifyMessage("Privacy policy negotiation",
                "Privacy Policy Negotiation",
                NegotiationActivity.class,
                intent,
                "SOCIETIES");

    }

    private void displayUserFeedbackNotification(UserFeedbackBean ufBean) {

        //DETERMINE WHICH ACTIVITY TO LAUNCH
        Class activityClass;
        if (ufBean.getMethod() == FeedbackMethodType.GET_EXPLICIT_FB) {

            // select type of explicit feedback
            if (ufBean.getType() == 0)
                activityClass = RadioPopup.class;
            else if (ufBean.getType() == 1)
                activityClass = CheckboxPopup.class;
            else
                activityClass = AcknackPopup.class;

        } else if (ufBean.getMethod() == FeedbackMethodType.GET_IMPLICIT_FB) {
            // only one type of implicit feedback

            activityClass = TimedAbortPopup.class;

            // Add to the background watcher
            TimedAbortProcessor.getInstance().addTimedAbort(ufBean);

        } else {
            // only one left is "SHOW_NOTIFICATION"

            activityClass = SimpleNotificationPopup.class;
        }

        //CREATE INTENT FOR LAUNCHING ACTIVITY
        Intent intent = new Intent(this.getApplicationContext(), activityClass);
        intent.putExtra(UserFeedbackActivityIntentExtra.USERFEEDBACK_NODES, (Parcelable) ufBean);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        //CREATE ANDROID NOTIFICATION
        notifier.notifyMessage(ufBean.getProposalText(),
                "Input required",
                activityClass,
                intent,
                "SOCIETIES");
    }

}
