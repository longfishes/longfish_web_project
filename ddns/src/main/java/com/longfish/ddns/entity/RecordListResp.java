package com.longfish.ddns.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordListResp {

    private RecordCountInfo RecordCountInfo;

    private List<RecordList> RecordList;

    private String RequestId;
}
