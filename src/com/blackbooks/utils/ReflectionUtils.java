package com.blackbooks.utils;

import java.lang.reflect.Field;

/**
 * Reflection utility class.
 */
public final class ReflectionUtils {

	/**
	 * Private constructor.
	 */
	private ReflectionUtils() {
	}

	/**
	 * Returns the value of the field in the specified bean.
	 * 
	 * @param field
	 *            Field.
	 * @param bean
	 *            T.
	 * @return Value.
	 */
	public static <T> Object getFieldValue(Field field, T bean) {
		Object value = null;
		try {
			value = field.get(bean);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}

		return value;
	}

	/**
	 * Get a new instance of the T class.
	 * 
	 * @param type
	 *            The class to instantiate.
	 * @return New instance of T.
	 */
	public static <T> T getNewInstance(Class<T> type) {
		T newInstance = null;
		try {
			newInstance = type.newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		return newInstance;
	}

	/**
	 * Sets the value of the field in the specified bean to the value.
	 * 
	 * @param field
	 *            Field.
	 * @param bean
	 *            Bean.
	 * @param value
	 *            Value.
	 */
	public static <T> void setFieldValue(Field field, T bean, Object value) {
		try {
			field.set(bean, value);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
