/**
 *
 */
package cn.edu.zju.isst1.v2.usercenter.userinfo;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.edu.zju.isst1.R;
import cn.edu.zju.isst1.ui.main.BaseActivity;
import cn.edu.zju.isst1.util.CroMan;
import cn.edu.zju.isst1.v2.globaldata.citylist.CSTCity;
import cn.edu.zju.isst1.v2.globaldata.citylist.CSTCityDataDelegate;
import cn.edu.zju.isst1.v2.user.data.CSTUser;
import cn.edu.zju.isst1.v2.user.data.CSTUserDataDelegate;

/**
 * @author theasir
 */
public class UserInfoActivity extends BaseActivity {

    public static final int REQUEST_CODE_EDIT = 0x01;

    public static final int RESULT_CODE_DONE = 0x10;

    public static final int RESULT_CODE_CANCEL = 0x20;

    private final ViewHolder m_viewHolder = new ViewHolder();

    private CSTUser m_userCurrent;

    /*
     * (non-Javadoc)
     *
     * @see cn.edu.zju.isst1.ui.main.BaseActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info_activity);

        setUpActionBar();

        initComponent();

        initUser();

        bindData(m_userCurrent);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_info_activity_ab_menu, menu);
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                UserInfoActivity.this.finish();
                return true;
            case R.id.action_edit:
                Intent intent = new Intent(UserInfoActivity.this,
                        UserInfoEditActivity.class);
                intent.putExtra("currentUser", m_userCurrent);
                UserInfoActivity.this.startActivityForResult(intent,
                        REQUEST_CODE_EDIT);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onActivityResult(int, int,
     * android.content.Intent)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_EDIT:
                switch (resultCode) {
                    case RESULT_CODE_DONE:
                        CroMan.showConfirm(UserInfoActivity.this, "修改成功!");
                        m_userCurrent = data.hasExtra("updatedUser") ? (CSTUser) data
                                .getSerializableExtra("updatedUser") :
                                CSTUserDataDelegate.getCurrentUser(this);
                        bindData(m_userCurrent);
                        break;

                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void setUpActionBar() {
        super.setUpActionBar();
        setTitle(R.string.personal_setting);
    }

    private void initComponent() {
        m_viewHolder.avatarImgv = (ImageView) findViewById(
                R.id.user_info_activity_user_avatar_imgv);
        m_viewHolder.genderImgv = (ImageView) findViewById(R.id.user_info_activity_gender_imgv);
        m_viewHolder.cityTxv = (TextView) findViewById(R.id.user_info_activity_city_txv);
        m_viewHolder.signatureTxv = (TextView) findViewById(R.id.user_info_activity_signature_txv);
        m_viewHolder.nameTxv = (TextView) findViewById(R.id.user_info_activity_name_txv);
        m_viewHolder.usernameTxv = (TextView) findViewById(R.id.user_info_activity_username_txv);
        m_viewHolder.gradeTxv = (TextView) findViewById(R.id.user_info_activity_grade_txv);
        m_viewHolder.classTxv = (TextView) findViewById(R.id.user_info_activity_class_txv);
        m_viewHolder.majorTxv = (TextView) findViewById(R.id.user_info_activity_major_txv);
        m_viewHolder.phoneTxv = (TextView) findViewById(R.id.user_info_activity_phone_txv);
        m_viewHolder.emailTxv = (TextView) findViewById(R.id.user_info_activity_email_txv);
        m_viewHolder.qqTxv = (TextView) findViewById(R.id.user_info_activity_qq_txv);
        m_viewHolder.companyTxv = (TextView) findViewById(R.id.user_info_activity_company_txv);
        m_viewHolder.positionTxv = (TextView) findViewById(R.id.user_info_activity_position_txv);
    }

    private void initUser() {
        m_userCurrent = CSTUserDataDelegate.getCurrentUser(this);
    }

    private void bindData(CSTUser currentUser) {
//        m_viewHolder.genderImgv
//                .setImageResource(currentUser.gender.getKey() == 1 ? R.drawable.ic_male
//                        : R.drawable.ic_female);

        List<CSTCity> cityList = CSTCityDataDelegate.getCityList(this);
        String cityName = "";
        for (CSTCity city : cityList) {
            if (city.id == currentUser.cityId) {
                cityName = city.name;
            }
        }
        m_viewHolder.cityTxv.setText(cityName);
        m_viewHolder.signatureTxv.setText(currentUser.sign);
        m_viewHolder.nameTxv.setText(currentUser.name);
        m_viewHolder.usernameTxv.setText(currentUser.userName);
        m_viewHolder.gradeTxv.setText("" + currentUser.grade);
        m_viewHolder.classTxv.setText("" + currentUser.clazzId);
        m_viewHolder.majorTxv.setText(currentUser.majorName);
        m_viewHolder.phoneTxv.setText(currentUser.phoneNum);
        m_viewHolder.emailTxv.setText(currentUser.email);
        m_viewHolder.qqTxv.setText(currentUser.qqNum);
        m_viewHolder.companyTxv.setText(currentUser.company);
        m_viewHolder.positionTxv.setText(currentUser.jobTitle);
    }

    private class ViewHolder {

        public ImageView avatarImgv;

        public ImageView genderImgv;

        public TextView cityTxv;

        public TextView signatureTxv;

        public TextView nameTxv;

        public TextView usernameTxv;

        public TextView gradeTxv;

        public TextView classTxv;

        public TextView majorTxv;

        public TextView phoneTxv;

        public TextView emailTxv;

        public TextView qqTxv;

        public TextView companyTxv;

        public TextView positionTxv;
    }
}
