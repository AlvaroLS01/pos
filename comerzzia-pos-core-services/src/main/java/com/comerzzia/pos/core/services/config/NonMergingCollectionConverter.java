package com.comerzzia.pos.core.services.config;

import java.util.Collection;
import java.util.Iterator;

import org.modelmapper.internal.util.Iterables;
import org.modelmapper.internal.util.MappingContextHelper;
import org.modelmapper.spi.MappingContext;

public class NonMergingCollectionConverter extends org.modelmapper.internal.converter.NonMergingCollectionConverter {

	@Override
	public Collection<Object> convert(MappingContext<Object, Collection<Object>> context) {
		Object source = context.getSource();
	    if (source == null)
	      return null;

	    Collection<Object> originalDestination = context.getDestination();
	    Collection<Object> destination = MappingContextHelper.createCollection(context);
	    Class<?> elementType = MappingContextHelper.resolveDestinationGenericType(context);

	    int index = 0;
	    for (Iterator<Object> iterator = Iterables.iterator(source); iterator.hasNext(); index++) {
	      Object sourceElement = iterator.next();
	      Object element = null;
	      if (originalDestination != null)
	        element = Iterables.getElement(originalDestination, index);
	      if (sourceElement != null) {
	    	Class<?> sourceIterableType = elementType;
    	    if(sourceIterableType.getTypeName().equals(Object.class.getName())) {
    	    	sourceIterableType = sourceElement.getClass();
			}
	        MappingContext<?, ?> elementContext = element == null
	            ? context.create(sourceElement, sourceIterableType)
	            : context.create(sourceElement, element);
	        element = context.getMappingEngine().map(elementContext);
	      }
	      destination.add(element);
	    }

	    return destination;
	}
}
