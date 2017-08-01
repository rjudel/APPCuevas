package com.jobreporting.business.actions;

import android.content.Context;
import android.util.Log;

import com.jobreporting.base.Constants;
import com.jobreporting.business.common.LogManager;
import com.jobreporting.dao.JobReportingDao;
import com.jobreporting.entities.Obra;
import com.jobreporting.utilities.Utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Alejandro on 03/04/2017.
 */

public class ObraAction {

    private final String LOG_TAG = ObraAction.class.getSimpleName();

    public static Context context = null;

    JobReportingDao dao = null;

    public ObraAction(Context context){

        this.context = context;
        dao = new JobReportingDao(this.context);

    }

    public ArrayList<Obra> GetDatosObras(){
        ArrayList<Obra> datos_obra= new ArrayList<>();
        try{
            datos_obra = dao.leerObra();

        } catch (Exception ex){
            LogManager.log(LOG_TAG, "CustomersListAction->Exception occurred : " + ex.getMessage(), Log.ERROR);
        }
        catch (Throwable th){
            LogManager.log(LOG_TAG, "CustomersListAction->Throwable occurred : " + th.getMessage(), Log.ERROR);
        }
        return datos_obra;
    }
}
