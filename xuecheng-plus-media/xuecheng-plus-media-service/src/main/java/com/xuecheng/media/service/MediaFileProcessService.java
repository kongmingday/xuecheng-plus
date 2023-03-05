package com.xuecheng.media.service;

import com.xuecheng.media.model.po.MediaProcess;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author eotouch
 * @version 1.0
 * @description 媒资处理业务方法
 * @date 2023/03/05 16:51
 */
public interface MediaFileProcessService {

    /**
     * @description 将url存储至数据，并更新状态为成功，并将待处理视频记录删除存入历史
     * @param status 处理结果，2:成功3失败
     * @param fileId 文件id
     * @param url 文件访问url
     * @param errorMsg 错误信息
     * @return void
     * @author eotouch
     * @date 2023-03-05 16:52
     */
    @Transactional
    void saveProcessFinishStatus(Long taskId, String status, String fileId, String url, String errorMsg);

    /**
     * @description 获取待处理任务
     * @param shardIndex 当前分片索引
     * @param shardTotal 分片总数
     * @param count 任务数量
     * @return java.util.List<com.xuecheng.media.model.po.MediaProcess>
     * @author eotouch
     * @date 2023-03-05 16:55
     */
    List<MediaProcess> getMediaProcessList(int shardIndex, int shardTotal, int count);
}
