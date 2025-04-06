package com.qingniao.judge.service.business.impls;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qingniao.judge.entity.Problem;
import com.qingniao.judge.entity.ProblemSet;
import com.qingniao.judge.entity.User;
import com.qingniao.judge.enums.PSetStatus;
import com.qingniao.judge.enums.PSets_UsersAuth;
import com.qingniao.judge.mapper.PSets_ProblemsMapper;
import com.qingniao.judge.mapper.PSets_UsersMapper;
import com.qingniao.judge.mapper.ProblemSetMapper;
import com.qingniao.judge.service.business.ProblemSetService;
import com.qingniao.judge.config.entity.BusinessException;
import com.qingniao.judge.config.entity.ReturnCode;
import com.qingniao.judge.util.UUIDUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class ProblemSetServiceImpl  implements ProblemSetService {
    private ProblemSetMapper psetMapper;
    private PSets_UsersMapper psets_usersMapper;
    private PSets_ProblemsMapper psets_problemsMapper;

    /**
     * 验证操作者是否有题集权限
     * @param userID
     * @param psetID
     * @param userAuth 需要的题集权限
     */
    private void verify(String userID, String psetID, PSets_UsersAuth userAuth) {
        PSets_UsersAuth actualAuth = psets_usersMapper.selectByPSetAndUser(psetID, userID);
        boolean flag;
        switch (userAuth) {
            case MANAGE -> flag = actualAuth.equals(PSets_UsersAuth.MANAGE);
            case MEMBER -> flag = actualAuth.equals(PSets_UsersAuth.MANAGE)
                    || actualAuth.equals(PSets_UsersAuth.MEMBER);
            default -> throw new IllegalStateException("Unexpected value: " + userAuth);
        }
        if(!flag) throw new BusinessException("ProblemSet", ReturnCode.RC_403);
    }

    @Override
    public void addPSet(String userID, ProblemSet problemSet) {
        problemSet.setId(UUIDUtil.generateUUIDStr());
        problemSet.setCreatorID(userID);
        problemSet.setCreateTime(new Date());
        problemSet.setUpdateTime(new Date());

        psetMapper.insert(problemSet);
        psets_usersMapper.insert(problemSet.getId(), userID, PSets_UsersAuth.MANAGE);
    }

    @Override
    public void modifyPSet(String userID, ProblemSet problemSet) {
        verify(userID, problemSet.getId(), PSets_UsersAuth.MANAGE);

        problemSet.setUpdateTime(new Date());

        psetMapper.update(problemSet);
    }

    @Override
    public void deletePSet(String userID, String psetID) {
        verify(userID, psetID, PSets_UsersAuth.MANAGE);

        psetMapper.delete(psetID);
        psets_usersMapper.deleteByPSet(psetID);
        psets_problemsMapper.deleteByPSet(psetID);
    }

    @Override
    public ProblemSet getPSet(String userID, String psetID) {
        PSetStatus status = psetMapper.selectStatus(psetID);
        if(PSetStatus.PRIVATE.equals(status)) {
            // 若题集私有，则成员权限能浏览
            verify(userID, psetID, PSets_UsersAuth.MEMBER);
        }

        return psetMapper.selectOne(psetID);
    }

    @Override
    public PageInfo<ProblemSet> loadPSet(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return new PageInfo<>(psetMapper.selectList());
    }

    @Override
    public PageInfo<ProblemSet> loadPsetByAuth(String userID, PSets_UsersAuth authority, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return new PageInfo<>(psets_usersMapper.selectByUserAndAuth(userID, authority));
    }

    @Override
    public void joinProblem(String userID, String psetID, String problemID) {
        verify(userID, psetID, PSets_UsersAuth.MANAGE);

        psets_problemsMapper.insert(psetID, problemID);
    }

    @Override
    public void removeProblem(String userID, String psetID, String problemID) {
        verify(userID, psetID, PSets_UsersAuth.MANAGE);

        psets_problemsMapper.deleteByPSetAndProblem(psetID, problemID);
    }

    @Override
    public PageInfo<Problem> loadPSetProblems(String userID, String psetID, int pageNum, int pageSize) {
        PSetStatus status = psetMapper.selectStatus(psetID);
        if(PSetStatus.PRIVATE.equals(status)) {
            // 若题集私有，则成员权限能浏览
            verify(userID, psetID, PSets_UsersAuth.MEMBER);
        }

        PageHelper.startPage(pageNum, pageSize);
        return new PageInfo<>(psets_problemsMapper.selectByPSet(psetID));
    }

    @Override
    public List<User> loadPSetUsers(String userID, String psetID, PSets_UsersAuth authority) {
        verify(userID, psetID, PSets_UsersAuth.MEMBER);

        return psets_usersMapper.selectByPSetAndAuth(psetID, authority);
    }

    @Override
    public void joinUser(String userID, String psetID, String targetID, PSets_UsersAuth authority) {
        if(targetID != null)
            verify(userID, psetID, PSets_UsersAuth.MANAGE);
        else // 为空时与收藏有关
            targetID = userID;

        psets_usersMapper.insert(psetID, targetID, authority);
    }

    @Override
    public void removeUser(String userID, String psetID, String targetID) {
        if(targetID != null) {// 为空时与收藏有关
            verify(userID, psetID, PSets_UsersAuth.MANAGE);
            // 当目标为创建者时不生效
            if(psetMapper.selectOne(psetID).getCreatorID().equals(targetID))
                throw new BusinessException("ProblemSet", ReturnCode.RC_403);
        }

        psets_usersMapper.deleteByPSetAndUser(psetID, targetID);
    }

    @Override
    public void changeUserAuth(String userID, String psetID, String targetID, PSets_UsersAuth newAuth) {
        verify(userID, psetID, PSets_UsersAuth.MANAGE);
        // 当目标为创建者时不生效
        if(psetMapper.selectOne(psetID).getCreatorID().equals(targetID))
            throw new BusinessException("ProblemSet", ReturnCode.RC_403);


        psets_usersMapper.updateUserAuth(psetID, targetID, newAuth);
    }

    @Override
    public void clearByUser(String userID) {
        List<String> psetIDList = psetMapper.selectByUser(userID);
        psetIDList.forEach(id -> {// 删除用户创建的题集
            this.deletePSet(userID, id);
        });

        // 删除与用户有关的题集
        psets_usersMapper.deleteByUser(userID);
    }

    @Override
    public PageInfo<ProblemSet> searchPSet(String keyword, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return new PageInfo<>(psetMapper.selectByKeyword(keyword));
    }
}
