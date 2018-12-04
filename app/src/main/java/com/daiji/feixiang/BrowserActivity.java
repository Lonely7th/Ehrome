package com.daiji.feixiang;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.daiji.feixiang.adapter.LongClickAdapter;
import com.daiji.feixiang.adapter.SetUpAdapter;
import com.daiji.feixiang.adapter.ViewUrlAdapter;
import com.daiji.feixiang.adapter.WindowsAdapter;
import com.daiji.feixiang.bean.LongClickBean;
import com.daiji.feixiang.bean.SetUpBean;
import com.daiji.feixiang.bean.ViewUrlBean;
import com.daiji.feixiang.common.Common;
import com.daiji.feixiang.common.Constant;
import com.daiji.feixiang.common.Request;
import com.daiji.feixiang.dao.AdDao;
import com.daiji.feixiang.dao.BrowserBookMarkDao;
import com.daiji.feixiang.dao.BrowserHistoryDao;
import com.daiji.feixiang.dao.UpdateDao;
import com.daiji.feixiang.holder.MyBaseViewHolder;
import com.daiji.feixiang.holder.WindowsViewHolder;
import com.daiji.feixiang.receiver.DownloadCompleteReceiver;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class BrowserActivity extends AppCompatActivity {
    private EditText et_search;
    private WebView webview;
    private String searchStr;
    private Toolbar myToolbar;
    private String home_url = "https://www.baidu.com/";
    private BrowserHistoryDao browserHistoryDao;
    private int back_num = 0;

    private MyHandler myHandler = new MyHandler();
    private BottomNavigationView navigation;
    private LinearLayout ll_root;
    private PopupWindow popupWindow;
    private ArrayList<SetUpBean> setupList;
    private ImageView iv_lock;
    private PopupWindow safePop;
    private ImageView iv_refresh;
    private boolean ivRefreshing;
    private BrowserBookMarkDao browserBookMarkDao;
    private ArrayList<WebView> webViews;
    private MyWindowsAdapter myWindowsAdapter;
    private FrameLayout fl_webview;
    private PopupWindow popWebView;

    private boolean isSafe = true;
    private ImageView iv_add;
    private boolean updateHeader = false;
    private boolean updateLockHeader = false;
    private boolean updateSearchHeader = false;
    private List<String> adUrls = new ArrayList<>();
    private List<String> jsUrls = new ArrayList<>();
    private ValueCallback<Uri[]> mFilePathCallback;
    private ValueCallback<Uri> mFilePathCallback41;
    private DownloadCompleteReceiver downloadCompleteReceiver;
    private AdDao adDao;
    private float ll_height;
    private PopupWindow popLongView;
    private static Activity mActivity;
    private LongClickBean viewBigPictureBean;
    private LongClickBean savePictureBean;
    private LongClickBean copyUrlBean;
    private LongClickBean newWindowOpenBean;
    private ClipboardManager clipboardManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        initUi();
        initToolbar();
        initMenu();
        initSearch();
        initLock();
        initBroadCastReceive();
        search(getIntent());
        initAdDao();
        initRoot();
        initUpdate();
        initLongClickBean();
    }

    private void initLongClickBean() {
        viewBigPictureBean = new LongClickBean(1, getString(R.string.view_picture));
        savePictureBean = new LongClickBean(2, getString(R.string.save_picture));
        copyUrlBean = new LongClickBean(3, getString(R.string.copy_url));
        newWindowOpenBean = new LongClickBean(4, getString(R.string.new_window_open));
    }

    private void initUpdate() {
        UpdateDao.update(BrowserActivity.this);
    }

    private void initRoot() {
        ll_root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (ll_height > 0) {
                    if (ll_height - ll_root.getHeight() < 200) {
                        //键盘隐藏
                        navigation.setVisibility(View.VISIBLE);
                    } else {
                        //键盘显示
                        navigation.setVisibility(View.GONE);
                    }
                } else {
                    ll_height = ll_root.getHeight();
                }
            }
        });
    }

    private void initAdDao() {
        adDao = new AdDao();
    }

    private void initBroadCastReceive() {
        downloadCompleteReceiver = new DownloadCompleteReceiver();
        registerReceiver(downloadCompleteReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    /**
     * 搜索、新链接在此打开
     *
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        search(intent);
    }

    /**
     * 搜索、打开链接
     *
     * @param intent
     */
    private void search(Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                if (action.equals(Intent.ACTION_VIEW)) {
                    String url = intent.getDataString();
                    //网页搜索
                    String q = intent.getData().getQueryParameter("q");
                    if (q != null) {
                        searchQ(q);
                        return;
                    }
                    if (url != null && url.startsWith("http")) {
                        if (webview != null) {
                            webview.loadUrl(url);
                        }
                    }
                }
            }
        }
    }

    /**
     * 更新头部
     *
     * @param url
     * @param title
     */
    private void resetHeader(String url, String title) {
        if (updateHeader) {
            return;
        }
        if (!updateLockHeader) {
            if (url != null && !TextUtils.isEmpty(url)) {
                initToolbarHeader(url);
                updateLockHeader = true;
            }
        }

        if (!updateSearchHeader) {
            if (title != null && !TextUtils.isEmpty(title)) {
                setSearchText(title, url);
                updateSearchHeader = true;
            }
        }

        if (updateLockHeader && updateSearchHeader) {
            updateHeader = true;
        }
    }

    /**
     * 设置状态锁
     *
     * @param url
     */
    private void initToolbarHeader(String url) {
        if (url == null) {
            return;
        }
        //检测网址安全
        if (url.startsWith("https")) {
            //安全
            if (isSafe) {
                iv_lock.setColorFilter(Color.parseColor(getString(R.string.safecolor)));
                iv_lock.setImageResource(R.drawable.ic_lock_outline_black_48dp);
            } else {
                iv_lock.setColorFilter(Color.parseColor(getString(R.string.unsafecolor)));
                iv_lock.setImageResource(R.drawable.ic_error_outline_black_48dp);
            }
        } else if (url.startsWith("http")) {
            //不安全
            iv_lock.setColorFilter(Color.parseColor(getString(R.string.unsafecolor)));
            iv_lock.setImageResource(R.drawable.ic_warning_black_48dp);
        } else {
            iv_lock.setColorFilter(Color.parseColor(getString(R.string.defaultcolor)));
            iv_lock.setImageResource(R.drawable.ic_error_outline_black_48dp);
        }
    }

    /**
     * toolbar安全锁
     */
    private void initLock() {
        iv_lock = findViewById(R.id.iv_lock);

        iv_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View safeView = View.inflate(getApplicationContext(), R.layout.popwindow_safe, null);
                TextView tv_desc = safeView.findViewById(R.id.tv_desc);
                TextView tv_title = safeView.findViewById(R.id.tv_title);
                TextView tv_url = safeView.findViewById(R.id.tv_url);
                tv_url.setText(webview.getUrl());
                if (webview.getUrl().startsWith("https")) {
                    if (isSafe) {
                        tv_title.setText(R.string.tip_safe_title);
                        tv_title.setTextColor(Color.parseColor(getString(R.string.safecolor)));
                        tv_desc.setText(R.string.tip_safe_desc);
                    } else {
                        tv_title.setText(R.string.tip_unsafe2_title);
                        tv_title.setTextColor(Color.parseColor(getString(R.string.unsafecolor)));
                        tv_desc.setText(R.string.tip_unsafe2_desc);
                    }

                } else if (webview.getUrl().startsWith("http")) {
                    tv_title.setText(R.string.tip_unsafe_title);
                    tv_title.setTextColor(Color.parseColor(getString(R.string.unsafecolor)));
                    tv_desc.setText(R.string.tip_unsafe_desc);
                } else {
                    return;
                }
                safePop = new PopupWindow(safeView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                safePop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                safePop.setOutsideTouchable(false);
                safePop.setFocusable(true);
                safePop.showAsDropDown(iv_lock, 10, 20);
            }
        });

        //刷新
        iv_refresh = findViewById(R.id.iv_refresh);
        iv_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webview != null) {
                    if (ivRefreshing) {
                        webview.stopLoading();
                    } else {
                        webview.reload();
                    }
                }
            }
        });

        //更多
        ImageView iv_more = findViewById(R.id.iv_more);
        iv_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(BrowserActivity.this);
                builder.setItems(R.array.more, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                //设置为主页
                                if (webview != null) {
                                    Common.setPreferences(getApplicationContext(), Constant.HOME_URL, webview.getUrl());
                                    Toast.makeText(getApplicationContext(), R.string.set_success, Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case 1:
                                //添加至书签
                                if (webview != null) {
                                    browserBookMarkDao = new BrowserBookMarkDao();
                                    browserBookMarkDao.add(webview.getUrl(), webview.getTitle());
                                    Toast.makeText(getApplicationContext(), R.string.add_success, Toast.LENGTH_SHORT).show();
                                }
                                break;

                            case 2:
                                //拦截Url
                                interceptUrl();
                                break;
                            case 3:
                                //拦截JavaScript
                                interceptJs();
                                break;
                            case 4:
                                //拦截此网站
                                interceptNowUrl();
                                break;
                            case 5:
                                //查看此网站
                                viewCurrentUrl();
                                break;
                            case 6:
                                //复制当前页面链接
                                copyUrl(webview.getUrl());
                                break;
                            case 7:
                                //分享此网页
                                shareUrl();
                                break;
                        }
                        dialog.dismiss();
                    }
                }).setTitle(R.string.choose).setIcon(R.drawable.app).create().show();
            }
        });
    }

    /**
     * 查看此网页
     */
    private void viewCurrentUrl() {
        final String cur_url = webview.getUrl();
        if (TextUtils.isEmpty(cur_url)) {
            Common.alert(getString(R.string.view_fail));
            return;
        }
        View view = View.inflate(getApplicationContext(), R.layout.popwindow_url, null);
        ListView lv_url = view.findViewById(R.id.lv_url);
        final ArrayList<ViewUrlBean> viewUrlBeans = new ArrayList<>();
        final ViewUrlAdapter viewUrlAdapter = new ViewUrlAdapter(viewUrlBeans);
        lv_url.setAdapter(viewUrlAdapter);
        new Thread() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                try {
                    URL u = new URL(cur_url);
                    if (!cur_url.startsWith("https")) {
                        conn = (HttpURLConnection) u.openConnection();
                    } else {
                        conn = (HttpsURLConnection) u.openConnection();
                    }
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(3000);
                    conn.setReadTimeout(5000);
                    conn.connect();

                    InetSocketAddress inetSocketAddress = new InetSocketAddress(Common.getHost(cur_url), 80);
                    InetAddress address = inetSocketAddress.getAddress();
                    String address2 = address == null ? getString(R.string.unknow) : address.toString().split("/")[1];
                    viewUrlBeans.add(new ViewUrlBean("IP（80端口）：" + address2));

                    inetSocketAddress = new InetSocketAddress(Common.getHost(cur_url), 443);
                    address = inetSocketAddress.getAddress();
                    address2 = address == null ? getString(R.string.unknow) : address.toString().split("/")[1];
                    viewUrlBeans.add(new ViewUrlBean("IP（443端口）：" + address2));

                    String contentType = conn.getHeaderField("Content-Type");
                    contentType = contentType == null ? getString(R.string.unknow) : contentType;
                    viewUrlBeans.add(new ViewUrlBean("网页类型：" + contentType));

                    String server = conn.getHeaderField("Server");
                    server = server == null ? getString(R.string.unknow) : server;
                    viewUrlBeans.add(new ViewUrlBean("服务器：" + server));

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        viewUrlAdapter.notifyDataSetChanged();
                    }
                });
            }
        }.start();

        PopupWindow popUrl = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popUrl.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popUrl.setOutsideTouchable(false);
        popUrl.setFocusable(true);
        popUrl.showAtLocation(ll_root, Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    /**
     * 分享网页
     */
    private void shareUrl() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, webview.getTitle());
        intent.putExtra(Intent.EXTRA_TEXT, webview.getUrl());
        startActivity(Intent.createChooser(intent, getString(R.string.share_url)));
    }

    //拦截此网页
    private void interceptNowUrl() {
        if (Common.getPreferences(getApplicationContext(), Constant.AD, false)) {
            final String current_url = Common.getHost(webview.getUrl());
            if (TextUtils.isEmpty(current_url)) {
                Common.alert(getString(R.string.intercept_url_fail));
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(BrowserActivity.this);
            builder.setIcon(R.drawable.app)
                    .setTitle(R.string.intercept_url)
                    .setMessage("您即将拦截：" + current_url + " 网页下的所有资源（包括图片、js等），拦截之后该网页将无法访问。")
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            adDao.add(current_url, 1);
                            Common.alert(getString(R.string.intercept_url_add));
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();
        } else {
            Common.alert(getString(R.string.open_url_intercept));
        }
    }

    /**
     * 拦截js
     */
    private void interceptJs() {
        if (Common.getPreferences(getApplicationContext(), Constant.JS, false)) {
            if (jsUrls.size() > 0) {
                String[] ads = new String[jsUrls.size()];
                jsUrls.toArray(ads);
                AlertDialog.Builder builder = new AlertDialog.Builder(BrowserActivity.this);
                builder.setTitle(R.string.js_intercept)
                        .setIcon(R.drawable.app)
                        .setMultiChoiceItems(ads, null, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, final int which, final boolean isChecked) {
                                new Thread() {
                                    @Override
                                    public void run() {
                                        String js = jsUrls.get(which).substring(0, jsUrls.get(which).lastIndexOf(".js") + 3);
                                        if (isChecked) {
                                            //添加
                                            adDao.add(js, 2);
                                        } else {
                                            //删除
                                            adDao.remove(js, 2);
                                        }

                                    }
                                }.start();
                            }
                        })
                        .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
            } else {
                Toast.makeText(getApplicationContext(), R.string.no_intercept, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), R.string.open_js_intercept, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 拦截url
     */
    private void interceptUrl() {
        if (Common.getPreferences(getApplicationContext(), Constant.AD, false)) {
            if (adUrls.size() > 0) {
                String[] ads = new String[adUrls.size()];
                adUrls.toArray(ads);
                AlertDialog.Builder builder = new AlertDialog.Builder(BrowserActivity.this);
                builder.setTitle(R.string.url_intercept)
                        .setIcon(R.drawable.app)
                        .setMultiChoiceItems(ads, null, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, final int which, final boolean isChecked) {
                                new Thread() {
                                    @Override
                                    public void run() {
                                        String u = adUrls.get(which);
                                        if (isChecked) {
                                            //添加
                                            adDao.add(u, 1);
                                        } else {
                                            //删除
                                            adDao.remove(u, 1);
                                        }

                                    }
                                }.start();
                            }
                        })
                        .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
            } else {
                Toast.makeText(getApplicationContext(), R.string.no_intercept, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), R.string.open_url_intercept, Toast.LENGTH_SHORT).show();
        }
    }

    private void searchQ(WebView webView, String searchStr) {
        if (searchStr.startsWith("http")) {
            webView.loadUrl(searchStr);
            return;
        }
        //搜索引擎
        int ck = Common.getPreferences(getApplicationContext(), Constant.SEARCH_SET, 0);
        switch (ck) {
            case 0:
                webView.loadUrl("https://www.baidu.com/s?wd=" + searchStr);
                break;
            case 1:
                webView.loadUrl("https://m.sm.cn/s?q=" + searchStr);
                break;
            case 2:
                webView.loadUrl("https://m.so.com/index.php?ie=utf-8&q=" + searchStr);
                break;
            case 3:
                webView.loadUrl("https://m.sogou.com/web/searchList.jsp?keyword=" + searchStr);
                break;
            case 4:
                webView.loadUrl("http://m.chinaso.com/page/search.htm?keys=" + searchStr);
                break;
            case 5:
                webView.loadUrl("http://cn.bing.com/search?q=" + searchStr);
                break;
        }
    }

    private void searchQ(String searchStr) {
        searchQ(webview, searchStr);
    }

    /**
     * 顶部输入框
     */
    private void initSearch() {
        et_search = findViewById(R.id.et_search);
        et_search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    searchStr = et_search.getText().toString().trim();
                    if (!searchStr.equals("")) {
                        if (searchStr.startsWith("http")) {
                            webview.loadUrl(searchStr);
                        } else {
                            //搜索引擎
                            searchQ(searchStr);
                        }
                    }
                    Common.hideKeyboard(getApplicationContext(), BrowserActivity.this);
                } else {
                    if (webview != null) {
                        if (Common.getPreferences(getApplicationContext(), Constant.SHOW_SEARCH, 1) == 0) {
                            et_search.setText(webview.getTitle());
                            et_search.setSelection(webview.getTitle().length());
                        } else {
                            et_search.setText(webview.getUrl());
                            et_search.setSelection(webview.getUrl().length());
                        }
                    } else {
                        et_search.setText("");
                    }
                }
            }
        });
    }

    private void initMenu() {
        navigation = findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_home);
        //菜单事件
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_back:
                        //后退
                        checkCanBack();
                        return true;

                    case R.id.navigation_forward:
                        //前进
                        if (webview.canGoForward()) {
                            webview.goForward();
                        }
                        return true;
                    case R.id.navigation_set:
                        //设置
                        openSetting();
                        return true;
                    case R.id.navigation_window:
                        //窗口
                        initWindows();
                        return true;

                    case R.id.navigation_home:
                        //主页
                        webview.loadUrl(Common.getPreferences(getApplicationContext(), Constant.HOME_URL, home_url));
                        return true;
                }
                return false;
            }
        });
    }

    //多窗口
    private void initWindows() {
        if (!Common.getPreferences(getApplicationContext(), Constant.MULWIN, true)) {
            Toast.makeText(getApplicationContext(), R.string.open_multiwindow, Toast.LENGTH_SHORT).show();
            return;
        }
        View view1 = getLayoutInflater().inflate(R.layout.popwindow_windows, null);
        iv_add = view1.findViewById(R.id.iv_add);
        iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebView w = new WebView(BrowserActivity.this);
                initWebSettings(w);
                webViews.add(0, w);
                myHandler.sendEmptyMessageDelayed(3, 50);
            }
        });

        ListView lv_windows = view1.findViewById(R.id.lv_windows);
        lv_windows.setAdapter(myWindowsAdapter);

        //设置点击
        lv_windows.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                replaceCurrentWebView(position);
            }
        });

        popWebView = new PopupWindow(view1, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        popWebView.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popWebView.setOutsideTouchable(false);
        popWebView.setFocusable(true);
        popWebView.showAtLocation(ll_root, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    /**
     * 窗口切换
     *
     * @param position
     */
    private void replaceCurrentWebView(int position) {
        fl_webview.removeAllViews();
        webview = webViews.get(position);
        fl_webview.addView(webview);
        popWebView.dismiss();
        initToolbarHeader(webview.getUrl());
        setSearchText(webview.getTitle(), webview.getUrl());
    }

    /**
     * 多窗口
     */
    private class MyWindowsAdapter extends WindowsAdapter {

        public MyWindowsAdapter(ArrayList data) {
            super(data);
        }

        @Override
        public void initView(WebView item, MyBaseViewHolder viewHolder) {
            final WindowsViewHolder myWindowsViewHolder = (WindowsViewHolder) viewHolder;
            final int position = webViews.indexOf(item);
            final WebView webView2 = webViews.get(position);
            String title = webView2.getTitle();
            if (title == null || TextUtils.isEmpty(title)) {
                title = getString(R.string.windows_loading);
            }
            myWindowsViewHolder.tv_title.setText(title);
            initWinsNum();
            myWindowsViewHolder.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (webViews.size() == 1) {
                                Toast.makeText(getApplicationContext(), R.string.no_delete, Toast.LENGTH_SHORT).show();
                                return;
                            }
                            webViews.remove(position);
                            webView2.destroy();
                            int len = webViews.size() - 1;
                            webview = webViews.get(len);
                            fl_webview.removeAllViews();
                            fl_webview.addView(webview);
                            myWindowsAdapter.notifyDataSetChanged();
                        }
                    });
                }
            });
            if (webViews.size() > 6) {
                iv_add.setVisibility(View.GONE);
            } else {
                iv_add.setVisibility(View.VISIBLE);
            }

            if (webViews.size() == 1) {
                myWindowsViewHolder.iv_delete.setVisibility(View.GONE);
            } else {
                myWindowsViewHolder.iv_delete.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 设置窗口图标
     */
    private void initWinsNum() {
        MenuItem windowsMenuItem = navigation.getMenu().findItem(R.id.navigation_window);
        switch (webViews.size()) {
            case 1:
                windowsMenuItem.setIcon(R.drawable.ic_check_box_outline_blank_black_24dp);
                break;
            default:
                windowsMenuItem.setIcon(R.drawable.ic_web_asset_black_24dp);
                break;
        }
    }

    //打开设置
    private void openSetting() {
        if (safePop != null && safePop.isShowing()) {
            safePop.dismiss();
        }
        View view1 = getLayoutInflater().inflate(R.layout.popwindow_browser, null);
        GridView gv_set = view1.findViewById(R.id.gv_set);
        //菜单项
        setupList = new ArrayList();
        setupList.add(new SetUpBean(R.drawable.ic_collections_bookmark_black_48dp, getString(R.string.bookmark)));
        setupList.add(new SetUpBean(R.drawable.ic_file_download_black_48dp, getString(R.string.download)));
        setupList.add(new SetUpBean(R.drawable.ic_history_black_48dp, getString(R.string.set_history)));
        setupList.add(new SetUpBean(R.drawable.ic_live_help_black_48dp, getString(R.string.help)));
        setupList.add(new SetUpBean(R.drawable.ic_feedback_black_48dp, getString(R.string.about)));
        setupList.add(new SetUpBean(R.drawable.ic_settings_applications_black_48dp, getString(R.string.set_setup)));
        setupList.add(new SetUpBean(R.drawable.ic_close_black_48dp, getString(R.string.close)));
        setupList.add(new SetUpBean(R.drawable.ic_exit_to_app_black_48dp, getString(R.string.quit)));
        gv_set.setAdapter(new SetUpAdapter(setupList));
        gv_set.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        //书签
                        startActivityForResult(new Intent(getApplicationContext(), BrowserBookMarkActivity.class), 3);
                        break;
                    case 1:
                        //下载
                        Common.goDownload();
                        break;
                    case 2:
                        //历史
                        startActivityForResult(new Intent(getApplicationContext(), BrowserHistoryActivity.class), 1);
                        break;
                    case 3:
                        //帮助
                        startActivity(new Intent(getApplicationContext(), HelpActivity.class));
                        break;
                    case 4:
                        //反馈---> 关于
                        startActivity(new Intent(getApplicationContext(), FeedBackActivity.class));
                        break;
                    case 5:
                        //设置
                        startActivityForResult(new Intent(getApplicationContext(), BrowserSetActivity.class), 2);
                        break;
                    case 6:
                        //关闭
                        break;
                    case 7:
                        //退出
                        if (webview != null) {
                            webview.destroy();
                        }
                        finish();
                        break;

                }

                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });
        popupWindow = new PopupWindow(view1, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(ll_root, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        initWebSettingsWithoutLoading(webview);
        switch (requestCode) {
            case 1:
                if (resultCode == 1 && data != null) {
                    String url = data.getExtras().getString("url");
                    webview.loadUrl(url);
                }
                break;

            case 2:
                if (resultCode == 1 && data != null) {
                    ArrayList<Integer> checkArr = data.getExtras().getIntegerArrayList("checkArr");
                    int len = checkArr.size();
                    for (int i = 0; i < len; i++) {
                        switch (checkArr.get(i)) {
                            case 0:
                                webview.clearHistory();
                                browserHistoryDao.removeAll();
                                break;
                            case 1:
                                webview.clearCache(true);
                                break;
                            case 2:
                                webview.clearSslPreferences();
                                break;
                            case 3:
                                webview.clearFormData();
                                break;
                            case 4:
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    CookieManager.getInstance().removeAllCookies(null);
                                } else {
                                    CookieManager.getInstance().removeAllCookie();
                                }
                                break;
                            case 5:
                                WebStorage.getInstance().deleteAllData();
                                break;
                        }
                    }
                    Toast.makeText(getApplicationContext(), R.string.clear_success, Toast.LENGTH_LONG).show();
                }
                break;
            case 3:
                if (resultCode == 1 && data != null) {
                    String url = data.getExtras().getString("url");
                    webview.loadUrl(url);
                }
                break;
            case 4:
                //文件上传
                if (resultCode == Activity.RESULT_OK && data != null) {
                    Uri[] results = null;
                    String dataString = data.getDataString();
                    if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};
                    }
                    mFilePathCallback.onReceiveValue(results);
                } else {
                    mFilePathCallback.onReceiveValue(null);
                }
                mFilePathCallback = null;
                break;

            case 5:
                //文件上传
                if (resultCode == Activity.RESULT_OK && data != null) {
                    Uri results = data.getData();
                    if (results != null) {
                        mFilePathCallback41.onReceiveValue(results);
                    }
                } else {
                    mFilePathCallback41.onReceiveValue(null);
                }
                mFilePathCallback41 = null;
                break;
        }
    }

    private void initToolbar() {
        myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
    }

    private void initUi() {
        mActivity = this;
        fl_webview = findViewById(R.id.fl_webview);
        webViews = new ArrayList<>();
        webview = new WebView(BrowserActivity.this);
        webViews.add(webview);
        initWebSettings(webview);
        browserHistoryDao = new BrowserHistoryDao();
        ll_root = findViewById(R.id.ll_root);
        fl_webview.addView(webview);
        myWindowsAdapter = new MyWindowsAdapter(webViews);
    }

    /**
     * 初始化窗口
     *
     * @param webview
     */
    private void initWebSettings(WebView webview) {
        initWebSettingsWithoutLoading(webview);
        if (webview.getUrl() == null) {
            webview.loadUrl(Common.getPreferences(getApplicationContext(), Constant.HOME_URL, home_url));
        } else {
            webview.loadUrl(webview.getUrl());
        }
    }

    private void initWebSettingsWithoutLoading(final WebView webview) {
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(Common.getPreferences(getApplicationContext(), Constant.JAVASCRIPT, true));
        int useragentId = Common.getPreferences(getApplicationContext(), Constant.USERAGENT, 0);
        switch (useragentId) {
            case 0:
                webSettings.setUserAgentString(getString(R.string.useragent));
                break;
            case 1:
                webSettings.setUserAgentString(getString(R.string.iphoneuseragent));
                break;
            case 2:
                webSettings.setUserAgentString(getString(R.string.pcuseragent));
                break;
            default:
                webSettings.setUserAgentString(getString(R.string.useragent));
                break;
        }
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setDatabasePath(getCacheDir().getAbsolutePath());
        webSettings.setSaveFormData(true);
        webSettings.setSavePassword(true);
        webSettings.setBlockNetworkImage(Common.getPreferences(getApplicationContext(), Constant.IMAGE, false));
        webSettings.setGeolocationEnabled(Common.getPreferences(getApplicationContext(), Constant.SET_ADDRESS, false));
        webSettings.setSupportMultipleWindows(Common.getPreferences(getApplicationContext(), Constant.MULWIN, true));
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setAppCacheEnabled(true);
        webSettings.setAppCachePath(getCacheDir().getAbsolutePath());
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(Common.getPreferences(getApplicationContext(), Constant.SET_JSOPEN, true));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            webSettings.setMediaPlaybackRequiresUserGesture(true);
            webSettings.setAllowUniversalAccessFromFileURLs(true);
            webSettings.setAllowFileAccessFromFileURLs(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        webview.setWebViewClient(new MyWebView());
        webview.setWebChromeClient(new MyWebChromeClient());
        if (Common.getPreferences(getApplicationContext(), Constant.DOWNLOAD, true)) {
            webview.setDownloadListener(new MyDownloadListener());
        }

        /**
         * 长按事件
         */


        webview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final ArrayList<LongClickBean> clickBeans = new ArrayList<>();

                WebView.HitTestResult hitTestResult = ((WebView) v).getHitTestResult();
                switch (hitTestResult.getType()) {
                    case WebView.HitTestResult.EDIT_TEXT_TYPE:
                        break;
                    case WebView.HitTestResult.EMAIL_TYPE:
                        break;
                    case WebView.HitTestResult.GEO_TYPE:
                        break;
                    case WebView.HitTestResult.IMAGE_TYPE:
                        clickBeans.add(viewBigPictureBean);
                        clickBeans.add(savePictureBean);
                        clickBeans.add(copyUrlBean);
                        clickBeans.add(newWindowOpenBean);
                        break;
                    case WebView.HitTestResult.PHONE_TYPE:
                        break;
                    case WebView.HitTestResult.SRC_ANCHOR_TYPE:
                        clickBeans.add(copyUrlBean);
                        clickBeans.add(newWindowOpenBean);
                        break;
                    case WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE:
                        clickBeans.add(copyUrlBean);
                        clickBeans.add(newWindowOpenBean);
                        break;
                    case WebView.HitTestResult.UNKNOWN_TYPE:
                        break;
                    case WebView.HitTestResult.ANCHOR_TYPE:
                        clickBeans.add(copyUrlBean);
                        clickBeans.add(newWindowOpenBean);
                        break;
                    case WebView.HitTestResult.IMAGE_ANCHOR_TYPE:
                        clickBeans.add(viewBigPictureBean);
                        clickBeans.add(savePictureBean);
                        clickBeans.add(copyUrlBean);
                        clickBeans.add(newWindowOpenBean);
                        break;
                }
                final String extra = hitTestResult.getExtra();
                if (clickBeans.size() > 0) {
                    View longView = View.inflate(getApplicationContext(), R.layout.popwindow_longclick, null);
                    final ListView popLv = longView.findViewById(R.id.lv_pop);
                    popLv.setAdapter(new LongClickAdapter(clickBeans));
                    popLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            popLongView.dismiss();
                            LongClickBean bean = clickBeans.get(position);
                            switch (bean.id) {
                                case 1:
                                    viewBigPicture(extra);
                                    break;
                                case 2:
                                    UpdateDao.download(extra);
                                    break;
                                case 3:
                                    copyUrl(extra);
                                    break;
                                case 4:
                                    openNewWindow(extra);
                                    break;
                            }
                        }
                    });
                    popLongView = new PopupWindow(longView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    popLongView.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    popLongView.setOutsideTouchable(false);
                    popLongView.setFocusable(true);
                    popLongView.showAtLocation(ll_root, Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * 复制链接
     *
     * @param url
     */
    private void copyUrl(String url) {
        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData data = ClipData.newPlainText("text", url);
        clipboardManager.setPrimaryClip(data);
        Common.alert(getString(R.string.copy_url_success));
    }

    /**
     * 新窗口打开
     *
     * @param url
     */
    private void openNewWindow(String url) {
        WebView wv = new WebView(BrowserActivity.this);
        initWebSettings(wv);
        searchQ(wv, url);
        webViews.add(0, wv);
        myHandler.sendEmptyMessage(4);
    }

    /**
     * 查看大图
     *
     * @param extra
     */
    private void viewBigPicture(final String extra) {
        View view = View.inflate(getApplicationContext(), R.layout.alertdialog_viewpicture, null);
        final ImageView iv_image = view.findViewById(R.id.iv_image);
        new Thread() {
            @Override
            public void run() {
                Request request = new Request();
                request.setUrl(extra);
                request.setTimeOut(10000);
                try {
                    InputStream inputStream = request.getInputStream();
                    final Bitmap bitmap2 = BitmapFactory.decodeStream(inputStream);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            iv_image.setImageBitmap(bitmap2);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
        AlertDialog.Builder builder = new AlertDialog.Builder(BrowserActivity.this);
        builder.setIcon(R.drawable.app)
                .setTitle("查看图片")
                .setView(view)
                .setNegativeButton(R.string.close, null)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UpdateDao.download(extra);
                    }
                })
                .create()
                .show();
    }

    private class MyWebView extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            isSafe = true;
            updateHeader = false;
            updateLockHeader = false;
            updateSearchHeader = false;
            ivRefreshing = true;
            iv_refresh.setImageResource(R.drawable.ic_close_black_48dp);
            adUrls = new ArrayList<>();
            jsUrls = new ArrayList<>();
        }

        @Override
        public void onPageFinished(final WebView view, String url) {
            super.onPageFinished(view, url);
            ivRefreshing = false;
            iv_refresh.setImageResource(R.drawable.ic_refresh_black_48dp);
            resetHeader(url, view.getTitle());
            //加入历史记录
            final String u = url;
            final String title = view.getTitle();
            new Thread() {
                @Override
                public void run() {
                    browserHistoryDao.add(u, title);
                }
            }.start();
            //打开网页超强复制
            if (Common.getPreferences(getApplicationContext(), Constant.OPEN_COPY, false)) {
                try {
                    webview.loadUrl("javascript:" + Common.readAssets("copy.js"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            if (!Common.getPreferences(getApplicationContext(), Constant.SET_SSL, true)) {
                myHandler.sendEmptyMessage(2);
            } else {
                super.onReceivedHttpError(view, request, errorResponse);
            }
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            isSafe = false;
            resetHeader(view.getUrl(), view.getTitle());
            if (Common.getPreferences(getApplicationContext(), Constant.SET_SSL, true)) {
                //继续浏览
                handler.proceed();
            } else {
                super.onReceivedSslError(view, handler, error);
                myHandler.sendEmptyMessage(2);
            }
        }

        @Nullable
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (Common.getPreferences(getApplicationContext(), Constant.AD, false) && isAdUrl(request.getUrl().toString())) {
                    return new WebResourceResponse(null, null, null);
                }

                if (Common.getPreferences(getApplicationContext(), Constant.JS, false) && jsJsUrl(request.getUrl().toString())) {
                    return new WebResourceResponse(null, null, null);
                }
            }
            return super.shouldInterceptRequest(view, request);
        }

        @Nullable
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            if (Common.getPreferences(getApplicationContext(), Constant.JS, false) && jsJsUrl(url)) {
                return new WebResourceResponse(null, null, null);
            }
            if (Common.getPreferences(getApplicationContext(), Constant.AD, false) && isAdUrl(url)) {
                return new WebResourceResponse(null, null, null);
            }
            return super.shouldInterceptRequest(view, url);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return startUrl(request.getUrl().toString());
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return startUrl(url);
        }
    }

    /**
     * 打开url
     *
     * @param url
     * @return true 拦截 false 不拦截
     */
    private boolean startUrl(String url) {
        if (url == null || TextUtils.isEmpty(url)) {
            return true;
        }

        if (url.startsWith("http")) {
            return false;
        }

        return true;
    }

    /**
     * 检测js
     *
     * @param s
     * @return
     */
    private boolean jsJsUrl(String s) {
        if (!TextUtils.isEmpty(s) && s.contains(".js")) {
            String js = s.substring(0, s.lastIndexOf(".js") + 3);
            if (!adDao.findOne(js, 2)) {
                if (!jsUrls.contains(s)) {
                    jsUrls.add(s);
                }
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * 检测url是否在拦截列表
     *
     * @param s
     * @return
     */
    private boolean isAdUrl(String s) {
        s = Common.getHost(s);
        if (!TextUtils.isEmpty(s)) {
            if (!adDao.findOne(s, 1)) {
                if (!adUrls.contains(s)) {
                    adUrls.add(s);
                }
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * 设置搜索框文字
     *
     * @param title
     * @param url
     */
    private void setSearchText(String title, String url) {
        if (title == null || TextUtils.isEmpty(title.trim())) {
            et_search.setText(url);
        } else {
            if (Common.getPreferences(getApplicationContext(), Constant.SHOW_SEARCH, 1) == 0) {
                et_search.setText(title);
            } else {
                et_search.setText(url);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            checkCanBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 检查是否可以后退
     */
    private void checkCanBack() {
        webview.stopLoading();
        if (webview.canGoBack()) {
            webview.goBack();
        } else {
            back_num++;
            if (webViews.size() == 1) {
                //一个窗口
                if (back_num < 2) {
                    myHandler.sendEmptyMessage(1);
                } else {
                    webview.destroy();
                    finish();
                }
            } else {
                //多个窗口
                webViews.remove(webview);
                webview.destroy();
                changeToLastWin();
                myWindowsAdapter.notifyDataSetChanged();
                back_num = 0;
                initWinsNum();
                initToolbarHeader(webview.getUrl());
            }
        }
    }

    /**
     * 切换至最新窗口
     */
    private void changeToLastWin() {
        fl_webview.removeAllViews();
        webview = webViews.get(0);
        fl_webview.addView(webview);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webview != null) {
            webview.destroy();
        }

        if (downloadCompleteReceiver != null) {
            unregisterReceiver(downloadCompleteReceiver);
        }
    }


    private class MyWebChromeClient extends WebChromeClient {
        /**
         * 打开新窗口
         *
         * @param view
         * @param isDialog
         * @param isUserGesture
         * @param resultMsg
         * @return
         */
        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            if (!isDialog) {
                WebView wv = new WebView(BrowserActivity.this);
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(wv);
                resultMsg.sendToTarget();
                initWebSettings(wv);
                webViews.add(0, wv);
                myHandler.sendEmptyMessage(4);
                return true;
            }
            return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
            AlertDialog.Builder builder = new AlertDialog.Builder(BrowserActivity.this);
            builder.setTitle(view.getTitle())
                    .setIcon(R.drawable.app)
                    .setMessage(message)
                    .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            result.confirm();
                        }
                    })
                    .setCancelable(false)
                    .create().show();
            return true;
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
            AlertDialog.Builder builder = new AlertDialog.Builder(BrowserActivity.this);
            builder.setTitle(view.getTitle())
                    .setIcon(R.drawable.app)
                    .setMessage(message)
                    .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            result.confirm();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            result.cancel();
                        }
                    })
                    .setCancelable(false)
                    .create().show();
            return true;
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result) {
            AlertDialog.Builder builder = new AlertDialog.Builder(BrowserActivity.this);
            View proview = View.inflate(BrowserActivity.this, R.layout.alertdialog_input_prompt, null);
            final EditText et_prompt = proview.findViewById(R.id.et_prompt);
            et_prompt.setText(defaultValue);
            builder.setTitle(message)
                    .setIcon(R.drawable.app)
                    .setView(proview)
                    .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            result.confirm(et_prompt.getText().toString().trim());
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            result.cancel();
                        }
                    })
                    .setCancelable(false)
                    .create().show();
            return true;
        }

        @Override
        public void onGeolocationPermissionsShowPrompt(final String origin, final GeolocationPermissions.Callback callback) {
            super.onGeolocationPermissionsShowPrompt(origin, callback);
            if (!Common.getPermissions(BrowserActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})) {
                //请求位置权限
                Common.grantedPermissions(BrowserActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(BrowserActivity.this);
            builder.setTitle(origin + getString(R.string.now_get_address))
                    .setIcon(R.drawable.app)
                    .setItems(R.array.address, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    callback.invoke(origin, false, false);
                                    break;
                                case 1:
                                    callback.invoke(origin, false, true);
                                    break;
                                case 2:
                                    callback.invoke(origin, true, false);
                                    break;
                                case 3:
                                    callback.invoke(origin, true, true);
                                    break;
                            }
                        }
                    })
                    .create()
                    .show();
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            resetHeader(view.getUrl(), title);
        }

        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            String acceptType = "*/*";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (fileChooserParams.getAcceptTypes().length > 0) {
                    acceptType = fileChooserParams.getAcceptTypes()[0];
                }
            }
            mFilePathCallback = filePathCallback;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType(acceptType);
            startActivityForResult(Intent.createChooser(i, getString(R.string.choose_file)), 4);
            return true;
        }

        //For Android 4.1
        public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
            mFilePathCallback41 = valueCallback;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType(acceptType);
            startActivityForResult(Intent.createChooser(i, getString(R.string.choose_file)), 5);
        }
    }

    /**
     * 下载设置
     */
    private class MyDownloadListener implements DownloadListener {
        @Override
        public void onDownloadStart(final String url, final String userAgent, final String contentDisposition, final String mimetype, final long contentLength) {
            if (!Common.getPreferences(getApplicationContext(), Constant.DOWNLOAD, true)) {
                myHandler.sendEmptyMessage(7);
                return;
            }
            if (!Common.getPermissions(BrowserActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})) {
                Common.grantedPermissions(BrowserActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
                return;
            }
            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                myHandler.sendEmptyMessage(6);
                return;
            }
            String filename = url.substring(url.lastIndexOf("/") + 1);
            if (filename.contains("?")) {
                filename = filename.substring(0, filename.lastIndexOf("?"));
            }
            if (filename.length() > 30) {
                filename = filename.substring(filename.length() - 30);
            }
            if (TextUtils.isEmpty(filename)) {
                Toast.makeText(getApplicationContext(), R.string.download_error, Toast.LENGTH_SHORT).show();
                return;
            }
            final String filename2 = filename;
            AlertDialog.Builder builder = new AlertDialog.Builder(BrowserActivity.this);
            String referUrl = Common.getHost(url);
            builder.setTitle(R.string.download_tip)
                    .setIcon(R.drawable.app)
                    .setMessage("来源：" + referUrl + "\n文件名：" + filename2 + "\n文件大小：" + Formatter.formatFileSize(getApplicationContext(), contentLength))
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            UpdateDao.download(url);
                        }
                    })
                    .create()
                    .show();
        }
    }

    public static Activity getActivity() {
        return mActivity;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 100:
                int len = grantResults.length;
                if (len > 0) {
                    for (int i = 0; i < len; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            Common.alert(getString(R.string.need_location));
                            return;
                        }
                    }
                } else {
                    Common.alert(getString(R.string.need_location));
                }
                break;

            case 101:
                int len2 = grantResults.length;
                if (len2 > 0) {
                    for (int i = 0; i < len2; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            Common.alert(getString(R.string.open_storage));
                            return;
                        }
                    }
                    Common.alert(getString(R.string.now_download));
                } else {
                    Common.alert(getString(R.string.need_storage));
                }
                break;
        }
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Toast.makeText(getApplicationContext(), R.string.quit_browser, Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    Toast.makeText(getApplicationContext(), R.string.page_load_error, Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    myWindowsAdapter.notifyDataSetChanged();
                    break;
                case 4:
                    initWinsNum();
                    changeToLastWin();
                    myWindowsAdapter.notifyDataSetChanged();
                    break;
                case 5:
                    Toast.makeText(getApplicationContext(), R.string.file_download_complete, Toast.LENGTH_SHORT).show();
                    break;
                case 6:
                    Toast.makeText(getApplicationContext(), R.string.no_sdcard_to_download, Toast.LENGTH_SHORT).show();
                    break;
                case 7:
                    Toast.makeText(getApplicationContext(), R.string.close_file_dowmload, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
