package com.xin.utils.tree;

import java.io.Serializable;
import java.util.List;

/**
 * @author Luchaoxin
 * @version V 1.0
 * @Description: 目录树数据节点接口
 * @date 2019-06-07 21:30
 */
public interface TreeNode<IDTYPE extends Serializable, T extends TreeNode<IDTYPE, T>> extends Serializable {

    /**
     * 添加子节点
     *
     * @param treeNode
     */
    void addChildren(T treeNode);

    /**
     * 获取当前节点id
     */
    IDTYPE getId();

    /**
     * 获取当前节点的父节点id
     */
    IDTYPE getParentId();

    /**
     * 获取当前节点的子节点
     */
    List<T> getChildren();

    T setChildren(List<T> children);
}
