package com.autoparts.pricingupdate.base;

import javax.servlet.http.HttpServletRequest;

import com.autoparts.pricingupdate.model.MicroserviceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class MsResponseArgumentResolver implements HandlerMethodArgumentResolver {
    private Logger log = LoggerFactory.getLogger(MsResponseArgumentResolver.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public MsResponseArgumentResolver() {
    }

    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest)webRequest.getNativeRequest();
        MicroserviceResponse msResponse = new MicroserviceResponse();
        msResponse.setHttpRequest(request);

        try {
            ObjectNode e = (ObjectNode)objectMapper.readTree(request.getInputStream());
            msResponse.setRequestbody(e);
        } catch (Exception var8) {
            this.log.trace("No body in request");
        }

        msResponse.setRequestParamsArray(webRequest.getParameterMap());
        return msResponse;
    }

    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(MicroserviceResponse.class);
    }
}
