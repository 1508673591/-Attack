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
import android.content.SharedPreferences;
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

public class LoginActivity extends Activity {

	//���ÿؼ������ͺͱ�����
	EditText user,psw;
	CheckBox store;
	Button button,toreg,cancel;
	TextView errorInfo;
	
	//�������ص�����
	String body;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//��������Ҫ�õ�uiҳ��	
		setContentView(R.layout.login);
		
		//���ݿؼ��������Լ����������Ӧ�ؼ���ȱ�ٸÿؼ�����ʱ��������ɽ��
		//�û��������ؼ�		
		user = (EditText) this.findViewById(R.id.login_userid);
		//���������ؼ�
		psw = (EditText) this.findViewById(R.id.login_password);
		//��ס����ؼ�
		store = (CheckBox) this.findViewById(R.id.login_storePassword);
        //��¼��ť�ؼ�
        button = (Button) this.findViewById(R.id.login_ok);
        toreg = (Button) this.findViewById(R.id.to_register_id);
        cancel = (Button) this.findViewById(R.id.login_cancel_id);
        //������ʾ�ؼ�
        errorInfo = (TextView) this.findViewById(R.id.login_errInfo);
        
        //button�ĵ���¼�
        button.setOnClickListener(new OnClickListener() {
        	@Override
			public void onClick(View v) {
        		//����û���������е�ֵ
        		String username = user.getText().toString().trim();
        		//�������������е�ֵ
        		String password = psw.getText().toString().trim();
        		//�ж��û��������Ƿ�Ϊ�գ�Ϊ����ʾ������ʾ
        		if(username.equals("")||password.equals("")){
        			errorInfo.setText("ȱ���û���������");
        		}else{
        			//�ǿ���������뽫������ʾ��ʾΪ�գ���������
        			errorInfo.setText("");
        			//�ӿڵ�ַ��������Ⱥ�������Щ����
        			//?�����ʱ��������ֵ���Ѷ�Ӧֵ���ڱ����������
        			String url = "http://119.23.206.8:8080/api/log?username="+username+"&password="+password;
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
        				Toast.makeText(LoginActivity.this, res.result.toString(), Toast.LENGTH_SHORT).show();
        			}else{
//                		if(store.isChecked()){}
                		SharedPreferences mySharedPreferences= getSharedPreferences("data", Activity.MODE_PRIVATE); 
            			SharedPreferences.Editor editor = mySharedPreferences.edit(); 
            			editor.putString("username", username); 
            			editor.putString("token", res.result.toString()); 
            			editor.commit();
                		//��¼�ɹ�ת������ҳ
                		finish();
        			}
        			
        		}
        		
			}
        });
        
        toreg.setOnClickListener(new OnClickListener() {
        	@Override
			public void onClick(View v) {
        		Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        		startActivity(intent);//������һactivity
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
