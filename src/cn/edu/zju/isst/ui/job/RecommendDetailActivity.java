/**
 * 
 */
package cn.edu.zju.isst.ui.job;

import static cn.edu.zju.isst.constant.Constants.*;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import cn.edu.zju.isst.R;
import cn.edu.zju.isst.api.JobApi;
import cn.edu.zju.isst.db.Job;
import cn.edu.zju.isst.net.CSTResponse;
import cn.edu.zju.isst.net.RequestListener;
import cn.edu.zju.isst.ui.contact.ContactDetailActivity;
import cn.edu.zju.isst.ui.main.BaseActivity;
import cn.edu.zju.isst.constant.Constants;
import cn.edu.zju.isst.util.J;
import cn.edu.zju.isst.util.L;
import cn.edu.zju.isst.util.TimeString;

/**
 * 归档详情页
 * 
 * @author theasir
 * 
 *         TODO WIP
 */
public class RecommendDetailActivity extends BaseActivity {

	/**
	 * 归档id
	 */
	private int m_nId;

	private Job m_jobCurrent;
	private Handler m_handlerJobDetail;

	private TextView m_txvTitle;
	private TextView m_txvDate;
	private TextView m_txvPublisher;
	private WebView m_webvContent;
	private ImageView m_imgBtnPublisher;
	private Button m_BtnCommened;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v7.app.ActionBarActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.job_recommend_detail_activity);
		initComponent();

		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);

		// 注意默认值-1，当Intent中没有id时是无效的，故启动这个JobDetailActivity的Activity必须在Intent中放置"id"参数
		m_nId = getIntent().getIntExtra("id", -1);

		m_handlerJobDetail = new Handler() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see android.os.Handler#handleMessage(android.os.Message)
			 */
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case STATUS_REQUEST_SUCCESS:
					L.i("Handler Success Archieve id = " + m_jobCurrent.getId());
					showJobDetail();
					break;
				case STATUS_NOT_LOGIN:
					break;
				default:
					break;
				}
			}

		};

		JobApi.getJobDetail(m_nId, new RequestListener() {

			@Override
			public void onComplete(Object result) {
				Message msg = m_handlerJobDetail.obtainMessage();

				try {
					JSONObject jsonObject = (JSONObject) result;
					if (!J.isValidJsonValue("status", jsonObject)) {
						return;
					}
					final int status = jsonObject.getInt("status");
					switch (status) {
					case STATUS_REQUEST_SUCCESS:
						if (!J.isValidJsonValue("status", jsonObject)) {
							break;
						}
						m_jobCurrent = new Job(jsonObject.getJSONObject("body"));
						break;
					case STATUS_NOT_LOGIN:
						break;
					default:
						break;
					}
					msg.what = status;
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				m_handlerJobDetail.sendMessage(msg);

			}

			@Override
			public void onHttpError(CSTResponse response) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onException(Exception e) {
				// m_jobCurrent = new Job(null);
				L.i("JobDetailActivity onError : id = " + m_nId + "!");
				if (L.isDebuggable()) {
					e.printStackTrace();
				}

			}

		});

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home: {
			// Intent intentParent = new Intent(JobDetailActivity.this,
			// MainActivity.class);
			// intentParent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			// JobDetailActivity.this.startActivity(intentParent);
			RecommendDetailActivity.this.finish();
			return true;
		}

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * 初始化组件
	 */
	private void initComponent() {
		m_txvTitle = (TextView) findViewById(R.id.job_detail_activity_title_txv);
		m_txvDate = (TextView) findViewById(R.id.job_detail_activity_date_txv);
		m_txvPublisher = (TextView) findViewById(R.id.job_detail_activity_publisher_txv);
		m_webvContent = (WebView) findViewById(R.id.job_detail_activity_content_webv);
		m_imgBtnPublisher = (ImageButton) findViewById(R.id.job_detail_activity_publisher_btn);
		m_BtnCommened = (Button) findViewById(R.id.job_recommend_imgbtn);
		
		m_imgBtnPublisher.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(RecommendDetailActivity.this,
						ContactDetailActivity.class);
				int id = -1;
				if (!J.isNullOrEmpty(m_jobCurrent)) {
					id = m_jobCurrent.getPublisherId();
				}
				intent.putExtra("id", id);
				startActivity(intent);
			}
		});
		
		m_BtnCommened.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(RecommendDetailActivity.this,
						JobCommentListActivity.class);
				int id = -1;
				if (!J.isNullOrEmpty(m_jobCurrent)) {
					id = m_jobCurrent.getId();
				}
				intent.putExtra("id", id);
				startActivity(intent);			}
		});
		WebSettings settings = m_webvContent.getSettings();
		settings.setUseWideViewPort(true);
		settings.setLoadWithOverviewMode(true);
		settings.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
		settings.setDefaultFontSize(48);
		// settings.setTextSize(TextSize.NORMAL);
	}

	/**
	 * 绑定数据并显示
	 */
	private void showJobDetail() {
		if (J.isNullOrEmpty(m_jobCurrent)) {
			return;
		}
		m_txvTitle.setText(m_jobCurrent.getTitle());
		m_txvDate.setText(TimeString.toFull(m_jobCurrent.getUpdatedAt()));
		m_txvPublisher.setText(PUBLISHER_NAME + m_jobCurrent.getPublisherId()
				+ " " + m_jobCurrent.getPublisher().getName());
		m_webvContent.loadDataWithBaseURL(null, m_jobCurrent.getContent(),
				"text/html", "utf-8", null);

	}

}