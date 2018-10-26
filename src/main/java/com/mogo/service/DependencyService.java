package com.mogo.service;

import com.scalified.tree.TreeNode;

public interface DependencyService {

    TreeNode<String> getDependencyTree(String serviceName);
}
