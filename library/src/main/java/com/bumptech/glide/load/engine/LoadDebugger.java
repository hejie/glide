package com.bumptech.glide.load.engine;

import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.data.DataFetcher;

import java.io.File;
import java.util.List;

/**
 * An interface for collecting loggable data about how a particular request attempts to obtain its resource.
 */
interface LoadDebugger {
    void startDecodeResultFromCache();

    void endDecodeResultFromCache();

    void appendResultCacheDecodeResult(Key key, Resource<?> resource, Exception e);

    void startDecodeSourceFromCache();

    void endDecodeSourceFromCache();

    void appendFoundCacheFile(Key key, File file);

    void appendMissingCacheFile(Key key);

    <R> void appendStartLoadPaths(List<? extends LoadPath<?, ?, R>> paths);

    void appendEndLoadPaths();

    void appendStartLoadPath(DataFetcher<?> fetcher);

    void appendLoadPathData(Object data);

    void appendDecodePath(DecodePath<?, ?, ?> path, Resource<?> result);

    void appendEndLoadPath(Exception e);

    void appendEndLoadPath(Resource<?> result);

    void startCacheAndDecodeSource();

    void endCacheAndDecodeSource();

    void startEncodeFromFetcher(DataFetcher fetcher);

    void appendDataFromFetcher(Object data);

    void appendWriteDataToCache(Key key);

    void endEncodeFromFetcher();

    void startDecodeFromSource();

    void endDecodeFromSource();

    void writeLogs(String tag);
}
