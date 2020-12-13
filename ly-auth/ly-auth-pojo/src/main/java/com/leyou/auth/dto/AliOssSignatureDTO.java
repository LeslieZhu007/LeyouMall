package com.leyou.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Leslie Arnold
 * @Date: 2020/12/12 16:22
 */
@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class AliOssSignatureDTO {
    private String accessId;
    private String host;
    private String policy;
    private String signature;
    private long expire;
    private String dir;
}