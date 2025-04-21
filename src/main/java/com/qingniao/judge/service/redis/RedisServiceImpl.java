package com.qingniao.judge.service.redis;

import com.qingniao.judge.enums.BanTime;
import com.qingniao.judge.config.entity.BusinessException;
import com.qingniao.judge.config.entity.ReturnCode;
import com.qingniao.judge.util.TokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
public class RedisServiceImpl implements RedisService {
    private RedisAccess redisAccess;

    @Override
    public String queryUserID(HttpServletRequest request) {
        String token = TokenUtil.extractToken(request);
        String inputToken = "token_"+ token;
        String outputId = (String) redisAccess.get(inputToken);
        if(outputId == null)
            throw new BusinessException("Redis", ReturnCode.RC_401);
        String userID = outputId.substring(7); // userID_xxx
        if(this.getBanTime(userID) != null)
            throw new BusinessException("Redis", 401, "用户被封禁");

        return userID;
    }

    @Override
    public String setToken(String userID, boolean remember) {
        String token = TokenUtil.generateToken(userID);
        String inputToken = "token_"+ token;
        String inputId = "userID_"+ userID;

        if(remember)
            redisAccess.set(inputToken, inputId, 30, TimeUnit.DAYS);
        else
            redisAccess.set(inputToken, inputId, 1, TimeUnit.HOURS);

        return token;
    }

    @Override
    public Boolean hasToken(String token) {
        String inputToken = "token_"+ token;
        return redisAccess.hasKey(inputToken);
    }

    @Override
    public void refreshToken(String token) {
        String inputToken = "token_"+ token;

        // 以小时为单位获取token过期时间，大于1判断为长时效token
        long expireTime = redisAccess.getExpireTime(inputToken, TimeUnit.HOURS);
        if(expireTime > 1) {// 长时效
            redisAccess.setExpireTime(inputToken, 30, TimeUnit.DAYS);
        }
        else {
            redisAccess.setExpireTime(inputToken, 1, TimeUnit.HOURS);
        }
    }

    @Override
    public void removeToken(String token) {
        String inputToken = "token_"+ token;

        try {
            redisAccess.delete(inputToken);
        }
        catch (Exception e) {
            throw new BusinessException("Redis", ReturnCode.RC_400);
        }
    }

    @Override
    public void banUser(String userID, BanTime banTime) {
        String inputKey = "ban_"+ userID;
        String inputValue = "time_"+ banTime.getDescription();
        try {
            switch (banTime) {
                case BAN_1Day -> redisAccess.set(inputKey, inputValue, 1, TimeUnit.DAYS);
                case BAN_7Days -> redisAccess.set(inputKey, inputValue, 7, TimeUnit.DAYS);
                case BAN_30Days -> redisAccess.set(inputKey, inputValue, 30, TimeUnit.DAYS);
                case BAN_Permanent -> redisAccess.set(inputKey, inputValue);
                case BAN_Unban -> redisAccess.delete(inputKey);
            }
        }
        catch (Exception e) {
            throw new BusinessException("Redis", ReturnCode.RC_400);
        }
    }

    @Override
    public Date getBanTime(String userID) {
        String inputKey = "ban_"+ userID;
        if(!redisAccess.hasKey(inputKey))
            return null;

        long remainTime = redisAccess.getExpireTime(inputKey, TimeUnit.MILLISECONDS);
        return new Date(System.currentTimeMillis() + remainTime);
    }
}
