package provider;

import provider.service.HelloService;
import provider.service.HelloServiceImpl;
import provider.util.RpcFrameWorkUtil;


public class ProviderApplication {

    public static void main(String[] args) {
        System.out.println("export service");
        HelloService service = new HelloServiceImpl();
        RpcFrameWorkUtil.exportService(service,5678);
    }
}
