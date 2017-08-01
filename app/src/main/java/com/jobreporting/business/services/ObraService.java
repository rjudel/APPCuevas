package com.jobreporting.business.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import com.jobreporting.base.ConfigConstants;
import com.jobreporting.base.Constants;
import com.jobreporting.business.common.LogManager;
import com.jobreporting.business.facade.ServicesFacade;
import com.jobreporting.dao.JobReportingDao;
import com.jobreporting.entities.Obra;
import com.jobreporting.requests.WSObraRequest;
import com.jobreporting.responses.WSBaseResponse;
import com.jobreporting.responses.WSErrorResponse;
import com.jobreporting.responses.WSObraResponse;
import com.jobreporting.utilities.ServiceUtility;
import com.jobreporting.utilities.Utility;
import com.jobreporting.views.ServerErrorActivity;
import java.net.HttpURLConnection;
import java.util.ArrayList;


/**
 * Created by Alejandro on 03/04/2017.
 */

public class ObraService extends IntentService {
    private final String LOG_TAG = ReportDispatchService.class.getSimpleName();
    JobReportingDao dao = null;

    public ObraService() {
        super("ReportDispatchService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        dao = new JobReportingDao(this);
        LogManager.log(LOG_TAG, "Report Dispatch Service is started to send the report data to server...", Log.DEBUG);
        boolean running = true;
        long threadTimer = ConfigConstants.REPORT_DISPATCH_SERVICE_TIMER * 60 * 1000;

        try{
            while(running){
                LogManager.log(LOG_TAG, "Report Dispatch Service igniting...", Log.DEBUG);
                boolean isConnected = ServiceUtility.checkConnectivity(this);
                if (isConnected){
                    LogManager.log(LOG_TAG, "Connectivity is available, system will try to connect with server and send the report data.", Log.DEBUG);
                    String tokenKey = dao.fetchToken();
                    LogManager.log(LOG_TAG, "Token key : " + tokenKey, Log.DEBUG);

                    callServiceFacade(tokenKey);

                    //This service will stopped if there is no more report data avaialble to send server to save the processing cycles
                    LogManager.log(LOG_TAG, "All data has been send to server and there is no report data more, hence this service will be stopped now.", Log.DEBUG);
                    running = false;
                }else{
                    LogManager.log(LOG_TAG, "Connectivity is NOT available, system will try after some time to connect with server.", Log.DEBUG);
                    Thread.sleep(threadTimer);
                    LogManager.log(LOG_TAG, "Report Dispatch Service sleeping for : " + ConfigConstants.REPORT_DISPATCH_SERVICE_TIMER, Log.DEBUG);
                }
            }
        }catch (Exception ex){
            LogManager.log(LOG_TAG, "Exception occurred in Report Dispatch Service : " + ex.getMessage(), Log.ERROR);
        }
        LogManager.log(LOG_TAG, "Report Dispatch Service is done with sending the data to server...", Log.DEBUG);
    }

    private void callServiceFacade(String tokenKey) throws Exception{
        ServicesFacade servicesFacade = new ServicesFacade();
        WSObraRequest request = new WSObraRequest();
        request.setTokenKey(tokenKey);
        LogManager.log(LOG_TAG, "Calling Services...", Log.DEBUG);
        WSBaseResponse baseResponse = servicesFacade.callObraService(request);

        if(null != baseResponse){
            LogManager.log(LOG_TAG, "Report service executed successfully.", Log.DEBUG);
            String status = baseResponse.getStatus();
            if(Utility.safeTrim(status).equals(Integer.toString(HttpURLConnection.HTTP_OK))){
                LogManager.log(LOG_TAG, "Success response returned from Server.", Log.DEBUG);
                WSObraResponse response = (WSObraResponse)baseResponse;
                String responseStatus = response.getResponse();

                ArrayList<Obra> datos_obra = new ArrayList<>();
                datos_obra = response.getListado_obras();
                dao.VaciarObra();
                for(Obra obra : datos_obra){dao.GuardarObra(obra);}
                LogManager.log(LOG_TAG, "Report Service response : " + responseStatus, Log.DEBUG);
            }else{
                LogManager.log(LOG_TAG, "ERROR response returned from Server.", Log.DEBUG);

                WSErrorResponse errorResponse = (WSErrorResponse)baseResponse;
                String code = errorResponse.getCode();
                String message = errorResponse.getUserMessage();
                String errorMessage = errorResponse.getErrorMessage();
                LogManager.log(LOG_TAG, "Error Message from server : " + errorMessage, Log.DEBUG);

                //Forward the redirection to Server Error Activity
                Intent intent = new Intent(this, ServerErrorActivity.class);
                intent.putExtra(Constants.INTENT_EXTRA_CUSTOM_SERVER_ERROR_CODE, code);
                intent.putExtra(Constants.INTENT_EXTRA_CUSTOM_SERVER_MESSAGE, message);
                intent.putExtra(Constants.INTENT_EXTRA_CUSTOM_SERVER_ERROR, errorMessage);
                intent.putExtra(Constants.INTENT_EXTRA_CUSTOM_SERVER_ERROR_OCCURRED, "Report Service: Sending Report data to server");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }
        }else{
            LogManager.log(LOG_TAG, "Response returned null.", Log.ERROR);
            throw new Exception("Response returned null.");
        }
    }
}
