package com.unidw.album;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.AbsListView;
import android.widget.ListView;

import com.unidw.album.adapter.DefaultListAdapter;
import com.unidw.album.model.AlbumModel;
import com.unidw.album.util.CommonUtil;
import com.unidw.album.util.HttpHandler;
import com.unidw.album.util.LogUtil;
import com.unidw.album.util.WebUtil;

public class AlbumCommentActivity extends Activity {
    protected static final String TAG = AlbumCommentActivity.class.getSimpleName();

    private Context mContext;
    
    private ListView defaultList;
    private List<AlbumModel> list = new ArrayList<AlbumModel>();
    private DefaultListAdapter adapter ;
    
    private String androidId = "";
    private int pageIndex = 0;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mContext = this;
        
        defaultList = (ListView) findViewById(R.id.defaultList);
        // 设备id
        androidId = CommonUtil.getAndroidId(mContext);
//        // 加载设备积分信息
//        loadDeviceInfo(androidId);
        
        adapter = new DefaultListAdapter(mContext, list);
        defaultList.setAdapter(adapter);
        defaultList.setOnScrollListener(listener); 
        // 加载默认相册列表
        loadDefaultAlbumList(androidId, 0);
        
    }
    
    private AbsListView.OnScrollListener listener = new AbsListView.OnScrollListener() {    
        
        @Override    
        public void onScrollStateChanged(AbsListView view, int scrollState) {    
            if (view.getLastVisiblePosition() == view.getCount() - 1) {   
            	pageIndex++;
            	loadDefaultAlbumList(androidId, pageIndex);
            }    
        }    
    
        @Override    
        public void onScroll(AbsListView view, int firstVisibleItem,    
                int visibleItemCount, int totalItemCount) {    
    
        }    
    };   
    
    
    /**
     * 加载默认相册列表
     * @param androidId 设备id
     * @param pageIndex 页号
     */
    private void loadDefaultAlbumList(String androidId, int pageIndex) {
    	HttpHandler handler = new HttpHandler(mContext, true, false, "请稍侯", "信息加载中") {

			@Override
			protected void succeed(String jObject) {
				super.succeed(jObject);
				try {
					JSONObject result = new JSONObject(jObject);
					LogUtil.d(TAG, result.toString());
					JSONArray jsonArray = result.getJSONArray("albumList");
					if (jsonArray!=null && jsonArray.length()>0){
						for (int i=0; i<jsonArray.length(); i++){
							JSONObject obj = jsonArray.getJSONObject(i);
							AlbumModel albumModel = new AlbumModel();
							albumModel.setAlbumName(obj.getString("album_name"));
							albumModel.setAlbumThumbnailSource(obj.getString("album_cover"));
							list.add(albumModel);
						}
					}
					adapter.notifyDataSetChanged();
				} catch (JSONException e) {
					LogUtil.e(TAG, "loadDefaultAlbumList parse error", e);
				}
			}

			@Override
			protected void failed(String jObject) {
				super.failed(jObject);
				LogUtil.e(TAG, "loadDefaultAlbumList parse fault");
			}

		};

		try {
			WebUtil.instance(mContext, handler).loadDefaultAlbumList(androidId, pageIndex);
		} catch (Exception e) {
			LogUtil.e(TAG, "loadDefaultAlbumList request error");
		}
	}

	/**
     * 加载设备积分信息
     * @param androidId 设备 android_id
     */
    private void loadDeviceInfo(String androidId) {
		HttpHandler handler = new HttpHandler(mContext, true, false, "请稍侯", "信息加载中") {

			@Override
			protected void succeed(String jObject) {
				super.succeed(jObject);
				try {
					JSONObject result = new JSONObject(jObject);
					LogUtil.d(TAG, result.toString());
				} catch (JSONException e) {
					LogUtil.e(TAG, "load device info parse error", e);
				}
			}

			@Override
			protected void failed(String jObject) {
				super.failed(jObject);
				LogUtil.e(TAG, "load device info parse fault");
			}

		};

		try {
			WebUtil.instance(mContext, handler).loadDeviceInfo(androidId);
		} catch (Exception e) {
			LogUtil.e(TAG, "load device info request error");
		}
	}
}