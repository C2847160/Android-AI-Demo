# My Application

一个基于 **Jetpack Compose** 的 Android 演示应用：展示「技术概念列表 → 详情页」的最小型完整实现，演示现代 Android 推荐的架构分层（UI / ViewModel / Repository）。

## 1. 项目解决什么问题

Android 初学者在写列表 + 详情页时，常见做法是把数据、状态和页面切换全部堆在 `Activity` 里，导致代码难以测试和扩展。本项目用一个小而完整的例子解决这些问题：

- **状态与 UI 解耦**：状态放在 `ViewModel`（`StateFlow`），`Activity` 只负责渲染。
- **数据与 UI 解耦**：数据通过 `Repository` 接口获取，当前是内存假数据实现，替换为网络/数据库实现时上层无需改动。
- **页面切换标准化**：使用 Navigation Compose 管理路由和返回栈，不再手写 `if/else` + 魔法值。
- **可测试**：Repository 与 UI 完全解耦，单元测试无需 Android 环境即可运行。

## 2. 主要功能

| 功能 | 实现 |
|------|------|
| 列表页 | `LazyColumn` 展示卡片列表，每张卡片显示标题 + 描述（最多 2 行，超出省略号） |
| 详情页 | 显示标题与完整描述，内容支持上下滚动 |
| 页面跳转 | 点击列表项通过路由 `detail/{itemId}` 进入详情页 |
| 返回 | 顶部返回箭头与系统返回键行为一致，均退出详情页回到列表 |
| 状态恢复 | 由 Navigation 的返回栈管理页面状态，进程重建后可恢复 |
| 加载态 | 详情数据尚未就绪时显示 `CircularProgressIndicator` |
| 主题 | Material 3 主题，支持 edge-to-edge 沉浸式布局 |
| 单元测试 | 13 个 `ItemRepository` 测试用例，覆盖正常与边界场景 |

> 说明：当前数据源为内置的 5 条静态假数据，**不涉及网络请求或本地数据库**。

## 3. 安装方法

### 环境要求

- JDK 17 或以上（直接使用 Android Studio 自带的 JBR 即可）
- Android Studio（推荐最新版本）
- Android SDK（compileSdk 37 / minSdk 24 / targetSdk 37）

### 方式一：Android Studio（推荐）

1. 打开 Android Studio，选择 `File → Open`，选中项目根目录 `MyApplication`。
2. 等待 Gradle 同步完成（右上角大象图标或提示条中的 **Sync Now**）。
3. 连接真机或启动模拟器，点击顶部绿色 ▶ 运行 `app`。

### 方式二：命令行

```bash
# 编译 Debug APK
./gradlew assembleDebug

# 生成 APK 路径
# app/build/outputs/apk/debug/app-debug.apk

# 安装到已连接的设备
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

Windows 下使用 `gradlew.bat`：

```powershell
.\gradlew.bat assembleDebug
```

若命令行提示 `JAVA_HOME is not set`，先指向 Android Studio 内置 JDK：

```powershell
$env:JAVA_HOME = "<Android Studio 安装目录>\jbr"
```

## 4. 使用方法

1. 启动应用，进入标题为「AI 训练列表」的列表页。
2. 浏览 5 张技术概念卡片，描述过长时自动截断为 2 行。
3. 点击任意卡片，进入详情页，查看完整描述；内容较长时可上下滑动。
4. 点击左上角返回箭头，或按系统返回键，回到列表页。

### 运行单元测试

```bash
./gradlew testDebugUnitTest
```

Windows：

```powershell
.\gradlew.bat testDebugUnitTest
```

测试报告位于 `app/build/reports/tests/testDebugUnitTest/index.html`。

## 5. 输入输出示例

本应用为 GUI 程序，没有命令行文本输入；「输入」是用户的点击 / 返回操作，「输出」是界面呈现。

### UI 交互示例

| 输入（用户操作） | 输出（界面结果） |
|------------------|------------------|
| 启动应用 | 列表页，标题「AI 训练列表」，显示 5 张卡片 |
| 点击第 2 张卡片（`Kotlin 协程`） | 进入详情页，标题栏显示 `Kotlin 协程`，正文显示完整描述 |
| 详情页内容超出屏幕 | 页面可上下滚动，无内容截断 |
| 点击返回箭头 / 按系统返回键 | 回到列表页 |
| 数据尚未加载完成时直接进入详情页 | 显示居中圆形进度指示器 |

### 数据层示例（Repository API）

```kotlin
// 输入：无参
repository.getItems()
// 输出：List<Item>，共 5 条
// [Item(1, "Jetpack Compose", "声明式 UI 工具包。..."),
//  Item(2, "Kotlin 协程", "轻量级异步编程方案。..."),
//  Item(3, "Material Design 3", "Google 最新设计系统。..."),
//  Item(4, "LazyColumn", "Compose 中的高效列表组件。..."),
//  Item(5, "状态管理", "Compose 通过 State 驱动 UI 重组。...")]

// 输入：id = 3
repository.getItem(3)
// 输出：Item(3, "Material Design 3", "Google 最新设计系统。支持动态取色 ...")

// 输入：id = 0 / -1 / 999 / Int.MAX_VALUE（不存在的 id）
repository.getItem(999)
// 输出：null
```

## 项目结构

```
app/src/main/java/com/example/myapplication/
├── MainActivity.kt      # Activity、ItemApp(NavHost)、ItemListScreen、ItemDetailScreen、Preview
├── Item.kt              # 数据模型 Item(id, title, description)
├── ItemRepository.kt    # Repository 接口 + FakeItemRepository（内存假数据）
└── ItemViewModel.kt     # ViewModel，通过 StateFlow 暴露列表数据

app/src/test/java/com/example/myapplication/
└── ItemRepositoryTest.kt # 13 个 Repository 单元测试
```

## 技术栈

- Kotlin 2.2.10
- Jetpack Compose（BOM 2026.02.01）+ Material 3
- Navigation Compose 2.8.4
- Lifecycle ViewModel Compose / KTX 2.6.1
- AGP 9.4.0，Java 11 编译目标
- JUnit 4.13.2（单元测试）
