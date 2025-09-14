如果有需要补充的前端代码参考，浏览/home/admin-unix/Deedm/legacy/frontend.md，里面记录了源代码所有前端设计。

如果有需要调用的后端逻辑实现，浏览/home/admin-unix/Deedm/legacy/backend.md，里面记录了源代码所有后端设计

注意，里面列出的路径，比如src/guiManager/MainSwingFrame.java，实际对应的是/Deedm/legacy/src/guiManager/MainSwingFrame.java，legacy目录下是源代码。

先不要考虑太多ui优化，完
  成完整的构建，减少bug，注意各个文件之间的适配性
  ，严丝合缝，用到了新配置需要在配置文件里修改，
  编写向后端的请求的路由统一成'/api/...'格式我已
  经在wsl中配置好了niginx代理，会把/api代理到loca
  lhost:8080,数学公式渲染采用Katex方式。

不要开过多的端口，前端只开3000，后端只开8080，如果有测试需要可额外开端口
最重要的是，不能修改源代码。需要通过调用源代码中的逻辑来实现正确逻辑，而不是重
  新实现。