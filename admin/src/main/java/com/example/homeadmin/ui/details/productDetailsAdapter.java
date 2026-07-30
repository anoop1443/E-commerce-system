package com.example.homeadmin.ui.details;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;


public class productDetailsAdapter extends FragmentPagerAdapter {

    int totalTabs;
    private String productDescription;
    private String productMoreInfo;
    private List<productSpecificationModel> productSpecificationModelList;

    public productDetailsAdapter(@NonNull FragmentManager fm, int totalTabs, String productDescription, List<productSpecificationModel> productSpecificationModelList , String productMoreInfo) {
        super(fm);
        this.totalTabs = totalTabs;
        this.productDescription = productDescription;
        this.productSpecificationModelList = productSpecificationModelList;
        this.productMoreInfo = productMoreInfo;

    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                productDecriptionFragment productDecriptionFragment = new productDecriptionFragment();
                productDecriptionFragment.body = productDescription;
                return productDecriptionFragment;

            case 1:
                productSpecificationFragment productSpecificationFragment = new productSpecificationFragment();
                productSpecificationFragment.productSpecificationModelListF = productSpecificationModelList;
                return productSpecificationFragment;

            case 2:
                productMoreInfoFragment productMoreInfoFragment = new productMoreInfoFragment();
                productMoreInfoFragment.productSpecificationModelList1 = productSpecificationModelList;
                productMoreInfoFragment.body = productMoreInfo;
                return productMoreInfoFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
