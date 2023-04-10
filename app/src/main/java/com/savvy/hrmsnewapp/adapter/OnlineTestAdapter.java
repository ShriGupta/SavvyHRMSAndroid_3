package com.savvy.hrmsnewapp.adapter;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.interfaces.OnOptionSelected;
import com.savvy.hrmsnewapp.model.AnswersModel;
import com.savvy.hrmsnewapp.model.QuestionAnswerModel;

import java.util.List;

public class OnlineTestAdapter extends RecyclerView.Adapter<OnlineTestAdapter.MyViewHolder> implements AnswersAdapter.AnswerClickListener {

    //    private AnswersAdapter answersAdapter;
    private Context mContext;
    private List<QuestionAnswerModel> mQuestionAnswerModelList;
    private OnOptionSelected onOptionSelected;


    public OnlineTestAdapter(Context context, List<QuestionAnswerModel> questionAnswerModelList) {
        this.mContext = context;
        this.mQuestionAnswerModelList = questionAnswerModelList;
    }

    public void setOnOptionSelected(OnOptionSelected onOptionSelected) {
        this.onOptionSelected = onOptionSelected;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_online_test, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final QuestionAnswerModel questionAnswerModel = mQuestionAnswerModelList.get(position);
        holder.txtQuestions.setText(questionAnswerModel.getOTQTESTQUESTIONNAME());
        final List<AnswersModel> answersModelList = questionAnswerModel.getOTQANSWERS();
        holder.radioFirstAnswer.setText(answersModelList.get(0).getOTQATESTANSWER());
        holder.radioSecondAnswer.setText(answersModelList.get(1).getOTQATESTANSWER());
        holder.radioThirdAnswer.setText(answersModelList.get(2).getOTQATESTANSWER());
        holder.radioFourthAnswer.setText(answersModelList.get(3).getOTQATESTANSWER());
        /*holder.recyclerAnswers.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        answersAdapter = new AnswersAdapter(mContext, questionAnswerModel.getOTQANSWERS());
        holder.recyclerAnswers.setAdapter(answersAdapter);
        answersAdapter.setmAnswerClickListener(this);
        answersAdapter.notifyDataSetChanged();*/

        holder.radioFirstAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionSelected.onOptionSelected(questionAnswerModel.getOTQTESTQUESTIONID(),
                        questionAnswerModel.getOTQTESTQUESTIONNAME(), answersModelList.get(0).getOTQATESTQUESTIONANSWERID(),
                        answersModelList.get(0).getOTQATESTANSWER(), questionAnswerModel.getOTQA_RIGHT_ANSWER_ID(),
                        questionAnswerModel.getOTQA_RIGHT_ANSWER());
                holder.radioSecondAnswer.setChecked(false);
                holder.radioThirdAnswer.setChecked(false);
                holder.radioFourthAnswer.setChecked(false);
                holder.radioFirstAnswer.setChecked(true);
            }
        });

        holder.radioSecondAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionSelected.onOptionSelected(questionAnswerModel.getOTQTESTQUESTIONID(),
                        questionAnswerModel.getOTQTESTQUESTIONNAME(), answersModelList.get(1).getOTQATESTQUESTIONANSWERID(),
                        answersModelList.get(1).getOTQATESTANSWER(), questionAnswerModel.getOTQA_RIGHT_ANSWER_ID(),
                        questionAnswerModel.getOTQA_RIGHT_ANSWER());
                holder.radioFirstAnswer.setChecked(false);
                holder.radioSecondAnswer.setChecked(true);
                holder.radioThirdAnswer.setChecked(false);
                holder.radioFourthAnswer.setChecked(false);
            }
        });

        holder.radioThirdAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionSelected.onOptionSelected(questionAnswerModel.getOTQTESTQUESTIONID(),
                        questionAnswerModel.getOTQTESTQUESTIONNAME(), answersModelList.get(2).getOTQATESTQUESTIONANSWERID(),
                        answersModelList.get(2).getOTQATESTANSWER(), questionAnswerModel.getOTQA_RIGHT_ANSWER_ID(),
                        questionAnswerModel.getOTQA_RIGHT_ANSWER());
                holder.radioSecondAnswer.setChecked(false);
                holder.radioFirstAnswer.setChecked(false);
                holder.radioFourthAnswer.setChecked(false);
                holder.radioThirdAnswer.setChecked(true);
            }
        });

        holder.radioFourthAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionSelected.onOptionSelected(questionAnswerModel.getOTQTESTQUESTIONID(),
                        questionAnswerModel.getOTQTESTQUESTIONNAME(), answersModelList.get(3).getOTQATESTQUESTIONANSWERID(),
                        answersModelList.get(3).getOTQATESTANSWER(), questionAnswerModel.getOTQA_RIGHT_ANSWER_ID(),
                        questionAnswerModel.getOTQA_RIGHT_ANSWER());
                holder.radioSecondAnswer.setChecked(false);
                holder.radioThirdAnswer.setChecked(false);
                holder.radioFirstAnswer.setChecked(false);
                holder.radioFourthAnswer.setChecked(true);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mQuestionAnswerModelList.size();
    }

    @Override
    public void onAnswerSelected(int position) {
        System.out.println("Selected Position: " + position);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        //        private RecyclerView recyclerAnswers;
        private TextView txtQuestions;
        private RadioButton radioFirstAnswer, radioSecondAnswer, radioThirdAnswer, radioFourthAnswer;

        public MyViewHolder(View itemView) {
            super(itemView);
//            recyclerAnswers = itemView.findViewById(R.id.recycler_answers);
            txtQuestions = itemView.findViewById(R.id.txt_questions);
            radioFirstAnswer = itemView.findViewById(R.id.radioFirstAnswer);
            radioSecondAnswer = itemView.findViewById(R.id.radioSecondAnswer);
            radioThirdAnswer = itemView.findViewById(R.id.radioThirdAnswer);
            radioFourthAnswer = itemView.findViewById(R.id.radioFourthAnswer);
        }
    }
}
