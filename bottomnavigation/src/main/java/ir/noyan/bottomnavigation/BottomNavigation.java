package ir.noyan.bottomnavigation;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;

import ir.noyan.bottomnavigation.events.OnSelectedItemChangeListener;
import ir.noyan.bottomnavigation.events.OnTabItemClickListener;
import ir.noyan.bottomnavigation.events.Status;
import ir.noyan.bottomnavigation.utils.AnimationHelper;
import ir.noyan.bottomnavigation.utils.Util;

import java.util.ArrayList;
import java.util.List;

import ir.noyan.bottomnavigation.R;

/**
 * @author S.Shahini
 * @since 11/13/1
 * Bottom navigation inspired by google material design guideline.
 * <p>
 * Copyright 2016 Saeed shahini
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class BottomNavigation extends LinearLayout implements OnTabItemClickListener {

    /**
     * this variable used to hold value of bottom navigation type
     *
     * @see BottomNavigation#TYPE_FIXED
     * @see BottomNavigation#TYPE_DYNAMIC
     */
    private int type;

    /**
     * If bottom navigation has only three items, then this constant used for {@link BottomNavigation#type}
     * in this type, tab labels will always show
     */
    public static final int TYPE_FIXED = 1;

    /**
     * If Bottom navigation tab items count be between 3 to 5, then this constant used for {@link BottomNavigation#type}
     */
    public static final int TYPE_DYNAMIC = 0;

    /**
     * bottom navigation in phone mode, is horizontal and will aligned bottom of activity
     *
     * @see BottomNavigation#mode
     */
    public static final int MODE_PHONE = 0;

    /**
     * bottom navigation in tablet mode, is vertical and will aligned left or right(based on local)
     *
     * @see BottomNavigation#mode
     */
    public static final int MODE_TABLET = 1;

    /**
     * describe default tab item that must be selected by default, it will be selected when method
     * {@link BottomNavigation#setOnSelectedItemChangeListener(OnSelectedItemChangeListener)} called.
     *
     * @see OnSelectedItemChangeListener
     */
    private int defaultItem = 0;


    private int preDefaultItem = 0;

    /**
     * this variable hold position of selected tab item.
     */
    private int selectedItemPosition = defaultItem;

    /**
     * List of bottom navigation tab items
     */
    List<TabItem> tabItems = new ArrayList<>();

    /**
     * typeface used for tab item labels
     */
    private Typeface typeface;

    /**
     * used for specify how bottom navigation must show to user.
     * <p/>
     * bottom navigation currently has two modes:
     * 1- {@link BottomNavigation#MODE_PHONE}
     * 2- {@link BottomNavigation#MODE_TABLET}
     */
    private int mode = MODE_PHONE;

    /**
     * @see OnSelectedItemChangeListener
     */
    private OnSelectedItemChangeListener onSelectedItemChangeListener;

    public BottomNavigation(Context context) {
        super(context);
        if (!isInEditMode()) {
            setup(null);
        }
    }

    public BottomNavigation(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            setup(attrs);
        }
    }

    public BottomNavigation(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            setup(attrs);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BottomNavigation(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        if (!isInEditMode()) {
            setup(attrs);
        }
    }

    /**
     * this method setup necessary attributes and behavior of bottom navigation
     *
     * @param attributeSet used for setup xml custom attributes
     */
    private void setup(AttributeSet attributeSet) {


        setBackgroundColor(Color.TRANSPARENT);
        parseAttributes(attributeSet);
        switch (mode) {
            case MODE_TABLET:
                setOrientation(VERTICAL);
                setGravity(Gravity.CENTER_HORIZONTAL);
                break;
            default:
            case MODE_PHONE:
                setOrientation(HORIZONTAL);
                setGravity(Gravity.LEFT);
                break;
        }


//        onSelectedItemChanged();
        setMinimumHeight(Util.dpToPx(81));
        this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, Util.dpToPx(81)));
        Log.e("where", "set up finished");
    }

    /**
     * we call {@link #setupChildren()} in this method, because bottom navigation children are drew in this
     * state and aren't null
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setupChildren();

        Log.e("where", "inflate finished");
    }

    /**
     * this function setup {@link TabItem}s, also specify {@link BottomNavigation#type}
     */
    private void setupChildren() {
        if (getChildCount() > 0) {
            if (getChildCount() >= 3 && getChildCount() <= 5) {


                type = TYPE_FIXED;


                for (int i = 0; i < getChildCount(); i++) {
                    if (!(getChildAt(i) instanceof TabItem)) {
                        throw new RuntimeException("Bottom navigation only accept tab item as child.");
                    } else {
                        final TabItem tabItem = (TabItem) getChildAt(i);
                        tabItem.setPosition(i);
                        if (i == selectedItemPosition)
                            tabItem.setSelected(Status.SELECTED);
                        else
                            tabItem.setSelected(Status.UNSELECTED);
                        tabItems.add(tabItem);
                        tabItem.setOnTabItemClickListener(this);

                    }
                }
            } else {
                throw new RuntimeException("Bottom navigation child count must between 3 to 5 only.");
            }
        } else {
            throw new RuntimeException("Bottom navigation can't be empty!");
        }
    }

    /**
     * this function used to manage which tab item must selected or which item must deselect
     */
    public void onSelectedItemChanged() {
        Log.e("where ", "onselectitem changed pos -> " + selectedItemPosition);
        for (int i = 0; i < tabItems.size(); i++) {
            if (tabItems.get(i).getPosition() == selectedItemPosition) {
                tabItems.get(i).setSelected(Status.SELECTED);
            } else {
                tabItems.get(i).setSelected(Status.UNSELECTED);
            }
        }
    }

    /**
     * @see OnTabItemClickListener
     */
    @Override
    public void onTabItemClick(final int position) {
        if (position != selectedItemPosition) {
            selectedItemPosition = position;
            onSelectedItemChanged();
            if (onSelectedItemChangeListener != null) {
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onSelectedItemChangeListener.onSelectedItemChanged(tabItems.get(position).getId());
                    }
                }, AnimationHelper.ANIMATION_DURATION);
            }
        }
    }

    /**
     * this function get xml custom attributes and parse it to instance variables
     *
     * @param attributeSet used for retrieve custom values
     */
    private void parseAttributes(AttributeSet attributeSet) {
        if (attributeSet != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.MyBottomNavigation);
            try {
                mode = (MODE_PHONE);
            } finally {
                typedArray.recycle();
            }
        }
    }

    //Getter and Setters
    public int getType() {
        return type;
    }

    public void setDefaultItem(int position) {
        this.defaultItem = position;
        this.selectedItemPosition = position;
    }

    public int getDefaultItem() {
        return defaultItem;
    }

    public Typeface getTypeface() {
        return typeface;
    }

    public void setTypeface(final Typeface typeface) {
        this.typeface = typeface;
        for (int i = 0; i < tabItems.size(); i++) {
            final TabItem tabItem = tabItems.get(i);
            tabItem.post(new Runnable() {
                @Override
                public void run() {
                    tabItem.setTypeface(typeface);
                }
            });
        }
    }

    public void setOnSelectedItemChangeListener(OnSelectedItemChangeListener onSelectedItemChangeListener) {
        this.onSelectedItemChangeListener = onSelectedItemChangeListener;
        onSelectedItemChangeListener.onSelectedItemChanged(tabItems.get(defaultItem).getId());
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getSelectedItem() {
        return selectedItemPosition;
    }

    public void setSelectedItem(int position) {
        onTabItemClick(position);
    }

}
