package baanhem.quizmakerforteacher;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import helper.DatabaseHelper;
import helper.PHPUrl;
import helper.QuizSystem;
import item.AccountItem;
import item.CauhoiItem;
import item.DeItem;
import item.MonhocItem;

public class QuanLySVActivity extends Activity
{
    Spinner spnGiangvien, spnMonhoc;
    Button btnLambai;
    EditText edtMade;
    TextView tvHelloSV;
    Menu menu;
    DatabaseHelper db;
    JSONObject jobj = null;
    String json = "", tende = "", UsernameSV = "";
    JSONArray JArr = null;
    InputStream is = null;
    String[] gv_username, gv_hoten, mon_mamon, mon_tenmon;
    ArrayList<AccountItem> AccountList;
    ArrayList<MonhocItem> MonHocList;
    int chonGV, chonMon, thoigian;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.right_in_start, R.anim.right_in_end);
        setContentView(R.layout.activity_quanly_sv);
        setTitle("\tSinh Viên");

        spnGiangvien = (Spinner)findViewById(R.id.spnGiangvien);
        spnMonhoc = (Spinner)findViewById(R.id.spnMonhoc);
        btnLambai = (Button)findViewById(R.id.btnLambai);
        edtMade = (EditText)findViewById(R.id.edtMade);
        tvHelloSV = (TextView)findViewById(R.id.tvHelloSV);

        Bundle b = getIntent().getExtras();
        String Ho = b.getString("Ho");
        String Ten = b.getString("Ten");
        UsernameSV = b.getString("Username");

        tvHelloSV.setText("Xin chào " + Ho + " " + Ten);

        db = new DatabaseHelper(QuanLySVActivity.this);
        AccountList = db.getGiangvien();
        MonHocList = db.getMonhoc();

        gv_username = new String[AccountList.size()];
        gv_hoten = new String[AccountList.size()];
        mon_mamon = new String[MonHocList.size()];
        mon_tenmon = new String[MonHocList.size()];

        for(int i = 0; i < AccountList.size(); i++)
        {
            gv_username[i] = AccountList.get(i).Username;
            gv_hoten[i] = AccountList.get(i).Ho + " " + AccountList.get(i).Ten;
        }
        for(int j = 0; j < MonHocList.size(); j++)
        {
            mon_mamon[j] = MonHocList.get(j).MaMon;
            mon_tenmon[j] = MonHocList.get(j).TenMon;
        }

        ArrayAdapter<String> adapterGV = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, gv_hoten);
        adapterGV.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spnGiangvien.setAdapter(adapterGV);

        ArrayAdapter<String> adapterMon = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mon_tenmon);
        adapterMon.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spnMonhoc.setAdapter(adapterMon);

        spnGiangvien.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                chonGV = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spnMonhoc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                chonMon = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnLambai.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                GetData task = new GetData(edtMade.getText().toString());
                task.execute();
            }
        });
    }

    public class GetData extends AsyncTask<Void, Void, Void>
    {
        String made;
        ProgressDialog progress;

        public GetData(String made)
        {
            this.made = made;
        }

        @Override
        protected void onPreExecute()
        {
            progress = new ProgressDialog(QuanLySVActivity.this);
            progress.setMessage("Đang lấy dữ liệu bài Quiz");
            progress.setIndeterminate(false);
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            JSONObject json_object = getJsonFromUrl(PHPUrl.getCauhoi, made);
            try
            {
                JArr = new JSONArray();
                JArr = json_object.getJSONArray("All_CauHoi");
                if (JArr.length() > 0)
                {
                    for(int i=0; i < JArr.length(); i++)
                    {
                        JSONObject gv = JArr.getJSONObject(i);
                        CauhoiItem ci = new CauhoiItem();
                        ci.MaCauHoi = gv.getInt("MaCauHoi");
                        ci.NoiDung = gv.getString("NoiDung");
                        ci.DapAnDung = gv.getString("DapAnDung");
                        ci.DapAn1 = gv.getString("DapAn1");
                        ci.DapAn2 = gv.getString("DapAn2");
                        ci.DapAn3 = gv.getString("DapAn3");

                        db.themCauhoi(ci);
                    }
                }
                else
                {
                    Toast.makeText(QuanLySVActivity.this, "Đề Không tồn tại!\nHãy thử nhập Mã đề khác.", Toast.LENGTH_SHORT).show();
                }
            }
            catch(Exception e)
            {
                Log.i("Lỗi Kết nối", e.toString());
            }

            JSONObject json_object2 = getJsonFromUrl(PHPUrl.getTende, made);
            try
            {
                JArr = new JSONArray();
                JArr = json_object2.getJSONArray("Get_De");
                for(int i=0; i < JArr.length(); i++)
                {
                    JSONObject gv = JArr.getJSONObject(i);
                    tende = gv.getString("TenDe");
                    thoigian = gv.getInt("ThoiGian");
                }
            }
            catch(Exception e)
            {
                Log.i("Lỗi Kết nối", e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            progress.dismiss();
            try
            {
                List<CauhoiItem> questions = db.getCauhoi();

                QuizSystem c = new QuizSystem();
                c.setQuestions(questions);
                c.setNumRounds(db.getSoCauhoi());
                c.setUsernameSV(UsernameSV);
                c.setUsernameGV(gv_username[chonGV]);
                c.setMaDe(made);
                c.setTime(thoigian);
                ((QuizApplication)getApplication()).setCurrentGame(c);

                Intent i = new Intent(QuanLySVActivity.this, CauHoiActivity.class);
                Bundle b = new Bundle();
                b.putString("TenDe", tende);
                i.putExtras(b);
                startActivity(i);
            }
            catch (Exception e)
            {
                Log.e("Lỗi Lấy đề", e.toString());
            }
            super.onPostExecute(result);
        }
    }

    public JSONObject getJsonFromUrl(String url, String CMD)
    {
        try
        {
            // Khởi tạo
            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);

            //Gửi thông tin lệnh lên Server
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("CMD", CMD));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.action_account);
        item.setIcon(R.drawable.ic_account);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_account:
                Toast.makeText(this, "Chức năng Quản lý Account chưa được phát triển!", Toast.LENGTH_LONG).show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}