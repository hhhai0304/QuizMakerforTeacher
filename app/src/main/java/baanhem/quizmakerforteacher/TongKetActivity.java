package baanhem.quizmakerforteacher;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import helper.DatabaseHelper;
import helper.PHPUrl;
import helper.QuizSystem;

import static java.nio.charset.StandardCharsets.*;

public class TongKetActivity extends Activity
{
    TextView tvTongket;
    Button btnDongTongKet;
    DatabaseHelper db;
    int SoCauDung, tongsocau;
    double Diem;
    String ketqua, NgayLam, UsernameSV, UsernameGV, MaDe;
    JSONObject jobj = null;
    String json = "";
    InputStream is = null;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tongket);
        setTitle("\tKết Quả");
        db = new DatabaseHelper(TongKetActivity.this);
        Calendar c = Calendar.getInstance();
        Date date = c.getTime();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        NgayLam = format.format(date);

        QuizSystem currentQuiz = ((QuizApplication)getApplication()).getCurrentGame();
        SoCauDung = currentQuiz.getRight();
        tongsocau = currentQuiz.getNumRounds();
        UsernameGV = currentQuiz.getUsernameGV();
        UsernameSV = currentQuiz.getUsernameSV();
        MaDe = currentQuiz.getMaDe();

        Diem = (Double.parseDouble(SoCauDung + "") / Double.parseDouble(tongsocau + "")) * 10;
        ketqua = "Trả lời đúng " + SoCauDung + " câu trên tổng số " + tongsocau + " câu hỏi!\nĐiểm: " + Diem + "đ";
        tvTongket = (TextView)findViewById(R.id.tvTongket);
        tvTongket.setText(ketqua);

        UpDiem task = new UpDiem(UsernameSV, UsernameGV, MaDe, NgayLam, SoCauDung + "", Diem + "");
        task.execute();
        db.xoaCauhoi();

        btnDongTongKet = (Button)findViewById(R.id.btnDongTongKet);
        btnDongTongKet.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

    public class UpDiem extends AsyncTask<Void, Void, Void>
    {
        ProgressDialog progress;
        String UsernameSV, UsernameGV, MaDe, NgayLam, SoCauDung, Diem;

        public UpDiem(String UsernameSV, String UsernameGV, String MaDe, String NgayLam, String SoCauDung, String Diem)
        {
            this.UsernameSV = UsernameSV;
            this.UsernameGV = UsernameGV;
            this.MaDe = MaDe;
            this.NgayLam = NgayLam;
            this.SoCauDung = SoCauDung;
            this.Diem = Diem;
        }
        @Override
        protected void onPreExecute()
        {
            progress = new ProgressDialog(TongKetActivity.this);
            progress.setMessage("Đang Cập nhật Thông tin Điểm số");
            progress.setIndeterminate(false);
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            JSONObject json_object = getJsonFromUrl(PHPUrl.updatediem, "", UsernameSV, UsernameGV, MaDe, NgayLam, SoCauDung, Diem);
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            progress.dismiss();
            super.onPostExecute(result);
        }
    }

    public JSONObject getJsonFromUrl(String url, String CMD, String UsernameSV, String UsernameGV, String MaDe, String NgayLam, String SoCauDung, String Diem)
    {
        try
        {
            // Khởi tạo
            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);

            //Gửi thông tin lệnh lên Server
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("CMD", CMD));
            nameValuePairs.add(new BasicNameValuePair("UsernameSV", UsernameSV));
            nameValuePairs.add(new BasicNameValuePair("UsernameGV", UsernameGV));
            nameValuePairs.add(new BasicNameValuePair("MaDe", MaDe));
            nameValuePairs.add(new BasicNameValuePair("NgayLam", NgayLam));
            nameValuePairs.add(new BasicNameValuePair("SoCauDung", SoCauDung));
            nameValuePairs.add(new BasicNameValuePair("Diem", Diem));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));


            // Thực thi, lấy nội dung về
            HttpResponse http_reaponse = client.execute(httppost);
            HttpEntity http_entity = http_reaponse.getEntity();

            is = http_entity.getContent();

            // Đọc dữ liệu
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"),8 );
            StringBuilder sb = new StringBuilder();
            String line = null;
            while((line=reader.readLine())!=null)
            {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString(); // Đọc StringBuilder vào chuỗi
            Pattern p = Pattern.compile("^.*?\\((.*?)\\);$", Pattern.DOTALL);
            Matcher m = p.matcher(json);
            if (m.matches())
            {
                json = m.group(1);
                @SuppressWarnings("unused")
                JSONObject jo = new JSONObject(json);
                jobj = new JSONObject(json); // Đưa chuỗi vào đối tượng JSon
            }
            else
            {
                jobj = new JSONObject(json); // Đưa chuỗi vào đối tượng JSon
            }

        }
        catch(Exception e)
        {
            Log.i("Lỗi Json",e.toString());
        }
        return jobj;
    }
}