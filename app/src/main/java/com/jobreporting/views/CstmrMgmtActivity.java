///*
// * Licensed To: ThoughtExecution & 9sistemes
// * Authored By: Rishi Raj Bansal
// * Developed in: 2016
// *
// * ===========================================================================
// * This is FULLY owned and COPYRIGHTED by ThoughtExecution
// * This code may NOT be RESOLD or REDISTRIBUTED under any circumstances, and is only to be used with this application
// * Using the code from this application in another application is strictly PROHIBITED and not PERMISSIBLE
// * ===========================================================================
// */
//
//package com.jobreporting.views;
//
//import android.content.Context;
//import android.graphics.Color;
//import android.graphics.drawable.ColorDrawable;
//import android.os.Bundle;
//import android.support.v7.app.ActionBar;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.EditText;
//
//import com.jobreporting.R;
//import com.jobreporting.utilities.Utility;
//
//public class CstmrMgmtActivity extends AppCompatActivity  {
//    private Toolbar mToolbar;
//    private MenuItem mSearchAction;
//    private boolean isSearchOpened = false;
//    private EditText edtSeach;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        setContentView(R.layout.activity_cstmr_mgmt);
//
//        mToolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(mToolbar);
//
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        mToolbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#607D8B")));
//        Utility.setStatusBarColor(this);
//
//    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_cstmr_mgmt, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        mSearchAction = menu.findItem(R.id.action_search);
//        return super.onPrepareOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        switch (id) {
//            case R.id.action_settings:
//                return true;
//            case R.id.action_search:
//                handleMenuSearch();
//                return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    protected void handleMenuSearch(){
//        ActionBar action = getSupportActionBar(); //get the actionbar
//
//        if(isSearchOpened){ //test if the search is open
//            action.setDisplayShowCustomEnabled(false); //disable a custom view inside the actionbar
//            action.setDisplayShowTitleEnabled(true); //show the title in the action bar
//
//            //hides the keyboard
//            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(edtSeach.getWindowToken(), 0);
//
//            //add the search icon in the action bar
//            mSearchAction.setIcon(getResources().getDrawable(R.drawable.ic_search));
//
//            isSearchOpened = false;
//        } else { //open the search entry
//
//            action.setDisplayShowCustomEnabled(true); //enable it to display a
//            // custom view in the action bar.
//            action.setCustomView(R.layout.search_bar);//add the custom view
//            action.setDisplayShowTitleEnabled(false); //hide the title
//
//            edtSeach = (EditText)action.getCustomView().findViewById(R.id.edtSearch); //the text editor
//            edtSeach.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                }
//
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {
//                    ActualizarListView(s);
//                }
//
//                @Override
//                public void afterTextChanged(Editable s) {
//
//                }
//
//
//            });
//
//            edtSeach.requestFocus();
//
//            //open the keyboard focused in the edtSearch
//            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.showSoftInput(edtSeach, InputMethodManager.SHOW_IMPLICIT);
//
//            //add the close icon
//            mSearchAction.setIcon(getResources().getDrawable(R.drawable.ic_home));
//
//            isSearchOpened = true;
//
//        }
//    }
//
//    public void ActualizarListView(CharSequence s) {
//        CstmrMgmtActivityFragment.customerListAdapter.getFilter().filter(s.toString());
//    }
//
//
//}
