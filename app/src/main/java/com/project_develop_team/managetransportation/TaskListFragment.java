package com.project_develop_team.managetransportation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ogaclejapan.smarttablayout.SmartTabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;


public class TaskListFragment extends Fragment {

    @BindView(R.id.toolbar_title_task_list)
    TextView toolbarTitle;

    FragmentPagerAdapter pagerAdapter;

    @BindView(R.id.tabs)
    SmartTabLayout smartTabLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getContext().startService(new Intent(getContext(), LocationUpdateService.class));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);
        ButterKnife.bind(this, view);

        pagerAdapter = new FragmentPagerAdapter(getChildFragmentManager()) {
            private final Fragment[] fragments = new Fragment[]{
                    new TaskTypeAllFragment(),
                    new TaskTypeTodayFragment(),
                    new TaskTypeExpressFragment(),
                    new TaskTypeTomorrowFragment(),
                    new TaskTypeCompleteFragment(),
            };

            @Override
            public Fragment getItem(int position) {
                return fragments[position];
            }

            @Override
            public int getCount() {
                return fragments.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return getResources().getStringArray(R.array.tab_headings)[position];
            }
        };

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.container);
        viewPager.setAdapter(pagerAdapter);
        smartTabLayout.setViewPager(viewPager);

        return view;
    }
}