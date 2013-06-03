/*
 * Copyright (C) 2010 Johan Nilsson <http://markupartist.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.bajawa.lib.widget;

import java.util.LinkedList;
import net.bajawa.lib.R;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ActionBar extends RelativeLayout implements OnClickListener {

    private LayoutInflater mInflater;
    private RelativeLayout mBarView;
    // private View mLogoView;
    // private View mHomeView;
    private TextView mTitleView;
    private LinearLayout mActionsView;
    private ImageButton mHomeBtn;
    private RelativeLayout mHomeLayout;
    private String titleText;

    public ActionBar(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.actionBarStyle);
    }

    public ActionBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mBarView = (RelativeLayout) mInflater.inflate(R.layout.actionbar, null);
        addView(mBarView);

        // TODO: Add logo

        mHomeLayout = (RelativeLayout) mBarView.findViewById(R.id.actionbar_home_bg);
        mHomeBtn = (ImageButton) mBarView.findViewById(R.id.actionbar_home_btn);

        mTitleView = (TextView) mBarView.findViewById(R.id.actionbar_title);
        mActionsView = (LinearLayout) mBarView.findViewById(R.id.actionbar_actions);

        // Attributes
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ActionBar, defStyle, 0);
        titleText = a.getString(R.styleable.ActionBar_title);
        if (titleText == null) {
            throw new IllegalArgumentException("Title must be set");
        }

        mTitleView.setText(titleText);
        a.recycle();
    }

    public void setHomeAction(IntentAction action) {
        mHomeBtn.setOnClickListener(this);
        mHomeBtn.setTag(action);
        mHomeBtn.setImageResource(action.getDrawable());
        mHomeLayout.setVisibility(View.VISIBLE);
    }

    /*
     * public void showLogo() { mLogoView.setVisibility(View.VISIBLE);
     * mHomeView.setVisibility(View.GONE); }
     */

    public void setTitle(CharSequence title) {
        mTitleView.setText(title);
    }

    public void setTitle(int resid) {
        mTitleView.setText(resid);
    }

    public void onClick(View view) {
        final Object tag = view.getTag();
        if (tag instanceof Action) {
            final Action action = (Action) tag;
            action.performAction();
        }
    }

    /**
     * Adds a list of {@link Action}s.
     * 
     * @param actionList
     */
    public void addActions(ActionList actionList) {
        int actions = actionList.size();
        for (int i = 0; i < actions; i++) {
            addAction(actionList.get(i));
        }
    }

    /**
     * Adds a new {@link Action}.
     * 
     * @param action
     *            the action to add
     */
    public void addAction(Action action) {
        final int index = mActionsView.getChildCount();
        addAction(action, index);
    }

    /**
     * Adds a new {@link Action} at the specified index.
     * 
     * @param action
     *            the action to add
     */
    public void addAction(Action action, int index) {
        mActionsView.addView(inflateAction(action), index);
    }

    /**
     * Inflates a {@link View} with the given {@link Action}.
     * 
     * @param action
     *            the action to inflate
     * @return a view
     */
    private View inflateAction(Action action) {
        View view = mInflater.inflate(R.layout.actionbar_item, mActionsView, false);

        ImageButton labelView = (ImageButton) view.findViewById(R.id.actionbar_item);
        labelView.setImageResource(action.getDrawable());

        view.setTag(action);
        view.setOnClickListener(this);
        return view;
    }

    /**
     * A {@link LinkedList} that holds a list of {@link Action}s.
     */
    public static class ActionList extends LinkedList<Action> {
    }

    /**
     * Definition of an action that could be performed, along with a icon to
     * show.
     */
    public interface Action {
        public int getDrawable();

        public void performAction();
    }

    public static abstract class AbstractAction implements Action {
        final private int mDrawable;

        public AbstractAction(int drawable) {
            mDrawable = drawable;
        }

        public int getDrawable() {
            return mDrawable;
        }
    }

    public static class IntentAction extends AbstractAction {

        private Context mContext;
        private Intent mIntent;

        public IntentAction(Context context, Intent intent, int drawable) {
            super(drawable);
            mContext = context;
            mIntent = intent;
        }

        public void performAction() {
            try {
                mContext.startActivity(mIntent);
                
            } catch (ActivityNotFoundException e) {
                Toast.makeText(mContext, mContext.getText(R.string.actionbar_activity_not_found), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*
     * public static abstract class SearchAction extends AbstractAction { public
     * SearchAction() { super(R.drawable.actionbar_search); } }
     */
}
