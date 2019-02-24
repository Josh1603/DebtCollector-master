package syd.jjj.debtcollector;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class GraphRecyclerViewAdapter extends RecyclerView.Adapter<GraphRecyclerViewAdapter.ViewHolder> {

    private List<DebtValue> dataset;
    private String period;
    private Date currentDate = new Date();
    private Date endDate;

    public GraphRecyclerViewAdapter(List<DebtValue> dataset, String period) {
        this.dataset = dataset;
        this.period = period;
    }

    public void setDataset(List<DebtValue> dataset) {
        this.dataset = dataset;
    }

    public List<DebtValue> getDataset() {
        return dataset;
    }

    public Date getEndDate() {
        return endDate;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_graph, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        Calendar cal = Calendar.getInstance();
        String displayText;
        
        // Draws the 'up-to-date' GraphView for a given period.
        if (i == 0) {
            if (dataset.size() > 0) {
                float[] data;
                switch (period) {
                    case ("WEEKLY"):
                        data = getCurrentWeeklyData();
                        viewHolder.periodGraphView.setXAxisScaleFactor(
                                getStartOfWeek(currentDate),
                                period);
                        viewHolder.periodGraphView.setYAxisScaleFactor(
                                getHighestDebtValue(data));
                        viewHolder.periodGraphView.setDataSet(data);

                        cal.setTime(getStartOfWeek(currentDate));
                        cal.setMinimalDaysInFirstWeek(7);
                        displayText = "Week " + cal.get(Calendar.WEEK_OF_YEAR) + " - " + cal.get(Calendar.YEAR);
                        viewHolder.detailsView.setTextSize(28);
                        viewHolder.detailsView.setText(displayText);
                        break;
                    case ("MONTHLY"):
                        data = getCurrentMonthlyData();
                        viewHolder.periodGraphView.setXAxisScaleFactor(
                                getStartOfMonth(currentDate),
                                period);
                        viewHolder.periodGraphView.setYAxisScaleFactor(
                                getHighestDebtValue(data));
                        viewHolder.periodGraphView.setDataSet(data);

                        cal.setTime(getStartOfMonth(currentDate));
                        String month = getMonthString(cal.get(Calendar.MONTH));
                        displayText = month + " " + cal.get(Calendar.YEAR);
                        viewHolder.detailsView.setTextSize(28);
                        viewHolder.detailsView.setText(displayText);
                        break;
                    case ("YEARLY"):
                        data = getCurrentYearlyData();
                        viewHolder.periodGraphView.setXAxisScaleFactor(
                                getStartOfYear(currentDate),
                                period);
                        viewHolder.periodGraphView.setYAxisScaleFactor(
                                getHighestDebtValue(data));
                        viewHolder.periodGraphView.setDataSet(data);

                        cal.setTime(getStartOfYear(currentDate));
                        displayText = Integer.toString(cal.get(Calendar.YEAR));
                        viewHolder.detailsView.setTextSize(28);
                        viewHolder.detailsView.setText(displayText);
                        break;

                    //TODO: Replace Yearly data with Minutely data (for testing)
                    /*
                        data = getCurrentMinutelyData();
                        viewHolder.periodGraphView.setXAxisScaleFactor(
                                getStartOfMinute(currentDate),
                                period);
                        viewHolder.periodGraphView.setYAxisScaleFactor(
                                getHighestDebtValue(data));
                        viewHolder.periodGraphView.setDataSet(data);

                        cal.setTime(getStartOfMinute(currentDate));
                        displayText = Integer.toString(cal.get(Calendar.MINUTE));
                        viewHolder.detailsView.setTextSize(28);
                        viewHolder.detailsView.setText(displayText);
                        break;
                        */
                }
            } else {
                viewHolder.periodGraphView.setXAxisScaleFactor(currentDate, "WEEKLY");
                viewHolder.periodGraphView.setYAxisScaleFactor(1);
                viewHolder.periodGraphView.setDataSet(new float[]{0,0,0,0});
                viewHolder.detailsView.setTextSize(24);
                viewHolder.detailsView.setText("Set your first value below");
            }
        }

        // Draws GraphView based on position.
        if (i > 0) {
            float[] data;
            switch (period) {
                case ("WEEKLY"):
                    data = getWeeklyDataAt(i);
                    viewHolder.periodGraphView.setXAxisScaleFactor(
                            getStartOfWeek(currentDate, i),
                            period);
                    viewHolder.periodGraphView.setYAxisScaleFactor(
                            getHighestDebtValue(data));
                    viewHolder.periodGraphView.setDataSet(data);

                    cal.setTime(getStartOfWeek(currentDate, i));
                    cal.setMinimalDaysInFirstWeek(7);
                    displayText = "Week " + cal.get(Calendar.WEEK_OF_YEAR) + " - " + cal.get(Calendar.YEAR);
                    viewHolder.detailsView.setTextSize(28);
                    viewHolder.detailsView.setText(displayText);
                    break;
                case ("MONTHLY"):
                    data = getMonthlyDataAt(i);
                    viewHolder.periodGraphView.setXAxisScaleFactor(
                            getStartOfMonth(currentDate, i),
                            period);
                    viewHolder.periodGraphView.setYAxisScaleFactor(
                            getHighestDebtValue(data));
                    viewHolder.periodGraphView.setDataSet(data);

                    cal.setTime(getStartOfMonth(currentDate, i));
                    String month = getMonthString(cal.get(Calendar.MONTH));
                    displayText = month + " " + cal.get(Calendar.YEAR);
                    viewHolder.detailsView.setTextSize(28);
                    viewHolder.detailsView.setText(displayText);
                    break;
                case ("YEARLY"):
                    data = getYearlyDataAt(i);
                    viewHolder.periodGraphView.setXAxisScaleFactor(
                            getStartOfYear(currentDate, i),
                            period);
                    viewHolder.periodGraphView.setYAxisScaleFactor(
                            getHighestDebtValue(data));
                    viewHolder.periodGraphView.setDataSet(data);

                    cal.setTime(getStartOfYear(currentDate, i));
                    displayText = Integer.toString(cal.get(Calendar.YEAR));
                    viewHolder.detailsView.setTextSize(28);
                    viewHolder.detailsView.setText(displayText);
                    break;

                //TODO: Replace Yearly data with Minutely data (for testing)
                    /*
                    data = getMinutelyDataAt(i);
                    viewHolder.periodGraphView.setXAxisScaleFactor(
                            getStartOfMinute(currentDate, i),
                            period);
                    viewHolder.periodGraphView.setYAxisScaleFactor(
                            getHighestDebtValue(data));
                    viewHolder.periodGraphView.setDataSet(data);

                    cal.setTime(getStartOfMinute(currentDate, i));
                    displayText = Integer.toString(cal.get(Calendar.MINUTE));
                    viewHolder.detailsView.setTextSize(28);
                    viewHolder.detailsView.setText(displayText);
                    break;
                    */
            }
        }
    }
    
    public float[] getCurrentWeeklyData() {
        Date startOfWeek = getStartOfWeek(currentDate);
        Date endOfWeek = getEndOfWeek(startOfWeek);
        return convertCurrentData(startOfWeek, endOfWeek);
    }

    public float[] getCurrentMonthlyData() {
        Date startOfMonth = getStartOfMonth(currentDate);
        Date endOfMonth = getEndOfMonth(startOfMonth);
        return convertCurrentData(startOfMonth, endOfMonth);
    }

    public float[] getCurrentYearlyData() {
        Date startOfYear = getStartOfYear(currentDate);
        Date endOfYear = getEndOfYear(startOfYear);
        return convertCurrentData(startOfYear, endOfYear);
    }

    public float[] getWeeklyDataAt(int position) {
        Date startOfWeek = getStartOfWeek(currentDate, position);
        Date endOfWeek = getEndOfWeek(startOfWeek);
        return convertDataBetween(startOfWeek, endOfWeek);
    }

    public float[] getMonthlyDataAt(int position) {
        Date startOfMonth = getStartOfMonth(currentDate, position);
        Date endOfMonth = getEndOfMonth(startOfMonth);
        return convertDataBetween(startOfMonth, endOfMonth);
    }

    public float[] getYearlyDataAt(int position) {
        Date startOfYear = getStartOfYear(currentDate, position);
        Date endOfYear = getEndOfYear(startOfYear);
        return convertDataBetween(startOfYear, endOfYear);
    }

    public float[] convertCurrentData(Date startDate, Date endDate) {
        this.endDate = endDate;
        List<DebtValue> rawData = new ArrayList<>();
        DebtValue lastCheckedDebtValue = dataset.get(dataset.size() -1);
        int conversionIterator = dataset.size() - 1;
        int lastCheckedPosition = conversionIterator;
        while (lastCheckedDebtValue.getMDate().getTime() > startDate.getTime()
                && conversionIterator >= 0) {
            rawData.add(lastCheckedDebtValue);
            conversionIterator--;
            if (conversionIterator >= 0) {
                lastCheckedDebtValue = dataset.get(conversionIterator);
                lastCheckedPosition = conversionIterator;
            }
        }

        DebtValue initialiserDebtValue;
        if (lastCheckedPosition > 0) {
            DebtValue lastPreviousDebtValue = dataset.get(lastCheckedPosition);
            initialiserDebtValue = new DebtValue(
                    startDate,
                    lastPreviousDebtValue.getMDollarValue(),
                    lastPreviousDebtValue.getMCentValue());
        } else {
            initialiserDebtValue = new DebtValue(
                    startDate,
                    "0",
                    "00");
        }

        float[] plotableData;
        if (rawData.size() > 0){
            rawData.add(initialiserDebtValue);
            plotableData = new float[rawData.size() * 4  - 4];
        } else {
            plotableData = new float[]{0,0,0,0};
        }

        int pos = 0;
        for (int i = (rawData.size() - 1); i > 0; i--) {
            // Each iteration stores two sets of coordinates required to draw a single line.
            // x - value position of first coordinate relative to first point in the series.
            plotableData[pos] = rawData.get(i).getRawX() - rawData.get(rawData.size() - 1).getRawX();
            // y - value position of first coordinate.
            plotableData[pos + 1] = rawData.get(i).getRawY();
            // x - value position of second coordinate relative to first point in the series.
            plotableData[pos + 2] = rawData.get(i - 1).getRawX() - rawData.get(rawData.size() - 1).getRawX();
            // y - value position of second coordinate.
            plotableData[pos + 3] = rawData.get(i - 1).getRawY();
            pos = pos + 4;
        }
        return plotableData;
    }

    public float[] convertDataBetween(Date startDate, Date endDate) {
        // Get debtvalue data between given dates
        //(Get previous value and set at x=0 (and x=l if no data))        
        // Convert data into a plottable float array based on size
        List<DebtValue> rawData = new ArrayList<>();

        int iterator = dataset.size() -1;
        while (iterator > 0 && dataset.get(iterator).getMDate().getTime() > endDate.getTime()){
            iterator--;
        }
        // Store a value to "complete" graph based on the last store value.
        DebtValue lastCheckedDebtValue = dataset.get(iterator);
        DebtValue lastDataValue = new DebtValue(
                endDate,
                lastCheckedDebtValue.getMDollarValue(),
                lastCheckedDebtValue.getMCentValue());
        rawData.add(lastDataValue);

        while (lastCheckedDebtValue.getMDate().getTime() > startDate.getTime() && iterator >= 0) {
            rawData.add(lastCheckedDebtValue);
            iterator--;
            if (iterator >= 0){
                lastCheckedDebtValue = dataset.get(iterator);
            }
        }

        if (iterator > 0) {
            DebtValue lastPreviousDebtValue = dataset.get(iterator);
            DebtValue initialiserDebtValue = new DebtValue(
                    startDate,
                    lastPreviousDebtValue.getMDollarValue(),
                    lastPreviousDebtValue.getMCentValue());
            rawData.add(initialiserDebtValue);
        } else {
            DebtValue initialiserDebtValue = new DebtValue(
                    startDate,
                    "0",
                    "00");
            rawData.add(initialiserDebtValue);
        }

        float[] plotableData = new float[rawData.size() * 4  - 4];
        int pos = 0;
        for (int i = (rawData.size() - 1); i > 0; i--) {
            // Each iteration stores two sets of coordinates required to draw a single line.
            // x - value position of first coordinate relative to first point in the series.
            plotableData[pos] = rawData.get(i).getRawX() - rawData.get(rawData.size() - 1).getRawX();
            // y - value position of first coordinate.
            plotableData[pos + 1] = rawData.get(i).getRawY();
            // x - value position of second coordinate relative to first point in the series.
            plotableData[pos + 2] = rawData.get(i - 1).getRawX() - rawData.get(rawData.size() - 1).getRawX();
            // y - value position of second coordinate.
            plotableData[pos + 3] = rawData.get(i - 1).getRawY();
            pos = pos + 4;
        }
        return plotableData;
    }

    @Override
    public int getItemCount() {

        Date firstDate;
        Date lastDate;

        if (dataset.size() > 0){

            switch (period) {
                case ("WEEKLY"):
                    firstDate = getStartOfWeek(dataset.get(0).getMDate());
                    lastDate = getStartOfWeek(currentDate);
                    return amountOfWeeklyGraphs(firstDate, lastDate);
                case ("MONTHLY"):
                    firstDate = getStartOfMonth(dataset.get(0).getMDate());
                    lastDate = getStartOfMonth(currentDate);
                    return amountOfMonthlyGraphs(firstDate, lastDate);
                case ("YEARLY"):

                    firstDate = getStartOfYear(dataset.get(0).getMDate());
                    lastDate = getStartOfYear(currentDate);
                    return amountOfYearlyGraphs(firstDate, lastDate);

                //TODO: Replace Yearly data with Minutely data (for testing)
                    /*
                    firstDate = getStartOfMinute(dataset.get(0).getMDate());
                    lastDate = getStartOfMinute(currentDate);
                    return amountOfMinutelyGraphs(firstDate, lastDate);
                    */
            }

        }
        return 1;
    }

    public int amountOfWeeklyGraphs(Date firstDate, Date lastDate) {
        if (dataset.size() < 1)
            return 1;

        int graphCount = 0;
        long limit = lastDate.getTime();
        while (limit >= firstDate.getTime()) {
            graphCount++;
            limit = getStartOfWeek(currentDate, graphCount).getTime();
        }

        return graphCount;
    }

    public int amountOfMonthlyGraphs(Date firstDate, Date lastDate) {
        if (dataset.size() < 1)
            return 1;

        int graphCount = 0;
        long limit = lastDate.getTime();
        while (limit >= firstDate.getTime()) {
            graphCount++;
            limit = getStartOfMonth(currentDate, graphCount).getTime();
        }

        return graphCount;
    }

    public int amountOfYearlyGraphs(Date firstDate, Date lastDate) {
        if (dataset.size() < 1)
            return 1;

        int graphCount = 0;
        long limit = lastDate.getTime();
        while (limit >= firstDate.getTime()) {
            graphCount++;
            limit = getStartOfYear(currentDate, graphCount).getTime();
        }

        return graphCount;
    }

    public float getHighestDebtValue(float[] dataset) {
        if (dataset != null) {
            float highestDebtValue = -1;
            for (int i = 0; i < (dataset.length / 2); i++) {
                int position = (2 * i) + 1;
                float debtValue = dataset[position];
                if (debtValue > highestDebtValue){
                    highestDebtValue = debtValue;
                }
            }
            return highestDebtValue;
        }
        return 1;
    }

    public String getMonthString(int month) {
        switch(month) {
            case 0:
                return "January";
            case 1:
                return "February";
            case 2:
                return "March";
            case 3:
                return "April";
            case 4:
                return "May";
            case 5:
                return "June";
            case 6:
                return "July";
            case 7:
                return "August";
            case 8:
                return "September";
            case 9:
                return "October";
            case 10:
                return "November";
            case 11:
                return "December";
            default:
                return "Error getting month String";
        }
    }

    public Date getStartOfWeek(Date currentDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int difference;
        switch (dayOfWeek) {
            case 1:
                difference = -6;
                break;
            default:
                difference = 2 - dayOfWeek;
                break;
        }
        calendar.add(Calendar.DATE, difference);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 1);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public Date getStartOfWeek(Date currentDate, int position) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getStartOfWeek(currentDate));
        calendar.add(Calendar.DATE, -7 * position);
        return calendar.getTime();
    }

    public Date getEndOfWeek(Date startOfWeek) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startOfWeek);
        calendar.add(Calendar.DATE, 7);
        calendar.add(Calendar.MILLISECOND, -1);
        return calendar.getTime();
    }

    public Date getStartOfMonth(Date currentDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 1);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public Date getStartOfMonth(Date currentDate, int position) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getStartOfMonth(currentDate));
        calendar.add(Calendar.MONTH, -1 * position);
        return calendar.getTime();
    }

    public Date getEndOfMonth(Date startOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startOfMonth);
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.MILLISECOND, -1);
        return calendar.getTime();
    }

    public Date getStartOfYear(Date currentDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 1);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public Date getStartOfYear(Date currentDate, int position) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getStartOfYear(currentDate));
        calendar.add(Calendar.YEAR, -1 * position);
        return calendar.getTime();
    }

    public Date getEndOfYear(Date startOfYear) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startOfYear);
        calendar.add(Calendar.YEAR, 1);
        calendar.add(Calendar.MILLISECOND, -1);
        return calendar.getTime();
    }

    public float[] getCurrentMinutelyData() {
        Date startOfMinute = getStartOfMinute(currentDate);
        Date endOfMinute = getEndOfMinute(startOfMinute);
        return convertCurrentData(startOfMinute, endOfMinute);
    }

    public Date getStartOfMinute(Date currentDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public float[] getMinutelyDataAt(int position) {
        Date startOfMinute = getStartOfMinute(currentDate, position);
        Date endOfMinute = getEndOfMinute(startOfMinute);
        return convertDataBetween(startOfMinute, endOfMinute);
    }

    public int amountOfMinutelyGraphs(Date firstDate, Date lastDate) {
        if (dataset.size() < 1)
            return 1;

        int graphCount = 0;
        long limit = lastDate.getTime();
        while (limit >= firstDate.getTime()) {
            graphCount++;
            limit = getStartOfMinute(currentDate, graphCount).getTime();
        }

        return graphCount;
    }

    public Date getStartOfMinute(Date currentDate, int position) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getStartOfMinute(currentDate));
        calendar.add(Calendar.MINUTE, -1 * position);
        return calendar.getTime();
    }

    public Date getEndOfMinute(Date startOfMinute) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startOfMinute);
        calendar.add(Calendar.MINUTE, 1);
        calendar.add(Calendar.MILLISECOND, -1);
        return calendar.getTime();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View parentView;
        public final TextView detailsView;
        public final PeriodGraphView periodGraphView;

        public ViewHolder(View view) {
            super(view);
            parentView = view;
            detailsView = (TextView) view.findViewById(R.id.list_item_graph_details);
            periodGraphView = (PeriodGraphView) view.findViewById(R.id.list_item_graph);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + detailsView.getText() + "'";
        }
    }
}
