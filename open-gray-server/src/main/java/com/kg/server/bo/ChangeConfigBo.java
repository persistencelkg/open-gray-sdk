package com.kg.server.bo;

import com.kg.server.vo.GraySwitchVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/11/22 7:30 PM
 */
@AllArgsConstructor
@NoArgsConstructor

@Data
public class ChangeConfigBo {

    private String serverName;

    private long grayVersion;

    private List<GraySwitchVo> graySwitchVoList;
}
