package com.jy.rock.service;

import com.jy.rock.TestClassBase;
import com.jy.rock.enums.TaskType;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author hzhou
 */
public class TaskCodeServiceImplTest extends TestClassBase {

    @Autowired
    private TaskCodeServiceImpl taskCodeService;

    @Test
    public void generateCode() throws InterruptedException {
        Set<String> codes = new HashSet<>();
        Set<String> duplicateCodes = new HashSet<>();

        List<Thread> threads = new ArrayList<>(200);

        for (int i = 0; i < 200; i++) {
            Thread t = new Thread(() -> {
                for (int j = 0; j < 5; j++) {
                    TaskType tt = j % 2 == 0 ? TaskType.Troubleshooting : TaskType.Maintenance;
                    String s = this.taskCodeService.generateCode(tt, false);
                    if (codes.contains(s)) {
                        duplicateCodes.add(s);
                    } else {
                        codes.add(s);
                    }
                }
            });

            threads.add(t);
            t.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }


        System.out.println(duplicateCodes);
        System.out.println(codes.size());
    }
}
