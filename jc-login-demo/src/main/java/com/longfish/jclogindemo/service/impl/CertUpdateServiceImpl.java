package com.longfish.jclogindemo.service.impl;

import com.longfish.jclogindemo.exception.SignInvalidException;
import com.longfish.jclogindemo.pojo.Cert;
import com.longfish.jclogindemo.service.CertUpdateService;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class CertUpdateServiceImpl implements CertUpdateService {

    @Override
    public void updateCert(Cert cert) {
        // 签名校验
        String webHookKey = "bb995744f2fc376a5c457390d95ec26a";
        if (cert.getSign() == null ||
                !DigestUtils.md5DigestAsHex((cert.getTimestamp()
                + ":"
                + webHookKey).getBytes()).equals(cert.getSign())) {
            throw new SignInvalidException();
        }
        // 更新证书
        System.out.println(cert);
    }
}
