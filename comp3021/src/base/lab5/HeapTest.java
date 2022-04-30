package base.lab5;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HeapTest {

    public static void main(String[] args) {
        testPeak();
        testSize();
        testPoll();
    }

    public static void testPeak() {
        output("Test Peak");
        Heap<Integer> integerHeap = new Heap<>();
        integerHeap.addAll(Arrays.asList(30, 10, 20));
        if(Integer.valueOf(10).equals(integerHeap.peek())) {
            output("Success");
        } else {
            output("Fail");
        }
        
    }

    public static void testPoll() {
        output("Test Poll");
        Heap<Integer> integerHeap = new Heap<>();
        List<Integer> values = Arrays.asList(2, 53, 213, 5, 1, 5, 4, 210, 14, 26, 44, 35, 31, 33, 19, 52, 27);
        integerHeap.addAll(values);

        Collections.sort(values);
        for (int x : values) {
            if(!Integer.valueOf(x).equals(integerHeap.poll())) {
                output("Fail");
                return;
            }
        }
        output("Success");
    }

    public static void testSize() {
        output("Test Size");
        Heap<Integer> integerHeap = new Heap<>();
        List<Integer> values = Arrays.asList(10, 14, 26, 44, 35, 31, 33, 19, 52, 27);
        integerHeap.addAll(values);
        if(values.size() == integerHeap.size()) {
            output("Success");
        } else {
            output("Fail");
        }
    }

    public static void output(String s) {
        System.out.println(s);
    }





}