package com.hxz.example.entity;

import io.netty.channel.group.ChannelGroup;
import lombok.Data;

import java.util.List;

/**
 * @Author: hxz
 * @Description:
 * @Date: 2020/2/27 10:26
 */
@Data
public class Room {

    private String roomId;

    private ChannelGroup globalGroup;

    private List<User>userList;
}
