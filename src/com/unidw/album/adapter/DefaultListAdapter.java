package com.unidw.album.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.unidw.album.R;
import com.unidw.album.model.AlbumModel;
import com.unidw.album.util.AsyncImageLoader;
import com.unidw.album.util.LogUtil;
import com.unidw.album.util.AsyncImageLoader.ImageCallback;

public class DefaultListAdapter extends BaseAdapter{

	private List<AlbumModel> data = new ArrayList<AlbumModel>();
	
	private Context mContext;
	
	private AsyncImageLoader asyncImageLoader = new AsyncImageLoader();
	
	public DefaultListAdapter(Context context, List<AlbumModel> list){
		mContext = context;
		data = list;
	}
	
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LogUtil.d("-----", "getView");
		LayoutInflater inflate = ((Activity) mContext).getLayoutInflater();
		View view = (View) inflate.inflate(R.layout.main_list, null);
		TextView title = (TextView) view.findViewById(R.id.listtitle);
		TextView text = (TextView) view.findViewById(R.id.listtext);
		ImageView img = (ImageView) view.findViewById(R.id.img);
		AlbumModel albumModel = data.get(position);
		title.setText(albumModel.getAlbumName());
		Drawable cachedImage = asyncImageLoader.loadDrawable("http://192.168.13.100/CodeIgniter/attach" + albumModel.getAlbumThumbnailSource(), img, new ImageCallback(){
            
            public void imageLoaded(Drawable imageDrawable,ImageView imageView, String imageUrl) {
                imageView.setImageDrawable(imageDrawable);
            }
        });
		img.setImageDrawable(cachedImage);
		return view;
	}

}
