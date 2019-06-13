package com.xin.utils.tree;

import lombok.extern.log4j.Log4j;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;

/**
 * @author Luchaoxin
 * @version V 1.0
 * @Description: 目录树数据生成工具类
 * @date 2018-10-24 17:56
 */
@Log4j
public class TreeBuilder {

    /**
     * 两层循环实现建树
     *
     * @param treeNodes 传入的树节点列表
     * @return
     */
    public static <I extends Serializable, T extends TreeNode<I, T>> List<T> build(List<T> treeNodes, I root) {
        return build(treeNodes, root, null);
    }

    /**
     * 两层循环实现建树
     *
     * @param treeNodes 传入的树节点列表
     * @return
     */
    private static <I extends Serializable, T extends TreeNode<I, T>> List<T> build(List<T> treeNodes, I root, Function<T, T> function) {
        if (Objects.isNull(function)) {
            function = (t -> t);
        }
        T rootNode = null;
        List<T> trees = new ArrayList<>();
        for (T treeNode : treeNodes) {
            boolean matchRootId = root.equals(treeNode.getParentId());
            boolean isRootNode = root.equals(treeNode.getId());

            if (matchRootId && !isRootNode) {
                treeNode = function.apply(treeNode);
                trees.add(treeNode);
            }

            if (isRootNode) {
                rootNode = treeNode;
            }

            for (T it : treeNodes) {
                if (root.equals(it.getId())) {
                    continue;
                }
                if (it.getParentId().equals(treeNode.getId())) {
                    if (Objects.isNull(treeNode.getChildren())) {
                        treeNode.setChildren(new ArrayList<>());
                    }
                    treeNode.addChildren(function.apply(it));
                }
            }
        }
        if (Objects.nonNull(rootNode)) {
            rootNode.setChildren(trees);
            T finalRootNode = function.apply(rootNode);
            return new ArrayList<T>(1) {{
                add(finalRootNode);
            }};
        }
        return trees;
    }

    public static <IDTYPE extends Serializable, T extends TreeNode<IDTYPE, T>> Set<IDTYPE> getTreeNodeIds(List<TreeNode<IDTYPE, T>> treeNodes) {
        if (Objects.isNull(treeNodes)) {
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
    public static <T extends TreeNode<? extends Serializable, T>> List<T> buildByRecursive(List<T> treeNodes, Object root) {
        List<T> trees = new ArrayList<>();
        for (T treeNode : treeNodes) {
            if (root.equals(treeNode.getParentId())) {
                if (root.equals(treeNode.getId())) {
                    continue;
                }
                trees.add(findChildren(treeNode, treeNodes));
            }
        }
        return trees;
    }

    /**
     * 递归查找子节点
     *
     * @param treeNodes
     * @return
     */
    public static <T extends TreeNode<? extends Serializable, T>> T findChildren(T treeNode, List<T> treeNodes) {
        for (T it : treeNodes) {
            if (treeNode.getId().equals(it.getParentId())) {
                if (Objects.isNull(treeNode.getChildren())) {
                    treeNode.setChildren(new ArrayList<T>());
                }
                treeNode.addChildren(findChildren(it, treeNodes));
            }
        }
        return treeNode;
    }

    /**
     * 通过sysMenu创建树形节点
     *
     * @param treeDataList
     * @param root
     * @return
     */
    public static <IDTYPE extends Serializable, T extends TreeNode<IDTYPE, T>> List<T> buildTree(List<T> treeDataList, IDTYPE root) {
        return buildTree(treeDataList, root, null);
    }

    public static <IDTYPE extends Serializable, T extends TreeNode<IDTYPE, T>> List<T> buildTree(List<T> treeDataList, IDTYPE root, Function<T, T> function) {
        return build(treeDataList, root, function);
    }

}
