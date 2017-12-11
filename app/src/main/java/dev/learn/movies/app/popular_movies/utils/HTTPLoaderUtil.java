package dev.learn.movies.app.popular_movies.utils;

import android.content.Context;
import android.widget.Toast;

import dev.learn.movies.app.popular_movies.R;

/**
 * Created by sudhar on 12/11/17.
 */

public class HTTPLoaderUtil {

    private Context mContext;
    private HTTPBlock mTryCallBlock;
    private HTTPBlock mNoNetworkBlock;

    private HTTPLoaderUtil(Context context) {
        this.mContext = context;
    }

    public static HTTPLoaderUtil with(Context context) {
        HTTPLoaderUtil httpLoaderUtil = new HTTPLoaderUtil(context);
        return httpLoaderUtil;
    }

    public HTTPLoaderUtil tryCall(HTTPBlock tryCallBlock) {
        this.mTryCallBlock = tryCallBlock;
        return this;
    }

    public HTTPLoaderUtil onNoNetwork(HTTPBlock noNetworkBlock) {
        this.mNoNetworkBlock = noNetworkBlock;
        return this;
    }

    public void execute() {
        if (HTTPUtils.isNetworkEnabled(mContext)) {
            if (this.mTryCallBlock != null) {
                this.mTryCallBlock.run();
            }
        } else {
            if (this.mNoNetworkBlock != null) {
                this.mNoNetworkBlock.run();
            } else {
                Toast.makeText(mContext, mContext.getString(R.string.no_network_connection_error_message), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public interface HTTPBlock {
        public void run();
    }
}
