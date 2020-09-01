package com.meow.comp6442_groupproject.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.meow.comp6442_groupproject.Model.Result;
import com.meow.comp6442_groupproject.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


public class ShowAnalyzeAdapter extends RecyclerView.Adapter<ShowAnalyzeAdapter.MyViewHolder> {

    // Define a list of result
    private List<Result> resList;
    private Context mContext;

    // Set up constructor
    public ShowAnalyzeAdapter(Context mContext,  List<Result> rlist) {
        this.mContext = mContext;
        resList = rlist;
    }



    @NonNull
    @Override
    //Self defined ViewHolder, the view of each question
    public ShowAnalyzeAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = (View) LayoutInflater.from(parent.getContext()).
                inflate(R.layout.chart,parent,false);

        MyViewHolder vh = new MyViewHolder(view);
        return vh;
    }



    @Override
    // Set up the view holder and set up anychartview
    public void onBindViewHolder(@NonNull ShowAnalyzeAdapter.MyViewHolder holder, int position) {

        //Set title
        holder.title.setText(resList.get(position).getQuestion());

        //Pie chart
        List<PieEntry> dataset = new ArrayList<>();
        HashMap<String, Integer> optionMap = resList.get(position).getResList();
            Set content = optionMap.keySet();
            for(Object k : content){
                String text = (String) k;
                int value = optionMap.get(text);
                dataset.add(new PieEntry(value, text));
            }

        PieDataSet pieDataSet = new PieDataSet(dataset,"");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
//        pieDataSet.setValueFormatter(new PercentFormatter(){
//            @Override
//            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
//                if(value > 0) {
//                    return super.getFormattedValue(value, entry, dataSetIndex, viewPortHandler);
//                }else{
//                    return "";
//                }
//            }
//        });
        pieDataSet.setValueTextSize(18);
        pieDataSet.setValueTextColor(Color.WHITE);
        PieData pieData = new PieData(pieDataSet);
        Description description = new Description();
        description.setText("");
        holder.piechart.setData(pieData);
        holder.piechart.animateXY(1500, 1500);
        holder.piechart.setDrawSliceText(false);
//        holder.piechart.setUsePercentValues(true);
        holder.piechart.setDescription(description);
        holder.piechart.getLegend().setWordWrapEnabled(true);
        holder.piechart.getLegend().setTextSize(14);
        holder.piechart.invalidate();

    }


    @Override
    // Counter to record the count
    public int getItemCount() {
        return resList.size();
    }




    // Define MyViewHolder
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public PieChart piechart;
        public TextView title;

        public MyViewHolder(@NonNull View ACView) {
            super(ACView);
            piechart = ACView.findViewById(R.id.any_chart_view1);
            title = ACView.findViewById(R.id.question_title);

        }
    }
}
