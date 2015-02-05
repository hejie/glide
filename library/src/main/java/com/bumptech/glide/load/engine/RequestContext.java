package com.bumptech.glide.load.engine;

import android.content.ContextWrapper;
import android.util.Log;

import com.bumptech.glide.GlideContext;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.Encoder;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.ResourceEncoder;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.data.DataFetcherSet;
import com.bumptech.glide.load.data.DataRewinder;
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.util.Util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Context for individual requests and decodes that contains and exposes classes necessary to obtain, decode, and
 * encode resources.
 *
 * @param <TranscodeClass> The type of resources returned using classes from this object.
 */
public class RequestContext<TranscodeClass> extends ContextWrapper {
    private static final String TAG = "RequestContext";

    private final GlideContext glideContext;
    private final Object model;
    private final Class<TranscodeClass> transcodeClass;
    private final BaseDecodeOptions<?> decodeOptions;
    private final RequestOptions requestOptions;
    private final LoadDebugger debugger = Log.isLoggable(TAG, Log.VERBOSE)
            ? new PrettyPrintDebugger() : new EmptyLoadDebugger();

    private DataFetcherSet<?> fetchers;

    public RequestContext(GlideContext glideContext, Object model, Class<TranscodeClass> transcodeClass,
            BaseDecodeOptions<?> decodeOptions, RequestOptions requestOptions) {
        super(glideContext);
        this.glideContext = glideContext;
        this.model = model;
        this.transcodeClass = transcodeClass;
        this.decodeOptions = decodeOptions;
        this.requestOptions = requestOptions;
    }

    List<? extends LoadPath<?, ?, TranscodeClass>> getLoadPaths() {
        return glideContext.getLoadPaths(model, decodeOptions.getResourceClass(), transcodeClass);
    }

    List<? extends LoadPath<?, ?, TranscodeClass>> getSourceCacheLoadPaths(File sourceCacheFile) {
        return glideContext.getLoadPaths(sourceCacheFile, decodeOptions.getResourceClass(), transcodeClass);
    }

    void buildDataFetchers(int width, int height) {
        Util.assertMainThread();
        if (fetchers == null) {
            fetchers = glideContext.getDataFetchers(model, width, height);
        }
    }

    DataFetcherSet<?> getDataFetchers() {
        if (fetchers == null) {
            throw new IllegalStateException("Must call buildDataFetchers first");
        }
        return fetchers;
    }

    String getId() {
        if (fetchers == null) {
            throw new IllegalStateException("Must call buildDataFetchers first");
        }
        return fetchers.getId();
    }

    Key getSignature() {
        return requestOptions.getSignature();
    }

    List<Class<?>> getRegisteredResourceClasses() {
        return glideContext.getRegisteredResourceClasses(model.getClass(), decodeOptions.getResourceClass());
    }

    Class<?> getResourceClass() {
        return decodeOptions.getResourceClass();
    }

    Class<TranscodeClass> getTranscodeClass() {
        return transcodeClass;
    }

    <DecodedResource> Transformation<DecodedResource> getTransformation(Class<DecodedResource> decodedResourceClass) {
        return decodeOptions.getTransformation(decodedResourceClass);
    }

    Map<String, Object> getOptions() {
        return new HashMap<String, Object>();
    }

    boolean isMemoryCacheable() {
        return requestOptions.isMemoryCacheable();
    }

    DiskCacheStrategy getDiskCacheStrategy() {
        return requestOptions.getDiskCacheStrategy();
    }

    Priority getPriority() {
        return requestOptions.getPriority();
    }

    ResourceTranscoder<?, ? extends TranscodeClass> getTranscoder() {
        return glideContext.getTranscoder(decodeOptions.getResourceClass(), transcodeClass);
    }

    <X> DataRewinder<X> getRewinder(X data) {
        return glideContext.getRewinder(data);
    }

    <X> ResourceDecoder<X, ?> getDecoder(DataRewinder<X> rewinder)
            throws IOException, GlideContext.NoDecoderAvailableException {
        return glideContext.getDecoder(rewinder, decodeOptions.getResourceClass());
    }

    <ResourceClass> ResourceEncoder<ResourceClass> getResultEncoder(Resource<ResourceClass> resource)
            throws GlideContext.NoResultEncoderAvailableException {
        return glideContext.getResultEncoder(resource);
    }

    <X> Encoder<X> getSourceEncoder(X data) throws GlideContext.NoSourceEncoderAvailableException {
        return glideContext.getSourceEncoder(data);
    }

    DataFetcherSet<?> getDataFetchers(File file, int width, int height)
            throws GlideContext.NoModelLoaderAvailableException {
        return glideContext.getDataFetchers(file, width, height);
    }

    LoadDebugger getDebugger() {
        return debugger;
    }

    String getTag() {
        return requestOptions.getTag();
    }
}
