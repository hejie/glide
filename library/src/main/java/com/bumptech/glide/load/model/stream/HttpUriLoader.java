package com.bumptech.glide.load.model.stream;

import android.content.Context;
import android.net.Uri;

import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Loads {@link InputStream}s from http or https {@link Uri}s.
 */
public class HttpUriLoader implements ModelLoader<Uri, InputStream> {
    private static final Set<String> SCHEMES = Collections.unmodifiableSet(
            new HashSet<String>(
                    Arrays.asList(
                            "http",
                            "https"
                    )
            )
    );

    private final ModelLoader<GlideUrl, InputStream> urlLoader;

    public HttpUriLoader(ModelLoader<GlideUrl, InputStream> urlLoader) {
        this.urlLoader = urlLoader;
    }

    @Override
    public DataFetcher<InputStream> getDataFetcher(Uri model, int width, int height) {
        return urlLoader.getDataFetcher(new GlideUrl(model.toString()), width, height);
    }

    @Override
    public boolean handles(Uri model) {
        return SCHEMES.contains(model.getScheme());
    }

    /**
     * Factory for loading {@link InputStream}s from http/https {@link Uri}s.
     */
    public static class Factory implements ModelLoaderFactory<Uri, InputStream> {

        @Override
        public ModelLoader<Uri, InputStream> build(Context context, MultiModelLoaderFactory multiFactory) {
            return new HttpUriLoader(multiFactory.build(GlideUrl.class, InputStream.class));
        }

        @Override
        public void teardown() {
            // Do nothing.
        }
    }
}
