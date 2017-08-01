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

package com.jobreporting.business.actions;


import android.content.Context;
import android.util.Log;

import com.jobreporting.base.Constants;
import com.jobreporting.business.common.LogManager;
import com.jobreporting.dao.JobReportingDao;
import com.jobreporting.utilities.Utility;
import com.jobreporting.views.RptFormActivityFragment;
import com.jobreporting.business.actions.Productos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductsListAction {

    private final String LOG_TAG = ProductsListAction.class.getSimpleName();

    public static Context context = null;

    JobReportingDao dao = null;

    private List<String> productNames = new ArrayList<>();
    private Map<String, String> productsDetails = new HashMap<>();

    private List<String> productNames2lines = new ArrayList<>();
    private ArrayList<Productos> productos = new ArrayList<>();

    public ProductsListAction(Context context){

        this.context = context;
        dao = new JobReportingDao(this.context);

    }

    public void execute(){

        try{
            byte[] prdDetailsBlob = dao.fetchDynaData(Constants.DYNATABLE_TYPE_PRODUCT);

            if (null != prdDetailsBlob){
                Object obj = Utility.deSerializeObjToForBlob(prdDetailsBlob);
                if (obj instanceof Map){
                    productsDetails = (Map<String, String>)obj;

                    for (String name : productsDetails.keySet()){
                        String objeto = productsDetails.get(name);
                        productNames.add(name);
                        int namecount = name.length();
                        String name2 = "";
                        if(namecount > 30){
                            name2 = name.substring(0, 30) + "\n" + name.substring(30, name.length());
                        }else {
                            name2 = name;
                        }
                        productNames2lines.add(name2);

                        Productos prd = new Productos();
                        String[] prdComoponentsSplit = objeto.split(Constants.REG_EX_FIELDID_VALUE_DATASET_SEPERATOR);
                        for (String fieldIdValue : prdComoponentsSplit) {
                            String[] fieldIdValueArr = fieldIdValue.split(Constants.REG_EX_FIELDID_VALUE_SEPERATOR);
                            String fieldName = fieldIdValueArr[0];
                            String fieldValue = "";
                            if (fieldIdValueArr.length > 1) {
                                fieldValue = fieldIdValueArr[1];
                            }
                            if (fieldName.equals("Código de producto")) {
                                prd.setCodigo(fieldValue);
                            }
                            if (fieldName.equals(">Nombre del producto")) {
                                prd.setNombre(fieldValue);
                            }
                        }
                        this.productos.add(prd);
                    }
                    this.setProductos(productos);
                    Collections.sort(productNames);

                    LogManager.log(LOG_TAG, "Total no. of products found: " + productsDetails.size(), Log.DEBUG);

                    this.setProductNames(productNames);
                    this.setProductsDetails(productsDetails);

                    this.setProductNames2lines(productNames2lines);
                }
                else{
                    throw new Exception("Invalid object type returned for Product dyna details.");
                }

            }
            else{
                throw new Exception("Not able to receive the dyna data for Products");
            }

        }
        catch (Exception ex){
            LogManager.log(LOG_TAG, "ProductsListAction->Exception occurred : " + ex.getMessage(), Log.ERROR);
        }
        catch (Throwable th){
            LogManager.log(LOG_TAG, "ProductsListAction->Throwable occurred : " + th.getMessage(), Log.ERROR);
        }

    }


    public List<String> getProductNames() {
        return productNames;
    }

    public void setProductNames(List<String> productNames) {

        this.productNames = productNames;
    }

    public Map<String, String> getProductsDetails() {

        return productsDetails;
    }

    public void setProductsDetails(Map<String, String> productsDetails) {
        this.productsDetails = productsDetails;
    }

    public List<String> getProductNames2lines() {
        return productNames2lines;
    }

    public void setProductNames2lines(List<String> productNames2lines) {

        this.productNames2lines = productNames2lines;
    }

    public ArrayList<Productos> getProductos() {
        return productos;
    }

    public void setProductos(ArrayList<Productos> productos) {

        this.productos = productos;
    }
}
