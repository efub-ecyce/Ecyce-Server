package com.ecyce.karma.domain.order.entity;

import com.ecyce.karma.domain.user.entity.User;

public record OrderStatusChangedEvent (User user, Orders orders){}
