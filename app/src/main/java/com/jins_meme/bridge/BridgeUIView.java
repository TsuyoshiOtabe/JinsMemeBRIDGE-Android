/**
 * BridgeUIView.java
 *
 * Copyright (C) 2017, Nariaki Iwatani(Anno Lab Inc.)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 **/

package com.jins_meme.bridge;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import java.util.Stack;

public class BridgeUIView extends RecyclerView {

  private CardLayoutManager mLayoutManager;
  private ScrollController mScrollController;
  private TouchListener mTouchListener;

  private static long selectedId;

  public BridgeUIView(Context context) {
    super(context);
    mLayoutManager = new CardLayoutManager(getContext());
    setLayoutManager(mLayoutManager);
    addItemDecoration(new CardDecoration());
    mScrollController = new ScrollController();
    addOnItemTouchListener(mScrollController);
    mTouchListener = new TouchListener();
  }

  @Override
  public void setAdapter(RecyclerView.Adapter adapter) {
    super.setAdapter(adapter);
    ((Adapter) adapter).setTouchEnabler(mTouchListener);
  }

  public void move(int amount) {
    int toIndex = getCurrentCenteredItemPosition() + amount;
    if (0 <= toIndex && toIndex < getAdapter().getItemCount()) {
      View target = mLayoutManager.findViewByPosition(toIndex);
      if (target != null) {
        int dx = target.getLeft() - (getWidth() - target.getWidth()) / 2;
        smoothScrollBy(dx, 0);
      } else {
        smoothScrollToPosition(toIndex);
      }
    }
  }

  public void moveToFit() {
    int pos = getCurrentCenteredItemPosition();
    View targetLeft = mLayoutManager.findViewByPosition(pos - 1);
    View targetCenter = mLayoutManager.findViewByPosition(pos);
    View targetRight = mLayoutManager.findViewByPosition(pos + 1);

    long posLeftId   = ((Adapter) getAdapter()).getItemId(pos - 1);
    long posCenterId = ((Adapter) getAdapter()).getItemId(pos);
    long posRightId  = ((Adapter) getAdapter()).getItemId(pos + 1);

    Log.d("DEBUG", "pos = " + selectedId + " " + posLeftId + " " + posCenterId + " " + posRightId);

    if (targetLeft != null && selectedId == posLeftId) {
      Log.d("DEBUG", "left");
      smoothScrollBy(targetLeft.getLeft() - 10, 0);
    } else if (targetCenter != null && selectedId == posCenterId) {
      Log.d("DEBUG", "center");
      smoothScrollBy(targetCenter.getLeft() - 10, 0);
    } else if (targetRight != null && selectedId == posRightId) {
      Log.d("DEBUG", "right");
      smoothScrollBy(targetRight.getLeft() - 10, 0);
    }

    /*
    if (lt != null) {
      Log.d("DEBUG",
          "-1 = " + getCurrentCenteredItemPosition() + " " + lt.getLeft() + " " + lt.getId());
    }
    if (target != null) {
      Log.d("DEBUG",
          " 0 = " + getCurrentCenteredItemPosition() + " " + target.getLeft() + " " + target.getId());
    }
    if (rt != null) {
      Log.d("DEBUG",
          "+1 = " + getCurrentCenteredItemPosition() + " " + rt.getLeft() + " " + rt.getId());
    }
    */
  }

  public void enter() {
    mLayoutManager.findViewByPosition(getCurrentCenteredItemPosition()).callOnClick();
  }

  public void reset() {
    ((Adapter) getAdapter()).reset();
  }

  public boolean back() {
    Adapter adapter = (Adapter) getAdapter();
    if (adapter.isAtTop()) {
      return false;
    }
    adapter.back();
    return true;
  }

  public void setToDefaultPosition(int offset) {
    Adapter adapter = (Adapter) getAdapter();
    int near = adapter.getItemCount() / 2 + offset;
    int dx = (getWidth() - mLayoutManager.getItemWidth()) / 2;
    mLayoutManager.scrollToPositionWithOffset(
        near - near % adapter.getChildCardCount(adapter.getSelectedCardId()), dx);
  }
  public void setPosition(int position) {
    int near = position;
    int dx = (getWidth() - mLayoutManager.getItemWidth()) / 2;
    mLayoutManager.scrollToPositionWithOffset(near, dx);
  }

  public int getCurrentCenteredItemPosition() {
    int last = mLayoutManager.findLastVisibleItemPosition();
    int center = getWidth() / 2;
    for (int i = mLayoutManager.findFirstVisibleItemPosition(); i <= last; ++i) {
      View view = mLayoutManager.findViewByPosition(i);
      if (view.getRight() > center) {
        return i;
      }
    }
    return last;
  }

  private View getCurrentCenteredItem() {
    int last = mLayoutManager.findLastVisibleItemPosition();
    int center = getWidth() / 2;
    for (int i = mLayoutManager.findFirstVisibleItemPosition(); i <= last; ++i) {
      View view = mLayoutManager.findViewByPosition(i);
      if (view.getRight() > center) {
        return view;
      }
    }
    return null;
  }

  public void setTouchEnabled(boolean enabled) {
    mTouchListener.setEnabled(enabled);
    mScrollController.setEnabled(enabled);
  }

  public int getCurrentParentCardId() {
    return ((Adapter)getAdapter()).getSelectedCardId();
  }

  public interface IResultListener {

    void onEnterCard(int id);
    void onEnteredCard(int id);
    void onExitCard(int id);
    void onExitedCard(int id);
    void onEndCardSelected(int id);
  }

  public static abstract class Adapter<CH extends CardHolder> extends RecyclerView.Adapter<CH> {

    static final int NO_ID = (int) RecyclerView.NO_ID;

    enum CardFunction {
      BACK, ENTER_MENU, END,
    }

    private Stack<Integer> mHistory = new Stack<>();
    private IResultListener mListener;
    private TouchListener mTouchEnabler;

    Adapter(IResultListener listener) {
      mListener = listener;
      setHasStableIds(true);
    }

    public abstract CardFunction getCardFunction(int id);

    public abstract int getCardId(int parent_id, int position);

    public abstract CH onCreateCardHolder(ViewGroup parent, int card_type);

    public abstract void onBindCardHolder(CH cardHolder, int id);

    public abstract int getChildCardCount(int parent_id);

    public int getCardType(int id) {
      return 0;
    }

    void enter(int id) {
      Log.d("DEBUG", "BridgeUIView:: enter " + id);

      selectedId = id;

      mListener.onEnterCard(id);
      mHistory.push(id);
      notifyDataSetChanged();
      mListener.onEnteredCard(id);
    }

    void reset() {
      mHistory.clear();
      notifyDataSetChanged();
    }

    void end(int id) {
      Log.d("DEBUG", "BridgeUIView:: end " + id);

      mListener.onEndCardSelected(id);
    }

    boolean isAtTop() {
      return mHistory.empty();
    }

    void back() {
      mListener.onExitCard(getSelectedCardId());
      if (!mHistory.empty()) {
        mHistory.pop();
        notifyDataSetChanged();
      }
      mListener.onExitedCard(getSelectedCardId());
    }

    @Override
    public final CH onCreateViewHolder(ViewGroup parent, int viewType) {
      return onCreateCardHolder(parent, viewType);
    }

    @Override
    public final int getItemCount() {
      return Integer.MAX_VALUE;
    }

    @Override
    public final long getItemId(int position) {
      return getCardId(getSelectedCardId(), calcCardPosition(position));
    }

    @Override
    public final int getItemViewType(int position) {
      return getCardType((int) getItemId(position));
    }

    @Override
    public final void onBindViewHolder(CH viewHolder, final int position) {
      onBindCardHolder(viewHolder, (int) getItemId(position));
      final int id = (int) viewHolder.getItemId();
      final CardFunction result = getCardFunction(id);
      viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          switch (result) {
            case BACK:
              back();
              break;
            case ENTER_MENU:
              enter(id);
              break;
            case END:
              end(id);
              break;
          }
        }
      });
      viewHolder.itemView.setOnTouchListener(mTouchEnabler);
    }

    private int calcCardPosition(int position) {
      return position % getChildCardCount(getSelectedCardId());
    }

    private int getSelectedCardId() {
      return mHistory.empty() ? NO_ID : mHistory.peek();
    }

    public void setTouchEnabler(TouchListener enabler) {
      mTouchEnabler = enabler;
    }
  }

  public static class CardHolder extends RecyclerView.ViewHolder {

    CardHolder(View itemView) {
      super(itemView);
      itemView.setLayoutParams(
          new ViewGroup.LayoutParams(-1, -1));    // LayoutManagerでLayoutParamsを再計算させるためのdirty hack
    }
  }

  private class CardDecoration extends RecyclerView.ItemDecoration {

    private View prev = null;

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, State state) {
      super.onDrawOver(c, parent, state);
      View view = ((BridgeUIView) (parent)).getCurrentCenteredItem();
      if (prev != view) {
        if (prev != null) {
          prev.setActivated(false);
        }
        if (view != null) {
          view.setActivated(true);
        }
        prev = view;
      }
    }
  }


  private class CardLayoutManager extends LinearLayoutManager {

    private final int CARD_MARGIN = 10;

    CardLayoutManager(Context context) {
      super(context, LinearLayoutManager.HORIZONTAL, false);
    }

    @Override
    public LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
      LayoutParams ret = super.generateLayoutParams(lp);
      ret.width = getItemWidth() - CARD_MARGIN * 2;
      ret.height = getItemHeight() - CARD_MARGIN * 2;
      ret.setMargins(CARD_MARGIN, CARD_MARGIN, CARD_MARGIN, CARD_MARGIN);
      return ret;
    }

    private int getItemWidth() {
      return getWidth();
    }

    private int getItemHeight() {
      return getHeight();
    }
  }

  private class TouchListener implements View.OnTouchListener {

    private boolean isEnabled = true;

    public void setEnabled(boolean enabled) {
      isEnabled = enabled;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
      return !isEnabled;
    }
  }

  private class ScrollController implements RecyclerView.OnItemTouchListener {

    private boolean isEnabled = true;

    public ScrollController() {
    }

    public void setEnabled(boolean enabled) {
      isEnabled = enabled;
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
      return !isEnabled;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }
  }

}
