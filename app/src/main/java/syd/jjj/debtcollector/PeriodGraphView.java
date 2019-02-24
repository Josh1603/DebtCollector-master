package syd.jjj.debtcollector;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import java.util.Calendar;
import java.util.Date;

import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

public class PeriodGraphView extends View {

    private Paint backgroundPaint;
    private Paint axesPaint;
    private Paint trendlinePaint;
    private float[] dataSet;
    private float[] normalisedDataSet;
    private float[] normalisedFloatDataSet;
    private float xAxisScaleFactor;
    private float yAxisScaleFactor;

    private Context context;

    private final static float WEEK_MILLIS = DateUtils.WEEK_IN_MILLIS;
    private final static float CLOCK_FORWARD_WEEK_MILLIS = WEEK_MILLIS - DateUtils.HOUR_IN_MILLIS;
    private final static float CLOCK_BACKWARD_WEEK_MILLIS = WEEK_MILLIS + DateUtils.HOUR_IN_MILLIS;

    private final static float THIRTY_ONE_DAY_MONTH_MILLIS = 31 * DateUtils.DAY_IN_MILLIS;
    private final static float THIRTY_DAY_MONTH_MILLIS = 30 * DateUtils.DAY_IN_MILLIS;
    private final static float FEB_MILLIS = 28 * DateUtils.DAY_IN_MILLIS;
    private final static float LEAP_YEAR_FEB_MILLIS = 29 * DateUtils.DAY_IN_MILLIS;
    private final static float OCT_MILLIS = 31 * DateUtils.DAY_IN_MILLIS - DateUtils.HOUR_IN_MILLIS;
    private final static float APR_MILLIS = 30 * DateUtils.DAY_IN_MILLIS + DateUtils.HOUR_IN_MILLIS;

    private final static float YEAR_MILLIS = DateUtils.YEAR_IN_MILLIS + DateUtils.DAY_IN_MILLIS;
    private final static float LEAP_YEAR_MILLIS = YEAR_MILLIS + DateUtils.DAY_IN_MILLIS;


    public PeriodGraphView(Context context) {
        this(context, null);
    }

    public PeriodGraphView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PeriodGraphView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.context = context;

        setFocusable(true);

        dataSet = null;
        normalisedDataSet = null;
        xAxisScaleFactor = 1;
        yAxisScaleFactor = 1;

        Context c = this.getContext();
        Resources r = this.getResources();

        Resources.Theme theme = c.getTheme();
        TypedValue typedValue = new TypedValue();

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        theme.resolveAttribute(R.attr.periodGraphBackgroundColor, typedValue, true);
        backgroundPaint.setColor(ContextCompat.getColor(c, typedValue.resourceId));
        backgroundPaint.setStyle(Paint.Style.FILL);
        
        axesPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        theme.resolveAttribute(R.attr.periodGraphAxesColor, typedValue, true);
        axesPaint.setColor(ContextCompat.getColor(c, typedValue.resourceId));
        axesPaint.setStrokeWidth(5);
        axesPaint.setStrokeCap(Paint.Cap.ROUND);
        axesPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        trendlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        theme.resolveAttribute(R.attr.periodGraphTrendlineColor, typedValue, true);
        trendlinePaint.setColor(ContextCompat.getColor(c, typedValue.resourceId));
        trendlinePaint.setStrokeWidth(5);
        trendlinePaint.setStrokeCap(Paint.Cap.ROUND);
        trendlinePaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    public void setDataSet(float[] dataSet) {
        if (dataSet != null) {
            this.dataSet = dataSet;
            this.normalisedDataSet = new float[dataSet.length];
            this.normalisedFloatDataSet = new float[dataSet.length];
            invalidate();
        }
    }

    public float[] getDataSet() {
        return dataSet;
    }

    public void setGraph(PeriodGraphView periodGraphView) {
        setDataSet(periodGraphView.getDataSet());
    }

    public int getYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }

    public void setXAxisScaleFactor(Date firstPossibleDateOfPeriod, String period) {
        switch (period) {
            case "WEEKLY":
                setXAxisByWeek(firstPossibleDateOfPeriod);
                break;
            case "MONTHLY":
                setXAxisByMonth(firstPossibleDateOfPeriod);
                break;
            case "YEARLY":
                setXAxisByYear(firstPossibleDateOfPeriod);
                break;

                //TODO: Replace Yearly data with Minutely data (for testing)
                /*
                xAxisScaleFactor = DateUtils.MINUTE_IN_MILLIS;
                break;
                */
        }
    }

    public void setYAxisScaleFactor(float largestDebtValue) {
        yAxisScaleFactor = largestDebtValue;
    }

    public void setXAxisByWeek(Date date){
        String monthNumber = (String) DateFormat.format("MM", date);
        String dayNumber = (String) DateFormat.format("dd", date);
        int dayNumberInt = Integer.valueOf(dayNumber);
        String day = (String) DateFormat.format("EEEE", date);
        xAxisScaleFactor = WEEK_MILLIS;
        if (monthNumber.equals("08") && dayNumber.equals("01") && day.equals("Monday")) {
            xAxisScaleFactor = CLOCK_FORWARD_WEEK_MILLIS;
        }
        if (monthNumber.equals("07") && dayNumberInt >= 26) {
            xAxisScaleFactor = CLOCK_FORWARD_WEEK_MILLIS;
        }
        if (monthNumber.equals("04") && dayNumber.equals("01") && day.equals("Monday")) {
            xAxisScaleFactor = CLOCK_BACKWARD_WEEK_MILLIS;
        }
        if (monthNumber.equals("03") && dayNumberInt >= 26) {
            xAxisScaleFactor = CLOCK_BACKWARD_WEEK_MILLIS;
        }


    }

    public void setXAxisByMonth(Date date){
        String monthNumber = (String) DateFormat.format("MM", date);
        int year = getYear(date);

        if (monthNumber.equals("01") ||
                monthNumber.equals("03") ||
                monthNumber.equals("05") ||
                monthNumber.equals("07") ||
                monthNumber.equals("08") ||
                monthNumber.equals("12")) {
            xAxisScaleFactor = THIRTY_ONE_DAY_MONTH_MILLIS;
        }
        if (monthNumber.equals("06") ||
                monthNumber.equals("09") ||
                monthNumber.equals("11") )
            xAxisScaleFactor = THIRTY_DAY_MONTH_MILLIS;
        if (monthNumber.equals("04")){
            xAxisScaleFactor = APR_MILLIS;
        }
        if (monthNumber.equals("10")){
            xAxisScaleFactor = OCT_MILLIS;
        }
        if (monthNumber.equals("02")){
            if (year % 4 == 0) {
                if(year % 100 == 0 && year % 400 != 0) {
                    xAxisScaleFactor = FEB_MILLIS;
                }
                xAxisScaleFactor = LEAP_YEAR_FEB_MILLIS;
            } else {
                xAxisScaleFactor = FEB_MILLIS;
            }
        }

    }

    public void setXAxisByYear(Date date){
        int year = getYear(date);
        if (year % 4 == 0) {
            if(year % 100 == 0 && year % 400 != 0) {
                xAxisScaleFactor = YEAR_MILLIS;
            }
            xAxisScaleFactor = LEAP_YEAR_MILLIS;
        } else {
            xAxisScaleFactor = YEAR_MILLIS;
        }
    }

    @Override
    public void onMeasure(int wMeasureSpec, int hMeasureSpec) {
        //int measuredWidth = measureWidth(wMeasureSpec);
        //int measuredHeight = measureHeight(hMeasureSpec);

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int pixelWidth;
        int pixelHeight;

        int offsetPortrait = size.x / 10;
        int offsetLandscape = size.x / 20;



        if (getActivity().getResources().getConfiguration().orientation == ORIENTATION_PORTRAIT) {
            pixelWidth = size.x - offsetPortrait;
            pixelHeight = (5 * size.y) / 12;
        } else {
            pixelWidth = size.x - offsetLandscape;
            pixelHeight = (18 * size.y) / 24;
        }


        final int desiredWSpec = MeasureSpec.makeMeasureSpec(pixelWidth, MeasureSpec.AT_MOST);
        final int desiredHSpec = MeasureSpec.makeMeasureSpec(pixelHeight, MeasureSpec.AT_MOST);

        int measuredWidth = measureWidth(desiredWSpec);
        int measuredHeight = measureHeight(desiredHSpec);

        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    private int measureWidth(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        int result = 500;

        if (specMode == MeasureSpec.AT_MOST) {
            result = specSize;
        } else if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        }

        return  result;
    }

    private int measureHeight(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        int result = 500;

        if (specMode == MeasureSpec.AT_MOST) {
            result = specSize;
        } else if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        }

        return  result;
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        if (dataSet.length > 0) {
            int mMeasuredWidth = getMeasuredWidth();
            int mMeasuredHeight = getMeasuredHeight();

            // Draws the Background.
            RectF rect = new RectF(0, 0, mMeasuredWidth, mMeasuredHeight);
            canvas.drawRoundRect(rect, 50, 50, backgroundPaint);

            // Sets scale factors for graph relative to the view.
            int widthRelativePositioner = (mMeasuredWidth / 12);
            int heightRelativePositioner = (mMeasuredHeight / 12);
            int xAxisLength = mMeasuredWidth - (2 * widthRelativePositioner);
            int yAxisLength = mMeasuredHeight - (2 * heightRelativePositioner);


            // Generates a normalised dataSet.
            for (int i = 0; i < dataSet.length; i++) {
                // X-coordinates representing time.
                if (i % 2 == 0) {
                    normalisedDataSet[i] = 
                            ((dataSet[i] - dataSet[0]) / xAxisScaleFactor) * xAxisLength
                                    + widthRelativePositioner;
                }
                // Y-coordinates representing debt value.
                if (i % 2 != 0) {
                    normalisedDataSet[i] = 
                            ((1 - (dataSet[i] / yAxisScaleFactor)) * yAxisLength)
                                    + heightRelativePositioner;
                }
            }

            canvas.drawLines(normalisedDataSet, trendlinePaint);

            // Draws the y-axis.
            canvas.drawLine(widthRelativePositioner, heightRelativePositioner,
                    widthRelativePositioner, mMeasuredHeight - heightRelativePositioner,
                    axesPaint);

            // Draws the x-axis.
            canvas.drawLine(widthRelativePositioner, mMeasuredHeight - heightRelativePositioner,
                    mMeasuredWidth - widthRelativePositioner, mMeasuredHeight - heightRelativePositioner,
                    axesPaint);

            canvas.save();
        }
    }

    private Activity getActivity() {
        Context context = getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity)context;
            }
            context = ((ContextWrapper)context).getBaseContext();
        }
        return null;
    }
}
