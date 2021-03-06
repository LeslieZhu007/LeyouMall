package com.leyou.common.entity;

import com.leyou.common.dto.BaseDTO;
import com.leyou.common.utils.BeanHelper;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author Leslie Arnoald
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BaseEntity {
    private Date createTime;
    private Date updateTime;

    public <T extends BaseDTO> T toDTO(Class<T> dtoClass){
        return BeanHelper.copyProperties(this, dtoClass);
    }
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
