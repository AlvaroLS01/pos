package com.comerzzia.pos.core.services.config;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.modelmapper.internal.typetools.TypeResolver;
import org.modelmapper.internal.typetools.TypeResolver.Unknown;
import org.modelmapper.spi.ConditionalConverter;
import org.modelmapper.spi.Mapping;
import org.modelmapper.spi.MappingContext;
import org.modelmapper.spi.PropertyInfo;
import org.modelmapper.spi.PropertyMapping;

public class MapConverter implements ConditionalConverter<Map<?, ?>, Map<Object, Object>> {

	@Override
	public MatchResult match(Class<?> sourceType, Class<?> destinationType) {
		return Map.class.isAssignableFrom(sourceType) && Map.class.isAssignableFrom(destinationType) ? MatchResult.FULL
				: MatchResult.NONE;
	}

	@Override
	public Map<Object, Object> convert(MappingContext<Map<?, ?>, Map<Object, Object>> context) {
		Map<?, ?> source = context.getSource();
		if (source == null)
			return null;

		Map<Object, Object> destination = context.getDestination() == null ? createDestination(context)
				: context.getDestination();
		Mapping mapping = context.getMapping();

		Type keyElementType = Object.class;
		Type valueElementType = Object.class;
		if (mapping instanceof PropertyMapping) {
			PropertyInfo destInfo = mapping.getLastDestinationProperty();
			Class<?>[] elementTypes = TypeResolver.resolveRawArguments(destInfo.getGenericType(), Map.class);
			if (elementTypes != null && elementTypes.length == 2) {
				keyElementType = elementTypes[0] == Unknown.class ? Object.class : elementTypes[0];
				valueElementType = elementTypes[1] == Unknown.class ? Object.class : elementTypes[1];
			}
		} else if (context.getGenericDestinationType() instanceof ParameterizedType) {
			Type[] elementTypes = ((ParameterizedType) context.getGenericDestinationType()).getActualTypeArguments();
			keyElementType = elementTypes[0];
			valueElementType = elementTypes[1];
		}

		for (Entry<?, ?> entry : source.entrySet()) {
			Object key = null;
			if (entry.getKey() != null) {
				MappingContext<?, ?> keyContext = context.create(entry.getKey(), keyElementType);
				key = context.getMappingEngine().map(keyContext);
			}

			Type entryValueElementType = valueElementType;
			Object value = null;
			if (entry.getValue() != null) {
				if(entryValueElementType.getTypeName().equals(Object.class.getName())) {
					entryValueElementType = entry.getValue().getClass();
				}
				MappingContext<?, ?> valueContext = context.create(entry.getValue(), entryValueElementType);
				value = context.getMappingEngine().map(valueContext);
			}

			destination.put(key, value);
		}

		return destination;
	}

	protected Map<Object, Object> createDestination(MappingContext<Map<?, ?>, Map<Object, Object>> context) {
		if (!context.getDestinationType().isInterface())
			return context.getMappingEngine().createDestination(context);
		if (SortedMap.class.isAssignableFrom(context.getDestinationType()))
			return new TreeMap<Object, Object>();
		return new HashMap<Object, Object>();
	}

}
