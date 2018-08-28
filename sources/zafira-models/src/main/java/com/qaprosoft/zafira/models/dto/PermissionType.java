package com.qaprosoft.zafira.models.dto;

import com.qaprosoft.zafira.models.db.Permission;
import com.qaprosoft.zafira.models.dto.AbstractType;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

public class PermissionType extends AbstractType {

    private static final long serialVersionUID = 7083932006258442862L;

    @NotEmpty(message = "Name required")
    private Permission.Name name;

    @NotNull(message = "Block required")
    private Permission.Block block;

    public Permission.Name getName()
    {
        return name;
    }

    public void setName(Permission.Name name)
    {
        this.name = name;
    }

    public Permission.Block getBlock()
    {
        return block;
    }

    public void setBlock(Permission.Block block)
    {
        this.block = block;
    }
}
