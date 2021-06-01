package ru.netology;

// CopyOnWriteArrayList - не подходит так как мы много записываем
// ConcurrentLinkedDeque и LinkedBlockingDeque - не подходят так как у нас нет необходимости работать в обратном порядке
// SynchronousQueue и LinkedTransferQueue - не нашёл аргуминтации в их пользу, не вижу смысла блокировки потоков в данной програме
// PriorityBlockingQueue - нет чесности
// ConcurrentLinkedQueue - чесность не надо задавать размер
// ArrayBlockingQueue - честность надо указывать размер
// DelayQueue - нет необходимости в задержке и нет чесности


import java.util.concurrent.ConcurrentLinkedQueue;

public class Main {
    public static final int CALL_DELAY = 500;
    public static final int SLEEP = 2000;

    public static void main(String[] args) throws InterruptedException {
        final int CALLS = 60;

        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();

        Thread threadATC = new Thread(сhallenges(queue, CALLS), "users");
        threadATC.start();

        Thread operator1 = new Thread(processing(queue), "Оператор 1");
        Thread operator2 = new Thread(processing(queue), "Оператор 2");
        Thread operator3 = new Thread(processing(queue), "Оператор 3");
        operator1.start();
        operator2.start();
        operator3.start();

        threadATC.join();

        operator1.join();
        operator2.join();
        operator3.join();


    }

    public static Runnable сhallenges(ConcurrentLinkedQueue<String> queue, int quantity) {
        return () -> {
            for (int i = 0; i < quantity; i++) {
                System.out.println("Поступил звонок " + (i + 1));
                queue.add("Звонок номер " + (i + 1));
                try {
                    Thread.sleep(CALL_DELAY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public static Runnable processing(ConcurrentLinkedQueue<String> queue) {
        return () -> {
            while (true) {
                if (queue.peek() != null) {
                    System.out.println(Thread.currentThread().getName() + " обработал " + queue.poll());
                    try {
                        Thread.sleep(SLEEP);
                    } catch (InterruptedException e) {
                        return;
                    }
                }

                if (queue.peek() == null) {
                    try {
                        Thread.sleep(SLEEP * 3);
                    } catch (InterruptedException e) {
                        return;
                    }
                    if (queue.peek() == null) {
                        System.out.println(Thread.currentThread().getName() + " закончил обработку звоков");
                        break;
                    }
                }
            }

        };
    }
}
