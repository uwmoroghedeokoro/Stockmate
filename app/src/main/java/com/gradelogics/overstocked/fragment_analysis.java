package com.gradelogics.overstocked;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the

 * to handle interaction events.
 * Use the {@link fragment_analysis#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_analysis extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    View root;

    analysis_Rec_Adapter gainAdapter;
    analysis_Rec_Adapter loseAdapter;

    RecyclerView listGain;
    RecyclerView listLose;


    public fragment_analysis() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_analysis.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_analysis newInstance(String param1, String param2) {
        fragment_analysis fragment = new fragment_analysis();
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
        root = inflater.inflate(R.layout.fragment_analysis, container, false);

        listGain=(RecyclerView) root.findViewById(R.id.list_gain);
        listLose=(RecyclerView) root.findViewById(R.id.list_lose);

        listGain.setNestedScrollingEnabled(false);
        listLose.setNestedScrollingEnabled(false);

        TextView monthlabel=(TextView)root.findViewById(R.id.month_label);
        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat monthdate=new SimpleDateFormat("MMMM");
        monthlabel.setText(monthdate.format(calendar.getTime()) + " Market Analysis");


       // new get_analysis().execute("");
        return root;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser) {
            Log.e("where","stockfragment");
            ArrayList<stock_sym> items=new ArrayList<>();

           // listLose.setAdapter(loseAdapter);
           // listGain.setAdapter(gainAdapter);

            new get_analysis().execute("");
        } else {

        }
    }

    private class get_analysis extends AsyncTask<String,String,String>
    {
        HttpURLConnection urlConnection;
        @Override
        protected String doInBackground(String... strings) {
            String result="";
            try {


                String domain,schyear,schterm="";
                int studentid=0;
                SharedPreferences prefs=fragment_analysis.this.getActivity().getSharedPreferences("gradelogics",MODE_PRIVATE);
                Gson gson = new Gson();
                String json = prefs.getString("student_object", "");
                // student stuobj = gson.fromJson(json, student.class);
                URL url=new URL("https://stockmate2.gradelogics.com/api/analyze");
                Log.e("url",url.toString());
                urlConnection=(HttpURLConnection)url.openConnection();

                InputStream in=new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader=new BufferedReader(new InputStreamReader(in));

                String line;
                while((line=reader.readLine()) != null){
                    result=line;
                }
                urlConnection.disconnect();
            }catch (Exception e){
                Log.e("error",e.toString());
            }finally {

            }

            //  Log.e(" grades json",result);
            return result;
        }

        @Override
        protected void onPostExecute(String res)
        {
            try {



                JSONObject jsonObj=new JSONObject(res);
                Log.e("ready",res);

                JSONArray gainsArray=new JSONArray(jsonObj.getString("gain"));
                ArrayList<stock_sym> gains_items=new ArrayList<>();
                for (int x=0;x<gainsArray.length();x++)
                {
                    JSONObject jObj=gainsArray.getJSONObject(x);
                  //  Log.e("gain",jObj.getString("symbol"));
                    stock_sym stockSym=new stock_sym(fragment_analysis.this.getActivity());
                    stockSym.sym_name=jObj.getString("symbol");
                    stockSym.change=BigDecimal.valueOf(jObj.getDouble("gain")).floatValue();
                    stockSym.change_perc=BigDecimal.valueOf(jObj.getDouble("gain_perc")).floatValue();
                    stockSym.high=BigDecimal.valueOf(jObj.getDouble("high")).floatValue();
                    stockSym.low=BigDecimal.valueOf(jObj.getDouble("low")).floatValue();
                    stockSym.current_price=BigDecimal.valueOf(jObj.getDouble("price")).floatValue();
                    gains_items.add(stockSym);
                }

                gainAdapter=new analysis_Rec_Adapter(gains_items,fragment_analysis.this.getContext());
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(fragment_analysis.this.getContext());
                listGain.setLayoutManager(mLayoutManager);
               listGain.setAdapter(gainAdapter);

               /// LOSERS

                JSONArray loseArray=new JSONArray(jsonObj.getString("lose"));
                ArrayList<stock_sym> lose_items=new ArrayList<>();
                for (int x=0;x<loseArray.length();x++)
                {
                    JSONObject jObj=loseArray.getJSONObject(x);
                    //  Log.e("gain",jObj.getString("symbol"));
                    stock_sym stockSym=new stock_sym(fragment_analysis.this.getActivity());
                    stockSym.sym_name=jObj.getString("symbol");
                    stockSym.change=BigDecimal.valueOf(jObj.getDouble("gain")).floatValue();
                    stockSym.change_perc=BigDecimal.valueOf(jObj.getDouble("gain_perc")).floatValue();
                    stockSym.high=BigDecimal.valueOf(jObj.getDouble("high")).floatValue();
                    stockSym.low=BigDecimal.valueOf(jObj.getDouble("low")).floatValue();
                    stockSym.current_price=BigDecimal.valueOf(jObj.getDouble("price")).floatValue();
                    lose_items.add(stockSym);
                }

                loseAdapter=new analysis_Rec_Adapter(lose_items,fragment_analysis.this.getContext());
                RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(fragment_analysis.this.getContext());
                listLose.setLayoutManager(mLayoutManager2);
                listLose.setAdapter(loseAdapter);
            }catch (Exception e)
            {
                Log.e("exception",e.getMessage().toString());

            }finally {
                // loadUI();
            }
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

}
