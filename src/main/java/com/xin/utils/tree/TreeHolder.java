package com.xin.utils.tree;

import lombok.extern.log4j.Log4j;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

/**
 * @author Luchaoxin
 * @version V 1.0
 * @Description: 目录树持有对象
 * @date 2019-06-07 21:49
 */
@Log4j
public class TreeHolder<IDTYPE extends Serializable, T extends TreeNode<IDTYPE, T>> implements Serializable {

    /**
     * 构建目录树数据,有的数据可能不在构建出来的树之中
     */
    private List<T> treeDataList;

    /**
     * 目录树跟节点id
     */
    private IDTYPE root;

    /**
     * 目录树遍历过程调用的function
     */
    private Function<T, T> function;

    /**
     * 当前目录树所有节点，平级在set之中
     */
    private Set<T> treeNodes;

    /**
     * 当前目录树所有节点id
     */
    private Set<IDTYPE> treeNodeIds;

    /**
     * 当前目录树嵌套结构数据
     */
    private List<T> tree;


    private AtomicBoolean alreadyBuild = new AtomicBoolean(false);

    /**
     * 自定义比较器，根据TreeNode的主键id去重
     */
    private Comparator<T> comparator = (o1, o2) -> {
        if (Objects.isNull(o1) || Objects.isNull(o2)) {
            return 0;
        }
        if (o1.getId().equals(o2.getId())) {
            return 0;
        }
        return o1.getId().hashCode() - o2.getId().hashCode();
    };

    public TreeHolder(List<T> treeDataList, IDTYPE root) {
        this(treeDataList, root, null);
    }

    /**
     * @param treeDataList 所有构建目录树数据
     * @param root         构建目录树根节点id
     * @param function     构建树过程之中处理每个节点调用的function，用户处理节点额外操作，如添加，修改数据
     */
    public TreeHolder(List<T> treeDataList, IDTYPE root, Function<T, T> function) {
        this.treeDataList = treeDataList;
        this.root = root;
        this.treeNodes = new TreeSet<>(comparator);
        this.treeNodeIds = new HashSet<>(treeDataList.size());
        if (Objects.isNull(function)) {
            function = t -> t;
        }

        this.function = function.andThen(treeNode -> {
            treeNodes.add(treeNode);
            treeNodeIds.add(treeNode.getId());
            return treeNode;
        });
    }

    private void buildTree() {
        if (alreadyBuild.compareAndSet(false, true)) {
            tree = TreeBuilder.buildTree(treeDataList, root, function);
        }
    }

    /**
     * 获取目录树的所有节点，使用unmodifiableSet包装返回结果禁止对treeNodes进行编辑
     *
     * @return
     */
    public Set<T> getTreeNodes() {
        buildTree();
        return Collections.unmodifiableSet(treeNodes);
    }

    /**
     * 获取目录树的所有节点id，使用unmodifiableSet包装返回结果禁止对treeNodeIds进行编辑
     *
     * @return
     */
    public Set<IDTYPE> getTreeNodeIds() {
        buildTree();
        return Collections.unmodifiableSet(treeNodeIds);
    }

    /**
     * 获取目录树信息，使用unmodifiableList包装返回结果禁止对tree进行编辑
     *
     * @return
     */
    public List<T> getTree() {
        buildTree();
        return Collections.unmodifiableList(tree);
    }
}