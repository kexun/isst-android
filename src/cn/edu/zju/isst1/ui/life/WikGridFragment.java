/**
 *
 */
package cn.edu.zju.isst1.ui.life;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.edu.zju.isst1.R;
import cn.edu.zju.isst1.db.Archive;
import cn.edu.zju.isst1.db.DataManager;
import cn.edu.zju.isst1.exception.ExceptionWeeder;
import cn.edu.zju.isst1.exception.HttpErrorWeeder;
import cn.edu.zju.isst1.net.CSTResponse;
import cn.edu.zju.isst1.net.NetworkConnection;
import cn.edu.zju.isst1.net.RequestListener;
import cn.edu.zju.isst1.util.Judge;
import cn.edu.zju.isst1.util.Lgr;
import cn.edu.zju.isst1.util.TSUtil;
import cn.edu.zju.isst1.v2.archive.gui.ArchiveDetailActivity;
import cn.edu.zju.isst1.v2.archive.net.ArchiveApi;
import pulltorefresh.widget.XListView;

import static cn.edu.zju.isst1.constant.Constants.NETWORK_NOT_CONNECTED;
import static cn.edu.zju.isst1.constant.Constants.STATUS_NOT_LOGIN;
import static cn.edu.zju.isst1.constant.Constants.STATUS_REQUEST_SUCCESS;

/**
 * 百科列表页
 *
 * @author yyy
 *         <p/>
 *         TODO WIP
 */
public class WikGridFragment extends Fragment implements XListView.IXListViewListener {

    private static WikGridFragment INSTANCE = new WikGridFragment();

    private final List<Archive> m_listAchive = new ArrayList<Archive>();

    private int m_nVisibleLastIndex;

    private Handler m_handlerWikiList;

    private WikiListAdapter m_adapterWikiList;

//    private SwipeRefreshLayout mSwipeRefreshLayout;

    private XListView mListView;

    private Boolean IS_FIRST = true;

    private int mCurrentPage = 1;

    private Handler rHandler;

    public WikGridFragment() {
    }

    public static WikGridFragment getInstance() {
        return INSTANCE;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        rHandler = new Handler();
        setHasOptionsMenu(true);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * android.support.v4.app.ListFragment#onCreateView(android.view.LayoutInflater
     * , android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.wiki_grid_fragment, null);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.ListFragment#onViewCreated(android.view.View,
     * android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onViewCreated(view, savedInstanceState);

        mListView = (XListView) view.findViewById(R.id.wiki_grid_fragment_wiki_gridv);

//        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
//        mSwipeRefreshLayout.setColorScheme(R.color.deepskyblue, R.color.darkorange, R.color.darkviolet,
//                R.color.lightcoral);

        initWikiList();
        initHandler();
        setUpListener();


        m_adapterWikiList = new WikiListAdapter(getActivity());

        // setListAdapter(m_adapterWikiList);
        mListView.setAdapter(m_adapterWikiList);

//        if (m_listAchive.size() == 0) {
//            requestData(LoadType.REFRESH);
//        }

        if (IS_FIRST) {
            requestData(LoadType.REFRESH);
//            mSwipeRefreshLayout.setRefreshing(true);
            IS_FIRST = false;
        }

        // 监听事件
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                Lgr.i(this.getClass().getName() + " onListItemClick postion = ");
                Intent intent = new Intent(getActivity(),
                        ArchiveDetailActivity.class);
                intent.putExtra("id", m_listAchive.get(arg2 - 1).getId());
                getActivity().startActivity(intent);
            }
        });

//        mSwipeRefreshLayout.setOnRefreshListener(this);

    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        // requestData(LoadType.REFRESH);

    }

    private void initHandler(){
        m_handlerWikiList = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case STATUS_REQUEST_SUCCESS:
//                        mSwipeRefreshLayout.setRefreshing(false);
                        m_adapterWikiList.notifyDataSetChanged();
                        break;
                    case STATUS_NOT_LOGIN:
                        break;
                    default:
                        break;
                }
            }

        };
    }

    /**
     * 初始化百科列表，若有缓存则读取缓存
     */
    private void initWikiList() {

        List<Archive> dbWikiList = DataManager
                .getCurrentWikiList();
        if (!m_listAchive.isEmpty()) {
            m_listAchive.clear();
        }
        if (!Judge.isNullOrEmpty(dbWikiList)) {
            for (Archive wiki : dbWikiList) {
                m_listAchive.add(wiki);
            }
        }
    }

    /**
     * 刷新列表
     *
     * @param jsonObject 数据源
     */
    private void refresh(JSONObject jsonObject) {
        if (!m_listAchive.isEmpty()) {
            m_listAchive.clear();
        }
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("body");
            for (int i = 0; i < jsonArray.length(); i++) {
                m_listAchive.add(new Archive((JSONObject) jsonArray.get(i)));
            }
            Lgr.i(this.getClass().getName() + " refreshList: "
                    + "Added archives to wikiList!");
            DataManager.syncWikiList(m_listAchive);
        } catch (JSONException e) {
            Lgr.i(this.getClass().getName() + " refreshList!");
            e.printStackTrace();
        }
    }

    /**
     * 加载更多
     *
     * @param jsonObject 数据源
     */
    private void loadMore(JSONObject jsonObject) {
        JSONArray jsonArray;
        try {
            jsonArray = jsonObject.getJSONArray("body");
            for (int i = 0; i < jsonArray.length(); i++) {
                m_listAchive.add(new Archive((JSONObject) jsonArray.get(i)));
            }
            Lgr.i(this.getClass().getName() + " loadMore: "
                    + "Added archives to wikiList!");
        } catch (JSONException e) {
            Lgr.i(this.getClass().getName() + " loadMore!");
            e.printStackTrace();
        }
    }

    /**
     * 请求数据
     *
     * @param type 加载方式
     */
    private void requestData(LoadType type) {
        if (NetworkConnection.isNetworkConnected(getActivity())) {
            switch (type) {// TODO 刷新策略
                case REFRESH:
                    // 设置刷新策略，一次性加载最新若干条
                    ArchiveApi.getWikiList(1, 20, "",
                            new WikiListRequestListener(type));
                    mCurrentPage = 1;
                    break;
                case LOADMORE:
                    ArchiveApi.getWikiList(++mCurrentPage, 20, "",
                            new WikiListRequestListener(type));
                    break;
                default:
                    break;
            }
        } else {
            Message msg = m_handlerWikiList.obtainMessage();
            msg.what = NETWORK_NOT_CONNECTED;
            m_handlerWikiList.sendMessage(msg);
        }
    }

    @Override
    public void onRefresh() {
        mListView.setPullLoadEnable(true);
        rHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                requestData(LoadType.REFRESH);
                onLoad();
            }
        }, 1000);
    }

    @Override
    public void onLoadMore() {
        rHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                requestData(LoadType.LOADMORE);
                onLoad();
            }
        }, 1000);
    }

    private void onLoad() {
        mListView.stopRefresh();
        mListView.stopLoadMore();
        mListView.setRefreshTime(TSUtil.getTime());
    }

    /**
     * 加载方式枚举类
     *
     * @author yyy
     */
    private enum LoadType {
        REFRESH, LOADMORE;
    }

    /**
     * 百科列表RequestListener类
     *
     * @author yyy
     */
    private class WikiListRequestListener implements RequestListener {

        private LoadType type;

        public WikiListRequestListener(LoadType type) {
            this.type = type;
        }

        @Override
        public void onComplete(Object result) {
            Message msg = m_handlerWikiList.obtainMessage();
            try {
                msg.what = ((JSONObject) result).getInt("status");
                switch (type) {
                    case REFRESH:
                        refresh((JSONObject) result);
                        break;
                    case LOADMORE:
                        loadMore((JSONObject) result);
                        break;
                    default:
                        break;
                }
            } catch (JSONException e) {
                Lgr.i(this.getClass().getName() + " onComplete!");
                e.printStackTrace();
            }

            m_handlerWikiList.sendMessage(msg);
//            mSwipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onHttpError(CSTResponse response) {
            Lgr.i(this.getClass().getName() + " onHttpError!");
            Message msg = m_handlerWikiList.obtainMessage();
            HttpErrorWeeder.fckHttpError(response, msg);
            m_handlerWikiList.sendMessage(msg);
        }

        @Override
        public void onException(Exception e) {
            Lgr.i(this.getClass().getName() + " onException!");
            Message msg = m_handlerWikiList.obtainMessage();
            ExceptionWeeder.fckException(e, msg);
            m_handlerWikiList.sendMessage(msg);
        }

    }

    protected void setUpListener() {
//        mSwipeRefreshLayout.setOnRefreshListener(this);
//        mSwipeRefreshLayout.setRefreshing(false);
//        m_lsvJobList.setOnScrollListener(this);
//        mListView.setOnScrollListener(this);
        mListView.setPullRefreshEnable(true);
        mListView.setPullLoadEnable(true);
        mListView.setAutoLoadEnable(true);
        mListView.setXListViewListener(this);
        mListView.setRefreshTime(TSUtil.getTime());
    }

    /**
     * View容器类
     *
     * @author yyy
     */
    private final class ViewHolder {

        public TextView titleTxv;

        public TextView dateTxv;

        public TextView publisherTxv;

        public TextView descriptionTxv;

        public View indicatorView;
    }

    /**
     * 新闻列表自定义适配器类
     *
     * @author yyy
     */
    private class WikiListAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        public WikiListAdapter(Context context) {
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return m_listAchive.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();

                convertView = inflater.inflate(R.layout.wiki_grid_item, null);
                // convertView.setLayoutParams(new GridView.LayoutParams(85,
                // 85));
                holder.titleTxv = (TextView) convertView
                        .findViewById(R.id.wiki_grid_item_title_txv);
                holder.dateTxv = (TextView) convertView
                        .findViewById(R.id.wiki_grid_item_date_txv);
                holder.publisherTxv = (TextView) convertView
                        .findViewById(R.id.wiki_grid_item_publisher_txv);
//                holder.descriptionTxv = (TextView) convertView
//                        .findViewById(R.id.wiki_grid_item_description_txv);
//                holder.indicatorView = (View) convertView
//                        .findViewById(R.id.wiki_grid_item_indicator_view);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.titleTxv.setText(m_listAchive.get(position).getTitle());
            holder.dateTxv.setText(TSUtil.toYMD(m_listAchive.get(position)
                    .getUpdatedAt()));
            holder.publisherTxv.setText(m_listAchive.get(position)
                    .getPublisher().getName());
//            holder.descriptionTxv.setText(m_listAchive.get(position)
//                    .getDescription());
            // holder.indicatorView.setBackgroundColor(Color.BLUE);

            return convertView;
        }

    }

}
