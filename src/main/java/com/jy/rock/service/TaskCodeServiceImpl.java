package com.jy.rock.service;

import com.jy.rock.dao.TaskCodeDao;
import com.jy.rock.domain.TaskCode;
import com.jy.rock.enums.TaskType;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.weekend.Weekend;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务编号生成器
 * 1. 任务编号由 6位字符-日期(YYYYMMDD)-时间(HHMMSS)-2位校验位组成
 * 2. 6位字符的第1，2位（00~ZZ），代表任务类型，其中0不要使用
 * 3. 6位数字的第3位（固定为0和1），代表任务是否有子任务，1：代表true，0：代表false
 * 4. 剩下3位固定是任务编号，可选范围是 (000~ZZZ)，其中 000 不要使用，按照某种类别下的任务数量，顺序生成
 * 5. 2位校验位，默认都是 00
 *
 * @author hzhou
 */
@Service
@Slf4j
public class TaskCodeServiceImpl {

    /**
     * 生成的编号的长度
     */
    private static final int CODE_LENGTH = 6 + 1 + 8 + 1 + 6 + 1 + 2;

    private static final char SPLITTER = '-';

    private static final Map<TaskType, String> TASK_TYPE_MAP = new HashMap<TaskType, String>() {{
        put(TaskType.Maintenance, "MT");
        put(TaskType.Troubleshooting, "TS");
    }};

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    private TaskCodeDao taskCodeDao;

    @Autowired
    public TaskCodeServiceImpl(TaskCodeDao taskCodeDao) {
        this.taskCodeDao = taskCodeDao;
    }

    private String getNextNumber(@NotNull TaskType taskType) {
        Weekend<TaskCode> weekend = Weekend.of(TaskCode.class);
        weekend.setForUpdate(true);
        weekend.weekendCriteria().andEqualTo(TaskCode::getTaskType, taskType);
        List<TaskCode> taskCodes = this.taskCodeDao.selectByExample(weekend);
        if (taskCodes.isEmpty()) {
            // 还没有这个类型的任务的记录，直接返回1，并且往数据里插一条值为2的数据
            TaskCode taskCode = new TaskCode();
            taskCode.setTaskType(taskType);
            taskCode.setNextNumber("002");
            return "001";
        } else {
            // 返回当前这条记录的nextNumber，并且更新值
            TaskCode taskCode = taskCodes.get(0);
            String result = taskCode.getNextNumber();

            // 获取下一个编号，并更新数据表里的记录
            int decode = AlphabetEncoder.decode(taskCode.getNextNumber()) + 1;
            String encode = AlphabetEncoder.encode(decode);
            taskCode.setNextNumber(encode);
            this.taskCodeDao.updateByPrimaryKey(taskCode);

            return result;
        }
    }

    /**
     * 根据指定的任务类型，生成一个任务编号
     *
     * @param taskType    任务类型
     * @param hasChildren 是否有子任务
     * @return 任务编号
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public String generateCode(@NotNull TaskType taskType, boolean hasChildren) {
        String typeString = TASK_TYPE_MAP.get(taskType);
        if (typeString == null) {
            throw new IllegalArgumentException("不支持任务类型：" + taskType);
        }

        StringBuilder code = new StringBuilder(CODE_LENGTH);

        // 6位字符的第1，2位（00~ZZ），代表任务类型，其中0不要使用
        code.append(typeString);

        // 6位数字的第3位（固定为0和1），代表任务是否有子任务，1：代表true，0：代表false
        code.append(hasChildren ? "1" : "0");

        // 剩下3位固定是任务编号，可选范围是 (000~ZZZ)，其中 000 不要使用，按照某种类别下的任务数量，顺序生成
        String nextNumber = this.getNextNumber(taskType);
        code.append(nextNumber);

        // 前6位字符生成完成，加上分隔符
        code.append(SPLITTER);

        // 时间日期
        String dateTimeString = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        code.append(dateTimeString);
        code.append(SPLITTER);

        // 校验位
        code.append("00");

        return code.toString();
    }
}
