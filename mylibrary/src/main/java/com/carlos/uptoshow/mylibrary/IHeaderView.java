package com.carlos.uptoshow.mylibrary;

import android.view.View;

/**
 * Created by carlos on 2016/6/3.
 * 刷新头布局的接口
 */
public interface IHeaderView {
    /**
     * @return 返回头部View
     */
    View getView();

    /**
     * 超过下拉刷新阀值
     *
     * @param view 头部view
     */
    void outThreshold(View view);

    /**
     * 低于下拉刷新阀值
     *
     * @param view 头部view
     */
    void inThreshold(View view);

    /**
     * 开始下拉刷新
     *
     * @param view 头部view
     */
    void startRefreshing(View view);

    /**
     * 停止下拉刷新
     *
     * @param view 头部view
     */
    void stopRefreshing(View view);
}
