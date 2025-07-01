package com.comerzzia.pos.core.services.config;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.util.Collection;
import java.util.Iterator;

import org.modelmapper.internal.util.Iterables;
import org.modelmapper.internal.util.Types;
import org.modelmapper.spi.ConditionalConverter;
import org.modelmapper.spi.MappingContext;

/**
 * Converts {@link Collection} and array instances to array instances without merging.
 * 
 */
public class NonMergingArrayConverter implements ConditionalConverter<Object, Object> {

	@Override
	public MatchResult match(Class<?> sourceType, Class<?> destinationType) {
		return Iterables.isIterable(sourceType) && destinationType.isArray() ? MatchResult.FULL : MatchResult.NONE;
	}

	@Override
	public Object convert(MappingContext<Object, Object> context) {
		Object source = context.getSource();
		if (source == null)
			return null;

		Object destination = createDestination(context);
		Class<?> elementType = getElementType(context);
		int index = 0;
		for (Iterator<Object> iterator = Iterables.iterator(source); iterator.hasNext(); index++) {
			Object sourceElement = iterator.next();
			MappingContext<?, ?> elementContext = context.create(sourceElement, elementType);
			Object element = context.getMappingEngine().map(elementContext);
			Array.set(destination, index, element);
		}

		return destination;
	}

	private Object createDestination(MappingContext<Object, Object> context) {
		int sourceLength = Iterables.getLength(context.getSource());
		Class<?> destType = context.getDestinationType();
		Object destination = Array.newInstance(destType.isArray() ? destType.getComponentType() : destType, sourceLength);
		return destination;
	}

	private Class<?> getElementType(MappingContext<Object, Object> context) {
		if (context.getGenericDestinationType() instanceof GenericArrayType)
			return Types.rawTypeFor(context.getGenericDestinationType()).getComponentType();
		return context.getDestinationType().getComponentType();
	}

}
