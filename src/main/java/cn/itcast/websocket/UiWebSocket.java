package cn.itcast.websocket;

import cn.itcast.service.GetDataService;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;
/**
 * @ServerEndpoint 注解是一个类层次的注解，它的功能主要是将目前的类定义成一个
websocket 服务器端,
 * 注解的值将被用于监听用户连接的终端访问 URL 地址,客户端可以通过这个 URL 来连接
到 WebSocket 服务器端
 */
@ServerEndpoint("/uiwebSocket")
public class UiWebSocket {
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;
    //concurrent 包的线程安全 Set，用来存放每个客户端对应的 MyWebSocket 对象。若要实现服务端与单一客户端通信的话，可以使用 Map 来存放，其中 Key 可以为用户标识
    private static CopyOnWriteArraySet<UiWebSocket> webSocketSet = new
            CopyOnWriteArraySet<UiWebSocket>();
    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;
    //建立连接成功时调用
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        webSocketSet.add(this); //加入 set 中
        addOnlineCount(); //在线数加 1
        System.out.println("有新连接加入！当前在线人数为" + getOnlineCount());
        onMessage("",session);
    }
    //连接断开时调用方法
    @OnClose
    public void onClose() {
        webSocketSet.remove(this); //从 set 中删除
        subOnlineCount(); //在线数减 1
        System.out.println("有一连接关闭！当前在线人数为" + getOnlineCount());
    }
    GetDataService getDataService = new GetDataService();
    //收到客户端消息后调用的方法
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("来自客户端的消息:" + message);
        //群发消息
        for (final UiWebSocket item : webSocketSet) {
            try {
                while (true){
                    //item.sendMessage("从 Redis 中查询数据：" + getDataService.getAll());
                    item.sendMessage(getDataService.getData());
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
    }
    //出错时调用
    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("发生错误");
        error.printStackTrace();
    }
    //根据自己需要添加的方法
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }
    public static synchronized int getOnlineCount() {
        return onlineCount;
    }
    public static synchronized void addOnlineCount() {
        UiWebSocket.onlineCount++;
    }
    public static synchronized void subOnlineCount() {
        UiWebSocket.onlineCount--;
    } }