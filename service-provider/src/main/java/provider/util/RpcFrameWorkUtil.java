package provider.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RpcFrameWorkUtil {

    public static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 10,
            10L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<Runnable>(10));;

    public static void exportService(final Object service, int port){
        System.out.println("exportService " + service.getClass().getName() + " on port " + port);

        try {
            ServerSocket server = new ServerSocket(port);
            for(;;){
                Socket socket = server.accept();
                threadPoolExecutor.execute(() -> {
                    System.out.println("threadpool excutor...");
                    try {
                        try {
                            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                            try {
                                String methodName = input.readUTF();
                                Class<?>[] parameterTypes = (Class<?>[])input.readObject();
                                Object[] arguments = (Object[])input.readObject();
                                ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                                System.out.println("methodName: " + methodName);

                                try {
                                    Method method = service.getClass().getMethod(methodName, parameterTypes);
                                    Object result = method.invoke(service, arguments);
                                    output.writeObject(result);
                                } catch (Throwable t) {
                                    output.writeObject(t);
                                } finally {
                                    output.close();
                                }
                            } finally {
                                input.close();
                            }
                        } finally {
                            socket.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
