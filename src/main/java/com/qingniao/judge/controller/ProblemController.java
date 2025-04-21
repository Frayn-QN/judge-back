package com.qingniao.judge.controller;

import com.github.pagehelper.PageInfo;
import com.qingniao.judge.entity.Problem;
import com.qingniao.judge.service.business.ProblemService;
import com.qingniao.judge.service.redis.RedisService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/problem")
@AllArgsConstructor
public class ProblemController {
    private ProblemService problemService;
    private RedisService redisService;

    @PostMapping("/add")
    void add(HttpServletRequest request, @RequestBody Problem problem) {
        String userID = redisService.queryUserID(request);
        problemService.addProblem(userID, problem);
    }

    @PostMapping("/modify")
    void modify(HttpServletRequest request, @RequestBody Problem problem) {
        String userID = redisService.queryUserID(request);
        problemService.modifyProblem(userID, problem);
    }

    @GetMapping("/delete")
    void delete(HttpServletRequest request, @RequestBody String problemID) {
        String userID = redisService.queryUserID(request);
        problemService.deleteProblem(userID, problemID);
    }

    @GetMapping("/get")
    Problem get(@RequestParam String id) {
        return problemService.getProblem(id);
    }

    @GetMapping("/detail")
    Problem getDetail(HttpServletRequest request, @RequestParam String id) {
        String userID = redisService.queryUserID(request);
        return problemService.getProblemDetail(userID, id);
    }

    @GetMapping("/load")
    PageInfo<Problem> load(@RequestParam int pageNum, @RequestParam int pageSize) {
        return problemService.loadProblem(pageNum, pageSize);
    }

    @GetMapping("/uploaded")
    PageInfo<Problem> loadUploaded(HttpServletRequest request, @RequestParam int pageNum,
                                          @RequestParam int pageSize) {
        String userID = redisService.queryUserID(request);
        return problemService.loadUploaded(userID, pageNum, pageSize);
    }

    @GetMapping("/search")
    PageInfo<Problem> search(@RequestParam String keyword, @RequestParam int pageNum,
                                    @RequestParam int pageSize) {
        return problemService.searchProblem(keyword, pageNum, pageSize);
    }
}
