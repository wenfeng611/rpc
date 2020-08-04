package provider.service;

public class HelloServiceImpl implements HelloService {

    @Override
    public String sayHello() {
        System.out.println("Hello World");
        return "success";
    }
}
