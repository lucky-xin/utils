package com.xin.utils.tree;

import com.xin.utils.CollectionUtil;
import lombok.extern.log4j.Log4j;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Luchaoxin
 * @version V 1.0
 * @Description: 目录树数据生成工具类
 * @date 2018-10-24 17:56
 */
@Log4j
public class TreeBuilder {

    public static <IDTYPE extends Serializable, T extends TreeNode<IDTYPE, T>> Set<IDTYPE> getTreeNodeIds(List<TreeNode<IDTYPE, T>> treeNodes) {
        if (CollectionUtil.isEmpty(treeNodes)) {
            return new HashSet<>();
        }
        Set<IDTYPE> ids = new HashSet<>();
        for (TreeNode<IDTYPE, T> treeNode : treeNodes) {
            getChildrenIds(ids, treeNode);
        }
        return ids;
    }

    private static <IDTYPE extends Serializable, T extends TreeNode<IDTYPE, T>> void getChildrenIds(Set<IDTYPE> ids, TreeNode<IDTYPE, T> treeNode) {
        if (treeNode == null || ids == null) {
            return;
        }
        ids.add(treeNode.getId());
        for (TreeNode<IDTYPE, T> child : treeNode.getChildren()) {
            ids.add(child.getId());
            if (child.getId().equals(treeNode.getId())) {
                continue;
            }
            getChildrenIds(ids, child);
        }
    }

    /**
     * 使用递归方法建树
     *
     * @param treeNodes
     * @return
     */
    public static <IDTYPE extends Serializable, T extends TreeNode<IDTYPE, T>> T buildTree(List<T> treeNodes,
                                                                                           IDTYPE root,
                                                                                           Function<T, T> function) {
        List<T> trees = new ArrayList<>();
        T rootNode = null;
        Class<T> clazz = null;
        if (Objects.isNull(function)) {
            function = t -> t;
        }
        Set<T> enableTreeNodes = treeNodes.stream().filter(TreeNode::enable).collect(Collectors.toSet());
        for (T treeNode : enableTreeNodes) {

            // 找到根节点
            if (root.equals(treeNode.getId())) {
                rootNode = function.apply(treeNode);
                continue;
            }

            if (root.equals(treeNode.getParentId())) {
                if (Objects.isNull(clazz)) {
                    clazz = (Class<T>) treeNode.getClass();
                }

                // 找到根节点的所有子节点
                treeNode = findChildren(treeNode, enableTreeNodes, function);
                //function函数处理之后可能返回null,返回null不加入树之中
                if (Objects.nonNull(treeNode)) {
                    trees.add(treeNode);
                }
            }
        }
        if (Objects.isNull(rootNode) && Objects.nonNull(clazz)) {
            try {
                rootNode = clazz.newInstance();
            } catch (Exception e) {
                log.error("创建TreeNode异常，请确保有无参构造器", e);
            }
        }
        if (Objects.nonNull(rootNode)) {
            rootNode.setChildren(trees);
        }
        return rootNode;
    }

    /**
     * 递归查找子节点
     *
     * @param treeNodes
     * @return
     */
    public static <IDTYPE extends Serializable, T extends TreeNode<IDTYPE, T>> T findChildren(T treeNode, Collection<T> treeNodes,
                                                                                              Function<T, T> function) {
        if (Objects.isNull(function)) {
            function = t -> t;
        }
        treeNode = function.apply(treeNode);
        if (Objects.isNull(treeNode) || Objects.isNull(treeNode.getId())) {
            return null;
        }
        for (T it : treeNodes) {
            if (treeNode.getId().equals(it.getParentId())) {
                if (Objects.isNull(treeNode.getChildren())) {
                    treeNode.setChildren(new ArrayList<T>());
                }
                it = function.apply(it);
                //function函数处理之后可能返回null,返回null不加入树之中
                if (Objects.nonNull(it)) {
                    // 找到根节点的所有子节点
                    it = findChildren(it, treeNodes, function);
                    if (Objects.nonNull(it)) {
                        treeNode.addChildren(it);
                    }
                }
            }
        }
        return treeNode;
    }
}
