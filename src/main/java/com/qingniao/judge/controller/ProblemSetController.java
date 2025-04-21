package com.qingniao.judge.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.pagehelper.PageInfo;
import com.qingniao.judge.entity.Problem;
import com.qingniao.judge.entity.ProblemSet;
import com.qingniao.judge.entity.User;
import com.qingniao.judge.enums.PSets_UsersAuth;
import com.qingniao.judge.service.business.ProblemSetService;
import com.qingniao.judge.service.redis.RedisService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pset")
@AllArgsConstructor
public class ProblemSetController {
    private ProblemSetService psetService;
    private RedisService redisService;

    @PostMapping("/add")
    void add(HttpServletRequest request, @RequestBody ProblemSet problemSet) {
        String userID = redisService.queryUserID(request);
        psetService.addPSet(userID, problemSet);
    }

    @PostMapping("/modify")
    void modify(HttpServletRequest request, @RequestBody ProblemSet problemSet) {
        String userID = redisService.queryUserID(request);
        psetService.modifyPSet(userID, problemSet);
    }

    @GetMapping("/delete")
    void delete(HttpServletRequest request, @RequestParam String id) {
        String userID = redisService.queryUserID(request);
        psetService.deletePSet(userID, id);
    }

    @GetMapping("/get")
    ProblemSet getInfo(HttpServletRequest request, @RequestParam String id) {
        String userID = redisService.queryUserID(request);
        return psetService.getPSet(userID, id);
    }

    @GetMapping("/problem/get")
    PageInfo<Problem> getProblem(HttpServletRequest request, @RequestParam String id,
                                        @RequestParam int pageNum, @RequestParam int pageSize) {
        String userID = redisService.queryUserID(request);
        return psetService.loadPSetProblems(userID, id, pageNum, pageSize);
    }

    @GetMapping("/auth")
    PSets_UsersAuth getAuthority(HttpServletRequest request, @RequestParam String id) {
        String userID = redisService.queryUserID(request);
        return psetService.getUserAuth(userID, id);
    }

    @GetMapping("/load")
    PageInfo<ProblemSet> load(@RequestParam int pageNum, @RequestParam int pageSize) {
        return psetService.loadPSet(pageNum, pageSize);
    }

    @GetMapping("/load/auth")
    PageInfo<ProblemSet> loadByAuth(HttpServletRequest request, @RequestParam PSets_UsersAuth authority,
                                           @RequestParam int pageNum, @RequestParam int pageSize) {
        String userID = redisService.queryUserID(request);
        return psetService.loadPsetByAuth(userID, authority, pageNum, pageSize);
    }

    @GetMapping("/search")
    PageInfo<ProblemSet> search(@RequestParam String keyword,
                                       @RequestParam int pageNum, @RequestParam int pageSize) {
        return psetService.searchPSet(keyword, pageNum, pageSize);
    }

    @PostMapping("/user/get")
    List<User> getUser(HttpServletRequest request, @RequestBody PSet_User body) {
        String userID = redisService.queryUserID(request);
        return psetService.loadPSetUsers(userID, body.psetID, body.authority);
    }

    @PostMapping("/user/add")
    void addUser(HttpServletRequest request, @RequestBody PSet_User body) {
        String userID = redisService.queryUserID(request);
        psetService.joinUser(userID, body.psetID, body.targetID, body.authority);
    }

    @PostMapping("/user/delete")
    void deleteUser(HttpServletRequest request, @RequestBody PSet_User body) {
        String userID = redisService.queryUserID(request);
        psetService.removeUser(userID, body.psetID, body.targetID);
    }

    @PostMapping("/user/auth")
    void changeUserAuth(HttpServletRequest request, @RequestBody PSet_User body) {
        String userID = redisService.queryUserID(request);
        psetService.changeUserAuth(userID, body.psetID, body.psetID, body.authority);
    }

    @PostMapping("/problem/add")
    void addProblem(HttpServletRequest request, @RequestBody PSet_Problem body) {
        String userID = redisService.queryUserID(request);
        psetService.joinProblem(userID, body.psetID, body.problemID);
    }

    @PostMapping("/problem/delete")
    void deleteProblem(HttpServletRequest request, @RequestBody PSet_Problem body) {
        String userID = redisService.queryUserID(request);
        psetService.removeProblem(userID, body.psetID, body.problemID);
    }

    @PostMapping("/favorite")
    void favorite(HttpServletRequest request, @RequestBody JsonNode body) {
        String userID = redisService.queryUserID(request);
        String psetID = body.get("psetID").asText();
        boolean flag = body.get("flag").asBoolean();

        if(flag) // 收藏
            psetService.joinUser(userID, psetID, null, PSets_UsersAuth.FAVORITE);
        else // 解除收藏
            psetService.removeUser(userID, psetID, null);
    }


    @Data
    private static class PSet_User {
        private String psetID;
        private String targetID;
        private PSets_UsersAuth authority;
    }

    @Data
    private static class PSet_Problem {
        private String psetID;
        private String problemID;
    }
}