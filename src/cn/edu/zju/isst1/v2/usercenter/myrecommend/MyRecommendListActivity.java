package cn.edu.zju.isst1.v2.usercenter.myrecommend;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import cn.edu.zju.isst1.R;
import cn.edu.zju.isst1.ui.main.BaseActivity;

public class MyRecommendListActivity extends BaseActivity {

    MyRecommendListFragment listFragment = new MyRecommendListFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container_activity);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.content_container, listFragment).commit();
        }
        setUpActionBar();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my_recoomend_list, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                MyRecommendListActivity.this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void setUpActionBar() {
        super.setUpActionBar();
        setTitle(getResources().getString(R.string.my_recommend));
    }
}
