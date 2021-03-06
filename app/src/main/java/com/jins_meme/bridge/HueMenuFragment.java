/**
 * MenuFragment.java
 *
 * Copyright (C) 2017, Nariaki Iwatani(Anno Lab Inc.) and Shunichi Yamamoto(Yamamoto Works Ltd.)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 **/

package com.jins_meme.bridge;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.jins_meme.bridge.BridgeUIView.Adapter;
import com.jins_meme.bridge.BridgeUIView.CardHolder;
import com.jins_meme.bridge.BridgeUIView.IResultListener;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HueMenuFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class HueMenuFragment extends MenuFragmentBase {

  private OnFragmentInteractionListener mListener;
  private Handler mHandler = new Handler();
  private HueController mHue;

  public HueMenuFragment() {
    // Required empty public constructor
  }

  @Override
  protected Adapter createAdapter() {
    return new CardAdapter(getContext(), this);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    Log.d("DEBUG", "HUE:: onViewCreated");

    super.onViewCreated(view, savedInstanceState);

    ((MainActivity) getActivity()).changeMainBackgroud(R.color.no4);
    ((MainActivity) getActivity()).updateActionBar(getString(R.string.actionbar_title), false);
    ((MainActivity) getActivity()).changeSettingButton(false);

    mHue = new HueController(getContext(), getFragmentManager());
  }

  public void destroy() {
    Log.d("DEBUG", "HUE:: destroy()");

    if (mHue != null) {
      //mHue.turnOff();
      mHue = null;
    }

    Log.d("FRAGMENT", "onDestroy...");
  }

  @Override
  protected SharedPreferences getPreferences() {
    return getContext().getSharedPreferences("hue_menu", Context.MODE_PRIVATE);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();

    super.onDestroy();
    this.destroy();
  }
  @Override
  public void onDestroy() {
    super.onDestroy();
    this.destroy();
  }

  @Override
  public void onAttach(Context context) {
    Log.d("DEBUG", "HUE:: onAttach");

    super.onAttach(context);
    if (context instanceof OnFragmentInteractionListener) {
      mListener = (OnFragmentInteractionListener) context;
    } else {
      throw new RuntimeException(context.toString()
          + " must implement OnFragmentInteractionListener");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mListener = null;
  }

  public interface OnFragmentInteractionListener {
    void backToPreviousMenu();
  }

  @Override
  public void onEnterCard(int id) {
    super.onEnterCard(id);
    Log.d("DEBUG", "HUE:: onEnterCard");
  }

  @Override
  public void onExitCard(int id) {
    super.onExitCard(id);
    Log.d("DEBUG", "HUE:: onExitCard");
  }

  @Override
  public void onEndCardSelected(int id) {
    super.onEndCardSelected(id);
    final CardAdapter.MyCardHolder mych = (CardAdapter.MyCardHolder) mView.findViewHolderForItemId(id);

    if (mHue != null) {
      switch (id) {
        case R.string.random:
          Random rand = new Random();
          if (mHue.isOn(0)) {
            mHue.changeColor(0, 0, 0, 0, 10, 0);
          } else {
            mHue.changeColor(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256),
                rand.nextInt(255), 10, 0);
          }
          break;
        case R.string.light1:
          if (mHue.isOn(1)) {
            mHue.changeColor(0, 0, 0, 0,
                ((MainActivity) getActivity()).getSavedValue("HUE_L1_TTIME", 10), 1);
          } else {
            mHue.changeColor(((MainActivity) getActivity()).getSavedValue("HUE_L1_R", 255),
                ((MainActivity) getActivity()).getSavedValue("HUE_L1_G", 0),
                ((MainActivity) getActivity()).getSavedValue("HUE_L1_B", 0),
                ((MainActivity) getActivity()).getSavedValue("HUE_L1_BRI", 127),
                ((MainActivity) getActivity()).getSavedValue("HUE_L1_TTIME", 10), 1);
          }
          break;
        case R.string.light2:
          if (mHue.isOn(2)) {
            mHue.changeColor(0, 0, 0, 0,
                ((MainActivity) getActivity()).getSavedValue("HUE_L2_TTIME", 10), 2);
          } else {
            mHue.changeColor(((MainActivity) getActivity()).getSavedValue("HUE_L2_R", 0),
                ((MainActivity) getActivity()).getSavedValue("HUE_L2_G", 255),
                ((MainActivity) getActivity()).getSavedValue("HUE_L2_B", 0),
                ((MainActivity) getActivity()).getSavedValue("HUE_L2_BRI", 127),
                ((MainActivity) getActivity()).getSavedValue("HUE_L2_TTIME", 10), 2);
          }
          break;
        case R.string.light3:
          if (mHue.isOn(3)) {
            mHue.changeColor(0, 0, 0, 0,
                ((MainActivity) getActivity()).getSavedValue("HUE_L3_TTIME", 10), 3);
          } else {
            mHue.changeColor(((MainActivity) getActivity()).getSavedValue("HUE_L3_R", 0),
                ((MainActivity) getActivity()).getSavedValue("HUE_L3_G", 0),
                ((MainActivity) getActivity()).getSavedValue("HUE_L3_B", 255),
                ((MainActivity) getActivity()).getSavedValue("HUE_L3_BRI", 127),
                ((MainActivity) getActivity()).getSavedValue("HUE_L3_TTIME", 10), 3);
          }
          break;
        case R.string.light4:
          if (mHue.isOn(4)) {
            mHue.changeColor(0, 0, 0, 0,
                ((MainActivity) getActivity()).getSavedValue("HUE_L4_TTIME", 10), 4);
          } else {
            mHue.changeColor(((MainActivity) getActivity()).getSavedValue("HUE_L4_R", 255),
                ((MainActivity) getActivity()).getSavedValue("HUE_L4_G", 255),
                ((MainActivity) getActivity()).getSavedValue("HUE_L4_B", 255),
                ((MainActivity) getActivity()).getSavedValue("HUE_L4_BRI", 127),
                ((MainActivity) getActivity()).getSavedValue("HUE_L4_TTIME", 10), 4);
          }
          break;
      }
    }
    mych.setText(getString(R.string.selected), 300);
  }

  private class CardAdapter extends BridgeUIView.Adapter<BridgeUIView.CardHolder> {

    Context mContext;
    LayoutInflater mInflater;

    CardAdapter(Context context, IResultListener listener) {
      super(listener);
      mContext = context;
      mInflater = LayoutInflater.from(context);
    }

    @Override
    public CardHolder onCreateCardHolder(ViewGroup parent, int card_type) {
      return new CardAdapter.MyCardHolder(mInflater.inflate(R.layout.card_default, parent, false));
    }

    @Override
    public void onBindCardHolder(CardHolder cardHolder, int id) {
      if (((MainActivity) getActivity()).getSavedValue("ENABLE_DARK", true)) {
        ((MyCardHolder) cardHolder).mCardView.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.no5));
      } else {
        ((MyCardHolder) cardHolder).mCardView.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.no4));
      }

      switch (id) {
        case R.string.random:
          ((MyCardHolder) cardHolder).mImageView.setImageResource(R.drawable.card_hue_light_random);
          break;
        case R.string.light1:
          ((MyCardHolder) cardHolder).mImageView.setImageResource(R.drawable.card_hue_light1);
          break;
        case R.string.light2:
          ((MyCardHolder) cardHolder).mImageView.setImageResource(R.drawable.card_hue_light2);
          break;
        case R.string.light3:
          ((MyCardHolder) cardHolder).mImageView.setImageResource(R.drawable.card_hue_light3);
          break;
        case R.string.light4:
          ((MyCardHolder) cardHolder).mImageView.setImageResource(R.drawable.card_hue_light4);
          break;
        default:
          ((MyCardHolder) cardHolder).mImageView.setImageResource(R.drawable.card_default);
          break;
      }

      ((MyCardHolder) cardHolder).mTitle.setText(getResources().getString(id));
      ((MyCardHolder) cardHolder).mTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.hue));
      ((MyCardHolder) cardHolder).mSubtitle.setText("");
      ((MyCardHolder) cardHolder).mSubtitle.setTextColor(ContextCompat.getColor(getContext(), R.color.hue));
      ((MyCardHolder) cardHolder).mValue.setTextColor(ContextCompat.getColor(getContext(), R.color.hue));
    }

    @Override
    public CardFunction getCardFunction(int id) {
      switch (id) {
        case R.string.back:
          return CardFunction.BACK;
        default:
          return CardFunction.END;
      }
    }

    @Override
    public int getCardId(int parent_id, int position) {
      int id = NO_ID;
      switch (position) {
        case 0:
          id = R.string.random;
          break;
        case 1:
          id = R.string.light1;
          break;
        case 2:
          id = R.string.light2;
          break;
        case 3:
          id = R.string.light3;
          break;
        case 4:
          id = R.string.light4;
          break;
        /*
        case 5:
          id = R.string.back;
          break;
         */
      }
      return id;
    }

    @Override
    public int getChildCardCount(int parent_id) {
      switch (parent_id) {
        case NO_ID:
          return 5;
          //return 6;
      }
      return 0;
    }

    @Override
    public int getCardType(int id) {
      return getResources().getInteger(R.integer.CARD_TYPE_LOGO_TITLE);
    }

    private class MyCardHolder extends CardHolder {

      CardView mCardView;
      ImageView mImageView;
      TextView mTitle;
      TextView mSubtitle;
      TextView mValue;
      Handler mHandler = new Handler();

      MyCardHolder(View itemView) {
        super(itemView);

        mCardView = itemView.findViewById(R.id.card_view);
        mImageView = itemView.findViewById(R.id.funcicon);
        mTitle = itemView.findViewById(R.id.card_text);
        mSubtitle = itemView.findViewById(R.id.card_subtext);
        mValue = itemView.findViewById(R.id.card_select);
      }

      void setText(String text) {
        //mValue.setText(getString(R.string.selected));
        mValue.setText(text);
      }

      void setText(String text, int msec) {
        //mValue.setText(getString(R.string.selected));
        mValue.setText(text);

        mHandler.postDelayed(new Runnable() {
          @Override
          public void run() {
            mValue.setText(" ");
          }
        }, msec);
      }

      void clearText() {
        mValue.setText(" ");
      }
    }
  }

}