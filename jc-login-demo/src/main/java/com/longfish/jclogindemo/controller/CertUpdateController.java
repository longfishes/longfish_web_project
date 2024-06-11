package com.longfish.jclogindemo.controller;

import com.longfish.jclogindemo.pojo.Cert;
import com.longfish.jclogindemo.pojo.R;
import com.longfish.jclogindemo.service.CertUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cert")
public class CertUpdateController {

    @Autowired
    private CertUpdateService certUpdateService;

    @PostMapping("/update")
    public R certUpdate(@RequestBody Cert cert) {
        certUpdateService.updateCert(cert);
        return R.success();
    }
}
