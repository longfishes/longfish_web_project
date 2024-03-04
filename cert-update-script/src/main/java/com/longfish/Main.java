package com.longfish;

import com.longfish.entity.Cert;
import com.longfish.util.IOUtil;
import com.longfish.util.OkhttpUtil;

public class Main {

    public static void main(String[] args) {
        Cert cert = OkhttpUtil.getCert();
        IOUtil.write(cert.getPayload().get("fullChainCerts"), "E:\\ssl\\cert.pem");
        IOUtil.write(cert.getPayload().get("certKey"), "E:\\ssl\\cert.key");
        System.exit(0);
    }
}
