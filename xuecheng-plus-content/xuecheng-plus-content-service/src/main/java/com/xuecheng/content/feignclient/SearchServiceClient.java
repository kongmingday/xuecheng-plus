package com.xuecheng.content.feignclient;

import com.xuecheng.content.feignclient.model.CourseIndex;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @author eotouch
 * @description 搜索服务远程调用接口
 * @date 2023-03-09 21:27
 */
@FeignClient(value = "search", fallbackFactory = SearchServiceClientFallbackFactory.class)
@RequestMapping("/search")
public interface SearchServiceClient {

    @PostMapping("/index/course")
    public Boolean add(@RequestBody CourseIndex courseIndex);

}
