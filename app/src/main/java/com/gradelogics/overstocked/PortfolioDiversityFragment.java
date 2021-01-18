package com.gradelogics.overstocked;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.NumberFormat;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PortfolioDiversityFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PortfolioDiversityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PortfolioDiversityFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private View root;

    public PortfolioDiversityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PortfolioDiversityFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PortfolioDiversityFragment newInstance(String param1, String param2) {
        PortfolioDiversityFragment fragment = new PortfolioDiversityFragment();
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
        root= inflater.inflate(R.layout.activity_portfolio, container, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            PortfolioDiversityFragment.this.getActivity().getWindow().setStatusBarColor(Color.parseColor("#46B7F8"));
        }
        TextView txtvalue=(TextView)root.findViewById(R.id.txt_total_invest);
        TextView txtreturn=(TextView)root.findViewById(R.id.txt_total_return);


        stock_sym istok=new stock_sym(PortfolioDiversityFragment.this.getActivity());
        stock_sym.portfolio portfolio=istok.portfolio_position();



        NumberFormat format = NumberFormat.getCurrencyInstance();


        txtvalue.setText(format.format(portfolio.total_invest));
        txtreturn.setText(format.format(portfolio.total_return));

        PieChart pieChart = root.findViewById(R.id.chart);
        ArrayList NoOfEmp = new ArrayList();

        stock_sym stockSym=new stock_sym(PortfolioDiversityFragment.this.getActivity());
        ArrayList<stock_sym> my_syms=stockSym.my_symbols();
        ArrayList year = new ArrayList();

        int x=0;
        for (stock_sym stc:my_syms) {
            stc.get_details();
            NoOfEmp.add(new Entry(((stc.sym_details.total_invest/portfolio.total_invest)*100), x));
            year.add(stc.sym_name);
            x++;
        }

       /* NoOfEmp.add(new Entry(945f, 0));
        NoOfEmp.add(new Entry(1040f, 1));
        NoOfEmp.add(new Entry(1133f, 2));
        NoOfEmp.add(new Entry(1240f, 3));
        NoOfEmp.add(new Entry(1369f, 4));
        NoOfEmp.add(new Entry(1487f, 5));
        NoOfEmp.add(new Entry(1501f, 6));
        NoOfEmp.add(new Entry(1645f, 7));
        NoOfEmp.add(new Entry(1578f, 8));
        NoOfEmp.add(new Entry(1695f, 9));*/
        PieDataSet dataSet = new PieDataSet(NoOfEmp, "");


        int [] color={ Color.parseColor("#003f5c"), Color.parseColor("#2f4b7c"), Color.parseColor("#665191"),
                Color.parseColor("#a05195"),Color.parseColor("#d45087"), Color.parseColor("#f95d6a"),
                Color.parseColor("#ff7c43"), Color.parseColor("#ffa600"), Color.parseColor("#7aa6c2"), Color.parseColor("#9dc6e0")
        };

        year.add("2008");
        year.add("2009");
        year.add("2010");
        year.add("2011");
        year.add("2012");
        year.add("2013");
        year.add("2014");
        year.add("2015");
        year.add("2016");
        year.add("2017");
        PieData data = new PieData(year, dataSet);
        pieChart.setData(data);
        pieChart.setCenterText(generateCenterSpannableText(String.valueOf(format.format(portfolio.total_value))));
        pieChart.setCenterTextSize(10f);
        dataSet.setColors(color);
        //  dataSet.sett
        pieChart.animateXY(5000, 5000);

// adding legends to the desigred positions
        Legend l = pieChart.getLegend();
        l.setTextSize(12f);

        //l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        //l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        //l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        //l.setDrawInside(false);
        l.setTextColor(Color.BLACK);
        l.setEnabled(true);

        return root;
    }


    // If we need to display center text with textStyle
    private SpannableString generateCenterSpannableText(String port_value) {
        SpannableString s = new SpannableString("Portfolio Value\n" + port_value);
        s.setSpan(new RelativeSizeSpan(2f), 15, s.length(), 0);
        s.setSpan(new StyleSpan(Typeface.BOLD), 15, s.length(), 0);
        return s;
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
