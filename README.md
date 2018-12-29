该项目已经废弃，这个项目是写在以前EventBus还没有注解支持的时候，那时候觉得注解还是挺有用的一个功能，就改造了这个轮子，不过现在也不需要了，所以，就
废弃了吧


# ExtendEventBus
一个扩展的Android端eventbus 控件，支持注解添加观察者


原来的使用方法：
1.onEventMainThread(EventType event){
   //do someing
}

2.onEventPostThread(EventType event){
   //do someing
}

3.onEventBackgroundThread(EventType event){
   //do someing
}

4.onEventAsync(EventType event){
   //do someing
}
现在增加注解的使用方法：EventBus默认使用方法名来注册监听，要使用这种模式要在EventBusBuilder的subscriberFindByMethodName方法设置为false

@Subscriber(ThreadMode.MainThread) 这里定义方法的执行线程
onNewMessage(EventType event){  //方法名字可以自定义
   //do someing
}
