import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class TextAnalyzer {
    private static BlockingQueue<String> queueA = new ArrayBlockingQueue<>(100);
    private static BlockingQueue<String> queueB = new ArrayBlockingQueue<>(100);
    private static BlockingQueue<String> queueC = new ArrayBlockingQueue<>(100);

    private static volatile boolean isGeneratorFinished = false;

    public static void main(String[] args) throws InterruptedException {
        Thread generatorThread = new Thread(() -> {
            String letters = "abc";
            int length = 100000;
            Random random = new Random();
            for (int i = 0; i < 10000; i++) {
                StringBuilder text = new StringBuilder();
                for (int j = 0; j < length; j++) {
                    text.append(letters.charAt(random.nextInt(letters.length())));
                }
                try {
                    queueA.put(text.toString());
                    queueB.put(text.toString());
                    queueC.put(text.toString());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            isGeneratorFinished = true;
        });

        Thread analyzerAThread = new Thread(() -> {
            int maxCount = 0;
            while (true) {
                try {
                    String text = queueA.take();
                    int count = countMaxConsecutiveOccurrences(text, 'a');
                    if (count > maxCount) {
                        maxCount = count;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (isGeneratorFinished && queueA.isEmpty()) {
                    break;
                }
            }
            System.out.println("Max 'a' count: " + maxCount);
        });

        Thread analyzerBThread = new Thread(() -> {
            int maxCount = 0;
            while (true) {
                try {
                    String text = queueB.take();
                    int count = countMaxConsecutiveOccurrences(text, 'b');
                    if (count > maxCount) {
                        maxCount = count;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (isGeneratorFinished && queueB.isEmpty()) {
                    break;
                }
            }
            System.out.println("Max 'b' count: " + maxCount);
        });

        Thread analyzerCThread = new Thread(() -> {
            int maxCount = 0;
            while (true) {
                try {
                    String text = queueC.take();
                    int count = countMaxConsecutiveOccurrences(text, 'c');
                    if (count > maxCount) {
                        maxCount = count;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (isGeneratorFinished && queueC.isEmpty()) {
                    break;
                }
            }
            System.out.println("Max 'c' count: " + maxCount);
        });

        generatorThread.start();
        analyzerAThread.start();
        analyzerBThread.start();
        analyzerCThread.start();

        generatorThread.join();
        analyzerAThread.join();
        analyzerBThread.join();
        analyzerCThread.join();
    }

    private static int countMaxConsecutiveOccurrences(String text, char symbol) {
        int maxCount = 0;
        int currentCount = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == symbol) {
                currentCount++;
                if (currentCount > maxCount) {
                    maxCount = currentCount;
                }
            } else {
                currentCount = 1;
            }
        }
        return maxCount;
    }
}