package com.longfish.jclogindemo.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.longfish.jclogindemo.enums.StatusCodeEnum;
import com.longfish.jclogindemo.exception.BizException;
import com.longfish.jclogindemo.mapper.UserAuthMapper;
import com.longfish.jclogindemo.pojo.UserAuth;
import com.longfish.jclogindemo.pojo.dto.PasswordDTO;
import com.longfish.jclogindemo.pojo.dto.UserLoginDTO;
import com.longfish.jclogindemo.pojo.dto.UserRegDTO;
import com.longfish.jclogindemo.pojo.vo.UserLoginVO;
import com.longfish.jclogindemo.properties.JwtProperties;
import com.longfish.jclogindemo.service.UserAuthService;
import com.longfish.jclogindemo.util.CodeRedisUtil;
import com.longfish.jclogindemo.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.longfish.jclogindemo.constant.CommonConstant.PATTERN;
import static com.longfish.jclogindemo.constant.CommonConstant.USER_ID;

@Service
@Slf4j
public class UserAuthServiceImpl extends ServiceImpl<UserAuthMapper, UserAuth> implements UserAuthService {

    @Autowired
    private CodeRedisUtil codeRedisUtil;

    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public UserLoginVO login(UserLoginDTO userLoginDTO) {
        log.info("用户 {} 登录 @ {}", userLoginDTO, LocalDateTime.now());
        if (userLoginDTO.getUsername() == null) {
            throw new BizException(StatusCodeEnum.USER_IS_NULL);
        }
        UserAuth userAuth = UserAuth.builder()
                .username(userLoginDTO.getUsername())
                .build();
        List<UserAuth> userAuthList = lambdaQuery(userAuth).list();
        if (userAuthList.size() != 1) {
            throw new BizException(StatusCodeEnum.USER_NOT_EXIST);
        }
        if (!DigestUtils.md5DigestAsHex(userLoginDTO.getPassword().getBytes()).equals(userAuthList.get(0).getPassword())) {
            throw new BizException(StatusCodeEnum.PASSWORD_ERROR);
        }
        Map<String, Object> claims = new HashMap<>();
        claims.put(USER_ID, userAuthList.get(0).getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getSecretKey(),
                jwtProperties.getTtl(),
                claims);
        return UserLoginVO.builder()
                .jwt(token)
                .build();
    }

    @Override
    public void register(UserRegDTO userRegDTO) {
        List<UserAuth> users = lambdaQuery(UserAuth.builder().username(userRegDTO.getUsername()).build()).list();
        if (users != null && users.size() != 0) {
            throw new BizException(StatusCodeEnum.USER_EXIST);
        }
//        String code = codeRedisUtil.get(userRegDTO.getUsername());
//        if (code == null || !code.equals(userRegDTO.getCode())){
//            throw new BizException(StatusCodeEnum.CODE_ERROR);
//        }

        UserAuth userAuth = BeanUtil.copyProperties(userRegDTO, UserAuth.class);
        userAuth.setPassword(DigestUtils.md5DigestAsHex(userRegDTO.getPassword().getBytes()));
        userAuth.setCreateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(PATTERN)));
        userAuth.setUpdateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(PATTERN)));
        save(userAuth);
    }

    @Override
    public void forgot(PasswordDTO passwordDTO) {
        List<UserAuth> users = lambdaQuery(UserAuth.builder().username(passwordDTO.getUsername()).build()).list();
        if (users == null || users.size() == 0) {
            throw new BizException(StatusCodeEnum.USER_NOT_EXIST);
        }
        String code = codeRedisUtil.get(passwordDTO.getUsername());
        if (code == null || !code.equals(passwordDTO.getCode())){
            throw new BizException(StatusCodeEnum.CODE_ERROR);
        }
        UserAuth userAuth = users.get(0);
        UserAuth auth = UserAuth.builder()
                .id(userAuth.getId())
                .password(DigestUtils.md5DigestAsHex(passwordDTO.getNewPassword().getBytes()))
                .updateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(PATTERN)))
                .build();
        updateById(auth);
    }

    @Override
    public void password(PasswordDTO passwordDTO) {
        List<UserAuth> userAuthList = lambdaQuery(UserAuth
                .builder()
                .username(passwordDTO.getUsername())
                .build()).list();
        if (userAuthList == null || userAuthList.size() == 0) {
            throw new BizException(StatusCodeEnum.USER_NOT_EXIST);
        }
        UserAuth userAuth = userAuthList.get(0);
        if (!DigestUtils.md5DigestAsHex(passwordDTO.getOldPassword().getBytes()).equals(userAuth.getPassword())) {
            throw new BizException(StatusCodeEnum.PASSWORD_ERROR);
        }
        UserAuth auth = UserAuth.builder()
                .id(userAuth.getId())
                .password(DigestUtils.md5DigestAsHex(passwordDTO.getNewPassword().getBytes()))
                .updateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(PATTERN)))
                .build();
        updateById(auth);
    }
}
