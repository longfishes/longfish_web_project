package com.longfish.jclogindemo.interceptor;

import com.longfish.jclogindemo.context.BaseContext;
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


/**
 * jwt令牌校验的拦截器
 */
@Component
@Slf4j
public class JwtTokenUserInterceptor implements HandlerInterceptor {

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.token-name}")
    private String tokenName;

    /**
     * 校验jwt
     *
     * @param req 请求
     * @param resp 响应
     * @param handler 处理器
     * @return 布尔类型
     */
    @Override
    @SuppressWarnings("all")
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        String token = req.getHeader(tokenName);

        try {
            log.info("jwt校验:{}", token);
            Claims claims = JwtUtil.parseJWT(secretKey, token);
            Long userId = Long.valueOf(claims.get(USER_ID).toString());
            log.info("当前用户id：{}", userId);
            BaseContext.setCurrentId(userId);
            return true;

        } catch (Exception ex) {
            resp.setStatus(401);
            return false;
        }
    }
}
