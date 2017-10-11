package com.events;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;


public class Bundle extends BaseBundle implements Cloneable {

    private static final int FLAG_HAS_FDS = 1 << 8;
    private static final int FLAG_HAS_FDS_KNOWN = 1 << 9;
    private static final int FLAG_ALLOW_FDS = 1 << 10;

    public static final Bundle EMPTY;

    static {
        EMPTY = new Bundle();
        EMPTY.mMap = new HashMap<>();
    }

    /**
     * Constructs a new, empty Bundle.
     */
    public Bundle() {
        super();
        mFlags = FLAG_HAS_FDS_KNOWN | FLAG_ALLOW_FDS;
    }

    /**
     * Constructs a new, empty Bundle that uses a specific ClassLoader for
     * instantiating Parcelable and Serializable objects.
     *
     * @param loader An explicit ClassLoader to use when instantiating objects
     * inside of the Bundle.
     */
    public Bundle(ClassLoader loader) {
        super(loader);
        mFlags = FLAG_HAS_FDS_KNOWN | FLAG_ALLOW_FDS;
    }

    /**
     * Constructs a new, empty Bundle sized to hold the given number of
     * elements. The Bundle will grow as needed.
     *
     * @param capacity the initial capacity of the Bundle
     */
    public Bundle(int capacity) {
        super(capacity);
        mFlags = FLAG_HAS_FDS_KNOWN | FLAG_ALLOW_FDS;
    }

    /**
     * Constructs a Bundle containing a copy of the mappings from the given
     * Bundle.
     *
     * @param b a Bundle to be copied.
     */
    public Bundle(Bundle b) {
        super(b);
        mFlags = b.mFlags;
    }



    /**
     * Make a Bundle for a single key/value pair.
     */
    public static Bundle forPair(String key, String value) {
        Bundle b = new Bundle(1);
        b.putString(key, value);
        return b;
    }

    /**
     * Changes the ClassLoader this Bundle uses when instantiating objects.
     *
     * @param loader An explicit ClassLoader to use when instantiating objects
     * inside of the Bundle.
     */
    @Override
    public void setClassLoader(ClassLoader loader) {
        super.setClassLoader(loader);
    }

    /**
     * Return the ClassLoader currently associated with this Bundle.
     */
    @Override
    public ClassLoader getClassLoader() {
        return super.getClassLoader();
    }

    public boolean setAllowFds(boolean allowFds) {
        final boolean orig = (mFlags & FLAG_ALLOW_FDS) != 0;
        if (allowFds) {
            mFlags |= FLAG_ALLOW_FDS;
        } else {
            mFlags &= ~FLAG_ALLOW_FDS;
        }
        return orig;
    }

    /**
     * Mark if this Bundle is okay to "defuse." That is, it's okay for system
     * processes to ignore any BadParcelableException encountered when
     * unparceling it, leaving an empty bundle in its place.
     * <p>
     * This should <em>only</em> be set when the Bundle reaches its final
     * destination, otherwise a system process may clobber contents that were
     * destined for an app that could have unparceled them.
     */
    public void setDefusable(boolean defusable) {
        if (defusable) {
            mFlags |= FLAG_DEFUSABLE;
        } else {
            mFlags &= ~FLAG_DEFUSABLE;
        }
    }

    public static Bundle setDefusable(Bundle bundle, boolean defusable) {
        if (bundle != null) {
            bundle.setDefusable(defusable);
        }
        return bundle;
    }

    /**
     * Clones the current Bundle. The internal map is cloned, but the keys and
     * values to which it refers are copied by reference.
     */
    @Override
    public Object clone() {
        return new Bundle(this);
    }

    /**
     * Removes all elements from the mapping of this Bundle.
     */
    @Override
    public void clear() {
        super.clear();
        mFlags = FLAG_HAS_FDS_KNOWN | FLAG_ALLOW_FDS;
    }

    /**
     * Removes any entry with the given key from the mapping of this Bundle.
     *
     * @param key a String key
     */
    public void remove(String key) {
        super.remove(key);
        if ((mFlags & FLAG_HAS_FDS) != 0) {
            mFlags &= ~FLAG_HAS_FDS_KNOWN;
        }
    }

    /**
     * Inserts all mappings from the given Bundle into this Bundle.
     *
     * @param bundle a Bundle
     */
    public void putAll(Bundle bundle) {
        mMap.putAll(bundle.mMap);

        // FD state is now known if and only if both bundles already knew
        if ((bundle.mFlags & FLAG_HAS_FDS) != 0) {
            mFlags |= FLAG_HAS_FDS;
        }
        if ((bundle.mFlags & FLAG_HAS_FDS_KNOWN) == 0) {
            mFlags &= ~FLAG_HAS_FDS_KNOWN;
        }
    }



    /**
     * Filter values in Bundle to only basic types.
     */
	/*
	public void filterValues() {
		if (mMap != null) {
			for (int i = mMap.size() - 1; i >= 0; i--) {
				Object value = mMap.valueAt(i);
				if (value instanceof Bundle) {
					((Bundle)value).filterValues();
				}
				if (value.getClass().getName().startsWith("android.")) {
					continue;
				}

				mMap.removeAt(i);
			}
		}

		mFlags |= FLAG_HAS_FDS_KNOWN;
		mFlags &= ~FLAG_HAS_FDS;
	}
	//*/

    /**
     * Inserts a byte value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key a String, or null
     * @param value a byte
     */
    @Override
    public void putByte(String key, byte value) {
        super.putByte(key, value);
    }

    /**
     * Inserts a char value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key a String, or null
     * @param value a char
     */
    @Override
    public void putChar(String key, char value) {
        super.putChar(key, value);
    }

    /**
     * Inserts a short value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key a String, or null
     * @param value a short
     */
    @Override
    public void putShort(String key, short value) {
        super.putShort(key, value);
    }

    /**
     * Inserts a float value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key a String, or null
     * @param value a float
     */
    @Override
    public void putFloat(String key, float value) {
        super.putFloat(key, value);
    }

    /**
     * Inserts a CharSequence value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key a String, or null
     * @param value a CharSequence, or null
     */
    @Override
    public void putCharSequence(String key, CharSequence value) {
        super.putCharSequence(key, value);
    }

    /**
     * Inserts an ArrayList<Integer> value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key a String, or null
     * @param value an ArrayList<Integer> object, or null
     */
    @Override
    public void putIntegerArrayList(String key, ArrayList<Integer> value) {
        super.putIntegerArrayList(key, value);
    }

    /**
     * Inserts an ArrayList<String> value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key a String, or null
     * @param value an ArrayList<String> object, or null
     */
    @Override
    public void putStringArrayList(String key, ArrayList<String> value) {
        super.putStringArrayList(key, value);
    }

    /**
     * Inserts an ArrayList<CharSequence> value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key a String, or null
     * @param value an ArrayList<CharSequence> object, or null
     */
    @Override
    public void putCharSequenceArrayList(String key, ArrayList<CharSequence> value) {
        super.putCharSequenceArrayList(key, value);
    }

    /**
     * Inserts a Serializable value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key a String, or null
     * @param value a Serializable object, or null
     */
    @Override
    public void putSerializable(String key, Serializable value) {
        super.putSerializable(key, value);
    }

    /**
     * Inserts a byte array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key a String, or null
     * @param value a byte array object, or null
     */
    @Override
    public void putByteArray(String key, byte[] value) {
        super.putByteArray(key, value);
    }

    /**
     * Inserts a short array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key a String, or null
     * @param value a short array object, or null
     */
    @Override
    public void putShortArray(String key, short[] value) {
        super.putShortArray(key, value);
    }

    /**
     * Inserts a char array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key a String, or null
     * @param value a char array object, or null
     */
    @Override
    public void putCharArray(String key, char[] value) {
        super.putCharArray(key, value);
    }

    /**
     * Inserts a float array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key a String, or null
     * @param value a float array object, or null
     */
    @Override
    public void putFloatArray(String key, float[] value) {
        super.putFloatArray(key, value);
    }

    /**
     * Inserts a CharSequence array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key a String, or null
     * @param value a CharSequence array object, or null
     */
    @Override
    public void putCharSequenceArray(String key, CharSequence[] value) {
        super.putCharSequenceArray(key, value);
    }

    /**
     * Inserts a Bundle value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key a String, or null
     * @param value a Bundle object, or null
     */
    public void putBundle(String key, Bundle value) {
        mMap.put(key, value);
    }

    /**
     * Returns the value associated with the given key, or (byte) 0 if
     * no mapping of the desired type exists for the given key.
     *
     * @param key a String
     * @return a byte value
     */
    @Override
    public byte getByte(String key) {
        return super.getByte(key);
    }

    /**
     * Returns the value associated with the given key, or defaultValue if
     * no mapping of the desired type exists for the given key.
     *
     * @param key a String
     * @param defaultValue Value to return if key does not exist
     * @return a byte value
     */
    @Override
    public Byte getByte(String key, byte defaultValue) {
        return super.getByte(key, defaultValue);
    }

    /**
     * Returns the value associated with the given key, or (char) 0 if
     * no mapping of the desired type exists for the given key.
     *
     * @param key a String
     * @return a char value
     */
    @Override
    public char getChar(String key) {
        return super.getChar(key);
    }

    /**
     * Returns the value associated with the given key, or defaultValue if
     * no mapping of the desired type exists for the given key.
     *
     * @param key a String
     * @param defaultValue Value to return if key does not exist
     * @return a char value
     */
    @Override
    public char getChar(String key, char defaultValue) {
        return super.getChar(key, defaultValue);
    }

    /**
     * Returns the value associated with the given key, or (short) 0 if
     * no mapping of the desired type exists for the given key.
     *
     * @param key a String
     * @return a short value
     */
    @Override
    public short getShort(String key) {
        return super.getShort(key);
    }

    /**
     * Returns the value associated with the given key, or defaultValue if
     * no mapping of the desired type exists for the given key.
     *
     * @param key a String
     * @param defaultValue Value to return if key does not exist
     * @return a short value
     */
    @Override
    public short getShort(String key, short defaultValue) {
        return super.getShort(key, defaultValue);
    }

    /**
     * Returns the value associated with the given key, or 0.0f if
     * no mapping of the desired type exists for the given key.
     *
     * @param key a String
     * @return a float value
     */
    @Override
    public float getFloat(String key) {
        return super.getFloat(key);
    }

    /**
     * Returns the value associated with the given key, or defaultValue if
     * no mapping of the desired type exists for the given key.
     *
     * @param key a String
     * @param defaultValue Value to return if key does not exist
     * @return a float value
     */
    @Override
    public float getFloat(String key, float defaultValue) {
        return super.getFloat(key, defaultValue);
    }

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String, or null
     * @return a CharSequence value, or null
     */
    @Override
    public CharSequence getCharSequence(String key) {
        return super.getCharSequence(key);
    }

    /**
     * Returns the value associated with the given key, or defaultValue if
     * no mapping of the desired type exists for the given key or if a null
     * value is explicitly associatd with the given key.
     *
     * @param key a String, or null
     * @param defaultValue Value to return if key does not exist or if a null
     *     value is associated with the given key.
     * @return the CharSequence value associated with the given key, or defaultValue
     *     if no valid CharSequence object is currently mapped to that key.
     */
    @Override
    public CharSequence getCharSequence(String key, CharSequence defaultValue) {
        return super.getCharSequence(key, defaultValue);
    }


    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String, or null
     * @return a Bundle value, or null
     */
    public Bundle getBundle(String key) {
        Object o = mMap.get(key);
        if (o == null) {
            return null;
        }
        try {
            return (Bundle) o;
        } catch (ClassCastException e) {
            return null;
        }
    }

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String, or null
     * @return a Serializable value, or null
     */
    @Override
    public Serializable getSerializable(String key) {
        return super.getSerializable(key);
    }

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String, or null
     * @return an ArrayList<String> value, or null
     */
    @Override
    public ArrayList<Integer> getIntegerArrayList(String key) {
        return super.getIntegerArrayList(key);
    }

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String, or null
     * @return an ArrayList<String> value, or null
     */
    @Override
    public ArrayList<String> getStringArrayList(String key) {
        return super.getStringArrayList(key);
    }

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String, or null
     * @return an ArrayList<CharSequence> value, or null
     */
    @Override
    public ArrayList<CharSequence> getCharSequenceArrayList(String key) {
        return super.getCharSequenceArrayList(key);
    }

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String, or null
     * @return a byte[] value, or null
     */
    @Override
    public byte[] getByteArray(String key) {
        return super.getByteArray(key);
    }

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String, or null
     * @return a short[] value, or null
     */
    @Override
    public short[] getShortArray(String key) {
        return super.getShortArray(key);
    }

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String, or null
     * @return a char[] value, or null
     */
    @Override
    public char[] getCharArray(String key) {
        return super.getCharArray(key);
    }

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String, or null
     * @return a float[] value, or null
     */
    @Override
    public float[] getFloatArray(String key) {
        return super.getFloatArray(key);
    }

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String, or null
     * @return a CharSequence[] value, or null
     */
    @Override
    public CharSequence[] getCharSequenceArray(String key) {
        return super.getCharSequenceArray(key);
    }


    @Override
    public synchronized String toString() {
        return "Bundle[" + mMap.toString() + "]";
    }

}
