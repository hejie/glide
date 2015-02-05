package com.bumptech.glide.provider;

import com.bumptech.glide.load.ResourceEncoder;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains an unordered list of {@link ResourceEncoder}s capable of encoding arbitrary resource types.
 */
public class ResourceEncoderRegistry {
    // TODO: this should probably be a set.
    final List<Entry<?>> encoders = new ArrayList<Entry<?>>();

    public synchronized <Z> void add(Class<Z> resourceClass, ResourceEncoder<Z> encoder) {
        encoders.add(new Entry<Z>(resourceClass, encoder));
    }

    @SuppressWarnings("unchecked")
    public synchronized <Z> ResourceEncoder<Z> get(Class<Z> resourceClass) {
        for (Entry<?> entry : encoders) {
            if (entry.handles(resourceClass)) {
                return (ResourceEncoder<Z>) entry.encoder;
            }
        }
        // TODO: throw an exception here?
        return null;
    }

    private static final class Entry<T> {
        private final Class<T> resourceClass;
        private final ResourceEncoder<T> encoder;

        Entry(Class<T> resourceClass, ResourceEncoder<T> encoder) {
            this.resourceClass = resourceClass;
            this.encoder = encoder;
        }

        private boolean handles(Class<?> resourceClass) {
            return this.resourceClass.isAssignableFrom(resourceClass);
        }
    }
}
