package com.kg.server.bo;

import com.kg.server.vo.GraySwitchVo;
import lombok.Data;

import java.util.List;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/11/22 7:30 PM
 */
@Data
public class ChangeConfigBo {

    private String serverName;

    private List<GraySwitchVo> graySwitchVoList;
}
