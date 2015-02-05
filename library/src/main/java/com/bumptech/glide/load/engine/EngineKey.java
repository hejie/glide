package com.bumptech.glide.load.engine;

import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.util.Preconditions;
import com.bumptech.glide.util.Util;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@SuppressWarnings("rawtypes")
class EngineKey implements Key {
    private final String id;
    private final int width;
    private final int height;
    private final Class<?> resourceClass;
    private final Class<?> transcodeClass;
    private final Key signature;
    private int hashCode;
    private Key originalKey;

    public EngineKey(String id, Key signature, int width, int height, Class<?> resourceClass,
            Class<?>  transcodeClass) {
        this.id = Preconditions.checkNotNull(id);
        this.signature = Preconditions.checkNotNull(signature, "Signature must not be null");
        this.width = width;
        this.height = height;
        this.resourceClass = Preconditions.checkNotNull(resourceClass, "Resource class must not be null");
        this.transcodeClass = Preconditions.checkNotNull(transcodeClass, "Transcode class must not be null");
    }

    public Key getOriginalKey() {
        if (originalKey == null) {
            originalKey = new OriginalKey(id, signature);
        }
        return originalKey;
    }

    public Key getResultKey(Transformation<?> appliedTransformation, Class<?> decodedResourceClass) {
        return new ResultKey(id, signature, width, height, appliedTransformation, decodedResourceClass);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof EngineKey) {
            EngineKey other = (EngineKey) o;
            return id.equals(other.id) && signature.equals(other.signature) && height == other.height
                    && width == other.width && resourceClass.equals(other.resourceClass)
                    && transcodeClass.equals(other.transcodeClass);
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (hashCode == 0) {
            hashCode = id.hashCode();
            hashCode = 31 * hashCode + signature.hashCode();
            hashCode = 31 * hashCode + width;
            hashCode = 31 * hashCode + height;
            hashCode = 31 * hashCode + resourceClass.hashCode();
            hashCode = 31 * hashCode + transcodeClass.hashCode();
        }
        return hashCode;
    }

    @Override
    public String toString() {
        return "EngineKey{"
                + "id='" + id + '\''
                + ", width=" + width
                + ", height=" + height
                + ", resourceClass=" + resourceClass
                + ", transcodeClass=" + transcodeClass
                + ", signature=" + signature
                + ", originalKey=" + originalKey
                + '}';
    }

    @Override
    public void updateDiskCacheKey(MessageDigest messageDigest) throws UnsupportedEncodingException {
        throw new UnsupportedOperationException();
    }

    private static class ResultKey implements Key {
        private final String id;
        private final Key signature;
        private final int width;
        private final int height;
        private final Transformation<?> appliedTransformation;
        private final Class<?> decodedResourceClass;

        public ResultKey(String id, Key signature, int width, int height, Transformation<?> appliedTransformation,
                Class<?> decodedResourceClass) {
            this.id = id;
            this.signature = signature;
            this.width = width;
            this.height = height;
            this.appliedTransformation = appliedTransformation;
            this.decodedResourceClass = decodedResourceClass;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof ResultKey) {
                ResultKey other = (ResultKey) o;
                return height == other.height && width == other.width
                        && appliedTransformation.getId().equals(other.appliedTransformation.getId())
                        && decodedResourceClass.equals(other.decodedResourceClass)
                        && id.equals(other.id)
                        && signature.equals(other.signature);
            }
            return false;
        }

        @Override
        public int hashCode() {
            int result = id.hashCode();
            result = 31 * result + signature.hashCode();
            result = 31 * result + width;
            result = 31 * result + height;
            result = 31 * result + appliedTransformation.getId().hashCode();
            result = 31 * result + decodedResourceClass.hashCode();
            return result;
        }

        @Override
        public void updateDiskCacheKey(MessageDigest messageDigest) throws UnsupportedEncodingException {
            byte[] dimensions = ByteBuffer.allocate(8)
                    .putInt(width)
                    .putInt(height)
                    .array();
            signature.updateDiskCacheKey(messageDigest);
            messageDigest.update(id.getBytes(STRING_CHARSET_NAME));
            messageDigest.update(dimensions);
            messageDigest.update(appliedTransformation.getId().getBytes(STRING_CHARSET_NAME));
        }

        @Override
        public String toString() {
            String cacheKey = null;
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                updateDiskCacheKey(md);
                cacheKey = Util.sha256BytesToHex(md.digest());
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return "ResultKey{"
                    + "id='" + id + '\''
                    + ", signature=" + signature
                    + ", width=" + width
                    + ", height=" + height
                    + ", appliedTransformation=" + appliedTransformation.getId()
                    + ", decodedResourceClass=" + decodedResourceClass
                    + ", cacheKey=" + cacheKey
                    + '}';
        }
    }
}
