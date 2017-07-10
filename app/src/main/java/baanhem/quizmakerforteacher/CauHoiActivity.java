package baanhem.quizmakerforteacher;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import helper.QuizSystem;
import item.CauhoiItem;

public class CauHoiActivity extends Activity
{
    private CauhoiItem currentQuestion;
    private QuizSystem currentTest;

    Button btnTraloi;
    RadioButton rbTraloi1, rbTraloi2, rbTraloi3, rbTraloi4;
    TextView tvDeBai, tvCauHoi;

    String tende;
    int thoigian;
    CountDownTimer time;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.right_in_start, R.anim.right_in_end);
        setContentView(R.layout.activity_cauhoi);
        setTitle("\tKiểm Tra");

        btnTraloi = (Button) findViewById(R.id.btnTraloi);
        rbTraloi1 = (RadioButton)findViewById(R.id.rbTraloi1);
        rbTraloi2 = (RadioButton)findViewById(R.id.rbTraloi2);
        rbTraloi3 = (RadioButton)findViewById(R.id.rbTraloi3);
        rbTraloi4 = (RadioButton)findViewById(R.id.rbTraloi4);
        tvDeBai = (TextView)findViewById(R.id.tvDeBai);
        tvCauHoi = (TextView)findViewById(R.id.tvCauHoi);

        Bundle b = getIntent().getExtras();
        tende = b.getString("TenDe");

        currentTest = ((QuizApplication)getApplication()).getCurrentGame();
        currentQuestion = currentTest.getNextQuestion();
        thoigian = currentTest.getTime() * 1000;

        String cauhoi = currentQuestion.getQuestion();
        tvCauHoi.setText(currentTest.getRound() + ". " + cauhoi);
        tvDeBai.setText(tende);

        List<String> answers = currentQuestion.getQuestionOptions();
        rbTraloi1.setText(answers.get(0));
        rbTraloi2.setText(answers.get(1));
        rbTraloi3.setText(answers.get(2));
        rbTraloi4.setText(answers.get(3));

        time = new CountDownTimer(thoigian, 1000)
        {
            @Override
            public void onTick(long millisUntilFinished)
            {
                tvDeBai.setText(tende + "\nCòn " + millisUntilFinished/1000 + " giây!");
            }

            @Override
            public void onFinish()
            {
                tvDeBai.setText(tende + "\nHẾT GIỜ!");
                if (!checkAnswer())
                {
                    return;
                }

                if (currentTest.isGameOver())
                {
                    Intent i = new Intent(CauHoiActivity.this, TongKetActivity.class);
                    startActivity(i);
                    finish();
                }
                else
                {
                    Intent i = new Intent(CauHoiActivity.this, CauHoiActivity.class);
                    Bundle b = new Bundle();
                    b.putString("TenDe", tende);
                    i.putExtras(b);
                    startActivity(i);
                    finish();
                }
            }
        }.start();

        btnTraloi.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                time.cancel();
                if (!checkAnswer())
                {
                    return;
                }

                if (currentTest.isGameOver())
                {
                    Intent i = new Intent(CauHoiActivity.this, TongKetActivity.class);
                    startActivity(i);
                    finish();
                }
                else
                {
                    Intent i = new Intent(CauHoiActivity.this, CauHoiActivity.class);
                    Bundle b = new Bundle();
                    b.putString("TenDe", tende);
                    i.putExtras(b);
                    startActivity(i);
                    finish();
                }
            }
        });


    }

    private boolean checkAnswer()
    {
        String answer = getSelectedAnswer();
        if (answer==null)
        {
            return false;
        }
        else
        {
            if (currentQuestion.getAnswer().equals(answer))
            {
                Toast.makeText(CauHoiActivity.this, "Đúng", Toast.LENGTH_SHORT).show();
                currentTest.incrementRightAnswers();
            }
            else
            {
                Toast.makeText(CauHoiActivity.this, "Sai", Toast.LENGTH_SHORT).show();
                currentTest.incrementWrongAnswers();
            }
            return true;
        }
    }

    private String getSelectedAnswer()
    {
        if (rbTraloi1.isChecked())
        {
            return rbTraloi1.getText().toString();
        }
        if (rbTraloi2.isChecked())
        {
            return rbTraloi2.getText().toString();
        }
        if (rbTraloi3.isChecked())
        {
            return rbTraloi3.getText().toString();
        }
        if (rbTraloi4.isChecked())
        {
            return rbTraloi4.getText().toString();
        }

        return null;
    }
}