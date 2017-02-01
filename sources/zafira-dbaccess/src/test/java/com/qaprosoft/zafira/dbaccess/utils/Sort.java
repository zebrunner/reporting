package com.qaprosoft.zafira.dbaccess.utils;

import com.qaprosoft.zafira.models.db.AbstractEntity;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Sort<T extends AbstractEntity> {

    public List<T> sortById(List<T> abstractEntityList) {
        Collections.sort(abstractEntityList, new Comparator<T>() {
            @Override
            public int compare(AbstractEntity o1, AbstractEntity o2) {
                return (int) (o1.getId() - o2.getId());
            }
        });
        return abstractEntityList;
    }
}
