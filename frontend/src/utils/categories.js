/**
 * 分类树工具：把后端 CategoryNodeDto 树拍平成带层级的列表，供 el-select 分组渲染。
 */
export function flattenCategories(tree) {
  const result = []
  const walk = (nodes, level, parentId = null) => {
    for (const node of nodes || []) {
      result.push({ id: node.id, name: node.name, level, parentId })
      walk(node.children, level + 1, node.id)
    }
  }
  walk(tree, 0)
  return result
}

/** 按父分类分组：返回 [{ parent: 父分类, children: [...] }, ...] */
export function groupCategories(tree) {
  const flat = flattenCategories(tree)
  return flat
    .filter((c) => c.level === 0)
    .map((root) => ({
      parent: root,
      children: flat.filter((c) => c.parentId === root.id),
    }))
}
