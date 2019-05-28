package com.example.chars.photocollection.network.observer;

import io.reactivex.observers.DisposableObserver;

public abstract class BaseObserver<T> extends DisposableObserver<T> {

    abstract void onSuccess(T t);

    abstract void onFailed();

    @Override
    public void onNext(T t) {
        if (t == null)
            onFailed();
        else
            onSuccess(t);
    }

    @Override
    public void onError(Throwable e) {
        onFailed();
    }

    @Override
    public void onComplete() {

    }
}
