package com.longfish.jclogindemo.service.impl;

import com.longfish.jclogindemo.enums.StatusCodeEnum;
import com.longfish.jclogindemo.exception.BizException;
import com.longfish.jclogindemo.mapper.UserAuthMapper;
import com.longfish.jclogindemo.pojo.UserAuth;
import com.longfish.jclogindemo.pojo.dto.PasswordDTO;
import com.longfish.jclogindemo.pojo.dto.UserLoginDTO;
import com.longfish.jclogindemo.pojo.dto.UserRegDTO;
import com.longfish.jclogindemo.pojo.vo.UserLoginVO;
import com.longfish.jclogindemo.service.UserAuthService;
import com.longfish.jclogindemo.util.CodeRedisUtil;
import com.longfish.jclogindemo.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.longfish.jclogindemo.constant.CommonConstant.PATTERN;
import static com.longfish.jclogindemo.constant.CommonConstant.USER_ID;

@Service
@Slf4j
public class UserAuthServiceImpl implements UserAuthService {

    @Autowired
    private UserAuthMapper userAuthMapper;

    @Autowired
    private CodeRedisUtil codeRedisUtil;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.ttl}")
    private Long ttl;

    @Override
    public UserLoginVO login(UserLoginDTO userLoginDTO) {
        if (userLoginDTO.getUsername() == null || userLoginDTO.getPassword() == null) {
            throw new BizException(StatusCodeEnum.USER_NAME_OR_PASSWORD_IS_NULL);
        }
        log.info("用户 {} 登录 @ {}", userLoginDTO, LocalDateTime.now());
        UserAuth userAuth = UserAuth.builder()
                .username(userLoginDTO.getUsername())
                .build();
        List<UserAuth> userAuthList = userAuthMapper.select(userAuth);
        if (userAuthList.size() != 1) {
            throw new BizException(StatusCodeEnum.USER_NOT_EXIST);
        }
        if (!userLoginDTO.getPassword().equals(userAuthList.get(0).getPassword())) {
            throw new BizException(StatusCodeEnum.PASSWORD_ERROR);
        }
        Map<String, Object> claims = new HashMap<>();
        claims.put(USER_ID, userAuthList.get(0).getId());
        String token = JwtUtil.createJWT(
                secretKey,
                ttl,
                claims);
        return UserLoginVO.builder()
                .jwt(token)
                .build();
    }

    @Override
    public void register(UserRegDTO userRegDTO) {
        List<UserAuth> users = userAuthMapper.select(UserAuth.builder().username(userRegDTO.getUsername()).build());
        if (users != null && users.size() != 0) {
            throw new BizException(StatusCodeEnum.USER_EXIST);
        }
        String code = codeRedisUtil.get(userRegDTO.getUsername());
        if (code == null || !code.equals(userRegDTO.getCode())){
            throw new BizException(StatusCodeEnum.CODE_ERROR);
        }
        UserAuth userAuth = new UserAuth();
        BeanUtils.copyProperties(userRegDTO, userAuth);
        userAuth.setCreateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(PATTERN)));
        userAuth.setUpdateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(PATTERN)));
        userAuthMapper.insert(userAuth);
    }

    @Override
    public void forgot(PasswordDTO passwordDTO) {
        List<UserAuth> users = userAuthMapper.select(UserAuth.builder().username(passwordDTO.getUsername()).build());
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
                .password(passwordDTO.getNewPassword())
                .updateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(PATTERN)))
                .build();
        userAuthMapper.updateById(auth);
    }

    @Override
    public void password(PasswordDTO passwordDTO) {
        List<UserAuth> userAuthList = userAuthMapper.select(UserAuth
                .builder()
                .username(passwordDTO.getUsername())
                .build());
        if (userAuthList == null || userAuthList.size() == 0) {
            throw new BizException(StatusCodeEnum.USER_NOT_EXIST);
        }
        UserAuth userAuth = userAuthList.get(0);
        if (!passwordDTO.getOldPassword().equals(userAuth.getPassword())) {
            throw new BizException(StatusCodeEnum.PASSWORD_ERROR);
        }
        UserAuth auth = UserAuth.builder()
                .id(userAuth.getId())
                .password(passwordDTO.getNewPassword())
                .updateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(PATTERN)))
                .build();
        userAuthMapper.updateById(auth);
    }
}
