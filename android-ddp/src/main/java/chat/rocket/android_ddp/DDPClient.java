package chat.rocket.android_ddp;

import android.support.annotation.Nullable;

import org.json.JSONArray;

import bolts.Task;
import bolts.TaskCompletionSource;
import okhttp3.OkHttpClient;
import rx.Observable;
import timber.log.Timber;

public class DDPClient {
    // reference: https://github.com/eddflrs/meteor-ddp/blob/master/meteor-ddp.js

    private final DDPClientImpl mImpl;
    public DDPClient(OkHttpClient client) {
        mImpl = new DDPClientImpl(this, client);
        Timber.plant(new Timber.DebugTree());
    }

    public Task<DDPClientCallback.Connect> connect(String url) {
        TaskCompletionSource<DDPClientCallback.Connect> task = new TaskCompletionSource<>();
        mImpl.connect(task, url);
        return task.getTask();
    }

    public Task<DDPClientCallback.Ping> ping(@Nullable String id) {
        TaskCompletionSource<DDPClientCallback.Ping> task = new TaskCompletionSource<>();
        mImpl.ping(task, id);
        return task.getTask();
    }

    public Task<DDPClientCallback.RPC> rpc(String method, JSONArray params, String id) {
        TaskCompletionSource<DDPClientCallback.RPC> task = new TaskCompletionSource<>();
        mImpl.rpc(task, method, params, id);
        return task.getTask();
    }

    public Task<DDPSubscription.Ready> sub(String id, String name, JSONArray params) {
        TaskCompletionSource<DDPSubscription.Ready> task = new TaskCompletionSource<>();
        mImpl.sub(task, name, params,id);
        return task.getTask();
    }

    public Task<DDPSubscription.NoSub> unsub(String id) {
        TaskCompletionSource<DDPSubscription.NoSub> task = new TaskCompletionSource<>();
        mImpl.unsub(task, id);
        return task.getTask();
    }

    public Observable<DDPSubscription.Event> getSubscriptionCallback() {
        return mImpl.getDDPSubscription();
    }

    public void close() {
        mImpl.close(1000, "closed by DDPClient#close()");
    }
}
