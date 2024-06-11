package com.longfish.jclogindemo.pojo;

import lombok.Data;

import java.util.Map;

@Data
public class Cert {

    private String timestamp;

    private Map<String, String> payload;

    private String sign;

    public String getCertificateName() {
        return payload.get("certificateName");
    }

    public String getCertificateDomains() {
        return payload.get("certificateDomains");
    }

    public String getCertificateCertKey() {
        return payload.get("certificateCertKey");
    }

    public String getCertificateFullchainCerts() {
        return payload.get("certificateFullchainCerts");
    }

    public String getCertificateExpireAt() {
        return payload.get("certificateExpireAt");
    }
}
