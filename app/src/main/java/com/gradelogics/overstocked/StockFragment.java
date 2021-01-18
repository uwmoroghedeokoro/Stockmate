package com.gradelogics.overstocked;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StockFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StockFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StockFragment extends Fragment implements SearchView.OnQueryTextListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    ListView listview_ticker;
    public static symbolAdapter adapter;//=new symbolAdapter();
    SearchView searchView;
    public static ArrayList<stock_sym> my_syms;

    public StockFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StockFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StockFragment newInstance(String param1, String param2) {
        StockFragment fragment = new StockFragment();
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
        View root = inflater.inflate(R.layout.activity_live_stock_table, container, false);




        listview_ticker=(ListView) root.findViewById(R.id.lv);
        // Locate the EditText in listview_main.xml
        searchView = (SearchView) root.findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(this);

       // Intent intent=getIntent();

       // actionBar.setTitle("Live Stock Table");

        my_syms=new ArrayList<stock_sym>();
        adapter=new symbolAdapter(StockFragment.this.getContext(),R.layout.symbol_list_item,my_syms);

        return root;
        //load_all_syms();
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        adapter.filter(s);
        return false;
    }

    @Override
    public void onResume()
    {
        super.onResume();
       // load_all_syms();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser) {
            Log.e("where","stockfragment");
            my_syms=new ArrayList<stock_sym>();
            adapter=new symbolAdapter(StockFragment.this.getContext(),R.layout.symbol_list_item,my_syms);

            listview_ticker.setAdapter(adapter);
           // new load_me_symbols().execute("");

           StockFragment.this.getActivity().runOnUiThread(new Runnable() {
               @Override
               public void run() {
                   load_all_syms();
               }
           });

        } else {

        }
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }



    private class load_me_symbols extends AsyncTask<String,String,String >
    {


        @Override
        protected String doInBackground(String... strings) {
            stock_sym stockSym=new stock_sym(StockFragment.this.getContext());
            try {


                my_syms=stockSym.all_symbols();

                listview_ticker.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        final stock_sym item = (stock_sym)parent.getItemAtPosition(position);

                        // custom dialog
                        final Dialog dialog = new Dialog(StockFragment.this.getContext());
                        dialog.setContentView(R.layout.pop_price_notify);
                        dialog.setTitle("Price Alerts");

                        Button dialogButton = (Button) dialog.findViewById(R.id.btn_sv);

                        stock_sym stockm=new stock_sym(StockFragment.this.getContext());
                        stockm.sym_name=item.sym_name;
                        // TextView txtsym=(TextView) dialog.findViewById(R.id.txt_sym);
                        // txtsym.setText(item.sym_name);
                        Log.e("selecte ",item.sym_name);
                        final EditText alert_v=(EditText)dialog.findViewById(R.id.txt_price);
                        final Spinner spinsym=(Spinner)dialog.findViewById(R.id.spin_sym);

                        ArrayList<String> opt=new ArrayList<String>();opt.add("Above");opt.add("Below");
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(StockFragment.this.getContext(), R.layout.simple_white_spinner,opt);
                        spinsym.setAdapter(adapter); // this will set list of values to spinner



                        // if button is clicked, close the custom dialog
                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                stock_sym stockm=new stock_sym(StockFragment.this.getContext());
                                stockm.sym_name=item.sym_name;
                                String spinv = spinsym.getSelectedItem().toString();
                                //Log.e("spin",spinv);
                                if (!alert_v.getText().toString().equals("")) {
                                    float alert_value = Float.parseFloat(alert_v.getText().toString());
                                    stockm.add_sym_alert(alert_value,spinv);
                                    dialog.dismiss();
                                }
                            }
                        });

                        //dialog.show();
                        Intent sym_details=new Intent(StockFragment.this.getContext(),activity_sym_details.class);
                        sym_details.putExtra("symname",item.sym_name);
                        startActivity(sym_details);
                    }
                });


            }catch (Exception ex)
            {
                Log.e("stockd",ex.getMessage());
            }
            return "";
        }

        @Override
        protected void onPostExecute(String d)
        {
          //  Log.e("new list siez",String.valueOf(my_syms.size()));
            adapter=new symbolAdapter(StockFragment.this.getContext(),R.layout.symbol_list_item,my_syms);

            listview_ticker.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    final Handler handler = new Handler();
    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            try{
                //do your code here
               // load_portfolio();
                load_all_syms();
               // buildGraph();
                 Log.e("stock fragment","refresher");
            }
            catch (Exception e) {
                // TODO: handle exception
            }
            finally{
                //also call the same runnable to call it at regular interval
                handler.postDelayed(this, 60000);//1200000
            }
        }
    };

    public void load_all_syms()
    {
        String[] syms={"NCB","BIL","SVL","KW","SEP"};
        final stock_sym stockSym=new stock_sym(this.getContext());
        Log.e("action","refresh symbols");
        try {

            final Handler handler1=new Handler();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    my_syms=stockSym.all_symbols();

                    handler1.post(new Runnable() {
                        @Override
                        public void run() {

                            adapter=new symbolAdapter(StockFragment.this.getActivity(),R.layout.symbol_list_item,my_syms);

                            listview_ticker.setAdapter(adapter);

                            listview_ticker.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    final stock_sym item = (stock_sym)parent.getItemAtPosition(position);

                                    // custom dialog
                                    final Dialog dialog = new Dialog(StockFragment.this.getActivity());
                                    dialog.setContentView(R.layout.pop_price_notify);
                                    dialog.setTitle("Price Alerts");

                                    Button dialogButton = (Button) dialog.findViewById(R.id.btn_sv);

                                    stock_sym stockm=new stock_sym(StockFragment.this.getContext());
                                    stockm.sym_name=item.sym_name;
                                    // TextView txtsym=(TextView) dialog.findViewById(R.id.txt_sym);
                                    // txtsym.setText(item.sym_name);
                                    Log.e("selecte ",item.sym_name);
                                    final EditText alert_v=(EditText)dialog.findViewById(R.id.txt_price);
                                    final Spinner spinsym=(Spinner)dialog.findViewById(R.id.spin_sym);

                                    ArrayList<String> opt=new ArrayList<String>();opt.add("Above");opt.add("Below");
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(StockFragment.this.getContext(), R.layout.simple_white_spinner,opt);
                                    spinsym.setAdapter(adapter); // this will set list of values to spinner



                                    // if button is clicked, close the custom dialog
                                    dialogButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            stock_sym stockm=new stock_sym(StockFragment.this.getContext());
                                            stockm.sym_name=item.sym_name;
                                            String spinv = spinsym.getSelectedItem().toString();
                                            //Log.e("spin",spinv);
                                            if (!alert_v.getText().toString().equals("")) {
                                                float alert_value = Float.parseFloat(alert_v.getText().toString());
                                                stockm.add_sym_alert(alert_value,spinv);
                                                dialog.dismiss();
                                            }
                                        }
                                    });

                                    //dialog.show();
                                    Intent sym_details=new Intent(StockFragment.this.getContext(),activity_sym_details.class);
                                    sym_details.putExtra("symname",item.sym_name);
                                    startActivity(sym_details);
                                }
                            });


                        }
                    });
                }
            }).start();


        }catch (Exception ex)
        {
            Log.e("stockd",ex.getMessage());
        }



    }
}
