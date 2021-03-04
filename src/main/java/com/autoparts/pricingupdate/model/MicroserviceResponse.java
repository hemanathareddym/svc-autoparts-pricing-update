package com.autoparts.pricingupdate.model;

import com.autoparts.pricingupdate.util.JsonUtils;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

@JsonAutoDetect(
    getterVisibility = Visibility.NONE,
    isGetterVisibility = Visibility.NONE
)
public class MicroserviceResponse {
    public static final String HTTP_HEADER_CORRELATION_ID = "ds-correlation-id";
    @Transient
    private Logger log = LoggerFactory.getLogger(MicroserviceResponse.class);
    @JsonInclude(Include.NON_NULL)
    @JsonProperty
    private Integer status;
    @JsonInclude(Include.NON_NULL)
    @JsonProperty
    private Integer code;
    @JsonInclude(Include.NON_NULL)
    @JsonProperty
    private String responseCode;
    @JsonInclude(Include.NON_NULL)
    @JsonProperty
    private String message;
    @JsonProperty
    private String time;
    @Id
    @JsonInclude(Include.NON_NULL)
    @JsonProperty
    private String correlationId;
    @JsonProperty
    private String requestId;
    @JsonInclude(Include.NON_NULL)
    @JsonProperty
    private String path;
    @JsonInclude(Include.NON_NULL)
    @JsonProperty
    private HttpMethod method;
    @JsonInclude(Include.NON_NULL)
    @JsonProperty
    private ObjectNode data;
    @JsonInclude(Include.NON_NULL)
    @JsonProperty
    private ObjectNode request;
    @JsonInclude(Include.NON_NULL)
    @JsonProperty
    private volatile List<MicroserviceResponse.MicroserviceError> errors;
    @JsonInclude(Include.NON_NULL)
    @JsonProperty
    private volatile Map<String, String> params;
    @JsonInclude(Include.NON_NULL)
    @JsonProperty
    private volatile Map<String, String> header;
    @JsonInclude(Include.NON_NULL)
    @JsonProperty
    private String respondTo;
    private String remoteAddress;
    private String xForwardedFor;
    private String userAgent;
    private String requestXSessionId;
    private Map<String, List<String>> requestHttpHeaders;
    private String receivedRoutingKey;
    private Map<String, Object> amqpRequestHeaders;
    private Map<String, Object> amqpResponseHeaders;

    public MicroserviceResponse() {
        this.status = Integer.valueOf(HttpStatus.OK.value());
        this.code = null;
        this.responseCode = null;
        this.message = null;
        this.time = (new SimpleDateFormat("yyyy-MM-dd\'T\'HH:mm:ss.SSSZ")).format(new Date());
        this.correlationId = UUID.randomUUID().toString();
        this.requestId = UUID.randomUUID().toString();
        this.data = JsonUtils.createObjectNode();
        this.request = JsonUtils.createObjectNode();
        this.request.putObject("params");
    }

    public MicroserviceResponse setHttpStatus(HttpStatus status) {
        this.status = Integer.valueOf(status.value());
        if(this.code == null) {
            this.code = Integer.valueOf(status.value());
        }

        return this;
    }

    public HttpStatus getHttpStatus() {
        return HttpStatus.valueOf(this.status.intValue());
    }

    public ObjectNode getData() {
        return this.data;
    }

    public MicroserviceResponse setRequestParamsArray(Map<String, String[]> requestParams) {
        ObjectNode params = null;
        if(this.request.has("params")) {
            params = (ObjectNode)ObjectNode.class.cast(this.request.get("params"));
        } else {
            params = this.request.putObject("params");
        }

        Iterator var3 = requestParams.entrySet().iterator();

        while(true) {
            while(var3.hasNext()) {
                Entry entry = (Entry)var3.next();
                if(entry.getValue() != null && ((String[])entry.getValue()).length == 1) {
                    params.put((String)entry.getKey(), ((String[])entry.getValue())[0]);
                } else if(entry.getValue() != null && ((String[])entry.getValue()).length > 1) {
                    ObjectNode currentParams = JsonUtils.valueToTree(entry.getValue());
                    params.set((String)entry.getKey(), currentParams);
                }
            }

            this.afterParamsSet();
            return this;
        }
    }

    public MicroserviceResponse setRequestParams(Map<String, String> requestParams) {
        ObjectNode params = null;
        if(this.request.has("params")) {
            params = (ObjectNode)ObjectNode.class.cast(this.request.get("params"));
        } else {
            params = this.request.putObject("params");
        }

        Iterator var3 = requestParams.entrySet().iterator();

        while(var3.hasNext()) {
            Entry entry = (Entry)var3.next();
            params.put((String)entry.getKey(), (String)entry.getValue());
        }

        this.afterParamsSet();
        return this;
    }

    public MicroserviceResponse setHeader(Map<String, String> requestHeader) {
        if(this.header == null) {
            this.header = new HashMap();
        }

        Iterator var2 = requestHeader.entrySet().iterator();

        while(var2.hasNext()) {
            Entry entry = (Entry)var2.next();
            this.header.put((String)entry.getKey(), (String)entry.getValue());
        }

        return this;
    }

    public MicroserviceResponse setRequestBody(Map<String, Object> requestBody) {
        ObjectNode node = JsonUtils.valueToTree(requestBody);
        this.request = JsonUtils.mergeObjectNodes(this.request, node);
        this.afterParamsSet();
        return this;
    }

    public MicroserviceResponse setRequestbody(ObjectNode bodyObj) {
        this.request = JsonUtils.mergeObjectNodes(this.request, bodyObj);
        this.afterParamsSet();
        return this;
    }

    private void afterParamsSet() {
        if(this.request.has("correlationId")) {
            this.correlationId = this.request.get("correlationId").asText();
            this.log.info("Set the correlationId on the microserviceResponse from the requestBody.correlationId");
        } else {
            Optional corrId = this.getRequestParam("correlationId");
            if(corrId.isPresent()) {
                this.correlationId = (String)corrId.get();
                this.log.info("Set the correlationId on the microserviceResponse from the requestBody.params.correlationId");
            } else {
                List corrIdList = this.getRequestHttpHeaderIgnoreCase("ds-correlation-id");
                if(corrIdList != null && !corrIdList.isEmpty()) {
                    this.correlationId = (String)corrIdList.get(0);
                    this.log.info("Set the correlationId on the microserviceResponse from the HTTP Headers ds-correlation-id");
                }
            }
        }

        if(this.request.has("method")) {
            this.method = HttpMethod.valueOf(this.request.get("method").asText());
        }

        if(this.request.has("path")) {
            this.path = this.request.get("path").asText();
        }

        if(this.request.has("requestId")) {
            this.requestId = this.request.get("requestId").asText();
            this.log.info("Set the requestId on the microserviceResponse from the requestBody.requestId");
        }

    }

    public MicroserviceResponse setData(ObjectNode data) {
        this.data = data;
        return this;
    }

    public MicroserviceResponse setHttpRequest(HttpServletRequest httpServletRequest) {
        this.request.put("method", httpServletRequest.getMethod());
        this.request.put("path", httpServletRequest.getServletPath());
        this.remoteAddress = httpServletRequest.getRemoteAddr();
        this.xForwardedFor = httpServletRequest.getHeader("X-FORWARDED-FOR");
        this.userAgent = httpServletRequest.getHeader("user-agent");
        this.requestXSessionId = httpServletRequest.getHeader("x-session-id");
        Enumeration headerNames = httpServletRequest.getHeaderNames();
        this.requestHttpHeaders = new HashMap();
        if(headerNames != null) {
            while(true) {
                String headerName;
                Enumeration headerValues;
                do {
                    if(!headerNames.hasMoreElements()) {
                        return this;
                    }

                    headerName = (String)headerNames.nextElement();
                    this.log.debug("Add the http request header [" + headerName + "] to the micro service response.");
                    headerValues = httpServletRequest.getHeaders(headerName);
                } while(headerValues == null);

                ArrayList headerValuesList = new ArrayList();

                while(headerValues.hasMoreElements()) {
                    String headerValue = (String)headerValues.nextElement();
                    headerValuesList.add(headerValue);
                    this.log.debug("Add the http request header [" + headerName + "] value [" + headerValue + "] to the micro service response.");
                }

                this.requestHttpHeaders.put(headerName, headerValuesList);
            }
        } else {
            return this;
        }
    }

    public MicroserviceResponse addError(String code, String message) {
        if(this.errors == null) {
            this.errors = new ArrayList();
        }

        this.errors.add(new MicroserviceResponse.MicroserviceError(code, message));
        if(this.code == null) {
            try {
                this.code = Integer.valueOf(Integer.parseInt(code));
            } catch (Exception var4) {
                this.log.info("Failed to parse the code from the errors array into an in to set the code.", var4);
            }
        }

        if(this.message == null) {
            this.message = message;
        }

        return this;
    }

    public MicroserviceResponse addError(String code, String message, String system) {
        if(this.errors == null) {
            this.errors = new ArrayList();
        }

        this.errors.add(new MicroserviceResponse.MicroserviceError(code, message, system));
        if(this.message == null) {
            this.message = message;
        }

        return this;
    }

    public MicroserviceResponse clearErrors() {
        if(this.errors == null) {
            this.errors = new ArrayList();
        }

        this.errors.clear();
        return this;
    }

    public boolean hasErrors() {
        return this.errors != null && !this.errors.isEmpty();
    }

    public List<MicroserviceResponse.MicroserviceError> getErrors() {
        if(this.errors == null) {
            this.errors = new ArrayList();
        }

        return this.errors;
    }

    public final Map<String, String> getParams() {
        return this.params;
    }

    public final Map<String, String> getHeader() {
        return this.header;
    }

    public MicroserviceResponse addParam(String key, String value) {
        if(this.params == null) {
            this.params = new HashMap();
        }

        this.params.put(key, value);
        return this;
    }

    public MicroserviceResponse setParams(Map<String, String> params) {
        this.params = params;
        return this;
    }

    public MicroserviceResponse setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
        return this;
    }

    public String getCorrelationId() {
        return this.correlationId;
    }

    public String getTime() {
        return this.time;
    }

    public ObjectNode getRequest() {
        return this.request;
    }

    public Optional<String> getRequestParam(String fieldName) {
        return this.request.has("params") && this.request.get("params").has(fieldName)?Optional.of(this.request.get("params").get(fieldName).textValue()):Optional.empty();
    }

    public String getRequestParam(String fieldName, String defaultValue) {
        Optional opt = this.getRequestParam(fieldName);
        return opt.isPresent()?(String)opt.get():defaultValue;
    }

    public String getRequestRespondTo() {
        return this.request.has("respondTo")?this.request.get("respondTo").textValue():"";
    }

    public ObjectNode getRequestParams() {
        return (ObjectNode)ObjectNode.class.cast(this.request.get("params"));
    }

    public HttpMethod getMethod() {
        return this.method;
    }

    public MicroserviceResponse setMethod(HttpMethod method) {
        this.method = method;
        return this;
    }

    public final String getPath() {
        return this.path;
    }

    public MicroserviceResponse setPath(String path) {
        this.path = path;
        return this;
    }

    public String getRespondTo() {
        return this.respondTo;
    }

    public MicroserviceResponse setRespondTo(String respondTo) {
        this.respondTo = respondTo;
        return this;
    }

    public Integer getCode() {
        return this.code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getResponseCode() {
        return this.responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRequestId() {
        return this.requestId;
    }

    public String toString() {
        if(this.errors != null && this.errors.isEmpty()) {
            this.errors = null;
        }

        if(this.params != null && this.params.isEmpty()) {
            this.params = null;
        }

        try {
            return JsonUtils.writeValueAsString(this);
        } catch (JsonProcessingException var2) {
            this.log.debug("Error printing microserviceresponse");
            return "Could not convert to json string";
        }
    }

    public String getRequestValByDotNotation(String requiredField) {
        try {
            return JsonUtils.getString(this.getMappedRequest(), requiredField);
        } catch (Exception var3) {
            return null;
        }
    }

    public String getRemoteAddress() {
        return this.remoteAddress;
    }

    public String getXForwardedFor() {
        return this.xForwardedFor;
    }

    public String getUserAgent() {
        return this.userAgent;
    }

    public String getRequestXSessionId() {
        return this.requestXSessionId;
    }

    public Map<String, List<String>> getRequestHttpHeaders() {
        return this.requestHttpHeaders;
    }

    public List<String> getRequestHttpHeaderIgnoreCase(String theKey) {
        if(this.requestHttpHeaders != null) {
            Iterator var2 = this.requestHttpHeaders.keySet().iterator();

            while(var2.hasNext()) {
                String aKey = (String)var2.next();
                if(StringUtils.equalsIgnoreCase(aKey, theKey)) {
                    return (List)this.requestHttpHeaders.get(aKey);
                }
            }
        }

        return null;
    }

    public void setRequestNull() {
        this.request = null;
    }

    private Map getMappedRequest() throws Exception {
        return JsonUtils.convert(this.request);
    }

    public String getReceivedRoutingKey() {
        return this.receivedRoutingKey;
    }

    public void setReceivedRoutingKey(String receivedRoutingKey) {
        this.receivedRoutingKey = receivedRoutingKey;
    }

    public Map<String, Object> getAmqpRequestHeaders() {
        return this.amqpRequestHeaders;
    }

    public void setAmqpRequestHeaders(Map<String, Object> amqpRequestHeaders) {
        this.amqpRequestHeaders = amqpRequestHeaders;
    }

    public Map<String, Object> getAmqpResponseHeaders() {
        return this.amqpResponseHeaders;
    }

    public void setAmqpResponseHeaders(Map<String, Object> amqpResponseHeaders) {
        this.amqpResponseHeaders = amqpResponseHeaders;
    }

    public class MicroserviceError {
        @JsonProperty
        private String message;
        @JsonProperty
        private String code;
        @JsonProperty
        @JsonInclude(Include.NON_NULL)
        private String system;

        MicroserviceError(String code, String message) {
            this.code = code;
            this.message = message;
        }

        MicroserviceError(String code, String message, String system) {
            this.code = code;
            this.message = message;
            this.system = system;
        }

        public MicroserviceResponse.MicroserviceError setSystem(String system) {
            this.system = system;
            return this;
        }

        public String getSystem() {
            return this.system;
        }

        public String getMessage() {
            return this.message;
        }

        public MicroserviceResponse.MicroserviceError setMessage(String message) {
            this.message = message;
            return this;
        }

        public String getCode() {
            return this.code;
        }

        public MicroserviceResponse.MicroserviceError setCode(String code) {
            this.code = code;
            return this;
        }
    }
}