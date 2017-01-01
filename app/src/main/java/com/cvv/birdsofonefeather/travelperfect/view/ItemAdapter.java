package com.cvv.birdsofonefeather.travelperfect.view;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.cvv.birdsofonefeather.travelperfect.R;
import com.cvv.birdsofonefeather.travelperfect.model.Item;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Carla
 * Date: 01/01/2017
 * Project: Capstone-Project
 */

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private LayoutInflater mLayoutInflater;
    private List<Item> mItems = new ArrayList<>();

    public ItemAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements TextWatcher, CompoundButton.OnCheckedChangeListener {

        @BindView(R.id.checkbox)
        CheckBox mCheckBox;
        @BindView(R.id.number_of)
        EditText mNumberOf;
        @BindView(R.id.name)
        EditText mName;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mNumberOf.addTextChangedListener(this);
            mName.addTextChangedListener(this);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            mItems.get(getAdapterPosition()).setNumberOf(Integer.parseInt(mNumberOf.getText().toString()));
            mItems.get(getAdapterPosition()).setWhat(mNumberOf.getText().toString());
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mItems.get(getAdapterPosition()).setDone(isChecked);
            // http://stackoverflow.com/questions/25650203/android-how-to-add-strikethrough-to-textview-by-clicking-on-checkbox-in-listvie
            if (isChecked) {
                mName.setPaintFlags(mName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                mName.setPaintFlags(mName.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mLayoutInflater.inflate(R.layout.item_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Item item = mItems.get(position);
        holder.mCheckBox.setChecked(item.isDone());
        holder.mName.setText(item.getWhat());
        holder.mNumberOf.setText(item.getNumberOf());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public long getItemId(int position) {
        return mItems.get(position).getId();
    }

    public void addItem(Item item) {
        int newPos = mItems.size();
        mItems.add(item);
        notifyItemInserted(newPos);
    }

}