package com.longfish.ddns.entity;

import lombok.Data;

@Data
public class RecordList {

    private String DefaultNS;

    private String Line;

    private Long LineId;

    private String MX;

    private String MonitorStatus;

    private String Name;

    private Long RecordId;

    private String Remark;

    private String Status;

    private Long TTL;

    private String Type;

    private String UpdatedOn;

    private String Value;

    private String Weight;

}
