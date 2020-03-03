package com.example.Orlando_GarageApplication;

import android.app.Activity;
import android.os.AsyncTask;

import com.google.cloud.dialogflow.v2beta1.DetectIntentRequest;
import com.google.cloud.dialogflow.v2beta1.DetectIntentResponse;
import com.google.cloud.dialogflow.v2beta1.QueryInput;
import com.google.cloud.dialogflow.v2beta1.SessionName;
import com.google.cloud.dialogflow.v2beta1.SessionsClient;


class ResponseRequest extends AsyncTask<Void, Void, DetectIntentResponse> {

    Activity activity;
    private SessionName session;
    private SessionsClient sessionsClient;
    private QueryInput queryInput;

    ResponseRequest(Activity activity, SessionName session, SessionsClient sessionsClient, QueryInput queryInput) {
        this.activity = activity;
        this.session = session;
        this.sessionsClient = sessionsClient;
        this.queryInput = queryInput;
    }

    @Override
    protected DetectIntentResponse doInBackground(Void... voids) {
        try {
            DetectIntentRequest detectIntentRequest =
                    DetectIntentRequest.newBuilder()
                            .setSession(session.toString())
                            .setQueryInput(queryInput)
                            .build();
            return sessionsClient.detectIntent(detectIntentRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //Sends DialogFlow response to dialogFlowResponse() in DialogFlowChatActivity
    @Override
    protected void onPostExecute(DetectIntentResponse response) {
        ((DialogFlowChatActivity) activity).dialogFlowResponse(response);
    }
}
