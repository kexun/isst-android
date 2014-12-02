package cn.edu.zju.isst1.v2.net;

import com.android.volley.toolbox.JsonRequest;

import org.json.JSONObject;

/**
 * Created by i308844 on 8/18/14.
 */
public class CSTNetworkEngine {

    private static CSTNetworkEngine INSTANCE;

    private CSTNetworkEngine() {

    }

    public static CSTNetworkEngine getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CSTNetworkEngine();
        }
        return INSTANCE;
    }

    public synchronized void requestJson(CSTRequest<JSONObject> jsonRequest) {
        VolleyImpl.requestJsonObject(jsonRequest, null);
    }

    public synchronized void requestCommon(JsonRequest<JSONObject> jsonRequest) {
        VolleyImpl.requestCommonObject(jsonRequest, null);
    }

}
