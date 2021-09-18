package com.example.assassin;

import java.io.IOException;
import java.util.Vector;

import com.google.gson.Gson;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity {

	//���ÿؼ������ͺͱ�����
	EditText user,psw,check_psw;
	Button button;
	Button cancel;
	TextView errorInfo;
	
	//�������ص�����
	String body;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//��������Ҫ�õ�uiҳ��
		setContentView(R.layout.register);
		
		user = (EditText) this.findViewById(R.id.login_userid);
		psw = (EditText) this.findViewById(R.id.login_password);
		check_psw = (EditText) this.findViewById(R.id.check_psw);
		button = (Button) this.findViewById(R.id.login_reg);
		cancel = (Button) this.findViewById(R.id.reg_cancel_id);
		errorInfo = (TextView) this.findViewById(R.id.login_errInfo);
		
		button.setOnClickListener(new OnClickListener() {
        	@Override
			public void onClick(View v) {
        		//����û���������е�ֵ
        		String username = user.getText().toString().trim();
        		//�������������е�ֵ
        		String password = psw.getText().toString().trim();
        		String check_password = check_psw.getText().toString().trim();
        		//�ж��û��������Ƿ�Ϊ�գ�Ϊ����ʾ������ʾ
        		if(username.equals("")||password.equals("")){
        			errorInfo.setText("ȱ���û���������");
        		}else if(!password.equals(check_password)){
        			errorInfo.setText("������������벻ͬ");
        		}else{
        			//�ǿ���������뽫������ʾ��ʾΪ�գ���������
        			errorInfo.setText("");
        			//�ӿڵ�ַ��������Ⱥ�������Щ����
        			//?�����ʱ��������ֵ���Ѷ�Ӧֵ���ڱ����������
        			String url = "http://119.23.206.8:8080/api/reg?username="+username+"&password="+password;
        			//���ﲻ�ö���ǰ�󽻻���
        			OkHttpClient okHttpClient = new OkHttpClient();
        			final Request request = new Request.Builder()
        			        .url(url)
        			        .build();
        			final Call call = okHttpClient.newCall(request);
        			Vector<Thread> threadVector = new Vector<Thread>();
                    Thread httpThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Response response = call.execute();
                                body = response.body().string();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    threadVector.add(httpThread);
                    httpThread.start();
                    //ʹ���������ӽ���ִ�к���ִ��
                    for(Thread thread : threadVector){
                        try {
							thread.join();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
                    }
        			//�����ݴ�jsonתΪ����
        			Object obj = new Object();
        			Data data = new Gson().fromJson(body,Data.class);
        			//ȡ��result�е�����
        			String result = new Gson().toJson(data.data);
        			Result res = new Gson().fromJson(result,Result.class);
        			res.result.toString();
        			//�ж��Ƿ��¼�ɹ�
        			if(data.code != 200){
        				//���ɹ��򷵻ش���
//        				errorInfo.setText(res.result.toString());
        				Toast.makeText(RegisterActivity.this, res.result.toString(), Toast.LENGTH_SHORT).show();
        			}else{
                		//ע��ɹ���ͣһ��ת������¼ҳ
//        				errorInfo.setText(res.result.toString());
        				Toast.makeText(RegisterActivity.this, res.result.toString(), Toast.LENGTH_SHORT).show();
        				try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
//                		Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
//                		startActivity(intent);//������һactivity
        				finish();
        			}
        			
        		}
        		
			}
        });
		
		cancel.setOnClickListener(new OnClickListener() {
        	@Override
			public void onClick(View v) {
        		finish();
			}
        });
		
	}
	
}
