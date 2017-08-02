/*
 * Licensed To: ThoughtExecution & 9sistemes
 * Authored By: Rishi Raj Bansal
 * Developed in: 2016
 *
 * ===========================================================================
 * This is FULLY owned and COPYRIGHTED by ThoughtExecution
 * This code may NOT be RESOLD or REDISTRIBUTED under any circumstances, and is only to be used with this application
 * Using the code from this application in another application is strictly PROHIBITED and not PERMISSIBLE
 * ===========================================================================
 */

package com.jobreporting.dao;


import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class JobReportingContract {

    public static final String CONTENT_AUTHORITY = "com.jobreporting";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_URGENCIA = "urgencia";
    public static final String PATH_REPORTDATA = "reportdata";
    public static final String PATH_TAREA = "tareadata";


    private JobReportingContract(){

    }

    public static final class TokenEntry implements BaseColumns {

        //Table name
        public static final String TABLE_NAME = "token";

        //Column names
        public static final String COLUMN_TOKEN_KEY = "token_key";
        public static final String COLUMN_CREATED_ON = "created_on";

    }

    public static final class UrgenciaDataEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_URGENCIA).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_URGENCIA;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_URGENCIA;

        //Table name
        public static final String TABLE_NAME = "urgencia";

        //Column names
        public static final String COLUMN_FECHA = "fecha";
        public static final String COLUMN_HORA = "hora";
        public static final String COLUMN_CLIENTE = "cliente";
        public static final String COLUMN_MATERIALES = "materiales";
        public static final String COLUMN_PAGO = "pago";
        public static final String COLUMN_INCIDENCIAS = "incidencias";
        public static final String COLUMN_FIRMANTE = "firmante";
        public static final String COLUMN_ESTADO = "estado";
        public static final String COLUMN_PHOTO = "photo";
        public static final String COLUMN_PHOTO_NAME = "photoname";
        public static final String COLUMN_SIGN = "sign";
        public static final String COLUMN_SYNC = "sync";

    }

    public static final class ReportDataEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_REPORTDATA).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REPORTDATA;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REPORTDATA;


        //Table name
        public static final String TABLE_NAME = "reportdata";

        //Column names
        public static final String COLUMN_ID_TAREA = "id_tarea";
        public static final String COLUMN_FECHA = "fecha";
        public static final String COLUMN_HORA = "hora";
        public static final String COLUMN_CLIENTE = "cliente";
        public static final String COLUMN_MATERIALES = "materiales";
        public static final String COLUMN_PAGO = "pago";
        public static final String COLUMN_INCIDENCIAS = "incidencias";
        public static final String COLUMN_FIRMANTE = "firmante";
        public static final String COLUMN_ESTADO = "estado";
        public static final String COLUMN_PHOTO = "photo";
        public static final String COLUMN_PHOTO_NAME = "photoname";
        public static final String COLUMN_SIGN = "sign";
        public static final String COLUMN_SYNC = "sync";

    }

    public static final class TareaDataEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TAREA).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TAREA;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TAREA;

        //Table name
        public static final String TABLE_NAME = "tarea";

        //Column names
        public static final String COLUMN_ID_TAREA = "id_tarea";
        public static final String COLUMN_FECHA = "fecha";
        public static final String COLUMN_HORA = "hora";
        public static final String COLUMN_CLIENTE = "cliente";
        public static final String COLUMN_DIRECCION = "direccion";
        public static final String COLUMN_MATERIALES = "materiales";
        public static final String COLUMN_PAGO = "pago";
        public static final String COLUMN_ESTADO = "estado";

    }

}
