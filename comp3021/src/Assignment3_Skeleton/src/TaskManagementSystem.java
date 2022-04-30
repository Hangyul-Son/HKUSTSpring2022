import java.util.*;
import java.util.function.Predicate;
import java.util.function.Function;
import java.time.LocalDate;
import java.time.Month;
import java.util.stream.Collectors;
import org.junit.Assert;

public class TaskManagementSystem {
    public static ArrayList<Task> tasks;

    public static void init() {
        tasks = new ArrayList<>();
    }


    /* * * * * * * * * * * * * * * *
     * Operation on the task list *
     * * * * * * * * * * * * * * * */

    /**
     * TODO: add a new task object to the member variable *tasks*
     *
     * @param t: the task object to be added
     */
    public static void appendNewTask(Task t) {
        tasks.add(t);
    }

    /**
     * TODO: find all unique tags from all the tasks
     * Note that each task could have multiple tags
     * If a tag occurs multiple times in the tasks, it should only occur once in the returned list.
     * All the tags should be sorted according to the order of the string content
     *
     * @return the sorted list of the tags, in which each tag only occurs once
     */
    public static List<String> findAndSortAllUniqueTags() {
        Set<String> uniqueTags = new HashSet<>();
        for(Task task: tasks){
             for(String tag :task.getTags()){
                 uniqueTags.add(tag.toLowerCase(Locale.ROOT));
             }
        }
        List<String> uniqueTagArray = new ArrayList<>(uniqueTags);
        Collections.sort(uniqueTagArray);
        return uniqueTagArray;
    }

    /**
     * TODO: find the tasks satisfying the condition p and sort by the ascending order of the ID.
     * You have to define the methods returning various predicates over task objects
     *
     * @param p: the condition
     * @return the sorted list of the tasks. The sorting order is the ascending order of the ID.
     */
    public static List<Task> findTasks(Predicate<Task> p) {
        return tasks.stream().filter(p).sorted(Comparator.comparing(Task::getId
//                task->Integer.parseInt(task.getId().replace("ID",""))
        )).collect(Collectors.toList());
    }

    /**
     * TODO: count the number of the tasks satisfying the condition p
     * You have to define the methods returning various predicates over task objects
     *
     * @param p: the condition
     * @return the number of the tasks satisfying p
     */
    public static int countTasks(Predicate<Task> p) {
        return (int) tasks.stream().filter(p).count();
    }

    /**
     * TODO: find the top-N tasks satisfying the condition p, which are sorted according to the ascending order of the ID.
     * You have to define the methods returning various predicates over task objects
     * If N > countTasks(p).size(), just return findTasks(p) directly.
     *
     * @param p: the condition
     * @return the sorted list of the tasks.
     */
    public static List<Task> getTopNTasks(Predicate<Task> p, int N) {
        return tasks.stream().filter(p).sorted(Comparator.comparing(Task::getId
//                task->Integer.parseInt(task.getId().replace("ID",""))
        )).limit(N).collect(Collectors.toList());
    }

    /**
     * TODO: remove the tasks satisfying the condition p from *tasks*
     *
     * @param p: the condition
     * @return: true if at least one task is removed, and otherwise return false.
     */
    public static boolean removeTask(Predicate<Task> p) {
        return tasks.removeIf(p);
    }

    /* * * * * * * * * * * * * * * *
     * Predicate Definition *
     * * * * * * * * * * * * * * * */
    public static Predicate<Task> byType(TaskType type) {
        return task-> task.getType().equals(type);
    }

    public static Predicate<Task> byTag(String tag) {
        return task->task.getTags().contains(tag);

    }

    public static Predicate<Task> byTitle(String keyword) {
        return task->task.getTitle().contains(keyword);
    }

    public static Predicate<Task> byDescription(String keyword) {
        return task->task.getDescription().contains(keyword);
    }

    public static Predicate<Task> byCreationTime(LocalDate d) {
        return task->task.getCreatedOn().isBefore(d);
    }

    public static List<Predicate<Task>> genPredicates(Function<String, Predicate<Task>> f, List<String> strs) {
        List<Predicate<Task>> predicateList = new ArrayList<>();
        for(String str: strs){
            predicateList.add(f.apply(str));
        }
        return predicateList;
    }

    public static Predicate<Task> andAll(List<Predicate<Task>> ps) {
        return task-> {
            for(Predicate<Task> t: ps){
                if(!t.test(task))
                    return false;
            }
            return true;
        };
    }

    public static Predicate<Task> orAll(List<Predicate<Task>> ps) {
        return task-> {
            for(Predicate<Task> t: ps){
                if(t.test(task))
                    return true;
            }
            return false;
        };
    }

    public static Predicate<Task> not(Predicate<Task> p) {
        return task->!p.test(task);
    }

    public static void main(String[] args) {
        init();
        System.out.println("OLK");

        Task task1 = new Task("ID1", "Read Version Control with Git book", TaskType.READING, LocalDate.of(2015, Month.JULY, 1)).addTag("git").addTag("reading").addTag("books");
        Task task2 = new Task("ID2", "Read Java 8 Lambdas book", TaskType.READING, LocalDate.of(2015, Month.JULY, 2)).addTag("java8").addTag("reading").addTag("books");
        Task task3 = new Task("ID3", "Write a mobile application to store my tasks", TaskType.CODING, LocalDate.of(2015, Month.JULY, 3)).addTag("coding").addTag("mobile");
        Task task4 = new Task("ID4", "Write a blog on Java 8 Streams", TaskType.WRITING, LocalDate.of(2014, Month.JULY, 4)).addTag("blogging").addTag("writing").addTag("streams");
        Task task5 = new Task("ID5", "Write a blog on Java 8 Streams", TaskType.WRITING, LocalDate.of(2016, Month.JULY, 7)).addTag("blogging").addTag("writing").addTag("streams");
        tasks.addAll(Arrays.asList(task1, task2, task3, task4, task5));
        Assert.assertEquals(tasks.get(4).toString(), task5.toString());

        /* Invoke the methods you defined */
        Task task6 = new Task("ID6", "Read Domain Driven Design book", TaskType.READING, LocalDate.of(2013, Month.JULY, 5)).addTag("ddd").addTag("books").addTag("reading");
        appendNewTask(task6);
        Assert.assertEquals(tasks.get(5).toString(), task6.toString());

        List sortedTags = findAndSortAllUniqueTags();
        /*
         * The assertion only checks the number of the sorted result.
         * Please manually check the content of the sorted list, which should contain
         * * "git", "reading", "books", "java8", "coding", "mobile", "blogging", "writing", "streams", "ddd"
         * * All the tags should be sorted according to the order of the string content
         */
        Assert.assertEquals(sortedTags.size(), 10);

        List writingTask = findTasks(byType(TaskType.WRITING));
        Assert.assertEquals(writingTask.size(), 2);
        Assert.assertEquals(writingTask.get(0).toString(), task4.toString());
        Assert.assertEquals(writingTask.get(1).toString(), task5.toString());

        int bloggingTagTaskNum = countTasks(byTag("blogging"));
        Assert.assertEquals(bloggingTagTaskNum, 2);

        List<Task> top2ReadingTasks = getTopNTasks(byType(TaskType.READING), 2);
        Assert.assertEquals(top2ReadingTasks.size(), 2);
        Assert.assertEquals(top2ReadingTasks.get(0).toString(), task1.toString());
        Assert.assertEquals(top2ReadingTasks.get(1).toString(), task2.toString());

        int readingTaskNum = countTasks(byType(TaskType.READING));
        Assert.assertEquals(readingTaskNum, 3);

        boolean isRemoved = removeTask(byCreationTime(LocalDate.of(2014, Month.JULY, 4)));
        Assert.assertTrue(isRemoved);

        readingTaskNum = countTasks(byType(TaskType.READING));
        Assert.assertEquals(readingTaskNum, 2);

//        System.out.println("OLK");
//
//        Task task1 = new Task("ID1", "Read Version Control with Git book", TaskType.READING, LocalDate.of(2015, Month.JULY, 1)).addTag("git").addTag("reading").addTag("books");
//        Task task2 = new Task("ID2", "Read Java 8 Lambdas book", TaskType.READING, LocalDate.of(2015, Month.JULY, 2)).addTag("java8").addTag("reading").addTag("books");
//        Task task3 = new Task("ID3", "Write a mobile application to store my tasks", TaskType.CODING, LocalDate.of(2015, Month.JULY, 3)).addTag("coding").addTag("mobile");
//        Task task4 = new Task("ID4", "Write a blog on Java 8 Streams", TaskType.WRITING, LocalDate.of(2014, Month.JULY, 4)).addTag("blogging").addTag("writing").addTag("streams");
//        Task task5 = new Task("ID5", "Write a blog on Java 8 Streams", TaskType.WRITING, LocalDate.of(2016, Month.JULY, 11)).addTag("blogging").addTag("writing").addTag("streams");
//        Task task7 = new Task("ID7", "Write a blog on Java 8 Streams", TaskType.WRITING, LocalDate.of(2016, Month.JANUARY, 7)).addTag("hey").addTag("writing").addTag("drumming");
//        Task task8 = new Task("ID8", "Write a blog on Java 8 Streams", TaskType.WRITING, LocalDate.of(2015, Month.JULY, 9)).addTag("Chicken").addTag("writing").addTag("playing");
//        Task task9 = new Task("ID9", "Write a blog on Java 8 Streams", TaskType.WRITING, LocalDate.of(2011, Month.FEBRUARY, 7)).addTag("Food").addTag("writing").addTag("cooking");
//        Task task10 = new Task("ID10", "Write a blog on Java 8 Streams", TaskType.WRITING, LocalDate.of(2014, Month.JULY, 7)).addTag("blogging").addTag("writing").addTag("streaming");
//        Task task11 = new Task("ID11", "Write a blog on Java 8 Streams", TaskType.WRITING, LocalDate.of(2014, Month.APRIL, 20)).addTag("blogging").addTag("writing").addTag("streams");
//
//        tasks.addAll(Arrays.asList(task1, task2, task3, task4, task5, task7, task8, task9, task10, task11));
//        Assert.assertEquals(tasks.get(4).toString(), task5.toString());
//
//        /* Invoke the methods you defined */
//        Task task6 = new Task("ID6", "Read Domain Driven Design book", TaskType.READING, LocalDate.of(2013, Month.JULY, 5)).addTag("ddd").addTag("books").addTag("reading");
//        appendNewTask(task6);
//        Assert.assertEquals(tasks.get(10).toString(), task6.toString());
//
//        List<Predicate<Task>> pls = genPredicates(TaskManagementSystem::byDescription, Arrays.asList("Write", "Java", "Streams"));
//        pls.addAll(genPredicates(TaskManagementSystem::byTag, Arrays.asList("blogging", "writing", "streaming")));
//
//        List<Task> taskList = findTasks(not(byTitle("Version")));
//
//        System.out.println("Size: " + taskList.size() + "\n " + taskList);
//
//
//
//        /*
//         * The assertion only checks the number of the sorted result.
//         * Please manually check the content of the sorted list, which should contain
//         * * "git", "reading", "books", "java8", "coding", "mobile", "blogging", "writing", "streams", "ddd"
//         * * All the tags should be sorted according to the order of the string content
//         */
////        Assert.assertEquals(sortedTags.size(), 10);
//
////        List writingTask = findTasks(byCreationTime(LocalDate.of(2016, Month.JANUARY,  1)));
////        List writingTask = findTasks(byDescription("Java"));
//////        System.out.println("Size: " + writingTask.size() + "\n" + writingTask);
////        for(Object o : writingTask) {
////            System.out.println(((Task)o).getId());
////        }
//
////        System.out.println(findTasks(byTag("blogging")));
////        Assert.assertEquals(writingTask.size(), 7);
////        Assert.assertEquals(writingTask.get(0).toString(), task4.toString());
////        Assert.assertEquals(writingTask.get(1).toString(), task5.toString());
//
////        int bloggingTagTaskNum = countTasks(byTag("blogging"));
//
////        Assert.assertEquals(bloggingTagTaskNum, 4 );
//
//        List<Task> top2ReadingTasks = getTopNTasks(byType(TaskType.WRITING), 8);
//        for(Object o : top2ReadingTasks) {
//            System.out.println(((Task)o).getId());
//        }
////
////        Assert.assertEquals(top2ReadingTasks.size(), 2);
////        Assert.assertEquals(top2ReadingTasks.get(0).toString(), task1.toString());
////        Assert.assertEquals(top2ReadingTasks.get(1).toString(), task2.toString());
////
////        int readingTaskNum = countTasks(byType(TaskType.READING));
////        Assert.assertEquals(readingTaskNum, 3);
////
////        boolean isRemoved = removeTask(byCreationTime(LocalDate.of(2014, Month.JULY, 4)));
////        removeTask(byType(TaskType.WRITING));
////        System.out.println("Size: " + tasks.size() + "\n" + tasks);
////        for(Object o : tasks) {
////            System.out.println(((Task)o).getId());
////        }
//
////        Assert.assertTrue(isRemoved);
////
////        readingTaskNum = countTasks(byType(TaskType.READING));
////        Assert.assertEquals(readingTaskNum, 2);
    }
}
