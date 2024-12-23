package com.longfish.zfSlider.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodeRefreshResp {

    private String msg;

    private Long t;

    private String si;

    private String imtk;

    private String mi;

    private String vs;

    private String status;
}
