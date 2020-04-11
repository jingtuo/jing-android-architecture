# jing-android-architecture
根据项目实践以及个人理解，整理出自己认为还算合理的安卓框架及常用组件

# 架构

- View层
- 业务层
  - DataBinding：用于将业务与视图分离，用于快速将数据更新到页面
- 数据层
  - RxAndroid-用于将网络操作、数据库操作封装到非UI线程
  - Retrofit用于定义接口
  - OkHttp用于网络请求
  - Room用于存储数据

# 内存优化

## 内存优化-相关知识

- 安卓内存堆栈根据对象的活跃时间长短，将对象划分为年轻、中年、永久，  每当其中一类占用内存达到该类的上限，就会触发GC。
- 每个App进程最初都从Zygote进程上克隆一份出来的，所有App进程共享framework code和resources（如何activity themes）。
- **大多数**静态数据会映射到同一个进程中
- getMemoryClass: 获取可用的堆空间大小，单位兆字节


## 内存优化-思路

将占用内存的对象分类，基于ComponentCallback2监控内存，在合适的场景下，主动释放资源

- 缓存对象: WebView的缓存数据、网络层的缓存数据
- View持有的对象: 这类对象分为静态、非静态
- 四大组件持有的对象: 合理情况下不应该持有对象
- Application持有的对象
- 单例模式

注: 如果第三方库提供了释放资源的功能，也可以在合适的场景下调用。

# 线程优化

## 线程优化-相关知识
CPU调度的负载能力。

## 线程优化-思路
统一应用内的线程管理。

# 笔记

## 笔记-方法

- Context.getFilesDir(): /data/user/0/com.jing.android.arch.demo/files
- Context.getCacheDir(): /data/user/0/com.jing.android.arch.demo/cache
- Context.getExternalCacheDir(): /storage/emulated/0/Android/data/com.jing.android.arch.demo/cache

## 版本变更

> Note: If your app targets Android 8.0 (API level 26) or higher, don't request the READ_SMS permission as part of verifying a user's credentials. Instead, generate an app-specific token using createAppSpecificSmsToken(), then pass this token to another app or service that can send a verification SMS message.

# 待学习

- 计算创建一个对象所占用的内存
- CPU
- 线程管理

# 问题

- 由于Retrofit目前只有adapter-rxjava2，暂时不使用adapter
- 由于Room目前只有room-rxjava2,暂时不使用Observable接收数据

# 参考资料

- [Guide to app architecture](https://developer.android.google.cn/jetpack/docs/guide?hl=en)
- [Manage your app's Memory](https://developer.android.google.cn/topic/performance/memory?hl=en)
- [调查RAM使用情况](https://developer.android.google.cn/studio/profile/investigate-ram)
- [Architecture-Components](https://developer.android.google.cn/jetpack#architecture-components)
