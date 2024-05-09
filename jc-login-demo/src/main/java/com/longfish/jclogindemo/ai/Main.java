package com.longfish.jclogindemo.ai;

import java.io.InputStream;
import java.util.Scanner;

public class Main {

    private final Session session = new Session();

    public static void main(String[] args) {
        Main main = new Main();
        main.run();
    }

    public void run() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print("You：");
            String question = sc.nextLine();
            if (question.trim().equals("")) continue;
            if (question.equals("q")) break;
            session.addMessageUser(question);
            InputStream is = Util.req(session.getUser(), session.getAssistant());
            assert is != null;
            System.out.print("大模型：");
            String answer = Util.printer(is);
            System.out.println();
            session.addMessageAssistant(answer);
        }
    }
}
