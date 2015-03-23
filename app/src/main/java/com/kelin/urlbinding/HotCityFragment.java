package com.kelin.urlbinding;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.kelin.library.base.BaseFragment;
import com.kelin.library.loader.OnLoadFinishedListener;
import com.kelin.library.data.JsonData;
import com.kelin.library.data.JsonListData;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * Created by kelin on 15-2-27.
 */
public class HotCityFragment extends BaseFragment {
    private PullToRefreshLayout mPullToRefreshLayout;
    private ListView mListView;

    public static HotCityFragment newInstance(String url, int layoutId) {
        HotCityFragment hotCityFragment = new HotCityFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putInt("layout_id", layoutId);
        hotCityFragment.setArguments(bundle);
        return hotCityFragment;
    }


    @Override
    public void onDataLoadedAndViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mListView = (ListView) view.findViewById(R.id.grid_view);
        mPullToRefreshLayout = (PullToRefreshLayout) view.findViewById(R.id.ptr_layout);
        ImageView imageView=new ImageView(getActivity());
        ActionBarPullToRefresh.from(this.getActivity())
                .allChildrenArePullable()
                .listener(new OnRefreshListener() {
                    @Override
                    public void onRefreshStarted(View view) {
                        mPullToRefreshLayout.setRefreshing(true);
                        loadData(mUrl, new OnLoadFinishedListener() {
                            @Override
                            public void onLoadFinished(JsonData jsonData, VolleyError volleyError) {
                                if (mJsonData != null) {
                                    mJsonData.getListDataHashMap().get("data_homepage").getJsonListItems().addAll(jsonData.getListDataHashMap().get("data_homepage").getJsonListItems());
                                    mPresentationModel.getPresentationModelChangeSupport().firePropertyChange("data_homepage");
                                    mPullToRefreshLayout.setRefreshComplete();
                                }
                            }
                        });

                    }
                })
                .setup(mPullToRefreshLayout);

    }

    @Override
    public void onDataLoadedFinish(JsonData data, VolleyError volleyError) {
        data.getJsonPrimary().add("visibility", true);
        Drawable drawable = getResources().getDrawable(R.drawable.ic_launcher);
        for (int i = 0; i < ((JsonListData) data.getList("data_homepage")).getSize(); i++) {
            ((JsonListData) data.getList("data_homepage")).get(i).add("src", drawable);
        }
        data.getJsonPrimary().add("background", drawable);
    }


    @Override
    protected Class eventPresentationModelClass() {
        return FunctionPresentationModel.class;
    }
}
