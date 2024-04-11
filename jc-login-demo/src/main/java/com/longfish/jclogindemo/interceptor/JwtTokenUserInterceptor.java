package com.longfish.jclogindemo.interceptor;

import com.longfish.jclogindemo.context.BaseContext;
import com.longfish.jclogindemo.enums.StatusCodeEnum;
import com.longfish.jclogindemo.exception.BizException;
import com.longfish.jclogindemo.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.longfish.jclogindemo.constant.CommonConstant.USER_ID;

@Component
@Slf4j
public class JwtTokenUserInterceptor implements HandlerInterceptor {

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.token-name}")
    private String tokenName;

    @Override
    @SuppressWarnings("all")
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        if (((HandlerMethod) handler).getBean().getClass().getName().contains("org.springdoc")) {
            return true;
        }

        String token = req.getHeader(tokenName);
        try {
            log.info("jwt check: {}", token);
            Claims claims = JwtUtil.parseJWT(secretKey, token);
            Long userId = Long.valueOf(claims.get(USER_ID).toString());
            log.info("current id: {}", userId);
            BaseContext.setCurrentId(userId);
            return true;

        } catch (Exception ex) {
            resp.setStatus(401);
            throw new BizException(StatusCodeEnum.AUTHORIZED);
        }
    }
}
