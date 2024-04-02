package com.longfish.jclogindemo.service;

import com.longfish.jclogindemo.pojo.dto.PasswordDTO;
import com.longfish.jclogindemo.pojo.dto.UserLoginDTO;
import com.longfish.jclogindemo.pojo.dto.UserRegDTO;
import com.longfish.jclogindemo.pojo.vo.UserLoginVO;

public interface UserAuthService {

    UserLoginVO login(UserLoginDTO userLoginDTO);

    void register(UserRegDTO userRegDTO);

    void forgot(PasswordDTO passwordDTO);

    void password(PasswordDTO passwordDTO);
}
