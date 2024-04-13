package com.longfish.jclogindemo.service.impl;

import com.longfish.jclogindemo.context.BaseContext;
import com.longfish.jclogindemo.mapper.UserInfoMapper;
import com.longfish.jclogindemo.pojo.UserInfo;
import com.longfish.jclogindemo.pojo.vo.UserInfoVO;
import com.longfish.jclogindemo.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.longfish.jclogindemo.constant.CommonConstant.PATTERN;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public UserInfoVO self() {
        List<UserInfo> userInfoList = userInfoMapper.selectById(BaseContext.getCurrentId());
        UserInfo userInfo = userInfoList.get(0);
        return UserInfoVO.builder()
                .username(userInfo.getUsername())
                .createTime(userInfo.getCreateTime().format(DateTimeFormatter.ofPattern(PATTERN)))
                .updateTime(userInfo.getUpdateTime().format(DateTimeFormatter.ofPattern(PATTERN)))
                .build();
    }
}
