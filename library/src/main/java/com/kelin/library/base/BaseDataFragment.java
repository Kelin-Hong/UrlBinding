package com.kelin.library.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kelin.library.UrlBindingApp;
import com.kelin.library.utils.AbstractPresentationModelObjectGen;
import com.kelin.library.utils.JsonData;
import com.kelin.library.utils.PresentationModelGen;
import com.kelin.library.utils.UtilMethod;
import com.kelin.library.viewmodel.AbstractPresentationModelParent;
import com.kelin.library.viewmodel.PresentationModelParent;

import org.robobinding.ViewBinder;
import org.robobinding.binder.UrlBinderFactory;

/**
 * Created by kelin on 15-2-10.
 */
public class BaseDataFragment extends Fragment {
    private String mUrl;
    private int mLayoutId;
    private PresentationModelParent mPresentationModel;

    public static BaseDataFragment newInstance(String url, int layoutId) {
        BaseDataFragment baseDataFragment = new BaseDataFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putInt("layout_id", layoutId);
        baseDataFragment.setArguments(bundle);
        return baseDataFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUrl = getArguments().getString("url");
            mLayoutId = getArguments().getInt("layout_id");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewBinder viewBinder = null;
        try {
            JsonData jsonData = ((BaseFragment) getParentFragment()).getmJsonData();
            mPresentationModel = (PresentationModelParent) PresentationModelGen.generatePresentationModel(this.getActivity(), UtilMethod.getSha1String(mUrl), functionPresentationModel(), jsonData);
            mPresentationModel.setJsonData(jsonData);
            jsonData.setChangeSupport(mPresentationModel.getPresentationModelChangeSupport());
            AbstractPresentationModelParent abstractPresentationModel = AbstractPresentationModelObjectGen.generateAbstractPresentationModel(this.getActivity(), jsonData, mPresentationModel, functionPresentationModel());
            viewBinder = createViewBinder(abstractPresentationModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return viewBinder.inflateAndBindWithoutAttachingToRoot(mLayoutId, mPresentationModel, container);
    }

    protected Class functionPresentationModel() {
        return ((BaseFragment) getParentFragment()).functionPresentationModelClass();
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((BaseFragment) getParentFragment()).mPresentationModel = mPresentationModel;
        ((BaseFragment) getParentFragment()).onDataLoadedAndViewCreated(view, savedInstanceState);
    }

    protected ViewBinder createViewBinder(AbstractPresentationModelParent presentationModelObjectType) {
        UrlBinderFactory binderFactory = getReusableBinderFactory();
        return binderFactory.createViewBinder(getActivity(), presentationModelObjectType);
    }

    private UrlBinderFactory getReusableBinderFactory() {
        UrlBinderFactory binderFactory = getApp().getReusableBinderFactory();
        return binderFactory;
    }

    private UrlBindingApp getApp() {
        return (UrlBindingApp) getActivity().getApplicationContext();
    }


}
