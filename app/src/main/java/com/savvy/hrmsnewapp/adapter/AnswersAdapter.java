package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.model.AnswersModel;

import java.util.List;

public class AnswersAdapter extends RecyclerView.Adapter<AnswersAdapter.AnswerViewHolder> {

    private Context mContext;
    private List<AnswersModel> mAnswersModelList;
    private int lastSelectedPosition = -1;
    private CompoundButton lastCheckedRB = null;
    private AnswerClickListener mAnswerClickListener = null;

    public AnswersAdapter(Context context, List<AnswersModel> answersModelList) {
        this.mContext = context;
        this.mAnswersModelList = answersModelList;
    }

    public void setmAnswerClickListener(AnswerClickListener answerClickListener) {
        this.mAnswerClickListener = answerClickListener;
    }

    @Override
    public AnswerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_answer, parent, false);
        return new AnswerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AnswerViewHolder holder, int position) {
        AnswersModel answersModel = mAnswersModelList.get(position);
        holder.radioAnswer.setText(answersModel.getOTQATESTANSWER());
        mAnswerClickListener.onAnswerSelected(position);
        holder.radioAnswer.setOnCheckedChangeListener(ls);
        holder.radioAnswer.setTag(position);
       /* holder.radioAnswer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                RadioButton checked_rb = (RadioButton) buttonView.findViewById(buttonView.getId());
                if (lastCheckedRB != null) {
                    lastCheckedRB.setChecked(false);
                    //store the clicked radiobutton
                    lastCheckedRB = checked_rb;
                }else {
                    //store the clicked radiobutton
                    lastCheckedRB = checked_rb;
                }

            }
        });*/
    }

    private CompoundButton.OnCheckedChangeListener ls = (new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            int tag = (int) buttonView.getTag();
            if (lastCheckedRB == null) {
                lastCheckedRB = buttonView;
            } else if (tag != (int) lastCheckedRB.getTag()) {
                lastCheckedRB.setChecked(false);
                lastCheckedRB = buttonView;
            }

        }
    });

    @Override
    public int getItemCount() {
        return mAnswersModelList.size();
    }

    public class AnswerViewHolder extends RecyclerView.ViewHolder {
        private RadioButton radioAnswer;


        public AnswerViewHolder(View itemView) {
            super(itemView);
            radioAnswer = itemView.findViewById(R.id.radio_answer);

           /* radioAnswer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lastSelectedPosition = getAdapterPosition();
                    notifyDataSetChanged();
                    mAnswerClickListener.onAnswerSelected(lastSelectedPosition);
                }
            });*/
        }
    }

    interface AnswerClickListener {
        void onAnswerSelected(int position);
    }
}
