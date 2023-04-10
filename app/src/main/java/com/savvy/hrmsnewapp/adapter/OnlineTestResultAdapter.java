package com.savvy.hrmsnewapp.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.model.SelectedAnswersModel;

import java.util.List;

public class OnlineTestResultAdapter extends RecyclerView.Adapter<OnlineTestResultAdapter.ResultViewHolder> {

    private Context mContext;
    private List<SelectedAnswersModel> selectedAnswersModelList;

    public OnlineTestResultAdapter(Context mContext, List<SelectedAnswersModel> selectedAnswersModelList) {
        this.mContext = mContext;
        this.selectedAnswersModelList = selectedAnswersModelList;
    }

    @Override
    public ResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question_answer, parent, false);
        return new ResultViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ResultViewHolder holder, int position) {
        SelectedAnswersModel selectedAnswersModel = selectedAnswersModelList.get(position);
        holder.txtQuestion.setText(selectedAnswersModel.getQuesName());
        holder.txtSelectedAnswer.setText(selectedAnswersModel.getAnsName());
        holder.txtCorrectAnswer.setText(selectedAnswersModel.getCorrecrAnsName());
    }

    @Override
    public int getItemCount() {
        return selectedAnswersModelList.size();
    }

    public class ResultViewHolder extends RecyclerView.ViewHolder {
        TextView txtQuestion, txtSelectedAnswer, txtCorrectAnswer;

        public ResultViewHolder(View itemView) {
            super(itemView);
            txtQuestion = itemView.findViewById(R.id.txt_questions);
            txtSelectedAnswer = itemView.findViewById(R.id.selectedAnswer);
            txtCorrectAnswer = itemView.findViewById(R.id.txtCorrectAnswer);
        }
    }
}
