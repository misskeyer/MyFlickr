package com.example.chars.photocollection.network.observer;

import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

public class ObserverContainer<T> extends DisposableObserver<T> {

    private CompositeDisposable mCompositeDisposable;
    private Observer<T> mObservable;

    public ObserverContainer(CompositeDisposable disposable, Observer<T> observer) {
        mCompositeDisposable = disposable;
        mObservable = observer;
    }

    @Override
    protected void onStart() {
        mCompositeDisposable.add(this);
        if (mObservable != null)
            mObservable.onSubscribe(this);
    }

    @Override
    public void onNext(T o) {
        if (mObservable != null)
            mObservable.onNext(o);
    }

    @Override
    public void onError(Throwable e) {
        if (mObservable != null)
            mObservable.onError(e);
        mCompositeDisposable.remove(this);
    }

    @Override
    public void onComplete() {
        if (mObservable != null)
            mObservable.onComplete();
        mCompositeDisposable.remove(this);
    }
}
