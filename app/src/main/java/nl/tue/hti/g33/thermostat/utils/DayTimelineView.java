package nl.tue.hti.g33.thermostat.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import nl.tue.hti.g33.thermostat.R;

/**
 * A small widget representing a timeline for one day.
 * Consists of a timeline (0-24 h) with areas coloured in two different colours,
 * one for day periods and one for night periods.
 */
public class DayTimelineView extends View implements ThermostatListener {

    private static final String LOG_TAG = "utils.DayTimelineView";

    private int mColorText = Color.BLACK;
    private int mColorGrid = Color.BLACK;
    private int mColorDay = Color.RED;
    private int mColorNight = Color.BLUE;

    private TextPaint mTextPaint;
    private Paint mDrawingPaint;

    private Iterable<Period> mDayPeriods;
    private DAY mDayOfTheWeek;
    private Thermostat mThermostat;

    private int mPaddingLeft = getPaddingLeft();
    private int mPaddingTop = getPaddingTop();
    private int mPaddingRight = getPaddingRight();
    private int mPaddingBottom = getPaddingBottom();

    public DayTimelineView(Context context) {

        super(context);
        init(context, null, 0);
    }

    public DayTimelineView(Context context, AttributeSet attrs) {

        super(context, attrs);
        init(context, attrs, 0);
    }

    public DayTimelineView(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    /**
     * Initialise the view.
     * @param attrs Attributes (from xml).
     * @param defStyle Styling (from style file).
     */
    private void init(Context context, AttributeSet attrs, int defStyle) {

        Log.v(LOG_TAG, "INIT STARTED");
        try {
            mThermostat = ((ThermostatProvider) context).provideThermostat();
        } catch (ClassCastException e) {
            Log.e(LOG_TAG, "Context must implement ThermostatProvider interface!");
            throw new IllegalArgumentException(LOG_TAG + "Initialisation failed due to" +
                    "context not implementing ThermostatProvider.");
        }

        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.DayTimelineView, defStyle, 0);
        mDayOfTheWeek = DAY.getByShortName(a.getString(R.styleable.DayTimelineView_dayOfTheWeek));
        mColorText = a.getColor(R.styleable.DayTimelineView_colorText, mColorText);
        mColorGrid = a.getColor(R.styleable.DayTimelineView_colorGrid, mColorGrid);
        mColorDay = a.getColor(R.styleable.DayTimelineView_colorDay, mColorDay);
        mColorNight = a.getColor(R.styleable.DayTimelineView_colorNight, mColorNight);
        a.recycle();

        mThermostat.addListener(this);
        mDayPeriods = mThermostat.getDaySchedule(mDayOfTheWeek);

        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setColor(mColorText);
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        // Set up the paint for the drawing;
        mDrawingPaint = new Paint();

        Log.v(LOG_TAG, "Finished init");
        setClickable(true);
        postInvalidate();
    }

    /**
     * Measure the view and its content to determine the measured width and the
     * measured height. This method is invoked by {@link #measure(int, int)} and
     * should be overridden by subclasses to provide accurate and efficient
     * measurement of their contents.
     * @param widthMeasureSpec  horizontal space requirements as imposed by the parent.
     *                          The requirements are encoded with
     *                          {@link MeasureSpec}.
     * @param heightMeasureSpec vertical space requirements as imposed by the parent.
     *                          The requirements are encoded with
     *                          {@link MeasureSpec}.
     * @see #getMeasuredWidth()
     * @see #getMeasuredHeight()
     * @see #setMeasuredDimension(int, int)
     * @see #getSuggestedMinimumHeight()
     * @see #getSuggestedMinimumWidth()
     * @see MeasureSpec#getMode(int)
     * @see MeasureSpec#getSize(int)
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        // Retrieve data
        int hSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int hSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        int wSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int wSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int height, width;

        // Set width
        if (wSpecMode == MeasureSpec.EXACTLY) {
            int wSuggested = getSuggestedMinimumWidth();
            width = (wSpecSize < wSuggested ? wSuggested : wSpecSize);
        }
        else if (wSpecMode == MeasureSpec.AT_MOST) {
            width = wSpecSize;
        }
        else if (wSpecMode == MeasureSpec.UNSPECIFIED) {
            width = getSuggestedMinimumWidth();
        }
        else {
            width = getSuggestedMinimumWidth();
        }

        // Set height
        if (hSpecMode == MeasureSpec.EXACTLY) {
            int hSuggested = getSuggestedMinimumHeight();
            height = (hSpecSize < hSuggested ? hSuggested : hSpecSize);
        }
        else if (hSpecMode == MeasureSpec.AT_MOST || hSpecMode == MeasureSpec.UNSPECIFIED) {
            height = computeHeight(width);
            height = (height < getSuggestedMinimumHeight() ? getSuggestedMinimumHeight() : height);
        }
        else {
            height = getSuggestedMinimumHeight();
        }

        // This has to be here
        setMeasuredDimension(width, height);
    }

    /**
     * Computes the height of the widget as deduced from width.
     * @param width Width of the widget.
     * @return Desirable height of the view.
     */
    private int computeHeight(int width) {

        final double ratio = 0.2;
        return (int) (width * ratio);
    }

    /**
     * Draws the view.
     * @param canvas the canvas on which the background will be drawn
     */
    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        int contentWidth = getWidth() - mPaddingLeft - mPaddingRight;
        int contentHeight = getHeight() - mPaddingTop - mPaddingBottom;
        int lineHeight = contentHeight - Math.round(mTextPaint.getFontSpacing());

        drawIntervals(lineHeight, contentWidth, canvas);

        drawGrid(lineHeight + 5, contentWidth, canvas);

        drawSubscript(contentHeight, contentWidth, canvas);
    }

    /**
     * Draws the night / day intervals.
     * @param height Height of the drawing area.
     * @param width Width of the drawing area.
     * @param canvas canvas to draw on.
     */
    private void drawIntervals(int height, int width, Canvas canvas) {

        Rect area = new Rect(0, 0, width, height);

        // Draw the border
        mDrawingPaint.setColor(mColorGrid);
        mDrawingPaint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(area, mDrawingPaint);

        // Everything is night by default
        mDrawingPaint.setColor(mColorNight);
        mDrawingPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(area, mDrawingPaint);

        // Draw day periods
        mDrawingPaint.setColor(mColorDay);
        mDrawingPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        for (Period p : mDayPeriods) {
            int left = (int) (width * p.getStartingTime() / (24 * 60.0));
            int right = (int) (width * p.getEndTime() / (24 * 60.0));
            canvas.drawRect(left, 0, right, height, mDrawingPaint);
        }
    }

    /**
     * Draws vertical lines corresponding to every hour.
     * @param height Height of the lines.
     * @param width Width of the lines.
     * @param canvas Canvas to draw on.
     */
    private void drawGrid(int height, int width, Canvas canvas) {

        mDrawingPaint.setColor(mColorGrid);
        mDrawingPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        for (int i = 0; i <= 24; i++) {
            int x = (int) (i * width / 24.0);
            canvas.drawLine(x, 0, x, height, mDrawingPaint);
        }
    }

    /**
     * Draws the subscript representing the current hour.
     * @param height Height of the drawing area.
     * @param width Width of the drawing area.
     * @param canvas Canvas to draw on.
     */
    private void drawSubscript(int height, int width, Canvas canvas) {

        for (int i = 1; i <= 23; i += 2) {
            canvas.drawText(Integer.valueOf(i).toString(),
                    (int) (i * width / 24.0), height, mTextPaint);
        }
    }

    @Override
    public void onThermostatUpdate(Thermostat thermostat) {

        mDayPeriods = thermostat.getDaySchedule(mDayOfTheWeek);
        postInvalidate();
    }
}
