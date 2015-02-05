package com.bumptech.glide.request;

import android.graphics.drawable.Drawable;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.EmptySignature;
import com.bumptech.glide.util.Preconditions;

/**
 * Contains and exposes a variety of non type specific options that can be applied to a load in Glide.
 */
public class RequestOptions implements Cloneable {
    private static final int UNSET = -1;
    private static final int SIZE_MULTIPLIER = 1 << 1;
    private static final int DISK_CACHE_STRATEGY = 1 << 2;
    private static final int PRIORITY = 1 << 3;
    private static final int ERROR_PLACEHOLDER = 1 << 4;
    private static final int ERROR_ID = 1 << 5;
    private static final int PLACEHOLDER = 1 << 6;
    private static final int PLACEHOLDER_ID = 1 << 7;
    private static final int IS_CACHEABLE = 1 << 8;
    private static final int OVERRIDE = 1 << 9;
    private static final int SIGNATURE = 1 << 10;
    private static final int TAG = 1 << 11;

    private int fields;

    private float sizeMultiplier = 1f;
    private DiskCacheStrategy diskCacheStrategy = DiskCacheStrategy.RESULT;
    private Priority priority = Priority.NORMAL;
    private Drawable errorPlaceholder;
    private int errorId;
    private Drawable placeholderDrawable;
    private int placeholderId;
    private boolean isCacheable = true;
    private int overrideHeight = UNSET;
    private int overrideWidth = UNSET;
    private Key signature = EmptySignature.obtain();
    private String tag;

    public static RequestOptions sizeMultiplierOf(float sizeMultiplier) {
        return new RequestOptions().sizeMultiplier(sizeMultiplier);
    }

    public static RequestOptions diskCacheStrategyOf(DiskCacheStrategy diskCacheStrategy) {
        return new RequestOptions().diskCacheStrategy(diskCacheStrategy);
    }

    public static RequestOptions priorityOf(Priority priority) {
        return new RequestOptions().priority(priority);
    }

    public static RequestOptions placeholderOf(Drawable placeholder) {
        return new RequestOptions().placeholder(placeholder);
    }

    public static RequestOptions placeholderOf(int placeholderId) {
        return new RequestOptions().placeholder(placeholderId);
    }

    public static RequestOptions errorOf(Drawable errorDrawable) {
        return new RequestOptions().error(errorDrawable);
    }

    public static RequestOptions errorOf(int errorId) {
        return new RequestOptions().error(errorId);
    }

    public static RequestOptions skipMemoryCacheOf(boolean skipMemoryCache) {
        return new RequestOptions().skipMemoryCache(skipMemoryCache);
    }

    public static RequestOptions overrideOf(int width, int height) {
        return new RequestOptions().override(width, height);
    }

    public static RequestOptions signatureOf(Key signature) {
        return new RequestOptions().signature(signature);
    }

    public static RequestOptions tagOf(String tag) {
        return new RequestOptions().tag(tag);
    }

    public RequestOptions tag(String tag) {
        this.tag = tag;
        fields |= TAG;
        return this;
    }

    /**
     * Applies a multiplier to the {@link com.bumptech.glide.request.target.Target}'s size before loading the resource.
     * Useful for loading thumbnails or trying to avoid loading huge resources (particularly
     * {@link android.graphics.Bitmap}s on devices with overly dense screens.
     *
     * @param sizeMultiplier The multiplier to apply to the {@link com.bumptech.glide.request.target.Target}'s
     *                       dimensions when loading the resource.
     * @return This request builder.
     */
    public RequestOptions sizeMultiplier(float sizeMultiplier) {
        if (sizeMultiplier < 0f || sizeMultiplier > 1f) {
            throw new IllegalArgumentException("sizeMultiplier must be between 0 and 1");
        }
        this.sizeMultiplier = sizeMultiplier;
        fields |= SIZE_MULTIPLIER;

        return this;
    }

    /**
     * Sets the {@link com.bumptech.glide.load.engine.DiskCacheStrategy} to use for this load. Defaults to
     * {@link com.bumptech.glide.load.engine.DiskCacheStrategy#RESULT}.
     *
     * <p>
     *     For most applications {@link com.bumptech.glide.load.engine.DiskCacheStrategy#RESULT} is ideal.
     *     Applications that use the same resource multiple times in multiple sizes and are willing to trade off some
     *     speed and disk space in return for lower bandwidth usage may want to consider using
     *     {@link com.bumptech.glide.load.engine.DiskCacheStrategy#SOURCE} or
     *     {@link com.bumptech.glide.load.engine.DiskCacheStrategy#RESULT}. Any download only operations should
     *     typically use {@link com.bumptech.glide.load.engine.DiskCacheStrategy#SOURCE}.
     * </p>
     *
     * @param strategy The strategy to use.
     * @return This request builder.
     */
    public RequestOptions diskCacheStrategy(DiskCacheStrategy strategy) {
        this.diskCacheStrategy = Preconditions.checkNotNull(strategy);
        fields |= DISK_CACHE_STRATEGY;

        return this;
    }

    /**
     * Sets the priority for this load.
     *
     * @param priority A priority.
     * @return This request builder.
     */
    public RequestOptions priority(Priority priority) {
        this.priority = Preconditions.checkNotNull(priority);
        fields |= PRIORITY;

        return this;
    }

    /**
     * Sets an {@link android.graphics.drawable.Drawable} to display while a resource is loading.
     *
     * @param drawable The drawable to display as a placeholder.
     * @return This request builder.
     */
    public RequestOptions placeholder(Drawable drawable) {
        this.placeholderDrawable = drawable;
        fields |= PLACEHOLDER;

        return this;
    }

    /**
     * Sets an Android resource id for a {@link android.graphics.drawable.Drawable} resourceto display while a resource
     * is loading.
     *
     * @param resourceId The id of the resource to use as a placeholder
     * @return This request builder.
     */
    public RequestOptions placeholder(int resourceId) {
        this.placeholderId = resourceId;
        fields |= PLACEHOLDER_ID;

        return this;
    }

    /**
     * Sets a {@link Drawable} to display if a load fails.
     *
     * @param drawable The drawable to display.
     * @return This request builder.
     */
    public RequestOptions error(Drawable drawable) {
        this.errorPlaceholder = drawable;
        fields |= ERROR_PLACEHOLDER;

        return this;
    }

    /**
     * Sets a resource to display if a load fails.
     *
     * @param resourceId The id of the resource to use as a placeholder.
     * @return This request builder.
     */
    public RequestOptions error(int resourceId) {
        this.errorId = resourceId;
        fields |= ERROR_ID;

        return this;
    }

    /**
     * Allows the loaded resource to skip the memory cache.
     *
     * <p>
     *     Note - this is not a guarantee. If a request is already pending for this resource and that request is not
     *     also skipping the memory cache, the resource will be cached in memory.
     * </p>
     *
     * @param skip True to allow the resource to skip the memory cache.
     * @return This request builder.
     */
    public RequestOptions skipMemoryCache(boolean skip) {
        this.isCacheable = !skip;
        fields |= IS_CACHEABLE;

        return this;
    }

    /**
     * Overrides the {@link com.bumptech.glide.request.target.Target}'s width and height with the given values. This is
     * useful for thumbnails, and should only be used for other cases when you need a very specific image size.
     *
     * @param width The width in pixels to use to load the resource.
     * @param height The height in pixels to use to load the resource.
     * @return This request builder.
     */
    public RequestOptions override(int width, int height) {
        this.overrideWidth = width;
        this.overrideHeight = height;
        fields |= OVERRIDE;

        return this;
    }

    /**
     * Sets some additional data to be mixed in to the memory and disk cache keys allowing the caller more control over
     * when cached data is invalidated.
     *
     * <p>
     *     Note - The signature does not replace the cache key, it is purely additive.
     * </p>
     *
     * @see com.bumptech.glide.signature.StringSignature
     *
     * @param signature A unique non-null {@link com.bumptech.glide.load.Key} representing the current state of the
     *                  model that will be mixed in to the cache key.
     * @return This request builder.
     */
    public RequestOptions signature(Key signature) {
        this.signature = Preconditions.checkNotNull(signature);
        fields |= SIGNATURE;
        return this;
    }

    /**
     * Returns a copy of this request builder with all of the options set so far on this builder.
     *
     * <p>
     *     This method returns a "deep" copy in that all non-immutable arguments are copied such that changes to one
     *     builder will not affect the other builder. However, in addition to immutable arguments, the current model
     *     is not copied copied so changes to the model will affect both builders.
     * </p>
     */
    @SuppressWarnings("unchecked")
    @Override
    public final RequestOptions clone() {
        try {
            return (RequestOptions) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public RequestOptions apply(RequestOptions other) {
        if (isSet(other.fields, DISK_CACHE_STRATEGY)) {
            diskCacheStrategy = other.diskCacheStrategy;
        }
        if (isSet(other.fields, ERROR_PLACEHOLDER)) {
            errorPlaceholder = other.errorPlaceholder;
        }
        if (isSet(other.fields, ERROR_ID)) {
            errorId = other.errorId;
        }
        if (isSet(other.fields, PLACEHOLDER)) {
            placeholderDrawable = other.placeholderDrawable;
        }
        if (isSet(other.fields, PLACEHOLDER_ID)) {
            placeholderId = other.placeholderId;
        }
        if (isSet(other.fields, IS_CACHEABLE)) {
            isCacheable = other.isCacheable;
        }
        if (isSet(other.fields, SIGNATURE)) {
            signature = other.signature;
        }
        if (isSet(other.fields, PRIORITY)) {
            priority = other.priority;
        }
        if (isSet(other.fields, OVERRIDE)) {
            overrideWidth = other.overrideWidth;
            overrideHeight = other.overrideHeight;
        }
        if (isSet(other.fields, SIZE_MULTIPLIER)) {
            sizeMultiplier = other.sizeMultiplier;
        }
        if (isSet(other.fields, TAG)) {
            tag = other.tag;
        }
        return this;
    }

    public DiskCacheStrategy getDiskCacheStrategy() {
        return diskCacheStrategy;
    }

    public Drawable getErrorPlaceholder() {
        return errorPlaceholder;
    }

    public int getErrorId() {
        return errorId;
    }

    public int getPlaceholderId() {
        return placeholderId;
    }

    public Drawable getPlaceholderDrawable() {
        return placeholderDrawable;
    }

    public boolean isMemoryCacheable() {
        return isCacheable;
    }

    public Key getSignature() {
        return signature;
    }

    public boolean isPrioritySet() {
        return isSet(PRIORITY);
    }

    public Priority getPriority() {
        return priority;
    }

    public int getOverrideWidth() {
        return overrideWidth;
    }

    public int getOverrideHeight() {
        return overrideHeight;
    }

    public float getSizeMultiplier() {
        return sizeMultiplier;
    }

    public String getTag() {
        return tag;
    }

    private boolean isSet(int flag) {
        return isSet(fields, flag);
    }

    private static boolean isSet(int fields, int flag) {
        return (fields & flag) != 0;
    }
}
