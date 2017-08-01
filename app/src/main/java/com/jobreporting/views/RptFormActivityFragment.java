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

package com.jobreporting.views;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.jobreporting.business.actions.ObraAction;
import com.jobreporting.business.actions.Productos;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.jobreporting.R;
import com.jobreporting.base.Constants;
import com.jobreporting.base.ERequestType;
import com.jobreporting.business.ApplicationInitializer;
import com.jobreporting.business.actions.CustomersListAction;
import com.jobreporting.business.actions.ReportAction;
import com.jobreporting.business.common.LogManager;
import com.jobreporting.business.validations.AbstractBusinessValidator;
import com.jobreporting.business.validations.ReportValidator;
import com.jobreporting.entities.Obra;
import com.jobreporting.entities.WSUserEntity;
import com.jobreporting.utilities.FileUtility;
import com.jobreporting.utilities.ServiceUtility;
import com.jobreporting.utilities.SignPadUtility;
import com.jobreporting.utilities.Utility;
import com.jobreporting.business.actions.ProductsListAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static android.graphics.Color.parseColor;

public class RptFormActivityFragment extends Fragment {

    private final String LOG_TAG = RptFormActivityFragment.class.getSimpleName();

    private View rootView = null;

    private List<WSUserEntity> reportDynaDetails = null;

    /* List to maintain the spinner values from runtime and save them on submit */
    Map<Integer, String> spinnerValues = new HashMap<>();

    /* List to maintain the checkbox values from runtime and save them on submit */
    Map<Integer, String> checkBoxValues = new HashMap<>();

    /* Counter for additional 2TB controls on run time */
    private static int dyna2TBIdCounterOp = 1;
    private static int dyna2TBIdCounterPr = 2;
    /* List to maintain 2TB controls and counter ids
    * Format: DynaId and counter value => DynaId | Counter value
    */
    private static Map<String, String> dyna2TBIdsCtrsOp = new LinkedHashMap<>();
    private static Map<String, String> dyna2TBIdsCtrsPr = new LinkedHashMap<>();

    /* Photo view */
    private static final int PICK_PHOTO_REQUEST = 1;
    Map<String, byte[]> photoBytesDataList = new HashMap<>();

    private static Map<String, String> requestCodesWithViewIds = new HashMap<>();

    /* Signature pad */
    private SignaturePad mSignaturePad;
    private byte[] signFileBytes;
    private boolean isSignaturePadUsed;
    private AutoCompleteTextView autocomplete;
    public static TableLayout tableLayout;
    public static LinearLayout linearLayout;
    public static Context ctx;

    public static TextView resultadoTxt;
    public static TextView resultadoTxtAnterior;
    public static Spinner spinnerViewObra;

    private static int idObra = '2';
    private static int idCorreo = '3';
    private static int idDescripcion = '4';
    private static int idObservaciones = '5';
    private static int idImagen = '6';
    private static int idFirmante = '7';
    private static int idFirma = '8';

    private static int idOperarios = 10;
    private static int idProductos = 20;


    public RptFormActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_rpt_form, container, false);
        Button submitButton = (Button)rootView.findViewById(R.id.btn_rpt_form_save);
        submitButton.setOnClickListener(mFormBtnsListener);
        Button resetButton = (Button)rootView.findViewById(R.id.btn_rpt_form_reset);
        resetButton.setOnClickListener(mFormBtnsListener);
        renderRptDetailsView();
        return rootView;
    }

    public void renderRptDetailsView(){
        ctx = getContext();

        final LinearLayout parentLayout = (LinearLayout) rootView.findViewById(R.id.layout_rpt_form_dyna);
        tableLayout = new TableLayout(ctx);

        linearLayout = new LinearLayout(ctx);
        linearLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        //Autocomplete Cliente
        Context contexto = getActivity().getApplicationContext();
        CustomersListAction action = new CustomersListAction(getActivity());
        action.execute();
        List<String> customerNames = action.getCustomerNames();
        final String[] customers = customerNames.toArray(new String[customerNames.size()]);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(contexto, R.layout.autocomplete_style, customers);
        autocomplete = ((AutoCompleteTextView) rootView.findViewById(R.id.layout_rpt_form).findViewById(R.id.clientes_autocomplete));
        autocomplete.setAdapter(adapter);
        autocomplete.setThreshold(1);
        autocomplete.setVisibility(View.VISIBLE);
        autocomplete.setHint("Nombre del Cliente");
        //autocomplete.setId(idCliente);
        autocomplete.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<String> listValues = new ArrayList<>();

                ObraAction action_obra = new ObraAction(getActivity());
                ArrayList<Obra> listObras = action_obra.GetDatosObras();

                for(Obra obra : listObras){
                    if(autocomplete.getText().toString().equals(obra.getCliente())){
                        String datos = obra.getCodigo().toString() +" - "+ obra.getDescripcion().toString();
                        listValues.add(datos);
                    }
                }

                List<String> listValuesWithInitialValue = new ArrayList<>();
                listValuesWithInitialValue.add(getString(R.string.rpt_form_spinner_initialvalue));
                listValuesWithInitialValue.addAll(listValues);

                ArrayAdapter<String> spinnerListAdapter = new ArrayAdapter<String>(getContext(), R.layout.rpt_form_spinner, listValuesWithInitialValue);
                spinnerViewObra.setAdapter(spinnerListAdapter);
                spinnerViewObra.setOnItemSelectedListener(new SpinnerItemListener());
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //Label Obra
        View labelObra = createInputLabel(ctx, "Obra");
        tableLayout.addView(labelObra);
        //Select Obra
        View spinnerViewObra = createComboBoxViewObra(ctx, idObra);
        tableLayout.addView(spinnerViewObra);

        //Label Correo
        View labelCorreo = createInputLabel(ctx, "Correo electrónico");
        tableLayout.addView(labelCorreo);
        //Input Correo
        View textBoxViewCorreo = createTextView(ctx, idCorreo);
        tableLayout.addView(textBoxViewCorreo);

        //Label Descripcion
        View labelDescripcion = createInputLabel(ctx, "Descripción");
        tableLayout.addView(labelDescripcion);
        //Input Descripcion
        View textBoxViewDescripcion = createTextAreaView(ctx, idDescripcion);
        tableLayout.addView(textBoxViewDescripcion);

        String ctrlNameOperarios = "Duracion Operarios(Operario - Tiempo)";
        //Label Operarios
        View labelOperarios = createInputLabel(ctx, ctrlNameOperarios);
        tableLayout.addView(labelOperarios);
        //Operarios
        View dyna2TextBoxViewOperarios = createDyna2TextBoxViewOperarios(ctx, idOperarios, ctrlNameOperarios);
        tableLayout.addView(dyna2TextBoxViewOperarios);

        String ctrlNameProductos = "Productos Utilizados (Producto - Cantidad)";
        //Label Productos
        View labelProductos = createInputLabel(ctx, ctrlNameProductos);
        tableLayout.addView(labelProductos);
        //Productos
        View dyna2TextBoxViewProductos = createDyna2TextBoxViewProductos(ctx, idProductos, ctrlNameProductos);
        tableLayout.addView(dyna2TextBoxViewProductos);

        //Label Observaciones
        View labelObservaciones = createInputLabel(ctx, "Observaciones");
        tableLayout.addView(labelObservaciones);
        //Input Observaciones
        View textBoxViewObservaciones = createTextAreaView(ctx, idObservaciones);
        tableLayout.addView(textBoxViewObservaciones);

        //Label Foto
        View labelFoto = createInputLabel(ctx, "Foto(No debe superar los 2MB)");
        tableLayout.addView(labelFoto);
        //Imagen
        View imageView = createImageUploadView(ctx, idImagen);
        tableLayout.addView(imageView);

        //Label Firmante
        View labelFirmante = createInputLabel(ctx, "Firmante");
        tableLayout.addView(labelFirmante);
        //Input Firmante
        View textBoxViewFirmante = createTextView(ctx, idFirmante);
        tableLayout.addView(textBoxViewFirmante);

        //Firma
        createSignPadView(ctx, idFirma);

        linearLayout.addView(tableLayout);
        parentLayout.addView(linearLayout);

    }

    public void submitReport(){
        List<String> isErrors = new ArrayList<>();
        isErrors.add("OK");
        Utility.checkErrors((ViewGroup)rootView.findViewById(R.id.scrollView_rpt_form), isErrors);
        if (isErrors.get(0).equals("NOT_OK")){
            ServiceUtility.showErrorAlertMessage(getActivity(), getResources().getString(R.string.rpt_data_invalid_title_alert), getResources().getString(R.string.rpt_data_invalid_message_alert));
            return;
        }

        String postedIdValues = updatePostedValuesInEntities();
        if (postedIdValues.equals("NOT_OK")){
            ServiceUtility.showErrorAlertMessage(getActivity(), "ERROR","El cliente no puede ser vacío.");
            return;
        }


        if (Utility.safeTrim(postedIdValues).equals(Constants.EMPTY_STRING)){
            //Display the alert box message
            ServiceUtility.showErrorAlertMessage(getActivity(), getResources().getString(R.string.rpt_data_empty_title_alert), getResources().getString(R.string.rpt_data_empty_message_alert));
        }
        else{
            ReportAction action = new ReportAction(getActivity());

            //Set Report Data
            action.setPostedIdValues(postedIdValues);

            //Set Image Data
            if (null != photoBytesDataList && photoBytesDataList.size() > 0){
                action.setPhotoImageName(photoBytesDataList.keySet().iterator().next());
                action.setPhotoBytesData(photoBytesDataList.get(action.getPhotoImageName()));
            }

            //Set Signature Data
            if (null != signFileBytes && signFileBytes.length > 0){
                action.setSignFileBytes(signFileBytes);
            }

            action.execute(ERequestType.SAVE_REPORT);

            //Empty 2TB controls and counter ids list so that it should not contain null controls
            dyna2TBIdsCtrsOp.clear();
            dyna2TBIdsCtrsPr.clear();
            dyna2TBIdCounterOp = 1;
            dyna2TBIdCounterPr = 2;
            //Redirect to Home Activity
            ApplicationInitializer.getApplicationInitializer().redirectToHome(true);

            //Display Toast
            Toast reportToast = Toast.makeText(getContext(), R.string.toast_message_submit_report, Toast.LENGTH_LONG);
            reportToast.show();
        }

    }

    public void reset(){

        Utility.resetForm((ViewGroup)rootView.findViewById(R.id.scrollView_rpt_form));

        //Empty spinner values
        spinnerValues.clear();

        //Empty checkbox values
        checkBoxValues.clear();

        //Empty Photo
        if (!photoBytesDataList.isEmpty()){
            String buttonId = requestCodesWithViewIds.get(Integer.toString(PICK_PHOTO_REQUEST));
            String imageUploadLabelViewId = buttonId + Constants.DYNA_CONTROL_TYPE_IMAGE_PHOTO_SELECTED_LABEL_ID;
            TextView imageUploadLabelView = (TextView) rootView.findViewById(Integer.parseInt(imageUploadLabelViewId));
            imageUploadLabelView.setText("");

            photoBytesDataList.clear();
            requestCodesWithViewIds.clear();
        }

        //Empty Signpad
        if (null != signFileBytes){
            signFileBytes = null;
        }
        if (rootView.findViewById(R.id.rpt_form_signpad_clear_btn).isEnabled()){
            mSignaturePad.clear();
        }

    }

    public String updatePostedValuesInEntities(){

        String postedIdValues = "";
        String errors = "NOT_OK";

        //Cliente
        if (autocomplete != null) {
            String nombreCliente = autocomplete.getEditableText().toString();
            if (!nombreCliente.equals("")) {
                postedIdValues = postedIdValues + "c_1" + Constants.FIELDID_VALUE_SEPERATOR + nombreCliente + Constants.FIELDID_VALUE_DATASET_SEPERATOR;
            }else{
                return errors;
            }
        }

        //Obra
        if (spinnerValues.containsKey(idObra)){
            postedIdValues = postedIdValues + "c_2" + Constants.FIELDID_VALUE_SEPERATOR + spinnerValues.get(idObra) + Constants.FIELDID_VALUE_DATASET_SEPERATOR;
        }


        //Correo
        EditText textView = (EditText) rootView.findViewById(idCorreo);
        if (null != textView && null != textView.getText() && !Utility.safeTrim(textView.getText().toString()).equals(Constants.EMPTY_STRING)) {
            String validatorMessage = ValidarCampoTexto(textView);
            if (!validatorMessage.equals("OK")) {
                return errors;
            }
            String value = textView.getText().toString();
            postedIdValues = postedIdValues + "c_7" + Constants.FIELDID_VALUE_SEPERATOR + value + Constants.FIELDID_VALUE_DATASET_SEPERATOR;
        }

        //Descripcion
        EditText textAreaView = (EditText) rootView.findViewById(idDescripcion);
        if (null != textAreaView && null != textAreaView.getText() && !Utility.safeTrim(textAreaView.getText().toString()).equals(Constants.EMPTY_STRING) ){
            String validatorMessage = ValidarCampoTexto(textAreaView);
            if (!validatorMessage.equals("OK")){
                return errors;
            }
            String value = textAreaView.getText().toString();
            postedIdValues = postedIdValues + "r_9" + Constants.FIELDID_VALUE_SEPERATOR + value + Constants.FIELDID_VALUE_DATASET_SEPERATOR;
        }

        //Operarios
        String consDyna2TBValues = "";
        if (dyna2TBIdsCtrsOp.size() > 0){
            for (String idAndCtr : dyna2TBIdsCtrsOp.keySet()){
                String dyna2TBValue = "";
                if (idAndCtr.startsWith(Integer.toString(idOperarios))){
                    String mapValue = dyna2TBIdsCtrsOp.get(idAndCtr);

                    String[] parsedIds = mapValue.split(Constants.REG_EX_PIPE_SEPERATOR);
                    String dyna2TBId = parsedIds[0];
                    String ctrValue = parsedIds[1];

                    String dyna2TBId_2 = dyna2TBId + Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_SUFFIX_SECONDOP + ctrValue;
                    String dyna2TBId_1 = dyna2TBId + Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_SUFFIX_FIRSTOP + ctrValue;

                    EditText textBoxView2 = (EditText) rootView.findViewById(Integer.parseInt(dyna2TBId_2));
                    AutoCompleteTextView autocompleteOper = (AutoCompleteTextView) rootView.findViewById(Integer.parseInt(dyna2TBId_1));

                    if (autocompleteOper != null) {
                        dyna2TBValue = dyna2TBValue + autocompleteOper.getEditableText().toString();
                    }
                    if (null != textBoxView2 && null != textBoxView2.getText() && !Utility.safeTrim(textBoxView2.getText().toString()).equals(Constants.EMPTY_STRING) && !dyna2TBValue.equals("Select...<>") && !dyna2TBValue.equals("Seleccionar<>")){
                        String validatorMessage = ValidarCampoTextoProducto(textBoxView2);
                        if (!validatorMessage.equals("OK")){
                            return errors;
                        }else {
                            dyna2TBValue = dyna2TBValue + Constants.FIELD_2TB_VALUES_SEPERATOR;
                            dyna2TBValue = dyna2TBValue + textBoxView2.getText().toString();
                        }
                    }
                    if (!Utility.safeTrim(dyna2TBValue).equals(Constants.EMPTY_STRING) && !Utility.safeTrim(dyna2TBValue).equals(Constants.FIELD_2TB_VALUES_SEPERATOR) && !Utility.safeTrim(dyna2TBValue).equals("Select...<>") && !Utility.safeTrim(dyna2TBValue).equals("Seleccionar<>")){
                        consDyna2TBValues = consDyna2TBValues + dyna2TBValue + Constants.FIELD_2TB_VALUES_DATASET_SEPERATOR;
                    }
                }
            }
            if (!Utility.safeTrim(consDyna2TBValues).equals(Constants.EMPTY_STRING)){
                consDyna2TBValues = consDyna2TBValues.substring(0, consDyna2TBValues.length() - Constants.FIELD_2TB_VALUES_DATASET_SEPERATOR.length());
                postedIdValues = postedIdValues + "r_9991" + Constants.FIELDID_VALUE_SEPERATOR + consDyna2TBValues + Constants.FIELDID_VALUE_DATASET_SEPERATOR;
            }
        }

        //Productos
        String consDyna2TBValues2 = "";
        if (dyna2TBIdsCtrsPr.size() > 0){
            for (String idAndCtr : dyna2TBIdsCtrsPr.keySet()){
                String dyna2TBValue = "";
                if (idAndCtr.startsWith(Integer.toString(idProductos))){
                    String mapValue = dyna2TBIdsCtrsPr.get(idAndCtr);

                    String[] parsedIds = mapValue.split(Constants.REG_EX_PIPE_SEPERATOR);
                    String dyna2TBId = parsedIds[0];
                    String ctrValue = parsedIds[1];

                    String dyna2TBId_2 = dyna2TBId + Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_SUFFIX_SECONDPR + ctrValue;
                    String dyna2TBId_1 = dyna2TBId + Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_SUFFIX_FIRSTPR + ctrValue;

                    EditText textBoxView2 = (EditText) rootView.findViewById(Integer.parseInt(dyna2TBId_2));
                    AutoCompleteTextView autocompleteProd = (AutoCompleteTextView) rootView.findViewById(Integer.parseInt(dyna2TBId_1));

                    if (autocompleteProd != null) {
                        String nombreProducto = autocompleteProd.getEditableText().toString();
                        if(!nombreProducto.equals("")) {
                            dyna2TBValue = nombreProducto;
                            dyna2TBValue = dyna2TBValue + '*';

                            ProductsListAction action = new ProductsListAction(getActivity());
                            action.execute();
                            ArrayList<Productos> productos = action.getProductos();
                            for (Productos prd : productos) {
                                int namecount = prd.getNombre().length();
                                String name2 = "";
                                if (namecount > 30) {
                                    name2 = prd.getNombre().substring(0, 30) + "\n" + prd.getNombre().substring(30, prd.getNombre().length());
                                } else {
                                    name2 = prd.getNombre();
                                }
                                if (name2.equals(nombreProducto)) {
                                    dyna2TBValue = dyna2TBValue + prd.getCodigo();
                                }
                            }
                        }
                    }

                    if (null != textBoxView2 && null != textBoxView2.getText() && !Utility.safeTrim(textBoxView2.getText().toString()).equals(Constants.EMPTY_STRING) && !dyna2TBValue.equals("Select...<>") && !dyna2TBValue.equals("Seleccionar<>")){
                        String validatorMessage = ValidarCampoTextoProducto(textBoxView2);
                        if (!validatorMessage.equals("OK")){
                            return errors;
                        }else {
                            dyna2TBValue = dyna2TBValue + Constants.FIELD_2TB_VALUES_SEPERATOR;
                            dyna2TBValue = dyna2TBValue + textBoxView2.getText().toString();
                        }
                    }
                    if (!Utility.safeTrim(dyna2TBValue).equals(Constants.EMPTY_STRING) && !Utility.safeTrim(dyna2TBValue).equals(Constants.FIELD_2TB_VALUES_SEPERATOR) && !Utility.safeTrim(dyna2TBValue).equals("Select...<>") && !Utility.safeTrim(dyna2TBValue).equals("Seleccionar<>")){
                        consDyna2TBValues2 = consDyna2TBValues2 + dyna2TBValue + Constants.FIELD_2TB_VALUES_DATASET_SEPERATOR;
                    }
                }
            }
            if (!Utility.safeTrim(consDyna2TBValues2).equals(Constants.EMPTY_STRING)){
                consDyna2TBValues2 = consDyna2TBValues2.substring(0, consDyna2TBValues2.length() - Constants.FIELD_2TB_VALUES_DATASET_SEPERATOR.length());
                postedIdValues = postedIdValues + "r_9995" + Constants.FIELDID_VALUE_SEPERATOR + consDyna2TBValues2 + Constants.FIELDID_VALUE_DATASET_SEPERATOR;
            }
        }

        //Observaciones
        EditText textAreaViewObservaciones = (EditText) rootView.findViewById(idObservaciones);
        if (null != textAreaViewObservaciones && null != textAreaViewObservaciones.getText() && !Utility.safeTrim(textAreaViewObservaciones.getText().toString()).equals(Constants.EMPTY_STRING) ){
            String validatorMessage = ValidarCampoTexto(textAreaViewObservaciones);
            if (!validatorMessage.equals("OK")){
                return errors;
            }
            String value = textAreaViewObservaciones.getText().toString();
            postedIdValues = postedIdValues + "r_9996" + Constants.FIELDID_VALUE_SEPERATOR + value + Constants.FIELDID_VALUE_DATASET_SEPERATOR;
        }

        //Foto
        if (null != photoBytesDataList && photoBytesDataList.size() > 0){
            String value = "r_9998" + Constants.DYNA_CONTROL_VALUE_PLACEHOLDER;
            postedIdValues = postedIdValues + "r_9998" + Constants.FIELDID_VALUE_SEPERATOR + value + Constants.FIELDID_VALUE_DATASET_SEPERATOR;
        }

        //Firmante
        EditText textViewFirmante = (EditText) rootView.findViewById(idFirmante);
        if (null != textViewFirmante && null != textViewFirmante.getText() && !Utility.safeTrim(textViewFirmante.getText().toString()).equals(Constants.EMPTY_STRING)) {
            String validatorMessage = ValidarCampoTexto(textViewFirmante);
            if (!validatorMessage.equals("OK")) {
                return errors;
            }
            String value = textViewFirmante.getText().toString();
            postedIdValues = postedIdValues + "r_9999" + Constants.FIELDID_VALUE_SEPERATOR + value + Constants.FIELDID_VALUE_DATASET_SEPERATOR;
        }

        //Firma
        if (isSignaturePadUsed){
            Bitmap signBitmap = mSignaturePad.getSignatureBitmap();
            signFileBytes = SignPadUtility.bitmapToJPG(signBitmap);
            if (null != signFileBytes && signFileBytes.length > 0){
                String value = "r_10000" + Constants.DYNA_CONTROL_VALUE_PLACEHOLDER;
                postedIdValues = postedIdValues + "r_10000" + Constants.FIELDID_VALUE_SEPERATOR + value + Constants.FIELDID_VALUE_DATASET_SEPERATOR;
            }
        }

        if (!Utility.safeTrim(postedIdValues).equals(Constants.EMPTY_STRING)){
            postedIdValues = postedIdValues.substring(0, postedIdValues.length() - Constants.FIELDID_VALUE_DATASET_SEPERATOR.length());
        }
        return postedIdValues;
    }

    private String ValidarCampoTextoProducto(View view){
        String valor = ((EditText)view).getText().toString();
        if(valor.contains(Constants.FIELDID_VALUE_DATASET_SEPERATOR) || valor.contains(Constants.DYNAID_PREFIX_SEPERATOR) || valor.contains(Constants.PUNTO_Y_COMA)){
            return "NOT_OK";
        }else{
            return "OK";
        }
    }

    private String ValidarCampoTexto(View view){
        String valor = ((EditText)view).getText().toString();
        if(valor.contains(Constants.FIELDID_VALUE_DATASET_SEPERATOR) || valor.contains(Constants.DYNAID_PREFIX_SEPERATOR) || valor.contains(Constants.PUNTO_Y_COMA) || valor.contains(Constants.FIELDID_VALUE_SEPERATOR)){
            return "NOT_OK";
        }else{
            return "OK";
        }
    }

    public View createInputLabel(Context ctx, String labelName){

        TextView labelView = new TextView(ctx);
        labelView.setText(labelName + " :");
        //labelView.setTypeface(Typeface.DEFAULT_BOLD);
        labelView.setTextSize(18);
        labelView.setTextColor(parseColor("#275b89"));
        labelView.setPadding(0, 90, 0, 20);

        return labelView;
    }

    public View createTextView(Context ctx, int id){

        EditText textBoxView = new EditText(ctx);
        textBoxView.setId(id);
        textBoxView.setTextSize(18);
        textBoxView.setBackgroundDrawable(getResources().getDrawable(R.drawable.rpt_form_edittextview));
        textBoxView.setOnFocusChangeListener(mTextValidationsListener);

        return textBoxView;

    }

    public View createTextAreaView(Context ctx, int id){

        EditText textAreaView = new EditText(ctx);
        textAreaView.setId(id);
        textAreaView.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        textAreaView.setLines(Constants.DYNA_CONTROL_TYPE_TEXTAREA_LINES);
        textAreaView.setGravity(Gravity.TOP);
        textAreaView.setTextSize(18);
        textAreaView.setBackgroundDrawable(getResources().getDrawable(R.drawable.rpt_form_edittextview));
        textAreaView.setOnFocusChangeListener(mTextValidationsListener);

        return textAreaView;

    }

    public View createComboBoxViewObra(Context ctx, int id){
        spinnerViewObra = new Spinner(ctx);
        spinnerViewObra.setId(id);
        //List<String> listValues = null;
        List<String> listValuesWithInitialValue = new ArrayList<>();
        listValuesWithInitialValue.add(getString(R.string.rpt_form_spinner_initialvalue));
        //listValuesWithInitialValue.addAll(listValues);

        //ArrayAdapter<String> spinnerListAdapter = new ArrayAdapter<String>(ctx, android.R.layout.simple_spinner_dropdown_item, listValues);
        ArrayAdapter<String> spinnerListAdapter = new ArrayAdapter<String>(ctx, R.layout.rpt_form_spinner, listValuesWithInitialValue);
        spinnerViewObra.setAdapter(spinnerListAdapter);
        spinnerViewObra.setOnItemSelectedListener(new SpinnerItemListener());

        return spinnerViewObra;

    }

    public View createDyna2TextBoxViewOperarios(Context ctx, int id, String ctrlName){
        String tableId = Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_TABLE_LAYOUT_IDOP + id;
        String tableRowId = Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_TABLE_LAYOUT_ROW_IDOP + id + dyna2TBIdCounterOp;
        String tableRowId2 = Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_TABLE_LAYOUT_ROW_IDOP + id + 1 + dyna2TBIdCounterOp;
        String plusButtonID = Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_PLUS_BUTTON_IDOP + id + dyna2TBIdCounterOp;
        String minusButtonID = Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_MINUS_BUTTON_IDOP + id + dyna2TBIdCounterOp;

        String combId = id + Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_SUFFIX_FIRSTOP + dyna2TBIdCounterOp;
        String tb2Id = id + Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_SUFFIX_SECONDOP + dyna2TBIdCounterOp;

        TableLayout tableLayout = new TableLayout(ctx);
        tableLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        tableLayout.setId(Integer.parseInt(tableId));

        TableRow row1 = new TableRow(ctx);
        row1.setId(Integer.parseInt(tableRowId));

        TableRow row2 = new TableRow(ctx);
        row2.setId(Integer.parseInt(tableRowId2));

        String tipo = "";
        TableRow.LayoutParams rowTextParams = new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, .35f);
        TableRow.LayoutParams rowButtonParams = new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, .15f);
        TableRow.LayoutParams rowAutoParams = new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, .55f);
        rowTextParams.setMargins(15, 0, 0, 0);
        rowButtonParams.setMargins(5, 0, 0, 0);


        tipo = "Operarios";
        Context contexto = getActivity().getApplicationContext();

        List<String> Operarios = Arrays.asList("MIQUEL","HORES GRUA","XAVI","JAUME RAMON","MANOLO SANTACRUZ","ENRIQUE RUIZ","ALQUILER PLATAFORMA ELEVADORA","MARIA DEL MAR");

        final String[] OperariosList = Operarios.toArray(new String[Operarios.size()]);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(contexto, R.layout.autocomplete_prod_style, OperariosList);

        AutoCompleteTextView autocompleteOper= new AutoCompleteTextView(ctx);
        autocompleteOper.setAdapter(adapter);
        autocompleteOper.setThreshold(1);
        autocompleteOper.setVisibility(View.VISIBLE);
        autocompleteOper.setHint("Operarios");
        autocompleteOper.setId(Integer.parseInt(combId));
        autocompleteOper.setLayoutParams(rowAutoParams);
        row1.addView(autocompleteOper);

        EditText textBoxView2 = new EditText(ctx);
        textBoxView2.setId(Integer.parseInt(tb2Id));
        textBoxView2.setLayoutParams(rowTextParams);
        textBoxView2.setTextSize(18);
        textBoxView2.setBackgroundDrawable(getResources().getDrawable(R.drawable.rpt_form_edittextview));
        textBoxView2.setOnFocusChangeListener(mTextValidationsListener);
        row2.addView(textBoxView2);

       /* Button minusBtn = new Button(ctx);
        minusBtn.setId(Integer.parseInt(minusButtonID));
        minusBtn.setText("-");
        minusBtn.setTextSize(18);
        minusBtn.setLayoutParams(rowButtonParams);
        minusBtn.setTextColor(parseColor("#275b89"));
        minusBtn.setTypeface(Typeface.DEFAULT_BOLD);
        minusBtn.setOnClickListener(mDyna2TextBoxesListenerOp);
        row2.addView(minusBtn);*/

        Button plusBtn = new Button(ctx);
        plusBtn.setId(Integer.parseInt(plusButtonID));
        plusBtn.setText("+");
        plusBtn.setTextSize(18);
        plusBtn.setLayoutParams(rowButtonParams);
        plusBtn.setTextColor(parseColor("#275b89"));
        plusBtn.setTypeface(Typeface.DEFAULT_BOLD);


        plusBtn.setOnClickListener(mDyna2TextBoxesListenerOp);
        row2.addView(plusBtn);

        tableLayout.addView(row1);
        tableLayout.addView(row2);

        dyna2TBIdsCtrsOp.put(Integer.toString(id) + dyna2TBIdCounterOp, id + "|" + dyna2TBIdCounterOp);
        ++dyna2TBIdCounterOp;

        return tableLayout;
    }

    public View createDyna2TextBoxViewProductos(Context ctx, int id, String ctrlName){
        String tableId = Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_TABLE_LAYOUT_IDPR + id;
        String tableRowId = Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_TABLE_LAYOUT_ROW_IDPR + id + dyna2TBIdCounterPr;
        String tableRowId2 = Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_TABLE_LAYOUT_ROW_IDPR + id + 1 + dyna2TBIdCounterPr;
        String plusButtonID = Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_PLUS_BUTTON_IDPR + id + dyna2TBIdCounterPr;
        String minusButtonID = Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_MINUS_BUTTON_IDPR + id + dyna2TBIdCounterPr;

        String combId = id + Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_SUFFIX_FIRSTPR + dyna2TBIdCounterPr;
        //String tb1Id = id + Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_SUFFIX_FIRST + dyna2TBIdCounter;
        String tb2Id = id + Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_SUFFIX_SECONDPR + dyna2TBIdCounterPr;

        TableLayout tableLayout = new TableLayout(ctx);
        tableLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        tableLayout.setId(Integer.parseInt(tableId));

        TableRow row1 = new TableRow(ctx);
        row1.setId(Integer.parseInt(tableRowId));

        TableRow row2 = new TableRow(ctx);
        row2.setId(Integer.parseInt(tableRowId2));
        //row1.setWeightSum(1);
        String tipo = "";
        TableRow.LayoutParams rowTextParams = new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, .35f);
        TableRow.LayoutParams rowButtonParams = new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, .15f);
        TableRow.LayoutParams rowAutoParams = new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, .55f);
        TableRow.LayoutParams rowAutoParamsProd = new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, .110f);
        rowTextParams.setMargins(15, 0, 0, 0);
        rowButtonParams.setMargins(5, 0, 0, 0);


        tipo = "Productos";
        Context contexto = getActivity().getApplicationContext();
        LogManager.log(LOG_TAG, "Fetching the customer list...", Log.DEBUG);
        ProductsListAction action = new ProductsListAction(getActivity());
        action.execute();

        List<String> Productos = action.getProductNames2lines();
        final String[] ProductosList = Productos.toArray(new String[Productos.size()]);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(contexto, R.layout.autocomplete_prod_style, ProductosList);

        AutoCompleteTextView autocompleteProd = new AutoCompleteTextView(ctx);
        //autocompleteProd.setId(Integer.parseInt(combId));
        autocompleteProd.setAdapter(adapter);
        autocompleteProd.setThreshold(1);
        autocompleteProd.setVisibility(View.VISIBLE);
        autocompleteProd.setHint("Productos");
        autocompleteProd.setId(Integer.parseInt(combId));
        autocompleteProd.setLayoutParams(rowAutoParamsProd);
        row1.addView(autocompleteProd);

        EditText textBoxView2 = new EditText(ctx);
        textBoxView2.setId(Integer.parseInt(tb2Id));
        textBoxView2.setLayoutParams(rowTextParams);
        textBoxView2.setTextSize(18);
        textBoxView2.setBackgroundDrawable(getResources().getDrawable(R.drawable.rpt_form_edittextview));
        textBoxView2.setOnFocusChangeListener(mTextValidationsListener);
        row2.addView(textBoxView2);

       /* Button minusBtn = new Button(ctx);
        minusBtn.setId(Integer.parseInt(minusButtonID));
        minusBtn.setText("-");
        minusBtn.setTextSize(18);
        minusBtn.setLayoutParams(rowButtonParams);
        minusBtn.setTextColor(parseColor("#275b89"));
        minusBtn.setTypeface(Typeface.DEFAULT_BOLD);
        minusBtn.setOnClickListener(mDyna2TextBoxesListenerPr);
        row2.addView(minusBtn);*/

        Button plusBtn = new Button(ctx);
        plusBtn.setId(Integer.parseInt(plusButtonID));
        plusBtn.setText("+");
        plusBtn.setTextSize(18);
        plusBtn.setLayoutParams(rowButtonParams);
        plusBtn.setTextColor(parseColor("#275b89"));
        plusBtn.setTypeface(Typeface.DEFAULT_BOLD);

        plusBtn.setOnClickListener(mDyna2TextBoxesListenerPr);
        row2.addView(plusBtn);

        tableLayout.addView(row1);
        tableLayout.addView(row2);

        dyna2TBIdsCtrsPr.put(Integer.toString(id) + dyna2TBIdCounterPr, id + "|" + dyna2TBIdCounterPr);
        ++dyna2TBIdCounterPr;

        return tableLayout;
    }

    public View createImageUploadView(Context ctx, int id){
        TableLayout tableLayout = new TableLayout(ctx);
        tableLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        TableRow row = new TableRow(ctx);

        Button imageUploadButton = new Button(ctx);
        imageUploadButton.setId(id);
        imageUploadButton.setText(ctx.getText(R.string.btn_rpt_form_dyna_imageUpload));
        imageUploadButton.setTransformationMethod(null);
        imageUploadButton.setOnClickListener(mImageViewBtnListener);
        row.addView(imageUploadButton);

        String photoSelectedId = id + Constants.DYNA_CONTROL_TYPE_IMAGE_PHOTO_SELECTED_LABEL_ID;
        TextView imageUploadLabelView = new TextView(ctx);
        imageUploadLabelView.setId(Integer.parseInt(photoSelectedId));
        imageUploadLabelView.setTextColor(parseColor("#E57373"));
        row.addView(imageUploadLabelView);

        tableLayout.addView(row);

        return tableLayout;

    }

    public void createSignPadView(Context ctx, int id){

        LinearLayout signPadLayout = (LinearLayout)rootView.findViewById(R.id.layout_rpt_form_signpad);
        signPadLayout.setVisibility(View.VISIBLE);

        mSignaturePad = (SignaturePad) rootView.findViewById(R.id.signature_pad);
        mSignaturePad.setOnSignedListener(mSignPadListener);

        Button signPadClearBtn = (Button) rootView.findViewById(R.id.rpt_form_signpad_clear_btn);
        signPadClearBtn.setOnClickListener(mSignPadBtnsListener);

        return;

    }

    private View.OnClickListener mFormBtnsListener = new View.OnClickListener() {

        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_rpt_form_save:
                    submitReport();
                    break;
                case R.id.btn_rpt_form_reset:
                    reset();
                    break;
                default:
                    break;
            }
        }
    };

    private class SpinnerItemListener implements AdapterView.OnItemSelectedListener  {
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            int spinnerId = parent.getId();
            String selectedItem = parent.getItemAtPosition(position).toString();
            if (!Utility.safeTrim(selectedItem).equals(getString(R.string.rpt_form_spinner_initialvalue))){
                spinnerValues.put(spinnerId, selectedItem);
            }
        }
        public void onNothingSelected(AdapterView<?> parent) {}
    }

    private View.OnClickListener mChkBoxListener = new View.OnClickListener() {
        public void onClick(View view) {
            int chkBoxId = view.getId();
            boolean checked = ((CheckBox) view).isChecked();
            if (checked){
                checkBoxValues.put(chkBoxId, Constants.DYNA_CONTROL_TYPE_CHECKBOX_DEFAULT_VALUE);
            }else{
                if (checkBoxValues.containsKey(chkBoxId)){
                    checkBoxValues.remove(chkBoxId);
                }
            }
        }
    };

    private View.OnClickListener mDatePickerListener = new View.OnClickListener() {
        public void onClick(View view) {
            DialogFragment newFragment = new DatePickerFragment();
            newFragment.show(getActivity().getFragmentManager(), Integer.toString(view.getId()));

        }
    };

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            String id = this.getTag();
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            String id = this.getTag();
            month = month + 1;
            String selectedDate = day + "/" + month + "/" + year;
            EditText dateView = (EditText) this.getActivity().findViewById(Integer.parseInt(id));
            dateView.setText(selectedDate);
        }
    }

    private View.OnClickListener mTimePickerListener = new View.OnClickListener() {

        public void onClick(View view) {
            DialogFragment newFragment = new TimePickerFragment();
            newFragment.show(getActivity().getFragmentManager(), Integer.toString(view.getId()));

        }
    };

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener  {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            String id = this.getTag();
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            return new TimePickerDialog(getActivity(), this, hour, minute, false);
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String id = this.getTag();
            String selectedTime = Utility.get12HourFormatTime(hourOfDay, minute);
            EditText timeView = (EditText) this.getActivity().findViewById(Integer.parseInt(id));
            timeView.setText(selectedTime);
        }

    }

    private View.OnClickListener mDyna2TextBoxesListenerOp = new View.OnClickListener() {
        public void onClick(View view) {
            Context ctx = view.getContext();
            String buttonId = Integer.toString(view.getId());
            if(buttonId.startsWith(Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_PLUS_BUTTON_IDOP)) {
                String idAndCtr = buttonId.substring(Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_PLUS_BUTTON_IDOP.length());
                idAndCtr = dyna2TBIdsCtrsOp.get(idAndCtr);
                String[] parsedIds = idAndCtr.split(Constants.REG_EX_PIPE_SEPERATOR);
                String orgId = parsedIds[0];
                String ctrValue = parsedIds[1];

                String tableId = Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_TABLE_LAYOUT_IDOP + orgId;
                String tableRowId = Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_TABLE_LAYOUT_ROW_IDOP + orgId + dyna2TBIdCounterOp;
                String tableRowId2 = Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_TABLE_LAYOUT_ROW_IDOP + orgId + 1 + dyna2TBIdCounterOp;
                String plusButtonID = Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_PLUS_BUTTON_IDOP + orgId + dyna2TBIdCounterOp;
                String minusButtonID = Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_MINUS_BUTTON_IDOP + orgId + dyna2TBIdCounterOp;

                String combId = orgId + Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_SUFFIX_FIRSTOP + dyna2TBIdCounterOp;
                //String tb1Id = orgId + Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_SUFFIX_FIRST + dyna2TBIdCounter;
                String tb2Id = orgId + Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_SUFFIX_SECONDOP + dyna2TBIdCounterOp;

                TableRow.LayoutParams rowTextParams = new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, .35f);
                TableRow.LayoutParams rowButtonParams = new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, .15f);
                TableRow.LayoutParams rowAutoParams = new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, .55f);
                TableRow.LayoutParams rowAutoParamsProd = new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, .110f);
                rowTextParams.setMargins(15, 0, 0, 0);
                rowButtonParams.setMargins(5, 0, 0, 0);

                TableLayout tableLayout = (TableLayout) rootView.findViewById(Integer.parseInt(tableId));

                TableRow row1 = new TableRow(ctx);
                row1.setId(Integer.parseInt(tableRowId));

                TableRow row2 = new TableRow(ctx);
                row2.setId(Integer.parseInt(tableRowId2));

                Context contexto = getActivity().getApplicationContext();
                List<String> Operarios = Arrays.asList("MIQUEL","HORES GRUA","XAVI","JAUME RAMON","MANOLO SANTACRUZ","ENRIQUE RUIZ","ALQUILER PLATAFORMA ELEVADORA","MARIA DEL MAR");
                final String[] OperariosList = Operarios.toArray(new String[Operarios.size()]);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(contexto, R.layout.autocomplete_prod_style, OperariosList);

                AutoCompleteTextView autocompleteOper= new AutoCompleteTextView(ctx);
                autocompleteOper.setAdapter(adapter);
                autocompleteOper.setThreshold(1);
                autocompleteOper.setVisibility(View.VISIBLE);
                autocompleteOper.setHint("Operarios");
                autocompleteOper.setId(Integer.parseInt(combId));
                autocompleteOper.setLayoutParams(rowAutoParams);
                row1.addView(autocompleteOper);

                EditText textBoxView2 = new EditText(ctx);
                textBoxView2.setId(Integer.parseInt(tb2Id));
                textBoxView2.setLayoutParams(rowTextParams);
                textBoxView2.setTextSize(18);
                textBoxView2.setBackgroundDrawable(getResources().getDrawable(R.drawable.rpt_form_edittextview));
                textBoxView2.setOnFocusChangeListener(mTextValidationsListener);
                row2.addView(textBoxView2);

                Button minusBtn = new Button(ctx);
                minusBtn.setId(Integer.parseInt(minusButtonID));
                minusBtn.setText("-");
                minusBtn.setTextSize(18);
                minusBtn.setLayoutParams(rowButtonParams);
                minusBtn.setTextColor(parseColor("#275b89"));
                minusBtn.setTypeface(Typeface.DEFAULT_BOLD);

                minusBtn.setOnClickListener(mDyna2TextBoxesListenerOp);
                row2.addView(minusBtn);

                Button plusBtn = new Button(ctx);
                plusBtn.setId(Integer.parseInt(plusButtonID));
                plusBtn.setText("+");
                plusBtn.setTextSize(18);
                plusBtn.setLayoutParams(rowButtonParams);
                plusBtn.setTextColor(parseColor("#275b89"));
                plusBtn.setTypeface(Typeface.DEFAULT_BOLD);

                plusBtn.setOnClickListener(mDyna2TextBoxesListenerOp);
                row2.addView(plusBtn);

                row1.setPadding(0,30,0,10);

                tableLayout.addView(row1);
                tableLayout.addView(row2);

                dyna2TBIdsCtrsOp.put(orgId + dyna2TBIdCounterOp, orgId + "|" + dyna2TBIdCounterOp);
                ++dyna2TBIdCounterOp;

                //Remove current plus button
                tableLayout.removeView(view);

            }else if (buttonId.startsWith(Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_MINUS_BUTTON_IDOP)){
                String mapKey = buttonId.substring(Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_PLUS_BUTTON_IDOP.length());
                String idAndCtr = dyna2TBIdsCtrsOp.get(mapKey);

                String[] parsedIds = idAndCtr.split(Constants.REG_EX_PIPE_SEPERATOR);
                String orgId = parsedIds[0];
                String ctrValue = parsedIds[1];
                String tableId = Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_TABLE_LAYOUT_IDOP + orgId;
                String tableRowId = Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_TABLE_LAYOUT_ROW_IDOP + orgId + ctrValue;
                String tableRowId2 = Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_TABLE_LAYOUT_ROW_IDOP + orgId + 1 + ctrValue;
                String plusButtonID = Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_PLUS_BUTTON_IDOP + orgId + ctrValue;
                String minusButtonID = Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_MINUS_BUTTON_IDOP + orgId + ctrValue;
                String tb1Id = orgId + Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_SUFFIX_FIRSTOP + ctrValue;
                String tb2Id = orgId + Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_SUFFIX_SECONDOP + ctrValue;

                TableLayout tableLayout = (TableLayout) rootView.findViewById(Integer.parseInt(tableId));
                tableLayout.removeView(rootView.findViewById(Integer.parseInt(tableRowId)));
                tableLayout.removeView(rootView.findViewById(Integer.parseInt(tableRowId2)));

                dyna2TBIdsCtrsOp.remove(mapKey);
            }
        }
    };

    private View.OnClickListener mDyna2TextBoxesListenerPr = new View.OnClickListener() {
        public void onClick(View view) {
            Context ctx = view.getContext();
            String buttonId = Integer.toString(view.getId());
            if(buttonId.startsWith(Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_PLUS_BUTTON_IDPR)) {
                String idAndCtr = buttonId.substring(Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_PLUS_BUTTON_IDPR.length());
                idAndCtr = dyna2TBIdsCtrsPr.get(idAndCtr);
                String[] parsedIds = idAndCtr.split(Constants.REG_EX_PIPE_SEPERATOR);
                String orgId = parsedIds[0];
                String ctrValue = parsedIds[1];

                String tableId = Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_TABLE_LAYOUT_IDPR + orgId;
                String tableRowId = Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_TABLE_LAYOUT_ROW_IDPR + orgId + dyna2TBIdCounterPr;
                String tableRowId2 = Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_TABLE_LAYOUT_ROW_IDPR + orgId + 1 + dyna2TBIdCounterPr;
                String plusButtonID = Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_PLUS_BUTTON_IDPR + orgId + dyna2TBIdCounterPr;
                String minusButtonID = Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_MINUS_BUTTON_IDPR + orgId + dyna2TBIdCounterPr;

                String combId = orgId + Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_SUFFIX_FIRSTPR + dyna2TBIdCounterPr;
                //String tb1Id = orgId + Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_SUFFIX_FIRST + dyna2TBIdCounter;
                String tb2Id = orgId + Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_SUFFIX_SECONDPR + dyna2TBIdCounterPr;

                TableRow.LayoutParams rowTextParams = new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, .35f);
                TableRow.LayoutParams rowButtonParams = new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, .15f);
                TableRow.LayoutParams rowAutoParamsProd = new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, .110f);
                rowTextParams.setMargins(15, 0, 0, 0);
                rowButtonParams.setMargins(5, 0, 0, 0);

                TableLayout tableLayout = (TableLayout) rootView.findViewById(Integer.parseInt(tableId));

                TableRow row1 = new TableRow(ctx);
                row1.setId(Integer.parseInt(tableRowId));

                TableRow row2 = new TableRow(ctx);
                row2.setId(Integer.parseInt(tableRowId2));

                Context contexto = getActivity().getApplicationContext();
                LogManager.log(LOG_TAG, "Fetching the customer list...", Log.DEBUG);
                ProductsListAction action = new ProductsListAction(getActivity());
                action.execute();

                List<String> Productos = action.getProductNames2lines();
                final String[] ProductosList = Productos.toArray(new String[Productos.size()]);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(contexto, R.layout.autocomplete_prod_style, ProductosList);

                AutoCompleteTextView autocompleteProd = new AutoCompleteTextView(ctx);
                autocompleteProd.setAdapter(adapter);
                autocompleteProd.setThreshold(1);
                autocompleteProd.setVisibility(View.VISIBLE);
                autocompleteProd.setHint("Productos");
                autocompleteProd.setId(Integer.parseInt(combId));
                autocompleteProd.setLayoutParams(rowAutoParamsProd);
                row1.addView(autocompleteProd);

                EditText textBoxView2 = new EditText(ctx);
                textBoxView2.setId(Integer.parseInt(tb2Id));
                textBoxView2.setLayoutParams(rowTextParams);
                textBoxView2.setTextSize(18);
                textBoxView2.setBackgroundDrawable(getResources().getDrawable(R.drawable.rpt_form_edittextview));
                textBoxView2.setOnFocusChangeListener(mTextValidationsListener);
                row2.addView(textBoxView2);

                Button minusBtn = new Button(ctx);
                minusBtn.setId(Integer.parseInt(minusButtonID));
                minusBtn.setText("-");
                minusBtn.setTextSize(18);
                minusBtn.setLayoutParams(rowButtonParams);
                minusBtn.setTextColor(parseColor("#275b89"));
                minusBtn.setTypeface(Typeface.DEFAULT_BOLD);

                minusBtn.setOnClickListener(mDyna2TextBoxesListenerPr);
                row2.addView(minusBtn);

                Button plusBtn = new Button(ctx);
                plusBtn.setId(Integer.parseInt(plusButtonID));
                plusBtn.setText("+");
                plusBtn.setTextSize(18);
                plusBtn.setLayoutParams(rowButtonParams);
                plusBtn.setTextColor(parseColor("#275b89"));
                plusBtn.setTypeface(Typeface.DEFAULT_BOLD);
                plusBtn.setOnClickListener(mDyna2TextBoxesListenerPr);
                row2.addView(plusBtn);

                row1.setPadding(0,30,0,10);

                tableLayout.addView(row1);
                tableLayout.addView(row2);

                dyna2TBIdsCtrsPr.put(orgId + dyna2TBIdCounterPr, orgId + "|" + dyna2TBIdCounterPr);
                ++dyna2TBIdCounterPr;
                //Remove current plus button
                tableLayout.removeView(view);

            }else if (buttonId.startsWith(Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_MINUS_BUTTON_IDPR)){
                String mapKey = buttonId.substring(Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_PLUS_BUTTON_IDPR.length());
                String idAndCtr = dyna2TBIdsCtrsPr.get(mapKey);
                String[] parsedIds = idAndCtr.split(Constants.REG_EX_PIPE_SEPERATOR);
                String orgId = parsedIds[0];
                String ctrValue = parsedIds[1];

                String tableId = Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_TABLE_LAYOUT_IDPR + orgId;
                String tableRowId = Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_TABLE_LAYOUT_ROW_IDPR + orgId + ctrValue;
                String tableRowId2 = Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_TABLE_LAYOUT_ROW_IDPR + orgId + 1 + ctrValue;
                String plusButtonID = Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_PLUS_BUTTON_IDPR + orgId + ctrValue;
                String minusButtonID = Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_MINUS_BUTTON_IDPR + orgId + ctrValue;
                String tb1Id = orgId + Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_SUFFIX_FIRSTPR + ctrValue;
                String tb2Id = orgId + Constants.DYNA_CONTROL_TYPE_DYNAMIC_TEXTBOXES_2_SUFFIX_SECONDPR + ctrValue;
                TableLayout tableLayout = (TableLayout) rootView.findViewById(Integer.parseInt(tableId));
                tableLayout.removeView(rootView.findViewById(Integer.parseInt(tableRowId)));
                tableLayout.removeView(rootView.findViewById(Integer.parseInt(tableRowId2)));

                dyna2TBIdsCtrsPr.remove(mapKey);
            }
        }
    };

    private View.OnClickListener mImageViewBtnListener = new View.OnClickListener() {

        public void onClick(View view) {
            Context ctx = view.getContext();
            displayPhotoChooser(view);
        }

    };

    private SignaturePad.OnSignedListener mSignPadListener = new SignaturePad.OnSignedListener() {
        @Override
        public void onStartSigning() {
            //Toast.makeText(getActivity(), "OnStartSigning", Toast.LENGTH_SHORT).show();
            isSignaturePadUsed = true;
        }

        @Override
        public void onSigned() {
            (rootView.findViewById(R.id.rpt_form_signpad_clear_btn)).setEnabled(true);
        }

        @Override
        public void onClear() {
            (rootView.findViewById(R.id.rpt_form_signpad_clear_btn)).setEnabled(false);
            isSignaturePadUsed = false;
        }

    };

    private View.OnClickListener mSignPadBtnsListener = new View.OnClickListener() {

        public void onClick(View view) {

            switch (view.getId()) {

                case R.id.rpt_form_signpad_clear_btn:

                    mSignaturePad.clear();

                    break;

                default:
                    break;

            }

        }
    };

    private View.OnFocusChangeListener mTextValidationsListener = new View.OnFocusChangeListener() {

        public void onFocusChange(View view, boolean hasFocus) {

            if (view instanceof EditText) {
                String validatorMessage = validateInputText(view);
                if (!validatorMessage.equals("OK")){
                    ((EditText) view).setError(validatorMessage);
                }
                else{
                    ((EditText) view).setError(null);
                }
            }

        }
    };

    private String validateInputText(View view){

        AbstractBusinessValidator validator = new ReportValidator(getContext());
        String value = ((EditText)view).getText().toString();
        String validatorMessage = validator.validate(value);

        return validatorMessage;
    }

     private void displayPhotoChooser(View view) {

        Intent intent = new Intent();
        intent.setType("image/*");

        intent.setAction(Intent.ACTION_GET_CONTENT);
        //intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        requestCodesWithViewIds.put(Integer.toString(PICK_PHOTO_REQUEST), Integer.toString(view.getId()));

        startActivityForResult(Intent.createChooser(intent, getResources().getText(R.string.text_imageUpload_selectImage)), PICK_PHOTO_REQUEST);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PHOTO_REQUEST){
            if (resultCode == Activity.RESULT_OK) {
                if (null != data && null != data.getData()){
                    Uri selectedFileUri = data.getData();
                    LogManager.log(LOG_TAG, "Uri of photo selected : " + selectedFileUri, Log.DEBUG);

                    FileUtility.getImageByteStream(getContext(), selectedFileUri, photoBytesDataList);

                    if (null != photoBytesDataList && photoBytesDataList.size() > 0){
                        String imageName = photoBytesDataList.keySet().iterator().next();

                        String buttonId = requestCodesWithViewIds.get(Integer.toString(PICK_PHOTO_REQUEST));
                        String imageUploadLabelViewId = buttonId + Constants.DYNA_CONTROL_TYPE_IMAGE_PHOTO_SELECTED_LABEL_ID;
                        TextView imageUploadLabelView = (TextView) rootView.findViewById(Integer.parseInt(imageUploadLabelViewId));
                        //imageUploadLabelView.setText(getResources().getText(R.string.text_imageUpload_label_photoSelected));
                        imageUploadLabelView.setText(imageName);
                    }else{
                        String buttonId = requestCodesWithViewIds.get(Integer.toString(PICK_PHOTO_REQUEST));
                        String imageUploadLabelViewId = buttonId + Constants.DYNA_CONTROL_TYPE_IMAGE_PHOTO_SELECTED_LABEL_ID;
                        TextView imageUploadLabelView = (TextView) rootView.findViewById(Integer.parseInt(imageUploadLabelViewId));
                        imageUploadLabelView.setText("Error:Foto > 2MB");
                    }

                }
                else{

                    LogManager.log(LOG_TAG, "No Photo Selected", Log.DEBUG);
                }
            }

        }

    }

}
