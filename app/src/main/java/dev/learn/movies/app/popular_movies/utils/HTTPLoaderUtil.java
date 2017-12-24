package dev.learn.movies.app.popular_movies.utils;

import android.content.Context;
import android.widget.Toast;

import dev.learn.movies.app.popular_movies.R;

/**
 * HTTPLoaderUtil - HTTP Loader helper
 */

public class HTTPLoaderUtil {

    private final Context mContext;
    private HTTPBlock mTryCallBlock;
    private HTTPBlock mNoNetworkBlock;

    private HTTPLoaderUtil(Context context) {
        this.mContext = context;
    }

    public static HTTPLoaderUtil with(Context context) {
        return new HTTPLoaderUtil(context);
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
        void run();
    }
}
