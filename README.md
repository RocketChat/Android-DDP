# Android-DDP

Promise-styled Android DDP implementation with Bolts-Android/RxJava.

## gradle integration (testing...)

in build.gradle
```
allprojects {
    repositories {
        jcenter()
        maven { url 'https://github.com/RocketChat/Android-DDP/raw/master/repository' } // for Android-DDP
    }
}
```

in app/build.gradle
```
dependencies {
    compile 'chat.rocket:android-ddp:0.0.5'
}
```

## Usage Example

```
...
  OkHttpClient wsClient = new OkHttpClient().setReadTimeout(0, TimeUnit.NANOSECONDS);
  DDPClient ddpClient = new DDPClient(wsClient);
  
  // connect to ddp server.
  ddpClient.connect("wss://demo.rocket.chat/websocket").onSuccessTask(task -> {
    DDPClientCallback.Connect result = task.getResult();
  
    // observe all callback events.
    result.client.getSubscriptionCallback().subscribe(event -> {
      Log.d(TAG,"Callback [DEBUG] < "+ event);
    });
  
    // observe all callback events.
    result.client.getFailureObservable().subscribe(event -> {
      Log.w(TAG,"disconnected!");
    });

    // next try to login with token  
    return result.client.rpc("login", new JSONArray().put(token) ,"2");
    
  }).onSuccess(task -> {
    DDPClientCallback.Connect result = task.getResult();
    Log.d(TAG, "Login succeeded.");

  }).continueWith(task -> {
    if(task.isFaulted()){
      if (task.getError instanceof DDPClientCallback.Connect.Error) {
        Log.e(TAG, "failed to connect.", task.getError());
      }
      if (task.getError instanceof DDPClientCallback.RPC.Error) {
        Log.e(TAG, "failed to login.", task.getError());
      }
    }
    return null;
  });
}

```
