package cn.cheng.vpn;

import cn.cheng.vpn.verticle.MainVerticle;
import io.netty.handler.proxy.HttpProxyHandler;
import io.vertx.core.Launcher;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetSocket;

/**
 * @author Cheng Mingwei
 * @create 2020-08-03 10:26
 **/
public class AppLauncher extends Launcher {

    public static void main(String[] args) {
        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");
        System.setProperty("vertx.disableDnsResolver", "true");
//        new AppLauncher().dispatch(new String[]{"run", MainVerticle.class.getName()});
        NetServer netServer = Vertx.vertx().createNetServer();
        NetClient netClient = Vertx.vertx().createNetClient();
        netServer.connectHandler(clientSocket -> {
//            log.info("客户端 {}:{} 创建连接", clientSocket.remoteAddress().host(), clientSocket.remoteAddress().port());
            netClient.connect(80, "202.108.22.5", result -> {
                if (result.succeeded()) {
                    NetSocket proxySocket = result.result();
//                    log.info("代理连接成功");
                    //Pump.pump(clientSocket, proxySocket).start()
                    clientSocket.handler(buffer -> {
//                        log.debug("客户端消息: {}", buffer);
                        System.out.println("客户端消息"+buffer);
//                        proxySocket.write(buffer);
                        proxySocket.write(buffer);
                    });
                    //Pump.pump(proxySocket, clientSocket).start()
                    proxySocket.handler(buffer -> {
//                        log.debug("代理端消息: {}", buffer);
                        System.out.println("代理消息"+buffer);
                        clientSocket.write(buffer);
                    });
                    proxySocket.closeHandler(event -> System.out.println("代理连接关闭"));
                } else {
                    System.out.println("代理连接失败");
                }
                clientSocket.closeHandler(event -> System.out.println("客户端 {}:{} 断开连接"+ clientSocket.remoteAddress().host() + clientSocket.remoteAddress().port()));
            });
        });
//        log.info("开始监听 8088");
        netServer.listen(8088);
    }
}
