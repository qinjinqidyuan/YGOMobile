package cn.garymb.ygomobile.activities;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;

import cn.garymb.ygomobile.lite.R;

public class BaseActivity extends AppCompatActivity {
    private final static int REQUEST_PERMISSIONS = 0x1000 + 1;
    private boolean mExitAnim = true;
    private boolean mEnterAnim = true;

    protected String[] getPermissions() {
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        startPermissionsActivity();
    }

    public void setEnterAnimEnable(boolean disableEnterAnim) {
        this.mEnterAnim = disableEnterAnim;
    }

    public void setExitAnimEnable(boolean disableExitAnim) {
        this.mExitAnim = disableExitAnim;
    }

    protected int getActivityHeight() {
        Rect rect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        return rect.height();
    }

    public void enableBackHome() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    protected int getStatusBarHeight() {
        Rect rect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        return rect.top;
    }

    public void setActionBarTitle(String title) {
        if (TextUtils.isEmpty(title)) {
            return;
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        if (mEnterAnim) {
            setAnim();
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        if (mEnterAnim) {
            setAnim();
        }
    }

    @Override
    public void finish() {
        super.finish();
        if (mExitAnim) {
            overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        super.startActivityForResult(intent, requestCode, options);
        if (mEnterAnim) {
            setAnim();
        }
    }

    private void setAnim() {
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
    }

    public void setActionBarTitle(int rid) {
        setActionBarTitle(getString(rid));
    }

    protected void startPermissionsActivity() {
        String[] PERMISSIONS = getPermissions();
        if (PERMISSIONS == null || PERMISSIONS.length == 0) return;
        PermissionsActivity.startActivityForResult(this, REQUEST_PERMISSIONS, PERMISSIONS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 拒绝时, 关闭页面, 缺少主要权限, 无法运行
        if (requestCode == REQUEST_PERMISSIONS && resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
            finish();
        }
    }
}
