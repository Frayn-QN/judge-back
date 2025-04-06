package com.qingniao.judge.enums;

import lombok.Getter;

@Getter
public enum BanTime {
    BAN_1Day("1d"),
    BAN_7Days("7d"),
    BAN_30Days("30d"),
    BAN_Permanent("permanent"),
    BAN_Unban("unban");

    private final String description;

    BanTime(String description) {
        this.description = description;
    }

    public static BanTime fromDescription(String description) {
        for(BanTime banTime: BanTime.values()) {
            if(banTime.getDescription().equals(description))
                return banTime;
        }
        throw new IllegalArgumentException("wrong description: "+ description);
    }
}
