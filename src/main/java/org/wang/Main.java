package org.wang;

public class Main {

    public static void main(String[] args) {
        BlockingQueue<Integer> blockingQueue = new BlockingQueue<>(10);
        for (int i = 0; i < 10; i++) {
            blockingQueue.add(i);
        }

        for (int i = 0; i < 10; i++) {
            System.out.println(blockingQueue.poll());
        }

        for (int i = 0; i < 10; i++) {
            blockingQueue.add(i);
        }

        for (int i = 0; i < 10; i++) {
            System.out.println(blockingQueue.poll());
        }
    }
}
