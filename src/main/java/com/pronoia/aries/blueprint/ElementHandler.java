package com.pronoia.aries.blueprint;

import org.apache.aries.blueprint.ParserContext;
import org.osgi.service.blueprint.reflect.Metadata;
import org.w3c.dom.Element;


public interface ElementHandler {

    String getElementName();


    Metadata parseElement(final Element serviceElement, final ParserContext parserContext);

}
