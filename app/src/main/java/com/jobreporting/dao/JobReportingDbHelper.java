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


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jobreporting.dao.JobReportingContract.UrgenciaDataEntry;
import com.jobreporting.dao.JobReportingContract.ReportDataEntry;
import com.jobreporting.dao.JobReportingContract.TokenEntry;
import com.jobreporting.dao.JobReportingContract.TareaDataEntry;

public class JobReportingDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "jobreporting.db";


    public JobReportingDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_TOKEN_TABLE = "CREATE TABLE " + TokenEntry.TABLE_NAME + " (" +
                TokenEntry._ID + " INTEGER PRIMARY KEY," +
                TokenEntry.COLUMN_TOKEN_KEY + " TEXT NOT NULL, " +
                TokenEntry.COLUMN_CREATED_ON + " TEXT " +
                " );";

        final String SQL_CREATE_REPORT_TABLE = "CREATE TABLE " + ReportDataEntry.TABLE_NAME + " (" +
                ReportDataEntry._ID + " INTEGER PRIMARY KEY," +
                ReportDataEntry.COLUMN_ID_TAREA + " INTEGER," +
                ReportDataEntry.COLUMN_FECHA + " DATE, " +
                ReportDataEntry.COLUMN_HORA + " TIME, " +
                ReportDataEntry.COLUMN_CLIENTE + " VARCHAR(50)," +
                ReportDataEntry.COLUMN_MATERIALES + " TEXT, " +
                ReportDataEntry.COLUMN_PAGO + " TEXT, " +
                ReportDataEntry.COLUMN_INCIDENCIAS + " TEXT, " +
                ReportDataEntry.COLUMN_FIRMANTE + " VARCHAR(50)," +
                ReportDataEntry.COLUMN_ESTADO + " VARCHAR(50), " +
                ReportDataEntry.COLUMN_PHOTO + " BLOB, " +
                ReportDataEntry.COLUMN_PHOTO_NAME + " TEXT, " +
                ReportDataEntry.COLUMN_SIGN + " BLOB, " +
                ReportDataEntry.COLUMN_SYNC + " INTEGER " +
                " );";

        final String SQL_CREATE_URGENCIA_TABLE = "CREATE TABLE " + UrgenciaDataEntry.TABLE_NAME + " (" +
                UrgenciaDataEntry._ID + " INTEGER PRIMARY KEY," +
                UrgenciaDataEntry.COLUMN_FECHA + " DATE, " +
                UrgenciaDataEntry.COLUMN_HORA + " TIME, " +
                UrgenciaDataEntry.COLUMN_CLIENTE + " VARCHAR(50)," +
                UrgenciaDataEntry.COLUMN_MATERIALES + " TEXT, " +
                UrgenciaDataEntry.COLUMN_PAGO + " TEXT, " +
                UrgenciaDataEntry.COLUMN_INCIDENCIAS + " TEXT, " +
                UrgenciaDataEntry.COLUMN_FIRMANTE + " VARCHAR(50)," +
                UrgenciaDataEntry.COLUMN_ESTADO + " VARCHAR(50), " +
                UrgenciaDataEntry.COLUMN_PHOTO + " BLOB, " +
                UrgenciaDataEntry.COLUMN_PHOTO_NAME + " TEXT, " +
                UrgenciaDataEntry.COLUMN_SIGN + " BLOB, " +
                UrgenciaDataEntry.COLUMN_SYNC + " INTEGER " +
                " );";

        final String SQL_CREATE_TAREA_TABLE = "CREATE TABLE " + TareaDataEntry.TABLE_NAME + " (" +
                TareaDataEntry._ID + " INTEGER PRIMARY KEY," +
                TareaDataEntry.COLUMN_ID_TAREA + " INTEGER," +
                TareaDataEntry.COLUMN_FECHA + " DATE, " +
                TareaDataEntry.COLUMN_HORA + " TIME, " +
                TareaDataEntry.COLUMN_CLIENTE + " VARCHAR(50)," +
                TareaDataEntry.COLUMN_DIRECCION + " TEXT, " +
                TareaDataEntry.COLUMN_MATERIALES + " TEXT, " +
                TareaDataEntry.COLUMN_PAGO + " TEXT, " +
                TareaDataEntry.COLUMN_ESTADO + " VARCHAR(50) " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_TOKEN_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REPORT_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_URGENCIA_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TAREA_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TokenEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + UrgenciaDataEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ReportDataEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TareaDataEntry.TABLE_NAME);

        onCreate(sqLiteDatabase);

    }
}
