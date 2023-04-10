package com.savvy.hrmsnewapp.markPunch;

import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.savvy.hrmsnewapp.R;
import com.savvy.hrmsnewapp.activity.BaseActivity;
import com.savvy.hrmsnewapp.adapter.OnlineTestResultAdapter;
import com.savvy.hrmsnewapp.model.SelectedAnswersModel;

import java.util.ArrayList;

public class OnlineTestResultActivity extends BaseActivity {

    private ArrayList<SelectedAnswersModel> selectedAnswersModelList;
    private RecyclerView recyclerViewResult;
    private OnlineTestResultAdapter mOnlineTestResultAdapter;
    private TextView txtResult, txtTotalScore, txtAnswered, txtTotal, txtMessage, txtSampleTextQuestions;
    private int totalScoreCount = 0;
    private String totalScore, totalMarks, totalAttempt, totalQuestions, resultStatus;
    private Button btnClose;
    private NestedScrollView parentScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_test_result);
        recyclerViewResult = (RecyclerView) findViewById(R.id.recycler_question_answer);
        selectedAnswersModelList = new ArrayList<>();
        txtResult = (TextView) findViewById(R.id.txv_result);
        txtTotalScore = (TextView) findViewById(R.id.txtTotalScore);
        txtAnswered = (TextView) findViewById(R.id.txtAnswered);
        txtTotal = (TextView) findViewById(R.id.txtTotalQuestion);
        txtMessage = (TextView) findViewById(R.id.txv_message);
        txtSampleTextQuestions = (TextView) findViewById(R.id.txtSampleTextQuestions);
        parentScrollView = (NestedScrollView) findViewById(R.id.parentScrollView);

        btnClose = (Button) findViewById(R.id.btn_close);

        Bundle intent = getIntent().getBundleExtra("bundle");
        selectedAnswersModelList = intent.getParcelableArrayList("SelectedAnswerList");
        totalScore = getIntent().getStringExtra("totalScore");
        resultStatus = getIntent().getStringExtra("resultStatus");
        totalMarks = getIntent().getStringExtra("totalMarks");
        totalAttempt = getIntent().getStringExtra("totalAttempt");
        totalQuestions = getIntent().getStringExtra("totalQuestions");

        for (SelectedAnswersModel selectedAnswersModel : selectedAnswersModelList) {
            if (selectedAnswersModel.getAnsId().equals(selectedAnswersModel.getCorrectAnsId())) {
                totalScoreCount++;
            }
        }

        if (resultStatus.equals("0")) {
            recyclerViewResult.setVisibility(View.VISIBLE);
            txtMessage.setText("Sorry ! You're not eligible to mark your Attendance !");
            txtResult.setText("FAIL");
        } else {
            recyclerViewResult.setVisibility(View.GONE);
            txtMessage.setText("Attendance marked Successfully.");
            txtResult.setText("PASS");
            txtSampleTextQuestions.setVisibility(View.GONE);
            txtResult.setTextColor(getResources().getColor(R.color.color_parat_green));
            if (resultStatus.equals("1")) {
                Snackbar snackbar = Snackbar
                        .make(parentScrollView, "Attendance marked Successfully.", Snackbar.LENGTH_LONG);
                snackbar.show();
                txtMessage.setText("Attendance marked Successfully.");
            } else if (resultStatus.equals("3")) {
                txtMessage.setText("Location out of range.");
                Snackbar snackbar = Snackbar
                        .make(parentScrollView, "Location out of range.", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }


        txtTotal.setText(totalQuestions);
        txtAnswered.setText(totalAttempt);
        txtTotalScore.setText("Scored " + totalScore + " out of " + totalMarks);
        mOnlineTestResultAdapter = new OnlineTestResultAdapter(this, selectedAnswersModelList);
        recyclerViewResult.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerViewResult.setAdapter(mOnlineTestResultAdapter);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
