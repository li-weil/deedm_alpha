现在的最大的问题是没有利用源代码。仿照/home/admin-unix/Deedm/legacy/src/guiManager/logic/FormulaTruthTableUIManager.java的后端调用逻辑，重构/home/admin-unix/Deedm/frontend/src/components/logic/TruthTableInterface.vue，重构/home/admin-unix/Deedm/backend的后端逻辑，用service连接上源代码的相应接口，方便在前端直接调用。/home/admin-unix/Deedm/legacy/src/guiManager/logic/FormulaTruthTableUIManager.java还利用了java应用swing框架下其他前端实现包，在vue框架下实现这些/home/admin-unix/Deedm/frontend/src/components/logic/TruthTableInterface.vue可能涉及的前端模块

注意你的任务只限于完成你的要求，只构建我需要的文件，不要额外去写其他界面的代码，一次只完成一个要求，防止出错，防止上下文溢出。

实现目标后，在这个文档后面简要说明你的改动，包括创建的新文档。