package GUI;
import java.io.File;

public class App {
    public static void main(String[] args) {
        File folder = new File(".");
        File[] listOfFiles = folder.listFiles((dir, name) -> name.endsWith("_info.txt"));

        if (listOfFiles.length == 0) {
            UserAccountCreation userAccountCreation = new UserAccountCreation();
            userAccountCreation.setVisible(true);
        } else {
            UserLogin userLogin = new UserLogin();
            userLogin.setVisible(true);
        }
    }
}
