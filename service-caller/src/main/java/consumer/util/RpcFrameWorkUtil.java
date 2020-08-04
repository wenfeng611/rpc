package consumer.util;

import consumer.service.HelloService;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.Socket;

public class RpcFrameWorkUtil {


   public static <T> T getService(Class<T> interfaceClass,final String ip,final int port){
       System.out.println("getService " + interfaceClass.getName() + " from server " + ip + ":" + port);

       return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass}, new InvocationHandler() {
           public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
               Socket socket = new Socket(ip, port);
               try {
                   ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                   try {
                       output.writeUTF(method.getName());
                       output.writeObject(method.getParameterTypes());
                       output.writeObject(args);
                       ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                       try {
                           Object result = input.readObject();
                           if (result instanceof Throwable) {
                               throw (Throwable) result;
                           }
                           return result;
                       } finally {
                           input.close();
                       }
                   } finally {
                       output.close();
                   }
               } finally {
                   socket.close();
               }
           }
       });
   }


    public static void main(String[] args)  throws Exception{
        HelloService helloService = RpcFrameWorkUtil.getService(HelloService.class,"127.0.0.1",5678);
        String msg = helloService.sayHello();
        System.out.println(msg);
    }
}
