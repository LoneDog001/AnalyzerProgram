import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    public static BlockingQueue<String> queueA = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> queueB = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> queueC = new ArrayBlockingQueue<>(100);
    public static Thread textGeneration;

    public static void main(String[] args) throws InterruptedException {
        textGeneration = new Thread(() -> {
            for (int i = 0; i < 10_000; i++) {
                String text = generateText("abc", 100_000);
                try {
                    queueA.put(text);
                    queueB.put(text);
                    queueC.put(text);
                } catch (InterruptedException e) {
                    return;
                }
            }
        });
        textGeneration.start();

        Thread a = getThread(queueA, 'a');
        Thread b = getThread(queueB, 'b');
        Thread c = getThread(queueC, 'c');

        a.start();
        b.start();
        c.start();

        a.join();
        b.join();
        c.join();
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static Thread getThread(BlockingQueue<String> blockingQueue, char s) {
        return new Thread(() -> letterCount(blockingQueue, s));
    }

    public static void letterCount(BlockingQueue<String> blockingQueue, char s) {
        int count = 0;
        int max = 0;
        for (int i = 0; i < 10000; i++) {
            try {
                String letter = blockingQueue.take();
                for (int j = 0; j < letter.length(); j++) {
                    if (letter.charAt(j) == s) {
                        count++;
                    }
                    if (count > max) {
                        max = count;
                        count = 0;
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("Количество букв " + s + " " + max + " штук");
    }
}
