package com.example.assassin;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	Menu game, character, view, place;

	// ��������
	TextView edit; // ��Ӧ��ɫ�༭��ť
	TextView title; // ��Ӧ����
	TextView content; // ��Ӧ��������
	EditText comment_info; // ��Ӧ���۵�����
	Button comment; // ��Ӧȷ���������۰�ť
	LinearLayout layout;

	String body;
	String sort_body;
	String cmt_body;

	String textid = "1";

	public void onResume() {
		super.onResume();
		onCreate(null);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		String url = "http://119.23.206.8:8080/api/text?id=" + textid;
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
      //�������������Ϣ
        String cmt_url = "http://119.23.206.8:8080/api/getcmt?textid=" + textid;
		OkHttpClient okCmtHttpClient = new OkHttpClient();
		final Request requestCmt = new Request.Builder()
		        .url(cmt_url)
		        .build();
		final Call callCmt = okCmtHttpClient.newCall(requestCmt);
		Vector<Thread> threadVectorCmt = new Vector<Thread>();
        Thread httpThreadCmt = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = callCmt.execute();
                    cmt_body = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        threadVectorCmt.add(httpThreadCmt);
        httpThreadCmt.start();
        //ʹ���������ӽ���ִ�к���ִ��
        for(Thread thread : threadVectorCmt){
            try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
        
        //�����ݴ�jsonתΪ����
		Data data = new Gson().fromJson(body,Data.class);
		Data cmtData = new Gson().fromJson(cmt_body,Data.class);
		//�ж��з��ȡ����
		if(data.code != 200){
			String result = new Gson().toJson(data.data);
			Result res = new Gson().fromJson(result,Result.class);
//			errorInfo.setText(res.result.toString());
		}else{
			String result = new Gson().toJson(data.data);
			Result res = new Gson().fromJson(result,Result.class);
			String resData = new Gson().toJson(res.result);
			List<Text> textlist = new Gson().fromJson(resData,new TypeToken<List<Text>>(){}.getType());
			Text text = textlist.get(0);
			
			
			edit = (TextView)this.findViewById(R.id.main_edit_btn);
	        title = (TextView)this.findViewById(R.id.main_title_id);
	        content = (TextView)this.findViewById(R.id.main_content_id);
	        comment_info = (EditText)this.findViewById(R.id.main_comment_input);
	        comment = (Button)this.findViewById(R.id.main_comment_btn);
			
	        //��������
			title.setText(text.title);
			content.setText(text.content);
			
	        //�����ҳ�����Եġ��༭����ť������ת���༭ҳ��ͬʱ�ڱ༭ҳ��ʾ��ҳ����Ϣ
	        edit.setOnClickListener(new OnClickListener() {
	        	@Override
	        	public void onClick(View v){
	        		Bundle bundle = new Bundle();
	        		bundle.putString("title", title.getText().toString()); //������
	        		bundle.putString("content", content.getText().toString()); //������
	        		bundle.putString("textid", textid.toString()); //��textid
	        		//����ҳ�Ĳ��������༭ҳ
	        		Intent intent1 = new Intent(MainActivity.this, ChangeActivity.class);
	        		intent1.putExtras(bundle);//���ز���
	        		startActivity(intent1);//������һactivity
	        	}
	        });
	        
	        if(cmtData.code == 200){
	        	String resultCmt = new Gson().toJson(cmtData.data);
				Result resCmt = new Gson().fromJson(resultCmt,Result.class);
				String resDataCmt = new Gson().toJson(resCmt.result);
				List<Comment> cmtList = new Gson().fromJson(resDataCmt,new TypeToken<List<Comment>>(){}.getType());
	        	//�������
		        LinearLayout layout = (LinearLayout)this.findViewById(R.id.comment);
		        for(int i=0;i<cmtList.size();i++){
		        	TextView textViewUser = new TextView(this);
		        	textViewUser.setText(cmtList.get(i).username+"��");
		            layout.addView(textViewUser);
		            TextView textViewCmt = new TextView(this);
		            textViewCmt.setText(cmtList.get(i).comment);
		            layout.addView(textViewCmt);
		            TextView textViewE = new TextView(this);
		            textViewE.setText(" ");
		            textViewE.setHeight(5);
		            layout.addView(textViewE);
		        }
	        }
	        
	        //���ȷ����������
	        comment.setOnClickListener(new OnClickListener() {
	        	@Override
	        	public void onClick(View v){
	        		String token = "";
	        		SharedPreferences sharedPreferences= getSharedPreferences("data", Activity.MODE_PRIVATE); 
	        		if (sharedPreferences != null) {
	        			token = sharedPreferences.getString("token", "");
	        		}
	        		//������ۿ��ֵ
	        		String comments = comment_info.getText().toString().trim();
	        		
	        		if(comments.equals("")){
	        			Toast.makeText(MainActivity.this, "��������յ�����", Toast.LENGTH_SHORT).show();
	        		}
	        		else{
	        			//���۵Ľӿ�
	        			String url = "http://119.23.206.8:8080/api/cmt?token="+token+"&textid=1&cmt=" + comments;
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
	        			
	        			//�ж��Ƿ�ɹ���������
	        			if(data.code != 200){
	        				//���ɹ��򷵻ش���
	        				Toast.makeText(MainActivity.this, res.result.toString(), Toast.LENGTH_SHORT).show();
	        			}else{
	        				onCreate(null);
	        			}
	        		}
	        	}
	        });
	        
		}
		
		
	}

	/**
	 * ����ѡ��˵��¼�
	 * 
	 * @param menu
	 * @return
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// ���Բ��ô���
		// ����ѡ��˵�
		getMenuInflater().inflate(R.menu.main_menu, menu);

		Menu game = menu.addSubMenu("��Ϸ��Ʒ").setIcon(
				android.R.drawable.ic_menu_gallery);
		Menu character = menu.addSubMenu("��ɫ").setIcon(
				android.R.drawable.ic_menu_my_calendar);
		Menu view = menu.addSubMenu("�����").setIcon(
				android.R.drawable.ic_menu_myplaces);
		Menu place = menu.addSubMenu("�ص�").setIcon(
				android.R.drawable.ic_menu_mapmode);

		String[] sort = { "��Ϸ��Ʒ", "��ɫ", "�����", "�ص�" };
		Menu[] menus = { game, character, view, place };

		for (int i = 0; i < sort.length; i++) {
			List<Text> textlist = getSort(sort[i]);
			for (int j = 0; j < textlist.size(); j++) {
				Text text = textlist.get(j);
				menus[i].add(i, text.id, i, text.title);
			}
		}

		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * ���ڲ˵�����¼�
	 * 
	 * @param item
	 */
	public void onAboutMenu(MenuItem item) {
		// ֪ͨϵͳˢ��Menu
		// invalidateOptionsMenu();
		// ��ת����¼����
		Intent intent1 = new Intent(MainActivity.this, LoginActivity.class);
		startActivity(intent1);// ������һactivity
	}

	public List<Text> getSort(String sort) {
		String url = "http://119.23.206.8:8080/api/sort?sort=" + sort;
		OkHttpClient okHttpClient = new OkHttpClient();
		final Request request = new Request.Builder().url(url).build();
		final Call call = okHttpClient.newCall(request);
		Vector<Thread> threadVector = new Vector<Thread>();
		Thread httpThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Response response = call.execute();
					sort_body = response.body().string();
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

		Data data = new Gson().fromJson(sort_body, Data.class);
		if (data.code != 200) {
			return new ArrayList<Text>();
		} else {
			String result = new Gson().toJson(data.data);
			Result res = new Gson().fromJson(result, Result.class);
			String resData = new Gson().toJson(res.result);
			List<Text> textlist = new Gson().fromJson(resData,
					new TypeToken<List<Text>>() {
					}.getType());
			return textlist;
		}
	}

	/**
	 * ѡ��˵���ѡ���¼�
	 * 
	 * @param item
	 * @return
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_search) {
			showNormalDia();
			return true;
		}
		if (item.getItemId() > 0) {
			Bundle bundle = new Bundle();
			bundle.putString("textid", Integer.toString(item.getItemId())); // ��textid
			// �Ѵ���ҳ�Ĳ��������༭ҳ
			Intent intent1 = new Intent(MainActivity.this, EntryActivity.class);
			intent1.putExtras(bundle);// ���ز���
			startActivity(intent1);// ������һactivity
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * ѡ��˵��򿪺��¼����������˵���ͼ����ʾ����
	 * 
	 * @param item
	 * @return
	 */
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
			if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
				try {
					Method m = menu.getClass().getDeclaredMethod(
							"setOptionalIconsVisible", Boolean.TYPE);
					m.setAccessible(true);
					m.invoke(menu, true);
				} catch (Exception e) {
				}
			}
		}
		return super.onMenuOpened(featureId, menu);
	}

	/* ������ */
	private void showNormalDia() {
		AlertDialog.Builder normalDia = new AlertDialog.Builder(
				MainActivity.this);
		normalDia.setIcon(R.drawable.ic_search);
		final EditText text = new EditText(MainActivity.this);
		normalDia.setView(text);
		normalDia.setTitle("����");
		normalDia.setMessage("��������");
		normalDia.setPositiveButton("ȷ��",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						String content = text.getText().toString();
						Bundle bundle = new Bundle();
						bundle.putString("content", content); // ��textid
						// �Ѵ���ҳ�Ĳ��������༭ҳ
						Intent searchintent = new Intent(MainActivity.this,
								SearchActivity.class);
						searchintent.putExtras(bundle);// ���ز���
						startActivity(searchintent);// ������һactivity
					}
				});
		normalDia.setNegativeButton("ȡ��",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
		normalDia.create().show();
	}

}
