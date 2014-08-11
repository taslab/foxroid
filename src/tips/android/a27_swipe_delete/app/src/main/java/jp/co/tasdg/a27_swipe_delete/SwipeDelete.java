package jp.co.tasdg.a27_swipe_delete;

/**
 * Created by Coty Saxman on 2014/08/05.
 * TAS Design Group
 * Foxroid Tips #27
 * Swipe-to-Delete
 */
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Controls the deletion of objects on completion of a swipe to the right
 */

public class SwipeDelete extends Activity {
    ////Constants////
    /**percentage of screen traversed before erasing*/
    public static final int ERASE_BOUNDARY = 30;
    /**percentage of screen traversed before fading*/
    public static final int FADE_BOUNDARY = 5;
    /**fade-out factor*/
    public static final float FADE_SPEED = 1.5f;
    /**enables fade effect*/
    public static final boolean ENABLE_OPACITY = true;
    /**enables red delete warning*/
    public static final boolean ENABLE_RED_SHADE = true;
    /**erasable color*/
    public static final int ERASE_COLOR = Color.rgb(255, 50, 50);
    /**normal color*/
    public static final int NORMAL_COLOR = Color.rgb(50, 50, 255);

    ////List Element Variables////
    LayoutInflater mInflater;   //Generates item from XML
    LinearLayout theList;       //The layout serving as a list container
    int count = 1;              //number of current list item

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.swipe_delete);
        theList = (LinearLayout) this.findViewById(R.id.theList);
        mInflater = getLayoutInflater();
    }


    /**
     * Adds an element to the list-like layout element,
     * adds event listeners to the list item element,
     * adds erase and color-fade functionality to the event listeners.
     * @param vNULL The button calling populate, which is not used, hence the NULL in its name
     */
    public void populate(View vNULL) {
        TextView v = (TextView) mInflater.inflate(R.layout.list_element, theList, false);
        v.setText(String.valueOf(count));
        v.setBackgroundColor(NORMAL_COLOR);

        theList.addView(v);

        v.setOnTouchListener(new OnTouchListener() {
             ////Touch related variables////
             int initX;     //Initial x-coordinate of touch
             int initLeft;  //Initial left of target element

             Display display = getWindowManager().getDefaultDisplay();  //The screen
             Point size;                                                //Screen size

            /**
             * Handles touch events on list items
             * @param v The list element being touched
             * @param event The touch event
             * @return Always returns true to dispose of the touch event
             */
             @Override
             public boolean onTouch(View v, MotionEvent event) {

                 ////First-time initialization of screen size////
                 if(size == null) {
                     size = new Point();    //Initialize the Point object
                     display.getSize(size); //Initialize screen size variable
                 }

                 int eventAction = event.getAction();

                 switch (eventAction) {
                     ////Touch event start////
                     case MotionEvent.ACTION_DOWN:
                         initX = (int)event.getRawX();  //Store initial touch X
                         initLeft = v.getLeft();        //Store initial left
                         break;
                     ////Drag event////
                     case MotionEvent.ACTION_MOVE:
                         v.setLeft((int) (event.getRawX() - initX));    //Move element
                         //Apply opacity filter
                         if(ENABLE_OPACITY) {
                             int fadePoint = size.x * FADE_BOUNDARY / 100;
                             int fadeRange = size.x - fadePoint;
                             int fadeOverlap = v.getLeft() - fadePoint;
                             float newFade;
                             if(v.getLeft() <= fadePoint) newFade = 1.0f;
                             else newFade = 1.0f - (FADE_SPEED *
                                     fadeOverlap / fadeRange);
                             v.setAlpha(newFade);
                         }
                         //Shade red when deletable
                         if(ENABLE_RED_SHADE) {
                             if(canErase(v)) {
                                 v.setBackgroundColor(ERASE_COLOR);
                             }
                             else v.setBackgroundColor(NORMAL_COLOR);
                         }
                         break;
                     ////Touch event end////
                     case MotionEvent.ACTION_UP:
                         if(ENABLE_RED_SHADE) v.setBackgroundColor(NORMAL_COLOR);
                         if(canErase(v)) {
                             ((ViewGroup) v.getParent()).removeView(v); //Delete
                         }
                         else {
                             v.setLeft(initLeft);
                             if(ENABLE_OPACITY) v.setAlpha(1.0f);
                         }
                         resetVariables();
                         break;
                 }
                 return true;
             }

             ////Check if the element is in erasable position////
             private boolean canErase(View v) {
                 int erasePoint = size.x * ERASE_BOUNDARY / 100;
                 int currentX = v.getLeft();
                 return (currentX > erasePoint);
             }

             ////Reset event-specific variables////
             private void resetVariables() {
                 initX = 0;
                 initLeft = 0;
             }
        });

        count++;
    }
}
