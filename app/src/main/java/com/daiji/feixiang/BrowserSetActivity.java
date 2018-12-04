package com.daiji.feixiang;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.daiji.feixiang.common.Common;
import com.daiji.feixiang.common.Constant;
import com.daiji.feixiang.common.Request;

import org.json.JSONObject;

import java.util.ArrayList;

public class BrowserSetActivity extends BaseActivity {

    private Switch set_javascript, set_image, set_address, set_ssl, set_mulwin, set_ad, set_js, set_download, set_jsopen, set_update, set_error, set_copy;
    private MyHandler myHandler = new MyHandler();

    @Override
    public void init(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_browser_set);
        initUi();
        initHomeUrl();
        initSearch();
        initJavaScript();
        initImage();
        initUserAgent();
        initSearchShow();
        initClear();
        initAddress();
        initSsl();
        initSetMulWin();
        initSetAd();
        initSetAdSet();
        initSetJs();
        initSetJsSet();
        initSetDownload();
        initSetJsopen();
        initSetCheckupdate();
        initSetUpdate();
        initSetErrorLog();
        initSetCopy();
    }

    private void initUi() {
        set_javascript = findViewById(R.id.set_javascript);
        set_javascript.setChecked(Common.getPreferences(mContext, Constant.JAVASCRIPT, true));

        set_image = findViewById(R.id.set_image);
        set_image.setChecked(Common.getPreferences(mContext, Constant.IMAGE, false));

        set_address = findViewById(R.id.set_address);
        set_address.setChecked(Common.getPreferences(mContext, Constant.SET_ADDRESS, false));

        set_ssl = findViewById(R.id.set_ssl);
        set_ssl.setChecked(Common.getPreferences(mContext, Constant.SET_SSL, true));

        set_mulwin = findViewById(R.id.set_mulwin);
        set_mulwin.setChecked(Common.getPreferences(mContext, Constant.MULWIN, true));

        set_ad = findViewById(R.id.set_ad);
        set_ad.setChecked(Common.getPreferences(mContext, Constant.AD, false));

        set_js = findViewById(R.id.set_js);
        set_js.setChecked(Common.getPreferences(mContext, Constant.JS, false));

        set_download = findViewById(R.id.set_download);
        set_download.setChecked(Common.getPreferences(mContext, Constant.DOWNLOAD, true));

        set_jsopen = findViewById(R.id.set_jsopen);
        set_jsopen.setChecked(Common.getPreferences(mContext, Constant.SET_JSOPEN, true));

        set_update = findViewById(R.id.set_update);
        set_update.setChecked(Common.getPreferences(mContext, Constant.UPDATE, true));

        set_error = findViewById(R.id.set_error);
        set_error.setChecked(Common.getPreferences(mContext, Constant.ERROR_LOG, false));

        set_copy = findViewById(R.id.set_copy);
        set_copy.setChecked(Common.getPreferences(mContext, Constant.OPEN_COPY, false));

    }

    private void initSetUpdate() {
        set_update.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Common.setPreferences(mContext, Constant.UPDATE, isChecked);
            }
        });
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Toast.makeText(getApplicationContext(), R.string.not_need_update, Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(getApplicationContext(), R.string.download_error, Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(getApplicationContext(), R.string.need_storage, Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    Toast.makeText(getApplicationContext(), R.string.no_sdcard, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    private void initSetCheckupdate() {
        TextView set_checkupdate = findViewById(R.id.set_checkupdate);
        set_checkupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        update();
                    }
                }.start();
            }
        });
    }

    private void update() {
        Request req = new Request();
        req.setUrl(getString(R.string.update_url));
        req.setTimeOut(5000);
        try {
            String reqString = req.getString();
            JSONObject object = new JSONObject(reqString);
            int updateVersion = object.getInt("version");
            final String content = object.getString("content");
            final String updateUrl = object.getString("url");
            PackageInfo info = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
            int localVersion = info.versionCode;
            if (updateVersion > localVersion) {
                //开始更新
                if (!Common.getPermissions(BrowserSetActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})) {
                    myHandler.sendEmptyMessage(3);
                    return;
                }
                if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    myHandler.sendEmptyMessage(4);
                    return;
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(BrowserSetActivity.this);
                        builder.setTitle(R.string.update_tip)
                                .setIcon(R.drawable.app)
                                .setMessage(content)
                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String filename = updateUrl.substring(updateUrl.lastIndexOf("/") + 1);
                                        if (filename.contains("?")) {
                                            filename = filename.substring(0, filename.lastIndexOf("?"));
                                        }
                                        if (filename.length() > 30) {
                                            filename = filename.substring(filename.length() - 30);
                                        }
                                        if (TextUtils.isEmpty(filename)) {
                                            myHandler.sendEmptyMessage(2);
                                            return;
                                        }
                                        Uri uri = Uri.parse(updateUrl);
                                        DownloadManager service = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                                        DownloadManager.Request request = new DownloadManager.Request(uri);
                                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
                                        request.setTitle(filename);
                                        request.setDescription("存储在：" + Environment.DIRECTORY_DOWNLOADS);
                                        service.enqueue(request);
                                        startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
                                    }
                                })
                                .create()
                                .show();
                    }
                });
            } else {
                //无需更新
                myHandler.sendEmptyMessage(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initSetCopy() {
        set_copy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Common.setPreferences(mContext, Constant.OPEN_COPY, isChecked);
            }
        });
    }

    private void initSetErrorLog() {
        set_error.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!Common.isStoragePermission()) {
                        Common.alert(getString(R.string.need_storage));
                        return;
                    }
                }
                Common.setPreferences(mContext, Constant.ERROR_LOG, isChecked);
            }
        });
    }

    private void initSetJsopen() {
        set_jsopen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Common.setPreferences(mContext, Constant.SET_JSOPEN, isChecked);
            }
        });
    }

    private void initSetDownload() {
        set_download.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Common.setPreferences(mContext, Constant.DOWNLOAD, isChecked);
            }
        });
    }

    private void initSetJsSet() {
        TextView set_jsset = findViewById(R.id.set_jsset);
        set_jsset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Common.getPreferences(mContext, Constant.JS, false)) {
                    Toast.makeText(mContext, R.string.please_open_js_intercept, Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(new Intent(getApplicationContext(), AdJsActivity.class));

            }
        });

    }

    private void initSetAd() {
        set_ad.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Common.setPreferences(mContext, Constant.AD, isChecked);
            }
        });
    }

    private void initSetJs() {
        set_js.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Common.setPreferences(mContext, Constant.JS, isChecked);
            }
        });
    }


    private void initSetAdSet() {
        TextView set_adset = findViewById(R.id.set_adset);
        set_adset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Common.getPreferences(mContext, Constant.AD, false)) {
                    Toast.makeText(mContext, R.string.please_open_url_intercept, Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(new Intent(getApplicationContext(), AdUrlActivity.class));
            }
        });

    }


    private void initSetMulWin() {
        set_mulwin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Common.setPreferences(mContext, Constant.MULWIN, isChecked);
            }
        });
    }

    private void initSsl() {
        set_ssl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Common.setPreferences(mContext, Constant.SET_SSL, isChecked);
            }
        });
    }

    private void initAddress() {
        set_address.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Common.setPreferences(mContext, Constant.SET_ADDRESS, isChecked);
            }
        });
    }

    private void initClear() {
        TextView clear = findViewById(R.id.clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                final ArrayList<Integer> checkArr = new ArrayList<>();
                builder.setTitle(R.string.clear_browser_log)
                        .setIcon(R.drawable.app)
                        .setMultiChoiceItems(R.array.clear, null, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if (isChecked) {
                                    checkArr.add(which);
                                } else {
                                    int len = checkArr.size();
                                    for (int i = 0; i < len; i++) {
                                        if (checkArr.get(i) == which) {
                                            checkArr.remove(i);
                                            break;
                                        }
                                    }
                                }
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                intent.putExtra("checkArr", checkArr);
                                setResult(1, intent);
                                finish();
                            }
                        })
                        .create()
                        .show();
            }
        });
    }

    private void initSearchShow() {
        TextView set_show_search = findViewById(R.id.set_show_search);
        set_show_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                int checkId = Common.getPreferences(mContext, Constant.SHOW_SEARCH, 1);
                builder.setTitle(R.string.set_searchtext_style)
                        .setIcon(R.drawable.app)
                        .setSingleChoiceItems(R.array.show, checkId, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Common.setPreferences(mContext, Constant.SHOW_SEARCH, which);
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
            }
        });

    }

    private void initUserAgent() {
        TextView set_useragent = findViewById(R.id.set_useragent);
        set_useragent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(R.string.set_browser_sign)
                        .setIcon(R.drawable.app)
                        .setSingleChoiceItems(R.array.useragent, Common.getPreferences(mContext, Constant.USERAGENT, 0), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Common.setPreferences(mContext, Constant.USERAGENT, which);
                                Toast.makeText(mContext, R.string.set_success, Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
            }
        });
    }

    private void initImage() {
        set_image.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Common.setPreferences(mContext, Constant.IMAGE, isChecked);
            }
        });
    }


    private void initJavaScript() {
        set_javascript.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Common.setPreferences(mContext, Constant.JAVASCRIPT, isChecked);
            }
        });
    }

    //设置搜索引擎
    private void initSearch() {
        TextView set_search = findViewById(R.id.set_search);
        set_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                int checkId = Common.getPreferences(mContext, Constant.SEARCH_SET, 0);
                builder.setTitle(R.string.set_search_engine)
                        .setIcon(R.drawable.app)
                        .setSingleChoiceItems(R.array.search, checkId, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Common.setPreferences(mContext, Constant.SEARCH_SET, which);
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
            }
        });

    }

    //主页设置
    private void initHomeUrl() {
        TextView set_homeurl = findViewById(R.id.set_homeurl);
        set_homeurl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = View.inflate(mContext, R.layout.alertdialog_input_homeurl, null);
                final EditText et_homeurl = view.findViewById(R.id.et_homeurl);
                et_homeurl.setText(Common.getPreferences(mContext, Constant.HOME_URL, ""));
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setView(view)
                        .setTitle(R.string.set_homeurl)
                        .setIcon(R.drawable.app)
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String s = et_homeurl.getText().toString().trim();
                                if (TextUtils.isEmpty(s)) {
                                    Toast.makeText(mContext, R.string.not_empty_url, Toast.LENGTH_LONG).show();
                                    return;
                                }

                                if (!s.startsWith("http")) {
                                    Toast.makeText(mContext, R.string.url_start_http, Toast.LENGTH_LONG).show();
                                    return;
                                }

                                Common.setPreferences(mContext, Constant.HOME_URL, s);
                                Toast.makeText(mContext, R.string.homeurl_set_success, Toast.LENGTH_LONG).show();
                            }
                        })
                        .create()
                        .show();
            }
        });
    }
}
