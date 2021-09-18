package com.example.assassin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.google.gson.Gson;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import android.view.View;
import android.view.View.OnClickListener;

public class ChangeActivity extends Activity {
	EditText title;
	EditText content;
	Button change_confirm;
	Button change_cancel;

	String textData = ""; // textid��ֵ
	String json_data;
	
	private AlertDialog alertdialog;

	// �������ص�����
	// String body;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change);

		String titleData = ""; // ��������
		String contentData = ""; // ��������
		
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			titleData = bundle.getString("title");// �õ���Ϊtitle��ֵ
			contentData = bundle.getString("content");// �õ���Ϊcontent��ֵ
			textData = bundle.getString("textid");// �õ���Ϊtextid��ֵ
		}
		title = (EditText) this.findViewById(R.id.change_title_id);
		content = (EditText) this.findViewById(R.id.change_content_id);
		change_confirm = (Button) this.findViewById(R.id.change_confirm_id);
		change_cancel = (Button) this.findViewById(R.id.change_cancel_id);
		// ����ҳ�ı���������ڱ༭ҳ������ʾ
		title.setText(titleData);
		content.setText(contentData);

		// "ȷ���޸�"��ť����¼�
		change_confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String token = "";
        		SharedPreferences sharedPreferences= getSharedPreferences("data", Activity.MODE_PRIVATE); 
        		if (sharedPreferences != null) {
        			token = sharedPreferences.getString("token", "");
        		}
				// ��ñ���������е�ֵ
				String title_change = title.getText().toString().trim();
				// �������������е�ֵ
				String content_change = content.getText().toString().trim();

				String url = "http://119.23.206.8:8080/api/edit";
				String Content = "token="+token+"&content="+content_change+"&title="+title_change+"&textid="+textData;
				MediaType type = MediaType
						.parse("application/x-www-form-urlencoded; charset=UTF-8");
				OkHttpClient okHttpClient = new OkHttpClient();
				RequestBody body = RequestBody.create(type, Content);
				final Request request = new Request.Builder().url(url)
						.post(body).build();
				final Call call = okHttpClient.newCall(request);
				Vector<Thread> threadVector = new Vector<Thread>();
				Thread httpThread = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Response response = call.execute();
							json_data = response.body().string();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});
				threadVector.add(httpThread);
				httpThread.start();
				// ʹ���������ӽ���ִ�к���ִ��
				for (Thread thread : threadVector) {
					try {
						thread.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				// �����ݴ�jsonתΪ����
				Object obj = new Object();
				Data data = new Gson().fromJson(json_data, Data.class);
				// ȡ��result�е�����
				String result = new Gson().toJson(data.data);
				Result res = new Gson().fromJson(result, Result.class);
				res.result.toString();
				// �ж��Ƿ��޸ĳɹ�
				if (data.code != 200) {
					Toast.makeText(ChangeActivity.this, res.result.toString(), Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(ChangeActivity.this, res.result.toString(), Toast.LENGTH_SHORT).show();
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
					finish();
				}

			}
		});

		change_cancel.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish();
			}
		});

	}

}
