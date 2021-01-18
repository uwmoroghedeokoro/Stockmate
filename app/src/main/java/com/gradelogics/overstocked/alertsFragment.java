package com.gradelogics.overstocked;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import lecho.lib.hellocharts.view.ColumnChartView;


public class alertsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ListView lv;
    stock_sym thistock;
    Toolbar toolbar;
    ColumnChartView columnChartView;
    TextView icofilter;
    SharedPreferences sharedpreferences;
    public alertAdapter adapter;
    ArrayList<stock_sym.alertz> my_alertz;

    public alertsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment alertsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static alertsFragment newInstance(String param1, String param2) {
        alertsFragment fragment = new alertsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View root=inflater.inflate(R.layout.layout_alert_activity, container, false);


        lv=(ListView)root.findViewById(R.id.lv);
        //ViewGroup headerView = (ViewGroup)getLayoutInflater().inflate(R.layout.lv_alert_header, lv,false);
        // Add header view to the ListView
        // lv.addHeaderView(headerView);

        thistock=new stock_sym(alertsFragment.this.getContext());
        // final ArrayList<stock_sym> my_syms=new ArrayList<stock_sym>();
        my_alertz=thistock.my_alerts();

        adapter=new alertAdapter(alertsFragment.this.getContext(),R.layout.alert_item,my_alertz);

        lv.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // custom dialog
                final Dialog dialog = new Dialog(alertsFragment.this.getContext());
                dialog.setContentView(R.layout.pop_price_notify);
                dialog.setTitle("Net Position Notification");

                stock_sym stocksym=new stock_sym(alertsFragment.this.getContext());
                ArrayList arrayList=stocksym.get_symbols();

                final Spinner spintarget=(Spinner)dialog.findViewById(R.id.spin_target);
                final EditText target_price=(EditText)dialog.findViewById(R.id.txt_price);

                ArrayList<String> opt=new ArrayList<String>();opt.add("Above");opt.add("Below");
                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(alertsFragment.this.getContext(), R.layout.simple_white_spinner,opt);
                spintarget.setAdapter(adapter); // this will set list of values to spinner

                final ArrayAdapter<String> symadapter=new ArrayAdapter(alertsFragment.this.getContext(),R.layout.simple_white_spinner, arrayList);
                final Spinner sym_spin=dialog.findViewById(R.id.spin_sym);
                symadapter.setDropDownViewResource(R.layout.spinner_item_white);
                sym_spin.setAdapter(symadapter);

                Button dialogButton = (Button) dialog.findViewById(R.id.btn_sv);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        stock_sym stockm=new stock_sym(alertsFragment.this.getContext());
                        stockm.sym_name=sym_spin.getSelectedItem().toString();
                        String target_thresh = spintarget.getSelectedItem().toString();
                        //Log.e("spin",spinv);
                        if (!target_price.getText().toString().equals("")) {
                            float alert_value = Float.parseFloat(target_price.getText().toString());
                            stockm.add_sym_alert(alert_value,target_thresh);

                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            ft.detach(alertsFragment.this).attach(alertsFragment.this).commit();
                            dialog.dismiss();
                        }
                    }
                });

                dialog.show();
            }
        });
        return root;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

}
