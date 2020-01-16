# EventBus
---
* 如果你需要定义自己的事件，则需要继承org.mve.event.Event类

* 注册与管理监听器需要org.mve.event.core.SimpleEventManager
用registerEvents来注册一个类中所有的监听器
用callEvent来触发一个事件

* 监听器所在的类需要实现org.mve.event.Listener类

* 监听事件的方法需要有@EventHandler注解
