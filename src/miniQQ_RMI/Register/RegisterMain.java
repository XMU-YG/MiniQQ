package miniQQ_RMI.Register;

public class RegisterMain {
    public static void main(String[] args) {
        RegisterService registerService=new RegisterService(8888);
        registerService.register();
    }
}
