package com.example.myapplication

interface ItemRepository {
    suspend fun getItems(): List<Item>
    suspend fun getItem(id: Int): Item?
}

/**
 * 假数据实现，后续可替换为网络/数据库实现而不影响上层
 */
class FakeItemRepository : ItemRepository {

    override suspend fun getItems(): List<Item> = sampleItems

    override suspend fun getItem(id: Int): Item? = sampleItems.firstOrNull { it.id == id }

    companion object {
        val sampleItems = listOf(
            Item(1, "Jetpack Compose", "声明式 UI 工具包。用更少的代码、直观的 Kotlin API 快速构建原生界面。"),
            Item(2, "Kotlin 协程", "轻量级异步编程方案。通过 suspend 函数与结构化并发，把回调地狱变成顺序代码。"),
            Item(3, "Material Design 3", "Google 最新设计系统。支持动态取色 (Dynamic Color)，全新组件样式与更大的圆角。"),
            Item(4, "LazyColumn", "Compose 中的高效列表组件。只组合和渲染可见项，类似 RecyclerView 但 API 更简洁。"),
            Item(5, "状态管理", "Compose 通过 State 驱动 UI 重组。使用 remember 与 mutableStateOf 保存界面状态。")
        )
    }
}
