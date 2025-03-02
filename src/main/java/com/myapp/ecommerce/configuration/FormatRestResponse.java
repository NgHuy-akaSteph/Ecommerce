package com.myapp.ecommerce.configuration;

import com.myapp.ecommerce.util.annotation.ApiMessage;
import org.springframework.core.io.Resource;
import com.myapp.ecommerce.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class FormatRestResponse implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        HttpServletResponse servletResponse = ((ServletServerHttpResponse) response).getServletResponse();
        int status = servletResponse.getStatus();

        ApiResponse<Object> apiResponse = new ApiResponse<>();
        apiResponse.setStatusCode(status);

        // fix temp
        if(body instanceof String || body instanceof Resource){
            return body;
        }

//        String path = request.getURI().getPath();
        if(status >= 400) {
            // show error
            return body;
        }
        else {
            apiResponse.setData(body);
            ApiMessage message = returnType.getMethodAnnotation(ApiMessage.class);
            apiResponse.setMessage(message == null ? "Call api success" : message.value());
        }
        return apiResponse;
    }
}
