# Microsoft Graph 培训模块 - 使用 Microsoft Graph Java SDK 生成 Android 本机应用

本模块将介绍如何通过生成本机移动平台 Android 应用程序来利用 Microsoft Graph SDK 访问 Office 365 中的数据。

## 实验 - 使用 Microsoft Graph Java SDK 生成 Android 本机应用

在此实验中，你将使用 Azure AD v2 身份验证终结点和 Microsoft 身份验证库 (MSAL) 创建 Android 应用程序，以便使用 Microsoft Graph 访问 Office 365 中的数据。

- [Android Microsoft Graph 教程](https://docs.microsoft.com/graph/tutorials/android)

## 演示

此存储库中的 [demos](./demos) 目录包含项目的副本，这些副本对应于教程的各个部分。如果只想演示教程的特定部分，可从上一部分的版本开始。

- [01-create-app](demos/01-create-app)：已完成[创建 Android 应用](https://docs.microsoft.com/graph/tutorials/android?tutorial-step=1)
- [02-add-aad-auth](demos/02-add-aad-auth)：已完成[添加 Azure AD 身份验证](https://docs.microsoft.com/graph/tutorials/android?tutorial-step=3)
- [03-add-msgraph](demos/03-add-msgraph)：已完成[获取日历数据](https://docs.microsoft.com/graph/tutorials/android?tutorial-step=4)

## 已完成的示例

如果只想使用根据此实验生成的已完成的示例，可在此处找到。

- [已完成的项目](demos/03-add-msgraph)

## 观看此模块

此模块已录制，并可在 YouTube 上的 Office 开发频道中找到：[使用 Microsoft Graph Java SDK 生成 Android 本机应用](https://youtu.be/BLmOmv4FSsQ)

## 参与者

| 角色 | 作者 |
| -------------------- | ------------------------------------------------------- |
| 实验手册/幻灯片 | Andrew Connell (Microsoft MVP, Voitanos) @andrewconnell |
| 代码 | Jason Johnston (Microsoft) @jasonjohmsft |
| 发起人/支持 | Yina Arenas (Microsoft) @yinaa |

## 版本历史记录

| 版本 | 日期 | 备注 |
| ------- | ------------------ | -------------------------------------------------------------------------- |
| 1.9 | 2019 年 11 月 13 日 | 使用 androidx工件和最新的 Android SDK、MSAL、Graph SDK 重新创建了项目 |
| 1.8 | 2019 年 6 月 18 日 | 更新了自述文件以更新截屏录制 |
| 1.7 | 2019 年 3 月 30 日 | FY2019Q4 内容更新 |
| 1.6 | 2019 年 2 月 20 日 | 更新为 docs.microsoft.com 格式 |
| 1.5 | 2019 年 2 月 12 日 | 更新了多个依赖项，应用了季度更新 |
| 1.4 | 2018 年 11 月 8 日 | 将 Graph SDK 更新为 GA v1 并应用了季度更新 |
| 1.3 | 2018 年 9 月 12 日 | 将 Graph Android SDK 替换为 Graph Java SDK 并应用了季度更新 |
| 1.2 | 2018 年 6 月 28 日 | 添加了截屏 |
| 1.1 | 2018 年 6 月 22 日 | 重写以使用最新版指南 |
| 1.0 | 大约 2017 年 11 月 24 日 | 添加与 Microsoft Graph 相关的产品分支 |

## 免责声明

**此代码_按原样_提供，不提供任何明示或暗示的担保，包括对特定用途适用性、适销性或不侵权的默示担保。**

<!-- markdownlint-disable MD033 -->
<img src="https://telemetry.sharepointpnp.com/msgraph-training-android" />
